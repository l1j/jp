-- ----------------------------
-- Table structure for `character_quests`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_quests` (
  `char_id` int(10) unsigned NOT NULL,
  `quest_id` int(10) unsigned NOT NULL,
  `quest_step` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`char_id`,`quest_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
