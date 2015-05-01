-- ----------------------------
-- Table structure for `race_tickets`
-- ----------------------------
DROP TABLE IF EXISTS `race_tickets`;
CREATE TABLE `race_tickets` (
  `item_obj_id` int(10) NOT NULL,
  `round` int(10) NOT NULL,
  `allotment_percentage` double NOT NULL,
  `victory` tinyint(1) NOT NULL,
  `runner_num` tinyint(3) NOT NULL,
  PRIMARY KEY (`item_obj_id`,`round`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
