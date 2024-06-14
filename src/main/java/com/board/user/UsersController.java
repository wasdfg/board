package com.board.user;

import com.board.question.dto.QuestionsBasicDTO;
import com.board.question.QuestionsService;
import com.board.reply.ReplysService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UsersController {
    private final UsersService usersService;

    private final QuestionsService questionsService;

    private final ReplysService replysService;

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
    @PreAuthorize("isAuthenticated()")
    public String changePw(){
        return "change_pwd";
    }

    @PostMapping("/chginfo")
    @PreAuthorize("isAuthenticated()")
    public String changePw(@RequestParam("inputpw") String inputpw,@RequestParam("checkpw") String checkpw,Principal principal,Model model){
        SignUpUser signUpUser = this.usersService.getUser(principal.getName());
        if (inputpw.equals(checkpw)) {
            this.usersService.updatePw(signUpUser,checkpw);
            return "redirect:/";
        }
        else{
            model.addAttribute("error",true);
            return "change_pwd";
        }
    }

    @GetMapping("/chkinfo")
    @PreAuthorize("isAuthenticated()")
    public String checkPw(){
        return "check_pwd";
    }

    @PostMapping("/chkinfo")
    @PreAuthorize("isAuthenticated()")
    public String checkPw(@RequestParam("password") String password, Principal principal, Model model){
        SignUpUser signUpUser = this.usersService.getUser(principal.getName());
        if(passwordEncoder.matches(password, signUpUser.getPassword())) {
            return changePw();
        }
        else{
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/showQNA")
    public String showQNA(Principal principal,Model model,@RequestParam(value="page", defaultValue="0") int page){
        SignUpUser signUpUser = this.usersService.getUser(principal.getName());
        Page<QuestionsBasicDTO> Qpaging = this.questionsService.getList(page,signUpUser);
        //Page<Replys> Rpaging = this.replysService.getList(page,signUpUser);
        model.addAttribute("Qpaging", Qpaging);
        return "show_info";
    }
}
