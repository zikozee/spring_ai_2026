package com.zee.springai.service;


import com.zee.springai.dto.Answer;
import com.zee.springai.dto.Question;
import com.zee.springai.exception.AnswerNotRelevantException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Service;

/**
 * @dev : Ezekiel Eromosei
 * @date : 05 Apr, 2026
 */

@Slf4j
@Service
public class SelfEvaluationgBoardGameService implements BoardGameService {

    private final ChatModel chatModel;
    private final RelevancyEvaluator relevancyEvaluator;

    //@Qualifier("openAiChatModel")
    public SelfEvaluationgBoardGameService(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
        this.relevancyEvaluator = new RelevancyEvaluator(ChatClient.builder(chatModel));
    }

    @Override
    @Retryable(retryFor = AnswerNotRelevantException.class, recover = "fallback")
    public Answer askQuestion(Question question) {
        int attempt = RetrySynchronizationManager.getContext() != null ? RetrySynchronizationManager.getContext().getRetryCount() + 1 : 1;
        log.info("askQuestion attempts: {}", attempt);

        ChatClient chatClient = ChatClient.create(chatModel);
        String answerText = chatClient.prompt()
                .user(question.question())
                .call()
                .content();

        /**
         * you could also do factEvaluation just as done in test but comes with cost of more tokens
         * also keep retry times minimum to reduce cost of tokens and rate limiting
         * if the FactEvaluator and RelevancyEvaluator does not meet your criteria you could implement the Evaluator interface
         * and create your own custom evaluator
        */
        evaluateRelevancy(question, answerText);
        return new Answer(answerText);
    }

    @Recover
    public Answer fallback(AnswerNotRelevantException e){
        log.info("recovering ...");
        return new Answer("I'm sorry, I wasn't able to answer the question");
    }

    private void evaluateRelevancy(Question question, String answerText){
        log.info("evaluating relevancy ...");
        EvaluationRequest evaluationRequest = new EvaluationRequest(question.question(), answerText);

        EvaluationResponse evaluatedRelevance = relevancyEvaluator.evaluate(evaluationRequest);

        if(!evaluatedRelevance.isPass()){
            throw new AnswerNotRelevantException(question.question(), answerText);
        }
    }
}
