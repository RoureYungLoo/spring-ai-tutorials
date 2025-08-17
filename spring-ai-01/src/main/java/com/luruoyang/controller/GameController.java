package com.luruoyang.controller;

/**
 * @author luruoyang
 */

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@RestController
@RequestMapping("/ai")
public class GameController {
  @Autowired
   private ChatClient gameClient;

  @GetMapping(value = "/game", produces = "text/html;charset=UTF-8")
  public Flux<String> game(String prompt, String chatId) {

    return gameClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
        .stream()
        .content();
  }
}
