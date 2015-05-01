-- ----------------------------
-- Table structure for `clan_applies`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `clan_applies` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `clan_id` int(10) unsigned NOT NULL,
  `clan_name` varchar(255) NOT NULL,
  `char_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
