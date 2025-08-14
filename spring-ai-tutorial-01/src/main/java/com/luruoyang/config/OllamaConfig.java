package com.luruoyang.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ollama 配置
 *
 * @author luruoyang
 */
@Configuration
@Slf4j
public class OllamaConfig {

  @Bean
  public ChatClient chatClient(OllamaChatModel chatModel, @Qualifier("redisChatMemory") ChatMemory chatMemory) {

    log.info("当前会话持久化Bean: {}", chatMemory);

    return ChatClient
        .builder(chatModel)
        /* 配置系统角色 */
        .defaultSystem("您是一家名为“黑马程序员”的职业教育公司的客户聊天助手，你的名字叫小黑。请以友好、乐于助人和愉快的方式解答学生的各种问题。")
        .defaultAdvisors(
            /* 配置日志拦截器 */
            simpleLoggerAdvisor(),
            /* 基于内存的会话记忆 */
            messageChatMemoryAdvisor(chatMemory)
        )
        .build();
  }

  /**
   * 日志记录
   */
  @Bean
  public Advisor simpleLoggerAdvisor() {
    return new SimpleLoggerAdvisor();
  }

  /**
   * 会话记忆
   */
//  @Bean
//  public ChatMemory chatMemory() {
//    return new InMemoryChatMemory();
//  }
  @Bean
  public Advisor messageChatMemoryAdvisor(@Qualifier("redisChatMemory") ChatMemory chatMemory) {

    log.info("当前会话持久化拦截器使用的Bean: {} ", chatMemory);

    return new MessageChatMemoryAdvisor(chatMemory);
  }
}
