::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Backup the L1J database
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
::
:: This program is free software: you can redistribute it and/or modify
:: it under the terms of the GNU General Public License as published by
:: the Free Software Foundation, either version 3 of the License, or
:: (at your option) any later version.
::
:: This program is distributed in the hope that it will be useful,
:: but WITHOUT ANY WARRANTY; without even the implied warranty of
:: MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
:: GNU General Public License for more details.
::
:: You should have received a copy of the GNU General Public License
:: along with this program.  If not, see <http://www.gnu.org/licenses/>.
::
:: Copyright (c) L1J-JP Project All Rights Reserved.
@echo off
echo Backup the database...

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: MySQL Config
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set database=l1jdb
set username=root
set password=

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: CSV Config
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set delimiter=,
set enclosed=
set newline=\r\n
set tmpdir=%~dp0
set outdir=%tmpdir:\=/%backup

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Date Format
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set tempdate=%date:/=%
set temptime=%time: =0%
set yyyy=%tempdate:~0,4%
set mm=%tempdate:~4,2%
set dd=%tempdate:~6,2%
set hh=%temptime:~0,2%
set ii=%temptime:~3,2%
set ss=%temptime:~6,2%
set datetime=%yyyy%-%mm%-%dd%_%hh%h%ii%m%ss%s

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Create dump directory
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
if not exist .\backup md .\backup
if not exist .\backup\%datetime% md .\backup\%datetime%

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump accelerator_logs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/accelerator_logs.csv
set query=^
SELECT 'id','account_id','char_id','clan_id','map_id','loc_x','loc_y','datetime' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/accelerator_logs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.accelerator_logs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump accounts table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/accounts.csv
set query=^
SELECT 'id','name','password','access_level','character_slot',^
'last_activated_at','ip','host','is_active' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/accounts.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.accounts
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump armor_sets table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/armor_sets.csv
set query=^
SELECT 'id','note','sets','poly_id','ac','hp','mp','hpr','mpr','str','dex',^
'con','wis','cha','int','sp','mr','damage_reduction','weight_reduction',^
'hit_modifier','dmg_modifier','bow_hit_modifier','bow_dmg_modifier',^
'defense_water','defense_wind','defense_fire','defense_earth','resist_stun',^
'resist_stone','resist_sleep','resist_freeze','resist_hold','resist_blind',^
'is_haste','exp_bonus','potion_recovery_rate' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/armor_sets.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.armor_sets
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump armors table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/armors.csv
set query=^
SELECT 'id','name','unidentified_name_id','identified_name_id','type',^
'material','grade','weight','inv_gfx_id','grd_gfx_id','item_desc_id','ac',^
'safe_enchant','use_royal','use_knight','use_wizard','use_elf','use_darkelf',^
'use_dragonknight','use_illusionist','str','con','dex','int','wis','cha','hp',^
'mp','hpr','mpr','sp','min_level','max_level','mr','is_haste',^
'damage_reduction','weight_reduction','hit_modifier','dmg_modifier',^
'bow_hit_modifier','bow_dmg_modifier','bless','tradable','deletable',^
'charge_time','expiration_time','defense_water','defense_wind','defense_fire',^
'defense_earth','resist_stun','resist_stone','resist_sleep','resist_freeze',^
'resist_hold','resist_blind','exp_bonus','potion_recovery_rate' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/armors.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.armors
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump auction_houses table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/auction_houses.csv
set query=^
SELECT 'house_id','note','deadline','price','owner_id','bidder_id' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/auction_houses.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.auction_houses
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump ban_ips table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/ban_ips.csv
set query=^
SELECT 'ip','host','maxk' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/ban_ips.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.ban_ips
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump beginner_items table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/beginner_items.csv
set query=^
SELECT 'id','item_id','note','item_count','charge_count','enchant_level','class_initial' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/beginner_items.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.beginner_items
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump board_posts table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/board_posts.csv
set query=^
SELECT 'id','name','date','title','content' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/board_posts.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.board_posts
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump castles table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/castles.csv
set query=^
SELECT 'id','name','war_time','tax_rate','public_money' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/castles.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.castles
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump character_bookmarks table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/character_bookmarks.csv
set query=^
SELECT 'id','char_id','name','loc_x','loc_y','map_id' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/character_bookmarks.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.character_bookmarks
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump character_buddys table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/character_buddys.csv
set query=^
SELECT 'id','char_id','buddy_id' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/character_buddys.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.character_buddys
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump character_buffs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/character_buffs.csv
set query=^
SELECT 'char_id','skill_id','remaining_time','poly_id','attr_kind' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/character_buffs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.character_buffs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump character_configs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/character_configs.csv
set query=^
SELECT 'char_id','length','data' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/character_configs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.character_configs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump character_quests table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/character_quests.csv
set query=^
SELECT 'char_id','quest_id','quest_step' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/character_quests.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.character_quests
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump character_skills table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/character_skills.csv
set query=^
SELECT 'id','char_id','skill_id','skill_name','is_active','active_time_left' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/character_skills.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.character_skills
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump characters table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/characters.csv
set query=^
SELECT 'id','account_id','name','birthday','level','high_level','exp','max_hp',^
'cur_hp','max_mp','cur_mp','ac','str','con','dex','cha','int','wis','status',^
'class','sex','type','heading','loc_x','loc_y','map_id','food','lawful',^
'title','clan_id','clan_name','clan_rank','bonus_status','elixir_status',^
'elf_attr','pk_count','pk_count_for_elf','exp_res','partner_id','access_level',^
'online_status','hometown_id','contribution','pay','hell_time','is_active',^
'karma','last_pk','last_pk_for_elf','delete_time','rejoin_clan_time',^
'original_str','original_con','original_dex','original_cha','original_int',^
'original_wis','use_additional_warehouse','logout_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/characters.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.characters
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump chat_logs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/chat_logs.csv
set query=^
SELECT 'id','account_id','char_id','clan_id','map_id','loc_x','loc_y','type',^
'target_account_id','target_char_id','target_clan_id','target_map_id',^
'target_loc_x','target_loc_y','content','datetime' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/chat_logs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.chat_logs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump clan_applies table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/clan_applies.csv
set query=^
SELECT 'id','clan_id','clan_name','char_name' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/clan_applies.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.clan_applies
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump clan_recommends table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/clan_recommends.csv
set query=^
SELECT 'clan_id','clan_name','char_name','clan_type','message' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/clan_recommends.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.clan_recommends
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump clan_warehouse_histories table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/clan_warehouse_histories.csv
set query=^
SELECT 'id','clan_id','char_name','type','item_name','item_count','record_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/clan_warehouse_histories.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.clan_warehouse_histories
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump clans table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/clans.csv
set query=^
SELECT 'id','name','leader_id','castle_id','house_id','created_at' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/clans.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.clans
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump commands table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/commands.csv
set query=^
SELECT 'name','access_level','class_name' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/commands.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.commands
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump cooking_ingredients table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/cooking_ingredients.csv
set query=^
SELECT 'id','cooking_recipe_id','item_id','amount' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/cooking_ingredients.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.cooking_ingredients
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump cooking_recipes table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/cooking_recipes.csv
set query=^
SELECT 'id','name','dish_id','dish_amount','fantasy_dish_id','fantasy_dish_amount' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/cooking_recipes.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.cooking_recipes
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump door_gfxs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/door_gfxs.csv
set query=^
SELECT 'id','note','direction','left_edge_offset','right_edge_offset' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/door_gfxs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.door_gfxs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump drop_items table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/drop_items.csv
set query=^
SELECT 'npc_id','item_id','note','min','max','chance' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/drop_items.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.drop_items
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump drop_rates table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/drop_rates.csv
set query=^
SELECT 'item_id','note','drop_rate','drop_amount','unique_rate' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/drop_rates.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.drop_rates
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump dungeons table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/dungeons.csv
set query=^
SELECT 'src_x','src_y','src_map_id','new_x','new_y','new_map_id','new_heading','note' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/dungeons.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.dungeons
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump enchant_logs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/enchant_logs.csv
set query=^
SELECT 'id','char_id','item_id','old_enchant_level','new_enchant_level' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/enchant_logs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.enchant_logs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump etc_items table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/etc_items.csv
set query=^
SELECT 'id','name','unidentified_name_id','identified_name_id','item_type',^
'use_type','material','weight','inv_gfx_id','grd_gfx_id','item_desc_id',^
'stackable','max_change_count','dmg_small','dmg_large','min_level','max_level',^
'loc_x','loc_y','map_id','bless','tradable','deletable','sealable','delay_id',^
'delay_time','delay_effect','food_volume','save_at_once','charge_time','expiration_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/etc_items.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.etc_items
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump houses table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/houses.csv
set query=^
SELECT 'id','name','area','location','npc_id','is_on_sale','is_purchase_basement','tax_deadline' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/houses.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.houses
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump inn_keys table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/inn_keys.csv
set query=^
SELECT 'item_obj_id','id','npc_id','hall','due_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/inn_keys.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.inn_keys
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump inns table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/inns.csv
set query=^
SELECT 'npc_id','note','room_number','key_id','lodger_id','hall','due_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/inns.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.inns
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump inventory_items table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/inventory_items.csv
set query=^
SELECT 'id','owner_id','location','item_id','item_count','is_equipped',^
'enchant_level','is_identified','durability','charge_count','charge_time',^
'expiration_time','last_used','is_sealed','is_protected','protect_item_id','attr_enchant_kind',^
'attr_enchant_level','ac','str','con','dex','wis','cha','int','hp','hpr','mp',^
'mpr','mr','sp','hit_modifier','dmg_modifier','bow_hit_modifier',^
'bow_dmg_modifier','defense_earth','defense_water','defense_fire',^
'defense_wind','resist_stun','resist_stone','resist_sleep','resist_freeze',^
'resist_hold','resist_blind','exp_bonus','is_haste','can_be_dmg','is_unique','porion_recovery_rate' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/inventory_items.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.inventory_items
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump item_rates table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/item_rates.csv
set query=^
SELECT 'item_id','note','selling_price','purchasing_price' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/item_rates.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.item_rates
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump magic_dolls table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/magic_dolls.csv
set query=^
SELECT 'item_id','npc_id','note','ac','str','con','dex','int','wis','cha','hp',^
'hpr','hpr_time','mp','mpr','mpr_time','mr','hit','dmg','dmg_chance','bow_hit',^
'bow_dmg','dmg_reduction','dmg_reduction_chance','dmg_evasion_chance',^
'weight_reduction','resist_stun','resist_stone','resist_sleep','resist_freeze',^
'resist_hold','resist_blind','exp_bonus','make_item_id','make_time','skill_id',^
'skill_chance','summon_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/magic_dolls.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.magic_dolls
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump mails table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/mails.csv
set query=^
SELECT 'id','type','sender','receiver','date','read_status','inbox_id',^
'subject','content' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/mails.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.mails
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump map_ids table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/map_ids.csv
set query=^
SELECT 'id','name','start_x','end_x','start_y','end_y','monster_amount',^
'drop_rate','unique_rate','underwater','makable','teleportable','escapable',^
'resurrection','painwand','penalty','take_pets','recall_pets','usable_item','usable_skill' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/map_ids.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.map_ids
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump map_timers table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/map_timers.csv
set query=^
SELECT 'char_id','map_id','area_id','enter_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/map_timers.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.map_timers
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump mob_groups table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/mob_groups.csv
set query=^
SELECT 'id','note','remove_group_if_leader_die','leader_id','minion1_id',^
'minion1_count','minion2_id','minion2_count','minion3_id','minion3_count',^
'minion4_id','minion4_count','minion5_id','minion5_count','minion6_id',^
'minion6_count','minion7_id','minion7_count' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/mob_groups.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.mob_groups
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump mob_skills table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/mob_skills.csv
set query=^
SELECT 'npc_id','note','act_no','type','tri_rnd','tri_hp','tri_companion_hp',^
'tri_range','tri_count','change_target','range','area_width','area_height',^
'leverage','skill_id','gfx_id','act_id','summon_id','summon_min','summon_max',^
'poly_id','chat_id' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/mob_skills.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.mob_skills
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump npc_actions table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/npc_actions.csv
set query=^
SELECT 'npc_id','note','normal_action','chaotic_action','teleport_url','teleport_urla' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/npc_actions.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.npc_actions
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump npc_chats table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/npc_chats.csv
set query=^
SELECT 'npc_id','note','chat_timing','start_delay_time','chat_id1','chat_id2',^
'chat_id3','chat_id4','chat_id5','chat_interval','is_shout','is_world_chat',^
'is_repeat','repeat_interval','game_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/npc_chats.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.npc_chats
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump npcs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/npcs.csv
set query=^
SELECT 'id','name','name_id','note','impl','gfx_id','level','hp','mp','ac',^
'str','con','dex','wis','int','mr','exp','lawful','size','weak_attr','ranged',^
'tamable','move_speed','atk_speed','alt_atk_speed','atk_magic_speed',^
'sub_magic_speed','undead','poison_atk','paralysis_atk','agro','agro_sosc',^
'agro_coi','family','agro_family','agro_gfx_id1','agro_gfx_id2','pickup_item',^
'digest_item','brave_speed','hpr_interval','hpr','mpr_interval','mpr',^
'teleport','random_level','random_hp','random_mp','random_ac','random_exp',^
'random_lawful','damage_reduction','hard','doppel','enable_tu','enable_erase',^
'bow_act_id','karma','transform_id','transform_gfx_id','light_size',^
'amount_fixed','change_head','cant_resurrect','is_equality_drop','boss' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/npcs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.npcs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump pet_items table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/pet_items.csv
set query=^
SELECT 'item_id','note','hit_modifier','dmg_modifier','ac','str','con','dex',^
'int','wis','hp','mp','sp','mr','use_type' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/pet_items.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.pet_items
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump pet_types table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/pet_types.csv
set query=^
SELECT 'npc_id','note','tame_item_id','min_hpup','max_hpup','min_mpup',^
'max_mpup','transform_item_id','transform_npc_id','message_id1','message_id2',^
'message_id3','message_id4','message_id5','defy_message_id','use_equipment' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/pet_types.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.pet_types
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump pets table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/pets.csv
set query=^
SELECT 'item_obj_id','id','name','npc_id','level','hp','mp','exp','lawful','food' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/pets.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.pets
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump polymorphs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/polymorphs.csv
set query=^
SELECT 'id','name','gfx_id','min_level','weapon_equip','armor_equip','can_use_skill','cause' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/polymorphs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.polymorphs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump race_tickets table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/race_tickets.csv
set query=^
SELECT 'item_obj_id','round','allotment_percentage','victory','runner_num' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/race_tickets.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.race_tickets
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump random_dungeons table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/random_dungeons.csv
set query=^
SELECT 'src_x','src_y','src_map_id','new_x1','new_y1','new_map_id1','new_x2',^
'new_y2','new_map_id2','new_x3','new_y3','new_map_id3','new_x4','new_y4',^
'new_map_id4','new_x5','new_y5','new_map_id5','new_heading','note' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/random_dungeons.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.random_dungeons
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump resolvents table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/resolvents.csv
set query=^
SELECT 'item_id','note','crystal_count' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/resolvents.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.resolvents
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump restart_locations table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/restart_locations.csv
set query=^
SELECT 'area','loc_x','loc_y','map_id','note' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/restart_locations.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.restart_locations
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump return_locations table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/return_locations.csv
set query=^
SELECT 'area_x1','area_y1','area_x2','area_y2','area_map_id','getback_x1',^
'getback_y1','getback_x2','getback_y2','getback_x3','getback_y3',^
'getback_map_id','getback_town_id','getback_town_id_elf',^
'getback_town_id_darkelf','scroll_escape','note' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/return_locations.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.return_locations
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump shops table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/shops.csv
set query=^
SELECT 'npc_id','item_id','note','order_id','pack_count' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/shops.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.shops
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump shutdown_requests table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/shutdown_requests.csv
set query=^
SELECT 'char_id','char_name','ip','datetime' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/shutdown_requests.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.shutdown_requests
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump skills table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/skills.csv
set query=^
SELECT 'id','name','skill_level','skill_number','consume_mp','consume_hp',^
'consume_item_id','consume_amount','reuse_delay','buff_duration','target',^
'target_to','damage_value','damage_dice','damage_dice_count',^
'probability_value','probability_dice','probability_max','attr','type',^
'lawful','ranged','area','through','skill_id','name_id','action_id','cast_gfx',^
'cast_gfx2','sys_msg_id_happen','sys_msg_id_stop','sys_msg_id_fail',^
'can_cast_with_invis','ignores_counter_magic','is_buff','impl' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/skills.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.skills
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_boss_mobs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_boss_mobs.csv
set query=^
SELECT 'id','npc_id','note','group_id','cycle_type','count','loc_x','loc_y',^
'random_x','random_y','loc_x1','loc_y1','loc_x2','loc_y2','heading','map_id',^
'respawn_screen','movement_distance','rest','spawn_type','percentage' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_boss_mobs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_boss_mobs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_doors table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_doors.csv
set query=^
SELECT 'id','map_id','note','gfx_id','loc_x','loc_y','hp','npc_id','is_open' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_doors.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_doors
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_furnitures table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_furnitures.csv
set query=^
SELECT 'item_obj_id','npc_id','loc_x','loc_y','map_id' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_furnitures.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_furnitures
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_lights table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_lights.csv
set query=^
SELECT 'id','npc_id','loc_x','loc_y','map_id' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_lights.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_lights
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_mobs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_mobs.csv
set query=^
SELECT 'id','npc_id','note','group_id','count','loc_x','loc_y','random_x',^
'random_y','loc_x1','loc_y1','loc_x2','loc_y2','heading','min_respawn_delay',^
'max_respawn_delay','map_id','respawn_screen','movement_distance','rest','near_spawn' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_mobs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_mobs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_npcs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_npcs.csv
set query=^
SELECT 'id','npc_id','note','count','loc_x','loc_y','random_x','random_y',^
'heading','respawn_delay','map_id','movement_distance' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_npcs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_npcs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_times table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_times.csv
set query=^
SELECT 'npc_id','note','time_start','time_end','delete_at_endtime' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_times.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_times
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_traps table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_traps.csv
set query=^
SELECT 'id','trap_id','note','map_id','loc_x','loc_y','loc_rnd_x','loc_rnd_y','count','span' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_traps.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_traps
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spawn_ub_mobs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spawn_ub_mobs.csv
set query=^
SELECT 'id','ub_id','pattern','group_id','npc_id','note','count','spawn_delay','seal_count' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spawn_ub_mobs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spawn_ub_mobs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump spr_actions table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/spr_actions.csv
set query=^
SELECT 'spr_id','act_id','frame_count','frame_rate' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/spr_actions.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.spr_actions
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump towns table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/towns.csv
set query=^
SELECT 'id','name','leader_id','tax_rate','tax_rate_reserved','sales_money',^
'sales_money_yesterday','town_tax','town_fix_tax' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/towns.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.towns
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump traps table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/traps.csv
set query=^
SELECT 'id','note','type','gfx_id','is_detectionable','base','dice',^
'dice_count','poison_type','poison_delay','poison_time','poison_damage',^
'monster_npc_id','monster_count','teleport_x','teleport_y','teleport_map_id',^
'skill_id','skill_time_seconds','switch_id' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/traps.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.traps
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump ub_managers table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/ub_managers.csv
set query=^
SELECT 'ub_id','npc_id','note' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/ub_managers.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.ub_managers
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump ub_times table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/ub_times.csv
set query=^
SELECT 'ub_id','ub_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/ub_times.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.ub_times
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump ubs table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/ubs.csv
set query=^
SELECT 'id','name','map_id','area_x1','area_y1','are_x2','area_y2',^
'min_level','max_level','max_player','enter_royal','enter_knight',^
'enter_wizard','enter_elf','enter_darkelf','enter_dragonknight',^
'enter_illusionist','enter_male','enter_female','use_pot','hpr_bonus','mpr_bonus' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/ubs.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.ubs
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump weapon_skills table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/weapon_skills.csv
set query=^
SELECT 'item_id','note','probability','prob_enchant','fix_damage',^
'random_damage','skill_id','arrow_type','enable_mr','enable_attr_mr' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/weapon_skills.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.weapon_skills
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Dump weapons table
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo %outdir%/%datetime%/weapons.csv
set query=^
SELECT 'id','name','unidentified_name_id','identified_name_id','type',^
'is_twohanded','material','weight','inv_gfx_id','grd_gfx_id','item_desc_id',^
'dmg_small','dmg_large','range','safe_enchant','use_royal','use_knight',^
'use_wizard','use_elf','use_darkelf','use_dragonknight','use_illusionist',^
'hit_modifier','dmg_modifier','str','con','dex','int','wis','cha','hp','mp',^
'hpr','mpr','sp','mr','is_haste','double_dmg_chance','weakness_exposure',^
'magic_dmg_modifier','can_be_dmg','min_level','max_level','bless','tradable',^
'deletable','charge_time','expiration_time' ^
UNION SELECT * INTO OUTFILE '%outdir%/%datetime%/weapons.csv' ^
FIELDS TERMINATED BY '%delimiter%' ENCLOSED BY '%enclosed%' ^
LINES TERMINATED BY '%newline%' ^
FROM %database%.weapons
mysql -u %username% -p%password% -e "%query%" --local-infile=1
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:END
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Backup is complete.
exit \b 0

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:ERR
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
pause
exit \b 1
