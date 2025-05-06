package com.board.question;

import com.board.DataNotFoundException;

import com.board.question.dto.QuestionsBasicDto;
import com.board.question.dto.QuestionsListDto;
import com.board.reply.Replys;
import com.board.reply.ReplysRepository;
import com.board.user.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionsService { //service에서 처리

    @Autowired
    private final QuestionsRepository questionsRepository;
    
    /*영속성 컨택스트를 위한 코드*/
    @PersistenceContext
    private EntityManager em;

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

    public Long getQuestionsCount(){
        Long count = this.questionsRepository.count();
        return count;
    }

    public List<Replys> getReplysList(Integer uploadnumber) { //투표수 기준으로 정렬
        Optional<Questions> questions = this.questionsRepository.findById(uploadnumber);
        if(questions.isPresent()){
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

    @Transactional
    public void createQuestions(String title, String content, Users users,Category category){
        Questions questions = Questions.create(title,content,users,category);
        em.persist(questions);

    }

    @Transactional
    public void modify(Questions questions,String title,String content){ //수정할 내용 저장
        questions.modify(title, content);
    }

    @Transactional
    public void delete(Questions questions){
        this.questionsRepository.delete(questions);
    }

    @Transactional
    public void vote(Questions questions,Users users){
        questions.getVoter().add(users);
        this.questionsRepository.save(questions);
    }
    public Page<QuestionsBasicDto> getMyWriteList(int page, Long id) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.questionsRepository.findByUser(id,pageable);
    }

    public Page<QuestionsListDto> getList(int page,Category category,String keyword,SearchType searchType) {
        Pageable pageable = PageRequest.of(page, 10);
        System.out.println("Category : "+category);
        System.out.println("keyword : "+keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("no keyword so use findAllWithoutKeyword");
            return questionsRepository.findAllWithoutKeyword(category, pageable);
        } else {
            System.out.println("yes keyword so use searchPage "+keyword);
            return this.questionsRepository.searchPage(category,keyword,searchType,pageable);
        }

    }

    public Page<QuestionsListDto> searchCheck(int page, Category category) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("uploadnumber"))); //10개씩 페이징예정
        if(category.equals("all")) {
            category = null;
        }
        System.out.println(category);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("쿼리 실행");
        System.out.println("111111");
        Page<QuestionsListDto> results = questionsRepository.findAllWithoutKeyword(category,pageable);
        //Page<QuestionsListDto> results = questionsRepository.findAllWithoutKeyword(category,pageable).map(QuestionsListDto::from);
        System.out.println("list의 사이즈는 "+results.getTotalElements());
        long totalCount = questionsRepository.countWithoutKeyword(category);
        stopWatch.stop();
        System.out.println("쿼리 실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");

        return results;
    }

    public Page<Questions> searchByKeyword(int page,String keyword,String selectIndex,String category){
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("uploadnumber"))); //10개씩 페이징예정
        if (selectIndex != null && !selectIndex.trim().isEmpty() && keyword != null && !keyword.trim().isEmpty()) {
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
                    return questionsRepository.findAllList(keyword,category,pageable); // 전체 결과 반환
            }
        } else {
            return questionsRepository.findAllList(keyword,category,pageable); // 전체 결과 반환
        }
    }
}