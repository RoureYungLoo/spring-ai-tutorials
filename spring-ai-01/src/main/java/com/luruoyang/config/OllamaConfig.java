package com.luruoyang.config;

import com.luruoyang.constants.SystemConstants;
import com.luruoyang.model.AlibabaOpenAiChatModel;
import com.luruoyang.tools.CourseTools;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.ai.autoconfigure.openai.OpenAiConnectionProperties;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Ollama 配置
 *
 * @author luruoyang
 */
@Configuration
@Slf4j
public class OllamaConfig {

  @Bean
  // public ChatClient chatClient(OllamaChatModel chatModel, @Qualifier("redisChatMemory") ChatMemory chatMemory) {
  public ChatClient chatClient(OpenAiChatModel chatModel, @Qualifier("redisChatMemory") ChatMemory chatMemory) {

    log.info("当前会话持久化Bean: {}", chatMemory);

    return ChatClient
        .builder(chatModel)
        .defaultOptions(
            ChatOptions.builder()
                /* 阿里百炼多模态模型 */
                .model("qwen-omni-turbo")
                .build()
        )
        /* 配置系统角色 */
        .defaultSystem("您是一家程序员的职业教育公司的客户聊天助手，你的名字叫小鹿。请以友好、乐于助人和愉快的方式解答学生的各种问题。")
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
//    return new JdbcChatMemory();
//    return new RedisChatMemory();
//  }
  @Bean
  public Advisor messageChatMemoryAdvisor(@Qualifier("redisChatMemory") ChatMemory chatMemory) {
    log.info("当前会话持久化拦截器使用的Bean: {} ", chatMemory);
    return new MessageChatMemoryAdvisor(chatMemory);
  }

  /**
   * 智能客服client客户端
   */
  @Bean
  // public ChatClient serviceChatClient(OpenAiChatModel model, @Qualifier("redisChatMemory") ChatMemory chatMemory, CourseTools courseTools) {
  public ChatClient serviceChatClient(AlibabaOpenAiChatModel model, @Qualifier("redisChatMemory") ChatMemory chatMemory, CourseTools courseTools) {
    return ChatClient.builder(model)
        .defaultSystem(SystemConstants.CUSTOMER_SERVICE_SYSTEM)
        .defaultAdvisors(
            simpleLoggerAdvisor(),
            messageChatMemoryAdvisor(chatMemory)
        )
        // 开启ToolCalling
        .defaultTools(courseTools)
        .build();
  }

  /* 注入阿里云百炼平台, 兼容流式调用 */
  @Bean
  public AlibabaOpenAiChatModel alibabaOpenAiChatModel(OpenAiConnectionProperties commonProperties, OpenAiChatProperties chatProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider, ObjectProvider<WebClient.Builder> webClientBuilderProvider, ToolCallingManager toolCallingManager, RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler, ObjectProvider<ObservationRegistry> observationRegistry, ObjectProvider<ChatModelObservationConvention> observationConvention) {
    String baseUrl = StringUtils.hasText(chatProperties.getBaseUrl()) ? chatProperties.getBaseUrl() : commonProperties.getBaseUrl();
    String apiKey = StringUtils.hasText(chatProperties.getApiKey()) ? chatProperties.getApiKey() : commonProperties.getApiKey();
    String projectId = StringUtils.hasText(chatProperties.getProjectId()) ? chatProperties.getProjectId() : commonProperties.getProjectId();
    String organizationId = StringUtils.hasText(chatProperties.getOrganizationId()) ? chatProperties.getOrganizationId() : commonProperties.getOrganizationId();
    Map<String, List<String>> connectionHeaders = new HashMap<>();
    if (StringUtils.hasText(projectId)) {
      connectionHeaders.put("OpenAI-Project", List.of(projectId));
    }

    if (StringUtils.hasText(organizationId)) {
      connectionHeaders.put("OpenAI-Organization", List.of(organizationId));
    }
    RestClient.Builder restClientBuilder = restClientBuilderProvider.getIfAvailable(RestClient::builder);
    WebClient.Builder webClientBuilder = webClientBuilderProvider.getIfAvailable(WebClient::builder);
    OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(baseUrl).apiKey(new SimpleApiKey(apiKey)).headers(CollectionUtils.toMultiValueMap(connectionHeaders)).completionsPath(chatProperties.getCompletionsPath()).embeddingsPath("/v1/embeddings").restClientBuilder(restClientBuilder).webClientBuilder(webClientBuilder).responseErrorHandler(responseErrorHandler).build();
    AlibabaOpenAiChatModel chatModel = AlibabaOpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(chatProperties.getOptions()).toolCallingManager(toolCallingManager).retryTemplate(retryTemplate).observationRegistry((ObservationRegistry) observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)).build();
    Objects.requireNonNull(chatModel);
    observationConvention.ifAvailable(chatModel::setObservationConvention);
    return chatModel;
  }

  /* 向量数据库 */
  @Bean
  public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
    return SimpleVectorStore.builder(embeddingModel).build();
  }

  /**
   * PDF 问答 Client
   */

  @Bean
  public ChatClient pdfClient(AlibabaOpenAiChatModel model, @Qualifier("redisChatMemory") ChatMemory chatMemory, VectorStore vectorStore) {
    return ChatClient.builder(model)
        .defaultSystem("请根据提供的上下文回答问题, 不要自己猜测, 不要联网搜索")
        .defaultAdvisors(
            simpleLoggerAdvisor(),
            messageChatMemoryAdvisor(chatMemory),
            questionAnswerAdvisor(vectorStore)
        )
        .build();
  }

  /**
   * PDF 问答 advisor
   */
  @Bean
  public Advisor questionAnswerAdvisor(VectorStore vectorStore) {
    return new QuestionAnswerAdvisor(
        vectorStore,
        SearchRequest.builder()
            .similarityThreshold(0.5)
            .topK(1)
            .build()
    );
  }
}
