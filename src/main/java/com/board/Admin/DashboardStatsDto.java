package com.board.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalUsers;
    private long todaySignups;
    private long totalQuestions;
    private long totalReplies;
    private long deletedQuestions;
    private long suspendedUsers;
    private long recentReports;
    private double reportResolvedRate;
    private List<String> recentActiveUsers;
}
