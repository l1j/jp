-- ----------------------------
-- Table structure for `spawn_npcs`
-- ----------------------------
DROP TABLE IF EXISTS `spawn_npcs`;
CREATE TABLE `spawn_npcs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `npc_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `count` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_x` int(10) unsigned NOT NULL DEFAULT '0',
  `loc_y` int(10) unsigned NOT NULL DEFAULT '0',
  `random_x` int(10) unsigned NOT NULL DEFAULT '0',
  `random_y` int(10) unsigned NOT NULL DEFAULT '0',
  `heading` int(10) unsigned NOT NULL DEFAULT '0',
  `respawn_delay` int(10) unsigned NOT NULL DEFAULT '0',
  `map_id` int(10) unsigned NOT NULL DEFAULT '0',
  `movement_distance` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
