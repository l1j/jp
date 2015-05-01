::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Create the L1J release build package
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
echo "Create the release build package..."

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Ant build
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
ant

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Copy the files
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
if not exist .\release-build md .\release-build
xcopy .\config .\release-build\config /D /E /I /-Y
xcopy .\data .\release-build\data /D /E /I /-Y
if not exist .\release-build\db md .\release-build\db
xcopy .\db\csv .\release-build\db\csv /D /E /I /-Y
xcopy .\db\schema .\release-build\db\schema /D /E /I /-Y
copy .\db\BackupDatabase.bat .\release-build\db\BackupDatabase.bat /Y
copy .\db\BackupDatabase.sh .\release-build\db\BackupDatabase.sh /Y
copy .\db\InstallDatabase.bat .\release-build\db\InstallDatabase.bat /Y
copy .\db\InstallDatabase.sh .\release-build\db\InstallDatabase.sh /Y
copy .\db\UpdateDatabase.bat .\release-build\db\UpdateDatabase.bat /Y
copy .\db\UpdateDatabase.sh .\release-build\db\UpdateDatabase.sh /Y
copy .\db\create_db.sql .\release-build\db\create_db.sql /Y
xcopy .\emblem .\release-build\emblem /D /E /I /Y
xcopy .\lib .\release-build\lib /D /E /I /Y
xcopy .\locale .\release-build\locale /D /E /I /Y
xcopy .\log .\release-build\log /D /E /I /Y
xcopy .\maps .\release-build\maps /D /E /I /Y
copy .\l1jserver.jar .\release-build\l1jserver.jar /Y
copy .\license.txt .\release-build\license.txt /Y
copy .\RebootServer.bat .\release-build\RebootServer.bat /Y
copy .\RebootServer.sh .\release-build\RebootServer.sh /Y
copy .\ServerStart.bat .\release-build\ServerStart.bat /Y
copy .\ServerStart.sh .\release-build\ServerStart.sh /Y
copy .\ServerDebug.bat .\release-build\ServerDebug.bat /Y
copy .\ServerDebug.sh .\release-build\ServerDebug.sh /Y

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:END
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo "Creating the release build package is complete."
pause
exit
