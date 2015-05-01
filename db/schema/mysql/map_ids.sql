-- ----------------------------
-- Table structure for `map_ids`
-- ----------------------------
DROP TABLE IF EXISTS `map_ids`;
CREATE TABLE `map_ids` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `start_x` int(10) unsigned NOT NULL,
  `end_x` int(10) unsigned NOT NULL,
  `start_y` int(10) unsigned NOT NULL,
  `end_y` int(10) unsigned NOT NULL,
  `monster_amount` float unsigned NOT NULL DEFAULT '1',
  `drop_rate` float unsigned NOT NULL DEFAULT '1',
  `unique_rate` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `underwater` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `markable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `teleportable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `escapable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `resurrection` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `painwand` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `penalty` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `take_pets` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `recall_pets` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `usable_item` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `usable_skill` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
