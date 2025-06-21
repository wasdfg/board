package com.board.Admin.report;

import com.board.Question.Questions;
import com.board.User.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
    boolean existsByQuestionAndUser(Questions questions, Users users);
}
