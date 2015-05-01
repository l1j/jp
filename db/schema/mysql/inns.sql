-- ----------------------------
-- Table structure for `inns`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `inns` (
  `npc_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `room_number` int(10) unsigned NOT NULL,
  `key_id` int(10) unsigned NOT NULL DEFAULT '0',
  `lodger_id` int(10) unsigned NOT NULL DEFAULT '0',
  `hall` int(10) unsigned NOT NULL DEFAULT '0',
  `due_time` datetime DEFAULT NULL,
  PRIMARY KEY (`npc_id`,`room_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
