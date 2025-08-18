CREATE TABLE `plan`
(
    `id`              bigint NOT NULL,
    `name`            varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `monthly_rent`    double                                 DEFAULT NULL,
    `data`            varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `call_minutes`    int                                    DEFAULT NULL,
    `extra_services`  varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `contract_period` int                                    DEFAULT NULL,
    `promotion`       varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci