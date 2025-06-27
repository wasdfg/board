package com.board.Reply;

import com.board.Admin.report.ReportedReason;
import com.board.Question.Questions;
import com.board.Question.QuestionsService;
import com.board.User.Users;
import com.board.User.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;


@RequestMapping("/replys")
@RequiredArgsConstructor
@Controller
public class ReplysController {
    private final QuestionsService questionsService;
    private final ReplysService replysService;

    private final UsersService usersService;

    @PreAuthorize("isAuthenticated()")//로그인 된 경우에만 실행됨, 로그인 된 상태에서 로그아웃하는 경우 강제로 로그인페이지로 이동함
    @PostMapping("/create/{id}") //post로 처리 대용량처리에 용이
    public String replysCreate(Model model, @PathVariable("id") Integer id, @Valid ReplysForm replysForm, BindingResult bindingResult,Principal principal, @RequestParam(value = "parentId", required = false) Integer parentId){//principal은 현재 로그인한 유저의 정보를 알려준다
        Questions questions = this.questionsService.getQuestions(id);
        Users users = this.usersService.getUsers(principal.getName());

        if(bindingResult.hasErrors()){
            model.addAttribute("questions", questions);
            return "questions/questions_detail";
        }

        Replys Preplys = null;
        if(parentId != null){
            Preplys = this.replysService.getReplys(parentId);
        }
        Replys replys = this.replysService.create(questions,replysForm.getContent(),users,Preplys);
        return String.format("redirect:/questions/detail/%s#replys_%s",replys.getQuestions().getId(),replys.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String replysModify(ReplysForm replysForm, @PathVariable("id") Integer id, Principal principal) {
        Replys replys = this.replysService.getReplys(id);
        if (!replys.getUsers().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        replysForm.setContent(replys.getContent());
        return "replys_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String replysModify(@Valid ReplysForm replysForm, BindingResult bindingResult,
                               @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "replys_form";
        }
        Replys replys = this.replysService.getReplys(id);
        if (!replys.getUsers().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.replysService.modify(replys, replysForm.getContent());
        return String.format("redirect:/questions/detail/%s", replys.getQuestions().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String replysDelete(Principal principal, @PathVariable("id") Integer id) {
        Replys replys = this.replysService.getReplys(id);

        if (!replys.getUsers().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.replysService.delete(replys.getId());

        return String.format("redirect:/questions/detail/%s", replys.getQuestions().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/report/{id}")
    public String replysReport(Principal principal, @RequestParam ReportedReason reason, @PathVariable("id") Integer id) {
        Replys replys = this.replysService.getReplys(id);

        replysService.report(id, replys.getUsers(), reason);

        return "redirect:/questions/detail/" + replys.getQuestions().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String replysVote(Principal principal, @PathVariable("id") Integer id,Model model) {
        Replys replys = this.replysService.getReplys(id);
        Users users = this.usersService.getUsers(principal.getName());
        if(replys.getVoter().contains(users) == true){ //이미 투표를 했는지 확인
            model.addAttribute("voted",true);
        }
        else{
            this.replysService.vote(replys, users);
        }
        return String.format("redirect:/questions/detail/%s#replys_%s",replys.getQuestions().getId(),replys.getId());
    }
}

