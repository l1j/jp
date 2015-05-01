-- ----------------------------
-- Table structure for `chat_logs`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `chat_logs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` int(10) unsigned NOT NULL,
  `char_id` int(10) unsigned NOT NULL,
  `clan_id` int(10) unsigned NOT NULL,
  `map_id` int(10) unsigned NOT NULL,
  `loc_x` int(10) unsigned NOT NULL,
  `loc_y` int(10) unsigned NOT NULL,
  `type` int(10) NOT NULL,
  `target_account_id` int(10) unsigned DEFAULT NULL,
  `target_char_id` int(10) unsigned DEFAULT NULL,
  `target_clan_id` int(10) unsigned DEFAULT NULL,
  `target_map_id` int(10) unsigned DEFAULT NULL,
  `target_loc_x` int(10) unsigned DEFAULT NULL,
  `target_loc_y` int(10) unsigned DEFAULT NULL,
  `content` varchar(255) NOT NULL,
  `datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
