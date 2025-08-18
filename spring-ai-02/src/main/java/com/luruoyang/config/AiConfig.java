package com.luruoyang.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luruoyang
 */
@Configuration
@Slf4j
public class AiConfig {

  /**
   * DashScope Chat Client
   */
  @Bean
  public ChatClient chatClient(DashScopeChatModel chatModel, @Autowired ChatMemory chatMemory) {
    ChatClient chatClient = ChatClient.builder(chatModel)
        .defaultSystem("你是乾隆宴叫花鸡的智能客服, 梁先生")
        .defaultAdvisors(
            // 日志
            simpleLoggerAdvisor(),
            // 持久化
            messageChatMemoryAdvisor(chatMemory)
        )
        // 全局 Tool Calling
        // .defaultToolNames("getToutiaoNews")
        .build();
    log.info("alibaba ai chatClient init success");
    return chatClient;
  }

  /**
   * 会话日志
   */
  @Bean
  public Advisor simpleLoggerAdvisor() {
    return new SimpleLoggerAdvisor();
  }

  /**
   * 会话持久化
   */
  @Bean
  public Advisor messageChatMemoryAdvisor(@Autowired ChatMemory chatMemory) {
    MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
    log.info(" 会话持久化: {}", advisor);
    return advisor;
  }

  /**
   * 会话记忆
   */
  @Bean
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.builder()
        .maxMessages(50)
        .chatMemoryRepository(new InMemoryChatMemoryRepository())
        .build();
  }

}
