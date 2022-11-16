#!/bin/ksh -x

UNAME=`uname`;
if [ $UNAME == "SunOS" ]
then
	PLATFORM="sol28g"
	CC="/opt/SUNWspro/bin/CC"
	FLAGS="-KPIC -G"
	LIB="-lkstat"
	JAVA_FLAG="/usr/java/include/solaris"
elif [ $UNAME == "Linux" ]
then
	PLATFORM="redhat80g"
	CC="/usr/bin/g++"
	FLAGS="-fPIC -shared"
	JAVA_FLAG="/usr/java/include/linux"
else
	echo "!! Cannot compile this code for $UNAME !!"
fi

# The header file for JNI has been compiled for the class
# 			com.baypackets.ase.ocm.CPU.java
# JAVAH="/usr/bin/javah"
# ${JAVAH} -jni -classpath <For_ASE.JAR> com.baypackets.ase.ocm.CPU

# Compile the native code alongwith JNI code to create the shared lib
${CC} -g ${FLAGS} cpu.c com_baypackets_ase_ocm_CPU.c -D${PLATFORM} -I/usr/java/include/ -I${JAVA_FLAG} -I. ${LIB} -o libCPU.so

# Compile a c test program for the shared lib
${CC} -g Test_libCPU.c -L. -lCPU -o Test_libCPU

# EOF
