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
//     File:     INGwIfrMgrThreadMgr.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************


#ifndef _INGW_IFR_MGR_THREAD_MGR_H_
#define _INGW_IFR_MGR_THREAD_MGR_H_

#include <unistd.h>
#include <pthread.h>

#include <list>

class INGwIfrMgrWorkerThread;
class INGwIfrMgrWorkUnit;
class INGwIfrMgrWorkerClbkIntf;

/* This class manages all the CMM worker threads and and one house keeping
* (HK) thread.
*/

class INGwIfrMgrThreadMgr
{
   public:

      static INGwIfrMgrThreadMgr& getInstance();
      virtual ~INGwIfrMgrThreadMgr();

      int initialize(void);
      int shutdown(void);
      int configure(const char* apcOID, const char* apcValue);
      int startWorkerThread(void);
      int stopWorkerThread(bool abIsGraceful = true);

      const long* getWorkerThreadId(void) const 
      { 
         return mplThreadId; 
      }

      inline long getThreadCount(void) const 
      { 
         return mlThreadCount; 
      }

      inline long getWorkerThreadCount(void) const
      {
         return mlWorkerThreadCount;
      }

      int setWorkerTSD(const char* apcKeyStr, void** apData, 
                       pthread_key_t* apKey);
      int startTimer(int aiDuration, void* apContext, INGwIfrMgrWorkerClbkIntf* apClbk, 
                     unsigned int& auiTimerId);
      int stopTimer(unsigned int& auiTimerId, int aiThreadId = -1);

      bool postMsg(INGwIfrMgrWorkUnit* apWork, bool chkFlag = false);

      bool postMsg(INGwIfrMgrWorkUnit* apWork, int aiThreadIdx, 
                                               bool chkFlag = false);

      void postBulkMsg(INGwIfrMgrWorkUnit **apWorkList, int count);

      void informMsg(INGwIfrMgrWorkUnit **apWork);

      bool postMsgForHK(INGwIfrMgrWorkUnit* apWork, bool chkFlag = false);

      bool postMsgForReplay(INGwIfrMgrWorkUnit* apWork, int pDlgId,
                            bool chkFlag = false);

      bool postMsgForTakeOverCalls(INGwIfrMgrWorkUnit* apWork, int pDlgId, 
                                   bool chkFlag = false);

      bool postMsgForSipReq(INGwIfrMgrWorkUnit* apWork, bool chkFlag = false);

      void postOutOfBandMsg(INGwIfrMgrWorkUnit* apWork = false);

      void postOutOfBandMsgForHK(INGwIfrMgrWorkUnit* apWork);

      void printStats(void);

      INGwIfrMgrWorkerThread & getCurrentThread();

      int
      getReplayThreadIdx(int pDlgId);       

      INGwIfrMgrWorkerThread& getThreadSpecificData();

   protected:

      INGwIfrMgrThreadMgr();

      long            mlHKThreadIndex;
      long            mlSipReqThread;
      long            mlWorkerThreadCount;
      long            mlThreadCount;
      long            mlReplayThreadCount;
      long            mlReplayThreadIndex;
      long*           mplThreadId;
       
      INGwIfrMgrWorkerThread* mpThreadArray;

      pthread_mutex_t           mKeyListMutex;
      std::list<pthread_key_t*> mKeyList;

      pthread_key_t   mSelfKey;

      bool                   mbIsInitialized;
      static INGwIfrMgrThreadMgr* mpSelf;

      inline int _getTargetThread(unsigned long hashID)
      {
         return (hashID%97)%mlWorkerThreadCount;
      }


   private:

      INGwIfrMgrThreadMgr(const INGwIfrMgrThreadMgr& p_Self);
      INGwIfrMgrThreadMgr& operator=(const INGwIfrMgrThreadMgr& p_Self);
};

#endif
