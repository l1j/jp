-- ----------------------------
-- Table structure for `door_gfxs`
-- ----------------------------
DROP TABLE IF EXISTS `door_gfxs`;
CREATE TABLE `door_gfxs` (
  `id` int(10) unsigned NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `direction` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `left_edge_offset` int(10) NOT NULL DEFAULT '0',
  `right_edge_offset` int(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
