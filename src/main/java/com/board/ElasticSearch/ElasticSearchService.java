package com.board.ElasticSearch;

import com.board.question.Category;
import com.board.question.SearchType;
import com.board.question.dto.QuestionsListDto;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        // Elasticsearch 쿼리 구성
        SearchRequest request = new SearchRequest("questions"); // 인덱스 이름
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 간단한 예: 제목 또는 내용에서 keyword 검색
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("category", category.name()))
                .must(QueryBuilders.multiMatchQuery(keyword, "title", "content")); // 필드 맞게 수정

        sourceBuilder.query(boolQuery);
        sourceBuilder.from((int) pageable.getOffset());
        sourceBuilder.size(pageable.getPageSize());

        request.source(sourceBuilder);

        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<QuestionsListDto> results = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> source = hit.getSourceAsMap();
                QuestionsListDto dto = new QuestionsListDto();
                dto.setTitle((String) source.get("title"));
                dto.setContent((String) source.get("content"));
                // 필요한 필드 매핑

                results.add(dto);
            }
            return new PageImpl<>(results, pageable, response.getHits().getTotalHits().value);
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 실패", e);
        }
    }
}

