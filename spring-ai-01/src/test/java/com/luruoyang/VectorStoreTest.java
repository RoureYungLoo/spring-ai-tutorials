package com.luruoyang;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author luruoyang
 */
@SpringBootTest
@DisplayName("向量数据库测试")
public class VectorStoreTest {
  @Autowired
  private VectorStore vectorStore;

  @Test
  @DisplayName("向量数据库测试")
  public void testVectorStore() {
    Resource resource = new FileSystemResource("F:\\001_学习资料\\项目二\\资料\\中二知识笔记.pdf");
    // 读取文档
    PagePdfDocumentReader reader = new PagePdfDocumentReader(resource,
        PdfDocumentReaderConfig.builder()
            // 文档解析格式
            .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
            // 每个文档的页数
            .withPagesPerDocument(1)
            .build()
    );

    List<Document> documentList = reader.read();
    // 添加到向量数据库
    vectorStore.add(documentList);

    // 构造搜索请求
    SearchRequest request = SearchRequest.builder()
        .query("论语中教育的目的是什么")
        .topK(1)
        .similarityThreshold(0.6)
        .filterExpression("file_name == '中二知识笔记.pdf'")
        .build();

    // 查询向量数据库
    List<Document> res = vectorStore.similaritySearch(request);
    if (res.isEmpty()) {
      System.out.println("no content");
      return;
    }

    // 输出结果
    for (Document re : res) {
      System.out.println(re.getId());
      System.out.println(re.getScore());
      System.out.println(re.getText());
    }
  }
}
