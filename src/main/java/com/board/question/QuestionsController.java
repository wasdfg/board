package com.board.question;

import com.board.reply.Replys;
import com.board.reply.ReplysForm;
import com.board.user.SignUpUser;
import com.board.user.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/questions") // /questions로 시작하는 url의 앞부분을 prefix해준다.
@RequiredArgsConstructor //final을 선언할때 사용
@Controller
public class QuestionsController { //controller에서 요청을 받아와서
    private final QuestionsRepository questionsRepository;
    private final QuestionsService questionsService; //service라는 dto를 생성해서 가져온다

    private final UsersService usersService;

    @GetMapping("/list") //   localhost:8080/가 기본 위치이다.
    public String list(Model model,@RequestParam(value="page", defaultValue="0") int page,@RequestParam(value = "kw", defaultValue = "") String kw,@RequestParam(value = "category", defaultValue = "") String category){//매개변수를 model로 지정하면 객체가 자동으로 생성된다.
        Page<Questions> paging = this.questionsService.getList(page,kw,category);
        model.addAttribute("paging", paging);
        model.addAttribute("kw",kw);
        return "questions_list";
    }

    @GetMapping(value = "/detail/{uploadnumber}")
    public String detail(Model model,@PathVariable("uploadnumber") Integer uploadnumber, ReplysForm replysForm){
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        List<Replys> sortVoter = this.questionsService.getSortByVoter(uploadnumber);
        model.addAttribute("sortVoter",sortVoter);
        model.addAttribute("questions",questions);

        return "questions_detail";
    }

    @PreAuthorize("isAuthenticated()") //로그인 된 경우에만 실행됨, 로그인 된 상태에서 로그아웃하는 경우 강제로 로그인페이지로 이동함
    @GetMapping("/create")
    public String questionsCreate(QuestionsForm questionsForm,Model model){
        List<Category> categories = Arrays.asList(
                new Category("free", "자유"),
                new Category("ask", "질문"),
                new Category("notice", "공지")
        );
        model.addAttribute("categories", categories);
        return "questions_form";
    }

    @PreAuthorize("isAuthenticated()") //로그인 된 경우에만 실행됨, 로그인 된 상태에서 로그아웃하는 경우 강제로 로그인페이지로 이동함
    @PostMapping("/create") //url처리
    public String questionsCreate(@Valid QuestionsForm questionsForm, BindingResult bindingResult, Principal principal,Model model){//질문 폼에 조건 추가
        if(bindingResult.hasErrors()){ //에러가 있는지 검사
            return "questions_form";
        }
        SignUpUser signUpUser = this.usersService.getUser(principal.getName());
        this.questionsService.create(questionsForm.getTitle(),questionsForm.getContent(),signUpUser,questionsForm.getCategory());
        return "redirect:/questions/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{uploadnumber}")
    public String questionModify(QuestionsForm questionsForm, @PathVariable("uploadnumber") Integer uploadnumber, Principal principal) {
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        if(!questions.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionsForm.setTitle(questions.getTitle());
        questionsForm.setContent(questions.getContent());
        return "questions_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{uploadnumber}")
    public String questionsModify(@Valid QuestionsForm questionsForm,BindingResult bindingResult,@PathVariable("uploadnumber") Integer uploadnumber, Principal principal) { //글 수정
        if (bindingResult.hasErrors()) {
            return "questions_form";
        }
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        if(!questions.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionsService.modify(questions,questionsForm.getTitle(),questionsForm.getContent());
        return String.format("redirect:/questions/detail/%s", uploadnumber);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("uploadnumber") Integer uploadnumber) {
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        if (!questions.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionsService.delete(questions);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{uploadnumber}")
    public String questionsVote(Principal principal, @PathVariable("uploadnumber") Integer uploadnumber, Model model) {
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        SignUpUser signUpUser = this.usersService.getUser(principal.getName()); // 현재 로그인한 유저의 정보를 담는다

        boolean alreadyVoted = questions.getVoter().contains(signUpUser); // 이미 투표를 했는지 확인
        if (!alreadyVoted) {
            this.questionsService.vote(questions, signUpUser);
        }

        model.addAttribute("voted", alreadyVoted);
        return "redirect:/questions/detail/" + uploadnumber + "?voted=" + alreadyVoted;
    }
}