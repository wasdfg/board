package com.board.Reply;

import com.board.Admin.report.Report;
import com.board.Admin.report.ReportRepository;
import com.board.Admin.report.ReportedReason;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional // 클래스 전체에 트랜잭션 적용 (쓰기 기본)
public class ReplysService {

    private final ReplysRepository replysRepository;

    private final ReportRepository reportRepository;
    private final QuestionsRepository questionsRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Replys create(Questions questions, String content, Users users, Replys prereplys) {
        Replys replys = new Replys();
        replys.setContent(content);
        replys.setNowtime(LocalDateTime.now());
        replys.setQuestions(questions);
        replys.setUsers(users);

        if (prereplys != null) {
            replys.setParent_id(prereplys.getId());
            replys.setDepth(Math.min(prereplys.getDepth() + 1, 15)); // 깊이 제한
        } else {
            replys.setParent_id(null);
            replys.setDepth(0);
        }

        replysRepository.save(replys);

        eventPublisher.publishEvent(new ReplyCreatedEvent(
                replys.getId().longValue(),
                prereplys != null ? prereplys.getUsers().getId() : null,
                questions.getUsers().getId(),
                users.getId(),
                content
        ));

        return replys;
    }

    @Transactional(readOnly = true)
    public Replys getReplys(Integer id) {
        return replysRepository.findById(id)
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

    public void report(Integer replyId, Users users, ReportedReason reason) {

        Replys reply = this.getReplys(replyId);

        if (reply.getUsers().getId().equals(users.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자신의 댓글은 신고할 수 없습니다.");
        }

        if (reportRepository.existsByReplyAndUser(reply, users)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신고하셨습니다.");
        }

        reportRepository.save(Report.replyReport(users, reply, reason));
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

    public List<Replys> getReplysList(Integer id) {
        if (!questionsRepository.existsById(id)) {
            throw new DataNotFoundException("questions not found");
        }
        return this.replysRepository.findReplysByQuestionsId(id);
    }
}