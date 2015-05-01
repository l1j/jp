#!/bin/bash
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

################################################################################
# Ant build
################################################################################
ant

################################################################################
# Copy the files
################################################################################
mkdir -p release-build
cp -ivR config release-build/config
cp -ivR data release-build/data
mkdir -p release-build/db
cp -ivR db/csv release-build/db/csv
cp -ivR db/schema release-build/db/schema
cp -fv db/BackupDatabase.bat release-build/db/BackupDatabase.bat
cp -fv db/BackupDatabase.sh release-build/db/BackupDatabase.sh
cp -fv db/InstallDatabase.bat release-build/db/InstallDatabase.bat
cp -fv db/InstallDatabase.sh release-build/db/InstallDatabase.sh
cp -fv db/UpdateDatabase.bat release-build/db/UpdateDatabase.bat
cp -fv db/UpdateDatabase.sh release-build/db/UpdateDatabase.sh
cp -fv db/create_db.sql release-build/db/create_db.sql
mkdir -p release-build/emblem
cp -fvR lib release-build/lib
cp -fvR locale release-build/locale
mkdir -p release-build/log
cp -fvR maps release-build/maps
cp -fv l1jserver.jar release-build/l1jserver.jar
cp -fv license.txt release-build/license.txt
cp -fv RebootServer.bat release-build/RebootServer.bat
cp -fv RebootServer.sh release-build/RebootServer.sh
cp -fv ServerStart.bat release-build/ServerStart.bat
cp -fv ServerStart.sh release-build/ServerStart.sh
cp -fv ServerDebug.bat release-build/ServerDebug.bat
cp -fv ServerDebug.sh release-build/ServerDebug.sh
