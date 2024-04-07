package com.board.reply;


import com.board.question.Questions;
import com.board.user.SignUpUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReplysService {
    private final ReplysRepository replysRepository;

    public Replys create(Questions questions, String content, SignUpUser author){
        Replys replys = new Replys();
        replys.setContent(content);
        replys.setNowtime(LocalDateTime.now());
        replys.setQuestions(questions);
        replys.setAuthor(author);
        this.replysRepository.save(replys); //위에 있는 content nowtime questions를 replys에 저장
        return replys;
    }

    public Replys getReplys(Integer uploadnumber){
        Optional<Replys> replys = this.replysRepository.findById(uploadnumber);
        if(replys.isPresent()){
            return replys.get();
        }
        else{
            throw new RuntimeException("replys not found");
        }
    }

    public void modify(Replys replys,String content){
        replys.setContent(content);
        replys.setModifyDate(LocalDateTime.now());
        this.replysRepository.save(replys);
    }

    public void delete(Replys replys) {
        this.replysRepository.delete(replys);
    }

    public void vote(Replys replys,SignUpUser signUpUser){
        replys.getVoter().add(signUpUser);
        this.replysRepository.save(replys);
    }
}
