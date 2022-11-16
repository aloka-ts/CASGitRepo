//////////////////////////////////////////////////////////////////////////
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
//     File:    INGwFtPktTcapMsg.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya    31/01/08     Initial Creation
//********************************************************************
//////////////////////////////////////////////////////////////////////////
#ifndef INGW_FT_PKT_TCAP_MSG_H_
#define INGW_FT_PKT_TCAP_MSG_H_

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <string>
#include "INGwTcapProvider/INGwTcapInclude.h"
#include "INGwCommonTypes/INCCommons.h"
#include "INGwCommonTypes/INCJainConstants.h"
#include "INGwCommonTypes/INCTags.h"
using namespace std;

class TcapMessage;

void addByte(U8* pBuffer, int &pLen, U8 pByte);
void addInteger(U8* pBuf, int &pLen, int pVal);
void addShort(U8* pBuffer, short pVal, int &pLen);
void encodeBuffer(U8* pBuf, int pContentLen,int &pBufLen,U8* pContentBuf);
void encodeContentLength(U8* pBuf, int &pBufLen, int pIndex, int pContentLen);

inline void getOctetCount(int pVal, U8 &pOctetLen) {
  pOctetLen= 0x00;
  while(pVal) 
  {
    pVal>>=8;
    pOctetLen++;
  }
}

class INGwFtPktTcapMsg : public INGwFtPktMsg
{
   public:

      INGwFtPktTcapMsg(void);

      virtual ~INGwFtPktTcapMsg();

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);
      
      int
      serialize(U8* p_buf, int &p_bufLen);

      bool encodeAnsiDlgEv(StAnsiDlgEv &pStAnsiDlgEv,
                           unsigned char* pcBuf, 
                           int &piLen);
      
      void 
			initialize(int stackDialogue, 
                 int userDialogue, 
							   const SccpAddr &srcaddr,
                 const SccpAddr &destaddr, 
					       const string &ipaddr, 
                 AppInstId &appid,

                 #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                 StAnsiDlgEv p_stAnsiDlgEv,
                 int p_billingNo,
                 #else
                 INcStr p_objAcn,
                 #endif

								 short p_srcId, 
                 short p_dstId);

      std::string 
			toLog(bool lbPrintFtInfo = true) const;

			int 		  	m_stackDialogue;
			int			  	m_userDialogue;
			SccpAddr  	m_srcAddr;
			SccpAddr    m_dstAddr;
			string 	  	m_ipAddr;
			AppInstId   m_appId;

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
      StAnsiDlgEv m_stAnsiDlgEv;
			int			  	m_billingNo;
#else
      INcStr      m_objAcn;
#endif
      
   private:

      INGwFtPktTcapMsg& operator= (const INGwFtPktTcapMsg& arSelf);
      INGwFtPktTcapMsg(const INGwFtPktTcapMsg& arSelf);
};

#endif // INGW_FT_PKT_ROLE_MSG_H_
