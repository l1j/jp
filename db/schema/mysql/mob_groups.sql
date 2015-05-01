-- ----------------------------
-- Table structure for `mob_groups`
-- ----------------------------
DROP TABLE IF EXISTS `mob_groups`;
CREATE TABLE `mob_groups` (
  `id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `remove_group_if_leader_die` int(10) unsigned NOT NULL DEFAULT '0',
  `leader_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion1_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion1_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion2_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion2_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion3_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion3_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion4_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion4_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion5_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion5_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion6_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion6_count` int(10) unsigned NOT NULL DEFAULT '0',
  `minion7_id` int(10) unsigned NOT NULL DEFAULT '0',
  `minion7_count` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;
