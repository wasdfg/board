package com.board.user;

import com.board.question.Questions;
import com.board.question.QuestionsRepository;
import com.board.question.QuestionsService;
import com.board.reply.Replys;
import com.board.reply.ReplysRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UsersController {
    private final UsersService usersService;

    private final ReplysRepository replysRepository;
    private final QuestionsService questionsService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signup(UsersCreateForm usersCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UsersCreateForm usersCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { 
            return "signup_form";
        }

        if (!usersCreateForm.getPassword1().equals(usersCreateForm.getPassword2())) { //비밀번호 검증이 실패했을 경우
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
        usersService.create(usersCreateForm.getUsername(),usersCreateForm.getPassword1(), usersCreateForm.getEmail());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login_form";
    }

    @GetMapping("/chginfo")
    public String changePw(){
        return "change_pwd";
    }

    @PostMapping("/chginfo")
    public String changePw(@RequestParam("inputpw") String inputpw,@RequestParam("checkpw") String checkpw,Principal principal,Model model){
        if(principal == null){
            return login();
        }
        else{
            SignUpUser signUpUser = this.usersService.getUser(principal.getName());
            if (inputpw.equals(checkpw)) {
                this.usersService.updatePw(signUpUser,checkpw);
                return "redirect:/";
            }
            else{
                System.out.println("wrong");
                model.addAttribute("error",true);
                return "/chginfo";
            }
        }
    }

    @GetMapping("/chkinfo")
    public String checkPw(Principal principal){
        if(principal == null){
            return login();
        }
        else {
            return "check_pwd";
        }
    }

    @PostMapping("/chkinfo")
    public String checkPw(@RequestParam("password") String password, Principal principal, Model model){
        SignUpUser signUpUser = this.usersService.getUser(principal.getName());
        if(passwordEncoder.matches(password, signUpUser.getPassword())) {
            return changePw();
        }
        else{
            System.out.println("wrong");
            model.addAttribute("error", true);
            return "check_pwd";
        }
    }

    @GetMapping("/findAccount")
    public String findAccount(){
        return "find_account";
    }

    @GetMapping("/chkEmail")
    public String checkEmail(){
        return "send_Email";
    }

    @GetMapping("/findId")
    public String findId(){
        return "find_id";
    }

    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestParam("inputEmail") String email) {
        Optional<SignUpUser> signUpUser = usersService.getUserByEmail(email);
        if (signUpUser.isPresent()) {
            return ResponseEntity.ok(signUpUser.get().getUsername());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/showQNA")
    public String showQNA(Principal principal,Model model,@RequestParam(defaultValue = "0") int page,@RequestParam(value = "kw", defaultValue = "") String kw,@RequestParam(value = "category", defaultValue = "") String category){
        Page<Questions> paging = this.questionsService.getList(page,principal.getName());
        model.addAttribute("paging", paging);
        return "show_info";
    }
}
