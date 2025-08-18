package com.luruoyang.controller;

import com.luruoyang.dto.ChatDto;
import com.mysql.cj.log.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author luruoyang
 */
@RestController
@RequestMapping("/ai")
@Tag(name = "MCP")
@Slf4j
public class ChatController {

  @Autowired
  private ChatClient chatClient;


  @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @Operation(summary = "发起MCP调用")
  public Flux<String> chat(@RequestBody ChatDto dto) {

    log.info("dto: {}", dto);

    return chatClient.prompt()
        .user(dto.getPrompt())
        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, dto.getChatId()))
        .stream()
        .content();
  }
}