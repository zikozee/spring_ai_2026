package com.zee.springai;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;
import com.zee.springai.service.SpringAiBoardGameService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @dev : Ezekiel Eromosei
 * @date : 08 Apr, 2026
 */

@SpringBootTest
class SpringAiBoardGameServiceTests {

    @Autowired
    private SpringAiBoardGameService springAiBoardGameService;

    @Autowired
    private OpenAiChatModel chatModel;

    private RelevancyEvaluator relevancyEvaluator;

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    void setUp() {
        ChatClient.Builder chantClientBuilder = ChatClient.builder(chatModel);
        this.relevancyEvaluator = new RelevancyEvaluator(chantClientBuilder);
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(chantClientBuilder).build();
    }

    @Test
    void evaluateRelevancy() {
        String userText = "Why is the sky blue?";
        Question question = new Question(userText);
        Answer answer = springAiBoardGameService.askQuestion(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, answer.answer());
        EvaluationResponse response = relevancyEvaluator.evaluate(evaluationRequest);

        Assertions.assertThat(response.isPass())
                .withFailMessage("""
                        ========================================
                        The answer "%s"
                        is not considered relevant to the question
                        "%s".
                        ========================================
                        """, answer.answer(), userText)
                .isTrue();

    }

    @Test
    void evaluateFactualAccuracy() {
        String userText = "Why is the sky blue?";
        Question question =  new Question(userText);
        Answer answer = springAiBoardGameService.askQuestion(question);

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, answer.answer());
        EvaluationResponse evaluationResponse = factCheckingEvaluator.evaluate(evaluationRequest);

        Assertions.assertThat(evaluationResponse.isPass())
                .withFailMessage("""
                        ========================================
                        The answer "%s"
                        is not considered factually accurate in response to the question
                        "%s".
                        ========================================
                        """, answer.answer(), userText)
                .isTrue();


    }
}
