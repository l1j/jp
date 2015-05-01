-- ----------------------------
-- Table structure for `ub_managers`
-- ----------------------------
DROP TABLE IF EXISTS `ub_managers`;
CREATE TABLE `ub_managers` (
  `ub_id` int(10) unsigned NOT NULL,
  `npc_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ub_id`, `npc_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
