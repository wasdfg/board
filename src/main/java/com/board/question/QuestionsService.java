package com.board.question;

import com.board.DataNotFoundException;

import com.board.question.dto.QuestionsBasicDto;
import com.board.reply.Replys;
import com.board.reply.ReplysRepository;
import com.board.user.Users;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class QuestionsService { //service에서 처리

    @Autowired
    private final QuestionsRepository questionsRepository;

    @Autowired
    private final ReplysRepository replysRepository;
    public Questions getQuestions(Integer uploadnumber){
        Optional<Questions> questions = this.questionsRepository.findById(uploadnumber); //uploadnumber로 찾는다.
        if(questions.isPresent()){ //있으면
            return questions.get(); //자료를 가져온다
        }
        else{
            throw new DataNotFoundException("questions not found"); //없으면 DataNotFoundException클래스를 동작시킨다.
        }
    }

    public List<Replys> getSortByDate(Integer uploadnumber) { //투표수 기준으로 정렬
        Optional<Questions> questions = this.questionsRepository.findById(uploadnumber);
        if(questions.isPresent()){
            Questions questions1 = questions.get();
            questions1.setView(questions1.getView()+1); //조회수를 1 증가시켜준다 여기서 증가시키는 이유는 질문이나 답변을 수정하는 서비스 코드에서 getQuestions가 조회되기 떄문
            this.questionsRepository.save(questions1);
            if(!questions.get().getReplysList().isEmpty()){ //답변이 있으면
                List<Replys> sortedReplys = this.replysRepository.findReplysByQuestionsUploadnumber(uploadnumber);
                return sortedReplys;
            }
            else{
                return new ArrayList<>(); //답변이 없으면 빈 배열을 전달
            }
        }
        else{
            throw new DataNotFoundException("questions not found"); //없으면 DataNotFoundException클래스를 동작시킨다.
        }
    }

    public void create(String title, String content, Users users,String category){
        Questions q = new Questions();
        q.setTitle(title);
        q.setContent(content);
        q.setNowtime(LocalDateTime.now());
        q.setAuthor(users);
        q.setCategory(category);
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

    public void vote(Questions questions,Users users){
        questions.getVoter().add(users); //현재 로그인한 아이디를 가져옴
        this.questionsRepository.save(questions);
    }

    public Page<Questions> getList(int page, String kw,String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort multiSort = Sort.by(
                Sort.Order.desc("nowtime"), //날짜 기준으로 내림차순으로 정렬
                Sort.Order.desc("uploadnumber") //날짜가 같다면 번호내림차순으로 정렬
        );
        Pageable pageable = PageRequest.of(page, 10,multiSort);
        Specification<Questions> spec = Specification.where(null); //조회한 내용을 저장 // 검색 조건이 있는 경우에는 search 메서드를 통해 검색 조건이 추가된 Specification 객체 생성
        if (!StringUtils.isEmpty(kw)) { // 검색으로 찾는 경우가 아닐 때 불필요한 쿼리 조회를 없애기 위해 사용
            // 검색 조건이 있는 경우에는 search 메서드를 통해 검색 조건이 추가된 Specification 객체 생성
            spec = search(kw);
        }

        if(StringUtils.isEmpty(category)) { //가테고리 분류시
            return this.questionsRepository.findAll(spec, pageable);
        }
        else{
            return this.questionsRepository.findByCategory(pageable,category);
        }
        //return this.questionsRepository.findAllByKeyword(kw, pageable); //쿼리로 사용했을 시 리턴문
    }
    public Page<QuestionsBasicDto> getList(int page, Users users) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort multiSort = Sort.by(
                Sort.Order.desc("nowtime"), //날짜 기준으로 내림차순으로 정렬
                Sort.Order.desc("uploadnumber") //날짜가 같다면 번호내림차순으로 정렬
        );
        Pageable pageable = PageRequest.of(page, 10,multiSort);
        return this.questionsRepository.findByUser(users.getUsername(),pageable);
    }

    public Page<Questions> getList(int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort multiSort = Sort.by(
                Sort.Order.desc("nowtime"), //날짜 기준으로 내림차순으로 정렬
                Sort.Order.desc("uploadnumber") //날짜가 같다면 번호내림차순으로 정렬
        );
        Pageable pageable = PageRequest.of(page, 10,multiSort);
        return this.questionsRepository.findAll(pageable);
    }

    private Specification<Questions> search(String kw){ //kw로 검색할 문자열 받아온다
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Questions> q, CriteriaQuery<?> query, CriteriaBuilder cb){
                query.distinct(true); //중복 없이
                Join<Questions,Users> u1 = q.join("author",JoinType.LEFT); //left outer join을 사용
                Join<Questions,Replys> a = q.join("replysList",JoinType.LEFT);
                Join<Replys, Users> u2 = q.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("title"),"%"+kw+"%"),//제목  or로 검색,sql문의 like %문자%와 같은 역할
                        cb.like(q.get("content"),"%"+kw+"%"), //내용
                        cb.like(u1.get("username"),"%"+kw+"%"), //글 작성자
                        cb.like(a.get("content"),"%"+kw+"%"), //답변 내용
                        cb.like(u2.get("username"),"%"+kw+"%") //답변 작성자
                );
            }
        };
    }

    public Page<Questions> searchKeyword(int page,String keyword, String selectIndex, String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        Sort multiSort = Sort.by(
                Sort.Order.desc("nowtime"), //날짜 기준으로 내림차순으로 정렬
                Sort.Order.desc("uploadnumber") //날짜가 같다면 번호내림차순으로 정렬
        );
        Pageable pageable = PageRequest.of(page, 10,multiSort);

        if (selectIndex != null && !selectIndex.trim().isEmpty() && keyword != null && !keyword.trim().isEmpty()) {
            keyword = '*'+keyword+'*'; //검색 키워드로 와일드 카드 %를 의미
            switch (selectIndex) {
                case "title":
                    return questionsRepository.searchByTitle(keyword, category, pageable);
                case "content":
                    return questionsRepository.searchByContent(keyword, category, pageable);
                case "titleContent":
                    return questionsRepository.searchByTitleContent(keyword, category, pageable);
                case "replys":
                    return questionsRepository.searchByReplys(keyword, category, pageable);
                case "username":
                    return questionsRepository.searchByUsername(keyword, category, pageable);
                default:
                    return questionsRepository.findAll(pageable); // 전체 결과 반환
            }
        } else {
            return questionsRepository.findAll(pageable); // 전체 결과 반환
        }
    }
}