package com.zee.springai.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public record AnswerWithTitle(String gameTitle, String answer) {}
