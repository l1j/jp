-- ----------------------------
-- Table structure for `beginner_items`
-- ----------------------------
DROP TABLE IF EXISTS `beginner_items`;
CREATE TABLE `beginner_items` (
  `id` int(10) unsigned NOT NULL,
  `item_id` int(10) unsigned NOT NULL DEFAULT '0',
  `note` varchar(255) DEFAULT NULL,
  `item_count` int(10) unsigned NOT NULL DEFAULT '1',
  `charge_count` int(10) unsigned NOT NULL DEFAULT '0',
  `enchant_level` tinyint(3) NOT NULL DEFAULT '0',
  `class_initial` varchar(1) NOT NULL DEFAULT 'A',
  PRIMARY KEY (`id`),
  KEY `class_initial` (`class_initial`) USING BTREE
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;
