package com.board.reply;

import com.board.question.Questions;
import com.board.user.SignUpUser;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @ManyToOne(fetch = FetchType.LAZY) //질문은 하나지만 답변은 여러개가 될수 있기에 N:1관계를 사용
    @JoinColumn(name = "questions_uploadnumber",referencedColumnName = "uploadnumber")
    private Questions questions; //Questions 엔티티를 참조하기 위해 선언

    @ManyToOne(fetch = FetchType.LAZY) //글쓴이 1명당 여러 답변을 할 수 있어 다대1로 설정
    @JoinColumn(name="author_id",referencedColumnName = "id")
    private SignUpUser author;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<SignUpUser> voter;
    //나중에 답변 추천기능 내림차순으로 출력할 예정

    @ColumnDefault("null")
    private Integer parent_id;

    // 데이터베이스에 물리적으로 저장하지 않지만 논리적으로 사용 가능한 속성
    private int depth;

}
