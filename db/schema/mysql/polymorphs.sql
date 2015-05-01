-- ----------------------------
-- Table structure for polymorphs
-- ----------------------------
DROP TABLE IF EXISTS `polymorphs`;
CREATE TABLE `polymorphs` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `gfx_id` int(10) unsigned NOT NULL,
  `min_level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `weapon_equip` int(10) unsigned NOT NULL DEFAULT '0',
  `armor_equip` int(10) unsigned NOT NULL DEFAULT '0',
  `can_use_skill` tinyint(1) unsigned DEFAULT '1',
  `cause` tinyint(3) unsigned NOT NULL DEFAULT '7',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
