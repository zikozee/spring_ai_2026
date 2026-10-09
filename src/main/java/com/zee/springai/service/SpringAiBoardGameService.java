package com.zee.springai.service;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@Slf4j
@Service
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatClient chatClient;

    //@Qualifier("openAiChatModel")
    public SpringAiBoardGameService(@Qualifier("aiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Answer askQuestion(Question question) {
//        ChatClient chatClient  = ChatClient.create(openAiChatModel);

        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();

        return new Answer(answerText);
    }
}
