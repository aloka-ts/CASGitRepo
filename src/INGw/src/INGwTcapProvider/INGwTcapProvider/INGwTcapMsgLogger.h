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
//     File:     INGwTcapMsgLogger.h
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_MSG_LOGGER_H_
#define _INGW_TCAP_MSG_LOGGER_H_

#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>
#include "INGwTcapProvider/INGwTcapInclude.h"
#include "INGwTcapMessage/TcapMessage.hpp"

using namespace std;

#define RX 0
#define TX 1
#define ENC RX
#define DEC TX
#define TCAP_LOGGING_L1 1
#define TCAP_LOGGING_L2 2
class INGwTcapMsgLogger
{
	public:

    INGwTcapMsgLogger(string pduFile, int loggingLevel);
    INGwTcapMsgLogger();
    ~INGwTcapMsgLogger();

    static INGwTcapMsgLogger& 
		getInstance();

    int 
		initialize();

    void 
		setLoggingLevel(int level);

		int
		dumpMsg(int dlg, int msgType, 
						TcapMessage *msg, bool rxOrTx = RX);
    int 
    INGwTcapMsgLogger::dumpCodecMsg(char * str, bool encOrdec);

		void
		dumpAddress(SccpAddr *src, SccpAddr *dst, 
							char *buffer, int &bufLen, int pbRxOrTx);

		int
		dumpComponent(TcapComp *comp, char* buffer, int& bufLen);

		int
		dumpComponent(int dlg, int msgType, TcapComp *comp, bool rxOrTx);

    int 
		dumpMsg(char * str, int msgType, bool rxOrTx = RX);

    int 
    getLoggingLevel();

    int 
    dumpString(char* str);
	protected:

	private:

    void 
		fetchHeaderInfo(char *evtBuf, int &evtLen, bool txMode, bool codecFlg = false);

    BpGenUtil::INGwIfrSmAppStreamer *mpPduStream;

    int                     mLoggingLevel;
    long                    mCounter;
    pthread_mutex_t         mLock;
    static INGwTcapMsgLogger*     mpSelf;
};

#endif
