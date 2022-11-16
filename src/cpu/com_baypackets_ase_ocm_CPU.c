#include "com_baypackets_ase_ocm_CPU.h"
#include "cpu.h"

JNIEXPORT void JNICALL Java_com_baypackets_ase_ocm_CPU_initialize
  (JNIEnv *env, jobject obj)
{
    initialize();
}

JNIEXPORT jfloat JNICALL Java_com_baypackets_ase_ocm_CPU_getCPUUsage
  (JNIEnv *env, jobject obj)
{
    return getCPUUsage();
}

