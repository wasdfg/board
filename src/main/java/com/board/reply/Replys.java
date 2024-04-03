package com.board.reply;

import com.board.question.Questions;
import com.board.user.SignUpUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Replys {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id를 순차적으로 증가
    private Integer uploadnumber;

    @Column(columnDefinition = "Text")
    private String content;

    private LocalDateTime nowtime;

    private LocalDateTime modifyDate; //수정된 글의 시간

    @ManyToOne //질문은 하나지만 답변은 여러개가 될수 있기에 N:1관계를 사용
    private Questions questions; //Questions 엔티티를 참조하기 위해 선언

    @ManyToOne //글쓴이 1명당 여러 답변을 할 수 있어 다대1로 설정
    private SignUpUser author;

    @ManyToMany
    Set<SignUpUser> voter;
    //나중에 답변 추천기능 내림차순으로 출력할 예정
}
