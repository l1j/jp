-- ----------------------------
-- Table structure for `pet_items`
-- ----------------------------
DROP TABLE IF EXISTS `pet_items`;
CREATE TABLE `pet_items` (
  `item_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `hit_modifier` tinyint(3) NOT NULL DEFAULT '0',
  `dmg_modifier` tinyint(3) NOT NULL DEFAULT '0',
  `ac` tinyint(3) NOT NULL DEFAULT '0',
  `str` tinyint(3) NOT NULL DEFAULT '0',
  `con` tinyint(3) NOT NULL DEFAULT '0',
  `dex` tinyint(3) NOT NULL DEFAULT '0',
  `int` tinyint(3) NOT NULL DEFAULT '0',
  `wis` tinyint(3) NOT NULL DEFAULT '0',
  `hp` int(10) NOT NULL DEFAULT '0',
  `mp` int(10) NOT NULL DEFAULT '0',
  `sp` int(10) NOT NULL DEFAULT '0',
  `mr` tinyint(3) NOT NULL DEFAULT '0',
  `use_type` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;
