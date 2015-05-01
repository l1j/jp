-- ----------------------------
-- Table structure for `spawn_mobs`
-- ----------------------------
DROP TABLE IF EXISTS `spawn_mobs`;
CREATE TABLE `spawn_mobs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `npc_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `group_id` int(10) unsigned NOT NULL DEFAULT '0',
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_x` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_y` int(10) unsigned NOT NULL DEFAULT '0',
  `random_x` int(10) unsigned NOT NULL DEFAULT '0',
  `random_y` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_x1` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_y1` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_x2` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_y2` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(10) unsigned NOT NULL DEFAULT '0',
  `min_respawn_delay` int(10) unsigned NOT NULL DEFAULT '0',
  `max_respawn_delay` int(10) unsigned NOT NULL DEFAULT '0',
  `map_id` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn_screen` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `movement_distance` int(10) unsigned NOT NULL DEFAULT '0',
  `rest` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `near_spawn` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
