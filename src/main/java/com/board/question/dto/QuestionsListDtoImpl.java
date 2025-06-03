package com.board.question.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "questions")
public class QuestionsListDtoImpl implements QuestionsListDto {
    @Id
    private final Integer uploadnumber;
    private final String title;
    private final String content;
    private final LocalDateTime nowtime;
    private final String category;
    private final int view;
    private final int replysCount;
    private final String nickname;

    public QuestionsListDtoImpl(Integer uploadnumber, String title, String content,
                                LocalDateTime nowtime, String category, int view,
                                long replysCount, String nickname) {
        this.uploadnumber = uploadnumber;
        this.title = title;
        this.content = content;
        this.nowtime = nowtime;
        this.category = category;
        this.view = view;
        this.replysCount = (int) replysCount;
        this.nickname = nickname;
    }

    public Integer getUploadnumber() { return uploadnumber; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getNowtime() { return nowtime; }
    public String getCategory() { return category; }
    public int getView() { return view; }
    public int getReplysCount() { return replysCount; }
    public String getNickname() { return nickname; }
}
