package com.board.question;

public class QuestionsModifiedEvent {
    private final Questions questions;

    public QuestionsModifiedEvent(Questions questions) {
        this.questions = questions;
    }

    public Questions getQuestions() {
        return questions;
    }
}
