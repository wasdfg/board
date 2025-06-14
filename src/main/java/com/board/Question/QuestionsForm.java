package com.board.Question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class QuestionsForm {
    @NotEmpty(message = "제목을 필수로 적어주세요.")//조건을 만족하지 않을 때 뜨는 문구
    @Size(max = 200)//길이를 최대 200으로
    private String title;

    @NotEmpty(message = "내용을 필수로 적어주세요.")//조건을 만족하지 않을 때 뜨는 문구
    private String content;

    @NotNull(message = "카테고리를 입력해 주세요.")
    private Category category;

    private List<MultipartFile> images;
}
