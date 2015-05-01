-- ----------------------------
-- armor_sets
-- ----------------------------
ALTER TABLE `armor_sets` CHANGE `name` `note` varchar(255) DEFAULT NULL;
-- ----------------------------
-- clans
-- ----------------------------
ALTER TABLE `clans` CHANGE `has_castle` `castle_id` int(10) unsigned NOT NULL;
ALTER TABLE `clans` CHANGE `has_house` `house_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- door_gfxs
-- ----------------------------
ALTER TABLE `door_gfxs` CHANGE `name` `note` varchar(255) DEFAULT NULL;
-- ----------------------------
-- houses
-- ----------------------------
ALTER TABLE `houses` CHANGE `keeper_id` `npc_id` int(10) unsigned NOT NULL AFTER `id`;
-- ----------------------------
-- inns
-- ----------------------------
ALTER TABLE `inns` CHANGE `name` `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
-- ----------------------------
-- magic_dolls
-- ----------------------------
ALTER TABLE `magic_dolls` CHANGE `doll_id` `npc_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- mob_skills
-- ----------------------------
ALTER TABLE `mob_skills` CHANGE `mob_name` `note` varchar(255) DEFAULT NULL AFTER `npc_id`;
-- ----------------------------
-- spawn_boss_mobs
-- ----------------------------
ALTER TABLE `spawn_boss_mobs` MODIFY `npc_id` int(10) unsigned NOT NULL AFTER `id`;
ALTER TABLE `spawn_boss_mobs` MODIFY `group_id` int(10) unsigned NOT NULL DEFAULT '0' AFTER `npc_id`;
-- ----------------------------
-- spawn_doors
-- ----------------------------
ALTER TABLE `spawn_doors` MODIFY `map_id` int(10) unsigned NOT NULL AFTER `id`;
ALTER TABLE `spawn_doors` CHANGE `keeper_id` `npc_id` int(10) unsigned NOT NULL AFTER `id`;
-- ----------------------------
-- spawn_mobs
-- ----------------------------
ALTER TABLE `spawn_mobs` MODIFY `count` int(10) unsigned NOT NULL DEFAULT '0' AFTER `group_id`;
-- ----------------------------
-- spawn_npcs
-- ----------------------------
ALTER TABLE `spawn_npcs` MODIFY `npc_id` int(10) unsigned NOT NULL AFTER `id`;
-- ----------------------------
-- towns
-- ----------------------------
ALTER TABLE `towns` DROP `leader_name`;
