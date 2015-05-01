#!/bin/bash
################################################################################
# Install the L1J database
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
# Copyright (c) L1J-JP Project All Rights Reserved.
echo 'Install the database...'

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
# Create the database
################################################################################
echo 'Drop the database and Create the database.'
mysql -u $username -p$password < create_db.sql || { exit 1; }

################################################################################
# Create the tables
################################################################################
echo 'Create the tables...'
for f in schema/mysql/*.sql; do
  echo $f
  mysql -u $username -p$password -L $database < $f || { exit 1; }
done

################################################################################
# Store the CSV data
################################################################################
echo 'Store the CSV data...'
for f in $d/*.csv; do
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
echo 'Install is complete.'
exit 0
