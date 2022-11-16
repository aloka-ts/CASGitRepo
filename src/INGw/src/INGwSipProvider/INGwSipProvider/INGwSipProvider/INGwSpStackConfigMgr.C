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
//     File:     INGwSpStackConfigMgr.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipProvider.h>

#include <INGwSipProvider/INGwSpThreadSpecificSipData.h>

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>

#include <INGwSipProvider/INGwSpStackConfigMgr.h>
#include <INGwSipProvider/INGwSpSipListenerThread.h>

#define MAX_USERPROFILE_SIZE 10000

INGwSpStackConfigMgr* INGwSpStackConfigMgr::m_selfPtr = NULL;

static bool isListnerStarted = false;

INGwSpStackConfigMgr*
INGwSpStackConfigMgr::getInstance()
{
	if(NULL == m_selfPtr)
	{
		m_selfPtr = new INGwSpStackConfigMgr();
  }
	return m_selfPtr;
}

int 
INGwSpStackConfigMgr::initializeSipStack()
{
  LogINGwTrace(false, 0, "IN initializeSipStack");
  Sdf_ty_initToolkitParams initparams;
  Sdf_st_error             sdferror;
  Sdf_ty_retVal            status = Sdf_co_fail;

  // initparams.dNumPreAllocMsgBuffers = 500000;
  // initparams.dPreAllocBufSize = 1024;

  status = sdf_ivk_uaInitToolkit(&initparams, &sdferror);
  if(status == Sdf_co_success)
  {
    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                    "SIP Stack Initialized successfully");
  }
  else
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                    "Error initializing the stack.");
    INGwSpSipUtil::checkError(status, sdferror);

    LogINGwTrace(false, 0, "OUT initializeSipStack");
    return -1;
  }

  sip_stackSetDecodeHeaderTypes(&m_HdrsTobeDecodedList);

  // Enable stack trace and error messages.
  status = sdf_ivk_uaSetTraceLevel(Sdf_en_allTraceLevels, &sdferror);
  if( status != Sdf_co_success){
    INGwSpSipUtil::checkError(status, sdferror);
  }

  status = sdf_ivk_uaSetTraceType(Sdf_en_uaAllTraces, &sdferror);
  if( status != Sdf_co_success){
    INGwSpSipUtil::checkError(status, sdferror);
  }
  std::string filename = "";
  INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_USER_PROFILE, 
																									 filename);
  logger.logINGwMsg(false, TRACE_FLAG, 0,
                  "initializeSipStack: reading the user profile [%s]",
                  filename.c_str());

  if(filename.empty())
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                    "initializeSipStack: ERROR: userProfileFileName is NULL");
    LogINGwTrace(false, 0, "OUT initializeSipStack");
    return -1;
  }

  status = readUserProfile(filename.c_str(), &pGlbProfile, &sdferror);
  if(status != Sdf_co_success)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                    "ERROR: Error reading the user profile for sip stack");
    LogINGwTrace(false, 0, "OUT initializeSipStack");
    return -1;
  }
  else
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0,
                    "User Profile successfully read.");
  }


  {
    //Setting stack timers.
    std::string valStr;
    INGwIfrPrParamRepository &rep = INGwIfrPrParamRepository::getInstance();

    rep.getValue(sipTIMER_T1_IN_MSECS, valStr);

    if(!valStr.empty())
    {
      logger.logMsg(ALWAYS_FLAG, 0, "Processing T1 [%s]", valStr.c_str());
      SIP_T1 = atoi(valStr.c_str());

      if(SIP_T1 == 0)
      {
        SIP_T1 = SIP_DEFAULT_T1;
      }
    }

    logger.logMsg(ALWAYS_FLAG, 0, "Timer T1 [%d]", SIP_T1);
    valStr.clear();

    rep.getValue(sipTIMER_T2_IN_MSECS, valStr);

    if(!valStr.empty())
    {
      logger.logMsg(ALWAYS_FLAG, 0, "Processing T2 [%s]", valStr.c_str());
      SIP_T2 = atoi(valStr.c_str());

      if(SIP_T2 == 0)
      {
        SIP_T2 = SIP_DEFAULT_T2;
      }
    }

    logger.logMsg(ALWAYS_FLAG, 0, "Timer T2 [%d]", SIP_T2);
    valStr.clear();


    rep.getValue(sipMAX_RETRAN, valStr);

    if(!valStr.empty())
    {
      logger.logMsg(ALWAYS_FLAG, 0, "Processing MaxRetran [%s]",
                    valStr.c_str());
      SIP_MAXRETRANS = atoi(valStr.c_str());

      if(SIP_MAXRETRANS == 0)
      {
        SIP_MAXRETRANS = SIP_DEFAULT_MAX_RETRANS;
      }
    }

    logger.logMsg(ALWAYS_FLAG, 0, "MaxRetran [%d]", SIP_MAXRETRANS);
    valStr.clear();

    rep.getValue(sipMAX_INV_RETRAN, valStr);

    if(!valStr.empty())
    {
      logger.logMsg(ALWAYS_FLAG, 0, "Processing MaxInvRetran [%s]",
                    valStr.c_str());
      SIP_MAXINVRETRANS = atoi(valStr.c_str());

      if(SIP_MAXINVRETRANS == 0)
      {
        SIP_MAXINVRETRANS = SIP_DEFAULT_INV_RETRANS;
      }
    }

    logger.logMsg(ALWAYS_FLAG, 0, "MaxInvRetran [%d]", SIP_MAXINVRETRANS);
    valStr.clear();
  }


  /************************************************************************/
  // There are some fields in the user profile which need to be set from
  // configuration instead of being read from a user profile xml file.
  // Do those modifications here.

  // Replace the from header from configuration.
  std::string fromuser = "ingw";
  std::string fromaddr = "";
  std::string fromport = "";
  INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_FROMINFO_USERNAME, 
																									 fromuser);
  INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR, fromaddr);
  INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT, 
																									 fromport);

  if(fromuser.empty() || fromaddr.empty() || !atoi(fromport.c_str()))
	{
    logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
                    "initializeSipStack: From info is not available."
                    "  From address will not be configurable.");
  }
  else
  {
    // Make a from header.
    SipHeader* fromhdr =
        INGwSpSipUtil::makeFromHeader
          (fromuser.c_str(), fromaddr.c_str(), atoi(fromport.c_str()));
    if(!fromhdr)
		{
      logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
											"initializeSipStack: Error creating from header."
											"  From address will not be configurable.");
		}
    else
    {
      // Replace the from header in the user profile with the one we just made
      pGlbProfile->pFrom = fromhdr;
      logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
                      "initializeSipStack: Overriding from hdr with "
											"username <%s>, addr <%s>, port <%d>",
                      fromuser.c_str(), fromaddr.c_str(), 
											atoi(fromport.c_str()));
    }

    SipError l_sipError;
    if(0 != pGlbProfile->pTransport->pIp) {
      sdf_memfree(0, (void**)(&(pGlbProfile->pTransport->pIp)), &sdferror);
      pGlbProfile->pTransport->pIp = Sdf_mc_strdupConfig(fromaddr.c_str());
    }
    else {
      pGlbProfile->pTransport->pIp = Sdf_mc_strdupConfig(fromaddr.c_str());
    }

    pGlbProfile->pTransport->dPort = atoi(fromport.c_str());

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
                    "VIA HEADER IP:<%s>, PORT:<%d>", 
										pGlbProfile->pTransport->pIp,
                    pGlbProfile->pTransport->dPort);

  } // end of else

  // Replace the contact header from configuration.
  std::string contactuser = "";
  std::string contactaddr = "";
  std::string contactport = "";
  INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_CONTACTINFO_USERNAME,
																									 contactuser);
  INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR,contactaddr);
  INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT,
																									 contactport);

  if(contactuser.empty() || contactaddr.empty() || !atoi(contactport.c_str()))
	{
    logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
										"initializeSipStack: Contact info is not available."
										" Contact address will not be configurable.");
	}
  else
  {
    // Make a contact header.
    SipHeader* contacthdr =
        INGwSpSipUtil::makeContactHeader
          (contactuser.c_str(), contactaddr.c_str(), atoi(contactport.c_str()));
    if(!contacthdr)
		{
      logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
											"initializeSipStack: Error creating contact header."
											" Contact address will not be configurable.");
		}
    else
    {
      // Replace the contact hdr list in the user profile with the one we
      // just created.
      Sdf_st_error sdferror;
      sdf_listDeleteAll(&(pGlbProfile->slContact), &sdferror);
      sdf_listPrepend(&(pGlbProfile->slContact),
                      (void *)contacthdr, &sdferror);
      logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
											"initializeSipStack : Overriding contact hdr"
											" with username <%s>, addr <%s>, port <%d>", 
											contactuser.c_str(), contactaddr.c_str(), 
											atoi(contactport.c_str()));
    }
  }

  /************************************************************************/

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
									"initializeSipStack: Setting callid generator function");
  // Set the callid generator function.
  sdf_ivk_uaSetDefaultCallIdGenerator(pGlbProfile,
                                      callidGeneratorFunction,
                                      &sdferror);

  LogINGwTrace(false, 0, "OUT initializeSipStack");
  return 0;
}

