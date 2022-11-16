#*******************************************************************************
#   Copyright (c) 2014 Agnity, Inc. All rights reserved.
#   
#   This is proprietary source code of Agnity, Inc. 
#   
#   Agnity, Inc. retains all intellectual property rights associated 
#   with this source code. Use is subject to license terms.
#   
#   This source code contains trade secrets owned by Agnity, Inc.
#   Confidentiality of this computer program must be maintained at 
#   all times, unless explicitly authorized by Agnity, Inc.
#*******************************************************************************
#!/bin/ksh

export CLASSPATH=$CLASSPATH
. ../build/ant.properties

export JAVA_HOME
$THIRDPARTY/ANT/apache-ant-1.5.3/bin/ant $@

