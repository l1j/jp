-- ----------------------------
-- Table structure for `accelerator_logs`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `accelerator_logs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` int(10) unsigned NOT NULL,
  `char_id` int(10) unsigned NOT NULL,
  `clan_id` int(10) unsigned NOT NULL,
  `map_id` int(10) unsigned NOT NULL,
  `loc_x` int(10) unsigned NOT NULL,
  `loc_y` int(10) unsigned NOT NULL,
  `datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
