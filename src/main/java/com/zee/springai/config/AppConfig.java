package com.zee.springai.config;


import io.qdrant.client.QdrantClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

/**
 * @dev : Ezekiel Eromosei
 * @date : 12 Apr, 2026
 */

@Configuration
public class AppConfig {

    //to see raw request and response
    // since Spring AI's ChatClient uses Spring RestClient under the covers
    @Bean
    RestClientCustomizer logbookCustomizer(LogbookClientHttpRequestInterceptor logbookInterceptor) {
        return restclient -> restclient.requestInterceptor(logbookInterceptor);
    }


    @Primary
    @Bean
    QdrantVectorStore customVectorStore(QdrantClient client, @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel){

        return QdrantVectorStore
                .builder(client, embeddingModel)
                .initializeSchema(true)
                .collectionName("GameRules")
                .build();
    }

    @Bean
    ChatMemory customChatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(50)  //
                .build();
    }
}
