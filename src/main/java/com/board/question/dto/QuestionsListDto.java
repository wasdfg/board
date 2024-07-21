package com.board.question.dto;

import com.board.question.Questions;
import com.board.user.Users;

import java.time.LocalDateTime;

public interface QuestionsListDto {
    String getTitle();

    Integer getUploadnumber();

    LocalDateTime getNowtime();

    Users getUsername();

    int getView();

    int getReplysSize();

}
