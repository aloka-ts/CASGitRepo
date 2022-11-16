#!/bin/ksh

#Execute Profile 
. $HOME/profile.ingw

export VERSION=REPLACE_VERSION

# Check if BACKUP Directory present in $INSTALLROOT

if [ ! -e $INSTALLROOT/BACKUP ]
then
# No backup Directory Available
# No patch backup present
	echo ""
	echo "\033[1mNo backup Directory Available..\033[0m"
	echo "No backup to rollback. Exiting......"
	exit
fi

export BKPFILENAME=`echo "INGw"$VERSION"_Backup"`

# Check if backup is present
if [ -e $INSTALLROOT/BACKUP/$BKPFILENAME* ]
then
	echo ""
	echo "\033[1mBackup of binaries before INGw Patch $VERSION is Present..\033[0m"
else
	echo ""
	echo "\033[1mBackup of binaries before INGw Patch $VERSION is NOT Present..\033[0m"
	echo "No backup to rollback. Exiting......"
	exit
fi

# Copy the backup in $INSTALLROOT/INGw dir
CURPATH=`pwd`

mv $INSTALLROOT/BACKUP/$BKPFILENAME.tar.gz $INSTALLROOT/INGw/
cd $INSTALLROOT/INGw

# Change permission before applying patch
chmod -fR 750 sol28g/lib/* sol28g/bin/* sol28g/CommonLib/EmsLib/*

#Rollback the patch

gunzip $BKPFILENAME.tar.gz
tar -xf $BKPFILENAME.tar

# Remove the Backup
\rm $BKPFILENAME.tar
\rm $INSTALLROOT/BACKUP/rollbackPatch_$VERSION.sh


# Update patch history
echo "INGw Patch $VERSION Rollbacked  on " `/bin/date` >> $INSTALLROOT/BACKUP/.PatchHistory

# Go to initial directory
cd $CURPATH

## 
echo ""
echo "\033[1mINGw$VERSION Rollbacked Successfully\033[0m"
echo ""

