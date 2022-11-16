#ifndef _BPAPACHEHANDLER_
#define _BPAPACHEHANDLER_

#include <sys/types.h>
#include <string>
#include <pthread.h>

class BpApacheHandler;

struct waitPidData {
	BpApacheHandler *hdlr;
	pid_t pid;
};

struct monitorPidData 
{
	BpApacheHandler *hdlr;
	pid_t pids[100];
	int count;
};

class BpApacheHandler {
	friend void *_pingApache(void *args);
	friend void *_waitPid(void *args);

private:
	pid_t apachePid;
	pthread_t pingThread;
	
	bool doPing;

	int openPidFile();
	pid_t readPid(std::string pidFile);
	int closePidFile();
	void pingApache(monitorPidData *data);
	void waitPid(pid_t pid);
	
public:
	int init();
	int startApache();
	int monitorApache();
	void shutdown();

	BpApacheHandler();
	~BpApacheHandler();
};	

#endif
