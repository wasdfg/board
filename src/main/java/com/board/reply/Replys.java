package com.board.reply;

import com.board.question.Questions;
import com.board.user.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@Getter
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
    private Users author;

    @ManyToMany(fetch = FetchType.LAZY)
    Set<Users> voter;
    //나중에 답변 추천기능 내림차순으로 출력할 예정

    @ColumnDefault("null")
    private Integer parent_id;

    private int depth;

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    public void setAuthor(Users author) {
        this.author = author;
    }

    public void setVoter(Set<Users> voter) {
        this.voter = voter;
    }

    public void setNowtime(LocalDateTime nowtime) {
        this.nowtime = nowtime;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public void setQuestions(Questions questions) {
        this.questions = questions;
    }

    public static Replys create(String content, Users author, Questions questions, Integer parentId, int depth) {
        Replys reply = new Replys();
        reply.content = content;
        reply.nowtime = LocalDateTime.now();
        reply.author = author;
        reply.questions = questions;
        reply.parent_id = parentId;
        reply.depth = depth;
        return reply;
    }
}
