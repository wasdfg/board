package com.board.reply;


import com.board.question.Questions;
import com.board.question.QuestionsRepository;
import com.board.user.SignUpUser;
import com.board.reply.dto.ReplysBasicDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReplysService {
    private final ReplysRepository replysRepository;

    private final QuestionsRepository questionsRepository;
    public Replys create(Questions questions, String content, SignUpUser author,Replys prereplys){
        Replys replys = new Replys();
        replys.setContent(content);
        replys.setNowtime(LocalDateTime.now());
        replys.setQuestions(questions);
        replys.setAuthor(author);
        if(prereplys != null) {
            replys.setParent_id(prereplys.getUploadnumber());
        }
        else{
            replys.setParent_id(null);
        }
        questions.setReplysListsize(questions.getReplysListsize()+1);
        this.questionsRepository.save(questions);
        this.replysRepository.save(replys); //위에 있는 content nowtime questions를 replys에 저장
        return replys;
    }

    public Replys getReplys(Integer uploadnumber){
        Optional<Replys> replys = this.replysRepository.findById(uploadnumber);
        if(replys.isPresent()){
            return replys.get();
        }
        else{
            throw new RuntimeException("replys not found");
        }
    }

    public void modify(Replys replys,String content){
        replys.setContent(content);
        replys.setModifyDate(LocalDateTime.now());
        this.replysRepository.save(replys);
    }

    public void delete(Replys replys) {
        this.replysRepository.delete(replys);
    }

    public void vote(Replys replys,SignUpUser signUpUser){
        replys.getVoter().add(signUpUser);
        this.replysRepository.save(replys);
    }

    public Page<ReplysBasicDto> getList(int page, SignUpUser signUpUser){
        List<Sort.Order> sorts = new ArrayList<>();
        Sort multiSort = Sort.by(
                Sort.Order.desc("nowtime"), //날짜 기준으로 내림차순으로 정렬
                Sort.Order.desc("uploadnumber") //날짜가 같다면 번호내림차순으로 정렬
        );
        Pageable pageable = PageRequest.of(page, 10,multiSort);
        return this.replysRepository.findByUser(signUpUser.getUsername(),pageable);
    }

}
