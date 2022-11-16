ECHO OFF

SET INSTALLROOT=INSTALL_ROOT
SET ASE_HOME=%INSTALLROOT%\ASESubsystem
SET PATH=%PATH%;%JAVA_HOME%\bin
cd %INSTALLROOT%

REM ###Create the directories required.....
md %ASE_HOME%\tmp\rulecomp
SET LOG_DIR=%ASE_HOME%\..\LOGS
md %LOG_DIR%

REM ### Set the CLASSPATH
SUBST I: %ASE_HOME%\bpjars
SET BP_CP=I:\bootstrap.jar

SUBST J: %JAVA_HOME%\lib
SET JAVA_CP=J:\tools.jar

SET CATALINA_HOME=%ASE_HOME%\Common\thirdParty\jakarta-tomcat

SET DEBUG_OPTS=-Xdebug -Xnoagent -debug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n,stdalloc=y

REM ### Run the ASEServer
java %DEBUG_OPTS% -classpath %BP_CP%;%JAVA_CP% -Djava.security.auth.login.config=%ASE_HOME%\conf\jaas.config -Dase.home=%ASE_HOME% -Dhttp.container.home=%CATALINA_HOME% -Dcatalina.home=%CATALINA_HOME% -DIsEmsManaged=false com.baypackets.ase.startup.Bootstrap

SUBST /D I:
SUBST /D J:
