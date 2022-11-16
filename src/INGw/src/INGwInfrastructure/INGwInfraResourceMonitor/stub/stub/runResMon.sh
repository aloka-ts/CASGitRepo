
export THIRDPARTY=/vob/thirdparty/
export INSTALLROOT=/home/ingw/install/sol28g/lib

export ACE_BUNDLE_HOME=$THIRDPARTY/TAO

LD_LIBRARY_PATH=/opt/SUNWspro/lib:/usr/local/lib:/usr/openwin/lib:/usr/lib:$INSTALLROOT/sol28g/lib:/opt/SUNWspro/WS6U1/lib:$ACE_BUNDLE_HOME/ACE/ACE_wrappers/ace:$LD_LIBRARY_PATH

./ResMon
