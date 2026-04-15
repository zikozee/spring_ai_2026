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