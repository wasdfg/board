package com.board.reply;


import com.board.question.Questions;
import com.board.user.SignUpUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ReplysService {
    private final ReplysRepository replysRepository;

    public void create(Questions questions, String content, SignUpUser author){
        Replys replys = new Replys();
        replys.setContent(content);
        replys.setNowtime(LocalDateTime.now());
        replys.setQuestions(questions);
        replys.setAuthor(author);
        this.replysRepository.save(replys); //위에 있는 content nowtime questions를 replys에 저장 
    }
}
