-- ----------------------------
-- Table structure for `restart_locations`
-- ----------------------------
DROP TABLE IF EXISTS `restart_locations`;
CREATE TABLE `restart_locations` (
  `area` int(10) unsigned NOT NULL,
  `loc_x` int(10) unsigned NOT NULL,
  `loc_y` int(10) unsigned NOT NULL,
  `map_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`area`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
