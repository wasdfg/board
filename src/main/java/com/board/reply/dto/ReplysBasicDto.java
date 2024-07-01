package com.board.reply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReplysBasicDto {

    private String content;

    private LocalDateTime nowtime;

    private Integer uploadnumber;

    public ReplysBasicDto(String content, Integer uploadnumber, LocalDateTime nowtime){
        this.content = content;
        this.uploadnumber = uploadnumber;
        this.nowtime = nowtime;

    }
}
