package com.zee.springai.service;


import com.zee.springai.dto.AnswerWithTitle;
import com.zee.springai.dto.QuestionWithTitle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@Slf4j
@Service
public class SpringAiBoardGameWithTitleService implements BoardGameWithTitleService {

    private final ChatModel chatModel;
    private final Resource questionPromptTemplate;
    private final GameRulesService gameRulesService;

    //@Qualifier("openAiChatModel")
    public SpringAiBoardGameWithTitleService(@Qualifier("ollamaChatModel") ChatModel chatModel,
                                             @Value("classpath:/promptTemplates/questionPromptTemplate.st") Resource questionPromptTemplate,
                                             GameRulesService gameRulesService) {
        this.chatModel = chatModel;
        this.questionPromptTemplate = questionPromptTemplate;
        this.gameRulesService = gameRulesService;
    }

    @Override
    public AnswerWithTitle askQuestion(QuestionWithTitle questionWithTitle) {

        String gameRules = gameRulesService.getRulesFor(questionWithTitle.gameTitle());

        ChatClient chatClient = ChatClient.create(chatModel);
        String answerText = chatClient.prompt()
                .user(userSpec -> userSpec
                        .text(questionPromptTemplate)
                        .param("gameTitle", questionWithTitle.gameTitle())
                        .param("question", questionWithTitle.question())
                        .param("rules", gameRules))
                .call()
                .content();

        return new AnswerWithTitle(questionWithTitle.gameTitle(), answerText);
    }
}
