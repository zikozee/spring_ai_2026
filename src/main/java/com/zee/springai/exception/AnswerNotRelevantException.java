package com.zee.springai.exception;


/**
 * @dev : Ezekiel Eromosei
 * @date : 08 Apr, 2026
 */

public class AnswerNotRelevantException extends RuntimeException {
    public AnswerNotRelevantException(String question, String answerText) {
        super("Answer is not relevant to the question. Question: " + question + ", Answer: " + answerText);
    }
}
