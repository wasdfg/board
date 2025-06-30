package com.board.Question;

import com.board.Admin.report.Report;
import com.board.Admin.report.ReportRepository;
import com.board.Admin.report.ReportedReason;
import com.board.DataNotFoundException;

import com.board.ElasticSearch.ElasticSearchService;
import com.board.Question.Dto.QuestionsBasicDto;
import com.board.Question.Dto.QuestionsListDto;
import com.board.Question.Repository.QuestionsImageRepository;
import com.board.Question.Repository.QuestionsRepository;
import com.board.Reply.ReplysRepository;
import com.board.User.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QuestionsService { //service에서 처리

    @Autowired
    private final QuestionsRepository questionsRepository;

    private final ElasticSearchService elasticSearchService;

    private final ApplicationEventPublisher eventPublisher;

    private final QuestionsImageRepository questionsImageRepository;

    private final ReportRepository reportRepository;

    /*영속성 컨택스트를 위한 코드*/
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private final ReplysRepository replysRepository;
    public Questions getQuestions(Integer id){
        Optional<Questions> questions = this.questionsRepository.findById(id); //id로 찾는다.
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


    @Transactional
    public Questions createQuestions(String title, String content, Users users,Category category){
        Questions questions = Questions.create(title,content,users,category);
        em.persist(questions);
        eventPublisher.publishEvent(new QuestionsCreatedEvent(questions));
        return questions;
    }

    @Transactional
    public void modify(Questions questions,String title,String content){ //수정할 내용 저장
        questions.modify(title, content);
        eventPublisher.publishEvent(new QuestionsModifiedEvent(questions));
    }

    @Transactional
    public void report(Questions questions, Users users, ReportedReason reason){

        if (questions.getUsers().getId().equals(users.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신의 글은 신고할 수 없습니다.");
        }

        if (reportRepository.existsByQuestionAndUser(questions, users)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 해당 글을 신고하셨습니다.");
        }

        reportRepository.save(Report.questionReport(users,questions,reason));
        questions.reported();

    }

    @Transactional
    public void delete(Questions questions){
        questions.delete();
        eventPublisher.publishEvent(new QuestionsDeletedEvent(questions.getId()));
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

    @Transactional
    public void increaseViewCount(Integer id) {
        Questions questions = questionsRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("게시글을 찾을 수 없습니다."));

        questions.increaseView();
        questionsRepository.save(questions);
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
            return this.elasticSearchService.searchByKeyword(keyword,category,searchType,pageable);
        }

    }

    @Transactional
    public void createQuestionsWithImages(QuestionsForm form, Users users) throws IOException {
        Questions questions = createQuestions(form.getTitle(),form.getContent(),users, form.getCategory());

        long maxFileSize = 5 * 1024 * 1024;

        List<MultipartFile> images = form.getImages();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    if (image.getSize() > maxFileSize) {
                        throw new IllegalArgumentException("이미지 크기가 너무 큽니다. 5MB 이하만 업로드 가능합니다.");
                    }
                    QuestionsImage questionsImage = QuestionsImage.create(
                            image.getOriginalFilename(),
                            image.getContentType(),
                            image.getSize(),
                            image.getBytes(),
                            questions
                    );
                    em.persist(questionsImage);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<QuestionsImage> getImagesByid(Integer id) {
        return questionsImageRepository.findByQuestionsId(id);
    }
}