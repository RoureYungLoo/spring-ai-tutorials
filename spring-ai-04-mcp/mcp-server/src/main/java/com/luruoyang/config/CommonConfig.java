package com.luruoyang.config;

import com.luruoyang.tool.WeatherServiceTool;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author luruoyang
 */
@Configuration
public class CommonConfig {
  @Autowired
  private WeatherServiceTool weatherServiceTool;

  @Bean
  public List<ToolCallback> weatherTool() {
    ToolCallback[] toolCallbacks = ToolCallbacks.from(weatherServiceTool);
    return List.of(toolCallbacks);
  }

}
