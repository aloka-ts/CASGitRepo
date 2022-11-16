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
//     File:     INGwIfrUtlGblFunc.C
//
//     Desc:     Defines Macros
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraUtil");

#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#include <INGwCommonTypes/INCTags.h>
#include "INGwCommonTypes/INCJainConstants.h"
#include <Util/LogMgr.h>
#include <sys/time.h>
#include <unistd.h>
#include <signal.h>
#include "stu.h"
// For debugging - Start [
extern bool g_enableDiagLogs;
// ] End - For debugging

extern void myStringToByteArray(std::string str, unsigned char* array, int& size);

vector<string> 
g_tokenizeValue(string p_value, string p_delim)
{
  vector<string> retVal;  

	if ( true == p_value.empty()) {    
		return retVal;  
	}

  int size = p_value.size();

  int pos =0;
  int curPos =0;

  while ( pos < p_value.size()){
    pos = p_value.find (p_delim, pos);

    if(pos != -1) {
      retVal.push_back(p_value.substr(curPos, (pos-curPos)));
      curPos = pos+1;
      pos = pos+1;
    }
    else {
      retVal.push_back(p_value.substr(curPos, pos));
    }
  } 
    
  return retVal;
}

bool
g_tokenizeValue(string p_value, string p_delim, vector<string>& p_retVal)
{
	if ( true == p_value.empty()) {    
		return false;  
	}

  int size = p_value.size();

  int pos =0;
  int curPos =0;

  while ( pos < p_value.size()){
    pos = p_value.find (p_delim, pos);

    if(pos != -1) {
      p_retVal.push_back(p_value.substr(curPos, (pos-curPos)));
      curPos = pos+1;
      pos = pos+1;
    }
    else {
      p_retVal.push_back(p_value.substr(curPos, pos));
    }
  } 
    
  return true;
}

U32
g_convertPcToDec(char *p_pcChar, U8* p_pcDetail, int p_protoType)
{
  if(NULL == p_pcChar) {
    return 0;
  }
   
  char buf[50];
  strcpy(buf, p_pcChar);
 
  // It is assumed that the point code will be
  // in zone-Network-SP format  U8 pc[3];
  U8 pc[3];
  int maxCheck[3];
  int minCheck[3] = {0, 0, 0};

	if (SW_ANSI == p_protoType) {
    // ANSI 8Bit-8Bit-8Bit format
  	maxCheck[0] = 255;
  	maxCheck[1] = 255;
  	maxCheck[2] = 255;
	}
	else if (SW_ITU == p_protoType) {
    // ITU 3Bit-8Bit-3Bit format
  	maxCheck[0] = 7;
  	maxCheck[1] = 255;
  	maxCheck[2] = 7;
	}
	else if (SW_JAPAN == p_protoType || 8 == p_protoType) {
    // JAPAN-NTT 7Bit-4Bit-5Bit format
  	maxCheck[0] = 127;
  	maxCheck[1] = 15;
  	maxCheck[2] = 31;
	}
	else if (SW_CHINA == p_protoType) {
    // CHINA 3Bit-8Bit-3Bit format
  	maxCheck[0] = 7;
  	maxCheck[1] = 255;
  	maxCheck[2] = 7;
	}
	else {
    printf("%s:%d--protoType:NOT FOUND-%d\n", 
           __FILE__, __LINE__, p_protoType);
	}


  U32 retVal =0;

  int pcLen = strlen(buf);
  char *curPtr = NULL;
  char *prvPtr = NULL;

  curPtr = buf;
  prvPtr = buf;

  int j=0;
  int i=0;
  int count =0;

  for (i=0, j=0; ((i < pcLen+1) && (count != 3)); ++i) {
    if ('-' == *curPtr || NULL == *curPtr ) {
      count += 1;
      *curPtr = NULL;
      int val = atoi(prvPtr);

      if (minCheck[j] > val || maxCheck[j] < val ) {
        return 0;
      }

      pc[j] = (val & 0x000000ff);
      j++;
      curPtr++;
      prvPtr = curPtr;
      continue;
    }

    curPtr++;
  }

  if ( count != 3 || i < pcLen+1) {
    return 0;
  }

	if (SW_ANSI == p_protoType) {
  	retVal = (pc[0] << 16);
  	retVal |= (pc[1] << 8);
  	retVal |= pc[2];
	}
	else if (SW_ITU == p_protoType) {
  	retVal = (pc[0] << 11);
  	retVal |= (pc[1] << 3);
  	retVal |= pc[2];
	}
	else if (SW_JAPAN == p_protoType || 8 == p_protoType) {
  	retVal = (pc[0] << 9);
  	retVal |= (pc[1] << 5);
  	retVal |= pc[2];
	}

	p_pcDetail[0] = pc[0];
	p_pcDetail[1] = pc[1];
	p_pcDetail[2] = pc[2];

  return (retVal);
}

