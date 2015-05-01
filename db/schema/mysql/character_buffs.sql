-- ----------------------------
-- Table structure for `character_buffs`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_buffs` (
  `char_id` int(10) unsigned NOT NULL,
  `skill_id` int(10) unsigned NOT NULL,
  `remaining_time` int(10) NOT NULL DEFAULT '0',
  `poly_id` int(10) unsigned NOT NULL DEFAULT '0',
  `attr_kind` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`char_id`,`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