int 
INGwSpStackConfigMgr::startListenerThread(int p_Port, char* p_Host, 
                                          void** p_TSDArray)
{
  LogINGwTrace(false, 0, "IN startListenerThread");

  if(isListnerStarted)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
										 "startListenerThread: listener already started, "
										 "Not starting again");
    return 0;
  }
  else
  {
    isListnerStarted = true;
  }

  bindFloatingIP(p_TSDArray);

  Sdf_st_error sdferror;
  if(0 != pGlbProfile->pTransport->pIp) {
    sdf_memfree(0, (void**)(&(pGlbProfile->pTransport->pIp)), &sdferror);
  }

  pGlbProfile->pTransport->pIp = Sdf_mc_strdupConfig(p_Host);

  pGlbProfile->pTransport->dPort = p_Port;

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
      "VIA HEADER IP:<%s>, PORT:<%d>", pGlbProfile->pTransport->pIp,
      pGlbProfile->pTransport->dPort);


 // Replace the from header from configuration.
    std::string fromuser = "";
    std::string fromaddr = "";
    std::string fromport = "";
    INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR, fromaddr);
    INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT, 
																										 fromport);
    if(fromuser.empty() || fromaddr.empty() || !atoi(fromport.c_str()))
    {
      logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
                      "startListenerThread: From info is not available. "
											" From address will not be configurable.");
    }
    else
    {
      // Make a from header.
      SipHeader* fromhdr =
        INGwSpSipUtil::makeFromHeader
          (fromuser.c_str(), fromaddr.c_str(), atoi(fromport.c_str()));

      if(!fromhdr)
      {
        logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
												"startListenerThread: Error creating from header."
												"  From address will not be configurable.");
      }
      else
      {
        // Replace the from header in the user profile with the one we just made
        pGlbProfile->pFrom = fromhdr;
        logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
              "Overriding from hdr with username <%s>, addr <%s>, port <%d>",
              fromuser.c_str(), fromaddr.c_str(), atoi(fromport.c_str()));
      }

    } // end of else


    // Replace the contact header from configuration.
    std::string contactuser = "";
    std::string contactaddr = "";
    std::string contactport = "";

    INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_CONTACTINFO_USERNAME,
																										 contactuser);
    INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR,
																										 contactaddr);
    INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_STACK_LISTENER_PORT,
																										 contactport);

    if(contactuser.empty() || contactaddr.empty() || !atoi(contactport.c_str()))
      logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
										 "startListenerThread: Contact info is "
										 "not available.  Contact address will not be configurable.");
    else
    {
      // Make a contact header.
      SipHeader* contacthdr =
        INGwSpSipUtil::makeContactHeader
          (contactuser.c_str(), contactaddr.c_str(), atoi(contactport.c_str()));
      if(!contacthdr)
        logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
												"startListenerThread: "
												"Error creating contact header. "
												"Contact address will not be configurable.");
      else
      {
        // Replace the contact hdr list in the user profile with the one we
        // just created.
        Sdf_st_error sdferror;
        sdf_listDeleteAll(&(pGlbProfile->slContact), &sdferror);
        sdf_listPrepend(&(pGlbProfile->slContact),
                        (void *)contacthdr, &sdferror);
        logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
											  "startListenerThread: Overriding "
												"contact hdr with username <%s>, addr <%s>, port <%d>",
                        contactuser.c_str(), 
												contactaddr.c_str(), atoi(contactport.c_str()));
      }
    }

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
									"startListenerThread: port <%d>, host <%s>", 
									p_Port, p_Host);
  if(!m_SipListenerThread.initialize(p_Port, p_Host))
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
										"startListenerThread: "
										"error initializing the sip network listener thread\n");
    LogINGwTrace(false, 0, "OUT startListenerThread");
    return -1;
  }

  try
  {
    m_SipListenerThread.start();
    m_SendSock = socket(AF_INET, SOCK_DGRAM, 0);
  }
  catch(...)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
										"startListenerThread: "
										"error starting the sip network listener thread\n");
    LogINGwTrace(false, 0, "OUT startListenerThread");
    return -1;
  }

  LogINGwTrace(false, 0, "OUT startListenerThread");
  return 0;
}

