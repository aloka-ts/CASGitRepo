@echo off
rem ---- starting JBoss server for maps config -----
rem ---------------------------



@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=shutdown.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

pushd %DIRNAME%..
set JBOSS_HOME=%CD%
popd

if exist "%JBOSS_HOME%\bin\shutdown.bat" goto FOUND_RUN
echo Could not locate run file. Please check that you are in the
echo bin directory when running this script.
goto END

:FOUND_RUN



%JBOSS_HOME%\bin\shutdown.bat -u admin -p admin -S -s !HOST_IP_ADDRESS%
                                                 

:END

echo "DONE"


