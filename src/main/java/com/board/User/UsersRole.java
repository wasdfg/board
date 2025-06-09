package com.board.User;

import lombok.Getter;

@Getter
public enum UsersRole { //로그인 유형 검사
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UsersRole(String value) {
        this.value = value;
    }

    private String value;
}
