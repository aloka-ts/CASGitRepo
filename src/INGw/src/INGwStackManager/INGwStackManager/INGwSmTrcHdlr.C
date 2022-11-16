/************************************************************************
     Name:     INAP Stack Manager Trace Handler - impl
 
     Type:     implementation
 
     Desc:     Implementation of the Trace Handler

     File:     INGwSmTrcHdlr.C

     Sid:      INGwSmTrcHdlr.C 0  -  03/27/03 

     Prg:      bd

************************************************************************/
//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

#include "INGwSmTrcHdlr.h"
//#include <StreamMgr/AppStreamer.h>
#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

using namespace std;

BpGenUtil::INGwIfrSmAppStreamer *gpINGwSmTrcStreamer;

static char* layerNameStr[] = {
  (char*)"UNKNOWN",    /* layer name unknown / invalid */
  (char*)"AIN/INAP",   /* BP_AIN_SM_AIN_LAYER */
  (char*)"TCAP",       /* BP_AIN_SM_TCA_LAYER */
  (char*)"SCCP",       /* BP_AIN_SM_SCC_LAYER */
  (char*)"M3UA",       /* BP_AIN_SM_M3U_LAYER */
  (char*)"SCTP",       /* BP_AIN_SM_SCT_LAYER */
  (char*)"TUCL",       /* BP_AIN_SM_TUC_LAYER */
  (char*)"MTP3",       /* BP_AIN_SM_MTP_LAYER */
  (char*)"INAP USER"   /* BP_AIN_SM_IU_LAYER  */
};

static char* sublayerNameStr[] = {
  (char*)"NA",         /* BP_AIN_SM_SUBLYR_NA  */
  (char*)"TCP",        /* BP_AIN_SM_SUBLYR_TCP */
  (char*)"UDP",        /* BP_AIN_SM_SUBLYR_UDP */
  (char*)"RAW",        /* BP_AIN_SM_SUBLYR_RAW */
  // M3UA MsgType names...
  (char*)"ASPSM",      /* BP_AIN_SM_SUBLYR_M3U_ASPSM */
  (char*)"ASPTM",      /* BP_AIN_SM_SUBLYR_M3U_ASPTM */
  (char*)"DATA",       /* BP_AIN_SM_SUBLYR_M3U_DATA */
  (char*)"MGMT",       /* BP_AIN_SM_SUBLYR_M3U_MGMT */
  (char*)"MSG",        /* BP_AIN_SM_SUBLYR_M3U_MSG */
  (char*)"RKM",        /* BP_AIN_SM_SUBLYR_M3U_RKM */
  (char*)"SSNM"        /* BP_AIN_SM_SUBLYR_M3U_SSNM */
};


//default constructor
INGwSmTrcHdlr::INGwSmTrcHdlr()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmTrcHdlr::INGwSmTrcHdlr");

  gpINGwSmTrcStreamer = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmTrcHdlr::INGwSmTrcHdlr");
}


//default destructor
INGwSmTrcHdlr::~INGwSmTrcHdlr()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmTrcHdlr::~INGwSmTrcHdlr");

  if (gpINGwSmTrcStreamer)
    delete gpINGwSmTrcStreamer;

  delete [] mpTrcFilePath;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmTrcHdlr::~INGwSmTrcHdlr");

}


//initialize the handler
int
INGwSmTrcHdlr::initialize(int aMaxTrcFileSz, char *apTrcFilePath)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmTrcHdlr::initialize");

  if (apTrcFilePath == 0)
    return (BP_AIN_SM_FAIL);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmTrcHdlr::initialize");

  mpTrcFilePath = new char[strlen(apTrcFilePath) + 1];

  strcpy (mpTrcFilePath, apTrcFilePath);

  gpINGwSmTrcStreamer = new BpGenUtil::INGwIfrSmAppStreamer("CCM_SS7_PDU", 
    mpTrcFilePath);

  if (gpINGwSmTrcStreamer == 0)
    return (BP_AIN_SM_FAIL);
  else
    gpINGwSmTrcStreamer->setLoggable (true);

  return (BP_AIN_SM_OK);
}

// method is invoked by INGwSmAdaptor to handle a
// PDU trace indication from a stack layer.
int
INGwSmTrcHdlr::handleTrace(INGwSmTrcInfo *aTrcInfo)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmTrcHdlr::handleTrace");

  if (aTrcInfo == 0)
    return (BP_AIN_SM_FAIL);

  int ret = logTrace(aTrcInfo);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmTrcHdlr::handleTrace");

  // could lead to various forms of value addition to
  // the trace info. currently we only log it in a file.
  return ret;
}


// log the trace info into file.
// note: this method is not a synchronized method, as
// we do not anticipate more than 1 thread writing to this
// file at the same time, as there is only 1 TAPA task, i.e.
// the AdaptorTask which is going to invoke handleTrace().
int
INGwSmTrcHdlr::logTrace(INGwSmTrcInfo *aTrcInfo)
{
  char *bufPtr;

  if ((aTrcInfo == 0) || ((bufPtr = aTrcInfo->maTrcBuf) == 0))
    return (BP_AIN_SM_FAIL);

  int len = aTrcInfo->miTrcLen;
  int bytesWritten=0;
  std::string lstrTrace;
  char *lpcTmpStr = new char [100];


  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmTrcHdlr::logTrace");


  lstrTrace = "------------------------------------------------------------\n";

  // the following trace printing logic can be improved to split a long trace
  // into multiple lines... : bd
  if (aTrcInfo->miDir == BP_AIN_SM_TRCDIR_RX) {
    snprintf (lpcTmpStr, 99, "%0.2d:%0.2d:%0.2d:%0.2d\t%s (%s) :: RX :: %d\n",
                          aTrcInfo->hour, aTrcInfo->min, 
                          aTrcInfo->sec, aTrcInfo->tenth,
                          layerNameStr[aTrcInfo->miLayerId],
                          sublayerNameStr[aTrcInfo->miSubLayerId], len);
  } 
  else {
    snprintf (lpcTmpStr, 100, "%0.2d:%0.2d:%0.2d:%0.2d\t%s (%s) :: TX :: %d\n",
                          aTrcInfo->hour, aTrcInfo->min, 
                          aTrcInfo->sec, aTrcInfo->tenth,
                          layerNameStr[aTrcInfo->miLayerId],
                          sublayerNameStr[aTrcInfo->miSubLayerId], len);
  }

  lstrTrace += lpcTmpStr;

  char lpcChar[5];

  for (int i=0; i < len; i++) 
  {

    snprintf (lpcChar, 4, "%0.2x ", (unsigned char) bufPtr[i]); // hex + space
    lstrTrace += lpcChar;
    if (((i+1) % 16) == 0)
    {
      lstrTrace += "\n";
    }
  }

  if (gpINGwSmTrcStreamer)
    gpINGwSmTrcStreamer->log ("%s\n", lstrTrace.c_str());

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmTrcHdlr::logTrace");

  delete [] lpcTmpStr;

  return (BP_AIN_SM_OK);
}
