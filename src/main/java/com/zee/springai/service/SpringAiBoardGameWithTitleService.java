package com.zee.springai.service;


import com.zee.springai.dto.AnswerWithTitle;
import com.zee.springai.dto.QuestionWithTitle;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@Slf4j
@Service
public class SpringAiBoardGameWithTitleService implements BoardGameWithTitleService {

    private final ChatClient chatClient;
    private final Resource promptTemplate;
    private final GameRulesService gameRulesService;

    //@Qualifier("openAiChatModel")
    public SpringAiBoardGameWithTitleService(@Qualifier("aiChatClient") ChatClient chatClient,
                                             @Value("classpath:/promptTemplates/systemPromptTemplate.st") Resource systemPromptTemplate,
                                             GameRulesService gameRulesService) {
        this.chatClient = chatClient;
        this.promptTemplate = systemPromptTemplate;
        this.gameRulesService = gameRulesService;
    }

    @Override
    public AnswerWithTitle askQuestion(QuestionWithTitle questionWithTitle) {

        String gameRules = gameRulesService.getRulesFor(questionWithTitle.gameTitle(), questionWithTitle.question());

//        String answerText = chatClient.prompt()
        ResponseEntity<ChatResponse, AnswerWithTitle> responseEntity = chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(promptTemplate)
                        .param("gameTitle", questionWithTitle.gameTitle())
                        .param("rules", gameRules))

                .user(questionWithTitle.question())

                // i can override props here
//                .options(ChatOptions.builder()
//                        .temperature(0.8)
//                        .topP(0.9)
//                        .topK(6)
//                        .build())

                .call()
                .responseEntity(AnswerWithTitle.class);
//                .content();

        ChatResponse response = responseEntity.getResponse();

        ChatResponseMetadata metadata = response.getMetadata();
        logUsage(metadata.getUsage());

        return responseEntity.entity();
//        return new AnswerWithTitle(questionWithTitle.gameTitle(), answerText);
    }

    public Flux<String> askQuestionStream(@Valid QuestionWithTitle questionWithTitle) {

        String gameRules = gameRulesService.getRulesFor(questionWithTitle.gameTitle(), questionWithTitle.question());

        return chatClient
                .prompt()
                .system(systemSpec -> systemSpec
                        .text(promptTemplate)
                        .param("gameTitle", questionWithTitle.gameTitle())
                        .param("rules", gameRules))
                .user(questionWithTitle.question())
                .stream()
                .content();
    }

    private void logUsage(Usage usage) {
        log.info("Token usage: prompt={}, generation={}, total={}", usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens());
    }
}
