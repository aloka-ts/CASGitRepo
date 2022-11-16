#!/bin/bash
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib
stop_ase.expect 0 23999
cd ../LOGS
rm *
cd ../scripts
sleep 10
ase_no_ems &
cd ../LOGS
sleep 35   	# For the secondary SAS as there is a 30 sec sleep time
#sleep 5	# For primary as the logs start after say 5 secs.
tail -f ASE*
