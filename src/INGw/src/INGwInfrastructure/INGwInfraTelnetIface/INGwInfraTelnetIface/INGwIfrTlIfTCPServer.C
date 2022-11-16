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
//     File:     INGwIfrTlIfTCPServer.C
//
//     Desc:     This file provides implementation of telnet interface.
//							 It takes command through telnet interface and fetch
//							 desired operation from system and return it to user console.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraTelnetIface");

using namespace std;

#include "INGwInfraTelnetIface/INGwIfrTlIfTCPServer.h"

/**
* Constructor
*/
INGwIfrTlIfTCPServer::INGwIfrTlIfTCPServer(const char* apcPrompt, 
																					 bool abUsePrompt, 
																					 bool abUseReadFormat)
{
    miSockFd = -1;
    mInterpreterfunction = NULL;
    miListenerBacklog = 1;

    const char* pcPrompt = NULL;
    if((apcPrompt != NULL) && (strcmp(apcPrompt, ""))) {
        pcPrompt = apcPrompt;
    }
    else {
        pcPrompt = DEFAULT_PROMPT;
    }
    mpcPrompt = new char[(strlen(pcPrompt) + 3 + 1)];
    sprintf(mpcPrompt, "%s > ", pcPrompt); 

    mbUsePrompt = abUsePrompt;
    mbUseReadFormat = abUseReadFormat;
}

/**
* Destructor
*/
INGwIfrTlIfTCPServer::~INGwIfrTlIfTCPServer()
{
    if(miSockFd >= 0) {
        close(miSockFd);
    }
}

/**
* Description : This method initialize TCP server 
*
* @param <apcSelfAddr> -
* @param <aiPort     > -
* @param <apFunc     > - 
* @param <abStartListener> - 
* @param <aiBacklog> -
*
* @return <bool> - 
*
*/
bool 
INGwIfrTlIfTCPServer::initialize(const char* apcSelfAddr, int aiPort,
    interpretFuncType apFunc, bool abStartListener, int aiBacklog)
{
    mInterpreterfunction = apFunc;
    miListenerBacklog = aiBacklog;

    bool retval = true;
    if((miSockFd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "socket creation failure");
        perror("PROBE: socket creation failure: ");
        retval = false;
    }

    int one = 1;
    if(true == retval) {
        if(setsockopt(miSockFd, IPPROTO_TCP, TCP_NODELAY, (char*) &one,
            sizeof(one)) < 0) {
            logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "setsockopt() for TCP_NODELAY failed");
            perror("PROBE: setsockopt() for TCP_NODELAY failed: ");
            retval = false;
        }
    }

    if(true == retval) {
        if (setsockopt(miSockFd, SOL_SOCKET, SO_REUSEADDR, (char*) &one,
            sizeof(one)) < 0) {
            logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "setsockopt() for SO_REUSEADDR failed");
            perror("PROBE: setsockopt() for SO_REUSEADDR failed: ");
            retval = false;
        }
    }

    if(true == retval) {
        uint32_t selfAddress = INADDR_ANY;
        if((apcSelfAddr != NULL) && (strcmp(apcSelfAddr, ""))) {
            selfAddress = inet_addr(apcSelfAddr);
        }
        struct sockaddr_in selfAddrStruct;
        bzero(&selfAddrStruct, sizeof(selfAddrStruct));
        selfAddrStruct.sin_family = AF_INET;
        selfAddrStruct.sin_port = htons(aiPort);
        selfAddrStruct.sin_addr.s_addr = htonl(selfAddress);
        if(bind(miSockFd, (struct sockaddr *)&selfAddrStruct,
            sizeof(selfAddrStruct)) < 0) {
            logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "bind failure");
            perror("PROBE: bind failure: ");
            retval = false;
        }
    }

    if((true == retval) && (true == abStartListener)) {
        pthread_t thdId;
        if(pthread_create(&thdId, NULL, listenerThread, (void*)this) < 0) {
            logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "listener thread creation failed");
            perror("PROBE: listener thread creation failed: ");
            retval = false;
        }
    }

    if(false == retval) {
        close(miSockFd);
        miSockFd = -1;
    }
    return retval;
}

