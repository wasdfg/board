package com.board.Admin.report;

import com.board.Question.Questions;
import com.board.Reply.Replys;
import com.board.User.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {

    boolean existsByQuestionAndUser(Questions questions, Users users);

    boolean existsByReplyAndUser(Replys replys, Users users);
}
