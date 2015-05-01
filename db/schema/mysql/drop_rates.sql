-- ----------------------------
-- Table structure for `drop_rates`
-- ----------------------------
DROP TABLE IF EXISTS `drop_rates`;
CREATE TABLE `drop_rates` (
  `item_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `drop_rate` float unsigned NOT NULL DEFAULT '1',
  `drop_amount` float unsigned NOT NULL DEFAULT '1',
  `unique_rate` float unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`item_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
