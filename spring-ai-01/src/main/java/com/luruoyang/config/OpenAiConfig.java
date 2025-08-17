package com.luruoyang.config;

import com.luruoyang.constants.SystemConstants;
import com.luruoyang.memory.RedisChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luruoyang
 */
@Configuration
public class OpenAiConfig {


  @Bean
  public ChatClient gameClient(OpenAiChatModel model, @Qualifier("redisChatMemory") ChatMemory chatMemory) {
//  public ChatClient gameClient(OllamaChatModel model, @Qualifier("redisChatMemory") ChatMemory chatMemory) {

    return ChatClient.builder(model)
        .defaultSystem(SystemConstants.SYSTEM_PROMPT_GAME)
        .defaultAdvisors(
            new SimpleLoggerAdvisor(),
            new MessageChatMemoryAdvisor(chatMemory)
        )
        .build();
  }
}
