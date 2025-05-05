package com.board.reply.dto;


import java.time.LocalDateTime;

public interface ReplysBasicDto {

    String getContent();

    LocalDateTime getNowtime();

    Integer getQuestionsUploadnumber();

}
