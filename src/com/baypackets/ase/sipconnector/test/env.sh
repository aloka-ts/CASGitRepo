#!/bin/ksh

export INSTALLROOT=/home/vmishra/work/builds/SipServlet

export ASE_CLASSPATH=$INSTALLROOT:$INSTALLROOT/bpjars/ase.jar:\
$INSTALLROOT/dsjars/JainSipApi1.0.jar:\
$INSTALLROOT/dsjars/dsua.jar:\
$INSTALLROOT/dsjars/mime.jar:\
$INSTALLROOT/dsjars/xmlParserAPIs.jar:\
$INSTALLROOT/dsjars/concurrent.jar:\
$INSTALLROOT/dsjars/flexlm.jar:\
$INSTALLROOT/dsjars/trove.jar:\
$INSTALLROOT/dsjars/xercesImpl.jar:\
$INSTALLROOT/dsjars/log4j.jar:\
$INSTALLROOT/otherjars/servlet-2.4.jar:\
$INSTALLROOT/otherjars/sipservlet.jar

#$INSTALLROOT/otherjars/commons-beanutils.jar:\
#$INSTALLROOT/otherjars/ojdbc14.jar:\
#$INSTALLROOT/otherjars/commons-collections-3.1.jar:\
#$INSTALLROOT/otherjars/commons-digester.jar:\
#$INSTALLROOT/otherjars/commons-logging-api.jar:\
#$INSTALLROOT/otherjars/xalan-jboss.jar:\
#$INSTALLROOT/otherjars/commons-logging.jar:\
#$INSTALLROOT/otherjars/xalan.jar:\
#$INSTALLROOT/otherjars/junit.jar:\
#$INSTALLROOT/otherjars/xerces.jar:\
#$INSTALLROOT/otherjars/log4j-1.2.8.jar:\
#$INSTALLROOT/otherjars/xerces1.jar:\
#$INSTALLROOT/otherjars/mail.jar:\
#$INSTALLROOT/otherjars/xmlparser.jar:\
#$INSTALLROOT/dsjars/dslicense.jar:\

export JAVA_HOME=/usr/java

