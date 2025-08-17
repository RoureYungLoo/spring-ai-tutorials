package com.luruoyang.controller;

import com.luruoyang.entity.vo.Result;
import com.luruoyang.enums.BusinessType;
import com.luruoyang.repository.ChatHistoryRepository;
import com.luruoyang.repository.FileRepository;
import com.luruoyang.repository.impl.RedisChatHistoryRepositoryImpl;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

/**
 * @author luruoyang
 */
@RestController
@RequestMapping("/ai/pdf")
public class PDFController {

  @Autowired
  private FileRepository localFileRepository;

  @Autowired
  private VectorStore vectorStore;

  @Autowired
  private ChatClient pdfClient;

  @Autowired
  private RedisChatHistoryRepositoryImpl redisChatHistoryRepository;

  /**
   * 文件上传
   */
  @RequestMapping("/upload/{chatId}")
  public Result upload(@PathVariable String chatId, @RequestParam MultipartFile file) {
    Result r = null;

    if (file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
      Resource resource = file.getResource();
      if (localFileRepository.save(chatId, resource)) {
        writeToVectorStore(resource);
        r = Result.ok();
      } else {
        r = Result.fail("文件保存失败");
      }
    }
    r = Result.fail("只能上传PDF文件");

    return r;

  }

  /**
   * 文件下载
   */
  @GetMapping("/file/{chatId}")
  public ResponseEntity<Resource> download(@PathVariable String chatId) {
    Resource resource = localFileRepository.selectFileByChatId(chatId);
    if (resource.exists()) {
      String filename = URLEncoder.encode(Objects.requireNonNull(resource.getFilename()), StandardCharsets.UTF_8);
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .header("Content-Disposition", "attachment; filename=" + filename)
          .body(resource);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  private void writeToVectorStore(Resource resource) {
    PagePdfDocumentReader reader = new PagePdfDocumentReader(resource,
        PdfDocumentReaderConfig.builder()
            .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
            .withPagesPerDocument(1)
            .build()
    );

    List<Document> docs = reader.read();
    vectorStore.add(docs);
  }

  /*
   * PDF 对话
   */
  @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8")
  public Flux<String> chat(String chatId, String prompt) {
    // 保存会话历史
    redisChatHistoryRepository.saveHistory(BusinessType.PDF, chatId);

    Resource file = localFileRepository.selectFileByChatId(chatId);
    return pdfClient.prompt()
        .user(prompt)
        .advisors(advisorSpec -> advisorSpec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
        .advisors(advisorSpec -> advisorSpec.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, String.format("file_name == '%s'", file.getFilename())))
        .stream()
        .content();

  }
}
