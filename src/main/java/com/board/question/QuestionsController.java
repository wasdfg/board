package com.board.question;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("/questions") // /questions로 시작하는 url의 앞부분을 prefix해준다.
@RequiredArgsConstructor //final을 선언할때 사용
@Controller
public class QuestionsController {

    private final QuestionsService questionsService; //service라는 dto를 생성해서 가져온다
    @GetMapping("/list") //   localhost:8080/가 기본 위치이다.
    public String list(Model model){//매개변수를 model로 지정하면 객체가 자동으로 생성된다.
        List<Questions> questionsList = this.questionsService.getList(); //dto에서 선언한 함수 getlist를 사용
        model.addAttribute("questionsList",questionsList);
        return "questions_list";
    }

    @GetMapping(value = "/detail/{uploadnumber}")
    public String detail(Model model, @PathVariable("uploadnumber") Integer uploadnumber){
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        model.addAttribute("questions",questions);
        model.addAttribute("replysListSize", questions.getReplysList().size());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); //날짜 형식을 변환
        String formattedDateTime = questions.getNowtime().format(formatter);
        model.addAttribute("questionsdate",formattedDateTime);

        return "questions_detail";
    }

    @GetMapping("/create")
    public String questionsCreate(){
        return "questions_form";
    }

    @PostMapping("/create") //url처리
    public String questionsCreate(@RequestParam(value="title") String title, @RequestParam(value="content") String content) {
        this.questionsService.create(title, content);
        return "redirect:/questions/list";
    }
}
