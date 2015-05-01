-- ----------------------------
-- Table structure for `ban_ips`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `ban_ips` (
  `ip` varchar(255) NOT NULL,
  `host` varchar(255) DEFAULT NULL,
  `mask` int(10) unsigned DEFAULT '32',
  PRIMARY KEY (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
