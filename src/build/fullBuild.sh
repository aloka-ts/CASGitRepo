#!/bin/ksh


echo "-----------------------------------------------"
#export FEE_BUILD_ROOT=/AGNITY/aconyx/GitRepo/FEE_BUILD/
#export MPH_BUILD_ROOT=/home/reeta/GitRepo/MPHGitRepo/build/libs/
./build.sh  ~/CAS_ROGERS
./build.sh  ~/CAS_ROGERS ra-radius
./build.sh  ~/CAS_ROGERS ra-http
./build.sh  ~/CAS_ROGERS ra-ro
#./build.sh  ~/CAS_ROGERS ra-gy
#./build.sh  ~/CAS_ROGERS ra-rf
./build.sh  ~/CAS_ROGERS ra-sh
./build.sh  ~/CAS_ROGERS ra-telnetssh
./build.sh  ~/CAS_ROGERS ra-smpp
./build.sh  ~/CAS_ROGERS isup
./build.sh  ~/CAS_ROGERS cdrutility
./build.sh  ~/CAS_ROGERS atclient
./build.sh  ~/CAS_ROGERS win
./build.sh  ~/CAS_ROGERS inapitu-t_cs2
./build.sh  ~/CAS_ROGERS map
./build.sh  ~/CAS_ROGERS ain
./build.sh  ~/CAS_ROGERS capv2
./build.sh  ~/CAS_ROGERS simulator
./createPatch.sh  ~/CAS_ROGERS 10.1.0.36.11
