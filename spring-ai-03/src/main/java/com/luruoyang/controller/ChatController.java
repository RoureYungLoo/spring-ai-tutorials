package com.luruoyang.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author luruoyang
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "智能客服")
public class ChatController {

  @Autowired
  private ChatClient chatClient;

  @GetMapping("/chat")
  public Flux<String> chat(String chatId, String prompt) {

    Flux<String> stringFlux = chatClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
        .stream()
        .content()
        .concatWith(Flux.just("[END]"));

    return stringFlux;
  }
}
