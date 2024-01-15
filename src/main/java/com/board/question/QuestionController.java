package com.board.question;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@RequiredArgsConstructor //final을 선언할때 사용
@Controller
public class QuestionController {

    private final QuestionsService questionsService; //service라는 dto를 생성해서 가져온다
    @GetMapping("/questions/list") //   localhost:8080/가 기본 위치이다.
    public String list(Model model){//매개변수를 model로 지정하면 객체가 자동으로 생성된다.
        List<Questions> questionsList = this.questionsService.getList(); //dto에서 선언한 함수 getlist를 사용
        model.addAttribute("questionsList",questionsList);
        return "questions_list";
    }
}
