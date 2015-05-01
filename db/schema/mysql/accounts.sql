-- ----------------------------
-- Table structure for `accounts`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `accounts` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `access_level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `character_slot` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `last_activated_at` datetime DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
