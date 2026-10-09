package com.zee.springai.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @dev : Ezekiel Eromosei
 * @date : 09 Oct, 2026
 */

@Slf4j
@Configuration
public class AIProviderConfig {


    @Bean("aiEmbeddingModel")
    @ConditionalOnProperty(prefix = "ai", name = "provider", havingValue = "openai", matchIfMissing = true)
    EmbeddingModel openAIEmbeddingModel(@Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel){
        log.info("using 'OPENAI' embedding model");
        return embeddingModel;
    }


    @Bean("aiEmbeddingModel")
    @ConditionalOnProperty(prefix = "ai", name = "provider", havingValue = "ollama", matchIfMissing = true)
    EmbeddingModel ollamEmbeddingModel(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel){
        log.info("using 'OLLAMA' embedding model");
        return embeddingModel;
    }


    @Bean("aiChatModel")
    @ConditionalOnProperty(prefix = "ai", name = "provider", havingValue = "openai", matchIfMissing = true)
    ChatModel openAIChatModel(@Qualifier("openAiChatModel") ChatModel chatModel){
        log.info("using 'OPENAI' chat model");
        return chatModel;
    }

    @Bean("aiChatModel")
    @ConditionalOnProperty(prefix = "ai", name = "provider", havingValue = "ollama", matchIfMissing = true)
    ChatModel ollamaChatModel(@Qualifier("ollamaChatModel") ChatModel chatModel){
        log.info("using 'OLLAMA' chat model");
        return chatModel;
    }

    @Bean("aiChatClient")
    @ConditionalOnProperty(prefix = "ai", name = "provider", havingValue = "openai", matchIfMissing = true)
    ChatClient openAIChatClient(OpenAiChatModel openAiChatModel, SimpleLoggerAdvisor loggerAdvisor) {
        log.info("using 'OPENAI' chat client");
        return ChatClient.builder(openAiChatModel).defaultAdvisors(loggerAdvisor).build();
    }

    @Bean("aiChatClient")
    @ConditionalOnProperty(prefix = "ai", name = "provider", havingValue = "ollama", matchIfMissing = true)
    ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel, SimpleLoggerAdvisor loggerAdvisor) {
        log.info("using 'OLLAMA' chat client");
        return ChatClient.builder(ollamaChatModel).defaultAdvisors(loggerAdvisor).build();
    }

    @Bean
    SimpleLoggerAdvisor loggerAdvisor(){
        return new SimpleLoggerAdvisor();
    }
}
