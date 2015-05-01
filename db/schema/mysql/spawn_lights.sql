-- ----------------------------
-- Table structure for `spawn_lights`
-- ----------------------------
DROP TABLE IF EXISTS `spawn_lights`;
CREATE TABLE `spawn_lights` (
  `id` int(10) unsigned NOT NULL,
  `npc_id` int(10) unsigned NOT NULL,
  `loc_x` int(10) unsigned NOT NULL,
  `loc_y` int(10) unsigned NOT NULL,
  `map_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
