-- ============================================================
-- 迁移脚本 v2：注册体系重构（账号系统分配 + 手机/邮箱唯一 + 医生标签字典）
-- 适用：已在运行中的旧库（不丢数据）；全新部署直接用 soul.sql 即可
-- 执行前请备份：mysqldump -u root -p 库名 > backup.sql
-- ============================================================

-- 0. 指定数据库（按实际库名修改）
-- USE `soul`;

-- ------------------------------------------------------------
-- 1. user 表：手机号改 varchar(11) 必填唯一、邮箱唯一、加时间戳
-- ------------------------------------------------------------
ALTER TABLE `user` MODIFY COLUMN `phone` varchar(11) NOT NULL COMMENT '手机号，唯一';
ALTER TABLE `user` MODIFY COLUMN `email` varchar(60) DEFAULT NULL COMMENT '邮箱，唯一（可空）';
ALTER TABLE `user` ADD COLUMN `create_time` datetime DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE `user` ADD COLUMN `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `user` ADD UNIQUE KEY `uk_user_phone` (`phone`);
ALTER TABLE `user` ADD UNIQUE KEY `uk_user_email` (`email`);

-- 若旧数据存在 phone 为空/重复导致加索引失败，先执行下面两条排查（按需取消注释）：
-- SELECT userid, nickname, phone FROM `user` WHERE phone IS NULL OR phone = '';
-- SELECT phone, COUNT(*) FROM `user` GROUP BY phone HAVING COUNT(*) > 1;

-- ------------------------------------------------------------
-- 2. doctor 表：type 扩容为 255（标签逗号存储）+ 加 update_time
-- ------------------------------------------------------------
ALTER TABLE `doctor` MODIFY COLUMN `type` varchar(255) DEFAULT NULL COMMENT '擅长领域标签，逗号分隔';
ALTER TABLE `doctor` ADD COLUMN `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 旧标签归一化为系统默认标签（可选，仅为演示数据更统一）
UPDATE `doctor` SET `type` = '恋爱心理,自我成长' WHERE `id` = 101;
UPDATE `doctor` SET `type` = '情绪管理,焦虑抑郁' WHERE `id` = 102;
UPDATE `doctor` SET `type` = '人际关系,学业压力' WHERE `id` = 103;
UPDATE `doctor` SET `type` = '情绪管理,亲子关系' WHERE `id` = 104;

-- ------------------------------------------------------------
-- 3. tag 表：系统默认标签字典
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `tag` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT '标签名称',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT IGNORE INTO `tag` (`name`,`sort_order`) VALUES
('情绪管理',1),('人际关系',2),('学业压力',3),('焦虑抑郁',4),('恋爱心理',5),
('亲子关系',6),('职业规划',7),('睡眠问题',8),('自我成长',9);

-- ------------------------------------------------------------
-- 4. 说明
-- ------------------------------------------------------------
-- · 账号规则：学生 = 入学年(4位) + 6位序号，如 2026000001；医生 = 2 + 5位序号，如 200001
-- · 管理员账号 111111 保留不变
-- · 存量账号（101/102/103/104/2233320103 等）继续可用，仅新注册走系统分配
