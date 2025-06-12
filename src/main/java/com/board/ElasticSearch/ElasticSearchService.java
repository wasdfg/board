package com.board.ElasticSearch;

import com.board.Question.Category;
import com.board.Question.SearchType;
import com.board.Question.Dto.QuestionsListDto;
import com.board.Question.Dto.QuestionsListDtoImpl;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ElasticSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    public ElasticSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public Page<QuestionsListDto> searchByKeyword(String keyword, Category category, SearchType searchType, Pageable pageable) {

        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        if (!category.equals(Category.ALL)) {
            boolQueryBuilder.must(m -> m.match(mq -> mq.field("category").query(category.name())));
        }

        switch (searchType) {
            case TITLE:
                boolQueryBuilder.must(m -> m.match(mq -> mq.field("title").query(keyword)));
                break;
            case CONTENT:
                boolQueryBuilder.must(m -> m.match(mq -> mq.field("content").query(keyword)));
                break;
            case TITLE_CONTENT:
                boolQueryBuilder.must(m -> m.multiMatch(mq -> mq.fields("title", "content").query(keyword)));
                break;
            case USERNAME:
                boolQueryBuilder.must(m -> m.match(mq -> mq.field("nickname").query(keyword)));
                break;
            case REPLYS:
                boolQueryBuilder.must(m -> m.match(mq -> mq.field("replysContent").query(keyword)));
                break;
            case ALL:
            default:
                boolQueryBuilder.must(m -> m.multiMatch(mq -> mq.fields("title", "content", "nickname", "replysContent").query(keyword)));
                break;
        }

        Query query = Query.of(q -> q.bool(boolQueryBuilder.build()));

        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        var searchHits = elasticsearchOperations.search(nativeQuery, QuestionsListDtoImpl.class);

        List<QuestionsListDto> results = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        searchHits.forEach(hit -> {
            Map<String, Object> source = hit.getContent() != null ? (Map<String, Object>) hit.getContent() : Map.of();

            QuestionsListDto dto = new QuestionsListDtoImpl(
                    source.get("uploadnumber") != null ? ((Number) source.get("uploadnumber")).intValue() : 0,
                    (String) source.get("title"),
                    (String) source.get("content"),
                    source.get("nowtime") != null ? LocalDateTime.parse((String) source.get("nowtime"), formatter) : null,
                    (String) source.get("category"),
                    source.get("view") != null ? ((Number) source.get("view")).intValue() : 0,
                    (source.get("replysCount") != null ? ((Number) source.get("replysCount")).longValue() : 0L),
                    (String) source.get("nickname")
            );
            results.add(dto);
        });

        return new PageImpl<>(results, pageable, searchHits.getTotalHits());
    }

}

