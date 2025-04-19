package com.board.question.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class QuestionsListDto {

    private Integer uploadnumber;
    private String title;
    private String content;
    private LocalDateTime nowtime;
    private String category;
    private int view;
    private int replysSize;
    private String nickname;
    private List<String> replyContents;  // 추가된 부분

    public QuestionsListDto(Integer uploadnumber, String title, String content,
                            LocalDateTime nowtime, String category, int view,
                            int replysSize, String nickname,
                            List<String> replyContents) {
        this.uploadnumber = uploadnumber;
        this.title = title;
        this.content = content;
        this.nowtime = nowtime;
        this.category = category;
        this.view = view;
        this.replysSize = replysSize;
        this.nickname = nickname;
        this.replyContents = replyContents;
    }
}
