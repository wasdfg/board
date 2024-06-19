package com.board.user.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailDto{
    String email;
    String title;
    String message;

    public MailDto(String email,String title,String message){
        this.email = email;
        this.title = title;
        this.message = message;
    }
}
