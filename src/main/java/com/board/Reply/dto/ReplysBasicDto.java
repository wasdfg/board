package com.board.Reply.dto;


import java.time.LocalDateTime;

public interface ReplysBasicDto {

    String getContent();

    LocalDateTime getNowtime();

    Integer getQuestionsId();

}