void 
INGwSpStackConfigMgr::sendToNetwork(void* p_Buffer, int p_Buflen, 
																		char* p_Addr, int p_Port)
{
  LogINGwTrace(false, 0, "IN sendToNetwork");

  struct sockaddr_in servaddr;
  servaddr.sin_family = AF_INET;
  servaddr.sin_port   = htons(p_Port);
  inet_pton(AF_INET, p_Addr, &servaddr.sin_addr);
  pthread_key_t& l_key = INGwSpSipProvider::getInstance().getKey();
  void* l_voidTsd = pthread_getspecific(l_key);

  if(0 != l_voidTsd)
  {
    INGwSpThreadSpecificSipData* l_tsd =
                                static_cast<INGwSpThreadSpecificSipData*>(l_voidTsd);
    sendto(l_tsd->getSendSocket(), p_Buffer, p_Buflen, 0,
           (sockaddr *)&servaddr, sizeof(servaddr));
  }
  else
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                    "TSD null from pthread_getspecific...Major corruption");
    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                    "EXITING....Quitting deliberatly.");
    LogINGwTrace(false, 0, "OUT sendToNetwork");
    exit(1);
  }
  LogINGwTrace(false, 0, "OUT sendToNetwork");
}

SipHdrTypeList& 
INGwSpStackConfigMgr::getStackDecodeHeaderTypes()
{
  return m_HdrsTobeDecodedList;
}

