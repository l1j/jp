::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Install the L1J database
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
echo Install the database...

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
:: Enter the CSV directory
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:RETRY
echo Enter the CSV directory (Cancel: Press the Enter key without entering)
set D=
set /p D=^>
if not defined D goto :CANCEL
if not exist %D% goto RETRY

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Create the database
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Drop the database and Create the database.
mysql -u %username% -p%password% < .\create_db.sql
if errorlevel 1 goto ERR

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Create the tables
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Create the tables...
for %%F in (.\schema\mysql\*.sql) do (
  echo %%~fF
  mysql -u %username% -p%password% %database% < %%F
  if errorlevel 1 goto ERR
)

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:: Store the CSV data
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Store the CSV data...
for %%F in (%D%\*.csv) do (
  echo %%~fF
  mysqlimport -u %username% -p%password% -L %database% %%F ^
  --fields-enclosed_by=%enclosed% ^
  --fields-terminated_by=%delimiter% ^
  --lines-terminated_by=%newline% ^
  --ignore-lines=%skipline%
  if errorlevel 1 goto ERR
)

::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:END
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
echo Install is complete.
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
