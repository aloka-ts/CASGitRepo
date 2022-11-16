@echo off
rem ---- starting JBoss server for maps config -----
rem ---------------------------



@if not "%ECHO%" == ""  echo %ECHO%
@if "%OS%" == "Windows_NT"  setlocal

set DIRNAME=.\
if "%OS%" == "Windows_NT" set DIRNAME=%~dp0%
set PROGNAME=run.bat
if "%OS%" == "Windows_NT" set PROGNAME=%~nx0%

pushd %DIRNAME%..
set JBOSS_HOME=%CD%
popd

if exist "%JBOSS_HOME%\bin\run.bat" goto FOUND_RUN
echo Could not locate run file. Please check that you are in the
echo bin directory when running this script.
goto END



:FOUND_RUN

%JBOSS_HOME%\bin\run.bat -c default -b !HOST_IP_ADDRESS%
                                

:END

echo "DONE"


