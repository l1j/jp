-- ----------------------------
-- Table structure for `dungeons`
-- ----------------------------
DROP TABLE IF EXISTS `dungeons`;
CREATE TABLE `dungeons` (
  `src_x` int(10) unsigned NOT NULL,
  `src_y` int(10) unsigned NOT NULL,
  `src_map_id` int(10) unsigned NOT NULL,
  `new_x` int(10) unsigned NOT NULL,
  `new_y` int(10) unsigned NOT NULL,
  `new_map_id` int(10) unsigned NOT NULL,
  `new_heading` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `note` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`src_x`,`src_y`,`src_map_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
