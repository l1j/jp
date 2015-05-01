-- ----------------------------
-- Table structure for `clan_warehouse_histories`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `clan_warehouse_histories` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clan_id` int(10) NOT NULL,
  `char_name` varchar(255) NOT NULL,
  `type` tinyint(2) NOT NULL,
  `item_name` varchar(255) NOT NULL,
  `item_count` int(10) NOT NULL,
  `record_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `char_name` (`char_name`),
  KEY `clan_id` (`clan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