//char*
//first character of the parameter byte array 
//decides format
void
g_getCurrentTime(char* apcTime)
{
	//char buf[64];
	//memset(buf, 0, sizeof(buf));
	struct timeval currTime;
	struct tm localTime;
  int timeChars = 0;

	gettimeofday(&currTime, NULL);
	localtime_r(&currTime.tv_sec, &localTime);
  if(apcTime[0] == '1') {
	  timeChars = strftime(apcTime, 64,"%C", &localTime);
  }
  else {
	  timeChars = strftime(apcTime, 64, "%H:%M:%S", &localTime);
  }
	sprintf(apcTime+ timeChars, ".%03d", currTime.tv_usec/1000);
	//return buf;
}

void
g_dumpMsg(const char *fileName, int line, const char* buf)
{
  bool timeReq = true; // this flag is required to specify
                       // whether time is needed or nanosec is needed
	char lpcTime[64];
	memset(lpcTime, 0, sizeof(lpcTime));
  lpcTime[0] = '1';

  if(buf != NULL)
  {
    if(timeReq)
		{
      g_getCurrentTime(lpcTime);
      printf("\n%s-%s-%s[%d]-%s\n", "Dump", lpcTime, 
			(fileName != NULL)?fileName:"NULL", line, buf);
		}
    else
		{
      printf("\n%s-%lld-%s[%d]-%s\n", "Dump", gethrtime(), 
			(fileName != NULL)?fileName:"NULL", line, buf);
		}

    fflush(stdout);
  }
}

void
g_dumpMsg(const char *fileName, int line, string &buf)
{
  bool timeReq = true; // this flag is required to specify
                       // whether time is needed or nanosec is needed
	char lpcTime[64];
	memset(lpcTime, 0, sizeof(lpcTime));
  lpcTime[0] = '1';

	if(!buf.empty())
  {
    if(timeReq)
		{ 
      g_getCurrentTime(lpcTime);
      printf("\n%s-%s-%s[%d]-%s\n", "Dump", lpcTime, 
			(fileName != NULL)?fileName:"NULL", line, buf.c_str());
		}
    else {
      printf("\n%s-%lld-%s[%d]-%s\n", "Dump", gethrtime(), 
			(fileName != NULL)?fileName:"NULL", line, buf.c_str());
    }

    fflush(stdout);
  }

}

void
g_dumpMsg(const char *fileName, int line, std::ostringstream &buf)
{
  bool timeReq = true; // this flag is required to specify
                       // whether time is needed or nanosec is needed
	char lpcTime[64];
	memset(lpcTime, 0, sizeof(lpcTime));
  lpcTime[0] = '1';

    if(timeReq)
		{
      g_getCurrentTime(lpcTime);
      printf("\n%s-%s-%s[%d]-%s\n", "Dump", lpcTime, 
			(fileName != NULL)?fileName:"NULL", line, (buf.str()).c_str());
		}
    else {
      printf("\n%s-%lld-%s[%d]-%s\n", "Dump", gethrtime(), 
			(fileName != NULL)?fileName:"NULL", line, (buf.str()).c_str());
    }

    fflush(stdout);
}

