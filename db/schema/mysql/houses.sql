-- ----------------------------
-- Table structure for `houses`
-- ----------------------------
CREATE TABLE IF NOT EXISTS `houses` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `area` int(10) unsigned NOT NULL DEFAULT '0',
  `location` varchar(255) DEFAULT NULL,
  `npc_id` int(10) unsigned NOT NULL DEFAULT '0',
  `is_on_sale` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `is_purchase_basement` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `tax_deadline` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