INGwSpStackConfigMgr::INGwSpStackConfigMgr()
{
  initDecodedHeaderList();
}

INGwSpStackConfigMgr::INGwSpStackConfigMgr(
										 const INGwSpStackConfigMgr& p_StackConfigMgr)
{
  m_HdrsTobeDecodedList = p_StackConfigMgr.m_HdrsTobeDecodedList;
}

INGwSpStackConfigMgr& 
INGwSpStackConfigMgr::operator=(const INGwSpStackConfigMgr& p_StackConfigMgr)
{
  m_HdrsTobeDecodedList = p_StackConfigMgr.m_HdrsTobeDecodedList;
  return *this;
}

void
INGwSpStackConfigMgr::bindFloatingIP(void** p_TSDArray)
{
  LogINGwTrace(false, 0, "IN bindFloatingIP");
  INGwIfrMgrThreadMgr& l_ThreadMgr = INGwIfrMgrThreadMgr::getInstance();
  int l_thrCnt = l_ThreadMgr.getThreadCount();

  for(int i= 0; i < l_thrCnt; ++i)
  {
    INGwSpThreadSpecificSipData *currData = 
               (INGwSpThreadSpecificSipData *)p_TSDArray[i];

    int sendSocket = currData->getSendSocket();

    std::string floatingIP =
           INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR);

    struct sockaddr_in serv_addr;
    memset(&serv_addr, 0, sizeof(sockaddr_in));
    serv_addr.sin_family = AF_INET;

    logger.logMsg(TRACE_FLAG, 0, "FloatingIP. [%s] and SOCKET [%d]",
                 floatingIP.c_str(), sendSocket);

    if(floatingIP.empty())
    {
       serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    }
    else
    {
      serv_addr.sin_addr.s_addr = inet_addr(floatingIP.c_str());

      if(serv_addr.sin_addr.s_addr == -1)
      {
        logger.logMsg(ERROR_FLAG, 0, "Invalid IP in floatingIP. [%s]",
                      floatingIP.c_str());

        serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
      }
    }
    serv_addr.sin_port = 0;

    if(bind(sendSocket, (sockaddr *)&serv_addr, sizeof(sockaddr_in)) < 0)
    {
      logger.logMsg(ERROR_FLAG, 0, "Error binding to FloatingIP [%s] [%s]",
                    floatingIP.c_str(), strerror(errno));
       printf("Error binding listener.\n");
       exit(1);
    }
  }
  LogINGwTrace(false, 0, "OUT bindFloatingIP");
}

