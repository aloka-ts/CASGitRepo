//============================================================================
// Name        : configmanager.h
// Author      : Sanjay Kumar
// Version     :
// Copyright   : 
// Description : manages the command line configuration needed  
//============================================================================

#include "configmanager.h"

using namespace std;

namespace AG {

  CConfigManager* CConfigManager::mInstance = NULL;
  bool CConfigManager::mIsCreated = false;

  CConfigManager* CConfigManager::getConfigManager(){
//cout<<"inside CConfigManager "<<endl;
    if ( ! mIsCreated ){
      if ( ! mInstance ){
        mInstance = new CConfigManager();
        mIsCreated = true;
      }
    }
//cout<<"returning from configmanager"<<endl;
    return mInstance;
  }

  void CConfigManager::initialize( int argc, char* aArgV[] )
	{
#ifndef PROMPT
		bool commandFound = false;
#endif

    if ( argc == 1  ){
      cout<<usage()<<endl;
      exit (3);
    }
    if (  argc == 2 && strcmp(aArgV[1], HELP_OPTION) == 0 ){
      cout<<usage()<<endl;
      exit (3);
    }
    for(int i=1; i < argc; i+=2 )
		{
      if( strcmp(aArgV[i], REMOTE_SERVER_OPTION ) == 0) 
			{
        if ( i+1 > argc - 1 ){
          cout<<endl;
          cout<<"INGw Server Not Specified"<<endl;
          cout<<"usage:"<<endl;
          cout<<usage()<<endl;
          exit( 3 );
        }
        mConfigMap.insert(pair<string,string>(REMOTE_SERVER_OPTION,aArgV[i+1])); 
      } 
			else if ( strcmp(aArgV[i], OUTPUT_FILE_OPTION) == 0 )
			{
        if ( i+1 > argc - 1 ){
          cout<<endl;
          cout<<"Log File Not Specified"<<endl;
          cout<<"usage:"<<endl;
          cout<<usage()<<endl;
          exit( 3 );
        }
        mConfigMap.insert(pair<string,string>(OUTPUT_FILE_OPTION, aArgV[i+1])); 
      } 
			else if(strcmp(aArgV[i], LOG_LEVEL) == 0)
			{
        if ( i+1 > argc - 1 ){
          cout<<endl;
          cout<<"-l 0|1"<<endl;
          exit( 3 );
        }

				if(strcmp(aArgV[i+1], "1") == 0)
					mIsLogOptionOn = true;
				else
					mIsLogOptionOn = false;

				cout << "Log level is: " << mIsLogOptionOn << endl;
			}
			else if(strcmp(aArgV[i], TELNET_PORT) == 0)
			{
        if ( i+1 > argc - 1 ){
          cout<<endl;
          cout<<"-p 5656"<<endl;
          exit( 3 );
        }
				mPort = atoi(aArgV[i+1]);
				cout << "Telnet port is: "<< mPort << endl;
			}
#ifndef PROMPT
			else if (strcmp(aArgV[i], COMMAND_INPUT) == 0)
			{
        if ( i+1 > argc - 1 )
				{
          cout<<endl;
          cout<<"Command Not Specified"<<endl;
          cout<<"usage:"<<endl;
          cout<<usage()<<endl;
          exit(3);
        }
        mConfigMap.insert(pair<string,string>(COMMAND_INPUT, aArgV[i+1])) ; 
        commandFound = true;
			}
#endif
			else 
			{
        cout<<"Invalid Option "<<aArgV[i]<<endl;
        exit(3);
      }
    }  

#ifndef PROMPT
		if(commandFound == false)
		{
			cout <<"Command not provided"<< endl;
			exit(3);
		}
#endif
  }
}
