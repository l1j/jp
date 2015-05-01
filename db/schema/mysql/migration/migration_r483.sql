-- ----------------------------
-- inventory_items
-- ----------------------------
ALTER TABLE `inventory_items` ADD `protect_item_id` int(10) unsigned NOT NULL DEFAULT '0' AFTER `is_protected`;
