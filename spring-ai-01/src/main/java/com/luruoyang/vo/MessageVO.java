package com.luruoyang.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

/**
 * @author luruoyang
 */
@Data
@NoArgsConstructor
public class MessageVO {
  private String role;
  private String content;

  public MessageVO(Message message) {
    this.role = switch (message.getMessageType()) {
      case USER -> "user";
      case ASSISTANT -> "assistant";
      default -> "";
    };
    this.content = message.getText();
  }
}
