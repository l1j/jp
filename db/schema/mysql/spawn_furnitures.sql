-- ----------------------------
-- Table structure for `spawn_furnitures`
-- ----------------------------
DROP TABLE IF EXISTS `spawn_furnitures`;
CREATE TABLE `spawn_furnitures` (
  `item_obj_id` int(10) unsigned NOT NULL,
  `npc_id` int(10) unsigned NOT NULL,
  `loc_x` int(10) NOT NULL,
  `loc_y` int(10) NOT NULL,
  `map_id` int(10) NOT NULL,
  PRIMARY KEY (`item_obj_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