//1 ACTN_FIX 2 ACTN_VAR_ASC 3 ACTN_VAR_DES 4 ACTN_GT_TO_PC 5 ACTN_CONST
//6 ACTN_INSERT_PC 7 ACTN_STRIP_PC 8 ACTN_SEL_INS
string
g_getGtActionType(unsigned char type)
{
	string oStr;
	switch(type)
	{
		case 1: oStr = "ACTN_FIX"; break;
		case 2: oStr = "ACTN_VAR_ASC"; break;
		case 3: oStr = "ACTN_VAR_DES"; break;
		case 4: oStr = "ACTN_GT_TO_PC"; break;
		case 5: oStr = "ACTN_CONST"; break;
		case 6: oStr = "ACTN_INSERT_PC"; break;
		case 7: oStr = "ACTN_STRIP_PC"; break;
		case 8: oStr = "ACTN_SEL_INS"; break;
		default: oStr = "Unknown"; break;
	}
	return oStr;
}

// 0 NO_GT_INCLUDED 1 GT_WITH_NAI 2 GT_WITH_TT 3 GT_WITH_TT_NP_ENC 
// 4 GT_WITH_TT_NP_ENC_NAI
string
g_getGtFormatType(unsigned char format)
{
	string oStr;
	switch(format)
	{
		case 0:  oStr ="NO_GT_INCLUDED"; break;
		case 1:  oStr ="GT_WITH_NAI"; break;
		case 2:  oStr ="GT_WITH_TT"; break;
		case 3:  oStr ="GT_WITH_TT_NP_ENC"; break;
		case 4:  oStr ="GT_WITH_TT_NP_ENC_NAI"; break;
		deafult: oStr ="Unknown"; break;
	}
	return oStr;
}

// 1 ITU 2 ANSI 6 CHINA 7 JAPAN
string
g_getSccpProtoVariant(unsigned short var)
{
	string oStr;
	switch(var)
	{
		case 1: oStr = "ITU"; break;
		case 2: oStr = "ANSI"; break;
		case 6: oStr = "CHINA"; break;
		case 7: oStr = "JAPAN"; break;
		default: oStr = "Unknown"; break;
	}
	return oStr;
}

//1 ITU88 2 ITU92 3 ANS88 4 ANS92 5 ANS96 6 ETS96 7 ITU96 8 NTT_INTER_NW
// 9 NTT_INTRA_NW
string
g_getTcapProtoVariant(unsigned short var)
{
	string oStr;
	switch(var)
	{
		case 1: oStr = "ITU88"; break;
		case 2: oStr = "ITU92"; break;
		case 3: oStr = "ANS88"; break;
		case 4: oStr = "ANS92"; break;
		case 5: oStr = "ANS96"; break;
		case 6: oStr = "ETS96"; break;
		case 7: oStr = "ITU96"; break;
		case 8: oStr = "NTT_INTER_NW"; break;
		case 9: oStr = "NTT_INTRA_NW"; break;
		default: oStr = "Unknwon"; break;
	}
	return oStr;
}

//1 ANSI 2 ITU 4 CHINA 5 BICI 6 ANS96 7 JAPAN-TTC 8 JAPAN-NTT
string
g_getMtp3ProtoVariant(unsigned short var)
{
	string oStr;
	switch(var)
	{
		case 1:  oStr = "ANSI";      break;
		case 2:  oStr = "ITU";       break;
		case 4:  oStr = "CHINA";     break;
		case 5:  oStr = "BICI";      break;
		case 6:  oStr = "ANS96";     break;
		case 7:  oStr = "JAPAN-TTC"; break;
		case 8:  oStr = "JAPAN-NTT"; break;
		default: oStr = "Unknown";   break;
	}
	return oStr;
}

// 1 ANSI 2 ITU 4 CHINA 5 BICI 6 ANS96 10 JAPAN-TTC 11 JAPAN-NTT
string
g_getM3uaProtoVariant1(unsigned short var)
{
	string oStr;
	switch(var)
	{
		case 1: oStr = "ANSI"; break;
		case 2: oStr = "ITU"; break;
		case 4: oStr = "CHINA"; break;
		case 5: oStr = "BICI"; break;
		case 6: oStr = "ANS96"; break;
		case 10: oStr = "JAPAN-TTC"; break;
		case 11: oStr = "JAPAN-NTT"; break;
		default: oStr = "Unknown"; break;
		break;
	}
	return oStr;
}

