package com.zee.springai.service;


import com.zee.springai.dto.AnswerWithTitle;
import com.zee.springai.dto.QuestionWithTitle;
import com.zee.springai.util.AiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@Slf4j
@Service
public class SpringAiBoardGameModularRagAdvisor {

    private final ChatModel chatModel;
    private final Resource promptTemplate;
    private final VectorStore vectorStore;

    //@Qualifier("openAiChatModel")
    public SpringAiBoardGameModularRagAdvisor(@Qualifier("openAiChatModel") ChatModel chatModel,
                                              @Value("classpath:/promptTemplates/ragAdvisorPromptTemplate.st") Resource promptTemplate,
                                              VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.promptTemplate = promptTemplate;
        this.vectorStore = vectorStore;
    }

// here we use vectorstore to store extra context via RAG
    public AnswerWithTitle askQuestion(QuestionWithTitle questionWithTitle, String conversationId) {

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
                                        RetrievalAugmentationAdvisor.builder()
                                                .documentRetriever(
                                                        VectorStoreDocumentRetriever.builder()
                                                                .vectorStore(vectorStore)
                                                                .build()
                                                )
                                                .queryTransformers(
                                                        TranslationQueryTransformer.builder()
                                                                .chatClientBuilder(ChatClient.builder(chatModel))
                                                                .targetLanguage("English")
                                                                .build()/*,
                                                        RewriteQueryTransformer.builder()
                                                                .chatClientBuilder(ChatClient.builder(chatModel))
                                                                .build()*/

                                                )
                                                .queryExpander( // info use in contrast to **RewriteQueryTransformer** as this gives you new queries plus the original query

                                                        MultiQueryExpander.builder()
                                                                .chatClientBuilder(ChatClient.builder(chatModel))
                                                                .numberOfQueries(5)// default 3, plus original 4
                                                                .includeOriginal(false) // if you intend to remove original, hence max == numberOfQueries
                                                                .build()
                                                )
                                                .build()
                                )
                                .param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, gameNameMatch)
                                .param(CONVERSATION_ID, conversationId)
                        )
                .call()
                .entity(AnswerWithTitle.class);
    }

}
