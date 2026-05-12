## Assigning prompt roles
- User — The message contains a question or statement made by (or on behalf of) the user of an application.
- System — The message contains instructions given to the LLM from the application itself.
- Assistant — The message contains a response from the LLM.
- Tool—The message contains instructions for invoking tools to perform some action or fetch additional context information.

### NOTE 
 - Not all LLM APIs support the same selection of message roles. 
 - When an API doesn’t support the System role, 
 - Spring AI simply adds the text of the message intended for the System role to the User message.

## LOGGING RAW HTTP REQUESTS AND RESPONSES FOR THE CHAT CLIENT
- we use logbook
- Note, however, that Spring AI’s Gemini module uses Google’s own HTTP client library instead of RestClient, 
- so Logbook will not work if you’re using Google Gemini as your LLM of choice

## ADJUSTING VARIABILITY 3.4.1
- consider: 
  - “books” - 0.475
  - “coins” - 0.236
  - “records” - 0.129
  - “arts” - 0.096
  - “stamps” - 0.064

- we can influence how random the LLM’s responses are by adjusting chat options: **temperature, Top-P, Top-K**.
- It is generally recommended to specify either temperature or Top-P, but not both. That said, it is allowed, and you could achieve some success using both.

  - **Temperature**: range (0 - 2), default 1.0. Higher values make output more random, while lower values make it more focused and deterministic.
    - As temperature approaches 2, the probabilities for each token tend to even out. As temperature approaches 0, 
    - the probability for the highest probability token approaches 1 while the other probabilities approach zero. 
    - When temperature is 1, the probabilities are unchanged

    - spring.ai.openai.chat.options.temperature=0.7
    - `java
      ChatOptions options = ChatOptions.builder()
          .temperature(0.7)
          .build();
      `

  - **Top-P**: range (0 - 1),  is used to limit the set of candidate tokens to the smallest set where the sum of probabilities 
    - meets or exceeds the value of top-P
    - i.e the smallest combinations of the tokens that will be closes to the value of top-P.
    - For example, suppose that Top-P is 0.8. As illustrated in figure 3.5, the sum of probabilities for **books**, **coins**, and **records** is 0.84
      - making that the smallest set whose sum of probabilities meets or exceeds Top-P

  - spring.ai.openai.chat.options.top-p=0.8
  - `java
    ChatOptions options = ChatOptions.builder()
        .topP(0.8)
        .build();
    `
  
    - **Top-K**: range (0 - ∞), works very similarly to Top-P, except that it applies simple counting rather than sum of 
    - _not supported by OpenAI, but supported by ollama_
    - probabilities to decide which tokens remain and which are eliminated from candidacy. 
    - For example, if Top-K is 3, only the first four items—books, coins, records, and arts  will be considered for output. 
    - **stamps** will be eliminated from candidacy, regardless of its probability.


## FORMATTING RESPONSE OUTPUT
- OpenAI’s GPT, llama3.1:8b (check others) models do a good job of applying the formatting instructions, but other LLMs (such as Mistral 7b) may not. 
  - Unfortunately, if you are using one of the LLMs that don’t honor requested formatting, 
  - you won’t be able to reliably count on the results to be converted and bound to an object. 
  - If that happens, a JsonParseException will be thrown when Spring AI tries to bind the non-JSON response to an object.

## IMPLEMENTING RAG ON GAMERULES
- note the other project _**https://github.com/zikozee/spring_ai_2026_rag_augmentation**_ already has the vector store loaded
- we just connect to the same vector store via this project
`java 
        SearchRequest searchRequest = SearchRequest
                .builder()
                .query(question)
                .topK(6) // default is 4
                .similarityThreshold(0.5) // range 0.0 - 1.0
                .filterExpression(new FilterExpressionBuilder()
                        .eq("gameTitle", normalizeGameTitle(gameTitle)).build()
                )
                .build();
`
- increasing topK means more tokens
- similarity threshold 1.0 means exact match, 
  - Although the similarity threshold can be useful in weeding out some results that don’t seem all that relevant, 
  - take caution against setting it too high. The higher it is set, 
  - the greater the possibility that you will get fewer (or possibly zero) results back, 
  - and the LLM will not have enough context to be able to respond to the question.

