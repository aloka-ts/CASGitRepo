#!/bin/ksh

#Execute Profile 
. $HOME/profile.ingw

export VERSION=REPLACE_VERSION

# Check if BACKUP Directory present in $INSTALLROOT
FirstPatch=0
if [ ! -e $INSTALLROOT/BACKUP ]
then
# No backup Directory Available; 
# First patch is being applied
	mkdir -p $INSTALLROOT/BACKUP
	FirstPatch=1
fi

export BKPFILENAME=`echo "INGw"$VERSION"_Backup"`

# Check if patch already Installed
if [ $FirstPatch!=1 ]
then 
	if [ -e $INSTALLROOT/BACKUP/$BKPFILENAME* ]
	then
		echo ""
		echo "\033[1mINGw Patch $VERSION is already applied..\033[0m"
		echo "Rollback Patch before re-applying it"
	  echo "Apply Patch Failed. Exiting......"
		exit
	fi
fi

# Copy the new patch in INSTALLROOT
CURPATH=`pwd`

if [ ! -e $CURPATH/INGW/INGw$VERSION.tar ]
then
# No Patch present.
# Cannot go ahead
	echo "\033[1mINGw Patch $VERSION not present at $CURPATH/INGW..\033[0m"
	echo "Apply Patch Failed. Exiting......"
	exit
fi

cp $CURPATH/INGW/INGw$VERSION.tar $INSTALLROOT/INGw
cp $CURPATH/INGW/rollbackPatch.sh $INSTALLROOT/INGw

# Take backup of exisiting setup
cd $INSTALLROOT/INGw
tar cf $BKPFILENAME.tar sol28g/lib sol28g/bin sol28g/CommonLib/EmsLib
gzip $BKPFILENAME.tar

# Remove the last backup Directory
\rm $INSTALLROOT/BACKUP/INGw*_Backup.tar.gz
\rm $INSTALLROOT/BACKUP/rollbackPatch*

# Move backup in backup Directory
mv $BKPFILENAME.tar.gz $INSTALLROOT/BACKUP/
mv rollbackPatch.sh $INSTALLROOT/BACKUP/rollbackPatch_$VERSION.sh

# Change permission before applying patch
chmod -fR 750 sol28g/lib/* sol28g/bin/* sol28g/CommonLib/EmsLib/*

# Apply patch
tar -xf INGw$VERSION.tar

#Remove patch
\rm INGw$VERSION.tar

# Update patch history
echo "INGw Patch $VERSION Applied " `/bin/date` >> $INSTALLROOT/BACKUP/.PatchHistory

## 
echo ""
echo "\033[1mINGw$VERSION Patch Applied Successfully\033[0m"
echo ""
