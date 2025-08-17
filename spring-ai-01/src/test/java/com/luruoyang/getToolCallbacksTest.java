package com.luruoyang;

import com.luruoyang.entity.pojo.Course;
import com.luruoyang.tools.CourseTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.util.ToolUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.springframework.ai.model.tool.ToolCallingChatOptions.validateToolCallbacks;

/**
 * @author luruoyang
 */
@Slf4j
public class getToolCallbacksTest {
  private List<Object> toolObjects = new ArrayList<>(List.of(new CourseTools()));

  @Test
  public void getToolCallbacks() {
    ToolCallback[] toolCallbacks = toolObjects.stream()
        .map(toolObject -> Stream.of(ReflectionUtils.getDeclaredMethods(toolObject.getClass()))
            .filter(toolMethod -> toolMethod.isAnnotationPresent(Tool.class))
            .filter(toolMethod -> !isFunctionalType(toolMethod))
            .map(toolMethod -> MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.from(toolMethod))
                .toolMetadata(ToolMetadata.from(toolMethod))
                .toolMethod(toolMethod)
                .toolObject(toolObject)
                .toolCallResultConverter(ToolUtils.getToolCallResultConverter(toolMethod))
                .build())
            .toArray(ToolCallback[]::new))
        .flatMap(Stream::of)
        .toArray(ToolCallback[]::new);

    System.out.println(toolCallbacks);
    validateToolCallbacks(List.of(toolCallbacks));

    for (ToolCallback callback : toolCallbacks) {
      System.out.println(callback);
    }
  }

  private boolean isFunctionalType(Method toolMethod) {
    var isFunction = ClassUtils.isAssignable(toolMethod.getReturnType(), Function.class)
        || ClassUtils.isAssignable(toolMethod.getReturnType(), Supplier.class)
        || ClassUtils.isAssignable(toolMethod.getReturnType(), Consumer.class);

    if (isFunction) {
      log.warn("Method {} is annotated with @Tool but returns a functional type. "
          + "This is not supported and the method will be ignored.", toolMethod.getName());
    }

    return isFunction;
  }
}
