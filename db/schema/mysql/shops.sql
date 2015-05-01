-- ----------------------------
-- Table structure for `shops`
-- ----------------------------
DROP TABLE IF EXISTS `shops`;
CREATE TABLE `shops` (
  `npc_id` int(10) unsigned NOT NULL,
  `item_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `order_id` int(10) unsigned NOT NULL,
  `pack_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_id`,`item_id`,`order_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
