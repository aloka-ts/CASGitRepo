//============================================================================
// Name        : configmanager.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : manages the command line configuration needed  
//============================================================================

#ifndef __AG_CONF_MGR__
#define __AG_CONF_MGR__

#include <iostream>
#include <map>
#include <string>

using namespace std;

namespace AG {

const char REMOTE_SERVER_OPTION[] = "-s";
const char OUTPUT_FILE_OPTION[] = "-o";
const char HELP_OPTION[]   = "-h";
const char COMMAND_INPUT[] = "-c";
const char TELNET_PORT[]   = "-p";
const char LOG_LEVEL[]     = "-l";

class CConfigManager{
  public:
    static CConfigManager* getConfigManager();
    void initialize(int argc, char* aArgV[]);

    string usage(){
      string usage = "ingwprobe -s <INGw Server> [-o <outputfile>] [-h]"; 
      return usage;
    }

    map<string,string>& getConfigMap(){ return mConfigMap; }
    bool isLogOptionOn(){ return mIsLogOptionOn; }
		int  getTelnetPort(){ return mPort; }

  private:
    //Stores the configuration option and its value
    map<string,string> mConfigMap;

    CConfigManager(): mIsLogOptionOn(false), mPort(5656){}
    CConfigManager(const CConfigManager& d){}

    bool mIsInit;
    static bool mIsCreated;
    bool mIsLogOptionOn;
		int  mPort;

    static CConfigManager *mInstance;
};

}
#endif
