package com.board.ElasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.board.question.dto.QuestionsListDtoImpl;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ElasticSyncJobConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final ElasticsearchClient elasticsearchClient;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job elasticSyncJob() {
        return new JobBuilder("elasticSyncJob", jobRepository)
                .start(elasticSyncStep())
                .build();
    }

    @Bean
    public Step elasticSyncStep() {
        return new StepBuilder("elasticSyncStep", jobRepository)
                .<QuestionsListDtoImpl, QuestionsListDtoImpl>chunk(100, transactionManager)
                .reader(jpaReader())
                .writer(elasticWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<QuestionsListDtoImpl> jpaReader() {
        return new JpaPagingItemReaderBuilder<QuestionsListDtoImpl>()
                .name("questionsReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("""
                    SELECT new com.board.question.dto.QuestionsListDtoImpl(
                        q.uploadnumber, q.title, q.content, q.nowtime,
                        q.category, q.view, COUNT(r), u.nickname
                    )
                    FROM Questions q
                    JOIN q.users u
                    LEFT JOIN q.replysList r
                    GROUP BY q.uploadnumber, q.title, q.content, q.nowtime,
                             q.category, q.view, u.nickname
                    """)
                .pageSize(100)
                .build();
    }

    @Bean
    public ItemWriter<QuestionsListDtoImpl> elasticWriter() {
        return items -> {
            if (items.isEmpty()) return;

            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();

            for (QuestionsListDtoImpl item : items) {
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("uploadnumber", item.getUploadnumber());
                jsonMap.put("title", item.getTitle());
                jsonMap.put("content", item.getContent());
                jsonMap.put("nowtime", item.getNowtime().toString()); // 문자열로 변환 필요
                jsonMap.put("category", item.getCategory());
                jsonMap.put("view", item.getView());
                jsonMap.put("replysCount", item.getReplysCount());
                jsonMap.put("nickname", item.getNickname());

                bulkBuilder.operations(op -> op
                        .index(idx -> idx
                                .index("questions")
                                .id(String.valueOf(item.getUploadnumber()))
                                .document(jsonMap)
                        )
                );
            }

            BulkResponse bulkResponse = elasticsearchClient.bulk(bulkBuilder.build());

            if (bulkResponse.errors()) {
                System.err.println("Bulk indexing had failures");
                // 실패 처리 로직 추가 가능
            } else {
                System.out.println("Successfully indexed batch of size: " + items.size());
            }
        };
    }
}
