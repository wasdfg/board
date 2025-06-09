package com.board.Question;

import com.board.Question.Dto.QuestionsListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionsRepositoryCustom {

    Page<QuestionsListDto> searchPage(Category category, String keyword, SearchType searchType, Pageable pageable);
    Page<QuestionsListDto> findAllWithoutKeyword(Category category, Pageable pageable);
}
