package com.board.Question;

public class QuestionsCreatedEvent {
    private final Questions questions;

    public QuestionsCreatedEvent(Questions questions) {
        this.questions = questions;
    }

    public Questions getQuestions() {
        return questions;
    }
}
