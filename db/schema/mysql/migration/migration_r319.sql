-- ----------------------------
-- accounts
-- ----------------------------
UPDATE `accounts` SET `is_banned` = 2 WHERE `is_banned` = 1;
UPDATE `accounts` SET `is_banned` = 1 WHERE `is_banned` = 0;
UPDATE `accounts` SET `is_banned` = 0 WHERE `is_banned` = 2;
ALTER TABLE `accounts` CHANGE `is_banned` `is_active` tinyint(1) unsigned NOT NULL DEFAULT '1' after `host`;
ALTER TABLE `accounts` MODIFY `access_level` tinyint(3) unsigned NOT NULL DEFAULT '0' AFTER `password`;
ALTER TABLE `accounts` MODIFY `character_slot` tinyint(3) unsigned NOT NULL DEFAULT '0' AFTER `access_level`;
-- ----------------------------
-- armor_sets
-- ----------------------------
ALTER TABLE `armor_sets` CHANGE `note` `name` varchar(255) DEFAULT NULL;
-- ----------------------------
-- armors
-- ----------------------------
ALTER TABLE `armors` CHANGE `item_id` `id` int(10) unsigned NOT NULL;
ALTER TABLE `armors` CHANGE `inv_gfx` `inv_gfx_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `armors` CHANGE `grd_gfx` `grd_gfx_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `armors` CHANGE `use_mage` `use_wizard` tinyint(1) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- beginner_items
-- ----------------------------
ALTER TABLE `beginner_items` DROP `note`;
-- ----------------------------
-- castles
-- ----------------------------
ALTER TABLE `castles` CHANGE `castle_id` `id` tinyint(3) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- character_buddys
-- ----------------------------
ALTER TABLE `character_buddys` DROP `buddy_name`;
-- ----------------------------
-- character_buffs
-- ----------------------------
ALTER TABLE `character_buffs` CHANGE `char_obj_id` `char_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- character_configs
-- ----------------------------
ALTER TABLE `character_configs` CHANGE `object_id` `char_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- character_skills
-- ----------------------------
ALTER TABLE `character_skills` CHANGE `char_obj_id` `char_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- characters
-- ----------------------------
UPDATE `characters` SET `banned` = 2 WHERE `banned` = 1;
UPDATE `characters` SET `banned` = 1 WHERE `banned` = 0;
UPDATE `characters` SET `banned` = 0 WHERE `banned` = 2;
ALTER TABLE `characters` CHANGE `banned` `is_active` tinyint(1) unsigned NOT NULL DEFAULT '1';
-- ----------------------------
-- clans
-- ----------------------------
ALTER TABLE `clans` CHANGE `clan_id` `id` int(10) unsigned NOT NULL AUTO_INCREMENT;
ALTER TABLE `clans` CHANGE `clan_name` `name` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `clans` DROP `leader_name`;
-- ----------------------------
-- door_gfxs
-- ----------------------------
ALTER TABLE `door_gfxs` CHANGE `gfx_id` `id` int(10) unsigned NOT NULL;
ALTER TABLE `door_gfxs` CHANGE `note` `name` varchar(255) DEFAULT NULL;
-- ----------------------------
-- drop_items
-- ----------------------------
ALTER TABLE `drop_items` CHANGE `mob_id` `npc_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- drop_rates
-- ----------------------------
ALTER TABLE `drop_rates` DROP `note`;
-- ----------------------------
-- etc_items
-- ----------------------------
ALTER TABLE `etc_items` CHANGE `item_id` `id` int(10) unsigned NOT NULL;
ALTER TABLE `etc_items` CHANGE `inv_gfx` `inv_gfx_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `etc_items` CHANGE `grd_gfx` `grd_gfx_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- houses
-- ----------------------------
ALTER TABLE `houses` CHANGE `house_id` `id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `houses` CHANGE `house_name` `name` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `houses` CHANGE `house_area` `area` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- inn_keys
-- ----------------------------
ALTER TABLE `inn_keys` CHANGE `key_id` `id` int(10) unsigned NOT NULL;
-- ----------------------------
-- item_rates
-- ----------------------------
ALTER TABLE `item_rates` DROP `item_name`;
-- ----------------------------
-- letters
-- ----------------------------
ALTER TABLE `letters` CHANGE `item_object_id` `item_obj_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- magic_dolls
-- ----------------------------
ALTER TABLE `magic_dolls` DROP `note`;
-- ----------------------------
-- map_ids
-- ----------------------------
ALTER TABLE `map_ids` CHANGE `map_id` `id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `map_ids` CHANGE `location_name` `name` varchar(255) DEFAULT NULL;
-- ----------------------------
-- mob_skills
-- ----------------------------
ALTER TABLE `mob_skills` CHANGE `mob_id` `npc_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- npc_chats
-- ----------------------------
ALTER TABLE `npc_chats` DROP `note`;
-- ----------------------------
-- pet_items
-- ----------------------------
ALTER TABLE `pet_items` MODIFY `item_id` int(10) unsigned NOT NULL;
ALTER TABLE `pet_items` DROP `note`;
-- ----------------------------
-- pet_types
-- ----------------------------
ALTER TABLE `pet_types` CHANGE `base_npc_id` `npc_id` int(10) NOT NULL;
ALTER TABLE `pet_types` DROP `name`;
-- ----------------------------
-- pets
-- ----------------------------
ALTER TABLE `pets` CHANGE `obj_id` `id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `pets` MODIFY `name` varchar(255) NOT NULL DEFAULT '' after `id`;
-- ----------------------------
-- polymorphs
-- ----------------------------
ALTER TABLE `polymorphs` CHANGE `poly_id` `gfx_id` int(10) unsigned DEFAULT NULL;
-- ----------------------------
-- resolvents
-- ----------------------------
ALTER TABLE `resolvents` DROP `note`;
-- ----------------------------
-- restart_locations
-- ----------------------------
ALTER TABLE `restart_locations` DROP `note`;
-- ----------------------------
-- return_locations
-- ----------------------------
ALTER TABLE `return_locations` DROP `note`;
-- ----------------------------
-- shops
-- ----------------------------
ALTER TABLE `shops` DROP `npc_name`;
ALTER TABLE `shops` DROP `item_name`;
-- ----------------------------
-- skills
-- ----------------------------
ALTER TABLE `skills` CHANGE `skill_id` `skill_id_tmp` int(10) unsigned NOT NULL;
ALTER TABLE `skills` CHANGE `id` `skill_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `skills` CHANGE `skill_id_tmp` `id` int(10) unsigned NOT NULL;
-- ----------------------------
-- spawn_doors
-- ----------------------------
ALTER TABLE `spawn_doors` DROP `location`;
ALTER TABLE `spawn_doors` MODIFY `is_open` tinyint(1) NOT NULL DEFAULT '0';
-- ----------------------------
-- spawn_mobs
-- ----------------------------
ALTER TABLE `spawn_mobs` DROP `location`;
ALTER TABLE `spawn_mobs` CHANGE `npc_template_id` `npc_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- spawn_times
-- ----------------------------
ALTER TABLE `spawn_times` CHANGE `spawn_id` `npc_id` int(10) NOT NULL;
-- ----------------------------
-- spawn_traps
-- ----------------------------
ALTER TABLE `spawn_traps` DROP `note`;
-- ----------------------------
-- towns
-- ----------------------------
ALTER TABLE `towns` CHANGE `town_id` `id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- ub_managers
-- ----------------------------
ALTER TABLE `ub_managers` CHANGE `ub_manager_npc_id` `npc_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- weapon_skills
-- ----------------------------
ALTER TABLE `weapon_skills` CHANGE `weapon_id` `item_id` int(10) unsigned NOT NULL;
ALTER TABLE `weapon_skills` DROP `note`;
-- ----------------------------
-- weapons
-- ----------------------------
ALTER TABLE `weapons` CHANGE `item_id` `id` int(10) unsigned NOT NULL;
ALTER TABLE `weapons` CHANGE `inv_gfx` `inv_gfx_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `weapons` CHANGE `grd_gfx` `grd_gfx_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `weapons` CHANGE `use_mage` `use_wizard` tinyint(1) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- log_accelerator to accelerator_logs
-- ----------------------------
ALTER TABLE `log_accelerator` RENAME TO `accelerator_logs`;
ALTER TABLE `accelerator_logs` ADD `account_id` int(10) unsigned NOT NULL AFTER `id`;
UPDATE `accelerator_logs` T1, `accounts` T2 SET T1.`account_id` = T2.`id` WHERE T1.`account_name` = T2.`name`;
ALTER TABLE `accelerator_logs` DROP `account_name`;
ALTER TABLE `accelerator_logs` DROP `clan_name`;
ALTER TABLE `accelerator_logs` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `accelerator_logs` MODIFY `clan_id` int(10) unsigned NOT NULL;
ALTER TABLE `accelerator_logs` MODIFY `map_id` int(10) unsigned NOT NULL AFTER `clan_id`;
-- ----------------------------
-- house_auction to auction_houses
-- ----------------------------
ALTER TABLE `house_auction` RENAME TO `auction_houses`;
ALTER TABLE `auction_houses` DROP `house_name`;
ALTER TABLE `auction_houses` DROP `house_area`;
ALTER TABLE `auction_houses` DROP `location`;
ALTER TABLE `auction_houses` DROP `old_owner`;
ALTER TABLE `auction_houses` DROP `bidder`;
ALTER TABLE `auction_houses` CHANGE `old_owner_id` `owner_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- board to board_posts
-- ----------------------------
ALTER TABLE `board` RENAME TO `board_posts`;
ALTER TABLE `board_posts` MODIFY `id` int(10) unsigned NOT NULL AUTO_INCREMENT;
-- ----------------------------
-- log_chat to chat_logs
-- ----------------------------
ALTER TABLE `log_chat` RENAME TO `chat_logs`;
ALTER TABLE `chat_logs` ADD `account_id` int(10) unsigned NOT NULL AFTER `id`;
UPDATE `chat_logs` T1, `accounts` T2 SET T1.`account_id` = T2.`id` WHERE T1.`account_name` = T2.`name`;
ALTER TABLE `chat_logs` DROP `account_name`;
ALTER TABLE `chat_logs` DROP `name`;
ALTER TABLE `chat_logs` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `chat_logs` MODIFY `clan_id` int(10) unsigned NOT NULL;
ALTER TABLE `chat_logs` MODIFY `map_id` int(10) unsigned NOT NULL AFTER `clan_id`;
ALTER TABLE `chat_logs` ADD `target_account_id` int(10) unsigned NOT NULL AFTER `type`;
UPDATE `chat_logs` T1, `accounts` T2 SET T1.`target_account_id` = T2.`id` WHERE T1.`target_account_name` = T2.`name`;
ALTER TABLE `chat_logs` DROP `target_account_name`;
ALTER TABLE `chat_logs` DROP `target_name`;
ALTER TABLE `chat_logs` CHANGE `target_id` `target_char_id` int(10) unsigned NOT NULL;
ALTER TABLE `chat_logs` MODIFY `target_clan_id` int(10) unsigned NOT NULL;
ALTER TABLE `chat_logs` MODIFY `target_map_id` int(10) unsigned NOT NULL AFTER `target_clan_id`;
-- ----------------------------
-- log_enchant to enchant_logs
-- ----------------------------
ALTER TABLE `log_enchant` RENAME TO `enchant_logs`;
-- ----------------------------
-- npc to npcs
-- ----------------------------
ALTER TABLE `npc` RENAME TO `npcs`;
ALTER TABLE `npcs` CHANGE `npc_id` `id` int(10) unsigned NOT NULL;
-- ----------------------------
-- dungeon_random to random_dungeons
-- ----------------------------
ALTER TABLE `dungeon_random` RENAME TO `random_dungeons`;
-- ----------------------------
-- spawn_boss to spawn_boss_mobs
-- ----------------------------
ALTER TABLE `spawn_boss` RENAME TO `spawn_boss_mobs`;
ALTER TABLE `spawn_boss_mobs` DROP `location`;
-- ----------------------------
-- spawn_npc to spawn_npcs
-- ----------------------------
ALTER TABLE `spawn_npc` RENAME TO `spawn_npcs`;
ALTER TABLE `spawn_npcs` DROP `location`;
ALTER TABLE `spawn_npcs` CHANGE `npc_template_id` `npc_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- spawn_ub to spawn_ub_mobs
-- ----------------------------
ALTER TABLE `spawn_ub` RENAME TO `spawn_ub_mobs`;
ALTER TABLE `spawn_ub_mobs` MODIFY `id` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_ub_mobs` DROP `location`;
ALTER TABLE `spawn_ub_mobs` CHANGE `npc_template_id` `npc_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- ub_settings to ubs
-- ----------------------------
ALTER TABLE `ub_settings` RENAME TO `ubs`;
ALTER TABLE `ubs` CHANGE `ub_id` `id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `ubs` CHANGE `ub_name` `name` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `ubs` CHANGE `ub_map_id` `map_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `ubs` CHANGE `ub_area_x1` `area_x1` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `ubs` CHANGE `ub_area_y1` `area_y1` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `ubs` CHANGE `ub_area_x2` `area_x2` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `ubs` CHANGE `ub_area_y2` `area_y2` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `ubs` CHANGE `enter_mage` `enter_wizard` tinyint(3) unsigned NOT NULL DEFAULT '0';
