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
//     File:     INGwSpSipConnection.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_CONNECTION_H_
#define INGW_SP_SIP_CONNECTION_H_

#include <string>
#include <list>

#include <INGwSipProvider/INGwSpSipIncludes.h>

#include <INGwSipProvider/INGwSpAddress.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipMsgHandler/INGwSpMsgSipSpecificAttr.h>

#include <INGwInfraUtil/INGwIfrUtlSerializable.h>
#include <INGwInfraUtil/INGwIfrUtlRefCount.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>

#include <INGwSipMsgHandler/INGwSpMsgBaseHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgInviteHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgInfoHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgCancelHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgByeHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgNotifyHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgOptionsHandler.h>

#define ISPROVRESP(x)    ((x >= 100) && (x < 200))
#define ISFINALRESP(x)   ((x >= 200) && (x < 700))
#define ISSUCCESSRESP(x) ((x >= 200) && (x < 300))
#define ISREDIRRESP(x)   ((x >= 300) && (x < 400))
#define ISFAILRESP(x)    ((x >= 400) && (x < 700))

#define INVALID_STATE_RETRY -9


// Minor state of the sip connection.
typedef enum
{
   CONN_MINSTATE_IDLE         ,
   CONN_MINSTATE_MID_3_MSGTX  ,
   CONN_MINSTATE_MID_3_MSGRX  ,
   CONN_MINSTATE_MID_3_RSPTX  ,
   CONN_MINSTATE_MID_3_RSPRX  ,
   CONN_MINSTATE_MID_2_MSGTX  ,
   CONN_MINSTATE_MID_2_MSGRX
}INGwSipConnMinorState;


typedef enum
{
  CONNECTION_CREATED         ,
  CONNECTION_CONNECTED       ,
  CONNECTION_DISCONNECTED    ,
  CONNECTION_FAILED          ,
  CONNECTION_INFO  
}INGwSipConnMajorState;

struct INGwSpSipConnTcapTransInfo
{
	int 					m_stackDlgId;
	int						m_appId;
	int						m_instId;
	bool					m_isDialogueComplete;
  short         m_suId;
  short         m_spId;
  int           m_seqNum;
	INGwSpSipConnTcapTransInfo():m_stackDlgId(0), m_appId(-1), 
												m_instId(-1), m_isDialogueComplete(true), m_suId(-1), m_spId(-1),
                        m_seqNum(-1)
	{}
	void reset()
	{
		m_stackDlgId = 0; m_appId = -1; m_instId = -1; m_isDialogueComplete = true;
    m_suId = -1; m_spId = -1; m_seqNum = -1;
	}
};

class INGwSpSipCall;

class INGwSpSipConnection_Reuse;

