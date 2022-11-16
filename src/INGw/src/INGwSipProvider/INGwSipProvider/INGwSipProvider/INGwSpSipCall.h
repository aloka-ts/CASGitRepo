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
//     File:     INGwSpSipCall.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************


#ifndef _INGW_SP_SIP_CALL_H_
#define _INGW_SP_SIP_CALL_H_

#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include <INGwInfraUtil/INGwIfrUtlSerializable.h>
#include <INGwInfraUtil/INGwIfrUtlRefCount.h>
#include <INGwFtPacket/INGwFtPktMsg.h>

#define MAX_SDP_LEN 4096

#define MAX_BDY_TYPE 100
#define PROV_RTPTUNNEL_SDP  0x0001
#define FINAL_RTPTUNNEL_SDP 0x0002

class INGwIfrMgrWorkUnit;
class INGwSpSipConnection;

#include <list>
#include <string>

typedef std::list<std::string> SigMsgList;
typedef SigMsgList::iterator SigMsgListIt;
typedef SigMsgList::const_iterator SigMsgListCIt;

/** This class represents a Call which is composed of multiple connections.
 *
 *  All the methods on INGwSpSipCall are MT unsafe and explicit locking/unlocking
 *  should be done using the lock() and unlock() methods on the call object.
 */

static const char* min_state[] = { "CALL_MIN_IDLE",
                                   "CALL_MIN_SEARCH",
                                   "CALL_MIN_TRANSFER", 
                                   "CALL_MIN_RTP_TUN_SERACH",
                                   "CALL_MIN_ERROR",
                                   "0"
                                 };

class INGwSpSipCall : public virtual INGwIfrUtlRefCount,
    public virtual INGwIfrMgrWorkerClbkIntf,
    public virtual INGwIfrUtlSerializable
{
    public:

        typedef enum {
             CALL_IDLE = 0,
             CALL_CONNECTED,
             CALL_TERMINATED
        } MajorState;

        typedef enum {
             CALL_MIN_IDLE = 0,
             CALL_MIN_SEARCH,
             CALL_MIN_TRANSFER,
             CALL_MIN_RTP_TUN_SERACH,
             CALL_MIN_ERROR
        } MinorState;

        bool mLogFlag;

   public:

        int handleWorkerClbk(INGwIfrMgrWorkUnit* apWork);

        int sendMsgToPeerINGw(char* apcMsgData, short asLength);

        virtual void 
        release(int aiCode = 0, short asRetryCount = 0);


        INGwSpSipCall();
	      INGwSpSipCall(const std::string& arconCallId);

        virtual void initObject(bool consFlag = false);

        void setCallId(const std::string& arconCallId);
        virtual ~INGwSpSipCall();
        static int getCount(void);
        static int initStaticCount(void);

        int getUniqId(void);

        void lock(void) const;
        void unlock(void) const;

        INGwSpSipCall::MajorState getMajorState(void);
        INGwSpSipCall::MinorState getMinorState(void);


        const std::string & getCallId(void) const;

        unsigned long getHashedCallId(void) const;
        void getExtCallId(const std::string& arIntCallId, std::string& arExtCallId);

        bool isCallReleased(void) const;

        void setMajorState(INGwSpSipCall::MajorState aeMajorState);
        void setMinorState(INGwSpSipCall::MinorState aeMinorState);

        /** Accessor method for the connections.  This method increases
         *  the ref count of the connection by "1"
         */

        INGwSpSipConnection* getConnection(short asConnId);
        INGwSpSipConnection* getFirstConnection(void);
        INGwSpSipConnection* getNextConnection(short asConnId);
        INGwSpSipConnection* getLastConnection();

        /** This method adds the connection to the call and increases
         *  the ref count of connection by "1". It returns the connection
         *  Id of the connection added.
         */
        short addConnection(INGwSpSipConnection* apConn, short asConnId = -1);

        /** This method removes the connection from the call and decrements
         *  the ref count object by "1". It is called by the provider as
         *  a call back for disconnect() on the INGwSpSipConnection. It returns
         *  the no. of remaining connections in the call.
         */
        short removeConnection(short asConnId, bool backupObj = false);

        short removeConnectionOnly(short asConnId, bool backupObj = false);

        /** This method removes all the connections from the call and
         *  decrements their ref count by "1".
         */
        void removeAllConnections(void);
    
        virtual std::string toLog(void) const;

        bool startSerialize(INGwIfrUtlSerializable::ReplicationOperationType apRepOpr = REP_CREATE,
                           INGwFtPktMsg* apMsg = NULL);


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


        virtual void cleanup(void);

        virtual void setCounters();

        void terminateCall();

        bool isValidConnection(int connID);

				void releaseRef(void);

    protected:

        int processCCMWorkUnit(INGwIfrMgrWorkUnit* apWork);
        int processTimerWorkUnit(INGwIfrMgrWorkUnit* apWork);

        void dispatchMsgToPeerINGw(INGwFtPktMsg *);

        typedef std::list<INGwSpSipConnection*> ConnList;

        /** This separator is used while making the internal call Id.
         */
        static std::string      mSeparator;

        static int              miStCount;
        static pthread_mutex_t  mStMutex;

        pthread_mutexattr_t     mCallMutexAttr;
        mutable pthread_mutex_t mCallMutex;


        /** This call Id used internally by the INGW.
         */

        std::string             mCallId;
        unsigned long           mulCallIdHash;
        bool                    mbIsCallReleased;

        ConnList                mConnList;

        struct ChangeableCallData
        {
           INGwSpSipCall::MajorState      meMajorState;
           INGwSpSipCall::MinorState      meMinorState;

           short                   msConnCounter;
           int                     miUniqId;

           long                    mlObjCreationTimeStamp;


           ChangeableCallData() : 
              meMajorState(CALL_IDLE), meMinorState(CALL_MIN_IDLE), 
              miUniqId(0), msConnCounter(0),
              mlObjCreationTimeStamp(0)
           {
           }
        };

        ChangeableCallData    mChangeData;

        int                   m_peerStartTime;



    public:

        //Message list.
        SigMsgList msgList;

    private:

        /** Assignment operator (Not implemented)
         */
        INGwSpSipCall& operator= (const INGwSpSipCall& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwSpSipCall(const INGwSpSipCall& arSelf);

   private:
      //PANKAJ - version
      //static RSI_NSP_CCM::VersionHolder *_verHolder;

   public:

      static void setVersionDetail();
      
};

class INGwSpSipCall_Init {

  public :

    INGwSpSipCall_Init() { }
    void operator() (INGwSpSipCall* s) { 
        s->setCounters();
    }
};

class INGwSpSipCall_Reuse {

  public :

    INGwSpSipCall_Reuse() { }

    void operator() (INGwSpSipCall* s) {
      s->initObject();
    }
};

class INGwSpSipCall_MemMgr
{
   public :

      INGwSpSipCall * allocate(int)
      {
         return new INGwSpSipCall();
      }

      void deallocate(INGwSpSipCall *s)
      {
         delete s;
         return;
      }
};

#endif //_INGW_SP_SIP_CALL_H_

