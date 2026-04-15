package com.zee.springai.controller;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @dev : Ezekiel Eromosei
 * @date : 15 Apr, 2026
 */
//todo how to return a list
@RestController
public class TopSongsController {

    @Value("classpath:/promptTemplates/top-songs-prompt.st")
    private Resource topSongsPromptTemplate;

    private final ChatModel chatModel;

    public TopSongsController(@Qualifier("ollamaChatModel") ChatModel chatModel){
        this.chatModel = chatModel;
    }

    @GetMapping(path = "top-songs", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> topSongs(@RequestParam("year") String year) {
        return ChatClient.create(chatModel)
                .prompt()
                .user( userSpec -> userSpec
                        .text(topSongsPromptTemplate)
                        .param("year", year))
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }
}
