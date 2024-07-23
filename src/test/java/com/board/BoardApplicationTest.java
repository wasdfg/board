package com.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.board.question.Questions;
import com.board.question.QuestionsForm;
import com.board.question.QuestionsRepository;
import com.board.question.QuestionsService;
import com.board.reply.Replys;
import com.board.reply.ReplysRepository;
import com.board.reply.ReplysService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class BoardApplicationTest {
    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private ReplysRepository replysRepository;

    @Autowired
    private QuestionsService questionsService;

    @Autowired
    private ReplysService replysService;

    @Test
    //@Rollback(value = false)
    //@Transactional //테스트 데이터를 인메모리db에 저장되는지 확인
    void testJpa() {
        for (int i = 1; i <= 1000000; i++) {
            String title = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            String[] arr = {"free","ask"};
            this.questionsService.create(title, content,null,arr[(int)((double)arr.length*Math.random())]);
        }
    }

    @Test
    void testReplys(){
        for(int i = 4;i <= 1000004;i++){
            String content = String.format("양산형 댓글입니다 [%03d]",(int)((double)100*Math.random()));
            Optional<Questions> questions = this.questionsRepository.findById(i);
            Questions questions1 = questions.get();
            this.replysService.create(questions1,content,null,null);
        }
    }

    @Test
    public void testQuestionsForm() {
        QuestionsForm form = new QuestionsForm();
        form.setTitle("Test Title");
        form.setContent("Test Content");
        form.setCategory("Test Category");

        Assertions.assertNotNull(form.getTitle());
        Assertions.assertNotNull(form.getContent());
        Assertions.assertNotNull(form.getCategory());
    }

    @Test
    void contextLoads() {
        // 테스트 코드가 없지만, 컨텍스트가 로드되는지 확인하는 기본 테스트
    }
}
