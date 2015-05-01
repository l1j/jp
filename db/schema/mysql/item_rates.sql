-- ----------------------------
-- Table structure for `item_rates`
-- ----------------------------
DROP TABLE IF EXISTS `item_rates`;
CREATE TABLE `item_rates` (
  `item_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `selling_price` float NOT NULL DEFAULT '-1',
  `purchasing_price` float NOT NULL DEFAULT '-1',
  PRIMARY KEY (`item_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
