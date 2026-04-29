package com.zee.springai.service;


import com.zee.springai.util.AiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @dev : Ezekiel Eromosei
 * @date : 12 Apr, 2026
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRulesService {

    private final VectorStore vectorStore;

    public String getRulesFor(String gameTitle, String question){
        SearchRequest searchRequest = SearchRequest
                .builder()
                .query(question)
                .topK(6) // default is
                .similarityThreshold(0.5) // range 0.0 - 1.0
                .filterExpression(new FilterExpressionBuilder()
                        .eq("gameTitle", AiUtil.normalizeGameTitle(gameTitle)).build()
                )
                .build();

        log.info("Search request: {}",  searchRequest);

        List<Document> similarDocs = vectorStore.similaritySearch(searchRequest);

        if(similarDocs.isEmpty()){
            return "The rules for " + gameTitle + " are not available";
        }

        return similarDocs.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
    }


//    public String getRulesFor(String gameName){
//        try{
//            String filename = String.format("classpath:/gameRules/%s.txt", gameName.toLowerCase().replace(" ", "_"));
//
//            return new DefaultResourceLoader()
//                    .getResource(filename)
//                    .getContentAsString(Charset.defaultCharset());
//        }catch (IOException ex) {
//            log.info("No rules found for game: {}",  gameName);
//            return "";
//        }
//    }
}
