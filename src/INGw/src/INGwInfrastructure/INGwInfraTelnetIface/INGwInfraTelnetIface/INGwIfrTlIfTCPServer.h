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
//     File:     INGwIfrTlIfTCPServer.h
//
//     Desc:     This file contains definition of telnet interface 
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_TL_IF_TCP_SERVER_H_
#define _INGW_IFR_TL_IF_TCP_SERVER_H_

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <pthread.h>
#include <time.h>
#include <stdio.h>
#include <strings.h>
#include <string>

#include <INGwInfraTelnetIface/INGwIfrTlIfIncludes.h>

typedef bool (*interpretFuncType)(const std::string&, char**, int&, int);

class INGwIfrTlIfTCPServer
{
  public:

    INGwIfrTlIfTCPServer(const char* apcPrompt, bool abUsePrompt, 
												 bool abUseReadFormat);

    ~INGwIfrTlIfTCPServer();

    bool 
    initialize(const char* apcSelfAddr, int aiPort,
        		   interpretFuncType apFunc, bool abStartListener, int aiBacklog);

  private:

    static void* 
    listenerThread(void*);

    int
    readInput(int aiSockFd, std::string& arInputStr);

    int
    writeOutput(int aiSockFd, const char* apcOutput, int aiSize);

    int
    sendPrompt(int aiSockFd);

    int    miSockFd;
    char*  mpcPrompt;
    int    miListenerBacklog;
    bool   mbUsePrompt;
    bool   mbUseReadFormat;
    interpretFuncType mInterpreterfunction;

};

#endif
