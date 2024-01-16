package com.board;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND,reason = "entity not found") //404에러와 엔티티를 찾을수 없다는 이유를 알려줌
public class DataNotFoundException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public DataNotFoundException(String message){
        super(message);
    }
}
