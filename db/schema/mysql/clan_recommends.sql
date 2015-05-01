-- ----------------------------
-- Table structure for `clan_recommends`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `clan_recommends` (
  `clan_id` int(10) unsigned NOT NULL,
  `clan_name` varchar(255) NOT NULL,
  `char_name` varchar(255) NOT NULL,
  `clan_type` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `message` varchar(255) NOT NULL,
  PRIMARY KEY (`clan_id`),
  KEY `clan_name` (`clan_name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
