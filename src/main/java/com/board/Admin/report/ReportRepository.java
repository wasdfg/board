package com.board.Admin.report;

import com.board.Admin.PostListDto;
import com.board.Admin.report.Dto.ReportSummaryDto;
import com.board.Question.Category;
import com.board.Question.Questions;
import com.board.Reply.Replys;
import com.board.User.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ReportRepository extends JpaRepository<Report,Long> {

    boolean existsByQuestionAndUser(Questions questions, Users users);

    boolean existsByReplyAndUser(Replys replys, Users users);

    @Query("""
    SELECT new com.board.Admin.report.ReportSummaryDto(
        r.users.nickname,
        r.reason,
        r.reportedDate,
        r.question.id,
        r.reply.question.id
    )
    FROM Report r
    ORDER BY r.reportedDate DESC
    """)
    Page<ReportSummaryDto> findAllSummary(Pageable pageable);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.createdDate >= :start")
    long countByCreatedDateAfter(LocalDateTime start);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.resolved = true")
    long countByResolvedTrue();

    Page<PostListDto> searchAdminPosts(String keyword, Category category, Boolean reported, Boolean deleted, Pageable pageable);
}
