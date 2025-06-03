package com.board.question;

public class QuestionsCreatedEvent {
    private final Questions questions;

    public QuestionsCreatedEvent(Questions questions) {
        this.questions = questions;
    }

    public Questions getQuestions() {
        return questions;
    }
}
