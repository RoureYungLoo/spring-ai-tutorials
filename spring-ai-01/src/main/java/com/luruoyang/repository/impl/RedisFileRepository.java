package com.luruoyang.repository.impl;

import com.luruoyang.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * @author luruoyang
 */
@Component
@Slf4j
public class RedisFileRepository implements FileRepository {
  @Autowired
  private VectorStore vectorStore;

  @Autowired
  private RedisTemplate<Object, Object> redisTemplate;

  @Override
  public boolean save(String chatId, Resource resource) {
    String filename = resource.getFilename();
    File target = new File(Objects.requireNonNull(filename));
    if (!target.exists()) {
      try {
        Files.copy(resource.getInputStream(), target.toPath());
      } catch (IOException e) {
        log.error("文件上传失败:{}", e.getMessage());
        throw new RuntimeException(e);
      }
    }

    // 保存会话的文件 ID 到 Redis
    String key = "spring:ai:pdf:" + chatId;
    ListOperations<Object, Object> fileList = redisTemplate.opsForList();
    fileList.rightPush(key, filename);

    return true;
  }

  /**
   * 根据ChatID获取文件
   */
  @Override
  public Resource selectFileByChatId(String chatId) {
    String key = "spring:ai:pdf:" + chatId;
    ListOperations<Object, Object> list = redisTemplate.opsForList();
    List<Object> filenameList = list.range(key, 0, -1);

    Resource resource = null;
    // 向后兼容
    if (filenameList.size() == 1) {
      resource = new FileSystemResource(filenameList.get(0).toString());
    }

    return resource;
  }
}
