package com.board.question;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionsApiController {

    private final QuestionsService questionsService;

    @Autowired
    public QuestionsApiController(QuestionsService questionsService) {
        this.questionsService = questionsService;
    }

    @GetMapping("/api/questions")
    public ResponseEntity<Page<Questions>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @RequestParam(value = "category", defaultValue = "") String category) {
        Page<Questions> paging = this.questionsService.getList(page, kw, category);
        return ResponseEntity.ok(paging);
    }
}
