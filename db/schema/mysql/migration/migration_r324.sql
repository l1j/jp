-- ----------------------------
-- accelerator_logs
-- ----------------------------
ALTER TABLE `accelerator_logs` MODIFY `loc_x` int(10) unsigned NOT NULL;
ALTER TABLE `accelerator_logs` MODIFY `loc_y` int(10) unsigned NOT NULL;
-- ----------------------------
-- accounts
-- ----------------------------
ALTER TABLE `accounts` MODIFY `last_activated_at` datetime DEFAULT NULL;
ALTER TABLE `accounts` MODIFY `ip` varchar(255) DEFAULT NULL;
ALTER TABLE `accounts` MODIFY `host` varchar(255) DEFAULT NULL;
-- ----------------------------
-- armors
-- ----------------------------
ALTER TABLE `armors` MODIFY `unidentified_name_id` varchar(255) NOT NULL;
ALTER TABLE `armors` MODIFY `identified_name_id` varchar(255) NOT NULL;
ALTER TABLE `armors` MODIFY `type` varchar(255) NOT NULL;
ALTER TABLE `armors` MODIFY `material` varchar(255) NOT NULL;
ALTER TABLE `armors` MODIFY `inv_gfx_id` int(10) unsigned NOT NULL;
ALTER TABLE `armors` MODIFY `grd_gfx_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- auction_houses
-- ----------------------------
ALTER TABLE `auction_houses` MODIFY `house_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- ban_ips
-- ----------------------------
ALTER TABLE `ban_ips` MODIFY `ip` varchar(255) NOT NULL;
ALTER TABLE `ban_ips` MODIFY `host` varchar(255) DEFAULT NULL;
-- ----------------------------
-- beginner_items
-- ----------------------------
ALTER TABLE `beginner_items` MODIFY `item_count` int(10) unsigned NOT NULL DEFAULT '1';
-- ----------------------------
-- castles
-- ----------------------------
ALTER TABLE `castles` MODIFY `id` tinyint(3) unsigned NOT NULL;
ALTER TABLE `castles` MODIFY `name` varchar(255) DEFAULT NULL;
-- ----------------------------
-- character_bookmarks
-- ----------------------------
ALTER TABLE `character_bookmarks` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `character_bookmarks` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `character_bookmarks` MODIFY `loc_x` int(10) unsigned NOT NULL;
ALTER TABLE `character_bookmarks` MODIFY `loc_y` int(10) unsigned NOT NULL;
ALTER TABLE `character_bookmarks` MODIFY `map_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- character_buddys
-- ----------------------------
ALTER TABLE `character_buddys` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `character_buddys` MODIFY `buddy_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- character_buffs
-- ----------------------------
ALTER TABLE `character_buffs` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `character_buffs` MODIFY `skill_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- character_configs
-- ----------------------------
ALTER TABLE `character_configs` MODIFY `char_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- character_quests
-- ----------------------------
ALTER TABLE `character_quests` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `character_quests` MODIFY `quest_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- character_skills
-- ----------------------------
ALTER TABLE `character_skills` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `character_skills` MODIFY `skill_id` int(10) unsigned NOT NULL;
ALTER TABLE `character_skills` MODIFY `skill_name` varchar(255) DEFAULT NULL;
ALTER TABLE `character_skills` MODIFY `is_active` tinyint(1) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `character_skills` MODIFY `active_time_left` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- characters
-- ----------------------------
ALTER TABLE `characters` MODIFY `level` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `characters` MODIFY `title` varchar(255) DEFAULT NULL;
ALTER TABLE `characters` MODIFY `clan_name` varchar(255) DEFAULT NULL;
ALTER TABLE `characters` MODIFY `IS_ACTIVE` BOOLEAN NOT NULL DEFAULT '1';
-- ----------------------------
-- clans
-- ----------------------------
ALTER TABLE `clans` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `clans` MODIFY `LEADER_ID` INT NOT NULL;
-- ----------------------------
-- commands
-- ----------------------------
ALTER TABLE `commands` MODIFY `name` varchar(255) NOT NULL;
ALTER TABLE `commands` MODIFY `class_name` varchar(255) DEFAULT NULL;
-- ----------------------------
-- door_gfxs
-- ----------------------------
ALTER TABLE `door_gfxs` MODIFY `direction` tinyint(1) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `door_gfxs` MODIFY `left_edge_offset` int(10) NOT NULL DEFAULT '0';
ALTER TABLE `door_gfxs` MODIFY `right_edge_offset` int(10) NOT NULL DEFAULT '0';
-- ----------------------------
-- drop_items
-- ----------------------------
ALTER TABLE `drop_items` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `drop_items` MODIFY `item_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- drop_rates
-- ----------------------------
ALTER TABLE `drop_rates` MODIFY `item_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- dungeons
-- ----------------------------
ALTER TABLE `dungeons` MODIFY `src_x` int(10) unsigned NOT NULL;
ALTER TABLE `dungeons` MODIFY `src_y` int(10) unsigned NOT NULL;
ALTER TABLE `dungeons` MODIFY `src_map_id` int(10) unsigned NOT NULL;
ALTER TABLE `dungeons` MODIFY `new_x` int(10) unsigned NOT NULL;
ALTER TABLE `dungeons` MODIFY `new_y` int(10) unsigned NOT NULL;
ALTER TABLE `dungeons` MODIFY `new_map_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- enchant_logs
-- ----------------------------
ALTER TABLE `enchant_logs` MODIFY `char_id` int(10) unsigned NOT NULL;
ALTER TABLE `enchant_logs` MODIFY `item_id` int(10) unsigned NOT NULL;
ALTER TABLE `enchant_logs` MODIFY `old_enchant_level` tinyint(3) NOT NULL DEFAULT '0';
ALTER TABLE `enchant_logs` MODIFY `new_enchant_level` tinyint(3) NOT NULL DEFAULT '0';
-- ----------------------------
-- etc_items
-- ----------------------------
ALTER TABLE `etc_items` MODIFY `name` varchar(255) NOT NULL;
ALTER TABLE `etc_items` MODIFY `unidentified_name_id` varchar(255) NOT NULL;
ALTER TABLE `etc_items` MODIFY `identified_name_id` varchar(255) NOT NULL;
ALTER TABLE `etc_items` MODIFY `item_type` varchar(255) NOT NULL;
ALTER TABLE `etc_items` MODIFY `use_type` varchar(255) NOT NULL;
ALTER TABLE `etc_items` MODIFY `material` varchar(255) NOT NULL;
ALTER TABLE `etc_items` MODIFY `inv_gfx_id` int(10) unsigned NOT NULL;
ALTER TABLE `etc_items` MODIFY `grd_gfx_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- houses
-- ----------------------------
ALTER TABLE `houses` MODIFY `id` int(10) unsigned NOT NULL;
ALTER TABLE `houses` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `houses` MODIFY `location` varchar(255) DEFAULT NULL;
ALTER TABLE `houses` MODIFY `is_on_sale` tinyint(1) unsigned NOT NULL DEFAULT '1';
-- ----------------------------
-- inns
-- ----------------------------
ALTER TABLE `inns` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `inns` MODIFY `key_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `inns` MODIFY `lodger_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `inns` MODIFY `hall` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- inventory_items
-- ----------------------------
ALTER TABLE `inventory_items` MODIFY `location` int(10) NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `item_count` int(10) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `inventory_items` MODIFY `is_equipped` tinyint(1) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `enchant_level` tinyint(3) NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `is_identified` tinyint(1) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `durability` int(10) NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `charge_count` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `charge_time` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `attr_enchant_kind` tinyint(3) NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `attr_enchant_level` tinyint(3) NOT NULL DEFAULT '0';
ALTER TABLE `inventory_items` MODIFY `ac` tinyint(3) NOT NULL DEFAULT '0';
-- ----------------------------
-- item_rates
-- ----------------------------
ALTER TABLE `item_rates` MODIFY `item_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- letters
-- ----------------------------
ALTER TABLE `letters` MODIFY `item_obj_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- mails
-- ----------------------------
ALTER TABLE `mails` MODIFY `ID` INT NOT NULL;
-- ----------------------------
-- map_ids
-- ----------------------------
ALTER TABLE `map_ids` MODIFY `id` int(10) unsigned NOT NULL;
ALTER TABLE `map_ids` MODIFY `start_x` int(10) unsigned NOT NULL;
ALTER TABLE `map_ids` MODIFY `end_x` int(10) unsigned NOT NULL;
ALTER TABLE `map_ids` MODIFY `start_y` int(10) unsigned NOT NULL;
ALTER TABLE `map_ids` MODIFY `end_y` int(10) unsigned NOT NULL;
ALTER TABLE `map_ids` MODIFY `monster_amount` float unsigned NOT NULL DEFAULT '1';
ALTER TABLE `map_ids` MODIFY `drop_rate` float unsigned NOT NULL DEFAULT '1';
-- ----------------------------
-- mob_groups
-- ----------------------------
ALTER TABLE `mob_groups` MODIFY `note` varchar(255) DEFAULT NULL;
-- ----------------------------
-- mob_skills
-- ----------------------------
ALTER TABLE `mob_skills` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `mob_skills` MODIFY `mob_name` varchar(255) DEFAULT NULL;
-- ----------------------------
-- npc_actions
-- ----------------------------
ALTER TABLE `npc_actions` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `npc_actions` MODIFY `normal_action` varchar(255) DEFAULT NULL;
ALTER TABLE `npc_actions` MODIFY `chaotic_action` varchar(255) DEFAULT NULL;
ALTER TABLE `npc_actions` MODIFY `teleport_url` varchar(255) DEFAULT NULL;
ALTER TABLE `npc_actions` MODIFY `teleport_urla` varchar(255) DEFAULT NULL;
-- ----------------------------
-- npc_chats
-- ----------------------------
ALTER TABLE `npc_chats` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `npc_chats` MODIFY `chat_id1` varchar(255) DEFAULT NULL;
ALTER TABLE `npc_chats` MODIFY `chat_id2` varchar(255) DEFAULT NULL;
ALTER TABLE `npc_chats` MODIFY `chat_id3` varchar(255) DEFAULT NULL;
ALTER TABLE `npc_chats` MODIFY `chat_id4` varchar(255) DEFAULT NULL;
ALTER TABLE `npc_chats` MODIFY `chat_id5` varchar(255) DEFAULT NULL;
-- ----------------------------
-- npcs
-- ----------------------------
ALTER TABLE `npcs` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `npcs` MODIFY `name_id` varchar(255) DEFAULT NULL;
ALTER TABLE `npcs` MODIFY `note` varchar(255) DEFAULT NULL;
ALTER TABLE `npcs` MODIFY `impl` varchar(255) NOT NULL;
ALTER TABLE `npcs` MODIFY `gfx_id` int(10) unsigned NOT NULL;
ALTER TABLE `npcs` MODIFY `level` int(10) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `npcs` MODIFY `size` varchar(10) DEFAULT NULL;
ALTER TABLE `npcs` MODIFY `family` varchar(255) DEFAULT NULL;
-- ----------------------------
-- pet_types
-- ----------------------------
ALTER TABLE `pet_types` MODIFY `tame_item_id` int(10) NOT NULL DEFAULT '0';
ALTER TABLE `pet_types` MODIFY `transform_item_id` int(10) NOT NULL NULL DEFAULT '0';
ALTER TABLE `pet_types` MODIFY `transform_npc_id` int(10) NOT NULL NULL DEFAULT '0';
-- ----------------------------
-- pets
-- ----------------------------
ALTER TABLE `pets` MODIFY `item_obj_id` int(10) unsigned NOT NULL;
ALTER TABLE `pets` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `pets` MODIFY `npc_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- polymorphs
-- ----------------------------
ALTER TABLE `polymorphs` MODIFY `GFX_ID` INT NOT NULL;
-- ----------------------------
-- random_dungeons
-- ----------------------------
ALTER TABLE `random_dungeons` MODIFY `src_x` int(10) unsigned NOT NULL;
ALTER TABLE `random_dungeons` MODIFY `src_y` int(10) unsigned NOT NULL;
ALTER TABLE `random_dungeons` MODIFY `src_map_id` int(10) unsigned NOT NULL;
ALTER TABLE `random_dungeons` MODIFY `note` varchar(255) DEFAULT NULL;
-- ----------------------------
-- resolvents
-- ----------------------------
ALTER TABLE `resolvents` MODIFY `item_id` int(10) NOT NULL;
-- ----------------------------
-- restart_locations
-- ----------------------------
ALTER TABLE `restart_locations` MODIFY `area` int(10) unsigned NOT NULL;
ALTER TABLE `restart_locations` MODIFY `loc_x` int(10) unsigned NOT NULL;
ALTER TABLE `restart_locations` MODIFY `loc_y` int(10) unsigned NOT NULL;
ALTER TABLE `restart_locations` MODIFY `map_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- shops
-- ----------------------------
ALTER TABLE `shops` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `shops` MODIFY `item_id` int(10) unsigned NOT NULL;
ALTER TABLE `shops` MODIFY `order_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- skills
-- ----------------------------
ALTER TABLE `skills` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `skills` MODIFY `target` varchar(255) DEFAULT NULL;
ALTER TABLE `skills` MODIFY `name_id` varchar(255) DEFAULT NULL;
-- ----------------------------
-- spawn_boss_mobs
-- ----------------------------
ALTER TABLE `spawn_boss_mobs` MODIFY `cycle_type` varchar(255) DEFAULT NULL;
ALTER TABLE `spawn_boss_mobs` MODIFY `npc_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- spawn_doors
-- ----------------------------
ALTER TABLE `spawn_doors` MODIFY `id` int(10) NOT NULL;
ALTER TABLE `spawn_doors` MODIFY `gfx_id` int(10) NOT NULL;
ALTER TABLE `spawn_doors` MODIFY `loc_x` int(10) NOT NULL;
ALTER TABLE `spawn_doors` MODIFY `loc_y` int(10) NOT NULL;
ALTER TABLE `spawn_doors` MODIFY `map_id` int(10) NOT NULL;
ALTER TABLE `spawn_doors` CHANGE `keeper` `keeper_id` int(10) NOT NULL DEFAULT '0';
-- ----------------------------
-- spawn_furnitures
-- ----------------------------
ALTER TABLE `spawn_furnitures` MODIFY `item_obj_id` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_furnitures` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_furnitures` MODIFY `loc_x` int(10) NOT NULL;
ALTER TABLE `spawn_furnitures` MODIFY `loc_y` int(10) NOT NULL;
ALTER TABLE `spawn_furnitures` MODIFY `map_id` int(10) NOT NULL;
-- ----------------------------
-- spawn_lights
-- ----------------------------
ALTER TABLE `spawn_lights` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_lights` MODIFY `loc_x` int(10) NOT NULL;
ALTER TABLE `spawn_lights` MODIFY `loc_y` int(10) NOT NULL;
ALTER TABLE `spawn_lights` MODIFY `map_id` int(10) NOT NULL;
-- ----------------------------
-- spawn_mobs
-- ----------------------------
ALTER TABLE `spawn_mobs` MODIFY `npc_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- spawn_npcs
-- ----------------------------
ALTER TABLE `spawn_npcs` MODIFY `npc_id` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_npcs` MODIFY `heading` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- spawn_times
-- ----------------------------
ALTER TABLE `spawn_times` MODIFY `delete_at_endtime` tinyint(1) unsigned NOT NULL DEFAULT '1';
-- ----------------------------
-- spawn_traps
-- ----------------------------
ALTER TABLE `spawn_traps` MODIFY `span` int(10) NOT NULL DEFAULT '0';
-- ----------------------------
-- spawn_ub_mobs
-- ----------------------------
ALTER TABLE `spawn_ub_mobs` MODIFY `ub_id` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_ub_mobs` MODIFY `pattern` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_ub_mobs` MODIFY `group_id` int(10) unsigned NOT NULL;
ALTER TABLE `spawn_ub_mobs` MODIFY `npc_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- spr_actions
-- ----------------------------
ALTER TABLE `spr_actions` MODIFY `frame_count` int(10) unsigned NOT NULL;
-- ----------------------------
-- towns
-- ----------------------------
ALTER TABLE `towns` MODIFY `id` int(10) unsigned NOT NULL;
ALTER TABLE `towns` MODIFY `name` varchar(255) DEFAULT NULL;
-- ----------------------------
-- traps
-- ----------------------------
ALTER TABLE `traps` MODIFY `id` int(10) unsigned NOT NULL;
ALTER TABLE `traps` MODIFY `gfx_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `is_detectionable` tinyint(1) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `base` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `dice` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `dice_count` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `poison_delay` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `poison_time` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `poison_damage` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `monster_npc_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `monster_count` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `teleport_x` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `teleport_y` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `teleport_map_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `skill_id` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `skill_time_seconds` int(10) unsigned NOT NULL DEFAULT '0';
ALTER TABLE `traps` MODIFY `switch_id` int(10) unsigned NOT NULL DEFAULT '0';
-- ----------------------------
-- ub_managers
-- ----------------------------
ALTER TABLE `ub_managers` MODIFY `ub_id` int(10) unsigned NOT NULL;
ALTER TABLE `ub_managers` MODIFY `npc_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- ub_times
-- ----------------------------
ALTER TABLE `ub_times` MODIFY `ub_id` int(10) unsigned NOT NULL;
ALTER TABLE `ub_times` MODIFY `ub_time` int(10) unsigned NOT NULL;
-- ----------------------------
-- ubs
-- ----------------------------
ALTER TABLE `ubs` MODIFY `id` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `name` varchar(255) DEFAULT NULL;
ALTER TABLE `ubs` MODIFY `map_id` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `area_x1` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `area_y1` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `area_x2` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `area_y2` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `min_level` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `max_level` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `max_player` int(10) unsigned NOT NULL;
ALTER TABLE `ubs` MODIFY `enter_royal` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_knight` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_wizard` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_elf` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_darkelf` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_dragonknight` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_illusionist` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_male` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `enter_female` tinyint(3) unsigned NOT NULL DEFAULT '1';
ALTER TABLE `ubs` MODIFY `use_pot` tinyint(3) unsigned NOT NULL DEFAULT '1';
-- ----------------------------
-- weapon_skills
-- ----------------------------
ALTER TABLE `weapon_skills` MODIFY `skill_id` int(10) unsigned NOT NULL;
-- ----------------------------
-- weapons
-- ----------------------------
ALTER TABLE `weapons` MODIFY `unidentified_name_id` varchar(255) NOT NULL;
ALTER TABLE `weapons` MODIFY `identified_name_id` varchar(255) NOT NULL;
ALTER TABLE `weapons` MODIFY `type` varchar(255) NOT NULL;
ALTER TABLE `weapons` MODIFY `material` varchar(255) NOT NULL;
ALTER TABLE `weapons` MODIFY `inv_gfx_id` int(10) unsigned NOT NULL;
ALTER TABLE `weapons` MODIFY `grd_gfx_id` int(10) unsigned NOT NULL;
