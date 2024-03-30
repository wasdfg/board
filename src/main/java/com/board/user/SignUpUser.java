package com.board.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SignUpUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true) //유일성을 만족시키기 위해 중복불가
    private String username;

    private String password;

    @Column(unique = true) //이메일 당 1개만 가입하게 설정
    private String email;
}
