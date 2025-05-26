package com.board.ElasticSearch;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import com.board.question.dto.QuestionsListDtoImpl;


import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class ElasticSyncJobConfig{

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Bean
    public Job elasticSyncJob() {
        return jobBuilderFactory.get("elasticSyncJob")
                .start(elasticSyncStep())
                .build();
    }

    @Bean
    public Step elasticSyncStep() {
        return stepBuilderFactory.get("elasticSyncStep")
                .<QuestionsListDtoImpl, QuestionsListDtoImpl>chunk(100)
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

                IndexQuery indexQuery = new IndexQueryBuilder()
                        .withId(String.valueOf(item.getUploadnumber()))
                        .withObject(jsonMap)
                        .build();

                elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of("questions"));
            }
        };
    }
}
