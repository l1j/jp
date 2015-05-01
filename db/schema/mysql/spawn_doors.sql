-- ----------------------------
-- Table structure for `spawn_doors`
-- ----------------------------
DROP TABLE IF EXISTS `spawn_doors`;
CREATE TABLE `spawn_doors` (
  `id` int(10) NOT NULL,
  `map_id` int(10) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `gfx_id` int(10) NOT NULL,
  `loc_x` int(10) NOT NULL,
  `loc_y` int(10) NOT NULL,
  `hp` int(10) NOT NULL DEFAULT '0',
  `npc_id` int(10) NOT NULL DEFAULT '0',
  `is_open` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
