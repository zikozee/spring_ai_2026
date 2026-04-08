package com.zee.springai;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;
import com.zee.springai.service.SpringAiBoardGameService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

//The @EnableWireMock annotation at the class level not only enables a WireMock test
// but also establishes a property named openai.base.url that will hold the value of the mock API.
// This property is then used in the @SpringBootTest annotation to set spring.ai.openai.base-url,
// the Spring AI configuration property that overrides the default OpenAI base URL.

@EnableWireMock(
        @ConfigureWireMock(baseUrlProperties = "openai.base.url")
)
@SpringBootTest(properties = "spring.ai.openai.base-url=${openai.base.url}")
class SpringAiBoardGameServiceWireMockTest {

    @Value("classpath:/test-openai-response.json")
    Resource responseResource;

    @Autowired
    SpringAiBoardGameService springAiBoardGameService;

    @BeforeEach
    void setUp() throws IOException {
        String cannedResponse = responseResource.getContentAsString(Charset.defaultCharset());
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(cannedResponse, Map.class);
        WireMock.stubFor(WireMock.post("/v1/chat/completions")
                .willReturn( ResponseDefinitionBuilder.okForJson(map)));

//        WireMock.stubFor(WireMock.post("/v1/chat/completions")
//                .willReturn(WireMock.aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(cannedResponse)));
    }

    @Test
    void testAskQuestion() {
        Answer answer = springAiBoardGameService.askQuestion(new Question("What is the capital of France?"));

        Assertions.assertThat(answer).isNotNull();
        Assertions.assertThat(answer.answer()).contains("Paris");
    }
}