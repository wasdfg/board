package com.board.question;

import com.board.reply.ReplysForm;
import com.board.user.SignUpUser;
import com.board.user.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;

import java.security.Principal;

@RequestMapping("/questions") // /questions로 시작하는 url의 앞부분을 prefix해준다.
@RequiredArgsConstructor //final을 선언할때 사용
@Controller
public class QuestionsController {
    private final QuestionsRepository questionsRepository;
    private final QuestionsService questionsService; //service라는 dto를 생성해서 가져온다

    private final UsersService usersService;

    @GetMapping("/list") //   localhost:8080/가 기본 위치이다.
    public String list(Model model,@RequestParam(value="page", defaultValue="0") int page){//매개변수를 model로 지정하면 객체가 자동으로 생성된다.
        Page<Questions> paging = this.questionsService.getList(page);
        model.addAttribute("paging", paging);
        return "questions_list";
    }

    @GetMapping(value = "/detail/{uploadnumber}")
    public String detail(Model model, @PathVariable("uploadnumber") Integer uploadnumber, ReplysForm replysForm){
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        model.addAttribute("questions",questions);

        return "questions_detail";
    }

    @PreAuthorize("isAuthenticated()") //로그인 된 경우에만 실행됨, 로그인 된 상태에서 로그아웃하는 경우 강제로 로그인페이지로 이동함
    @GetMapping("/create")
    public String questionsCreate(QuestionsForm questionsForm){
        return "questions_form";
    }

    @PreAuthorize("isAuthenticated()") //로그인 된 경우에만 실행됨, 로그인 된 상태에서 로그아웃하는 경우 강제로 로그인페이지로 이동함
    @PostMapping("/create") //url처리
    public String questionsCreate(@Valid QuestionsForm questionsForm, BindingResult bindingResult, Principal principal){//질문 폼에 조건 추가
        if(bindingResult.hasErrors()){ //에러가 있는지 검사
            return "questions_form";
        }
        SignUpUser signUpUser = this.usersService.getUser(principal.getName());
        this.questionsService.create(questionsForm.getTitle(),questionsForm.getContent(),signUpUser);
        return "redirect:/questions/list";
    }
}
