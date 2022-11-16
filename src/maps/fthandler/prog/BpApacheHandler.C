#include <Util/Logger.h>
LOG("LoadBalancer");

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>

#include "BpFtHandler.h"
#include "BpApacheHandler.h"

void *
_pingApache(void *args) {
	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());
	
	monitorPidData *data = (monitorPidData *)args;
	data->hdlr->pingApache(data);

	return NULL;
}

void *
_waitPid(void *args) {

	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());

	waitPidData *data = (waitPidData *)args;
	data->hdlr->waitPid(data->pid);
	return NULL;
}

int
BpApacheHandler::init()
{
   LogTrace(0, "Entering init");

	LogTrace(0, "Leaving init");
	return 0;
}

int
BpApacheHandler::startApache()
{
	LogTrace(0, "Entering startApache");

	errno = 0;

	// We may have multiple command that need spawning and monitoring
	// So we do this in a while loop
	
	BpFtHandler &lb = BpFtHandler::getInstance();

	int cmdCount = lb.getNumberCommands();
	cmdNode *nodes = lb.getCommands();

	for (int i = 0; i < cmdCount; i++) {
		
		// Fasih: There might be a case where the cmd should not be started by
		// FT instead it is only to be monitored....
		// In such a case we will only mention the pid file.. and not the command to run..
		if (!nodes[i].cmdToRun.empty()) {
		
			LogTrace(0,"Trying to fork()");
			cmdNode *node = &(nodes[i]);
			pid_t pid = fork();

			// Child Process
			if (0 == pid) {
				logger.logMsg(ALWAYS_FLAG, 0,
								  "Running the command : [%s]", node->cmdToRun.c_str());
				
				char **argv = new char *[(node->cmdArgCount)+1];
				for (int count = 0; count < node->cmdArgCount; count++)
					argv[count] = strdup(node->cmdArgs[count].c_str());
					argv[node->cmdArgCount] = NULL;
					
					if (-1 == execv(node->cmdToRun.c_str(), argv)) {
						logger.logMsg(ERROR_FLAG, 0,
										  "execv call failed : [%s]", strerror(errno));
						//LogError(0, "Exiting");
						//exit(1);
						LogError(0, "Going to dead state");
						BpFtHandler::getInstance().
							updateState(BpFtHandler::DEAD);
						return -1;
					}
				}

			// Fork Error
			else if (-1 == pid) {
				logger.logMsg(ERROR_FLAG, 0,
								  "fork call failed : [%s]", strerror(errno));
				//LogError(0, "Exiting");
				//exit(1);
				LogError(0, "Going to dead state");
				BpFtHandler::getInstance().
					updateState(BpFtHandler::DEAD);
				return -1;
			}

			// Parent Process
			else {
				LogTrace(0, "Parent process. Do waitpid");
				logger.logMsg(TRACE_FLAG, 0, "Child PID = [%d]", pid);
				
				// Create a thread to do waitpid
				// If running a daemon then waitpid returns right away
				// In other cases it will hang forever, hence a seperate thread
				
				pthread_t waitThread;
				waitPidData *data = new waitPidData();
				data->hdlr = this;
				data->pid = pid;
				
				if (-1 == pthread_create(&(waitThread), NULL,
												 _waitPid, (void *)data)) {
					LogError(0, "Failed to create waitpid thread");
					//LogError(0, "Exiting");
					//exit(1);
					LogError(0, "Going to dead state");
					BpFtHandler::getInstance().
						updateState(BpFtHandler::DEAD);
					return -1;	
				}
				// To get the waitpid thread on its way
				sleep(1);
			
				logger.logMsg(ALWAYS_FLAG, 0, "Command successfully run: [%s]",
								  node->cmdToRun.c_str());
			}
		}
	
		else {
			logger.logMsg(ALWAYS_FLAG, 0,
				"Mast hai... I dont have to start the process... just have to monitor the pid in [%s]", 
				nodes[i].pidFile.c_str());
		}
	}

	// Sleep so that all the launched commands get a chance to write
	// their PID's
	sleep(2);

	// Now to start the monitoring thread
	monitorPidData *data = new monitorPidData;
	data->count = 0;
	data->hdlr = this;
	
	for (int i = 0; i < cmdCount; i++) {
		cmdNode *node = &(nodes[i]);
		pid_t childPid = readPid(node->pidFile);
		if ((pid_t)(-1) == childPid) {
			LogError(0, "Failed to read Child Pid from File");
			//LogError(0, "Exiting");
			//exit(1);
			LogError(0, "Going to dead state");
			BpFtHandler::getInstance().
				updateState(BpFtHandler::DEAD);
			return -1;
		}
			
		data->pids[i] = childPid;
		data->count ++;
	}

	// Set the loop condition to true
	this->doPing = true;

	// Launch a thread which will ping the PID's
	LogTrace(0,"Creating the ping thread for the process");
	if (-1 == pthread_create(&(this->pingThread), NULL,
		_pingApache, (void *)data)) {
		LogError(0, "Failed to create ping thread");
		//LogError(0, "Exiting");
		//exit(1);
		LogError(0, "Going to dead state");
		BpFtHandler::getInstance().
			updateState(BpFtHandler::DEAD);
		return -1;
	}

	// Get the thread on its way
	sleep(1);
	
	LogTrace(0, "Leaving startApache");
	return 0;
}

