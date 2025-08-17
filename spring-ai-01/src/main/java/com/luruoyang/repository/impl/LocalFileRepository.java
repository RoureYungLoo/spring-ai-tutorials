package com.luruoyang.repository.impl;

import com.luruoyang.repository.FileRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

/**
 * @author luruoyang
 */
@Repository
@Slf4j
public class LocalFileRepository implements FileRepository {
  private final String CHAT_PDF_PATH = "spring-ai-01\\chat-pdf.properties";
  private final String CHAT_PDF_JSON = "spring-ai-01\\chat-pdf.json";

  @Autowired
  private VectorStore vectorStore;

  // 会话id 与 文件名的对应关系，方便查询会话历史时重新加载文件
  private final Properties chatFiles = new Properties();

  @Override
  public boolean save(String chatId, Resource resource) {
    // 2.保存到本地磁盘
    String filename = resource.getFilename();
    File target = new File(Objects.requireNonNull(filename));
    if (!target.exists()) {
      try {
        Files.copy(resource.getInputStream(), target.toPath());
      } catch (IOException e) {
        log.error("Failed to save PDF resource.", e);
        return false;
      }
    }
    // 3.保存映射关系
    chatFiles.put(chatId, filename);
    return true;
  }

  @Override
  public Resource selectFileByChatId(String chatId) {
    return new FileSystemResource(chatFiles.getProperty(chatId));
  }

  @PostConstruct
  private void init() {
    Resource resource = new FileSystemResource(CHAT_PDF_PATH);
    if (resource.exists()) {
      try {
        chatFiles.load(new BufferedReader(new InputStreamReader(
            resource.getInputStream(), StandardCharsets.UTF_8
        )));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    Resource vectorResource = new FileSystemResource(CHAT_PDF_JSON);
    if (vectorResource.exists()) {
      SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
      simpleVectorStore.load(vectorResource);
    }
  }

  @PreDestroy
  private void persistent() {
    try {
      chatFiles.store(new FileWriter(CHAT_PDF_PATH), LocalDateTime.now().toString());
      SimpleVectorStore simpleVectorStore = (SimpleVectorStore) vectorStore;
      simpleVectorStore.save(new File(CHAT_PDF_JSON));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
