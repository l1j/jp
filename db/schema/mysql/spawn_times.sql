-- ----------------------------
-- Table structure for `spawn_times`
-- ----------------------------
DROP TABLE IF EXISTS `spawn_times`;
CREATE TABLE `spawn_times` (
  `npc_id` int(10) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `time_start` time DEFAULT NULL,
  `time_end` time DEFAULT NULL,
  `delete_at_endtime` tinyint(1) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`npc_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
