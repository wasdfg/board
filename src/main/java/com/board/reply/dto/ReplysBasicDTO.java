package com.board.reply.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReplysBasicDTO {

    private String content;

    private LocalDateTime nowtime;

    private Integer uploadnumber;

    public ReplysBasicDTO(String content,LocalDateTime nowtime,Integer uploadnumber){
        this.content = content;
        this.nowtime = nowtime;
        this.uploadnumber = uploadnumber;
    }
}
