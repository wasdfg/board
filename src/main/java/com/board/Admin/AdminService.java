package com.board.Admin;

import com.board.Admin.report.Dto.ReportSummaryDto;
import com.board.Admin.report.Report;
import com.board.Admin.report.ReportRepository;
import com.board.User.Users;
import com.board.User.UsersRepository;
import com.board.User.UsersRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {

    @Autowired
    private final UsersRepository usersRepository;

    private final ReportRepository reportRepository;


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
}
