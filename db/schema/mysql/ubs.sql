-- ----------------------------
-- Table structure for `ubs`
-- ----------------------------
DROP TABLE IF EXISTS `ubs`;
CREATE TABLE `ubs` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `map_id` int(10) unsigned NOT NULL,
  `area_x1` int(10) unsigned NOT NULL,
  `area_y1` int(10) unsigned NOT NULL,
  `area_x2` int(10) unsigned NOT NULL,
  `area_y2` int(10) unsigned NOT NULL,
  `min_level` int(10) unsigned NOT NULL,
  `max_level` int(10) unsigned NOT NULL,
  `max_player` int(10) unsigned NOT NULL,
  `enter_royal` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_knight` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_wizard` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_elf` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_darkelf` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_dragonknight` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_illusionist` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_male` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `enter_female` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `use_pot` tinyint(3) unsigned NOT NULL DEFAULT '1',
  `hpr_bonus` int(10) NOT NULL DEFAULT '0',
  `mpr_bonus` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
