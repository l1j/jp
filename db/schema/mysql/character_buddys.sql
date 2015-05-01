-- ----------------------------
-- Table structure for `character_buddys`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_buddys` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_id` int(10) unsigned NOT NULL,
  `buddy_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`char_id`,`buddy_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
