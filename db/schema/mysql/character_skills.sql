-- ----------------------------
-- Table structure for `character_skills`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_skills` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `char_id` int(10) unsigned NOT NULL,
  `skill_id` int(10) unsigned NOT NULL,
  `skill_name` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `active_time_left` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE` (`char_id`,`skill_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
