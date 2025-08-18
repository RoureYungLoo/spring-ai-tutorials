package com.luruoyang.vector;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.observation.conventions.VectorStoreProvider;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author luruoyang
 */
@Slf4j
@Component
public class RuleDataEmbedding {

  /**
   * 向量存储
   */
  @Autowired
  private VectorStore vectorStore;

  /**
   * 资源文件路径
   */
  @Value("classpath:rule-data.json")
  private Resource resource;

  //  @PostConstruct
  public void init() throws Exception {
    JsonReader reader = new JsonReader(resource, "type", "condition", "description");
    List<Document> documentList = reader.get();

    vectorStore.add(documentList);

    log.info("数据写入向量数据成功, 数据条数:{}", documentList.size());
  }

}
