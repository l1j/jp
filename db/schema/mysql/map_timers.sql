-- ----------------------------
-- Table structure for `map_timers`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `map_timers` (
  `char_id` int(10) unsigned NOT NULL,
  `map_id` int(10) unsigned NOT NULL,
  `area_id` int(10) unsigned NOT NULL,
  `enter_time` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`char_id`,`area_id`),
  KEY `map_id` (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
