package com.zee.springai.service;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@Service
public class SpringAiBoardGameService implements BoardGameService {
    private final OpenAiChatModel openAiChatModel;
    private final OllamaChatModel ollamaChatModel;

    public SpringAiBoardGameService(OpenAiChatModel openAiChatModel, OllamaChatModel ollamaChatModel){
        this.openAiChatModel = openAiChatModel;
        this.ollamaChatModel = ollamaChatModel;
    }


    @Override
    public Answer askQuestion(Question question) {
//        ChatClient chatClient  = ChatClient.create(openAiChatModel);

        ChatClient chatClient = ChatClient.create(ollamaChatModel);
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();

        return new Answer(answerText);
    }
}
