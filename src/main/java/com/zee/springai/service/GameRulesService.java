package com.zee.springai.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @dev : Ezekiel Eromosei
 * @date : 12 Apr, 2026
 */

@Slf4j
@Service
public class GameRulesService {

    public String getRulesFor(String gameName){
        try{
            String filename = String.format("classpath:/gameRules/%s.txt", gameName.toLowerCase().replace(" ", "_"));

            return new DefaultResourceLoader()
                    .getResource(filename)
                    .getContentAsString(Charset.defaultCharset());
        }catch (IOException ex) {
            log.info("No rules found for game: {}",  gameName);
            return "";
        }
    }
}
