package com.zee.springai.controller;


import com.zee.springai.dto.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

/**
 * @dev : Ezekiel Eromosei
 * @date : 11 Oct, 2026
 */

@RestController
@RequestMapping(path ="mcp")
public class McpAskController {

    private final ChatClient chatClient;

    public McpAskController(@Qualifier("aiChatClient") ChatClient chatClient, ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClient
                .mutate()
                .defaultTools(toolCallbackProvider) // enable chatcCLient to use the tools that the server(s) make available
                .build();
    }

    @PostMapping(path = "ask")
    public Mono<Answer> ask(@RequestBody Question question) {
        return Mono.fromCallable(() ->
                chatClient.prompt()
                .system("""
                                Use the board game tools to answer the question.
                                Return matching games in the games field.
                                Preserve each game's title, and description.
                                """)
                .user(question.question())
                .call()
                .entity(Answer.class))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public record BoardGame(String title, String description){}
    public record Answer(List<BoardGame> games) {}
}
