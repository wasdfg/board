package com.board.reply;

import com.board.question.Questions;
import com.board.question.QuestionsService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.format.DateTimeFormatter;

@RequestMapping("/replys")
@RequiredArgsConstructor
@Controller
public class ReplysController {
    private final QuestionsService questionsService;
    private final ReplysService replysService;

    @PostMapping("/create/{uploadnumber}") //post로 처리 대용량처리에 용이
    public String createReply(Model model, @PathVariable("uploadnumber") Integer uploadnumber, @RequestParam(value="content") String content){
        Questions questions = this.questionsService.getQuestions(uploadnumber);
        this.replysService.create(questions,content);
        return String.format("redirect:/questions/detail/%s",uploadnumber);
    }
}

