-- ----------------------------
-- Table structure for `enchant_logs`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `enchant_logs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_id` int(10) unsigned NOT NULL,
  `item_id` int(10) unsigned NOT NULL,
  `old_enchant_level` tinyint(3) NOT NULL DEFAULT '0',
  `new_enchant_level` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `key_id` (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
