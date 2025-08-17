package com.luruoyang.controller;

import com.luruoyang.tools.DateTimeTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * alibaba chat controller
 *
 * @author luruoyang
 */
@RestController("/ai")
@Tag(name = "智能聊天")
public class AliChatController {

  @Autowired
  private ChatClient chatClient;

  @GetMapping("/chat")
  @Operation(summary = "同步聊天")
  public String chat(String chatId, String prompt) {
    return chatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
        .toolNames("getToutiaoNews")
        .call()
        .content();
  }

  @GetMapping("/stream/chat")
  @Operation(summary = "流式聊天")
  public Flux<String> toolToutiao(String chatId, String prompt) {
    Flux<String> content = chatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
        .toolNames("getToutiaoNews")
        .stream()
        .content();
    return content;
  }

  @GetMapping("/sina/news")
  @Operation(summary = "Tool新浪新闻")
  public Flux<String> toolSina(String chatId, String prompt) {
    Flux<String> content = chatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
        .toolNames("getSinaNews")
        .stream()
        .content();
    return content;
  }

  @Autowired
  private DateTimeTools dateTimeTools;

  @GetMapping("/time")
  @Operation(summary = "获取当前时间")
  public Flux<String> toolDate(String chatId, String prompt) {
    Flux<String> content = chatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
        .tools(dateTimeTools)
        .stream()
        .content();
    return content;
  }

  @GetMapping("/alarm")
  @Operation(summary = "设置闹钟")
  public Flux<String> toolAlarm(String chatId, String prompt) {
    Flux<String> content = chatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
        .tools(dateTimeTools)
        .stream()
        .content();
    return content;
  }

}
