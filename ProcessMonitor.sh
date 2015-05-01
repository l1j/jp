#!/bin/bash#!/bin/bash
################################################################################
# Create the L1J release build package
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
echo "Monitoring the L1J-Server process ..."

srvdir="~/l1jserver"

isAlive=`ps -ef | grep "ServerStart.sh" | wc -l`
if [ $isAlive = 1 ]; then
echo "Reboot the L1J-Server."
cd $srvdir
sh ServerStart.sh
fi
