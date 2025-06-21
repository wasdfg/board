package com.board.Question.Dto;

import java.time.LocalDateTime;

public interface QuestionsListDto {

    Integer getId();
    String getTitle();
    String getContent();
    LocalDateTime getNowtime();
    String getCategory();
    int getView();
    Long getReplysCount();
    String getNickname();

}
