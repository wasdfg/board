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

    public void changeUsersRole(Long id, UsersRole role){

    }

    public Page<ReportSummaryDto> getReports(int page){
        Pageable pageable = PageRequest.of(page, 20);
        return reportRepository.findAllSummary(pageable);
    }
}
