package com.board.Question.Dto;

import java.time.LocalDateTime;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "questions")
public class QuestionsListDtoImpl implements QuestionsListDto {
    @Id
    private final Integer id;
    private final String title;
    private final String content;
    private final LocalDateTime nowtime;
    private final String category;
    private final int view;
    private final Long replysCount;
    private final String nickname;

    public QuestionsListDtoImpl(Integer id, String title, String content,
                                LocalDateTime nowtime, String category, int view,
                                Long replysCount, String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nowtime = nowtime;
        this.category = category;
        this.view = view;
        this.replysCount = replysCount;
        this.nickname = nickname;
    }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getNowtime() { return nowtime; }
    public String getCategory() { return category; }
    public int getView() { return view; }
    public Long getReplysCount() { return replysCount; }
    public String getNickname() { return nickname; }
}