void
BpApacheHandler::waitPid(pid_t pid)
{
	LogTrace(0, "Entering waitPid");
	int ret = waitpid(pid, NULL, 0);
	logger.logMsg(ALWAYS_FLAG, 0,
					  "waitpid returned. Return Value [%d] : Error [%s]",
					  ret, strerror(errno));
	LogTrace(0, "Leaving waitPid");
}

int
BpApacheHandler::monitorApache()
{
	LogTrace(0, "Entering monitorApache");

// 	if (-1 == this->openPidFile()) {
//    	LogError(0, "Could not open Apache PID file.");
// 		LogError(0, "Leaving monitorApache");
// 		exit(1);
// 		return -1;
// 	}

// 	this->apachePid = this->readPid();
// 	this->closePidFile();

// 	if ((pid_t)(-1) == this->apachePid ||
// 		 (pid_t)(0) == this->apachePid) {
// 	   LogError(0, "Could not read PID.");
// 		LogError(0, "Exiting");
// 		exit(1);
// 		return -1;
// 	}

// 	logger.logMsg(ALWAYS_FLAG, 0, "Apache PID = [%d]",
// 		(int)(this->apachePid));

// 	// Set the loop condition to true
// 	this->doPing = true;

// 	// Launch a thread which will ping the apachePid
// 	if (-1 == pthread_create(&(this->pingThread), NULL,
// 		_pingApache, (void *)this)) {
// 		LogError(0, "Failed to create ping thread");
// 		LogError(0, "Exiting");
// 		exit(1);
// 	}

	LogTrace(0, "Leaving monitorApache");
	return 0;	
}

void
BpApacheHandler::pingApache(monitorPidData *data)
{
	LogTrace(0, "Entering pingApache");

	bool informMain = true;
	while (this->doPing) {
		sleep(1);

		for (int i = 0; i < data->count; i++) {
			logger.logMsg(TRACE_FLAG, 0, 
				"Pinging the process [%d]",data->pids[i]);

			if ((pid_t)(-1) == getpgid(data->pids[i])) {
			
				logger.logMsg(ERROR_FLAG, 0,
								  "getpgid Failed : [%d]", data->pids[i]);
								  
				// Fasih: Here... If the FTHandler had brought up a process...
				// It should die if it is dead...
				// However... If it did not bring up the process..
				// It should not die/return...

				BpFtHandler &ft = BpFtHandler::getInstance();
				LogTrace(0,"Getting the cmdNodes");
				cmdNode *nodes = ft.getCommands();
									
				if(!nodes[i].cmdToRun.empty()) {
					logger.logMsg(TRACE_FLAG, 0, 
						"I had started [%s]... The poor thing is dead... So I am leaving pingApache",
						nodes[i].cmdToRun.c_str());
					BpFtHandler::getInstance().
						updateState(BpFtHandler::DEAD);
					return;
				}

				else {
					logger.logMsg(TRACE_FLAG, 0,
						"Process in [%s] is dead... Lets try refreshing the pid from file",
						nodes[i].pidFile.c_str());

					LogTrace(0, "Marking as defunct");

					BpFtHandler::getInstance().
						updateState(BpFtHandler::DEFUNCT);

					pid_t newPid;
					while ( 1 ) {
						sleep (5);
						LogTrace(0, "Reading pid to check if the process is brought up");
						newPid = readPid(nodes[i].pidFile);
						
						if ((pid_t)(-1) == newPid) {
							LogError(0, "Failed to read new pid from File...Updating state to DEAD");
							BpFtHandler::getInstance().
								updateState(BpFtHandler::DEAD);
							return;
						} else if (data->pids[i] == newPid) {
							LogTrace(0, "PID not updated..");
						} else {
							LogTrace(0, "PID updated... ");
							data->pids[i] = newPid;
							LogTrace(0, "Updating state to ACTIVE");
							BpFtHandler::getInstance().
							   updateState(BpFtHandler::ACTIVE);
							informMain = false;
							break;
						}
					}
						
				}
																	               
				
			}
		}
		
		if (informMain) {
			BpFtHandler::getInstance().
				updateApacheStatus(BpFtHandler::UP);
			informMain = false;
		}
	}

	LogTrace(0, "Leaving pingApache");
}			