// 1 ITU 4 ETSI 7 ANSI 8 JAPAN-TTC
string
g_getM3uaProtoVariant2(unsigned short var)
{
	string oStr;
	switch(var)
	{
		case 1: oStr = "ITU"; break;
		case 4: oStr = "ETSI"; break;
		case 7: oStr = "ANSI"; break;
		case 8: oStr = "JAPAN-TTC"; break;
		default:oStr ="Unknown"; break;
	}
	return oStr;
}

//1 DOMINANT,2 LOADSHARE,3 DOMINANT_ALTERNATE 
// 4 LOADSHARE_ALTERNATE
string
g_getSelectionMode(unsigned char mode)
{
	string oStr;
	switch(mode)
	{
		case 1: oStr = "DOMINANT"; break;
		case 2: oStr = "LOADSHARE"; break;
		case 3: oStr = "DOMINANT_ALTERNATE"; break;
		case 4: oStr = "LOADSHARE_ALTERNATE"; break;
		default: oStr = "Unknown"; break;
	}
	return oStr;
}

// 0 INTERNATIONAL 1 SPARE 2 NATIONAL 3 RESERVED
string
g_getSsfType(unsigned char ssf)
{
	string oStr;

	if(ssf == 0)
		oStr = "INTERNATIONAL";
	else if(ssf == 1)
		oStr = "SPARE";
	else if(ssf == 2)
		oStr = "NATIONAL";
	else if(ssf == 3)
		oStr = "RESERVED";
	else
		oStr = "Unknown";

	return oStr;
}

// 1 LOCAL-SSN 2 REMOTE-SSN
string
g_getSsnType(unsigned char type)
{
	string oStr;

	if(type == 1)
		oStr = "LOCAL-SSN";
	else if(type == 2)
		oStr = "REMOTE-SSN";
	else
		oStr = "Unknown";

	return oStr;
}

// 1 OUTGOING 2 INCOMING
string
g_getRouteType(unsigned char rteType)
{
	string oStr;

	if(rteType == 1)
		oStr = "OUTGOING";
	else if(rteType == 2)
		oStr = "INCOMING";
	else
		oStr ="Unknown";

	return oStr;
}

// 1 SINGLEENDED 2 DOUBLEENDED
string
g_getIpspMode(unsigned char mode)
{
	string oStr;

	if(mode == 1)
		oStr = "SINGLEENDED";
	else if(mode == 2)
		oStr = "DOUBLEENDED";
	else
		oStr = "Unknown";

	return oStr;
}

// 1 DOMINANT 2 LOADSHARE
string
g_getOutAddrSelMode(unsigned char mode)
{
	string oStr;

	if(mode == 1)
		oStr = "DOMINANT";
	else if(mode == 2)
		oStr = "LOADSHARE";
	else
		oStr = "Unknown";

	return oStr;
}

void
getEnvVarInt(char *var, int *val)
{
  int mMtp2Var = 0; //MTP2 DLSAP config parameters 
   char * lcMtp2Var = getenv(var);
   if (lcMtp2Var) {
     mMtp2Var = atoi(lcMtp2Var);
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
          "getEnvVarInt()::EnvVar %s value is:%d ",
          var, mMtp2Var);
     *val = mMtp2Var; 
   }
   else {
     *val = mMtp2Var;
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "getEnvVarInt()::EnvVar %s not defined. "
            "Setting to default value %d",var,val);
   }
}

void
g_exit(char * aFile, int aLine, char * msg)
{
  logger.logMsg (ERROR_FLAG, 0, "g_exit():: S H U T T I N G   D O W N. "
         "Problem at File<%s> Line<%d> Reason<%s>",
         aFile, aLine, msg);
  sleep(3);
  raise(9);
}

string
g_convertIpLongToStr(U32 ll)
{
	long d = ll % 256; ll -= d; ll /= 256;
	long c = ll % 256; ll -= c; ll /= 256;
	long b = ll % 256; ll -= b; ll /= 256;
	long a = ll % 256; ll -= a; ll /= 256;

	char buf[20];
	memset(buf, 0, sizeof(buf));
	sprintf(buf, "%d.%d.%d.%d", a, b, c, d);

	return buf;
}

