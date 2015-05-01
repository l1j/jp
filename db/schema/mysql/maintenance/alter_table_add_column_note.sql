-- ----------------------------
-- auction_houses
-- ----------------------------
ALTER TABLE `auction_houses` ADD `note` varchar(255) DEFAULT NULL AFTER `house_id`;
UPDATE `auction_houses` T1, `houses` T2 SET T1.`note` = T2.`name` WHERE T1.`house_id` = T2.`id`;
-- ----------------------------
-- beginner_items
-- ----------------------------
ALTER TABLE `beginner_items` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `beginner_items` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `beginner_items` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `beginner_items` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
-- ----------------------------
-- drop_items
-- ----------------------------
ALTER TABLE `drop_items` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `drop_items` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `drop_items` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `drop_items` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
-- ----------------------------
-- drop_rates
-- ----------------------------
ALTER TABLE `drop_rates` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `drop_rates` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `drop_rates` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `drop_rates` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
-- ----------------------------
-- item_rates
-- ----------------------------
ALTER TABLE `item_rates` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `item_rates` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `item_rates` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `item_rates` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
-- ----------------------------
-- magic_dolls
-- ----------------------------
ALTER TABLE `magic_dolls` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `magic_dolls` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- npc_actions
-- ----------------------------
ALTER TABLE `npc_actions` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `npc_actions` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- npc_chats
-- ----------------------------
ALTER TABLE `npc_chats` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `npc_chats` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- pet_items
-- ----------------------------
ALTER TABLE `pet_items` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `pet_items` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `pet_items` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `pet_items` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
-- ----------------------------
-- pet_types
-- ----------------------------
ALTER TABLE `pet_types` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `pet_types` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- resolvents
-- ----------------------------
ALTER TABLE `resolvents` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `resolvents` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `resolvents` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `resolvents` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
-- ----------------------------
-- restart_locations
-- ----------------------------
ALTER TABLE `restart_locations` ADD `note` varchar(255) DEFAULT NULL AFTER `map_id`;
UPDATE `restart_locations` T1, `map_ids` T2 SET T1.`note` = T2.`name` WHERE T1.`map_id` = T2.`id`;
-- ----------------------------
-- return_locations
-- ----------------------------
ALTER TABLE `return_locations` ADD `note` varchar(255) DEFAULT NULL AFTER `scroll_escape`;
UPDATE `return_locations` T1, `map_ids` T2 SET T1.`note` = T2.`name` WHERE T1.`area_map_id` = T2.`id`;
-- ----------------------------
-- shops
-- ----------------------------
ALTER TABLE `shops` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `shops` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `shops` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `shops` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
-- ----------------------------
-- spawn_boss_mobs
-- ----------------------------
ALTER TABLE `spawn_boss_mobs` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `spawn_boss_mobs` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- spawn_doors
-- ----------------------------
ALTER TABLE `spawn_doors` ADD `note` varchar(255) DEFAULT NULL AFTER `map_id`;
UPDATE `spawn_doors` T1, `map_ids` T2 SET T1.`note` = T2.`name` WHERE T1.`map_id` = T2.`id`;
-- ----------------------------
-- spawn_mobs
-- ----------------------------
ALTER TABLE `spawn_mobs` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `spawn_mobs` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- spawn_npcs
-- ----------------------------
ALTER TABLE `spawn_npcs` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `spawn_npcs` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- spawn_times
-- ----------------------------
ALTER TABLE `spawn_times` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `spawn_times` T1, `spawn_mobs` T2 SET T1.`note` = T2.`note` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- spawn_traps
-- ----------------------------
ALTER TABLE `spawn_traps` ADD `note` varchar(255) DEFAULT NULL AFTER `trap_id`;
UPDATE `spawn_traps` T1, `traps` T2 SET T1.`note` = T2.`note` WHERE T1.`trap_id` = T2.`id`;
-- ----------------------------
-- spawn_ub_mobs
-- ----------------------------
ALTER TABLE `spawn_ub_mobs` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `spawn_ub_mobs` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- ub_managers
-- ----------------------------
ALTER TABLE `ub_managers` ADD `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
UPDATE `ub_managers` T1, `npcs` T2 SET T1.`note` = T2.`name` WHERE T1.`npc_id` = T2.`id`;
-- ----------------------------
-- weapon_skills
-- ----------------------------
ALTER TABLE `weapon_skills` ADD `note` varchar(255) DEFAULT NULL AFTER `item_id`;
UPDATE `weapon_skills` T1, `armors` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `weapon_skills` T1, `etc_items` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
UPDATE `weapon_skills` T1, `weapons` T2 SET T1.`note` = T2.`name` WHERE T1.`item_id` = T2.`id`;
