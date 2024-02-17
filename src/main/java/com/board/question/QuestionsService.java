package com.board.question;

import com.board.DataNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionsService {
    private final QuestionsRepository questionsRepository;

    public List<Questions> getList(){
        return this.questionsRepository.findAll();
    }

    public Questions getQuestions(Integer uploadnumber){
        Optional<Questions> questions = this.questionsRepository.findById(uploadnumber); //uploadnumber로 찾는다.
        if(questions.isPresent()){ //있으면
            return questions.get(); //자료를 가져온다
        }
        else{
            throw new DataNotFoundException("questions not found"); //없으면 DataNotFoundException클래스를 동작시킨다.
        }
    }

    public void create(String title,String content){
        Questions q = new Questions();
        q.setTitle(title);
        q.setContent(content);
        q.setNowtime(LocalDateTime.now());
        this.questionsRepository.save(q);
    }

    public Page<Questions> getList(Pageable pageable) {
        return this.questionsRepository.findAll(pageable);
    }
}
