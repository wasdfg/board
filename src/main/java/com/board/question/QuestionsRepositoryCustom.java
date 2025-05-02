package com.board.question;

import com.board.question.dto.QuestionsListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionsRepositoryCustom {

    Page<QuestionsListDto> searchPage(Category category, String keyword, SearchType searchType, Pageable pageable);
    Page<QuestionsListDto> findAllWithoutKeyword(Category category, Pageable pageable);
}
