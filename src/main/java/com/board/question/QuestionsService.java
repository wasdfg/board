package com.board.question;

import com.board.DataNotFoundException;

import com.board.user.SignUpUser;
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
public class QuestionsService { //service에서 처리
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

    public void create(String title, String content, SignUpUser user){
        Questions q = new Questions();
        q.setTitle(title);
        q.setContent(content);
        q.setNowtime(LocalDateTime.now());
        q.setAuthor(user);
        this.questionsRepository.save(q);
    }

    public void modify(Questions questions,String title,String content){ //수정할 내용 저장
        questions.setTitle(title);
        questions.setContent(content);
        questions.setModifyDate(LocalDateTime.now());
        this.questionsRepository.save(questions);
    }

    public void delete(Questions questions){
        this.questionsRepository.delete(questions);
    }

    public Page<Questions> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("nowtime")); //날짜 기준으로 오름차순으로 정렬
        Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
        return this.questionsRepository.findAll(pageable);
    }

}
