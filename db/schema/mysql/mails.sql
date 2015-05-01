-- ----------------------------
-- Table structure for `mails`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `mails` (
  `id` int(10) unsigned NOT NULL,
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `sender` varchar(255) DEFAULT NULL,
  `receiver` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `read_status` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `inbox_id` int(10) unsigned NOT NULL DEFAULT '0',
  `subject` blob,
  `content` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
