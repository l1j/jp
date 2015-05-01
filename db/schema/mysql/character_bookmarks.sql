-- ----------------------------
-- Table structure for `character_bookmarks`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_bookmarks` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_id` int(10) unsigned NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `loc_x` int(10) unsigned NOT NULL,
  `loc_y` int(10) unsigned NOT NULL,
  `map_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `char_id` (`char_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
