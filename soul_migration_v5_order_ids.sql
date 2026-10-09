-- ============================================================
-- soul 数据库迁移 v5：预约订单号规则化
-- 变更内容：
--   1. consultation.id 由 int 自增 改为 bigint（系统按规则分配）
--   2. 订单号规则：yyyyMMdd（8位日期） + 4位当日流水，共 12 位
--      例：2026-10-09 的第 1 条订单 → 202610090001
--   3. 存量订单按「预约日期 + 当日流水」重新编号
-- 影响：后端 ConsultationServiceImpl 负责分配订单号，前端展示无需改动
-- 执行方式：mysql -uroot -p123456 < soul_migration_v5_order_ids.sql
-- ============================================================

USE `soul`;

-- ------------------------------------------------------------
-- 1) 订单号升位（必须在重编号之前：12 位数字已超出 int 范围）
--    同时去掉 AUTO_INCREMENT，改为由后端显式分配
-- ------------------------------------------------------------
ALTER TABLE `consultation`
    MODIFY COLUMN `id` bigint NOT NULL COMMENT '订单号：yyyyMMdd+4位当日流水';

-- ------------------------------------------------------------
-- 2) 存量订单重编号：以「预约日期」为前缀，同日按原 id 顺序编排流水
--    用临时表承载新号，避免 UPDATE 与子查询同表冲突
-- ------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS `tmp_order_id_map`;

CREATE TEMPORARY TABLE `tmp_order_id_map` (
    `old_id` bigint NOT NULL,
    `new_id` bigint NOT NULL,
    PRIMARY KEY (`old_id`)
);

INSERT INTO `tmp_order_id_map` (`old_id`, `new_id`)
SELECT t.`old_id`,
       CAST(CONCAT(t.`ymd`, LPAD(t.`rn`, 4, '0')) AS UNSIGNED)
FROM (
    SELECT c.`id` AS `old_id`,
           DATE_FORMAT(
               COALESCE(
                   STR_TO_DATE(SUBSTRING_INDEX(c.`appointment_time`, '/', 1), '%Y-%m.%d'),
                   CURDATE()
               ), '%Y%m%d'
           ) AS `ymd`,
           ROW_NUMBER() OVER (
               PARTITION BY DATE_FORMAT(
                   COALESCE(
                       STR_TO_DATE(SUBSTRING_INDEX(c.`appointment_time`, '/', 1), '%Y-%m.%d'),
                       CURDATE()
                   ), '%Y%m%d'
               )
               ORDER BY c.`id`
           ) AS `rn`
    FROM `consultation` c
) t;

UPDATE `consultation` c
JOIN `tmp_order_id_map` m ON m.`old_id` = c.`id`
SET c.`id` = m.`new_id`;

DROP TEMPORARY TABLE IF EXISTS `tmp_order_id_map`;

-- ------------------------------------------------------------
-- 3) 可选：清理历史遗留的「孤儿订单」
--    引用的学生/医生账号已不存在 —— 正是"删除用户未级联删除订单"留下的残留
--    需要清理时取消下面注释后执行
-- ------------------------------------------------------------
-- DELETE c FROM `consultation` c
-- LEFT JOIN `user` u ON u.`userid` = c.`stu_id`
-- LEFT JOIN `user` d ON d.`userid` = c.`doc_id`
-- WHERE u.`userid` IS NULL OR d.`userid` IS NULL;

-- ------------------------------------------------------------
-- 4) 校验：迁移后的订单号应形如 202411270001
-- ------------------------------------------------------------
SELECT `id` AS 订单号, `stu_id` AS 学生账号, `doc_id` AS 医生账号,
       `appointment_time` AS 预约时间, `status` AS 状态
FROM `consultation`
ORDER BY `id`;
