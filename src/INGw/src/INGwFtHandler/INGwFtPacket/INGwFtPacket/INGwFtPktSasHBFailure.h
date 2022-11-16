//********************************************************************
//
//     File:    INGwFtPktSasHBFailure.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 23/02/12       Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_HANDLE_HB_FAILURE_H_
#define INGW_FT_PKT_HANDLE_HB_FAILURE_H_

#include <INGwFtPacket/INGwFtPktMsgDefine.h>
#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwCommonTypes/INCCommons.h>

   
class INGwFtPktSasHBFailure : public INGwFtPktMsg
{
   public:

      INGwFtPktSasHBFailure(void);

      virtual ~INGwFtPktSasHBFailure();

      int depacketize(const char* apcData, int asSize, int version);

      int packetize(char** apcData, int version);

      void initialize(string p_ipAddr, AppInstId p_appId,
                                   short srcid, short destid);

      std::string toLog(void) const;
      void getSasIp(string &p_ipAddr);
      void getAppId(AppInstId &p_appId); 
   private:
      char m_ipAddr[50];
      AppInstId m_appId;
      INGwFtPktSasHBFailure& operator= (const INGwFtPktSasHBFailure& arSelf);
      INGwFtPktSasHBFailure(const INGwFtPktSasHBFailure& arSelf);

};

#endif // INGW_FT_PKT_HANDLE_HB_FAILURE_H_

