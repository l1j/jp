-- ----------------------------
-- Table structure for `drop_items`
-- ----------------------------
DROP TABLE IF EXISTS `drop_items`;
CREATE TABLE `drop_items`(
  `npc_id` int(10) unsigned NOT NULL,
  `item_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `min` int(10) unsigned NOT NULL DEFAULT '0',
  `max` int(10) unsigned NOT NULL DEFAULT '0',
  `chance` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY(`npc_id`,`item_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
