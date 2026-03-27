package com.luruoyang.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luruoyang
 */
@Configuration
public class AiConfig {

  @Bean
  public ChatClient chatClient() {
    OpenAiChatModel openAiChatModel = OpenAiChatModel.builder().build();
    ChatClient chatClient = ChatClient.builder(openAiChatModel)
        .defaultSystem("你是一个Java程序员学习小助手, 你的角色是高级Java开发工程师")
        .defaultAdvisors(advisorSpec -> advisorSpec.)
        .build();

    return chatClient;
  }
}
