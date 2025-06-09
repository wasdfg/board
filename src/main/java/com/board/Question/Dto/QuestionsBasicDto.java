package com.board.Question.Dto;

import java.time.LocalDateTime;


public interface QuestionsBasicDto { //dto를 사용해서 일부 데이터를 가져와 I/O를 단축시키기
    String getTitle();
    Integer getUploadnumber();

    LocalDateTime getNowtime();

}
