package com.zee.springai.tools.datatype;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @dev : Ezekiel Eromosei
 * @date : 10 Oct, 2026
 */

@Getter
@AllArgsConstructor
public enum GameComplexity {
    UNKNOWN(0),
    EASY(1),
    MODERATELY_EASY(2),
    MODERATE(3),
    MODERATELY_DIFFICULT(4),
    DIFFICULT(5);

    private final int value;

}
