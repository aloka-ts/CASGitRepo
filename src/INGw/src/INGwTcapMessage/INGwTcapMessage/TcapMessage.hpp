#ifndef __TCAPMESSAGE
#define __TCAPMESSAGE

#define SUNOS

#include "INGwCommonTypes/INCCommons.h"
#include "INGwTcapProvider/INGwTcapInclude.h"
#include "INGwCommonTypes/INCTagLengths.h"
#include "INGwCommonTypes/INCJainConstants.h"
#include "INGwCommonTypes/INCTags.h"
#include "INGwTcapProvider/INGwTcapWorkUnitMsg.h"
#include "INGwTcapProvider/INGwTcapMsgLogger.h"
#include <INGwTcapProvider/INGwTcapFtHandler.h>
#include <INGwTcapProvider/INGwTcapSession.h>
#include <INGwTcapProvider/INGwAppProtoParamDecoder.h>
#include "INGwFtPacket/INGwFtPktUpdateTcapSession.h"
#include <list>
#include <map>
#include <vector>
#include <INGwInfraUtil/INGwIfrUtlBitArray.h>

class INGwTcapMsgLogger;
using namespace std;
#define ALLOCATION_CHUNK 256 
#define SIZE_OF_INT   4
#define SIZE_OF_SHORT 2
#define SIZE_OF_LONG  4
#define MESSAGE_COMPLETE (INC_ROK + 1)
extern void * getUserId(SccpAddr &ownAddress);
extern void getAppId(SccpAddr &ownAddress, AppInstId &);
#ifdef PLANO_BUILD

#endif
//extern void  TcapUserInformationForSerialization(int stackDialogue, int userDialogue, const SccpAddr &srcaddr, const SccpAddr &destaddr, const string &ipaddr, AppInstId & appid,INcStr objAcn);

typedef struct t_INGwTcapWorkUnitMsg INGwTcapWorkUnitMsg;

extern int stack_log_mask;
extern int CCPU_LOG_MASK_INC_MSG;
 
//defined in INGwTcapFtHandler.C unit 
extern int gUpdateReplayCallCnt(int pDlgId);
extern bool gGetReplayFlg();

/* just a container class for user information */
class TcapUserAddressInformation
{
      public:
      TcapUserAddressInformation()
      {
        memset(&srcaddr,0,sizeof(SccpAddr));
        memset(&destaddr,0,sizeof(SccpAddr));
        mDlgType = 0;
        did = 0;
        userDlgId = 0;
        ipaddr = "";
        isDuplicated = false;
        suId = spId = -1;
      }

      TcapUserAddressInformation(
              int _did, 
              int _userDlgId, 
              const SccpAddr &_srcaddr, 
              const SccpAddr &_destaddr, 
              const string &_ipaddr, 
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
              StAnsiDlgEv p_ansiDlgEv, 
              NonASNBillingId &pBillingId, 
#else
              INcStr &p_objAcn,
#endif 
              S16 p_suId, 
              S16 p_spId, U8 pDlgType)
      {
            mDlgType = pDlgType;
            did = _did;
            mDlgType = pDlgType;
            userDlgId = _userDlgId;

            memcpy(&srcaddr, &_srcaddr, sizeof(SccpAddr));
            memcpy(&destaddr, &_destaddr, sizeof(SccpAddr));

            ipaddr = _ipaddr;
            isDuplicated = false;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
            memcpy(&ansiDlgEv, &p_ansiDlgEv, sizeof(StAnsiDlgEv));
            mBillingId = pBillingId;
#else
            memcpy(objAcn.string, p_objAcn.string, p_objAcn.len);
            objAcn.len = p_objAcn.len;

            //we are not duplicating if peer is up.

            if(stack_log_mask & CCPU_LOG_MASK_INC_MSG) {
              if(0 != p_objAcn.len ) {
                printf("\n[TUAI]mriganka+rem+--A C N--\n");
                printf("\n[TUAI]+rem+appConName len = %d\n",objAcn.len);
                for(int i = 0; i< objAcn.len; i++) {
                  printf("%02X ",objAcn.string[i]);
                }
                printf("\n");
                printf("\n[TUAI]+rem+--A C N--\n");
              }
              else{
                printf("\n[TUAI]+rem+--A C N--\n"
                       "\n[TUAI]+rem+--NULL --\n"
                       "\n[TUAI]+rem+--A C N---\n");
              }
            }
#endif 

            suId = p_suId;
            spId = p_spId;
      }

