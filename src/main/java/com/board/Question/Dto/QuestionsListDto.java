package com.board.Question.Dto;

import java.time.LocalDateTime;

public interface QuestionsListDto {

    Integer getUploadnumber();
    String getTitle();
    String getContent();
    LocalDateTime getNowtime();
    String getCategory();
    int getView();
    Long getReplysCount();
    String getNickname();

}
