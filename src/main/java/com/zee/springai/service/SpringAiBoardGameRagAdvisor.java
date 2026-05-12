package com.zee.springai.service;


import com.zee.springai.dto.AnswerWithTitle;
import com.zee.springai.dto.QuestionWithTitle;
import com.zee.springai.util.AiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
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
public class SpringAiBoardGameRagAdvisor {

    private final ChatModel chatModel;
    private final Resource promptTemplate;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    //@Qualifier("openAiChatModel")
    public SpringAiBoardGameRagAdvisor(@Qualifier("openAiChatModel") ChatModel chatModel,
                                       @Value("classpath:/promptTemplates/ragAdvisorPromptTemplate.st") Resource promptTemplate,
                                       VectorStore vectorStore, @Qualifier("customChatMemory") ChatMemory chatMemory) {
        this.chatModel = chatModel;
        this.promptTemplate = promptTemplate;
        this.vectorStore = vectorStore;
        this.chatMemory = chatMemory;
    }


    public AnswerWithTitle askQuestion(QuestionWithTitle questionWithTitle) {

        String gameNameMatch = String.format("gameTitle == '%s'", AiUtil.normalizeGameTitle(questionWithTitle.gameTitle()));

        ChatClient chatClient = ChatClient.create(chatModel);
        return chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(promptTemplate)
                        .param("gameTitle", questionWithTitle.gameTitle()))
                .user(questionWithTitle.question())
                .advisors(advisorSpec ->
                        advisorSpec
                                .advisors(
//                                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                                        PromptChatMemoryAdvisor.builder(chatMemory).build(),
                                        QuestionAnswerAdvisor.builder(vectorStore).build()
                                )
                                .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, gameNameMatch)
                        )
                .call()
                .entity(AnswerWithTitle.class);
    }

}
