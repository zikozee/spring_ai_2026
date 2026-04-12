package com.zee.springai.controller;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.AnswerWithTitle;
import com.zee.springai.dto.Question;
import com.zee.springai.dto.QuestionWithTitle;
import com.zee.springai.service.SelfEvaluationgBoardGameService;
import com.zee.springai.service.SpringAiBoardGameService;
import com.zee.springai.service.SpringAiBoardGameWithTitleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@RestController
@RequiredArgsConstructor
public class AskController {

    private final SpringAiBoardGameService boardGameService;
    private final SelfEvaluationgBoardGameService selfEvaluationgBoardGameService;
    private final SpringAiBoardGameWithTitleService boardGameWithTitleService;

    @PostMapping(path = "ask", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer ask(@RequestBody Question question){
        return boardGameService.askQuestion(question);
    }

    @PostMapping(path = "ask-with-retry", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer askWithRetry(@RequestBody Question question){
        return selfEvaluationgBoardGameService.askQuestion(question);
    }

    @PostMapping(path = "ask-with-game-title", produces = MediaType.APPLICATION_JSON_VALUE)
    public AnswerWithTitle askWithGameTitle(@RequestBody @Valid QuestionWithTitle questionWithTitle){
        return boardGameWithTitleService.askQuestion(questionWithTitle);
    }
}
