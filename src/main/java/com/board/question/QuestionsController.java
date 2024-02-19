package com.board.question;

import com.board.reply.ReplysForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequestMapping("/questions") // /questions로 시작하는 url의 앞부분을 prefix해준다.
@RequiredArgsConstructor //final을 선언할때 사용
@Controller
public class QuestionsController {

    private final QuestionsService questionsService; //service라는 dto를 생성해서 가져온다
    @GetMapping("/list") //   localhost:8080/가 기본 위치이다.
    public String list(Model model, @PageableDefault(size = 10, sort = "uploadnumber", direction = Sort.Direction.DESC) Pageable pageable){//매개변수를 model로 지정하면 객체가 자동으로 생성된다.
        Page<Questions> paging = this.questionsService.getList(pageable);
        System.out.println(paging.getNumber());
        ArrayList pageIndex = new ArrayList();
        for(int i = (paging.getNumber() / 10 * 10 +1);i <= (paging.getNumber() / 10 * 10 +10);i++){
            pageIndex.add(i);
        }

        model.addAttribute("paging", paging);
        model.addAttribute("num", pageIndex);
        model.addAttribute("hasPrev", paging.hasPrevious()); //이전 페이지가 있는지 확인
        model.addAttribute("previous", pageable.previousOrFirst().getPageNumber()-9); //이전 페이지를 가져옴
        model.addAttribute("current", pageable.getPageNumber()+1); //현재 페이지
        model.addAttribute("notcur", pageable.getPageNumber()); //페이지는 0부터 시작하므로 버튼은 1부터 출력하게
        model.addAttribute("hasNext", paging.hasNext()); //다음 페이지가 있는지 확인
        model.addAttribute("next", pageable.next().getPageNumber()+9); //다음 페이지를 가져옴

        return "questions_list";
    }

    @GetMapping(value = "/detail/{uploadnumber}")
    public String detail(Model model, @PathVariable("uploadnumber") Integer uploadnumber, ReplysForm replysForm){
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
    public String questionsCreate(@Valid QuestionsForm questionsForm, BindingResult bindingResult,Model model){//질문 폼에 조건 추가
        if(bindingResult.hasErrors()){ //에러가 있는지 검사
            model.addAttribute("errors",bindingResult.getAllErrors()); //mustache는 에러를 직접 검사하는 기능이 없기에 모델로 추가
            return "questions_form";
        }
        this.questionsService.create(questionsForm.getTitle(),questionsForm.getContent());
        return "redirect:/questions/list";
    }
}
