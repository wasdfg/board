package com.board.question.dto;

import com.board.user.Users;

import java.time.LocalDateTime;

public interface QuestionsListDto {
    String getTitle();

    Integer getUploadnumber();

    LocalDateTime getNowtime();

    Long getUser_id();

    String getNickname();

    int getView();

    int getReplysSize();
}