bool 
doReallocation(U8 **pBuf, int pSize, int pReallocChunkSize) {
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In doReallocation");
    U8* lTempBuf = *pBuf;

    int size = pSize;
    *pBuf = new (nothrow) U8[size + pReallocChunkSize];

    if(NULL == *pBuf) 
    {
      return false;
    }
    
    logger.logINGwMsg(false,ALWAYS_FLAG,0,
    "doReallocation newBuf<%x> oldBuf<%x> size<%d> reallocSize<%d>",
    *pBuf, lTempBuf, size, pReallocChunkSize);

    memmove((void*)(*pBuf),(void*)lTempBuf, size); 
    delete[] lTempBuf;
    lTempBuf = NULL;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out doReallocation");
}


// For debugging - Start [
extern "C" void *
g_diag(void * aArgs)
{
  LogINGwTrace(false, 0, "Entering g_diag()");
  int len = strlen((char *)aArgs);
  sprintf(((char *)aArgs + len), ",%d", pthread_self());

  vector <string> strArgs; 
      g_tokenizeValue(std::string((char *) aArgs),std::string(","), strArgs);

  
  if (strArgs.size() == 0) {
    logger.logMsg (ERROR_FLAG, 0, "g_diag():: "
           "At least scenario argument should be passed");
  }
  else {
    char fileName[257];
    int lineNum = 0;
    char reason[257];
    int scenario = 0;
    int threadId = 0;
    char action[32];

    strcpy(fileName, (char *)strArgs[0].c_str());
    lineNum = atoi((char *)strArgs[1].c_str());
    strcpy(reason, (char *)strArgs[2].c_str());
    scenario = atoi((char *)strArgs[3].c_str());
    strcpy(action, (char *)strArgs[4].c_str());
    threadId = atoi((char *)strArgs[5].c_str());

    char cmd[1024];
    memset(cmd, 0, sizeof(cmd));
    sprintf(cmd, "../scripts/DiagScr.sh %s %d %d %s %d %s",
            action, scenario, threadId, fileName, lineNum, reason);

    logger.logMsg (TRACE_FLAG, 0, "g_diag():: "
           "At File<%s> Line<%d> Reason<%s> Sce<%d>",
           fileName, lineNum, reason, scenario);
    
    system(cmd);
  }
  LogINGwTrace(false, 0, "Exiting g_diag()");
  return 0;
}

int
g_IncDiag(char * action, char * aFile, int aLine, char * reason, int scenario) {
   LogINGwTrace(false, 0, "Entering g_IncDiag()");

   if (!g_enableDiagLogs) {
     logger.logMsg(ALWAYS_FLAG, 0, "Diag not enabled. "
            "Action<%s> File<%s> Line<%d> Reason<%s> Scenario<%d>",
            action, aFile, aLine, reason, scenario);
     LogINGwTrace(false, 0, "Exiting g_IncDiag()");
     return 0;
   }

   int fileNameLen = 0;
   int reasonLen = 0;
   if (((fileNameLen = strlen(aFile)) > 256) || ((reasonLen = strlen(reason)) > 256)) {
     logger.logMsg(ERROR_FLAG, 0,
                   "fileNameLen<%d> or reasonLen<%d> greater than 256",
                   fileNameLen, reasonLen);
     LogINGwTrace(false, 0, "Exiting g_IncDiag()");
     return 0;
   }

   char largs[1024];
   
   sprintf(largs, "\"%s\",%d,\"%s\",%d,\"%s\"", (aFile)?aFile:"", aLine, 
           (reason)?reason:"", scenario, action);

   pthread_t diagThr;
   if(0 != pthread_create(&diagThr, NULL, g_diag,
                          (void *)largs)) {
      logger.logMsg(ERROR_FLAG, 0, "Error creating Thread diag");
      return 1;
   }
   else {
      logger.logMsg(ALWAYS_FLAG, 0, "Thread to get diag");
   }

   LogINGwTrace(false, 0, "Exiting g_IncDiag()");
   return 0;
}
// ] End - For debugging


