#!/bin/bash
################################################################################
# Update the L1J database
################################################################################
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# Copyright (c) 2012 L1J-JP Project All Rights Reserved.
echo 'Update the database...'

################################################################################
# MySQL Config
################################################################################
database='l1jdb'
username='root'
password=

################################################################################
# CSV Config
################################################################################
delimiter=','
enclosed=
newline='\r\n'
skipline=1

################################################################################
# Tables to be updated
################################################################################
tables=()
tables[0]='armor_sets'
tables[1]='armors'
tables[2]='beginner_items'
tables[3]='commands'
tables[4]='cooking_ingredients'
tables[5]='cooking_recipes'
tables[6]='door_gfxs'
tables[7]='drop_items'
tables[8]='drop_rates'
tables[9]='dungeons'
tables[10]='etc_items'
tables[11]='item_rates'
tables[12]='magic_dolls'
tables[13]='map_ids'
tables[14]='mob_groups'
tables[15]='mob_skills'
tables[16]='npc_actions'
tables[17]='npc_chats'
tables[18]='npcs'
tables[19]='pet_items'
tables[20]='pet_types'
tables[21]='polymorphs'
tables[22]='random_dungeons'
tables[23]='resolvents'
tables[24]='restart_locations'
tables[25]='return_locations'
tables[26]='shops'
tables[27]='skills'
tables[28]='spawn_boss_mobs'
tables[29]='spawn_doors'
tables[30]='spawn_lights'
tables[31]='spawn_mobs'
tables[32]='spawn_npcs'
tables[33]='spawn_times'
tables[34]='spawn_traps'
tables[35]='spawn_ub_mobs'
tables[36]='spr_actions'
tables[37]='traps'
tables[38]='ub_managers'
tables[39]='ub_times'
tables[40]='ubs'
tables[41]='weapon_skills'
tables[42]='weapons'

################################################################################
# Enter the CSV directory
################################################################################
while true; do
  d=
  echo 'CSV directory (Cancel: Press the Enter key without entering)'
  echo -n 'Enter directory: '
  read d
  if [ -z $d ]; then
    echo 'Canceled the install.'
    exit 1
  elif [ -d $d ]; then
    break
  fi
done

################################################################################
# Update the tables
################################################################################
echo 'Update the tables...'
for t in ${tables[@]}; do
  f="schema/mysql/$t.sql"
  echo $f
  mysql -u $username -p$password $database < $f || { exit 1; }
done

################################################################################
# Store the CSV data
################################################################################
echo 'Store the CSV data...'
for t in ${tables[@]}; do
  f="$d/$t.csv"
  echo $f
  mysqlimport -u $username -p$password -L $database $f \
  --fields-enclosed_by=$enclosed \
  --fields-terminated_by=$delimiter \
  --lines-terminated_by=$newline \
  --ignore-lines=$skipline \
  || { exit 1; }
done

################################################################################
# END
################################################################################
echo 'Update is complete.'
exit 0
