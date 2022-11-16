#!/bin/ksh


if [ -d /opt/Baypackets/SystemMonitor ]
then
	echo "SystemMonitor link already exists"
else
	echo "SystemMonitor link doesn't exist. creating it..."
	ln -s /opt/Baypackets/SAS/SystemMonitor /opt/Baypackets/SystemMonitor
fi
