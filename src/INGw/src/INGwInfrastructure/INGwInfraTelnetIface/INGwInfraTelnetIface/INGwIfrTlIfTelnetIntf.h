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
//     File:     INGwIfrTlIfTelnetIntf.h
//
//     Desc:     This file contains definition of Telnet Interface class.
//							 Uses Port number on whcih telnet interface shall listen.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_TL_IF_TELNET_INTF_H_
#define _INGW_IFR_TL_IF_TELNET_INTF_H_

#include <pthread.h>
#include <string>

#include <INGwInfraParamRepository/INGwIfrPrIncludes.h>
#include <INGwInfraTelnetIface/INGwIfrTlIfTCPServer.h>
 
typedef struct t_INGwTcapWorkUnitMsg INGwTcapWorkUnitMsg; 

bool
cliFunc(const std::string& arInputStr, char** apcOutput, int& size, int);

void 
myStringToByteArray(std::string str, unsigned char* array, int& size);

extern "C" void * doConfiguration(void *pThis);

class INGwIfrTlIfTelnetIntf : public virtual INGwIfrTlIfTCPServer
{
	public :

		INGwIfrTlIfTelnetIntf();

		virtual ~INGwIfrTlIfTelnetIntf();

		bool 
		initialize(const char* apcSelfAddr, int aiPort, 
							 bool abStartListener, int aiBacklog);

    protected :

    private:

        INGwIfrTlIfTelnetIntf& operator= (const INGwIfrTlIfTelnetIntf& arSelf);
        INGwIfrTlIfTelnetIntf(const INGwIfrTlIfTelnetIntf& arSelf);
};

#endif 