Sdf_ty_retVal 
INGwSpStackConfigMgr::readUserProfile(const char*       p_Filename,
										                  Sdf_st_initData** p_AppProfile,
										                  Sdf_st_error*     p_Err)
{
  LogINGwTrace(false, 0, "IN readUserProfile");
  Sdf_ty_s8bit   *pProfileFileName = (Sdf_ty_s8bit*)p_Filename;
  Sdf_ty_u32bit   dCount;
  Sdf_ty_s8bit    pInput[MAX_USERPROFILE_SIZE];
  FILE           *fp;
  Sdf_ty_retVal   status = Sdf_co_fail;

  // Read the contents of the user profile file into the buffer.
  sdf_ivk_uaInitInitData(p_AppProfile, p_Err);
  fp=fopen(pProfileFileName, "r");
  if(!fp)
  {
    printf("ERROR: Error opening user profile file <%s>\n", p_Filename);
    LogINGwTrace(false, 0, "OUT readUserProfile");
    return Sdf_co_fail;
  }

  dCount = 0;
  while(!feof(fp))
  {
    pInput[dCount]=fgetc(fp);
    dCount++;
  } // end of while
  pInput[dCount-1]='\0';
  fclose(fp);

  // Now parse the file buffer and create the user profile structure.
  status = sdf_ivk_uaParseConfigData(&pInput[0], dCount-1, *p_AppProfile, p_Err);

  LogINGwTrace(false, 0, "OUT readUserProfile");
  return status;

}

void
INGwSpStackConfigMgr::initDecodedHeaderList()
{

   // Enable all required headers.  All other header will be treated as
   // unknown.

   for(int i = 0; i < HEADERTYPENUM; i++)
   {
      m_HdrsTobeDecodedList.enable[i] = SipFail;
   }

   m_HdrsTobeDecodedList.enable[SipHdrTypeFrom           ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeTo             ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeCallId         ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeCseq           ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeVia            ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeContentType    ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeContentLength  ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeContactAny     ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeContactNormal  ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeContactWildCard] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeRequire        ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeRSeq           ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeSessionExpires ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeRAck           ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeUnknown        ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeSupported      ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeMinSE          ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeDcsRemotePartyId] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeDcsAnonymity   ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeRoute        ] = SipSuccess;
   m_HdrsTobeDecodedList.enable[SipHdrTypeRecordRoute        ] = SipSuccess;
}

