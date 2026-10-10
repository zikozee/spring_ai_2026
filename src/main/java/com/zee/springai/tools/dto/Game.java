package com.zee.springai.tools.dto;


import com.zee.springai.tools.datatype.GameComplexity;
import org.springframework.data.annotation.Id;

/**
 * @dev : Ezekiel Eromosei
 * @date : 10 Oct, 2026
 */

public record Game(
        @Id Long id,
        String slug,
        String title,
        float complexity
) {
    public GameComplexity complexityEnum() {
        int rounded = Math.round(complexity);
        return GameComplexity.values()[rounded];
    }
}
