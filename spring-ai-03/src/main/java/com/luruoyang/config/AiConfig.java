package com.luruoyang.config;

import com.luruoyang.constants.SystemConstants;
import com.luruoyang.tools.ChinaMobileTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.BaseChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luruoyang
 */
@Configuration
public class AiConfig {

  /**
   * 向量存储 Vector Store
   */
  @Bean
  public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
  }

  /**
   * Chat Client
   */
  @Bean
  public ChatClient chatClient(
      OpenAiChatModel chatModel,
      VectorStore vectorStore,
      ChatMemoryRepository chatMemoryRepository,
      ChinaMobileTool chinaMobileTool
  ) {
    return ChatClient.builder(chatModel)
        .defaultAdvisors(
            simpleLoggerAdvisor(),
            messageChatMemoryAdvisor(chatMemoryRepository),
            questionAnswerAdvisor(vectorStore)
        )
        .defaultSystem(SystemConstants.PROMPT)
        .defaultTools(chinaMobileTool)
        .build();
  }

  /**
   * Chat Log
   */
  @Bean
  public Advisor simpleLoggerAdvisor() {
    return SimpleLoggerAdvisor.builder().build();
  }

  /**
   * Chat Memory
   */
  @Bean
  public Advisor messageChatMemoryAdvisor(ChatMemoryRepository chatMemoryRepository) {
    MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
        .chatMemoryRepository(chatMemoryRepository)
        .build();
    MessageChatMemoryAdvisor advisor = MessageChatMemoryAdvisor.builder(chatMemory)
        .build();
    return advisor;
  }

  /**
   * Chat Memory Repository
   */
  @Bean
  public ChatMemoryRepository chatMemoryRepository() {
    return new InMemoryChatMemoryRepository();
  }

  /**
   * Chat RAG
   */
  @Bean
  public Advisor questionAnswerAdvisor(VectorStore vectorStore) {
    return new QuestionAnswerAdvisor(vectorStore);
  }
}

