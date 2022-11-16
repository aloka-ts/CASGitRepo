#include <Util/Logger.h>
LOG("LoadBalancer");

#include <iostream.h>

#include <Util/LogMgr.h>

#include "BpFtHandler.h"

int
main(int argc, char **argv)
{
	if (5 != argc) {
		cout << "Illegal Command Line. "
           << "Executable to be run as lbFtHandler -cfgFile "
			  << "<CONFIGFILE> -logCfg <logCfgFile>"
           << endl;
      exit(0);
   }

	// Create  log Manager Instance
	LogMgr &logMgr = LogMgr::instance();
   logMgr.init(argv[4]);
	
	LogTrace(0, "Entering Main");

	// Initialize the LoadBalancer
	if (-1 == BpFtHandler::getInstance().init(argv[2])) {
		LogError(0, "Failed to initialize the Load Balancer");
		LogTrace(0, "Leaving Main");
		return -1;
	}

	LogTrace(0, "Leaving Main");
   return 0;
}
