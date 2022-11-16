#include <stdio.h>
#include <errno.h>
#include <unistd.h>
#include <string.h>
#include <pthread.h>

#ifdef sol28g
#include <kstat.h>
#include <sys/processor.h>
#endif

#include <sys/sysinfo.h>
#include <sys/unistd.h>
#include <sys/types.h>

#ifdef __cplusplus
extern "C" {
#endif

int initialize(void);
int initPsrInfo(void);
float getCPUUsage(void);

#ifdef __cplusplus
}
#endif
