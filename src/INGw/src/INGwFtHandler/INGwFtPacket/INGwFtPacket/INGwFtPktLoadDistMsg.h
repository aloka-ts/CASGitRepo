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
//     File:    INGwFtPktLoadDistMsg.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya    31/01/08     Initial Creation
//********************************************************************
#ifndef INGW_FT_PKT_LOADDIST_MSG_H_
#define INGW_FT_PKT_LOADDIST_MSG_H_

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <string>
#include <vector>
#include <INGwCommonTypes/INCCommons.h>
using namespace std;

const short REGISTER_SAS_APP   = 1;
const short DEREGISTER_SAS_APP = 2;
const short DELETE_SAS_APP     = 3;
typedef struct LoadDistInfo {
	U32 m_pc;
	U8	m_ssn;

	char m_sasIp[10][50];
	int	 m_sasIpCnt;

	LoadDistInfo() {
		m_sasIpCnt = 0;
		for(int i=0; i < 10; ++i) {
			memset(&m_sasIp[i], 0, 50);
		}
	}

} t_loadDistInfo;

class INGwFtPktLoadDistMsg : public INGwFtPktMsg
{
   public:

      INGwFtPktLoadDistMsg(void);

      virtual ~INGwFtPktLoadDistMsg();

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

      void 
			initialize(short p_loadDistType, short srcid, short destid);

      std::string 
			toLog(void) const;

			short 
			getLoadDistMsgType();

			int
			appendSasDestAddrToList(vector<string> &p_sasIP, U32 &p_pc, U8 &p_ssn);

			int
			appendSasDestAddrToList(string &p_sasIP, U32 &p_pc, U8 &p_ssn);

			vector<t_loadDistInfo>
			getLoadDistInfo();

			void
			getLoadDistInfo(vector<t_loadDistInfo> &info);

			int
			getLdDistMsgCnt();

			void
			getPcSsnAtIndex(U32 &p_pc, U8 &p_pssn, int &p_sasIpCnt, int &p_indx);

			string
			getIpFromInfoAtIndex(int &p_infoIndx, int &p_ipIndx);

   protected:

			short m_LoadDistMsgType;
			vector<t_loadDistInfo> m_info;


   private:

      INGwFtPktLoadDistMsg& operator= (const INGwFtPktLoadDistMsg& arSelf);
      INGwFtPktLoadDistMsg(const INGwFtPktLoadDistMsg& arSelf);
};

#endif // INGW_FT_PKT_ROLE_MSG_H_

