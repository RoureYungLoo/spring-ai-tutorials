package com.luruoyang.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luruoyang
 */
@Configuration
public class AiConfig {

  private static final String SYSTEM_PROMPT = """
    #角色
    你是一个全能助手，名字叫小智，可以帮我解决各种问题。
    #技能
    ##技能1：
    帮我分析运行bug，并且给我提出解决方案。

    ##技能2：
    给代码生成注释，无需逐行都注释，在关键代码添加注释
    当前的时间是{now}
  
    北京:101010100
    天津:101030100
    上海:101020100
    重庆:101040100
    广州:101280101
    深圳:101280601
    石家庄:101090101
    郑州:101180101
    武汉:101200101
    长沙:101250101
    南京:101190101
    杭州:101210101
    成都:101270101
    西安:101110101
    沈阳:101070101
    长春:101060101
    哈尔滨:101050101
    太原:101100101
      """;

  @Bean
  public ChatClient chatClient(OpenAiChatModel chatModel, ToolCallbackProvider toolCallbackProvider) {
    return ChatClient.builder(chatModel)
        .defaultSystem(SYSTEM_PROMPT)
        // Tool Callback
        .defaultToolCallbacks(toolCallbackProvider)
        .build();
  }
}
