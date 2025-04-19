package com.board.question;

import lombok.Getter;

@Getter
public enum SearchType {
    ALL("all", "전체"),
    TITLE_CONTENT("titleContent", "제목+내용"),
    TITLE("title", "제목"),
    CONTENT("content", "내용"),
    USERNAME("username", "글쓴이"),
    REPLYS("replys", "댓글");

    private final String value;
    private final String label;

    SearchType(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
