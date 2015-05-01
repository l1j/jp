-- ----------------------------
-- Table structure for `clans`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `clans` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `leader_id` int(10) unsigned NOT NULL,
  `castle_id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `house_id` int(10) unsigned NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
