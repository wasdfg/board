package com.board.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UsersController {
    private final UsersService usersService;

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

    @GetMapping("/chkinfo")
    public String checkPw(){
        return "check_pwd";
    }

    @PostMapping("/chkinfo")
    public String checkPw(@RequestParam("password") String password, Principal principal, Model model){
        SignUpUser signUpUser = this.usersService.getUser(principal.getName());
        if(passwordEncoder.matches(password, signUpUser.getPassword())){
            return changePw();
        }
        else {
            System.out.println("wrong");
            model.addAttribute("error", true);
            return "check_pwd";
        }
    }
}
