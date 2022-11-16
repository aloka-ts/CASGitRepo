#ifndef _BPMACHPING_
#define _BPMACHPING_
#include <sys/types.h>
#include <inttypes.h>

class BpMachPing {
public:
   enum BpMachStatus {
		AVAILABLE = 1,
		NOT_AVAILABLE
	};

private:
	void _setOptions(int sockID);
	static BpMachPing *me;
public:
	BpMachStatus getStatus(uint32_t IP, int timeout);
	static BpMachPing &getInstance();
};

#endif
