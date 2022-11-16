#ifndef _BPFTHANDLER_
#define _BPFTHANDLER_

#include <pthread.h>
#include <string>

#include "BpIPAddrHandler.h"

class BpApacheHandler;
class BpPeerHandler;

struct cmdNode {
	std::string cmdToRun;
	std::string cmdArgs[100];
	int cmdArgCount;
	std::string pidFile;
};

class BpFtHandler {
public:
	enum Status {
		NO_STATE = 0,   // Initially no state
		DOWN,
		UP
	};

	enum State {
		STARTUP = 0,   // Initial state
		INIT,          // Initialization completed
		ROLE_CHECK,    // During Role resolution
		SLEEP_ROLE_CHECK,
		RUNNING,

		// Fasih: The five states
		PRIMARY,
		SECONDARY,
		DEFUNCT,
		ACTIVE,
		DEAD

	};

	static const int C_PRIMARY = 10;
	static const int A_PRIMARY = 11;
	static const int C_SECONDARY = 20;
	static const int A_SECONDARY = 21;

	static const int APACHE_CHANGE = 0x1;
	static const int PEER_CHANGE = 0x2;
	static const int ROLE_CHANGE = 0x4;
	static const int STATE_CHANGE = 0x8;

public:
   static BpFtHandler &getInstance();
	int init(const char *cfgFile);

	bool getIsPrimary() {return isPrimary;}
	std::string getSelfIp() {return selfIpAddr;}
	std::string getSelfAgentIp() {return selfAgentIpAddr;}
	int getSelfPort() {return selfPort;}
	std::string getPeerIp() {return peerIpAddr;}
	std::string getPeerAgentIp() {return peerAgentIpAddr;}
	int getPeerPort() {return peerPort;}
	std::string getFloatingIp() {return floatingIpAddr;}
	std::string *getRefIps() {return refIpAddrs;}
	int getNumRefIps() {return numRefIps;}
	int getAgentPort() {return agentPort;}
	cmdNode *getCommands() {return cmdNodes;}
	int getNumberCommands() {return numberCommands;}
	
// 	std::string getCmdToRun() {return cmdToRun;}
// 	int getCmdArgsCount() {return cmdArgCount;}
// 	std::string *getCmdArgs() {return cmdArgs;}
// 	std::string getPidFile() {return pidFile;}
	
	// Apache and Peer status related methods
	void getPeerStatus(BpFtHandler::Status &oldStatus,
							 BpFtHandler::Status &curStatus);
	void updatePeerStatus(BpFtHandler::Status status);
	void getApacheStatus(BpFtHandler::Status &oldStatus,
								BpFtHandler::Status &curStatus);
	void updateApacheStatus(BpFtHandler::Status status);

	// Role related methods
	int getConfiguredRole();
	int getActualRole();
	void updateConfiguredRole(int role);
	void updateRole(int role);

	// State related methods
	BpFtHandler::State getState();
	void updateState(BpFtHandler::State aState);
	void makeDefunct();
	bool isDefunct();

	// mainChange related methods
	void updateMainChange(int change);
	bool isApacheChange();
	bool isStateChange();
	bool isPeerChange();
	bool isRoleChange();

	void setSignal();
	void raiseAlarm(const char* errorCode,const char* content);
	
private:
	BpFtHandler();
	~BpFtHandler();
	//BPInd17020 (Mayank)
	bool isPredicateFalse();
	void setPredicateTrue() ;
	void setPredicateFalse() ; 
	//BPInd17020 Ends
	int action();
	void checkRole();
	void setConfiguredRole();
	void changeRole(bool isPrimary);

private:
	std::string primaryIp;
	std::string secondaryIp;
	std::string primaryAgentIp;
	std::string secondaryAgentIp;
	int primaryPort;
	int secondaryPort;
        int predicateFlag ;//BPInd17020 (mayank) 	
	bool isPrimary;
	int _isDefunct;
   std::string selfIpAddr;
   std::string selfAgentIpAddr;
	int selfPort;
   std::string peerIpAddr;
   std::string peerAgentIpAddr;
	int peerPort;
   std::string floatingIpAddr;
   // Fasih: Instead of having a single ref ip...
   // There might be multiple ref ips corresponding to multiple interfaces....
   // For now I will assume that there are only two interfaces....
	int numRefIps;
   std::string refIpAddrs[2];
	std::string installRoot;
	
   int agentPort;   
// 	std::string cmdToRun;
// 	std::string cmdArgs[100];
// 	int cmdArgCount;
	struct cmdNode cmdNodes[100];
	int numberCommands;
	
	BpApacheHandler *apacheHdlr;
	BpPeerHandler *peerHdlr;
	
	// Apache and Peer Status related attributes
	Status oldApacheStatus;
	Status apacheStatus;

	Status oldPeerStatus;
	Status peerStatus;

	pthread_rwlock_t statusLock;

	// Main Thread Condition related attributes
	pthread_mutex_t mainLock;
	pthread_cond_t mainCond;
	int mainChange;
	
	// My state related attributes
	pthread_rwlock_t stateLock;
	BpFtHandler::State state;

	// My Role related attributes
	pthread_rwlock_t roleLock;
	int cRole;
	int aRole;

   static BpFtHandler *me;

	int readConfig(const char *cfgFile);
	bool isComment(const char *);
	void parseLine(const char *buf, char *&param, char *&value);

	
};

#endif
