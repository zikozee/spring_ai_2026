package com.zee.springai.service;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@Slf4j
@Service
public class SpringAiBoardGameService implements BoardGameService {

    private final ChatModel chatModel;

    //@Qualifier("openAiChatModel")
    public SpringAiBoardGameService(@Qualifier("aiChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Answer askQuestion(Question question) {
//        ChatClient chatClient  = ChatClient.create(openAiChatModel);

        ChatClient chatClient = ChatClient.create(chatModel);
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();

        return new Answer(answerText);
    }
}
