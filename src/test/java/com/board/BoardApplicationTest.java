package com.board;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
public class BoardApplicationTest {
    @Autowired
    private QuestionRepository questionRepository;

    /*@AfterEach
    public void cleanup(){
        questionRepository.deleteAll(); //test 실행 후 db에 저장된 내용 삭제
    }*/
    @Test
    @Transactional
    void test(){
        Question q1 = new Question();
        //q1.setTitle("질문 있습니다.");//질문 제목
        //q1.setContent("이거 뭐 만드는건가요?");//질문 내용
        //q1.setNowTime(LocalDateTime.now());//질문을 올린 시간
        questionRepository.save(Question.builder().Title("질문 있습니다.").Content("이거 뭐 만드는건가요?").NowTime(LocalDateTime.now()).build()); //질문을 builder를 사용하여 저장

        Question q2 = new Question();
        q2.setTitle("질문이요");
        q2.setContent("이거 만들때 id는 자동으로 생성되나요?");
        q2.setNowTime(LocalDateTime.now());
        this.questionRepository.save(q2);

        List<Question> questionList = questionRepository.findAll(); //Question 테이블의 모든 자료를 가져온다

        Question question0 = questionList.get(0);
        Question question1 = questionList.get(1);
        assertThat(question0.getContent()).isEqualTo("이거 뭐 만드는건가요?");
        assertThat(question1.getContent()).isEqualTo("이거 만들때 id는 자동으로 생성되나요?");
    }
    @GetMapping("/testing")
    public void testing(){
        questionRepository.findAll();
    }
}
