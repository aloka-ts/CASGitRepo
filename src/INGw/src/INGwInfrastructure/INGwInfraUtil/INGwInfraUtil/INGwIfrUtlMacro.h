//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwIfrUtlMacro.h
//
//     Desc:     Defines Macros
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef __BP_MACRO_H__
#define __BP_MACRO_H__

#include <pthread.h>

#ifdef linux

#ifdef REDHAT80

#define MACRO_THREAD_ID() \
   ((pthread_self() == 0x2000) ? 1 : ((pthread_self() >> 13) + 1))
#define MACRO_RET_THREAD_ID(THREAD) \
   ((THREAD == 0x2000) ? 1 : ((THREAD >> 13) + 1))

#else

#define MACRO_THREAD_ID() (pthread_self())
#define MACRO_RET_THREAD_ID(THREAD) (THREAD)

#endif

#else

#define MACRO_THREAD_ID() (pthread_self())
#define MACRO_RET_THREAD_ID(THREAD) (THREAD)

#endif

#endif
