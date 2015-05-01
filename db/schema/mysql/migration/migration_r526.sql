-- ----------------------------
-- clans
-- ----------------------------
ALTER TABLE `clans` ADD `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `house_id`;
