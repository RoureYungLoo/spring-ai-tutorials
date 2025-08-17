package com.luruoyang.controller;

import cn.hutool.core.util.ArrayUtil;
import com.luruoyang.enums.BusinessType;
import com.luruoyang.repository.ChatHistoryRepository;
import com.luruoyang.repository.impl.RedisChatHistoryRepositoryImpl;
import com.luruoyang.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * @author luruoyang
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class ChatController {

  @Autowired
  private ChatClient chatClient;


  @Autowired
  private ChatHistoryRepository chatHistoryRepository;

  @Autowired
  private RedisChatHistoryRepositoryImpl redisChatHistoryRepository;

  @Autowired
  @Qualifier("redisChatMemory")
  private ChatMemory chatMemory;


  /**
   * 同步调用
   */
  @RequestMapping("/chat2")
  public String chat(@RequestParam(value = "prompt", defaultValue = "你是谁?") String prompt) {
    String assistant = chatClient.prompt(prompt).call().content();
    return assistant;
  }

  /**
   * 流式调用
   */
  @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8", method = RequestMethod.POST)
  public Flux<String> chatWithStream(
      @RequestParam(value = "prompt", defaultValue = "给我讲一个计算机编程领域的笑话?") String prompt,
      @RequestParam("chatId") String chatId,
      @RequestParam(value = "files", required = false) List<MultipartFile> files
  ) {

    log.info("chatId: {}", chatId);
    log.info("prompt: {}", prompt);


    // 保存会话ID
    // chatHistoryRepository.saveHistory(BusinessType.fromValue("chat"), chatId);
    redisChatHistoryRepository.saveHistory(BusinessType.fromValue("chat"), chatId);

    // 非多模态
    if (ArrayUtil.isEmpty(files)) {
      Flux<String> content = chatClient
          .prompt()
          .user(prompt)
          /* 区分不同会话 */
          .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
          .stream()
          .content();
      return content;
    } else {
      // 多模态
      Media[] mediaList = files.stream().map(file -> Media.builder()
          .mimeType(MimeType.valueOf(file.getContentType()))
          .data(file.getResource())
          .build()).toArray(Media[]::new);

      Flux<String> content = chatClient
          .prompt()
          // 多模态
          .user(promptUserSpec -> {
            promptUserSpec
                .text(prompt)
                .media(mediaList);
          })
          /* 区分不同会话 */
          .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
          .stream()
          .content();
      return content;
    }

  }

  /**
   * 查询会话ID列表
   */
  @GetMapping("/history/{businessType}")
  public List<String> getChatIdsByBusinessType(@PathVariable("businessType") String businessType) {
    // return chatHistoryRepository.getChatIdsByType(businessType);
    return redisChatHistoryRepository.getChatIdsByType(businessType);
  }

  /**
   * 根据会话ID查询会话历史记录
   */
  @GetMapping("/history/{businessType}/{chatId}")
  public List<MessageVO> getChatHistoryByTypeAndChatId(
      @PathVariable("businessType") String businessType,
      @PathVariable("chatId") String chatId) {

    List<Message> messages = chatMemory.get(chatId, Integer.MAX_VALUE);
    if (messages.isEmpty()) {
      return List.of();
    }

    return messages.stream().map(MessageVO::new).collect(Collectors.toList());
  }


}