int
BpApacheHandler::openPidFile()
{
	LogTrace(0, "Entering openPidFile");

// 	BpFtHandler &lb = BpFtHandler::getInstance();
// 	std::string pidFile = lb.getPidFile();
	
// 	int mode = 0;
// 	mode |= O_RDONLY;

// 	this->pidFd = open(pidFile.c_str(), mode);

// 	if (-1 == this->pidFd) {
//    	LogTrace(0, "Could not open PID file.");
// 		LogTrace(0, "Leaving openPidFile");
// 		return -1;
// 	}

	LogTrace(0, "PID file successfully opened");
	LogTrace(0, "Leaving openPidFile");
	return 0;
}

pid_t
BpApacheHandler::readPid(std::string pidFile)
{
	LogTrace(0, "Entering readPid");

	logger.logMsg(ALWAYS_FLAG, 0,
					  "PID File : [%s]", pidFile.c_str());
	
	// Open the Pid file in the read mode
	int mode = 0;
	mode |= O_RDONLY;

	int pidFd = open(pidFile.c_str(), mode);

	if (-1 == pidFd) {
   	LogTrace(0, "Could not open PID file.");
		LogTrace(0, "Leaving readPid");
		return (pid_t)(-1);
	}

	// Read the PID
	char *buf = new char[50];
	memset((void *)buf, 0, 50);

	int nBytes = read(pidFd, (void *)buf, 30);

	if (nBytes <= 0) {
		LogError(0, "Failed to read PID");
	   LogTrace(0, "Leaving readPid");
		return (pid_t)(-1);
	}

	pid_t pid = (pid_t)(atoi(buf));
	logger.logMsg(ALWAYS_FLAG, 0, "PID  : [%d]", (int)(pid));

	// Close the File
	close(pidFd);
	
	LogTrace(0, "Leaving readPid");
	return pid;
}

int
BpApacheHandler::closePidFile()
{
	LogTrace(0, "Entering closePidFile");

// 	close(pidFd);

	LogTrace(0, "Leaving closePidFile");
	return 0;
}

void
BpApacheHandler::shutdown() {
	LogTrace(0, "Entering shutdown");
	BpFtHandler &ftH = BpFtHandler::getInstance();
	int cmdCount = ftH.getNumberCommands();
	cmdNode *nodes = ftH.getCommands();

	for (int i = 0; i < cmdCount; i++) {

		// Fasih: Terminate only those which were started...
		if (!nodes[i].cmdToRun.empty()) {
			
			LogTrace(0, "Find out the pid of the process");
			pid_t childPid = readPid(nodes[i].pidFile);
			
			if ((pid_t)(-1) == childPid) {
         	LogError(0, "Failed to read child pid from File...");
			} else {
				logger.logMsg(ALWAYS_FLAG, 0, "Terminating [%d]",childPid);
				// Send Signal 15.. this would allow the process to do a clean exit
				kill(childPid,15);
			}
		}
	}
	LogTrace(0, "Leaving shutdown");
}

BpApacheHandler::BpApacheHandler() :
	apachePid((pid_t)(-1)),
	doPing(false)
{}

BpApacheHandler::~BpApacheHandler()
{}
