package com.board.Admin.report.Dto;

import com.board.Admin.report.ReportedReason;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportSummaryDto {
    private String reporterNickname;
    private ReportedReason reason;
    private LocalDateTime reportedDate;

    private Integer questionId;
    private Integer replyQuestionId;

    public ReportSummaryDto(String reporterNickname,
                            ReportedReason reason,
                            LocalDateTime reportedDate,
                            Integer questionId,
                            Integer replyQuestionId) {
        this.reporterNickname = reporterNickname;
        this.reason = reason;
        this.reportedDate = reportedDate;
        this.questionId = questionId;
        this.replyQuestionId = replyQuestionId;
    }
}