      bool isValid() const;
      int did;
      int userDlgId;
      SccpAddr srcaddr;
      SccpAddr destaddr;
      string ipaddr;
      INcStr objAcn;
      S16 suId;
      S16 spId;
      bool isDuplicated;
      U8 mDlgType; 

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      StAnsiDlgEv   ansiDlgEv;
      NonASNBillingId mBillingId;
#endif 

      void serialize(U8* pBuf, int &pOffset); 
      void deSerialize(U8* pBuf, int &pOffset); 
      void logTuai(char* str);
};

class TcapMessage
{
private:
      static int mCodecLogLevel;
      static INGwTcapMsgLogger *mLogger;
      int mBufSize; 
      int mReallocCount;
      string ipaddr;
      U32 dlgIdR;
      void setDialogue(U32 did);
      bool isAnsi();
      static int TcapMessage::createUserDialogue(int userDlgId, 
                                                     SccpAddr &srcaddr,
                                                     SccpAddr &destaddr,
                                                     const string & ipaddr);

			static void TcapMessage::removeDialogue(int userDlgId, int stackDialogue, 
                                              SccpAddr &useraddr, 
                                              AppInstId & appid,
                                              const string & ipaddr, 
                                              bool forStandby = false, 
                                              bool getLock = true);

      static void removeDialogueFt(int userDlgId, 
                                   int stackDialogue, 
                                   AppInstId & appid, const string & ipaddr, 
                                   bool getLock = true);
        
      static void storeDialogue(int userDlgId, int stackDialogue,
                                AppInstId &, const string & ipaddr,
                                TcapUserAddressInformation &tuai,
                                bool forStandby =false);
     /** decodeTcapDlg
      *  @params   p_dlgBuf        pointer to DLGTYPE tag in byte array
      *
      *            p_nextTag O ffset will be updated to total length of dialogue 
      *                            this can be used to jump to the next tag in byte array 
      *            p_dlg           dialogue will be decoded and populated in p_dlg  
      *  returns   true/false for success/failure
      */
      
         bool decodeTcapDlg(unsigned char* p_dlgBuf,
                            unsigned int &p_nextIndex, TcapDlg & p_dlg);
     /** decodeTcapCmp
      *  @params   p_cmpBuf        pointer to COMPONENT_TYPE tag in byte array
      *
      *            p_nextTagO ffset will be updated to total length of component
      *                            this can be used to jump to the next tag in byte array 
      *            p_compTx        component will be decoded and populated @p_compTx  
      *  returns   true/false for success/failure
      */      
         bool decodeTcapCmp(unsigned char* p_cmpBuf ,
                            unsigned int &p_NextTagIndex, 
                            TcapComp* p_compTx);

      void
      createCmpEvent(TcapComp **pCompTx,
                     U8  acCompType, 
                     U8  acInvId, 
                     U8  acOpType, 
                     U8  acOpCode,
                     U8  acOpCodeLen, 
                     U8  acParamLen,
                     U8* acParamString,
                     U8  acOpClass);
      public:
      static void initDialogueStateHashMap(int pOffset,int pRange);
      
      static void auditDlg(vector<int> &avDlgIdList);

      static int getUserAddressByDlgId(vector<int> &pDlgList,
                                vector<TcapUserAddressInformation> &pTuaiList);
        
      bool      dlgClndByINGw;

