#include <sys/resource.h>
#include "cpu.h"

int main(int argc, char** argv)
{
	int i = 1000;
	int j = i;

    initialize();
    printf("Initialization done !!\n");

	while(i--) {
		printf("System CPU [%.2f]\n", getCPUUsage());
		sleep(10);
	}

	return 0;
}
