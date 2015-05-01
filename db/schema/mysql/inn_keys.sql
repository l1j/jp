-- ----------------------------
-- Table structure for `inn_keys`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `inn_keys` (
  `item_obj_id` int(10) unsigned NOT NULL,
  `id` int(10) unsigned NOT NULL,
  `npc_id` int(10) unsigned DEFAULT NULL,
  `hall` int(10) unsigned DEFAULT NULL,
  `due_time` datetime DEFAULT NULL,
  PRIMARY KEY (`item_obj_id`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
