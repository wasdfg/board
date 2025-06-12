package com.board.Reply;

import com.board.DataNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import com.board.Notice.ReplyCreatedEvent;
import com.board.Question.Questions;
import com.board.Question.Repository.QuestionsRepository;
import com.board.Reply.dto.ReplysBasicDto;
import com.board.User.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional // 클래스 전체에 트랜잭션 적용 (쓰기 기본)
public class ReplysService {

    private final ReplysRepository replysRepository;
    private final QuestionsRepository questionsRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Replys create(Questions questions, String content, Users users, Replys prereplys) {
        Replys replys = new Replys();
        replys.setContent(content);
        replys.setNowtime(LocalDateTime.now());
        replys.setQuestions(questions);
        replys.setUsers(users);

        if (prereplys != null) {
            replys.setParent_id(prereplys.getUploadnumber());
            replys.setDepth(Math.min(prereplys.getDepth() + 1, 15)); // 깊이 제한
        } else {
            replys.setParent_id(null);
            replys.setDepth(0);
        }

        replysRepository.save(replys);

        eventPublisher.publishEvent(new ReplyCreatedEvent(
                replys.getUploadnumber().longValue(),
                prereplys != null ? prereplys.getUsers().getId() : null,
                questions.getUsers().getId(),
                users.getId(),
                content
        ));

        return replys;
    }

    @Transactional(readOnly = true)
    public Replys getReplys(Integer uploadnumber) {
        return replysRepository.findById(uploadnumber)
                .orElseThrow(() -> new RuntimeException("replys not found"));
    }

    public void modify(Replys replys, String content) {
        replys.setContent(content);
        replys.setModifyDate(LocalDateTime.now());
        replysRepository.save(replys);
    }

    public void delete(Integer id) {
        Replys reply = replysRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글이 존재하지 않습니다."));
        reply.setDeleted(true);
        reply.setContent("삭제된 댓글입니다.");
        reply.setUsers(null); // 작성자도 지움
        replysRepository.save(reply);
    }

    public void vote(Replys replys, Users users) {
        replys.getVoter().add(users);
        replysRepository.save(replys);
    }

    @Transactional(readOnly = true)
    public Page<ReplysBasicDto> getMyReplysList(int page, Long id) {
        Pageable pageable = PageRequest.of(page, 10);
        return replysRepository.findByUser(id, pageable);
    }

    public List<Replys> getReplysList(Integer uploadnumber) {
        if (!questionsRepository.existsById(uploadnumber)) {
            throw new DataNotFoundException("questions not found");
        }
        return this.replysRepository.findReplysByQuestionsUploadnumber(uploadnumber);
    }
}