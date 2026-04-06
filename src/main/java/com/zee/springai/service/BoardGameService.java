package com.zee.springai.service;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

public interface BoardGameService {

    Answer askQuestion(Question question);
}
