::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Update the L1J database
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
:: Copyright (c) 2012 L1J-JP Project All Rights Reserved.
@echo off
echo Update the database...

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
set skipline=1

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Tables to be updated
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
set t[1]=armor_sets
set t[2]=armors
set t[3]=beginner_items
set t[4]=commands
set t[5]=cooking_ingredients
set t[6]=cooking_recipes
set t[7]=door_gfxs
set t[8]=drop_items
set t[9]=drop_rates
set t[10]=dungeons
set t[11]=etc_items
set t[12]=item_rates
set t[13]=magic_dolls
set t[14]=map_ids
set t[15]=mob_groups
set t[16]=mob_skills
set t[17]=npc_actions
set t[18]=npc_chats
set t[19]=npcs
set t[20]=pet_items
set t[21]=pet_types
set t[22]=polymorphs
set t[23]=random_dungeons
set t[24]=resolvents
set t[25]=restart_locations
set t[26]=return_locations
set t[27]=shops
set t[28]=skills
set t[29]=spawn_boss_mobs
set t[30]=spawn_doors
set t[31]=spawn_lights
set t[32]=spawn_mobs
set t[33]=spawn_npcs
set t[34]=spawn_times
set t[35]=spawn_traps
set t[36]=spawn_ub_mobs
set t[37]=spr_actions
set t[38]=traps
set t[39]=ub_managers
set t[40]=ub_times
set t[41]=ubs
set t[42]=weapon_skills
set t[43]=weapons
set max=43

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Enter the CSV directory
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:RETRY
echo Enter the CSV directory (Cancel: Press the Enter key without entering)
set D=
set /p D=^>
if not defined D goto :CANCEL
if not exist %D% goto RETRY

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Update the tables
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Update the tables...
setlocal ENABLEDELAYEDEXPANSION
for /L %%i in (1,1,%max%) do (
  call set F=.\schema\mysql\%%t[%%i]%%.sql
  echo !F!
  mysql -u %username% -p%password% %database% < !F!
  if errorlevel 1 goto ERR
)
endlocal

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Store the csv data
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Store the CSV data...
setlocal ENABLEDELAYEDEXPANSION
for /L %%i in (1,1,%max%) do (
  call set F=%D%\%%t[%%i]%%.csv
  echo !F!
  mysqlimport -u %username% -p%password% -L %database% !F! ^
  --fields-enclosed_by=%enclosed% ^
  --fields-terminated_by=%delimiter% ^
  --lines-terminated_by=%newline% ^
  --ignore-lines=%skipline%
  if errorlevel 1 goto ERR
)
endlocal

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:END
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Update is complete.
pause
exit \b 0

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:CANCEL
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Canceled the install.

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:ERR
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
pause
exit \b 1
