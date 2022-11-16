#!/bin/sh

#Find the Process ID for syncapp running instance


PID=`ps -eaf | grep ASE | grep -v grep | awk '{print $2}'`
if [[ "" !=  "$PID" ]]; then
  echo "killing $PID"
  kill -9 $PID
fi
