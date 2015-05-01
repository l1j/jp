-- ----------------------------
-- Table structure for `weapon_skills`
-- ----------------------------
DROP TABLE IF EXISTS `weapon_skills`;
CREATE TABLE `weapon_skills` (
  `item_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `probability` int(10) unsigned NOT NULL DEFAULT '0',
  `prob_enchant` int(10) NOT NULL DEFAULT '0',
  `fix_damage` int(10) unsigned NOT NULL DEFAULT '0',
  `random_damage` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_id` int(10) unsigned NOT NULL,
  `arrow_type` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `enable_mr` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `enable_attr_mr` tinyint(1) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`item_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;
