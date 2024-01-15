package com.board.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionsService {
    private final QuestionsRepository questionsRepository;

    public List<Questions> getList(){
        return this.questionsRepository.findAll();
    }
}
