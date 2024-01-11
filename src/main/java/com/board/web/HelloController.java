package com.board.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller //MVC의 C인 Controller의 기능을 수행하는 어노테이션
public class HelloController {
    @GetMapping("/hello") //http://localhost:8080/hello 요청이 발생했을 때 hello 메서드가 실행된다
    @ResponseBody //문자열이 출력방식일 때 사용한다. post방식일 경우 @PostMapping 사용
    public String hello() {
        return "Hello World";
    }
}
