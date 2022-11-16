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
//     File:     INGwSpData.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************


#ifndef INGW_SP_DATA_H_
#define INGW_SP_DATA_H_

#define MAX_BODYTYPE_LEN 100

#include <string>

#include <INGwInfraUtil/INGwIfrUtlStrStr.h>
#include <INGwInfraUtil/INGwIfrUtlSerializable.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>

const int ATTRIB_MSGBODY = 0x1;
const int ATTRIB_SIPMESG = 0x2;

class INGwSpData : public virtual INGwIfrUtlSerializable
{
   private:

      static INGwIfrUtlStrStr _sdpAppSDPStrStr;
      static INGwIfrUtlStrStr _sdp2CRLFStrStr;
      static INGwIfrUtlStrStr _sdpVStrStr;
      static INGwIfrUtlStrStr _sdpCStrStr;
      static INGwIfrUtlStrStr _sdpAStrStr;

  public:
    INGwSpData();
    virtual ~INGwSpData();
    void reset();

    const void* getBody();
    void setBody(const char *, int = -1);

    unsigned getBodyLength();

    void setBodyLength(unsigned);

    void setDialogueId(int pDlgId);
    void setBillingId(int pBillingId);
    int getDialogueId();
    int getBillingId();

    void setSeqNum(int pSeqNum);
    int getSeqNum();

    const char* getBodyType();
    void setBodyType(const char *);

    void copyMsgBody(INGwSpData &aGwData);
    void getSDPGWData(INGwSpData* apData);

    int getChargeStatus();
    void setChargeStatus(int aStatus);

    virtual std::string toLog() const;

    virtual bool serialize
           (unsigned char*       apcData,
            int                  aiOffset,
            int&                 aiNewOffset,
            int                  aiMaxSize,
            bool                 abForceFullSerialization = false);

    virtual bool deserialize
           (const unsigned char* apcData,
            int                  aiOffset,
            int&                 aiNewOffset,
            int                  aiMaxSize);

    SipMessage*  getSipMessage();

		void  setSipMessage(SipMessage *);

		void copy(INGwSpData &aSipData, int aCopyAttribute);

    // FOR total count
    static int              miStCount;
    static pthread_mutex_t  mStMutex;
    void setCounters();
    void decrementCounters();
    static int getCount(void);
    static int initStaticCount(void);


  private:
    char           mSipBodyType[MAX_BODYTYPE_LEN];
    char          *mpSipBody;
    unsigned int   mBodyLength;
    int            miChargeStatus;
    int            mDialogueId;
    int            miBillingId;
    int            mSeqNum;
		SipMessage    *mpSipMsg;

}; // End of class INiGwSpData

class INGwSpData_Init {

  public :

    INGwSpData_Init() { }
    void operator() (INGwSpData* s) { s->setCounters();}
};

class INGwSpData_Reuse {

  public :

    INGwSpData_Reuse() { }

    void operator() (INGwSpData* s) {
      s->decrementCounters();
      s->reset();
    }
};

class INGwSpData_MemMgr 
{
   public :

      INGwSpData * allocate(int)
      {
         return new INGwSpData();
      }

      void deallocate(INGwSpData *s)
      {
         delete s;
         return;
      }
};

#endif //INGW_SP_DATA_H_
