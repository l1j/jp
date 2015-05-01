-- ----------------------------
-- Table structure for `shutdown_requests`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `shutdown_requests` (
  `char_id` int(10) unsigned NOT NULL,
  `char_name` varchar(255) NOT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `request_at` datetime NOT NULL,
  PRIMARY KEY (`char_id`),
  UNIQUE KEY `UNIQUE` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
