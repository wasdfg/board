package com.board.question;

public class QuestionsDeletedEvent {
    private final Integer questionsUploadnumber;

    public QuestionsDeletedEvent(Integer questionsUploadnumber) {
        this.questionsUploadnumber = questionsUploadnumber;
    }

    public Integer getQuestionsUploadnumber() {
        return questionsUploadnumber;
    }
}
