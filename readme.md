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