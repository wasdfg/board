package com.board.question.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QuestionsBasicDTO { //dto를 사용해서 일부 데이터를 가져와 I/O를 단축시키기
    private String title;
    private Integer uploadnumber;

    private LocalDateTime nowtime;

    public QuestionsBasicDTO(String title, Integer uploadnumber, LocalDateTime nowtime) { //생성자 추가
        this.title = title;
        this.uploadnumber = uploadnumber;
        this.nowtime = nowtime;
    }
}
