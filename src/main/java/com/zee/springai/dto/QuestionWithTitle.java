package com.zee.springai.dto;


import jakarta.validation.constraints.NotBlank;

/**
 * @dev : Ezekiel Eromosei
 * @date : 11 Apr, 2026
 */

public record QuestionWithTitle(
        @NotBlank(message = "Game title is required") String gameTitle,
        @NotBlank(message = "Question is required") String question
) {
}
