#!/bin/ksh

export VERSION=REPLACE_VERSION
CURRDIR=`pwd`

#Execute if SAS profile is present
if [ -e $HOME/profile.sas.* ]
then
	# Execute ApplyPatch
	./SAS/applyPatch.sh
	if [ $? -ne 0 ]; then
  	  echo "Patch application for SAS has FAILED"
      cd $CURRDIR
      #exit 1
	fi

	cd $CURRDIR
fi


#Execute if MAPS profile is present
if [ -e $HOME/profile.maps.* ]
then
	. $HOME/profile.maps.*

	# Execute ApplyPatch
	./MAPS/applyPatch.sh ${VERSION}
	if [ $? -ne 0 ]; then
  	  echo "Patch application for MAPS has FAILED"
      cd $CURRDIR
      #exit 1
	fi

	cd $CURRDIR
fi

#Execute if INGW profile is present
if [ -e $HOME/profile.ingw ]
then
	# Execute ApplyPatch
	INGW/applyPatch.sh $VERSION
fi


