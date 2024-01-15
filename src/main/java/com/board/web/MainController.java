package com.board.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {
    @GetMapping("/main") //getmapping으로 선언한 주소는 ResponseBody으로 println이 아닌 return 값으로 줘야한다
    @ResponseBody
    public String index() { //형을 정해진 형식으로 선언해줘야한다 string,int ...
        System.out.println("index"); //홈페이지에 뜨지 않는다
        return "welcome to my website";
    }

    @GetMapping("/")
    public String root(){
        return "redirect:/questions/list"; // url을 localhost:8080/questions/list 로 유도해준다
    }
}
