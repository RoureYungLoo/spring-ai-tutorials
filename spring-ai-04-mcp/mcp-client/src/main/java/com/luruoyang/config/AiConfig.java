package com.luruoyang.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luruoyang
 */
@Configuration
public class AiConfig {

  private static final String SYSTEM_PROMPT = """
      你是一个全能助手，可以帮我解决各种问题。
      """;

  @Bean
  public ChatClient chatClient(OpenAiChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
    return ChatClient.builder(chatModel)
        .defaultSystem(SYSTEM_PROMPT)
        // Tool Callback
        .defaultToolCallbacks(toolCallbackProvider)
        .build();
  }
}
