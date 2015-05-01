-- ----------------------------
-- Table structure for `ub_times`
-- ----------------------------
DROP TABLE IF EXISTS `ub_times`;
CREATE TABLE `ub_times` (
  `ub_id` int(10) unsigned NOT NULL,
  `ub_time` int(10) unsigned NOT NULL,
  PRIMARY KEY (`ub_id`,`ub_time`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
