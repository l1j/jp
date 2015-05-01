-- ----------------------------
-- Table structure for `auction_houses`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `auction_houses` (
  `house_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `deadline` datetime DEFAULT NULL,
  `price` int(10) unsigned NOT NULL DEFAULT '0',
  `owner_id` int(10) unsigned NOT NULL DEFAULT '0',
  `bidder_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
