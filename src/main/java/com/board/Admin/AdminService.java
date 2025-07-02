package com.board.Admin;

import com.board.Admin.report.Dto.ReportSummaryDto;
import com.board.Admin.report.Report;
import com.board.Admin.report.ReportRepository;
import com.board.Question.Category;
import com.board.Question.Questions;
import com.board.Question.Repository.QuestionsRepository;
import com.board.Reply.Replys;
import com.board.Reply.ReplysRepository;
import com.board.User.Users;
import com.board.User.UsersRepository;
import com.board.User.UsersRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminService {

    @Autowired
    private final UsersRepository usersRepository;

    private final ReportRepository reportRepository;

    private final QuestionsRepository questionsRepository;

    private final ReplysRepository replysRepository;

    public Page<Users> getAllUsers(int page){
        Pageable pageable = PageRequest.of(page, 20);
        return usersRepository.findAll(pageable);
    }

    @Transactional
    public void changeUsersRole(Long userId, UsersRole role) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        user.setRole(role);
    }

    @Transactional
    public void changeUserSuspension(Long id) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (user.isSuspended()) {
            user.unsuspend();
        } else {
            user.suspend();
        }
    }

    public Page<ReportSummaryDto> getReports(int page){
        Pageable pageable = PageRequest.of(page, 20);
        return reportRepository.findAllSummary(pageable);
    }

    public DashboardStatsDto getDashboardStats() {
        long totalUsers = usersRepository.count();
        long todaySignups = usersRepository.countByCreatedDateAfter(LocalDate.now().atStartOfDay());
        long totalQuestions = questionsRepository.count();
        long totalReplies = replysRepository.count();
        long deletedQuestions = questionsRepository.countByDeletedTrue();
        long suspendedUsers = usersRepository.countBySuspendedTrue();
        long recentReports = reportRepository.countByCreatedDateAfter(LocalDate.now().minusDays(7).atStartOfDay());

        long totalReports = reportRepository.count();
        long resolvedReports = reportRepository.countByResolvedTrue();
        double resolvedRate = totalReports == 0 ? 0.0 : ((double) resolvedReports / totalReports) * 100;

        List<String> recentActiveUsers = usersRepository.findTop5ActiveUserNicknames();

        return new DashboardStatsDto(
                totalUsers,
                todaySignups,
                totalQuestions,
                totalReplies,
                deletedQuestions,
                suspendedUsers,
                recentReports,
                resolvedRate,
                recentActiveUsers
        );
    }

    public Page<PostListDto> searchAdminPosts(String keyword, Category category, Boolean reported, Boolean deleted, int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdDate"));
        return questionsRepository.searchAdminPosts(keyword, category, reported, deleted, pageable);
    }

    public void softDeletePost(Integer id) {
        Questions questions = getQuestions(id);
        questions.delete(); // 도메인 메서드 호출
        questionsRepository.save(questions);
    }

    public void restorePost(Integer id) {
        Questions questions = getQuestions(id);
        questions.restore();
        questionsRepository.save(questions);
    }

    public void hidePost(Integer id) {
        Questions questions = getQuestions(id);
        questions.hide();
        questionsRepository.save(questions);
    }

    public void showPost(Integer id) {
        Questions questions = getQuestions(id);
        questions.show();
        questionsRepository.save(questions);
    }

    private Questions getQuestions(Integer id) {
        return questionsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시글을 찾을 수 없습니다."));
    }

    @Transactional
    public void resolveReport(Long id, String action) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("신고를 찾을 수 없습니다."));

        if ("accept".equals(action)) {
            if (report.getQuestion() != null) {
                Questions question = report.getQuestion();
                question.reported();
                questionsRepository.save(question);
            } else if (report.getReply() != null) {
                Replys reply = report.getReply();
                reply.setReported(false);
                replysRepository.save(reply);
            }
        }

        report.resolved();
        reportRepository.save(report);
    }
}
