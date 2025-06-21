package com.board.Admin.report;

import com.board.Question.Questions;
import com.board.User.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "questions_id"})
})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    //신고자
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계
    @JoinColumn(name = "user_id") // FK 컬럼 이름
    private Users users;
    
    //신고사유
    @Enumerated(EnumType.STRING)
    private ReportedReason reason;

    @Column(nullable = false)
    private LocalDateTime reportedDate;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계
    @JoinColumn(name = "questions_id") // FK 컬럼 이름
    private Questions question;

    public static Report of(Users users, Questions questions, ReportedReason reason) {
        Report r = new Report();
        r.users = users;
        r.question = questions;
        r.reason = reason;
        r.reportedDate = LocalDateTime.now();
        return r;
    }
}
