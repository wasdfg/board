package com.board.Question;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.board.Question.Dto.QuestionsListDtoImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionsSearchService {

    private final ElasticsearchClient elasticsearchClient;

    public void index(Questions questions) {
        QuestionsListDtoImpl dto = convertToDto(questions);
        indexDocument(dto);
    }

    public void update(Questions questions) {
        QuestionsListDtoImpl dto = convertToDto(questions);
        indexDocument(dto);
    }

    public void delete(Integer questionsUploadnumber) {
        try {
            DeleteRequest deleteRequest = DeleteRequest.of(d -> d
                    .index("questions")
                    .id(String.valueOf(questionsUploadnumber))
            );
            DeleteResponse response = elasticsearchClient.delete(deleteRequest);

        } catch (Exception e) {
            throw new RuntimeException("Elasticsearch 문서 삭제 실패", e);
        }
    }

    private QuestionsListDtoImpl convertToDto(Questions questions) {
        return new QuestionsListDtoImpl(
                questions.getUploadnumber(),
                questions.getTitle(),
                questions.getContent(),
                questions.getNowtime(),
                questions.getCategory(),
                questions.getView(),
                (long) questions.getReplysList().size(),
                questions.getUsers().getNickname()
        );
    }

    private void indexDocument(QuestionsListDtoImpl dto) {
        try {
            IndexRequest<QuestionsListDtoImpl> request = IndexRequest.of(i -> i
                    .index("questions")
                    .id(String.valueOf(dto.getUploadnumber()))
                    .document(dto)
            );
            IndexResponse response = elasticsearchClient.index(request);

        } catch (Exception e) {
            throw new RuntimeException("Elasticsearch 문서 색인 실패", e);
        }
    }
}