      //1 only abort      
      //2 end and abort depending on transaction state
      static U8 lcdlgCloseOpt;

      static U8 lcAbrtCause;

      //returns 0 if dialogue is found
      //        1 if dialogue is uni
      //        2 if dialogue is not found
      static int getUserAddressByDlgId(int pDlgId, 
                                       TcapUserAddressInformation& pTuai);
      static INGwIfrUtlBitArray* cloneDialogueStateMap();
      vector<TcapComp*> mCompVector;
      //make this a friend function of class INGwTcapFtHandler
     /** decode
      *  @Params   recBuf, byte array received from SAS
      *            BufLen, length of byte array
      *            p_dlg,  dialogue will be decoded and populated in p_dlg
      *            p_compList, list of components
      *
      *  returns   true/false for success/failure
      */
      bool decode(unsigned char* recBuf ,unsigned int BufLen, 
                  TcapDlg &p_Dlg, vector<TcapComp*>& p_complist);

      static TcapUserAddressInformation & TcapMessage::getUserAddressBy(int stackDialogue, AppInstId & appid);
      static TcapUserAddressInformation & TcapMessage::getUserAddressBy(int userDialogue, const string & ipaddr);
     
      static 
      bool getObjAcn(U8* pBuf, 
                     int pBufLen, 
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                     StAnsiDlgEv &pAnsiDlgEv,
#else
                     INcStr &pObjAcn,
#endif
                     U8 pTag); 

      int 
      InitLogger();

      void 
      encodeContentLength(unsigned char* pBuf, int &pBufLen, int pIndex, int pContentLen);
      U8* TcapMessage::getBuffer(int pSize);

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      inline bool 
      encodeAnsiDlgEv(StAnsiDlgEv &pStAnsiDlgEv, U8* pcBuf, int &piLen);
#endif

      inline void 
      addByte(U8* pBuffer,int &pLen, U8 pByte);
 
      inline void 
      addInteger(U8* pBuffer, int &pLen, int pVal);

      inline void
      addShort(U8* pBuffer, short pVal, int &pLen);

      inline void
      doReAllocation(U8** pBuf, int &pLen, int reallocSize = -1); 

      void 
      encodeBuffer(U8* pBuf, int pContentLen,int &pBufLen,U8* pContentBuf);

      int 
      encodeSccpAddr(U8* pBuf,int &pBufLen, SccpAddr* pAddr, U8 pOrigin);

      inline int 
      encodeParamBuffer(U8* pBuf, int &pLen, PtrStr pParam, U8 pParamFlg);

      inline int 
      encodeInvokeId(U8* pBuf, int &pLen, TcapComp pComp);

      inline int 
      encodeLinkedId(U8* pBuf, int pLen, TcapComp pComp);

      inline void 
      decodeLength(U8* pBuf, int &pIndex, int &pDecodeLen);

      inline int 
      decodeValue(U8* pBuf, int pIndex, int pLen);

      int 
      decodeSccpAddr(U8* pBuf, int &pIndex, int &pAddrLen, SccpAddr* pAddr);
      public:

      
      /**
       * TcapMessage
       * public ctor
       *
       * @param  userId for this user.
       */
      TcapMessage(bool Itu=true);

      /**
       * TcapMessage
       * public ctor
       *
       * @param  ipaddr for receiver.
       */
      TcapMessage(const string & ipaddr, bool Itu=true);
      
      /**
       * TcapMessage
       * public dtor
       */
      virtual ~TcapMessage();
      inline void getOctetCount(int p_val, unsigned char &p_octetLen);
      /*
        release the memory acquired by TcapMessage
      */ 
      void 
      dumpObject(char* str);

      void releaseTcapMsg();
     /**
      * decodeNstate-req-event
      *  
      */ 
      bool decodeNStateReqEvent(unsigned char* p_recBuf,int& p_bufLen,
                                SccpAddr &p_affectedUser, U8 &p_us);
      static int
      TcapMessage:: dumpSpAddr(SccpAddr addr, string pDir, char* setBuffer = NULL, int pLen = 0);

