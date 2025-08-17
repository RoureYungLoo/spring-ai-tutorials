package com.luruoyang.controller;

import com.luruoyang.enums.BusinessType;
import com.luruoyang.repository.ChatHistoryRepository;
import com.luruoyang.repository.impl.RedisChatHistoryRepositoryImpl;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * @author luruoyang
 */
@RestController
@RequestMapping("/ai")
public class CustomerController {
  @Autowired
  private ChatClient serviceChatClient;

  @Autowired
  private ChatHistoryRepository chatHistoryRepository;

  @Autowired
  private RedisChatHistoryRepositoryImpl redisChatHistoryRepository;

  /**
   * 智能客服业务方法, 同步调用
   */
  @GetMapping(value = "/serviceSync", produces = "text/html;charset=UTF-8")
  public String serviceSync(String prompt, String chatId) {
    redisChatHistoryRepository.saveHistory(BusinessType.fromValue("service"), chatId);

    return serviceChatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
        .call()
        .content();
  }

  /**
   * 智能客服业务方法, 流式调用
   */
  @GetMapping(value = "/service", produces = "text/html;charset=UTF-8")
  public Flux<String> serviceStream(String prompt, String chatId) {
    redisChatHistoryRepository.saveHistory(BusinessType.fromValue("service"), chatId);

    return serviceChatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
        .stream()
        .content();
  }
}
