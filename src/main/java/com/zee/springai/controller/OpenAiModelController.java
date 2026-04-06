package com.zee.springai.controller;


import com.zee.springai.dto.ModelResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */
@RestController
public class OpenAiModelController {

    @Value("${spring.ai.openai.api-key}")
    private String key;


    @GetMapping("/model-info")
    public ResponseEntity<ModelResponse> getModelInfo() {
        // Logic to retrieve model information based on the modelId
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "bearer " + key);
        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<ModelResponse> exchange = new RestTemplate().exchange("https://api.openai.com/v1/models", HttpMethod.GET, entity, ModelResponse.class);
        return ResponseEntity.ok(exchange.getBody());
    }
}
