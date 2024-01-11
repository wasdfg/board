package com.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity //데이터베이스 entity로 사용하기 위해 선언
//entity는 setter를 사용하지 않는다. 데이터베이스와 연동되는 사안이기 때문에 데이터 수정이 쉬우면 안된다.
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //generationtype은 값을 자동으로 1씩 증가시켜준다
    private Integer id; //int와 달리 integer는 null값으로 사용이 가능하여 sql용으로 적절하다. 저장공간도 크다
    
    @Column(length = 200) //길이를 200으로 제한
    private String subject;

    @Column(columnDefinition = "TEXT")//해당 컬럼을 TEXT로 정의 하겠다
    private String content;

    private LocalDateTime createDate;
}
