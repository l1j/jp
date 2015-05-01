-- ----------------------------
-- Table structure for `npc_actions`
-- ----------------------------
DROP TABLE IF EXISTS `npc_actions`;
CREATE TABLE `npc_actions` (
  `npc_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `normal_action` varchar(255) DEFAULT NULL,
  `chaotic_action` varchar(255) DEFAULT NULL,
  `teleport_url` varchar(255) DEFAULT NULL,
  `teleport_urla` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`npc_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