- askQuestion for Ollama was returning two separate conflicting json but openAi worked well
- `json   
  "$schema": "https://json-schema.org/draft/2020-12/schema",
      "type": "object",
      "properties": {
        "answer": {
          "type": "string"
        },
        "gameTitle": {
          "type": "string"
        }
      },
      "required": [
        "answer",
        "gameTitle"
      ],
      "additionalProperties": false
    } {
      "gameTitle": "Hamlet",
      "answer": "To build the Church, make a delivery to it by transporting the required materials and consuming them. The required materials and point rewards are different for each slot."
    }" into class com.zee.springai.dto.AnswerWithTitle
 `

### RAG WITH ADVISOR
- _**QuestionAnswerAdvisor**_ [see implementation]  appends the below to the prompt 
- Context information is below.
  ---------------------
  {question_answer_context}
  ---------------------
  Given the context and provided history information and no prior knowledge,
  reply to the user comment. If the answer is not in the context, inform
  the user that you can't answer the question.

### APPLYING MODULAR RAG
- _**RetrievalAugmentationAdvisor**_ is a modular RAG advisor that lets you customize its behavior by plugging in supporting components to handle various tasks.
  - e.g ensure that the question being asked is in the same language as the documents being queried from the vector store
  - say you want to increase the accuracy of the documents found when querying the vector store by focusing the user’s query.
  -  maybe you want to find relevant documents from some location other than a vector store.
  - VectorStoreDocumentRetriever is the only implementation available **you can create other implementations of the interface to meet other business needs**
`java 
  public interface DocumentRetriever extends Function<Query, List<Document>> {
      List<Document> retrieve(Query query);
      default List<Document> apply(Query query) {
        return retrieve(query);
      }
    }
`

- add dependency
  - implementation 'org.springframework.ai:spring-ai-rag:2.0.0-M4'
  
  #### Rewriting the user’s query
  - Spring AI offers a query transformer called **RewriteQueryTransformer** that will rewrite the user’s query to be more concise and clear-cut. It works its magic by using an LLM via a ChatClient

  #### Translating user queries
  - **TranslationQueryTransformer** is another query transformer that translates the user’s query to a target language, ideally matching the language of the documents.
  
  #### Expanding user queries (opposite of RewriteQueryTransformer)
  - MultiQueryExpander takes an opposite approach to RewriteQueryTransformer. 
    - Instead of simplifying the query, MultiQueryExpander expands it by creating multiple queries, each a different way of saying the original.
  
  - The idea here is that by asking the same question in multiple different ways, the vector search might identify relevant documents that it wouldn’t have matched based on the original wording.

### MAKING MEMORIES IN AI
- Spring AI implements management of conversational memory as advisors
  - More Specifically Spring AI provides three different advisors
    - **MessageChatMemoryAdvisor**
      - adds the chat history to the prompt as distinct messages for the user and assistant roles. _**Not all Model supports this**_
    - **PromptChatMemoryAdvisor**
      - will inject the chat history into the prompt as one big string injected into a system message template
    - **VectorStoreChatMemoryAdvisor**
      - takes a completely different approach to storing chat history by storing it in a vector store
        -  it can query the vector store, the same way it would query for document chunks in RAG, finding the pieces of chat history that are most similar to the question being asked
          - then injects it into the prompt as string injected into a system message template same as PromptChatMemoryAdvisor
        
- MessageChatMemoryAdvisor and PromptChatMemoryAdvisor store the chat history via some implementation of ChatMemory
  - we can also customize the max messages in the chatMemory (MessageChatMemoryAdvisor, PromptChatMemoryAdvisor: since they both use the MessageWindowChatMemory)