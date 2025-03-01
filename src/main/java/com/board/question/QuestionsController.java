package com.board.question;

import com.board.question.dto.QuestionsListDto;
import com.board.reply.Replys;
import com.board.reply.ReplysForm;
import com.board.user.Users;
import com.board.user.UsersService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequestMapping("/questions") // /questions로 시작하는 url의 앞부분을 prefix해준다.
@RequiredArgsConstructor //final을 선언할때 사용
@Controller
public class QuestionsController { //controller에서 요청을 받아와서
    private final QuestionsService questionsService; //service라는 dto를 생성해서 가져온다

    private final UsersService usersService;

    @GetMapping("/list") //   localhost:8080/가 기본 위치이다.
    public String list(Model model,@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "category", defaultValue = "all") String category,
                       @RequestParam(name = "selectIndex", required = false) String selectIndex,HttpSession session, HttpServletRequest request,Principal principal){//매개변수를 model로 지정하면 객체가 자동으로 생성된다.
        List<Category> searchIndex = Arrays.asList(
                new Category("all","전체"),
                new Category("titleContent","제목+내용"),
                new Category("title","제목"),
                new Category("content","내용"),
                new Category("username","글쓴이"),
                new Category("replys","댓글")
        );
        Page<?> paging; //받는 타입을 동적으로 사용하기 위해서 선언
        long startTime = System.currentTimeMillis();
        if(kw.trim().isEmpty()) { //공백이나 null값이면 없는걸로 처리
            paging = this.questionsService.searchCheck(page, category);
        }
        else{
            paging = this.questionsService.searchByKeyword(page, kw, selectIndex, category);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("searchCheck 실행 시간: " + (endTime - startTime) + "ms");
        int all_content_count = (int)paging.getTotalElements();
        int current_Page = paging.getNumber(); //현제 페이지
        int current_Page_Group_Start = (current_Page / 10) * 10 + 1; //현재페이지의 시작번호 10개기준이므로 mod10 = 1인거
        int current_Page_Group_End = Math.min(current_Page_Group_Start + 9, all_content_count); //뒷자리가 10의배수인거 또는 마지막번호 중 작은거


        Set<Integer> readQuestions = new HashSet<>(); //세션이나 쿠키를 가져와서 저장할 공간
        principal = request.getUserPrincipal();
        if(principal != null) {// 로그인 상태일 때 세션을 가져온다
            readQuestions = (Set<Integer>) session.getAttribute("readQuestions");
            if (readQuestions == null) {
                readQuestions = new HashSet<>();
            }
        }
        else {// 비로그인 상태라면 쿠키를 가져온다
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().startsWith("readQuestions_")) {
                        try {
                            Integer id = Integer.parseInt(cookie.getName().split("_")[1]);
                            readQuestions.add(id);
                        } catch (NumberFormatException e) {
                            // 쿠키 이름이 올바르지 않으면 무시
                        }
                    }
                }
            }
        }
        model.addAttribute("readQuestions", readQuestions); //읽은 글을 확인
        model.addAttribute("searchIndex",searchIndex);
        model.addAttribute("paging",paging);
        model.addAttribute("all_content_count",all_content_count);
        model.addAttribute("currentPageGroupStart", current_Page_Group_Start);
        model.addAttribute("currentPageGroupEnd", current_Page_Group_End);

        return "questions_list";
    }

    @GetMapping(value = "/detail/{uploadnumber}")
    public String detail(Model model, @PathVariable("uploadnumber") Integer uploadnumber, ReplysForm replysForm, HttpSession session, Principal principal, HttpServletRequest request, HttpServletResponse response){
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        List<Replys> sortDate = this.questionsService.getSortByDate(uploadnumber);
        model.addAttribute("sortDate",sortDate);
        model.addAttribute("questions",questions);
        if(principal != null){ //로그인 된 상태라면 세션으로 사용
            String username = principal.getName();

            // 세션에서 읽은 댓글 목록 가져오기
            Set<Integer> readQuestions = (Set<Integer>) session.getAttribute("readQuestions");
            if (readQuestions == null) {
                readQuestions = new HashSet<>();
            }

            if (!readQuestions.contains(uploadnumber)) {
                // 세션에 읽은 댓글 ID 추가
                readQuestions.add(uploadnumber);
                session.setAttribute("readQuestions", readQuestions);
            }
        }
        else{ //비로그인 상태라면 쿠키를 사용
            Cookie[] cookies = request.getCookies();
            Cookie readQuestionsCookie = null;

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("readQuestions")) {
                        readQuestionsCookie = cookie;
                        break;
                    }
                }
            }
            if (readQuestionsCookie == null) {
                readQuestionsCookie = new Cookie("readQuestions", String.valueOf(uploadnumber));
            }
            else {
                String value = readQuestionsCookie.getValue();
                if (!value.contains(String.valueOf(uploadnumber))) {
                    readQuestionsCookie.setValue(value + "," + uploadnumber);
                }
            }
                // 쿠키에 읽은 댓글 ID 추가
            readQuestionsCookie.setPath("/"); // 쿠키가 모든 경로에서 유효하게 설정
            readQuestionsCookie.setMaxAge(30 * 24 * 60 * 60); // 30일
            response.addCookie(readQuestionsCookie);
        }
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
    public String questionsCreate(@Valid QuestionsForm questionsForm, BindingResult bindingResult, Principal principal){//질문 폼에 조건 추가
        if(bindingResult.hasErrors()){ //에러가 있는지 검사
            return "questions_form";
        }
        Users users = this.usersService.getUsers(principal.getName());
        this.questionsService.create(questionsForm.getTitle(),questionsForm.getContent(),users,questionsForm.getCategory());
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
    public String questionsModify(@Valid QuestionsForm questionsForm, BindingResult bindingResult, @PathVariable("uploadnumber") Integer uploadnumber, Principal principal) { //글 수정
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
        Users users = this.usersService.getUsers(principal.getName()); // 현재 로그인한 유저의 정보를 담는다

        boolean alreadyVoted = questions.getVoter().contains(users); // 이미 투표를 했는지 확인
        if (!alreadyVoted) {
            this.questionsService.vote(questions, users);
        }

        model.addAttribute("voted", alreadyVoted);
        return "redirect:/questions/detail/" + uploadnumber + "?voted=" + alreadyVoted;
    }
}