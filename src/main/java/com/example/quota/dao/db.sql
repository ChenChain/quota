create database quota;

CREATE TABLE `quota` (
                         `id` bigint unsigned NOT NULL AUTO_INCREMENT,
                         `user_id` bigint NOT NULL,
                         `type` varchar(255) NOT NULL DEFAULT '',
                         `avail` double NOT NULL,
                         `total` double NOT NULL,
                         `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
                         `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `update_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uni_user_type` (`user_id`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;