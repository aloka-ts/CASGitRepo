#ifndef _BPPEERHANDLER_
#define _BPPEERHANDLER_

#include <pthread.h>

class BpPeerHandler {
friend void *_listener(void *);
friend void *_receiver(void *);
friend void *_heartbeat(void *);
friend void *_peerConnection(void *);
friend void *_initializer(void *);
friend void *_roleResolution(void *);

private:
	pthread_t listenerThread;
	pthread_t receiverThread;
	pthread_t heartbeatThread;

	bool doListen;
	int listenerFd;
	int peerSockFd;
	int selfSockFd;
	
	void listener();
	void receiver();
	void heartbeat();
	
	void _connectWithPeer();
	int _connect();
	void _resolveRole();
	int _init();
	void _cleanupSelf();
	void _cleanupPeer();
	
public:
	BpPeerHandler();
	~BpPeerHandler();
	int init();
	void shutdown();
	int connectWithPeer();
	void resolveRole();
	void startHeartbeat();
};

#endif
