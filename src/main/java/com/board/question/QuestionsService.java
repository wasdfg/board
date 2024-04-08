package com.board.question;

import com.board.DataNotFoundException;

import com.board.reply.Replys;
import com.board.user.SignUpUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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

    public void vote(Questions questions,SignUpUser signUpUser){
        questions.getVoter().add(signUpUser); //현재 로그인한 아이디를 가져옴
        this.questionsRepository.save(questions);
    }

    public Page<Questions> getList(int page,String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("nowtime")); //날짜 기준으로 오름차순으로 정렬
        Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
        Specification<Questions> spec = search(kw); //조회한 내용을 저장
        return this.questionsRepository.findAll(spec,pageable);
        //return this.questionsRepository.findAllByKeyword(kw, pageable); //쿼리로 사용했을 시 리턴문
    }

    private Specification<Questions> search(String kw){ //kw로 검색할 문자열 받아온다
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Questions> q, CriteriaQuery<?> query, CriteriaBuilder cb){
                query.distinct(true); //중복 없이
                Join<Questions,SignUpUser> u1 = q.join("author",JoinType.LEFT); //left outer join을 사용
                Join<Questions,Replys> a = q.join("replysList",JoinType.LEFT);
                Join<Questions,SignUpUser> u2 = q.join("author",JoinType.LEFT);
                return cb.or(cb.like(q.get("title"),"%"+kw+"%"),//제목  or로 검색,sql문의 like %문자%와 같은 역할
                        cb.like(q.get("content"),"%"+kw+"%"), //내용
                        cb.like(u1.get("username"),"%"+kw+"%"), //글 작성자
                        cb.like(a.get("content"),"%"+kw+"%"), //답변 내용
                        cb.like(u2.get("username"),"%"+kw+"%") //답변 작성자
                        );
            }
        };
    }
}
