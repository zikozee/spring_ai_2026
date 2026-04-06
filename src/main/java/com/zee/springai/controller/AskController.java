package com.zee.springai.controller;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;
import com.zee.springai.service.BoardGameService;
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

    private final BoardGameService boardGameService;

    @PostMapping(path = "ask", produces = MediaType.APPLICATION_JSON_VALUE)
    public Answer ask(@RequestBody Question question){
        return boardGameService.askQuestion(question);
    }
}
