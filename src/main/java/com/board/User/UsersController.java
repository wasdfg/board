package com.board.User;

import com.board.Question.Dto.QuestionsBasicDto;
import com.board.Question.QuestionsService;
import com.board.Reply.ReplysService;
import com.board.Reply.dto.ReplysBasicDto;
import com.board.User.dto.MailDto;
import jakarta.mail.MessagingException;
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
            return "account/signup_form";
        }
        if (!usersCreateForm.getPassword1().equals(usersCreateForm.getPassword2())) { //비밀번호 검증이 실패했을 경우
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "account/signup_form";
        }

        try {
        usersService.create(usersCreateForm.getUsername(),usersCreateForm.getPassword1(), usersCreateForm.getEmail());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "account/signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "account/signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "account/login_form";
    }

    @GetMapping("/chginfo")
    @PreAuthorize("isAuthenticated()")
    public String changePw(){
        return "account/change_pwd";
    }

    @PostMapping("/chginfo")
    @PreAuthorize("isAuthenticated()")
    public String changePw(@RequestParam("inputpw") String inputpw,@RequestParam("checkpw") String checkpw,Principal principal,Model model){
        UsersDetail usersDetail = this.usersService.getUsersDetail(principal.getName());
        if (inputpw.equals(checkpw)) {
            this.usersService.updatePw(usersDetail,checkpw);
            return "redirect:/";
        }
        else{
            model.addAttribute("error",true);
            return "account/change_pwd";
        }
    }

    @GetMapping("/chkinfo")
    @PreAuthorize("isAuthenticated()")
    public String checkPw(){
        return "account/check_pwd";
    }

    @PostMapping("/chkinfo")
    @PreAuthorize("isAuthenticated()")
    public String checkPw(@RequestParam("password") String password, Principal principal, Model model){
        UsersDetail usersDetail = this.usersService.getUsersDetail(principal.getName());
        if(passwordEncoder.matches(password, usersDetail.getPassword())) {
            return changePw();
        }
        else{
            model.addAttribute("error", true);
            return "account/check_pwd";
        }
    }

    @GetMapping("/findAccount")
    public String findAccount(){
        return "account/find_account";
    }

    @GetMapping("/chkEmail")
    public String checkEmail(){
        return "account/send_Email";
    }

    @GetMapping("/findId")
    public String findId(){
        return "account/find_id";
    }

    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestParam("inputEmail") String email) {
        UsersDetail usersDetail = usersService.getUserByEmail(email);
        if (usersDetail != null) {
            Users users = usersDetail.getUsers();
            return ResponseEntity.ok(users.getUsername());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/showQNA")
    public String showQNA(Principal principal,Model model,@RequestParam(value="page", defaultValue="0") int page,@RequestParam(value="type", defaultValue="question") String type){
        Users users = this.usersService.getUsers(principal.getName());
        Page<QuestionsBasicDto> Qpaging = this.questionsService.getMyWriteList(page,users.getId());
        Page<ReplysBasicDto> Rpaging = this.replysService.getMyReplysList(page,users.getId());
        model.addAttribute("type", type);
        if(type.equals("question")) {
            model.addAttribute("Qpaging", Qpaging);
        }
        else if (type.equals("replys")) {
            model.addAttribute("Rpaging",Rpaging);
        }
        return "account/show_info";
    }

    @GetMapping("/findPwd")
    public String findPassword(){
        return "account/find_password";
    }

    @PostMapping("/findPwd")
    public ResponseEntity<?> findPassword(@RequestParam(value="inputEmail")String email){
        UsersDetail usersDetail = this.usersService.getUserByEmail(email);
        if(usersDetail == null){
            return ResponseEntity.notFound().build();
        }
        else{
            Users users = usersDetail.getUsers();
            String tempPw = this.usersService.makeTempPw();
            this.usersService.updatePw(usersDetail,tempPw);
            MailDto mailDto = new MailDto();
            mailDto.setEmail(email);
            mailDto.setTitle("임시 비밀번호 안내 이메일입니다.");
            mailDto.setMessage("안녕하세요. 임시비밀번호 안내 관련 메일 입니다." + "[" + users.getUsername() + "]" + "님의 임시 비밀번호는 " + tempPw + " 입니다.");
            try {
                this.usersService.sendMail(mailDto);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok(email);
        }
    }
}
