//********************************************************************
//
//     File:  INGwAppProtoParamDecoder.C 
//
//     Desc:  Binary Decoder Class for INAP Parameters
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 04/03/13       Initial Creation
//********************************************************************
#include <INGwTcapProvider/INGwAppProtoParamDecoder.h>
#include <INGwTcapProvider/INGwAppProtoInclude.h>
#include <stdio.h>

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwAppProtoParamDecoder");

unsigned char* splitTag(unsigned char* pcTag, int piTag)
{
  pcTag[2] = (unsigned char) piTag & 0x00FF0000;
  pcTag[1] = (unsigned char) piTag & 0x0000FF00;
  pcTag[0] = (unsigned char) piTag & 0x000000FF;
  
  return pcTag; 
}

void NonASNBillingId :: decode(unsigned char* pcParamBuff)
{

  memcpy(&origMarketId, pcParamBuff, SIZE_OF_SHORT);

  origSwitchNo = pcParamBuff[2];
  IdNumber = 0;

  IdNumber = (IdNumber | (pcParamBuff[3])); IdNumber<<=8;
  IdNumber = (IdNumber | (pcParamBuff[4])); IdNumber<<=8;
  IdNumber = (IdNumber | (pcParamBuff[5]));
    
  segmentCntr = pcParamBuff[6];
}

void NonASNBillingId :: logBillingId(int piDialogueId)
{
  char lcBuf[256] = {0,};
  int liLen = 0;

  liLen += sprintf(lcBuf, "\n--billing Id-- %d",piDialogueId); 
  liLen += sprintf(lcBuf, "\norigMarketId <%d>", origMarketId);
  liLen += sprintf(lcBuf + liLen, "\norigSwitchNo <%d>", origSwitchNo);
  liLen += sprintf(lcBuf + liLen, "\nIdNumber     <%d>", IdNumber);
  liLen += sprintf(lcBuf + liLen, "\nsegmentCntr  <%d>", segmentCntr);
  logger.logINGwMsg(false, VERBOSE_FLAG,0,"%s", lcBuf); 

}
