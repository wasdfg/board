package com.board.Question.Repository;

import com.board.Admin.PostListDto;
import com.board.Question.Category;
import com.board.Question.Dto.QuestionsListDto;
import com.board.Question.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionsRepositoryCustom {

    Page<QuestionsListDto> searchPage(Category category, String keyword, SearchType searchType, Pageable pageable);
    Page<QuestionsListDto> findAllWithoutKeyword(Category category, Pageable pageable);

    Page<PostListDto> searchAdminPosts(String keyword, Category category, Boolean reported, Boolean deleted, Pageable pageable);
}
