package com.board.question;

import com.board.reply.Replys;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor //기본 생성자 자동추가 public Question{}을 생성해준다
@Getter
@Setter
@Entity //데이터베이스 entity로 사용하기 위해 선언
//entity는 setter를 사용하지 않는다. 데이터베이스와 연동되는 사안이기 때문에 데이터 수정이 쉬우면 안된다.
public class Questions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //generationtype은 값을 자동으로 1씩 증가시켜준다
    private Integer uploadnumber; //int와 달리 integer는 null값으로 사용이 가능하여 sql용으로 적절하다. 저장공간도 크다
    
    @Column(length = 200,nullable = false) //길이를 200으로 제한
    private String title;

    @Column(columnDefinition = "TEXT",nullable = false)//해당 컬럼을 TEXT로 정의 하겠다
    private String content;

    private LocalDateTime nowtime;


    
    @OneToMany(mappedBy = "questions",cascade = CascadeType.REMOVE) //질문이 삭제되면 답변도 같이 삭제되게 cascade를 추가
    private List<Replys> replysList; //reply 객체를 list로 담아둔다.
    //참조하기 위해 questions.getReplysList()를 호출한다.
    //mappedBy는 참조하는 엔티티를 선언해준다.
    
    @Builder
    public Questions(String title, String content, LocalDateTime nowtime) {
        this.title = title;
        this.content = content;
        this.nowtime = nowtime;
    }

}
