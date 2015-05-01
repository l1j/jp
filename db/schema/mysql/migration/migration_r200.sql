ALTER TABLE `inventory_items` MODIFY COLUMN `is_sealed` tinyint(1) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` ADD `is_protected` tinyint(1) unsigned NOT NULL DEFAULT '0' AFTER `is_sealed`;
