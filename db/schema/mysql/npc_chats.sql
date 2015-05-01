-- ----------------------------
-- Table structure for `npc_chats`
-- ----------------------------
DROP TABLE IF EXISTS `npc_chats`;
CREATE TABLE `npc_chats` (
  `npc_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `chat_timing` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `start_delay_time` int(10) NOT NULL DEFAULT '0',
  `chat_id1` varchar(255) DEFAULT NULL,
  `chat_id2` varchar(255) DEFAULT NULL,
  `chat_id3` varchar(255) DEFAULT NULL,
  `chat_id4` varchar(255) DEFAULT NULL,
  `chat_id5` varchar(255) DEFAULT NULL,
  `chat_interval` int(10) unsigned NOT NULL DEFAULT '0',
  `is_shout` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `is_world_chat` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `is_repeat` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `repeat_interval` int(10) unsigned NOT NULL DEFAULT '0',
  `game_time` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_id`,`chat_timing`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