class INGwSpSipConnection : public virtual INGwIfrUtlRefCount,
                            public virtual INGwIfrUtlSerializable
{
   public:

      INGwSpSipConnection();
      void initObject(bool consFlag = false);
      void releaseRef(void);
      void reset(void);
      virtual ~INGwSpSipConnection();

    /*************************************************************************/
    /**** Following are callbacks issued by the stack.                       */
    /**************************Notify***********************************************/
      void
      setNotifyTcSeqNum(int aSeqNum);
      Sdf_ty_retVal stackCallbackRequest(INGwSipMethodType aMethodType, 
                                         Sdf_st_callObject **ppCallObj, 
                                         Sdf_st_eventContext *pEventContext,
                                         Sdf_st_error *pErr,
                                    Sdf_st_overlapTransInfo *pOverlapTransInfo);

      Sdf_ty_retVal stackCallbackResponse(INGwSipMethodType aMethodType, 
                                          int aRespCode, 
                                          Sdf_st_callObject **ppCallObj, 
                                          Sdf_st_eventContext *pEventContext, 
                                          Sdf_st_error *pErr, 
                                    Sdf_st_overlapTransInfo *pOverlapTransInfo);

      Sdf_ty_retVal stackCallbackAck
                  (INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             );

      void          indicateTimeout
                  (INGwSipMethodType             aMethodType      ,
                   INGwSipTimerType::TimerType aType           ,
                   unsigned int             aTimerid         );

    /************************************************************************/
    /**** The following methods are called by the gateway call to pass      */
    /**** information from a peer connection to this connection.            */
    /************************************************************************/
    int mSendRequest (INGwSipMethodType      aMethodType,
                      INGwSpData         *aGwData,
                      Sdf_st_overlapTransInfo* peerOverlapTxInfo = 0);

    //The aMode is used for Merge Tfr only

    int mSendResponse(INGwSipMethodType      aMethodType,
          INGwSpData*      aIpData,
          int            aRespCode);
          

    int mSendAck(INGwSipMethodType aMethodType, INGwSpData *aIpData);

    int mSendRPR(INGwSipMethodType aMethodType, INGwSpData *aGwData, int aCode);

    short disconnect(int errCode);

    short continueProcessing(short originator);

    virtual short sendOptions(const char *aIpAddr, const int aPort);

    virtual short sendInfo(INGwSpData &aIpData);

    /************************************************************************/
    /***** Fault tolerance methods                                          */
    /************************************************************************/
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
    /************************************************************************/
    /***** Accessor methods                                                 */
    /************************************************************************/
    void                setMinorState(INGwSipConnMinorState aNewState);
    INGwSipConnMinorState getMinorState() const;

    int getProviderSpecificMinorState() const;

    INGwSipMethodType        getLastMethod() const;
    void                setLastMethod(INGwSipMethodType aMethodType);

    INGwSpMsgBaseHandler*       getSipHandler(INGwSipMethodType aMethodType);

    Sdf_st_callObject*  getHssCallObject() const;
    void                setHssCallObject(Sdf_st_callObject *);

    SipUrl*
    getInvReqLine();

    /************************************************************************/
    /******************* Miscellaneous methods ******************************/
    /************************************************************************/
    virtual std::string toLog(void) const;


    INGwSpSipConnection* clone();

		// BPUsa07842 : 
		void reuseForNextTarget();

		// BPUsa07847 : 
		INGwSpSipConnection* getPeerSipConnection();

    void
    unlockResource();

    void
    lockResource();

    int
    setSessionTimerInfo(Sdf_st_callObject* pCallObject,
      bool isResponse = false);


    unsigned int  mActiveSessionTimerId;

    /************************************************************************/
    /************************************************************************/

    // Function that control serialization of data to PEER
    void transactionStarted(INGwSpMsgBaseHandler *p_Handler);
    void transactionEnded(INGwSpMsgBaseHandler *p_Handler, INGwSipHandlerType type);
  public:

    typedef enum 
    { 
       ORIGINATING_ADDRESS,
       DIALED_ADDRESS,
       TARGET_ADDRESS,
       REDIRECTING_ADDRESS 
     } AddressType;


     enum TransactionState
     {
        TRX_FAILURE,
        TRX_SUCCESS
     };


    int miFailureRespCode;

		int m_MinSessionInterval;
		int m_MaxSessionInterval;

    char paiTelUri[300];
    char paiSipUri[300];

    bool mLogFlag;

    // FOR connection count
    static int              miStCount;
    static pthread_mutex_t  mStMutex;
    void setCounters();
    static int getCount(void);
    static int initStaticCount(void);



  private:

    INGwSipConnMinorState   meConnMinorState     ;
    INGwSipMethodType          meLastMethod         ;

    bool mbStackSerReqdFlag;
    Sdf_st_callObject    *mHssCallLeg          ;
    int  miIncomingGwPort;

	  INGwSpMsgInviteHandler mSipInviteHandler;
 	  INGwSpMsgCancelHandler mSipCancelHandler;
 	  INGwSpMsgInfoHandler   mSipInfoHandler;
	  INGwSpMsgByeHandler    mSipByeHandler;
	  INGwSpMsgNotifyHandler mSipNotifyHandler;
	  INGwSpMsgOptionsHandler mSipOptionsHandler;

    int                   miPrivacyMask;
    int                   miActiveEPPort;

		short        msSelfConnId;
		bool         mbIsDead;
		std::string     msLocalCallID;
		INGwSpSipCall*      mpCall;

    struct ConstConnectionData
    {
      INGwSpAddress     mOriginatingAddr;
      INGwSpAddress     mDialedAddr;
      INGwSpAddress     mTargetAddr;
    };

    struct ChangeableConnectionData
    {
      INGwSipConnMajorState   meMajorState;
      char         mGwIPAddress[SIZE_OF_IPADDR + 1];
      int          miGwPort;
			char         mContactHdr[MAX_HEADER_LEN + 1];

      ChangeableConnectionData() : meMajorState(CONNECTION_CREATED)
      {
        mGwIPAddress[0] = '\0';
				mContactHdr[0] = '\0';
      }
    };

		ChangeableConnectionData mChangeData;
    ConstConnectionData      mConstData;

		INGwSpMsgSipSpecificAttr* sipAttr;

		SipAddrSpec* mContactAddSpec;
		SipAddrSpec* mFromAddSpec;

		INGwSpSipConnTcapTransInfo mTcapTransInfo;

  public:

		void setTcapTransInfo(INGwSpSipConnTcapTransInfo& p_TcapTransInfo)
		{
			mTcapTransInfo.m_stackDlgId = p_TcapTransInfo.m_stackDlgId;
			mTcapTransInfo.m_appId = p_TcapTransInfo.m_appId;
			mTcapTransInfo.m_instId = p_TcapTransInfo.m_instId;
			mTcapTransInfo.m_isDialogueComplete = p_TcapTransInfo.m_isDialogueComplete;
      mTcapTransInfo.m_suId = p_TcapTransInfo.m_suId;
      mTcapTransInfo.m_spId = p_TcapTransInfo.m_spId;
      mTcapTransInfo.m_seqNum = p_TcapTransInfo.m_seqNum;
		}

		void getTcapTransInfo(INGwSpSipConnTcapTransInfo& p_TcapTransInfo)
		{
			p_TcapTransInfo.m_stackDlgId = mTcapTransInfo.m_stackDlgId;
			p_TcapTransInfo.m_appId = mTcapTransInfo.m_appId;
			p_TcapTransInfo.m_instId = mTcapTransInfo.m_instId;
			p_TcapTransInfo.m_isDialogueComplete = mTcapTransInfo.m_isDialogueComplete;
      p_TcapTransInfo.m_suId = mTcapTransInfo.m_suId;
      p_TcapTransInfo.m_spId = mTcapTransInfo.m_spId;
      p_TcapTransInfo.m_seqNum = mTcapTransInfo.m_seqNum;
		}

    void setContactAddSpec(SipAddrSpec* p_ContactAddSpec)
		{
			mContactAddSpec = p_ContactAddSpec;
    }

		SipAddrSpec* getContactAddSpec()
		{
			return mContactAddSpec;
		}

    void setFromAddSpec(SipAddrSpec* p_FromAddSpec)
		{
			mFromAddSpec = p_FromAddSpec;
    }

		SipAddrSpec* getFromAddSpec()
		{
			return mFromAddSpec;
		}

		inline void setActiveEPPort(int piActiveEPPort)
		{
			miActiveEPPort = piActiveEPPort;
		}

		inline int getActiveEPPort()
		{
			return miActiveEPPort;
		}

		const std::string & getLocalCallId(void);
		void setLocalCallId(const char *aStr);
		const std::string & getCallId(void) const;

		const char *getGwIPAddress(void) ;
		void setGwIPAddress(const char* apcIPAddr, int len = -1);

		void setGwPort(int aPort);
		int getGwPort();

		INGwSipConnMajorState getMajorState(void) const;
		void setMajorState(INGwSipConnMajorState aeState);

		const char *getContactHdr(void) ;
		void setContactHdr(const char* apcContactHdr, int len = -1);

		INGwSpAddress& getAddress(INGwSpSipConnection::AddressType aeAddrType);
		void setAddress(INGwSpSipConnection::AddressType aeAddrType,
										const INGwSpAddress& arAddr);
    int getPrivacyMask();
    void setPrivacyMask(int aiMask);

    void setCall(INGwSpSipCall* apCall);
    INGwSpSipCall& getCall(void);
    void resetCall(void);

    inline bool isDead(void) {return mbIsDead;}
    inline void setDead(bool aIsDead)
    {
      mbIsDead = aIsDead;
    }

    inline int getSelfConnectionId(void) {return msSelfConnId;}
    inline void setSelfConnectionId(int p_SelfConnId) 
		{
			 msSelfConnId = p_SelfConnId;
		}

    inline int getPeerConnectionId(void) {return -1;}

    inline void setPeerConnectionId(int p_PeerConnId) {}

		inline INGwSpMsgSipSpecificAttr* getSipSpecificAttr()
		{
			 if(sipAttr == NULL)
			 {
					sipAttr = new INGwSpMsgSipSpecificAttr();
			 }
			 return sipAttr;
		}

   private:

      //static RSI_NSP_CCM::VersionHolder *_verHolder;

   public:

      static void setVersionDetail();

   public:

			void handleConnectionFailure();
}; 

class INGwSpSipConnection_Init {

  public :

    INGwSpSipConnection_Init() { }
    void operator() (INGwSpSipConnection* s) 
    { 
       s->setCounters();
    }
};

class INGwSpSipConnection_Reuse {

  public :

    INGwSpSipConnection_Reuse() { }

    void operator() (INGwSpSipConnection* s) 
    { 
       s->reset();
       s->initObject();
    }
};

class INGwSpSipConnection_MemMgr {

  public :

    INGwSpSipConnection_MemMgr() { }

    INGwSpSipConnection * allocate(int)
    {
       return new INGwSpSipConnection();
    }

    void deallocate(INGwSpSipConnection* s) 
    { 
       delete s;
       return;
    }
};

#endif //INGW_SP_SIP_CONNECTION_H_
