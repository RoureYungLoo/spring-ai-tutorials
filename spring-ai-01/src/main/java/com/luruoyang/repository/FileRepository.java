package com.luruoyang.repository;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

/**
 * @author luruoyang
 */
public interface FileRepository {

  /**
   * 上传文件, 关联会话ID
   */
  boolean save(String chatId, Resource resource);

  /**
   * 根据会话ID获取文件
   */
  Resource selectFileByChatId(String chatId);
}
