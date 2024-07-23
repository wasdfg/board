package com.board.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionsForm {
    @NotEmpty(message = "제목을 필수로 적어주세요.")//조건을 만족하지 않을 때 뜨는 문구
    @Size(max = 200)//길이를 최대 200으로
    private String title;

    @NotEmpty(message = "내용을 필수로 적어주세요.")//조건을 만족하지 않을 때 뜨는 문구
    private String content;

    private String category;
}