      unsigned char* TcapMessage :: encode(TcapDlg currentDlg, vector<TcapComp*>* compVector,
                                     unsigned int* p_bufLen);                         

      /**
       * addReceivedContent
       * adds a TcapMessage message to the current object.
       *
       * user's call this function to add an additional component indication to an already existing
       * dialogue indication, this method call will notify the user when DONE.
       *
       * @param msg TcapMessage.
       *
       * @return int INC_ROK - if everything was ok.
       *             MESSAGE_COMPLETE - if everything was ok and the message is complete. (no more components)
       *             -1 - fatal error
       */
      int addReceivedContent(TcapMessage *msg);

      /**
       * processReceivedContent
       * decodes user content
       *
       * user's call this function with received data from a stream (that contains application/tcap)
       *
       * @return int INC_TRUE - if everything was ok.
       *             MESSAGE_COMPLETE - if everything was ok and the message is complete. (no more components)
       *             -1 - fatal error
       */
      int processReceivedContent(unsigned char *p_buf, unsigned int p_bufLen, int p_seqNum = -1);

      /**
       * providerAbort
       * generates a provider abort to be sent to tcap user
       *
       * @param  userDialogue - user's dialogue id.
       * @param  p_abort::value - reason for abort.
       */
      //INCTBD
      void providerAbort(int userDialogue, U8 pa);
  
      inline
      vector<TcapComp*>* getCompVector(){
        return &mCompVector;
      }
     
      inline 
      void setCompVector(vector<TcapComp*>* pCompVector){
          mCompVector.swap(*pCompVector); 
      }
      /**
       * getUserAddress
       * returns the addressing information that is associated with this message (and app user)
       *
       * @return TcapUserAddressInformation user is responsible for checking validity of return.
       *   for it could be that this message is from the stack and begins a new dialogue.
       */

      bool getUserAddress(string &returnAddress);
       
      /**
       * assignUserToDialogue
       * assigns an tcap user to this new dialogue indication from stack.
       *
       * @param  ipaddr the user's ip address.
       */
      //INCTBD
      void assignUserToDialogue(const string & ipaddr);

      /**
       * Initialize the library called once from static initialization
       */     
      //INCTBD not required
      static void Initialize();
      
      /**
       * Terminate the library called once from static destruction
       */     
      //INCTBD not required
      static void Terminate();
 
      /**
       * becomeActive
       * called when user of library detects transition to active state from standby.
       * attempts to take over dialogues from active tcap user
       *
       */
      //INCTBD
      //static void becomeActive();

      /**
       * failedTakeover
       *  Tcap stack has indicated a problem with dialogue takeover 
       *  returned TcapMessage should be sent to user destination (getUserAddress).
       *
       */
      //static TcapMessage * failedTakeover(int stackDialogue, void *userId, AppInstId & appid, bool Itu=true);

      /**
       * createConfiguration
       * creates a list of local-address-uesrs.
       *
       * @param  list of SccpAddr addresses, U32* for getting the length of buffer
       *
       * @return pointer to encoded buffer 
       */
      unsigned char* createConfiguration(/*const*/ std::list<SccpAddr> &addrlist,
                                                unsigned int* p_bufLen);

      /**
       * createNpcstateInd
       * creates a <npcstate-ind-event/>
       *
       * @param  ownaddr - own point code.
       * @param  affectedDpc - affected point code.
       * @param  signaling_point_status - status
       *     sps DESTINATION_ACCESSIBLE,
       *     sps DESTINATION_CONGESTED,
       *     sps DESTINATION_INACCESSIBLE
       *     sps DESTINATION_CONGESTION_ABATEMENT
       * @return TcapMessage instance.
       */
      unsigned char* createNpcstateInd(SccpAddr &ownaddr, SccpAddr &affectedDpc, U8 sps, unsigned int* p_bufLen);

