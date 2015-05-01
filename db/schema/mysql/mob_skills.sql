-- ----------------------------
-- Table structure for `mob_skills`
-- ----------------------------
DROP TABLE IF EXISTS `mob_skills`;
CREATE TABLE `mob_skills` (
  `npc_id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `act_no` int(10) unsigned NOT NULL DEFAULT '0',
  `type` int(10) unsigned NOT NULL DEFAULT '0',
  `tri_rnd` int(10) unsigned NOT NULL DEFAULT '0',
  `tri_hp` int(10) unsigned NOT NULL DEFAULT '0',
  `tri_companion_hp` int(10) unsigned NOT NULL DEFAULT '0',
  `tri_range` int(10) NOT NULL DEFAULT '0',
  `tri_count` int(10) NOT NULL DEFAULT '0',
  `change_target` int(10) unsigned NOT NULL DEFAULT '0',
  `range` int(10) unsigned NOT NULL DEFAULT '0',
  `area_width` int(10) unsigned NOT NULL DEFAULT '0',
  `area_height` int(10) unsigned NOT NULL DEFAULT '0',
  `leverage` int(10) unsigned NOT NULL DEFAULT '0',
  `skill_id` int(10) unsigned NOT NULL DEFAULT '0',
  `gfx_id` int(10) NOT NULL DEFAULT '-1',
  `act_id` int(10) unsigned NOT NULL DEFAULT '0',
  `summon_id` int(10) unsigned NOT NULL DEFAULT '0',
  `summon_min` int(10) NOT NULL DEFAULT '0',
  `summon_max` int(10) NOT NULL DEFAULT '0',
  `poly_id` int(10) unsigned NOT NULL DEFAULT '0',
  `chat_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`npc_id`,`act_no`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
