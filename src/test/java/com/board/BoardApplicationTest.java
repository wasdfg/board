package com.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.board.question.Questions;
import com.board.question.QuestionsRepository;
import com.board.question.QuestionsService;
import com.board.reply.Replys;
import com.board.reply.ReplysRepository;
import com.board.user.SignUpUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootTest
public class BoardApplicationTest {
    @Autowired
    private QuestionsRepository questionsRepository;

    @Autowired
    private ReplysRepository replysRepository;

    @Autowired
    private QuestionsService questionsService;

    @Test
    void test(){ //테스트 데이터는 h2 인메모리에 저장되지 않는다. test시 test전용 db가 생기기 떄문이다.
        //Questions q1 = new Questions();
        //q1.setTitle("질문 있습니다.");//질문 제목
        //q1.setContent("이거 뭐 만드는건가요?");//질문 내용
        //q1.setNowtime(LocalDateTime.now());//질문을 올린 시간
        //this.questionsRepository.save(q1);

        questionsRepository.save(Questions.builder().title("질문 있습니다.").content("이거 뭐 만드는건가요?").nowtime(LocalDateTime.now()).build()); //질문을 builder를 사용하여 저장

        Questions q2 = new Questions();
        q2.setTitle("질문이요");
        q2.setContent("이거 만들때 id는 자동으로 생성되나요?");
        q2.setNowtime(LocalDateTime.now());
        this.questionsRepository.save(q2);

        List<Questions> questionsList = questionsRepository.findAll(); //Question 테이블의 모든 자료를 가져온다
        assertEquals(2,questionsList.size()); //현재 questionsList의 크기가 2가 맞는지
        Questions q = questionsList.get(0); //Uploadnumber가 1인 것을 가져온다
        assertEquals("질문 있습니다.",q.getTitle());  //uploadnumber가 1인 엔티티의 제목이 같은지 비교

        Questions questions0 = questionsList.get(0);
        Questions questions1 = questionsList.get(1);
        assertThat(questions0.getContent()).isEqualTo("이거 뭐 만드는건가요?");
        assertThat(questions1.getContent()).isEqualTo("이거 만들때 id는 자동으로 생성되나요?");
    }

    @Test
    void test1(){
        Optional<Questions> oq = this.questionsRepository.findById(1); //findbyid의 리턴 타입은 optional이다.
        if(oq.isPresent()){ //값이 존재하면 true
            Questions q = oq.get();
            assertEquals("질문 있습니다.",q.getTitle());
        }
    }

    @Test
    void testTitle(){
        Questions q = this.questionsRepository.findByTitle("질문 있습니다.");
        //findby 사용자 정의 함수는 JPA에서 메서드 명을 분석하고 쿼리를 만들어 실행하기 때문에 직접 구현하지 않아도 된다.
        assertEquals(1,q.getUploadnumber());
    }

    @Test
    void testTitleandContent(){
       Questions q = this.questionsRepository.findByTitleAndContent("질문이요","이거 만들때 id는 자동으로 생성되나요?");
       assertEquals(2,q.getUploadnumber()); //이번엔 두번째 쿼리를 가져온다
    }

    @Test
    void testFindTitle(){
        List<Questions> qlist = this.questionsRepository.findByTitleLike("질문%"); //sql의 like '질문%' 구문과 같은 역할을 한다.
        Questions q = qlist.get(0); //list중 첫번째 자료를 가져온다.
        assertEquals(q.getTitle(),"질문 있습니다.");
    }

    @Test
    void testEdit(){
       Optional<Questions> oq = this.questionsRepository.findById(1);
       assertTrue(oq.isPresent()); //값이 존재하는지 확인
       Questions q = oq.get(); //첫번째 데이터를 가져온다
       q.setTitle("수정된 제목");  //첫번째 title을 새로 바꾼다 update문
       this.questionsRepository.save(q); //다시 저장해준다.
    }

    @Test
    void testDelete(){
       Optional<Questions> oq = this.questionsRepository.findById(2); //두번째 튜플을 가져온다
       assertTrue(oq.isPresent());
       Questions q = oq.get();
       this.questionsRepository.delete(q); //그 튜플을 삭제
       assertEquals(1,this.questionsRepository.count()); //튜플의 개수가 1개인지 확인
    }

    @Test
    void testReply(){
        Optional<Questions> oq = this.questionsRepository.findById(1);
        assertTrue(oq.isPresent());
        Questions q = oq.get(); //Question 객체 하나를 가져온다

        Replys replys = new Replys();
        replys.setContent("이거 질답게시판 만드는겁니다.");
        replys.setQuestions(q); //어떤 질문인지 알기 위해 위에서 맞는 객체를 가져온다.
        replys.setNowtime(LocalDateTime.now());
        this.replysRepository.save(replys);
    }

    @Test
    void testCheckReply(){
        Optional<Replys> oq = this.replysRepository.findById(1);
        assertThat(oq.isPresent());
        Replys r = oq.get();
        assertEquals(1,r.getQuestions().getUploadnumber()); //answer의 question uploadnumber를 확인
    }

    @Transactional
    @Test
    void testcheck(){
        Optional<Questions> oq = this.questionsRepository.findById(1);
        assertTrue(oq.isPresent());
        Questions q = oq.get();

        List<Replys> replysList = q.getReplysList(); //질문에 대한 답변은 여러개일수도 있으므로 list사용

        assertEquals(1,replysList.size());
        assertEquals("이거 질답게시판 만드는겁니다.",replysList.get(0).getContent());
    }

    @Test
    @Rollback(value = false)
    @Transactional //테스트 데이터를 인메모리db에 저장되는지 확인
    void testJpa() {
        for (int i = 1; i <= 1000000; i++) {
            String title = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
            String[] arr = {"common","ask"};
            this.questionsService.create(title, content,null,arr[(int)((double)arr.length*Math.random())]);
        }
    }
}
