package com.board.Question;

public class QuestionsDeletedEvent {
    private final Integer questionsid;

    public QuestionsDeletedEvent(Integer questionsid) {
        this.questionsid = questionsid;
    }

    public Integer getQuestionsid() {
        return questionsid;
    }
}