/**
* Description : This method initialize TCP server 
*
* @param <apSelf> -
*
* @return <void*> - 
*
*/
void* 
INGwIfrTlIfTCPServer::listenerThread(void* apSelf)
{
   pthread_detach(pthread_self());

    INGwIfrTlIfTCPServer* pSelf = static_cast<INGwIfrTlIfTCPServer*>(apSelf);
    while(1) {
        if(listen(pSelf->miSockFd, pSelf->miListenerBacklog) < 0) {
            logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "listen error");
            perror("PROBE: listen error: ");
            break;
        }
  
        int newfd;
        if((newfd = accept(pSelf->miSockFd, NULL, NULL)) < 0) {
            logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "accept error");
            perror("PROBE: accept error: ");
            break;
        }

        bool closeSession = false;
        while(false == closeSession) {
            int retResult = pSelf->sendPrompt(newfd);
            if(0 != retResult) {
                break;
            }

            string inputStr;
            retResult = pSelf->readInput(newfd, inputStr);
            if(0 != retResult) {
                break;
            }
  
            if((0 == strcmp(inputStr.c_str(), "bye" )) || 
               (0 == strcmp(inputStr.c_str(), "quit")) ||
               (0 == strcmp(inputStr.c_str(), "exit"))) 
            {
                break;
            }

            char *output;
            int size = 0;
            closeSession = (*(pSelf->mInterpreterfunction))(inputStr, 
															&output, size, newfd);

            if(0 < size) {
                retResult = pSelf->writeOutput(newfd, output, size);
                delete [] output;
                if(0 != retResult) {
                    break;
                }
            }
        }
        close(newfd);

    } // End of while for ever

    return NULL;
}

/**
* Description : This method initialize TCP server 
*
* @param <aiSockFd> -
* @param <arInputStr> -
*
* @return <int> - 
*
*/
int
INGwIfrTlIfTCPServer::readInput(int aiSockFd, string& arInputStr)
{
    int retResult = 0;

    int size = MAX_BUFFER_SIZE;
    if(true == mbUseReadFormat) {
        if(read(aiSockFd, &size, 4) < 0) {
            perror("PROBE: read error: ");
            retResult = -1;
        }
    }

    if(0 == retResult) {
        char command[MAX_BUFFER_SIZE + 1];
				bzero(command, (MAX_BUFFER_SIZE + 1) );
        char c = 0;
        int index = 0;
        for( ; index < size; ) {
            if(read(aiSockFd, &c, 1) < 0) {
                logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "read error");
                perror("PROBE: read error: ");
                retResult = -1;
                break;
            }

            if(c == 13) {
                continue;
            }
            else if(c == 10) {
                break;
            }
            else {
                command[index++] = c;
            }
        }
        if(0 == retResult) {
            command[index] = '\0';
            arInputStr = command;
        }
    }

    return retResult;
}

/**
* Description : This method initialize TCP server 
*
* @param <aiSockFd> -
* @param <apcOutput> -
* @param <aiSize> - 
*
* @return <int> - 
*
*/
int
INGwIfrTlIfTCPServer::writeOutput(int aiSockFd, const char* apcOutput, 
																	int aiSize)
{
    int retResult = 0;
    if(write(aiSockFd, (void*)apcOutput, aiSize) < 0) {
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "write error");
        perror("PROBE: write error: ");
        retResult = -1;
    }
    return retResult;
}

/**
* Description : 
*
* @param <aiSockFd> -
*
* @return <int> - 
*
*/
int
INGwIfrTlIfTCPServer::sendPrompt(int aiSockFd) 
{
    int retResult = 0;
    if(true == mbUsePrompt) {
        retResult = writeOutput(aiSockFd, mpcPrompt, strlen(mpcPrompt));
    }
    return retResult;
}

