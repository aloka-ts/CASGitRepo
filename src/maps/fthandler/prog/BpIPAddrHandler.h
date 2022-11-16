#ifndef _BPIPADDRHANDLER_
#define _BPIPADDRHANDLER_

#include <string>
#include <sys/types.h>
#include <inttypes.h>

#include "BpMachPing.h"

class BpIPAddrHandler
{
friend void * _monitorNetwork(void *args);
friend void * _monitorReferenceIPs(void *args);

public:
	static BpIPAddrHandler &getInstance();
	int initialize();
   int shutdown(void);

   int setSelfMode(bool abIsPrimary);
   bool isNetworkAvailable();
   bool getSelfMode(void);
   std::string getFloatingIPAddr();
	BpMachPing::BpMachStatus getPeerMachStatus();
    
private:
   int _changeExtIPAddr(bool abMode, const std::string& arIPAddr);
   int _sendCommand(const std::string& arPeerIP, int aiPeerPort, 
                    const std::string& arCmd);
	void _waitOnNetwork();
	int waitOnNetwork();
	void _pingReferenceIPs();
	int pingReferenceIPs();
	bool isIsolated();

   bool mbIsPrimary;
   std::string mSelfIPAddr;
	std::string mSelfAgentIPAddr;
	
   std::string mPeerIPAddr;
	std::string mPeerAgentIPAddr;
	
   std::string mFloatingIPAddr;
   int miAgentPort;
	// Fasih: Support for multiple interfaces.....
   std::string *mRefIPAddrs;
	int mNumRefIps;

   uint32_t _peerIP;
   uint32_t *_refIPs;

   BpMachPing::BpMachStatus _peerMachStatus;
   BpMachPing::BpMachStatus _refMachStatus;
	
	static BpIPAddrHandler *me;
private:
   BpIPAddrHandler(void);
   ~BpIPAddrHandler();
   struct {
   	bool isRaised;
	pthread_rwlock_t lock;
	} alarm;
};

#endif 
