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
//     File:     INGwIfrUtlMGblFunc.h
//
//     Desc:     Defines Macros
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_UTL_GBL_FUNC_H_
#define _INGW_IFR_UTL_GBL_FUNC_H_

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
//#include "INGwCommonTypes/INCCommons.h"
//#include "INGwTcapProvider/INGwInapParamTags.h"
#include <vector>
#include <string>
#include <sstream>

using namespace std;

vector<string> 
g_tokenizeValue(string p_value, string p_delim);

bool
g_tokenizeValue(string p_value, string p_delim, vector<string>& p_retVal);

U32
g_convertPcToDec(char *p_pcChar, U8* p_pcDetail, int p_protoType = 0 );

void
g_getCurrentTime(char* apcTime);

void
g_dumpMsg(const char *fileName, int line, const char* buf);

void
g_dumpMsg(const char *fileName, int line, string &buf);

void
g_dumpMsg(const char *fileName, int line, std::ostringstream &buf);

//1 ACTN_FIX 2 ACTN_VAR_ASC 3 ACTN_VAR_DES 4 ACTN_GT_TO_PC 5 ACTN_CONST
//6 ACTN_INSERT_PC 7 ACTN_STRIP_PC 8 ACTN_SEL_INS
string
g_getGtActionType(unsigned char type);

// 0 NO_GT_INCLUDED 1 GT_WITH_NAI 2 GT_WITH_TT 3 GT_WITH_TT_NP_ENC 
// 4 GT_WITH_TT_NP_ENC_NAI
string
g_getGtFormatType(unsigned char format);

// 1 ITU 2 ANSI 6 CHINA 7 JAPAN
string
g_getSccpProtoVariant(unsigned short var);

//1 ITU88 2 ITU92 3 ANS88 4 ANS92 5 ANS96 6 ETS96 7 ITU96 8 NTT_INTER_NW
// 9 NTT_INTRA_NW
string
g_getTcapProtoVariant(unsigned short var);

//1 ANSI 2 ITU 4 CHINA 5 BICI 6 ANS96 7 JAPAN-TTC 8 JAPAN-NTT
string
g_getMtp3ProtoVariant(unsigned short var);

// 1 ANSI 2 ITU 4 CHINA 5 BICI 6 ANS96 10 JAPAN-TTC 11 JAPAN-NTT
string
g_getM3uaProtoVariant1(unsigned short var);

// 1 ITU 4 ETSI 7 ANSI 8 JAPAN-TTC
string
g_getM3uaProtoVariant2(unsigned short var);

//1 DOMINANT,2 LOADSHARE,3 DOMINANT_ALTERNATE 
// 4 LOADSHARE_ALTERNATE
string
g_getSelectionMode(unsigned char mode);

// 0 INTERNATIONAL 1 SPARE 2 NATIONAL 3 RESERVED
string
g_getSsfType(unsigned char ssf);

// 1 LOCAL-SSN 2 REMOTE-SSN
string
g_getSsnType(unsigned char type);

// 1 OUTGOING 2 INCOMING
string
g_getRouteType(unsigned char rteType);

// 1 SINGLEENDED 2 DOUBLEENDED
string
g_getIpspMode(unsigned char mode);

// 1 DOMINANT 2 LOADSHARE
string
g_getOutAddrSelMode(unsigned char mode);

void 
getEnvVarInt(char *var, int *val);

void
g_exit(char * aFile, int aLine, char * msg);

string
g_convertIpLongToStr(U32 ll);

bool 
doReallocation(U8 **pBuf, int pSize, int pReallocSize = 512);
// For debugging - Start [
int
g_IncDiag(char * aFile, int aLine, char * reason, int scenario);
// ] End - For debugging

// Performance Enhancements
bool
g_isTraceEnabled();

bool
g_isVerboseEnabled();

void getMsgBuf(char * envVar, U8 ** octetBuff, unsigned short  * buffLen);
unsigned char getTcTransType(U8* apcMsgBody);

//template <class InapParam>
//InapParam &getNonAsnInapParam(PtrStr& pParamBuff, 
//                             InapParam& pInapParam)
//{
//  /*Index 2 MSB, Index 0 LSB*/
//  byte *tag = pInapParam.getTag();
//  int liParamLen = 0; 
//  
//  while ( liParamLen < pParamBuff.len )
//  {
//    if(   pParamBuff.string[liParamLen] >=  FIRST_OCTET_1B_MIN 
//       && pParamBuff.string[liParamLen] <=  FIRST_OCTET_1B_MAX)
//    {
//      if (tag[0] != pParamBuff.string[liParamLen]) 
//      {
//        liParamLen += pParamBuff.string[liParamLen + 1] + 2;
//      }
//      else
//      {
//        pInapParam.decode(&(pParamBuff.string[liParamLen + 2]));
//        return pInapParam;
//      }
//    }
//    if( pParamBuff.string[liParamLen] == FIRST_OCTET_2B)
//    {
//       liParamLen++; 
//       if( pParamBuff.string[liParamLen] <=  SEC_OCTET_2B_MAX
//           && pParamBuff.string[liParamLen] >=  SEC_OCTET_2B_MIN)
//       {
//         if (tag[1] != pParamBuff.string[liParamLen]) 
//         {
//           liParamLen += pParamBuff.string[liParamLen + 1] + 2;
//         }
//         else
//         {
//           pInapParam.decode(&(pParamBuff.string[liParamLen + 2]));
//           return pInapParam;
//         }           
//       }
//    }
//    else
//    {
//      throw EXCEPTION_UNKNOWN_TAG;
//    }
//
//    if(pParamBuff.string[liParamLen] <= SEC_OCTET_3B_MAX &&
//       pParamBuff.string[liParamLen] >= SEC_OCTET_3B_MIN)
//    {
//       liParamLen++;
//
//       if( pParamBuff.string[liParamLen] <= THIRD_OCTET_3B_MAX 
//           && pParamBuff.string[liParamLen] >=  THIRD_OCTET_3B_MIN )
//       {
//         if (tag[2] != pParamBuff.string[liParamLen]) 
//         {
//           liParamLen += pParamBuff.string[liParamLen + 1] + 2;
//         }
//         else
//         {
//           pInapParam.decode(&(pParamBuff.string[liParamLen + 2]));
//           return pInapParam;
//         }           
//       }
//    }
//    else
//    {
//      throw EXCEPTION_UNKNOWN_TAG;
//    }
//  }
//
//  throw  EXCEPTION_TAG_NOT_FOUND; 
//} 
#endif
