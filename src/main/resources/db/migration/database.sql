



CREATE DATABASE IF NOT EXISTS `zhongpingyouxuan` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `zhongpingyouxuan`;


CREATE TABLE IF NOT EXISTS `roles` ( 
    `id` INT NOT NULL AUTO_INCREMENT, 
    `name` ENUM('ROLE_USER','ROLE_MERCHANT','ROLE_ADMIN') NOT NULL, 
    PRIMARY KEY (`id`), 
    UNIQUE INDEX `UK_role_name` (`name` ASC) 
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `users` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `username` VARCHAR(50) NOT NULL, 
    `password` VARCHAR(100) NOT NULL, 
    `email` VARCHAR(100) NOT NULL, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    PRIMARY KEY (`id`), 
    UNIQUE INDEX `UKr43af9ap4edm43mmtq01oddj6` (`username` ASC), 
    UNIQUE INDEX `UK6dotkott2kjsp8vw4d0m25fb7` (`email` ASC) 
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `categories` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(50) NOT NULL, 
    PRIMARY KEY (`id`), 
    UNIQUE INDEX `UK_category_name` (`name` ASC) 
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `carousels` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `image_url` VARCHAR(255) NOT NULL, 
    `target_url` VARCHAR(255) NULL, 
    `display_order` INT NOT NULL DEFAULT 0, 
    PRIMARY KEY (`id`) 
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `user_roles` ( 
    `user_id` BIGINT NOT NULL, 
    `role_id` INT NOT NULL, 
    PRIMARY KEY (`user_id`, `role_id`), 
    INDEX `FK_user_roles_role_id` (`role_id` ASC), 
    CONSTRAINT `FK_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE, 
    CONSTRAINT `FK_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE 
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `merchants` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(100) NOT NULL, 
    `description` TEXT NULL, 
    `address` VARCHAR(255) NULL, 
    `province` VARCHAR(50) NULL, 
    `city` VARCHAR(50) NULL, 
    `area` VARCHAR(50) NULL, 
    `user_id` BIGINT NULL, 
    PRIMARY KEY (`id`), 
    UNIQUE INDEX `UK_merchant_user_id` (`user_id` ASC), 
    CONSTRAINT `FK_merchants_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL 
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `products` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `name` VARCHAR(200) NOT NULL, 
    `description` TEXT NULL, 
    `image_urls` TEXT NULL, 
    `average_rating` DECIMAL(3,2) DEFAULT 0.00, 
    `review_count` INT NOT NULL DEFAULT 0, 
    `likes_count` INT NOT NULL DEFAULT 0, 
    `favorites_count` INT NOT NULL DEFAULT 0, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
    `category_id` BIGINT NULL, 
    `merchant_id` BIGINT NOT NULL, 
    PRIMARY KEY (`id`), 
    INDEX `FK_products_category_id` (`category_id` ASC), 
    INDEX `FK_products_merchant_id` (`merchant_id` ASC), 
    CONSTRAINT `FK_products_category_id` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL, 
    CONSTRAINT `FK_products_merchant_id` FOREIGN KEY (`merchant_id`) REFERENCES `merchants` (`id`) ON DELETE CASCADE 
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `reviews` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `rating` INT NOT NULL, 
    `title` VARCHAR(200) NOT NULL, 
    `content` TEXT NOT NULL, 
    `image_urls` VARCHAR(1024) NULL, 
    `likes_count` INT NOT NULL DEFAULT 0, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    `user_id` BIGINT NOT NULL, 
    `product_id` BIGINT NOT NULL, 
    `response_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING', 
    PRIMARY KEY (`id`), 
    INDEX `FK_reviews_user_id` (`user_id` ASC), 
    INDEX `FK_reviews_product_id` (`product_id` ASC), 
    CONSTRAINT `FK_reviews_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE, 
    CONSTRAINT `FK_reviews_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE 
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `comments` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `content` TEXT NOT NULL, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    `review_id` BIGINT NOT NULL, 
    `user_id` BIGINT NOT NULL, 
    `parent_id` BIGINT NULL, 
    `reply_to_user_id` BIGINT NULL, 
    PRIMARY KEY (`id`), 
    INDEX `FK_comments_review_id` (`review_id` ASC), 
    INDEX `FK_comments_user_id` (`user_id` ASC), 
    INDEX `FK_comments_parent_id` (`parent_id` ASC), 
    INDEX `FK_comments_reply_to_user_id` (`reply_to_user_id` ASC), 
    CONSTRAINT `FK_comments_review_id` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE, 
    CONSTRAINT `FK_comments_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE, 
    CONSTRAINT `FK_comments_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE, 
    CONSTRAINT `FK_comments_reply_to_user_id` FOREIGN KEY (`reply_to_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL 
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `favorites` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `user_id` BIGINT NOT NULL, 
    `review_id` BIGINT NOT NULL, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    PRIMARY KEY (`id`), 
    UNIQUE INDEX `UKenol5bdci3ia5woh2h80f53uw` (`user_id` ASC, `review_id` ASC), 
    INDEX `FK_favorites_review_id` (`review_id` ASC), 
    CONSTRAINT `FK_favorites_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE, 
    CONSTRAINT `FK_favorites_review_id` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE 
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `likes` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `user_id` BIGINT NOT NULL, 
    `review_id` BIGINT NOT NULL, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    PRIMARY KEY (`id`), 
    UNIQUE INDEX `UKdtvlgco6hxau378a8cg2er5g8` (`user_id` ASC, `review_id` ASC), 
    INDEX `FK_likes_review_id` (`review_id` ASC), 
    CONSTRAINT `FK_likes_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE, 
    CONSTRAINT `FK_likes_review_id` FOREIGN KEY (`review_id`) REFERENCES `reviews` (`id`) ON DELETE CASCADE 
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `product_favorites` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    `product_id` BIGINT NOT NULL, 
    `user_id` BIGINT NOT NULL, 
    PRIMARY KEY (`id`),
    UNIQUE INDEX `UKaeio5gmhk53yb0vug1gs3cs6s` (`user_id`, `product_id`),
    CONSTRAINT `FK_product_favorites_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `FK_product_favorites_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `product_likes` ( 
    `id` BIGINT NOT NULL AUTO_INCREMENT, 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    `product_id` BIGINT NOT NULL, 
    `user_id` BIGINT NOT NULL, 
    PRIMARY KEY (`id`),
    UNIQUE INDEX `UK712hmqku6f5l2q28jql7vj0f5` (`user_id`, `product_id`),
    CONSTRAINT `FK_product_likes_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `FK_product_likes_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB;


DELIMITER $$

DROP TRIGGER IF EXISTS `after_review_insert`$$
CREATE TRIGGER `after_review_insert`
    AFTER INSERT ON `reviews`
    FOR EACH ROW
BEGIN
    UPDATE `products`
    SET
        `review_count` = (SELECT COUNT(*) FROM `reviews` WHERE `product_id` = NEW.product_id),
        `average_rating` = IFNULL((SELECT AVG(`rating`) FROM `reviews` WHERE `product_id` = NEW.product_id), 0.00)
    WHERE `id` = NEW.product_id;
END$$


DROP TRIGGER IF EXISTS `after_review_delete`$$
CREATE TRIGGER `after_review_delete`
    AFTER DELETE ON `reviews`
    FOR EACH ROW
BEGIN
    UPDATE `products`
    SET
        `review_count` = (SELECT COUNT(*) FROM `reviews` WHERE `product_id` = OLD.product_id),
        `average_rating` = IFNULL((SELECT AVG(`rating`) FROM `reviews` WHERE `product_id` = OLD.product_id), 0.00)
    WHERE `id` = OLD.product_id;
END$$


DROP TRIGGER IF EXISTS `after_like_insert`$$
CREATE TRIGGER `after_like_insert`
    AFTER INSERT ON `likes`
    FOR EACH ROW
BEGIN
    UPDATE `reviews`
    SET `likes_count` = `likes_count` + 1
    WHERE `id` = NEW.review_id;
END$$


DROP TRIGGER IF EXISTS `after_like_delete`$$
CREATE TRIGGER `after_like_delete`
    AFTER DELETE ON `likes`
    FOR EACH ROW
BEGIN
    UPDATE `reviews`
    SET `likes_count` = `likes_count` - 1
    WHERE `id` = OLD.review_id;
END$$


DROP TRIGGER IF EXISTS `after_product_favorite_insert`$$
CREATE TRIGGER `after_product_favorite_insert`
    AFTER INSERT ON `product_favorites`
    FOR EACH ROW
BEGIN
    UPDATE `products`
    SET `favorites_count` = `favorites_count` + 1
    WHERE `id` = NEW.product_id;
END$$


DROP TRIGGER IF EXISTS `after_product_favorite_delete`$$
CREATE TRIGGER `after_product_favorite_delete`
    AFTER DELETE ON `product_favorites`
    FOR EACH ROW
BEGIN
    UPDATE `products`
    SET `favorites_count` = `favorites_count` - 1
    WHERE `id` = OLD.product_id;
END$$


DROP TRIGGER IF EXISTS `after_product_like_insert`$$
CREATE TRIGGER `after_product_like_insert`
    AFTER INSERT ON `product_likes`
    FOR EACH ROW
BEGIN
    UPDATE `products`
    SET `likes_count` = `likes_count` + 1
    WHERE `id` = NEW.product_id;
END$$


DROP TRIGGER IF EXISTS `after_product_like_delete`$$
CREATE TRIGGER `after_product_like_delete`
    AFTER DELETE ON `product_likes`
    FOR EACH ROW
BEGIN
    UPDATE `products`
    SET `likes_count` = `likes_count` - 1
    WHERE `id` = OLD.product_id;
END$$
DELIMITER ;


DELIMITER $$
DROP PROCEDURE IF EXISTS `AddComment`$$
CREATE PROCEDURE `AddComment`(IN p_review_id BIGINT, IN p_user_id BIGINT, IN p_content TEXT, IN p_parent_id BIGINT, IN p_reply_to_user_id BIGINT)
BEGIN
    
    INSERT INTO `comments` (`review_id`, `user_id`, `content`, `parent_id`, `reply_to_user_id`, `created_at`)
    VALUES (p_review_id, p_user_id, p_content, p_parent_id, p_reply_to_user_id, NOW());
END$$
DELIMITER ;


INSERT INTO `roles` (`id`, `name`) VALUES (1, 'ROLE_USER'), (2, 'ROLE_MERCHANT'), (3, 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE `name`=`name`;

INSERT INTO `users` (`id`, `username`, `password`, `email`) VALUES
                                                                (1, 'admin', '$2a$12$fC6wH11eQBMYrc12GHsNlO73rzUKFdkqm/Hz5D3HhxjmcVGrjltHe', 'admin@example.com'),
                                                                (2, 'merchant_apple', '$2a$12$fC6wH11eQBMYrc12GHsNlO73rzUKFdkqm/Hz5D3HhxjmcVGrjltHe', 'store@apple.com'),
                                                                (3, 'user_zhangsan', '$2a$12$fC6wH11eQBMYrc12GHsNlO73rzUKFdkqm/Hz5D3HhxjmcVGrjltHe', 'zhangsan@example.com'),
                                                                (4, 'user_lisi', '$2a$12$fC6wH11eQBMYrc12GHsNlO73rzUKFdkqm/Hz5D3HhxjmcVGrjltHe', 'lisi@example.com')
ON DUPLICATE KEY UPDATE `username`=`username`;

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (1, 3), (2, 2), (3, 1), (4, 1)
ON DUPLICATE KEY UPDATE `user_id`=`user_id`;

INSERT INTO `categories` (`id`, `name`) VALUES (1, '电子产品'), (2, '家用电器'), (3, '图书音像')
ON DUPLICATE KEY UPDATE `name`=`name`;

INSERT INTO `merchants` (`id`, `name`, `description`, `address`, `province`, `city`, `area`, `user_id`) VALUES
    (1, 'Apple官方旗舰店', '销售Apple全系列产品', '四川省成都市锦江区', '四川省', '成都市', '锦江区', 2)
ON DUPLICATE KEY UPDATE `name`=`name`;

INSERT INTO `products` (`id`, `name`, `description`, `image_urls`, `category_id`, `merchant_id`) VALUES
                                                                                       (1, 'iPhone 25 Pro', '最新款苹果智能手机，配备A25芯片', '/static/productsImg/82c20ce3e2d6aff00a9bf0d667aa388f.jpg', 1, 1),
                                                                                       (2, 'MacBook Pro 16英寸', '搭载M5 Pro芯片，性能强劲', '/static/productsImg/8adab70e36394fb3880bc83243666401.jpg', 1, 1)
ON DUPLICATE KEY UPDATE `name`=`name`;

INSERT INTO `reviews` (`rating`, `title`, `content`, `user_id`, `product_id`) VALUES
                                                                                  (5, '性能卓越！', 'iPhone 25 Pro的摄像系统太棒了，夜景拍摄效果惊人！', 3, 1),
                                                                                  (4, '续航有点失望', '虽然性能很好，但是电池续航没有想象中那么给力。', 4, 1),
                                                                                  (5, '程序员的梦想', 'MacBook Pro的新款键盘手感很好，编译代码速度飞快！', 3, 2);

INSERT INTO `carousels` (`image_url`, `target_url`, `display_order`) VALUES
                                                                         ('/static/productsImg/82c20ce3e2d6aff00a9bf0d667aa388f.jpg', '/product/detail.html?id=1', 1),
                                                                         ('/static/productsImg/8adab70e36394fb3880bc83243666401.jpg', '/product/detail.html?id=2', 2);

COMMIT;