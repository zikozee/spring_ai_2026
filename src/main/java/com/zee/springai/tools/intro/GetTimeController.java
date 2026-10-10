package com.zee.springai.tools.intro;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @dev : Ezekiel Eromosei
 * @date : 09 Oct, 2026
 */

@RestController
public class GetTimeController {
    public static final String CURRENT_TIME_TEMPLATE =
            "What is the current time in {city}";

    private final ChatClient chatClient;

    public GetTimeController(@Qualifier("aiChatClient") ChatClient chatClient, TimeTools timeTools) {
        this.chatClient = chatClient
                .mutate()
                .defaultTools(timeTools)
                .build();
    }

    @GetMapping(path = "time", params = {"city"})
    public String getTime(@RequestParam("city") String city) {
        return chatClient
                .prompt()
                .user(userSpec -> {
                    userSpec.text(CURRENT_TIME_TEMPLATE)
                            .param("city", city);
                })
                .call()
                .content();
    }
}
