package com.board.Question;

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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
    public void delete(Questions questions){
        this.questionsRepository.delete(questions);
        eventPublisher.publishEvent(new QuestionsDeletedEvent(questions.getUploadnumber()));
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
    public void increaseViewCount(Integer uploadnumber) {
        Questions questions = questionsRepository.findById(uploadnumber)
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
    public List<QuestionsImage> getImagesByUploadNumber(Integer uploadNumber) {
        return questionsImageRepository.findByQuestionsUploadNumber(uploadNumber);
    }
}