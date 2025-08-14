drop database if exists `spring-ai-ollama`;

create database if not exists `spring-ai-ollama`;

use `spring-ai-ollama`;

CREATE TABLE `conversation`
(
    `id`              bigint unsigned NOT NULL AUTO_INCREMENT,
    `conversation_id` varchar(255)  DEFAULT NULL COMMENT '对话id',
    `message`         varchar(1024) DEFAULT NULL COMMENT '对话',
    `create_time`     datetime      DEFAULT NULL COMMENT '创建时间',
    `type`            varchar(255)  DEFAULT NULL COMMENT '类型：system、user、assistant、tool',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1939645577516060674
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;