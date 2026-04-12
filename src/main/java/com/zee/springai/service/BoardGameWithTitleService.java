package com.zee.springai.service;


import com.zee.springai.dto.AnswerWithTitle;
import com.zee.springai.dto.QuestionWithTitle;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

public interface BoardGameWithTitleService {

    AnswerWithTitle askQuestion(QuestionWithTitle question);
}
