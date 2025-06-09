package com.board.Question;

import lombok.Getter;

@Getter
public enum Category{
    ALL("all", "전체"),
    FREE("free", "자유"),
    ASK("ask", "질문"),
    NOTICE("notice", "공지");

    private final String value;
    private final String label;

    Category(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
