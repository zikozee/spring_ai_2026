package com.zee.springai.util;


/**
 * @dev : Ezekiel Eromosei
 * @date : 29 Apr, 2026
 */

public final class AiUtil {
    private AiUtil(){}

    public static String normalizeGameTitle(String gameTitle){
        return gameTitle.toLowerCase().replace(" ", "_");
    }

}
