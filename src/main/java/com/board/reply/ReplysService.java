package com.board.reply;


import com.board.question.Questions;
import com.board.question.QuestionsRepository;
import com.board.reply.dto.ReplysBasicDto;
import com.board.user.Users;
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
    public Replys create(Questions questions, String content, Users author, Replys prereplys){
        Replys replys = new Replys();
        replys.setContent(content);
        replys.setNowtime(LocalDateTime.now());
        replys.setQuestions(questions);
        replys.setAuthor(author);
        if(prereplys != null) {
            replys.setParent_id(prereplys.getUploadnumber());
            if(prereplys.getDepth() < 15) { //깊이를 15로 지정
                replys.setDepth(prereplys.getDepth() + 1);
            }
            else{
                replys.setDepth(15);
            }
        }
        else{
            replys.setParent_id(null);
            replys.setDepth(0);
        }
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

    public void vote(Replys replys,Users users){
        replys.getVoter().add(users);
        this.replysRepository.save(replys);
    }

    public Page<ReplysBasicDto> getMyReplysList(int page, Long id){
        Pageable pageable = PageRequest.of(page, 10);
        return this.replysRepository.findByUser(id,pageable);
    }

}
