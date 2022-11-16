echo off
if "%1"=="" goto usuage  

if NOT "%3"=="5.8" if NOT "%3"=="5.10" goto usuage


set PARENT_BASE="%cd%"

set INSALLROOT=%1
set TARGET=%2

set PARENT_BASE_1= %PARENT_BASE%\..\temp
set CAS_THIRDPARTY_ROOT= %PARENT_BASE%\..\..\..\thirdPartyCASGitRepo7.5\
set CAS_STACK_ROOT=%PARENT_BASE%\..\..\..\CASStackGitRepo7.5
set BAYPROCESSOR_ROOT=%PARENT_BASE%\..\..\..\BayProcessorGitRepo
set EMS_COMMON_ROOT=%PARENT_BASE%\..\..\..\CommonEMSGitRepo
set ANT_HOME=
set JAVA_HOME=
set DS_HOME=%CAS_STACK_ROOT%\stacks\SIPStack\DySIPUAJava_6.4\src\dsua
set CAP_HOME=%PARENT_BASE%\tcap\cap
set BN_HOME=%CAS_THIRDPARTY_ROOT%\BinaryNotes1.5.2

echo -----------------------------------------------
echo Using INSTALLROOT= %INSTALLROOT%
echo Using JAVA_HOME= %JAVA_HOME%
echo Using ANT_HOME= %ANT_HOME%
echo Using DS_HOME= %DS_HOME%
echo Using CAP_HOME= %CAP_HOME%
echo Using BN_HOME= %BN_HOME%
echo Using CAS_THIRDPARTY_ROOT= %CAS_THIRDPARTY_ROOT%
echo Using CAS_STACK_ROOT= %CAS_STACK_ROOT%
echo Using BAYPROCESSOR_ROOT= %BAYPROCESSOR_ROOT%
echo Using EMS_COMMON_ROOT= %EMS_COMMON_ROOT%
echo -----------------------------------------------
echo ""

echo ANT TARGET to execute: %TARGET%

%ANT_HOME%\bin\ant -DJAVA_HOME=%JAVA_HOME% -DINSTALLROOT=%INSTALLROOT% -DPARENT_BASE=%PARENT_BASE% -DDS_HOME=%DS_HOME% -DCAP_HOME=%CAP_HOME% -DBN_HOME=%BN_HOME% -DTHIRDPARTY=%CAS_THIRDPARTY_ROOT% -DCAS_STACK_ROOT=%CAS_STACK_ROOT% -DBAYPROCESSOR_ROOT=%BAYPROCESSOR_ROOT% -DEMS_COMMON_ROOT=%EMS_COMMON_ROOT% -v -f build.xml %TARGET%



:usuage
echo Usage: $0 [INSTALLROOT] [<Target>]
