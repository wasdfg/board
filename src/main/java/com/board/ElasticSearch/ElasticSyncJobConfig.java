package com.board.ElasticSearch;

import com.board.question.dto.QuestionsListDtoImpl;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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
    private final RestHighLevelClient restHighLevelClient;
    private final JobRepository jobRepository;
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
            BulkRequest bulkRequest = new BulkRequest();

            for (QuestionsListDtoImpl item : items) {
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("uploadnumber", item.getUploadnumber());
                jsonMap.put("title", item.getTitle());
                jsonMap.put("content", item.getContent());
                jsonMap.put("nowtime", item.getNowtime());
                jsonMap.put("category", item.getCategory());
                jsonMap.put("view", item.getView());
                jsonMap.put("replysCount", item.getReplysCount());
                jsonMap.put("nickname", item.getNickname());

                IndexRequest indexRequest = new IndexRequest("questions")
                        .id(String.valueOf(item.getUploadnumber()))
                        .source(jsonMap);

                bulkRequest.add(indexRequest);
            }

            if (bulkRequest.numberOfActions() > 0) {
                BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

                if (bulkResponse.hasFailures()) {
                    System.err.println("Bulk indexing had failures: " + bulkResponse.buildFailureMessage());
                    // 필요 시 실패 아이템 재처리 로직 추가 가능
                } else {
                    System.out.println("Successfully indexed batch of size: " + items.size());
                }
            }
        };
    }
}
