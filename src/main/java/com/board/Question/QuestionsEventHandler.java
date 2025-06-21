package com.board.Question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class QuestionsEventHandler {
    private final QuestionsSearchService questionsSearchService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQuestionsCreated(QuestionsCreatedEvent event) {
        questionsSearchService.index(event.getQuestions());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQuestionsModified(QuestionsModifiedEvent event) {
        questionsSearchService.update(event.getQuestions());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQuestionsDeleted(QuestionsDeletedEvent event) {
        questionsSearchService.delete(event.getQuestionsid());
    }
}
