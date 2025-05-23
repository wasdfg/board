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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (!category.equals(Category.ALL)) {
            boolQuery.must(QueryBuilders.matchQuery("category", category.name()));
        }

        switch (searchType) {
            case TITLE:
                boolQuery.must(QueryBuilders.matchQuery("title", keyword));
                break;
            case CONTENT:
                boolQuery.must(QueryBuilders.matchQuery("content", keyword));
                break;
            case TITLE_CONTENT:
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "title", "content"));
                break;
            case USERNAME:
                boolQuery.must(QueryBuilders.matchQuery("nickname", keyword));
                break;
            case REPLYS:
                boolQuery.must(QueryBuilders.matchQuery("replysContent", keyword));
                break;
            case ALL:
            default:
                boolQuery.must(QueryBuilders.multiMatchQuery(keyword, "title", "content", "nickname", "replysContent"));
                break;
        }

        sourceBuilder.query(boolQuery);
        sourceBuilder.from((int) pageable.getOffset());
        sourceBuilder.size(pageable.getPageSize());
        sourceBuilder.sort("uploadnumber", SortOrder.DESC); // 정렬 추가

        System.out.println("💡 ES Query:\n" + sourceBuilder.toString());

        request.source(sourceBuilder);

        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            List<QuestionsListDto> results = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            for (SearchHit hit : response.getHits().getHits()) {
                Map<String, Object> source = hit.getSourceAsMap();

                QuestionsListDto dto = new QuestionsListDtoImpl(
                        source.get("uploadnumber") != null ? ((Number) source.get("uploadnumber")).intValue() : 0,
                        (String) source.get("title"),
                        (String) source.get("content"),
                        source.get("nowtime") != null ? LocalDateTime.parse((String) source.get("nowtime"), formatter) : null,
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