      /**
       * createNstateInd
       * creates a nstate-ind-event
       *
       * @param  ownaddr - own point code.
       * @param  affectedDpc - affected sccp address.
       * @param  user_status - status
       *     us USER_IN_SERVICE,
       *     us USER_OUT_OF_SERVICE,
       *
       * @return TcapMessage instance.
       */
      unsigned char * createNstateInd(SccpAddr &ownaddr, SccpAddr &affectedAddress,
                                             U8 us, unsigned int * p_bufLen);


      int TcapMessage::getDialogueFromRequest(TcapDlg& dre, 
                                              const string & _ipaddr, 
                                              INGwTcapWorkUnitMsg& p_workUnit,
                                              bool &p_isValid);
      /**
       * getDialogue
       * returns stack dialogue id for this message
       *
       * @return int dialogue id.
       */
      int getDialogue();
 
      //this will extract dialogueId and dialogueType
      void  
      extractDlgInfo(U8 *p_msgBody, int &pDlgId, U8 &pDlgType);
      /**
       * userAbort
       * generates a abort to be sent to stack
       *
       * @param  stackDialogue - stackDialogue dialogue id.
       * @param  appId - application and instance id.
       */
/*
			void userAbort(U8 abortReason, int stackDialogue, S16 p_suId, S16 p_spId, bool isFirstMsg=false, bool getLock = true, bool isHbFailClnup = false, bool auditFlg = false);
*/
      void closeDialogue(U8 abortReason,
                     int stackDialogue, 
                     S16 p_suId, S16 p_spId, 
                     bool isFirstMsg=false,
                     bool getLock = true, 
                     bool isHbFailClnup = false, 
                     bool auditFlg = false);
 
      /**
       * cleanup
       * cleanup map 
       *
       * @param  sasIp - sas address for which cleanup as to be called.
       */
      //INCTBD
			void cleanup(const string &sasIpAddr, AppInstId &appId, 
                   bool forStandBy = false);

      /**
       * storeUserInformationFT
       * called by user to store information obtained via -- extern void TcapUserInformationForSerialization
       */

      static void storeUserInformationFT(
                        int stackDialogue, 
                        int userDialogue, 
                        SccpAddr &srcaddr, 
                        SccpAddr &destaddr, 
                        const string &ipaddr, 
                        AppInstId  &appid, 
                        #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                        StAnsiDlgEv p_ansiDlgEv,
                        int piBillingNo
                        #else
                        INcStr &p_objAcn
                        #endif
                        ); 

      static void clearUserInformationFT(int userDlgId, int stackDialogue,
                  AppInstId & appid, const string & ipaddr, 
                  bool getLock = true);

   
      
      // This shall have the dialogue types for ITU and ANSI both
      U8 msg_type;
      U8 msgTypeR;
      bool compPresR;
      INcUOctet       pAbtCauseR;
      TcapDlg         dlgR;
      INcUOctet       rCauseR;
      TcapQosSet      qosR;
      stringBuff      uAbrtInfoR;
      TcapComp        *comp;
      AppInstId       appid;
      NonASNBillingId mBillingId;
      void * userId;
			bool dialogueClosed;
			S16 	m_suId;
			S16   m_spId;
      bool isRetransmission(U32 pDlgId, int pSeqNum);
      U8 getDlgType(U8* p_msgBody,char* pLogBuf,short &pLogBufLen, int pDlgId);

      static void 
      getAllActiveDlgs();

      static void 
      printCallMaps(bool pbGetLock=true);

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)   
      static void 
      dumpAnsiDlgEv(StAnsiDlgEv &pStAnsiDlgEv, char *pcBuf = NULL);
#endif
     
private:
      SccpAddr srcaddrR;
      SccpAddr dstaddrR;
      bool Itu;
};
#endif


