#!/bin/ksh

export VERSION=REPLACE_VERSION
CURRDIR=`pwd`

#Execute if SAS profile is present
if [ -e $HOME/profile.sas.* ]
then
	. $HOME/profile.sas.*

	# Execute RollbackPatch.sh
	./SAS/rollbackPatch.sh
	if [ $? -ne 0 ]; then
  	  echo "Patch rollback for SAS has FAILED"
      cd $CURRDIR
      #exit 1
	fi

	cd $CURRDIR
fi


#Execute if MAPS profile is present
if [ -e $HOME/profile.maps.* ]
then
	. $HOME/profile.maps.*

	# Execute ARollbackPatch.sh
	./MAPS/rollbackPatch.sh ${VERSION}
	if [ $? -ne 0 ]; then
  	  echo "Patch rollback for MAPS has FAILED"
      cd $CURRDIR
      #exit 1
	fi

	cd $CURRDIR
fi

#Execute if INGW profile is present
if [ -e $HOME/profile.ingw ]
then
	# Execute RollbackPatch.sh
	INGW/rollbackPatch.sh $VERSION
fi


