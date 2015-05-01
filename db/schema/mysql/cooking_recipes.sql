-- ----------------------------
-- Table structure for `cooking_recipes`
-- ----------------------------
DROP TABLE IF EXISTS `cooking_recipes`;
CREATE TABLE `cooking_recipes` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `dish_id` int(10) unsigned NOT NULL,
  `dish_amount` int(10) unsigned NOT NULL,
  `fantasy_dish_id` int(10) unsigned NOT NULL,
  `fantasy_dish_amount` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
