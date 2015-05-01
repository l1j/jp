-- ----------------------------
-- Table structure for `pet_types`
-- ----------------------------
DROP TABLE IF EXISTS `pet_types`;
CREATE TABLE `pet_types` (
  `npc_id` int(10) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `tame_item_id` int(10) NOT NULL DEFAULT '0',
  `min_hpup` int(10) NOT NULL,
  `max_hpup` int(10) NOT NULL,
  `min_mpup` int(10) NOT NULL,
  `max_mpup` int(10) NOT NULL,
  `transform_item_id` int(10) NOT NULL NULL DEFAULT '0',
  `transform_npc_id` int(10) NOT NULL NULL DEFAULT '0',
  `message_id1` int(10) NOT NULL,
  `message_id2` int(10) NOT NULL,
  `message_id3` int(10) NOT NULL,
  `message_id4` int(10) NOT NULL,
  `message_id5` int(10) NOT NULL,
  `defy_message_id` int(10) NOT NULL,
  `use_equipment` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
