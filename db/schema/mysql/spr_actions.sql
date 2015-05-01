-- ----------------------------
-- Table structure for spr_actions
-- ----------------------------
DROP TABLE IF EXISTS `spr_actions`;
CREATE TABLE `spr_actions` (
  `spr_id` int(10) unsigned NOT NULL,
  `act_id` int(10) unsigned NOT NULL,
  `frame_count` int(10) unsigned NOT NULL,
  `frame_rate` int(10) unsigned NOT NULL DEFAULT '24',
PRIMARY KEY (`spr_id`,`act_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
