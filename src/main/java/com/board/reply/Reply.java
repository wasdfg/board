package com.board.reply;

import com.board.question.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id를 순차적으로 증가
    private Integer UploadNumber;

    @Column(columnDefinition = "Text")
    private String Content;

    private LocalDateTime NowTime;

    @ManyToOne //질문은 하나지만 답변은 여러개가 될수 있기에 N:1관계를 사용
    private Question question; //Question 엔티티를 참조하기 위해 선언
}
