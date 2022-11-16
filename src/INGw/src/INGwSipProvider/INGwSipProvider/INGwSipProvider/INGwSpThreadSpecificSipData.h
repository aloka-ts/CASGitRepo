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
//     File:     INGwSpThreadSpecificSipData.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_THREAD_SPEC_SIP_DATA_H_
#define INGW_SP_THREAD_SPEC_SIP_DATA_H_

#include <INGwSipProvider/INGwSpSipCallTable.h>
#include <INGwSipProvider/INGwSpStackTimer.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpSipHeaderPolicy.h>

class INGwSpSipConnection;

class INGwSpThreadSpecificSipData 
{
   public:

      INGwSpThreadSpecificSipData();

      INGwSpSipCallTable&    getCallTable();
      INGwSpStackTimer&      getStackTimer();
      int                getSendSocket();
      Sdf_st_hash*       getRemoteRetransHash();
      void               setRemoteRetransHash(Sdf_st_hash* aHash);
      INGwSpSipProviderConfig& getConfigRepository();

      const RSI_NSP_SIP::INGwSpSipHeaderDefaultData &getHdrDefault() const;
      void setHdrDefault(const RSI_NSP_SIP::INGwSpSipHeaderDefaultData &);

      // FT Serialization related temporary contexts
      void setStackSerializationContext(unsigned char* apcData, int aiOffset, 
                                        int aiMaxSize);

      void getStackSerializationContext(unsigned char*& apcData, int& aiOffset, 
                                        int& aiMaxSize);
    
   private:

      INGwSpSipCallTable    mCallTable;
      INGwSpStackTimer      mStackTimer;
      int               mSendSocket;
      Sdf_st_hash*      mpRemoteRetransHash;
      INGwSpSipProviderConfig    mConfigRep;

      unsigned char* mpcFTContextData;
      int miFTContextOffset;
      int miFTContextMaxSize;

      RSI_NSP_SIP::INGwSpSipHeaderDefaultData headerDefault;

   public:

      //Used in the sendTonetwork.
      INGwSpSipConnection *conn;

      //Used to capture first invite in CallTrace.
      char *msgBuf;
      INGwSipTranspInfo *msgTransport;

      //To reuse frequently used structures.
      SipParam *userPhoneParam;
      SipParam *earlyYesParam;
      SipParam *earlyNoParam;
};

#endif //INGW_SP_THREAD_SPEC_SIP_DATA_H_
