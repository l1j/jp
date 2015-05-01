-- ----------------------------
-- Table structure for `pets`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `pets` (
  `item_obj_id` int(10) unsigned NOT NULL,
  `id` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `npc_id` int(10) unsigned NOT NULL,
  `level` int(10) unsigned NOT NULL DEFAULT '0',
  `hp` int(10) unsigned NOT NULL DEFAULT '0',
  `mp` int(10) unsigned NOT NULL DEFAULT '0',
  `exp` int(10) unsigned NOT NULL DEFAULT '0',
  `lawful` int(10) unsigned NOT NULL DEFAULT '0',
  `food` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_obj_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
