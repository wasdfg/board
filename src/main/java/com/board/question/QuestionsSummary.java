package com.board.question;

import com.board.user.SignUpUser;

import java.time.LocalDateTime;

public interface QuestionsSummary {
    String getTitle();
    String getContent();
    LocalDateTime getNowtime();
    LocalDateTime getModifyDate();
    SignUpUser getAuthor();
}