void getMsgBuf(char * envVar, U8 ** octetBuff, unsigned short  * buffLen)
{
  char* byteBuff = getenv(envVar);
  
  string byteBuffStr;

  if (byteBuff == NULL) {
    if(0 == strcmp(envVar,"NTT_UABORT_USR_INFO")) {
      byteBuffStr = std::string("280f06080283386603020600a0030a0100");
    }
    else if(0 == strcmp(envVar,"NTT_RC_PARAM_BUF")) {
      byteBuffStr = std::string("040283A9");
    }
    else {
      byteBuffStr = std::string("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
    }

    logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGwIfrUtlGlbFunc::getMsgBuf()"
           "EnvVar<%s> not defined, Using default<%s>", envVar, byteBuffStr.c_str());
 
  }
  else {
    byteBuffStr = std::string(byteBuff);
  }
  
  int length = byteBuffStr.length();
  // make sure the input string has an even digit numbers
  if(length%2 == 1) {
  	byteBuffStr = "0" + byteBuffStr;
  	length++;
  }
  
  // allocate memory for the output array
  *octetBuff = new unsigned char[length/2 + 2];
  *buffLen = length/2;

  int len = *buffLen;
  
  myStringToByteArray(byteBuffStr, *octetBuff, len);

  char usrInfoByte[256];
  int l = 0;
  for (int i = 0;  i < len; i++) {
     l += sprintf((usrInfoByte + l), " 0x%02x", (*octetBuff)[i]);
  }
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "%s[%s]", envVar, usrInfoByte);
}

unsigned char getTcTransType(unsigned char *apcMsgBody)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In remft getTcTransType");
  U8 lcTransType = 255;
  int lLenOffset = 0;

  if(0x00 != (apcMsgBody[1] & 0x80)){
    lLenOffset = (apcMsgBody[1]) - 128;
  }

  lcTransType = apcMsgBody[2 + lLenOffset];
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
  char *lpcDlgType =(lcTransType == JAIN_BEGIN)? "QWP":
                    (lcTransType == JAIN_CONTINUE)?"STU_CONVERSATION":
                    (lcTransType == JAIN_END)?"STU_RESPONSE":
                    (lcTransType == JAIN_PREARR_END)?"STU_RESPONSE_PREARR":
                    (lcTransType == JAIN_USER_ABORT)?"STU_ANSI_U_ABORT":
                    (lcTransType == JAIN_UNIDIRECTIONAL)?"STU_UNI":
                    (lcTransType == JAIN_PROVIDER_ABORT)?"STU_P_ABORT":"UNKNOWN";
#else
  char *lpcDlgType =(lcTransType == JAIN_BEGIN)? "INC_BEGIN":
                    (lcTransType == JAIN_CONTINUE)?"INC_CONTINUE":
                    (lcTransType == JAIN_END)?"INC_END":
                    (lcTransType == JAIN_PREARR_END)?"INC_END":
                    (lcTransType == JAIN_USER_ABORT)?"INC_U_ABORT":
                    (lcTransType == JAIN_UNIDIRECTIONAL)?"INC_UNI":
                    (lcTransType == JAIN_PROVIDER_ABORT)?"INC_P_ABORT":"UNKNOWN";
#endif

  logger.logINGwMsg(false,TRACE_FLAG,0,"Out remft getTcTransType<%d> <%s>",
                                       lcTransType, lpcDlgType);
  return lcTransType; 
}

bool
g_isTraceEnabled()
{
	bool retVal = false;
  logger.logINGwMsg(false,TRACE_FLAG,0,"In isTraceEnabled()");

	LogMgr::LogParam logParam;
	LogMgr::instance().getLogParam(logParam);

	if(logParam.logLevel == Logger::TRACE )
		retVal = true;

  logger.logINGwMsg(false,TRACE_FLAG,0,
										"Out isTraceEnabled(), retVal:%d",retVal);
	return retVal;
}

bool
g_isVerboseEnabled()
{
	bool retVal = false;
  logger.logINGwMsg(false,TRACE_FLAG,0,"In isVerboseEnabled()");

	LogMgr::LogParam logParam;
	LogMgr::instance().getLogParam(logParam);

	if(logParam.logLevel == Logger::VERBOSE )
		retVal = true;

  logger.logINGwMsg(false,TRACE_FLAG,0,
										"Out isVerboseEnabled(), retVal:%d", retVal);

	return retVal;
}
