package com.board.ElasticSearch;

import com.board.question.Category;
import com.board.question.SearchType;
import com.board.question.dto.QuestionsListDto;
import com.board.question.dto.QuestionsListDtoImpl;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchService {

    private final RestHighLevelClient client;

    public ElasticSearchService(RestHighLevelClient client) {
        this.client = client;
    }

    public Page<QuestionsListDto> searchByKeyword(String keyword, Category category, SearchType searchType, Pageable pageable) {
        SearchRequest request = new SearchRequest("questions");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("category", category.name()))
                .must(QueryBuilders.multiMatchQuery(keyword, "title", "content"));

        sourceBuilder.query(boolQuery);
        sourceBuilder.from((int) pageable.getOffset());
        sourceBuilder.size(pageable.getPageSize());

        request.source(sourceBuilder);

        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<QuestionsListDto> results = new ArrayList<>();

            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> source = hit.getSourceAsMap();

                QuestionsListDto dto = new QuestionsListDtoImpl(
                        (Integer) source.get("uploadnumber"),
                        (String) source.get("title"),
                        (String) source.get("content"),
                        source.get("nowtime") != null ? LocalDateTime.parse((String) source.get("nowtime")) : null,
                        (String) source.get("category"),
                        source.get("view") != null ? ((Number) source.get("view")).intValue() : 0,
                        source.get("replysCount") != null ? ((Number) source.get("replysCount")).intValue() : 0,
                        (String) source.get("nickname")
                );

                results.add(dto);
            }

            return new PageImpl<>(results, pageable, response.getHits().getTotalHits().value);
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 실패", e);
        }
    }
}

