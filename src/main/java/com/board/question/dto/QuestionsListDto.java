package com.board.question.dto;

import java.time.LocalDateTime;

public interface QuestionsListDto {
    String getTitle();

    Integer getUploadnumber();

    LocalDateTime getNowtime();

    Long getUserId();

    String getNickname();

    int getView();

    int getReplysSize();

    String getCategory();
}
