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
//     File:     INGwIfrMgrIPAddrHandler.h
//
//     Desc:     The class INGwIfrMgrIPAddrHandler : handles the 
//							 floating IP address of INGw by interacting with the 
//							 SystemMonitor running on both the primary and backup machines.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_MGR_IP_ADD_HANDLER_H_
#define _INGW_IFR_MGR_IP_ADD_HANDLER_H_

#include <string>
#include <INGwInfraUtil/INGwIfrUtlMachPing.h>
#include <INGwInfraUtil/INGwIfrUtlReject.h>

class INGwIfrMgrIPAddrHandler
{
   public:
         
      INGwIfrMgrIPAddrHandler(void);

      virtual ~INGwIfrMgrIPAddrHandler();

      void initialize(bool abIsPrimary) throw (INGwIfrUtlReject);

      int shutdown(void);

      /** Sets the mode of the INGW to primary or backup mode. If the
      *  mode changes from backup to primary - it connects to the peer
      *  CCMAgent and asks to relinquish the floating IP address. It 
      *  then connects to the Agent running on its own machine to 
      *  set the floating IP address.
      */

      int setSelfMode(bool abIsPrimary);

      bool isNetworkAvailable();

      bool getSelfMode(void);
      std::string getFloatingIPAddr();
    
      static int changeExtIPAddr(bool abMode, const std::string &floatingIp,
                                 const std::string& arIPAddr);
   private:

      //Set or unset floating IP on arIPAddr machine based on abMode.

      int _changeExtIPAddr(bool abMode, const std::string& arIPAddr);

      //Opens channel to the peer and send command and receives result.

      static int _sendCommand(const std::string& arPeerIP, int aiPeerPort, 
                              const std::string& arCmd);

      bool         mbIsPrimary;
      std::string  mSelfIPAddr;
      int          miAgentPort;
      std::string  mRefIPAddr;

      int _refIP;

      RSI_NSP_CCM::INGwIfrUtlMachPing::INGwIfrUtlMachStatus _refMachStatus;

   private:

      INGwIfrMgrIPAddrHandler(const INGwIfrMgrIPAddrHandler& arconSelf);
      INGwIfrMgrIPAddrHandler& operator=(const INGwIfrMgrIPAddrHandler& arconSelf);
};

#endif // _INGW_IFR_MGR_IP_ADD_HANDLER_H_
