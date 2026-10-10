package com.zee.springai.tools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @dev : Ezekiel Eromosei
 * @date : 09 Oct, 2026
 */

@Slf4j
@Component
public class TimeTools {

    @Tool(name = "getCurrentTime", description = "Get the current time in the specified time zone.")
    public String getCurrentTime(String timeZone){
        log.info("Getting the current time in {}", timeZone);

        LocalDateTime now = LocalDateTime.now(ZoneId.of(timeZone));
        return now.toString();
    }
}
