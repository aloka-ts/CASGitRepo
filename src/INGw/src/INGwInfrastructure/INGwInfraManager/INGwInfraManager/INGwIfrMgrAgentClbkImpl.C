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
//     File:     INGwIfrMgrAgentClbkImpl.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   07/12/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");

#include <unistd.h>
#include <Agent/BayAgentImpl.h>
#include <Util/LogMgr.h>

#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwInfraManager/INGwIfrMgrAgentClbkImpl.h>
#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwInfraUtil/INGwIfrUtlConfigurable.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraParamRepository/INGwIfrPrConfigMgr.h>
#include <INGwInfraMsrMgr/MsrMgr.h>
#include <EmsIdl/RSIEmsTypes_c.hh>
#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#include <INGwStackManager/INGwSmIncludes.h>


///////////////////////////////////////
#include <map>

extern bool configLoopBack;

class emsConfigInfo
{
	public:
		unsigned short	       nwId;		   // network Id
		unsigned short         proto;		   // Protocol Variant
		vector<unsigned int>   opcList;    // List of OPCs barring default OPC
		vector<string>         opcListStr; // List of OPCs barring default OPC
		vector<unsigned short> opcTypeList;// SS7=1, SIGTRAN=2
		unsigned int 				   defOpc;	   // Default OPC 
		string								 defOpcStr;  // Defauly OPC String
		unsigned short				 defOpcType; // SS7=1, SIGTRAN=2
		int									   mode;		   // Mode - HYbrid, SIGTRAN, Electrical
		vector<unsigned short> lsIdList;   // List of Linkset Ids for this Nw
		unsigned short				 dpcLen;

		unsigned short         mtp3SccpLsapId;
		unsigned short         m3uaSccpLsapId;

		emsConfigInfo():nwId(0), proto(0), 
						defOpc(0), mode(0), dpcLen(0), mtp3SccpLsapId(0),
            m3uaSccpLsapId(0) {}

		unsigned short getProto() { return proto;  }
		int getMode()             { return mode;   }
		int getNwId()             { return nwId;   }
		int getDefOpc()           { return defOpc; }
		int getDpcLen()           { return dpcLen; }

		bool doesOpcBelongs(string opc) {
			bool retVal = false;
			if(opc == defOpcStr) 
				retVal = true;

			for(int i=0; i < opcListStr.size() && retVal == false; ++i) {
				if(opc == opcListStr[i]) 
					retVal = true;
			}

			return retVal;
		}

		bool doesLsIdBelongs(unsigned short lsId) {
			bool retVal = false;
			for(int i=0; i < lsIdList.size(); ++i) {
				if(lsIdList[i] == lsId) {
					retVal = true;
					break;
				}
			}
			return retVal;
		}

		unsigned short getOpcType(string opc)
		{
			if(opc == defOpcStr)
				return defOpcType;

			for(int i=0; i < opcListStr.size(); ++i)
			{
				if(opc == opcListStr[i])
					return opcTypeList[i];
			}

			return 0;
		}

}; // end of emsConfigInfo

typedef std::map<int, emsConfigInfo*> StackConfigMap;
StackConfigMap g_configMap;

emsConfigInfo* getEmsConfigInfo(unsigned short nwId) {
	emsConfigInfo *conf = NULL;
	StackConfigMap::iterator it = g_configMap.find(nwId);
	if(it != g_configMap.end()) {
		conf = it->second;
	}
	return conf;
}

emsConfigInfo* getEmsConfigInfo(string opcStr) {
	emsConfigInfo *conf = NULL;
	unsigned short type =0;
	StackConfigMap::iterator it = g_configMap.begin();
	while(it != g_configMap.end()) {
		conf = it->second;
		if(conf->doesOpcBelongs(opcStr))
			break;
		else
			conf = NULL;

		it++;
	}
	return conf;
}

void removeEmsConfigEntry(unsigned short nwId) {
	StackConfigMap::iterator it = g_configMap.find(nwId);
	if(it != g_configMap.end()) {
		g_configMap.erase(it);
	}
}

void addEmsConfigEntry(emsConfigInfo *conf) {
	if(conf == NULL)
		return;

	// check for duplicacy
	StackConfigMap::iterator it = g_configMap.find(conf->nwId);
	if(it == g_configMap.end()) {
		g_configMap[conf->nwId] = conf;
	}
	else {
		// error scenario
	}
}

///////////////////////////////////////


/*
 * Configuration change Agent Callback function
 */

const int debugFlag = 1;

int
configIfForINGw(void* apContext, const CORBA::Any& arData, const char* apcOID,
	              const ImMediateTypes::OperationTypeValue aeOpValue)
{
   LogINGwTrace(false, 0, "IN configIfForINGw");
   int retval = 0;
   char* pcValue = NULL;

   try 
   {
      const char *indata;
      arData >>= indata;
      pcValue = CORBA::string_dup(indata);
   }
   catch(...) 
   {
      logger.logINGwMsg(false, WARNING_FLAG, 0, 
                      "configIfForINGw() : Unable to extract value for OID [%s]", 
                      apcOID);
      return -1;
   }

   INGwIfrUtlConfigurable::ConfigOpType opType = INGwIfrUtlConfigurable::CONFIG_OP_TYPE_ADD;

   switch(aeOpValue) 
   {
      case ImMediateTypes::OperationType_Delete :
      {
         opType = INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REMOVE;
      }
      break;

      case ImMediateTypes::OperationType_Modify :
      {
         opType = INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE;
      }
      break;

      case ImMediateTypes::OperationType_Add :
      default :
      {
      }
      break;
   }

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                   "configIfForINGw() : OID [%s], value [%s] and op-type [%s]", 
                   apcOID, pcValue, INGwIfrUtlConfigurable::getString(opType));

   INGwIfrMgrManager* pIfrMgr = static_cast<INGwIfrMgrManager*> (apContext);
   retval = pIfrMgr->configure(apcOID, pcValue, opType);
   CORBA::string_free(pcValue);
   LogINGwTrace(false, 0, "OUT configIfForINGw");
   return retval;
}

/*
 * Startup function for INGW as Agent Callback function
 */

int
startupIfForINGw(void* apContext)
{
   LogINGwTrace(false, 0, "IN startupIfForINGw");
   INGwIfrMgrManager* pIfrMgr = static_cast<INGwIfrMgrManager*> (apContext);
   int ret = pIfrMgr->startUp();
   LogINGwTrace(false, 0, "OUT startupIfForINGw");
   return ret;
}

/*
 * State change  Agent Callback function
 */

int
changeStateIfForINGw(void* apContext,
		                 const ImMediateTypes::SubsystemStateTypeValue aValue)
{
   LogINGwTrace(false, 0, "IN changeStateIfForINGw");
   INGwIfrMgrManager* pIfrMgr = static_cast<INGwIfrMgrManager*> (apContext);
   int retval = pIfrMgr->changeState((int)aValue);
   LogINGwTrace(false, 0, "OUT changeStateIfForINGw");
	 return retval;
}

/*
 * Performance : Agent Callback function
 */

ImMediateTypes::NVTypeSeq*
performanceIfForINGw(void* apContext)
{
   LogINGwTrace(false, 0, "IN performanceIfForINGw");
   INGwIfrMgrManager* pIfrMgr = static_cast<INGwIfrMgrManager*> (apContext);
   EmsOidValMap perfData;
   pIfrMgr->performance(&perfData);

   ImMediateTypes::NVTypeSeq* perfListPtr = 
                                 new ImMediateTypes::NVTypeSeq(perfData.size());
   perfListPtr->length(perfData.size());

   int idx = 0;
   for(EmsOidValMap::iterator it = perfData.begin(); it != perfData.end(); it++) 
   {
      (*perfListPtr)[idx].oid = CORBA::string_dup((*it).first.c_str());

	  // In FT setup, though calls are handled By primary but
	  // performance statistics are displayed by secondary 
	  // also. This is to avoid displaying stats by secondary.

    string selfMode = INGwIfrPrParamRepository::getInstance().getValue(ingwIS_PRIMARY);
    bool bIsPrimary = static_cast<bool>(atoi(selfMode.c_str()));


	  if(false == bIsPrimary)
	  {
		  long statsVal =0;
      (*perfListPtr)[idx].value <<= (CORBA::Long)statsVal;
	  }
	  else
      (*perfListPtr)[idx].value <<= (CORBA::Long)(*it).second;

    idx++;

	  logger.logMsg(VERBOSE_FLAG, 0, 
                 "PERFORMANCE OID[%s] Value[%d] fetch from MsrMgr",
					(*it).first.c_str(), (*it).second);
   }

   LogINGwTrace(false, 0, "OUT performanceIfForINGw");
   return perfListPtr;
}

/*
 * Reconfiguration of MsrMgr : Agent Callback function
 */

void
reconfigMsrMgrForINGw(const char *apcXml)
{
  LogINGwTrace(false, 0, "IN reconfigMsrMgrForINGw");
  if (apcXml)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "reconfigMsrMgr <%s>", apcXml);

    if (MsrMgr::getInstance()->reconfigureMsrMgr (apcXml) != 0)
      logger.logMsg (ERROR_FLAG, 0, "Reconfiguration failed for Measurement Manager");

  }

  LogINGwTrace(false, 0, "OUT reconfigMsrMgrForINGw");
}

/*
 * OID change  Agent Callback function
 */

int
oidChangedIfForINGw(void *apContext, const CORBA::Any& arData,
                    const char* apcOID, long  alSubsysId,
                    ImMediateTypes::SubsysTypeCode subsysType,
                    const ImMediateTypes::OperationTypeValue aeOpType)
{
   LogINGwTrace(false, 0, "IN oidChangedIfForINGw");
   int retval = 0;
   char* pcValue = NULL;

   try 
   {
      const char *indata;
      arData >>= indata;
      pcValue = CORBA::string_dup(indata);
   }
   catch(...) 
   {
      logger.logINGwMsg(false, WARNING_FLAG, 0, 
                      "oidChangedIfForINGw() : Unable to extract value for OID [%s]", 
                      apcOID);
        return -1;
   }

   INGwIfrUtlConfigurable::ConfigOpType opType = INGwIfrUtlConfigurable::CONFIG_OP_TYPE_ADD;

   switch(aeOpType) 
   {
      case ImMediateTypes::OperationType_Delete :
      {
         opType = INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REMOVE;
      }
      break;

      case ImMediateTypes::OperationType_Modify :
      {
         opType = INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE;
      }
      break;

      case ImMediateTypes::OperationType_Add :
      default :
      {
      }
      break;
   }

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                   "oidChangedIf() : OID [%s], value [%s] and op-type [%s] "
                   "subsystem Id [%d]", apcOID, pcValue, 
                   INGwIfrUtlConfigurable::getString(opType), alSubsysId);

   INGwIfrMgrManager* pIfrMgr = static_cast<INGwIfrMgrManager*> (apContext);
   retval = pIfrMgr->oidChanged(apcOID, pcValue, opType, alSubsysId);

   INGwIfrPrParamRepository &param = INGwIfrPrParamRepository::getInstance();

   if(param.getOperationMode() == NPlusOne)
   {
      if(0 == strcmp(cmSUBSYS_STATE, apcOID))
      {
         if(subsysType == ImMediateTypes::SubsysTypeCode_Ccm)
         {
            //To avoid making CORBA calls from CORBA thread with mutex 
            //PANKAJ TO DO
            pthread_t newThread;
            //pthread_create(&newThread, NULL, _processCCMRunning, NULL);
         }
      }
   }

   CORBA::string_free(pcValue);
   LogINGwTrace(false, 0, "OUT oidChangedIfForINGw");
   return retval;
}

void
newSubsysAddedIf(void *context, const RSIEmsTypes::ComponentInfo &info)
{
}

string
copyAddNetwork(Ss7SigtranSubsReq *req,  
							 const RSIEmsTypes::AddNetwork &addNw)
{
	string retVal = "SUCCESS";
	bool isHybridMode = false;
	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 2048);

	emsConfigInfo *conf = new emsConfigInfo();

	req->cmd_type = ADD_NETWORK;

	req->u.addNwk.nwId 	  			 = addNw.nwId;
	req->u.addNwk.variant 			 = addNw.variant; 
	req->u.addNwk.nmbSpcs 			 = addNw.selfPc_char.length();
	req->u.addNwk.ssf 					 = addNw.ssf;

	conf->nwId = addNw.nwId;

	// hardcoding
	req->u.addNwk.slsLen 				 = LIT_SLS4; // possible values, 4, 5, 8 bit
	req->u.addNwk.niInd 				 = INAT_IND; // NAT_IND=1, INAT_IND=0
	req->u.addNwk.subService 	   = addNw.ssf; // SSF_INTL=0, NAT=2, RES=3
  req->u.addNwk.protoType      = (addNw.mode == 1)?0:(addNw.mode == 2)?1:2;

	// SIGTRAN=2, ELECTRICAL=1, HYBRID=3
	if(addNw.mode == 3 || addNw.mode == 2)
		isHybridMode= true;	

	conf->mode = addNw.mode;

	int protoType = 0;
	switch(req->u.addNwk.variant) {
		case SW_ITU:
		case LSP_SW_ITU88:
		case LSP_SW_ITU92:
		case LSP_SW_ITU96:
		case SW_CHINA:
			protoType = SW_ITU; break;
		case SW_ANSI:
		case LSP_SW_ANS88:
		case LSP_SW_ANS92:
		case LSP_SW_ANS96:
			protoType = SW_ANSI; break;
		case SW_JAPAN:
			protoType = SW_JAPAN; break;
		default:
			protoType = -1;
	}

	if(protoType == -1) {
		// Error condition. 
		delete req;
		delete conf;
		req=NULL;
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
			"+VER+: copyAddNetwork, failed, invalid variant rx[%d]", 
				addNw.variant);
		return "Invalid Protocol";
	}

	U8 pcDet[3];
	req->u.addNwk.defaultPc = g_convertPcToDec((char*)addNw.defaultPc_char.in(),
																						 pcDet, protoType);

	conf->proto     = protoType;
	conf->defOpc    = req->u.addNwk.defaultPc;
	conf->defOpcStr = addNw.defaultPc_char.in();

	if(debugFlag) {
		dumpLen = sprintf(dumpBuf, "Add Network Request, cmd_type:%d, NwName:%s, NwId:%d,"
			 " Variant:%d, defaultPcStr:%s:%d:%d-%d-%d, nmbSpcs:%d, "
			 " niInd:%d, subService:%d, mode:%d:%s, slsLen:%d, ssf:%d, ",
			 req->cmd_type, addNw.nwName.in(), req->u.addNwk.nwId, req->u.addNwk.variant,
			 addNw.defaultPc_char.in(), req->u.addNwk.defaultPc,
			 pcDet[0], pcDet[1], pcDet[2], req->u.addNwk.nmbSpcs, 
			 req->u.addNwk.niInd, req->u.addNwk.subService, addNw.mode,
		   (addNw.mode == 3)?"HYBRID":(addNw.mode ==2)?"SIGTRAN":"ELECTRICAL",
			req->u.addNwk.slsLen, req->u.addNwk.ssf);
	}

	bool opcConfig = true;
	for(int i=0;i < req->u.addNwk.nmbSpcs; ++i) {
		req->u.addNwk.selfPc[i] = g_convertPcToDec
			((char *)addNw.selfPc_char[i].in(), pcDet, protoType);

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, " SelfPc[%d|%s:%d:%d-%d-%d,", 
							i, addNw.selfPc_char[i].in(), req->u.addNwk.selfPc[i],
							 pcDet[0], pcDet[1], pcDet[2]); 
		}

		U8 ssn =0;
		opcConfig = INGwTcapProvider::getInstance().verifyOpcSsnInConfigList
																	(req->u.addNwk.selfPc[i], ssn);

		if(opcConfig == false)
			break;

		conf->opcList.push_back(req->u.addNwk.selfPc[i]);
		conf->opcListStr.push_back(addNw.selfPc_char[i].in());

		if(addNw.mode == 3)
		{
			conf->opcTypeList.push_back(addNw.selfPcType[i]);
			if(conf->defOpc == req->u.addNwk.selfPc[i])
				conf->defOpcType = addNw.selfPcType[i];
		}
		else if(addNw.mode == 1)
		{
			conf->opcTypeList.push_back(1);
			if(conf->defOpc == req->u.addNwk.selfPc[i])
				conf->defOpcType = 1;
		}
		else 
		{
			conf->opcTypeList.push_back(2);
			if(conf->defOpc == req->u.addNwk.selfPc[i])
				conf->defOpcType = 2;
		}

	}

	if(opcConfig == false) 
	{
		delete req;
		delete conf;
		req=NULL;
	  if(debugFlag) {
	  	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addNetwork: %s", dumpBuf);
	  }
		logger.logMsg(ERROR_FLAG, 0, 
		"copyAddNetwork: OPC not configured as default");
		return ("OPC not configured as default");
	}

	memset(&req->u.addNwk.nwkApp, 0, LIT_MAX_PSP);
	// currently making hardcoded 16 as EMS has defined
	// LIT_MAX_PSP as 16, actual value is 20
	// for(int i=0; i < LIT_MAX_PSP; ++i) 
	for(int i=0; i < 16; ++i) {
		req->u.addNwk.nwkApp[i] = addNw.nwkApp[i];

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, " NwApp[%d]:%d,",
							i, req->u.addNwk.nwkApp[i]);
		}
	}

	// Calculated based on Variant. 
	if(protoType == SW_ITU) {
		req->u.addNwk.dpcLen = DPC14;
		if(isHybridMode) {
			req->u.addNwk.suSwtch  = addNw.suSwtch;  // LIT_SW_ANS96;
			req->u.addNwk.su2Swtch = addNw.su2Swtch; // LIT_SW2_ANS;
		}
		
		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, 
					"VARAINT: SW_ITU, dpcLen:[%d], suSwtch:%d, su2Swtch:%d ",
							req->u.addNwk.dpcLen, req->u.addNwk.suSwtch,
							req->u.addNwk.su2Swtch);
		}
	}
	else if(protoType == SW_ANSI) {
		req->u.addNwk.dpcLen = DPC24;
		req->u.addNwk.slsLen = LIT_SLS8;
		if(isHybridMode) {
			req->u.addNwk.suSwtch  = addNw.suSwtch;  // LIT_SW_ANS96;
			req->u.addNwk.su2Swtch = addNw.su2Swtch; // LIT_SW2_ANS;
		}

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, "VARAINT: SW_ANSI, dpcLen:[%d],"
							"slsLen:%d, suSwtch:%d, su2Swtch:%d",
							req->u.addNwk.dpcLen, req->u.addNwk.slsLen,
							req->u.addNwk.suSwtch, req->u.addNwk.su2Swtch);
		}
	}
	else if(protoType == SW_JAPAN) {
		req->u.addNwk.dpcLen = DPC16;
		if(isHybridMode) {
			// if NTT
			req->u.addNwk.suSwtch  =  addNw.suSwtch; // LIT_SW_NTT(11)LIT_SW_TTC(10)
			req->u.addNwk.su2Swtch = addNw.su2Swtch; // LIT_SW2_TTC, LIT_SW2_TTC;

			if(debugFlag) {
				dumpLen += sprintf(dumpBuf+dumpLen, "VARAINT: SW_JAPAN, dpcLen:[%d],"
							" suSwtch:%d, su2Swtch:%d", req->u.addNwk.dpcLen,
							req->u.addNwk.suSwtch, req->u.addNwk.su2Swtch);
			}
		}
	}

	// Required during AddLink
	conf->dpcLen = req->u.addNwk.dpcLen;

	// Default Values
	req->u.addNwk.spcBroadcastOn = TRUE;

	if(debugFlag) {
				dumpLen += sprintf(dumpBuf+dumpLen, ", spcBroadcastOn: TRUE");
	}

	addEmsConfigEntry(conf);

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addNetwork: %s", dumpBuf);
	}

	return "Success";
}

int
copyDelNetwork(Ss7SigtranSubsReq *req,  
													const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelNetwork &delNw = cfgReq.union_type.delNwk();

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

	req->cmd_type = DEL_NETWORK;

	req->u.delNwk.nwkId 	  = delNw.nwkId; 
	req->u.delNwk.variant 	= delNw.variant;

	if(debugFlag) {
		sprintf(dumpBuf, "Delete Network Request: cmd_type:%d, NwId: %d, Variant:%d",
			req->cmd_type, req->u.delNwk.nwkId, req->u.delNwk.variant);

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "delNetwork: %s", dumpBuf);
	}

	return 1;
}

int copyAddLink(Ss7SigtranSubsReq *req, 
                    const RSIEmsTypes::AddLink &addLink)   
										//const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

	req->cmd_type = ADD_LINK;

	emsConfigInfo* conf = getEmsConfigInfo(addLink.opc_char.in());
	if(conf == NULL) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"Leaving AddLink, No Info for OPC:%s", addLink.opc_char.in());
		return 0;
	}

	req->u.lnk.lnkId 	  		= addLink.lnkId; 
	req->u.lnk.lnkSetId 		= addLink.lnkSetId;       

	U8 pcDet[3];
	req->u.lnk.opc = g_convertPcToDec((char*)addLink.opc_char.in(),
																						 pcDet, conf->getProto());
	req->u.lnk.adjDpc = g_convertPcToDec((char*)addLink.adjDpc_char.in(),
																						 pcDet, conf->getProto());

	req->u.lnk.dpcLen   		= conf->getDpcLen();
	req->u.lnk.physPort 		= addLink.physPort;
	req->u.lnk.timeSlot 		= addLink.timeSlot; 
	req->u.lnk.ssf      		= addLink.ssf;
  string lnkname = CORBA::string_dup(addLink.lnkName);
  int len = lnkname.size();
  for(int i=0 ; i<len ; i++)
  {
    req->u.lnk.lnkName[i] = lnkname[i];
  }
	req->u.lnk.slc      		= addLink.slc;  
	req->u.lnk.lnkPrior 		= 0; //addLink.lnkPrior;       
	req->u.lnk.mtp2UsapId 	= addLink.mtp2UsapId;       
	req->u.lnk.mtp3LsapId   = addLink.mtp3LsapId;
	req->u.lnk.currentLinkState   = addLink.currentLinkState;

	req->u.lnk.mtp2ProcId   = INGwTcapProvider::getInstance().
									getProcIdForSubsysId(addLink.mtp2ProcId);

	// Link Type calculated based on protocol, EMS is sending hardcoded 1
	if(conf->getProto() == SW_ITU) 
		req->u.lnk.lnkType= LSN_SW_ITU;
	else if(conf->getProto() == SW_ANSI)
		req->u.lnk.lnkType= LSN_SW_ANS;
	else if(conf->getProto() == SW_JAPAN)
		req->u.lnk.lnkType= LSN_SW_NTT;

	if(debugFlag) {
		sprintf(dumpBuf, "Add Link Request: cmd_type:%d, LnkId:%d, LnkSetId:%d,"
					" dpcLen:%d, opc:%d:%s, adjDpc:%d:%s, physPort:%d, timeSlot:%d,"
					" ssf:%d, slc:%d, lnkType:%d, lnkPrior:%d, mtp2UsapId:%d,"
					" mtp3LsapId:%d, mtp2ProcId:%d:%d, currentLinkState:%d ",
					req->cmd_type, req->u.lnk.lnkId, req->u.lnk.lnkSetId, req->u.lnk.dpcLen,
					req->u.lnk.opc, addLink.opc_char.in(), req->u.lnk.adjDpc, 
					addLink.adjDpc_char.in(), req->u.lnk.physPort,
					req->u.lnk.timeSlot, req->u.lnk.ssf, req->u.lnk.slc,
					req->u.lnk.lnkType, req->u.lnk.lnkPrior, req->u.lnk.mtp2UsapId,
					req->u.lnk.mtp3LsapId, req->u.lnk.mtp2ProcId, 
					addLink.mtp2ProcId, req->u.lnk.currentLinkState);

	}

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addlink: %s", dumpBuf);

	return 1;
}


int copyModLink(Ss7SigtranSubsReq *req, 
                    const RSIEmsTypes::AddLink &addLink)   
										//const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

	req->cmd_type = MODIFY_LINK;

	emsConfigInfo* conf = getEmsConfigInfo(addLink.opc_char.in());
	if(conf == NULL) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"Leaving ModLink, No Info for OPC:%s", addLink.opc_char.in());
		return 0;
	}

	req->u.lnk.lnkId 	  		= addLink.lnkId; 
	req->u.lnk.lnkSetId 		= addLink.lnkSetId;       

	U8 pcDet[3];
	req->u.lnk.opc = g_convertPcToDec((char*)addLink.opc_char.in(),
																						 pcDet, conf->getProto());
	req->u.lnk.adjDpc = g_convertPcToDec((char*)addLink.adjDpc_char.in(),
																						 pcDet, conf->getProto());

	req->u.lnk.dpcLen   		= conf->getDpcLen();
	req->u.lnk.physPort 		= addLink.physPort;
	req->u.lnk.timeSlot 		= addLink.timeSlot; 
	req->u.lnk.ssf      		= addLink.ssf;  
	req->u.lnk.slc      		= addLink.slc;  
	req->u.lnk.lnkPrior 		= 0; //addLink.lnkPrior;       
	req->u.lnk.mtp2UsapId 	= addLink.mtp2UsapId;       
	req->u.lnk.mtp3LsapId   = addLink.mtp3LsapId;

	req->u.lnk.mtp2ProcId   = INGwTcapProvider::getInstance().
									getProcIdForSubsysId(addLink.mtp2ProcId);

	// Link Type calculated based on protocol, EMS is sending hardcoded 1
	if(conf->getProto() == SW_ITU) 
		req->u.lnk.lnkType= LSN_SW_ITU;
	else if(conf->getProto() == SW_ANSI)
		req->u.lnk.lnkType= LSN_SW_ANS;
	else if(conf->getProto() == SW_JAPAN)
		req->u.lnk.lnkType= LSN_SW_NTT;

	if(debugFlag) {
		sprintf(dumpBuf, "Modify Link Request: cmd_type:%d, LnkId:%d, LnkSetId:%d,"
					" dpcLen:%d, opc:%d:%s, adjDpc:%d:%s, physPort:%d, timeSlot:%d,"
					" ssf:%d, slc:%d, lnkType:%d, lnkPrior:%d, mtp2UsapId:%d,"
					" mtp3LsapId:%d, mtp2ProcId:%d:%d, currentLinkSTate:%d ",
					req->cmd_type, req->u.lnk.lnkId, req->u.lnk.lnkSetId, req->u.lnk.dpcLen,
					req->u.lnk.opc, addLink.opc_char.in(), req->u.lnk.adjDpc, 
					addLink.adjDpc_char.in(), req->u.lnk.physPort,
					req->u.lnk.timeSlot, req->u.lnk.ssf, req->u.lnk.slc,
					req->u.lnk.lnkType, req->u.lnk.lnkPrior, req->u.lnk.mtp2UsapId,
					req->u.lnk.mtp3LsapId, req->u.lnk.mtp2ProcId, 
					addLink.mtp2ProcId, req->u.lnk.currentLinkState);

	}

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "modlink: %s", dumpBuf);

	return 1;
}

int copyModLinkSet(Ss7SigtranSubsReq *req, 
                    const RSIEmsTypes::AddLinkSet &addLinkSet)   
{

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

  emsConfigInfo* conf = getEmsConfigInfo(addLinkSet.nwId);
	if(conf == NULL) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"Leaving ModLinkSet, No Info for nwId:%d", addLinkSet.nwId);
		return 0;
	}

	req->cmd_type = MODIFY_LINKSET;

	req->u.lnkSet.lnkSetId 	= addLinkSet.lnkSetId; 
	req->u.lnkSet.lnkSetType 		= addLinkSet.lnkSetType;       

	U8 pcDet[3];
	req->u.lnkSet.adjDpc = g_convertPcToDec((char*)addLinkSet.adjDpc_char.in(),
																						 pcDet, conf->getProto());
  req->u.lnkSet.nmbActLnkReqd = addLinkSet.nmbActLnkReqd;
	req->u.lnkSet.nmbCmbLnkSet  = addLinkSet.nmbCmbLnkSet;

	if(debugFlag) {
		dumpLen = sprintf(dumpBuf, "Modify Linkset Request: cmd_type:%d, lnkSetId:%d,"
					"lnksetType: %d, adjDpc: %d:%d-%d-%d:%s, nmbAckLnkReqd: %d," 
					"nmbCmbLnkSet: %d", req->cmd_type, req->u.lnkSet.lnkSetId,req->u.lnkSet.lnkSetType,
					req->u.lnkSet.adjDpc, pcDet[0], pcDet[1], pcDet[2], 
					addLinkSet.adjDpc_char.in(),
					req->u.lnkSet.nmbActLnkReqd, req->u.lnkSet.nmbCmbLnkSet);
	}

	for(int i=0; i < req->u.lnkSet.nmbCmbLnkSet; ++i) {
		req->u.lnkSet.cmbLnkSet[i].cmbLnkSetId = addLinkSet.cmbLnkSet[i].cmbLnkSetId;
		req->u.lnkSet.cmbLnkSet[i].lnkSetPrior = 0;
		req->u.lnkSet.cmbLnkSet[i].nmbPrefLinks= 
																	addLinkSet.cmbLnkSet[i].prefLnkId.length();

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, "cmbLnkSet[%d]: cmbLnkSetId: %d,"
					"lnkSetPrior: %d, nmbPrefLinks: %d", i,
					req->u.lnkSet.cmbLnkSet[i].cmbLnkSetId,
					req->u.lnkSet.cmbLnkSet[i].lnkSetPrior,
					req->u.lnkSet.cmbLnkSet[i].nmbPrefLinks);
		}

		for(int j=0; j < req->u.lnkSet.cmbLnkSet[i].nmbPrefLinks; ++j) {
			req->u.lnkSet.cmbLnkSet[i].prefLnkId[j] = addLinkSet.cmbLnkSet[i].prefLnkId[j];

			if(debugFlag) {
				dumpLen += sprintf(dumpBuf+dumpLen, " prefLnkId[%d]:%d,",
				j, req->u.lnkSet.cmbLnkSet[i].prefLnkId[j]);
			}
		}
	}

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "modLinkset: %s", dumpBuf);
	}
	return 1;

}


int copyDelLink(Ss7SigtranSubsReq *req,  
														const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelLink &delLink = cfgReq.union_type.delLnk();

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

	req->cmd_type = DEL_LINK;

	req->u.delLnk.lnkId 			= delLink.lnkId;
	req->u.delLnk.mtp2ProcId  = INGwTcapProvider::getInstance().
										getProcIdForSubsysId(delLink.mtp2ProcId);

	req->u.delLnk.mtp2UsapId  = delLink.mtp2UsapId;
	req->u.delLnk.mtp3LsapId  = delLink.mtp3LsapId;

	if(debugFlag) {
		sprintf(dumpBuf, "Delete Link Request: cmd_type:%d, lnkId:%d, mtp2ProcId:%d:%d,"
				" mtp2UsapId:%d, mtp3LsapId:%d", req->cmd_type, req->u.delLnk.lnkId,
				 req->u.delLnk.mtp2ProcId, delLink.mtp2ProcId,
				 req->u.delLnk.mtp2UsapId, req->u.delLnk.mtp3LsapId);

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "deleteLink: %s", dumpBuf);
	}

	return 1;
}

void copyLinkStatus(Ss7SigtranSubsReq *req,  
														const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::LinkStatus &lnkStatus = cfgReq.union_type.lnkstatus();

  int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

	req->cmd_type = STA_LINK;

  req->u.lnkstatus.lnkId = lnkStatus.lnkId;
  req->u.lnkstatus.layer = lnkStatus.layer;
  req->u.lnkstatus.mtp2UsapId = lnkStatus.mtp2UsapId;
  req->u.lnkstatus.mtp3LsapId = lnkStatus.mtp3LsapId;

  if(debugFlag) {
		sprintf(dumpBuf, "Status Link Request: cmd_type:%d, lnkId:%d, layer:%d"
				" mtp2UsapId:%d, mtp3LsapId:%d", req->cmd_type, req->u.lnkstatus.lnkId,req->u.lnkstatus.layer,
				 req->u.lnkstatus.mtp2UsapId, req->u.lnkstatus.mtp3LsapId);

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "LinkStatus: %s", dumpBuf);
	}

}

void copyEnableLink(Ss7SigtranSubsReq *req,  
													const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::LinkEnable &lnkEnable = cfgReq.union_type.lnkEnable();

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

	req->cmd_type = ENABLE_LINK;
	req->u.lnkEnable.lnkId 			= lnkEnable.lnkId;
	req->u.lnkEnable.lnkSetId 	= lnkEnable.lnkSetId;
	//req->u.lnkEnable.procId 		= lnkEnable.procId;
  req->u.lnkEnable.procId  = INGwTcapProvider::getInstance().
										getProcIdForSubsysId(lnkEnable.procId);

	req->u.lnkEnable.mtp2UsapId = lnkEnable.mtp2UsapId;
	req->u.lnkEnable.mtp3LsapId = lnkEnable.mtp3LsapId;

	if(debugFlag) {
		sprintf(dumpBuf, "Enable Link Request: cmd_type:%d, lnkId:%d, lnkSetId:%d, procId:%d,"
				" mtp2UsapId:%d, mtp3LsapId:%d", req->cmd_type, req->u.lnkEnable.lnkId,
				 req->u.lnkEnable.lnkSetId,   req->u.lnkEnable.procId,
					req->u.lnkEnable.mtp2UsapId, req->u.lnkEnable.mtp3LsapId);

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "enableLink: %s", dumpBuf);
	}
}

void copyDisableLink(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::LinkDisable &lnkDisable = cfgReq.union_type.lnkDisable();

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 1024);

	req->cmd_type = DISABLE_LINK;
	req->u.lnkDisable.lnkId 			= lnkDisable.lnkId;
	req->u.lnkDisable.lnkSetId 	= lnkDisable.lnkSetId;
	//req->u.lnkDisable.procId 		= lnkDisable.procId;
  req->u.lnkDisable.procId  = INGwTcapProvider::getInstance().
										getProcIdForSubsysId(lnkDisable.procId);
	req->u.lnkDisable.mtp2UsapId = lnkDisable.mtp2UsapId;
	req->u.lnkDisable.mtp3LsapId = lnkDisable.mtp3LsapId;

	if(debugFlag) {
		sprintf(dumpBuf, "Disable Link Request: cmd_type:%d, lnkId:%d, lnkSetId:%d, procId:%d,"
				" mtp2UsapId:%d, mtp3LsapId:%d", req->cmd_type, req->u.lnkDisable.lnkId,
				 req->u.lnkDisable.lnkSetId,   req->u.lnkDisable.procId,
					req->u.lnkDisable.mtp2UsapId, req->u.lnkDisable.mtp3LsapId);

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "disableLink: %s", dumpBuf);
	}
}

int copyAddLinkset(Ss7SigtranSubsReq *req,  
                           const RSIEmsTypes::AddLinkSet &addLnkset)
{

	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 2048);

	emsConfigInfo* conf = getEmsConfigInfo(addLnkset.nwId);
	if(conf == NULL) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"Leaving AddLinkSet, No Info for nwId:%d", addLnkset.nwId);
		return 0;
	}

	req->cmd_type = ADD_LINKSET;

	req->u.lnkSet.lnkSetId 	 	  = addLnkset.lnkSetId;
	req->u.lnkSet.lnkSetType 	  = addLnkset.lnkSetType;

	// store LS Id for this NwId, will be used in AddLink
	conf->lsIdList.push_back(req->u.lnkSet.lnkSetId); 

	U8 pcDet[3];
	req->u.lnkSet.adjDpc = g_convertPcToDec((char*)addLnkset.adjDpc_char.in(),
																							 pcDet, conf->proto);
	req->u.lnkSet.nmbActLnkReqd = addLnkset.nmbActLnkReqd;
	req->u.lnkSet.nmbCmbLnkSet  = addLnkset.nmbCmbLnkSet;

	if(debugFlag) {
		dumpLen = sprintf(dumpBuf, "Add Linkset Request: cmd_type:%d, lnkSetId:%d,"
					"lnksetType: %d, adjDpc: %d:%d-%d-%d:%s, nmbAckLnkReqd: %d," 
					"nmbCmbLnkSet: %d", req->cmd_type, req->u.lnkSet.lnkSetId,req->u.lnkSet.lnkSetType,
					req->u.lnkSet.adjDpc, pcDet[0], pcDet[1], pcDet[2], 
					addLnkset.adjDpc_char.in(),
					req->u.lnkSet.nmbActLnkReqd, req->u.lnkSet.nmbCmbLnkSet);
	}

	for(int i=0; i < req->u.lnkSet.nmbCmbLnkSet; ++i) {
		req->u.lnkSet.cmbLnkSet[i].cmbLnkSetId = addLnkset.cmbLnkSet[i].cmbLnkSetId;
		req->u.lnkSet.cmbLnkSet[i].lnkSetPrior = 0;
								// rajeev
								//addLnkset.cmbLnkSet[i].lnkSetPrior[0];
		req->u.lnkSet.cmbLnkSet[i].nmbPrefLinks= 
																	addLnkset.cmbLnkSet[i].prefLnkId.length();
								// addLnkset.cmbLnkSet[i].nmbPrefLinks;

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, "cmbLnkSet[%d]: cmbLnkSetId: %d,"
					"lnkSetPrior: %d, nmbPrefLinks: %d", i,
					req->u.lnkSet.cmbLnkSet[i].cmbLnkSetId,
					req->u.lnkSet.cmbLnkSet[i].lnkSetPrior,
					req->u.lnkSet.cmbLnkSet[i].nmbPrefLinks);
		}

		for(int j=0; j < req->u.lnkSet.cmbLnkSet[i].nmbPrefLinks; ++j) {
			req->u.lnkSet.cmbLnkSet[i].prefLnkId[j] = addLnkset.cmbLnkSet[i].prefLnkId[j];

			if(debugFlag) {
				dumpLen += sprintf(dumpBuf+dumpLen, " prefLnkId[%d]:%d,",
				j, req->u.lnkSet.cmbLnkSet[i].prefLnkId[j]);
			}
		}
	}

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addLinkset: %s", dumpBuf);
	}
	return 1;
}

void copyDelLinkset(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelLinkSet &delLnkset = cfgReq.union_type.delLnkSet();

	req->cmd_type = DEL_LINKSET;
	req->u.delLnkSet.lnkSetId = delLnkset.lnkSetId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
			"Delete LinkSet: cmd_type:%d, LinksetID:%d", req->cmd_type, req->u.delLnkSet.lnkSetId);
	}
}

void copyLinksetStatus(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::LinkSetStatus &lnksetStatus = cfgReq.union_type.lnkSetStatus();

	req->cmd_type = STA_LINKSET;

  req->u.lnkStatus.lnkSet      = lnksetStatus.lnkSet;
  req->u.lnkStatus.cmbLnkSetId = lnksetStatus.cmbLnkSetId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "LinksetStatus: cmd_type:%d, lnkSet:%d, cmbLnkSetId:%d",
			req->cmd_type, req->u.lnkStatus.lnkSet,  req->u.lnkStatus.cmbLnkSetId);
	}

}

int copyAddRoute(Ss7SigtranSubsReq *req,
                          const RSIEmsTypes::AddRoute &addRoute)  
{
	int  dumpLen =0;
	char dumpBuf[4000];
	memset(dumpBuf, 0, 4000);

	emsConfigInfo* conf = getEmsConfigInfo(addRoute.preferredOpc_char.in());
	if(conf == NULL) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"Leaving AddRoute, No Info for OPC:%s", addRoute.preferredOpc_char.in());
		return 0;
	}

	req->cmd_type = ADD_ROUTE;

	U8 pcDet[3];
	req->u.addRoute.dpc 				= g_convertPcToDec
					((char*)addRoute.dpc_char.in(), pcDet, addRoute.swtch);

	// Type 1 = MTP3
	unsigned short type = 0;
	if(0 != (type = conf->getOpcType(addRoute.preferredOpc_char.in())))
	{
		if(type == 1)
			req->u.addRoute.swtchType = addRoute.swtchType;
		else
			req->u.addRoute.swtchType = 255;
	}

	req->u.addRoute.spType			= addRoute.spType;
	req->u.addRoute.upSwtch			= addRoute.upSwtch;
	req->u.addRoute.cmbLnkSetId	= addRoute.cmbLnkSetId;
	req->u.addRoute.dir					= addRoute.dir;
	req->u.addRoute.rteToAdjSp	= addRoute.rteToAdjSp;
	req->u.addRoute.ssf					= addRoute.ssf;
	req->u.addRoute.swtch				= addRoute.swtch;

	req->u.addRoute.nmbBpc			= addRoute.bpcList.length();
	req->u.addRoute.nmbSsns			= addRoute.ssnList.length();
#if (SS7_ANS96 || SS7_BELL05)
	req->u.addRoute.replicatedMode= addRoute.replicatedMode;
#endif
	req->u.addRoute.preferredOpc	= addRoute.preferredOpc;
	req->u.addRoute.nSapId				= addRoute.nSapId; //  should be same as 
																									 // Add Userpart SccpLsapId 
	req->u.addRoute.currentDpcState	= addRoute.currentDpcState;

	// check if preferred OPC and DPC are same then it is Self Route
	req->u.addRoute.preferredOpc = g_convertPcToDec
					((char*)addRoute.preferredOpc_char.in(), pcDet, addRoute.swtch);

#if LSPV3_1
req->u.addRoute.secRteCfg          = FALSE;
req->u.addRoute.nSap1RteStatus     = ~0x01; // ~0x01:SP_OFFLINE, 0x01: SP_ONLINE
req->u.addRoute.nSap2RteStatus     = ~0x01; // ~0x01:SP_OFFLINE, 0x01: SP_ONLINE
req->u.addRoute.defaultRoutenSapId = addRoute.nSapId;

string prefNw= INGwIfrPrParamRepository::getInstance().getValue("PREF_NW");
// Possible Values of perfNw
// NONE - This shall be used in hybrid network where STP has 2 different 
//        point code reachable through SS7 and SIGTRAN
// SIGTRAN - STP has single point code and can be reachable through SIGTRAN
//          or MTP3. In this case SIGTRAN n/w gets priority.
// MTP3 - STP has signle point code and can be reachable through both SIGTRAN
//        or MTP3. In this case MTP3 gets priority.
if(conf->mode == 3 && prefNw != "NONE") 
{
  req->u.addRoute.secRteCfg            = TRUE;

  if(type == 1)
    req->u.addRoute.nSapId2 = conf->m3uaSccpLsapId;
  else if(type == 2)
    req->u.addRoute.nSapId2 = conf->mtp3SccpLsapId;

  if(prefNw == "SIGTRAN") {
    req->u.addRoute.defaultRoutenSapId = conf->m3uaSccpLsapId;
  }
  else  {
    req->u.addRoute.defaultRoutenSapId = conf->mtp3SccpLsapId;
  }
}
#endif

	if(req->u.addRoute.preferredOpc == req->u.addRoute.dpc) {
		req->u.addRoute.status			 = SP_TRANS;
		req->u.addRoute.dir					 = LSN_RTE_UP;
		req->u.addRoute.preferredOpc = 0;  // prefered OPC is 0 for self route
		req->u.addRoute.swtch        = -1;
	}
	else {
		//req->u.addRoute.status = SP_ADJACENT;
		req->u.addRoute.status = SP_OFFLINE;
		req->u.addRoute.dir		 = LSN_RTE_DN;
	}

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, "Add Route Request: cmd_type:%d, dpc:%d, type:%d mode:%d swtchType:%d"
				", spType:%d, upSwtch:%d, cmbLnkSetId:%d, dir:%d, rteToAdjSp:%d"
				", ssf:%d, swtch:%d, status:%d, nmbBpc:%d, nmbSsns:%d"
				", preferredOpc:%d, nSapId:%d, currentDpcState:%d", 
				req->cmd_type, req->u.addRoute.dpc, type, conf->mode, req->u.addRoute.swtchType, req->u.addRoute.spType,
				req->u.addRoute.upSwtch, req->u.addRoute.cmbLnkSetId, req->u.addRoute.dir,
				req->u.addRoute.rteToAdjSp, req->u.addRoute.ssf, req->u.addRoute.swtch	,
				req->u.addRoute.status, req->u.addRoute.nmbBpc, req->u.addRoute.nmbSsns,
				req->u.addRoute.preferredOpc, req->u.addRoute.nSapId		,
				req->u.addRoute.currentDpcState);

#if LSPV3_1
    dumpLen += sprintf(dumpBuf+dumpLen, 
        "defaultRoutenSapId:%d, nSap1RteStatus:%d, secRteCfg:%d, nSapId2:%d, nSap2RteStatus:%d",
        req->u.addRoute.defaultRoutenSapId, req->u.addRoute.nSap1RteStatus,
        req->u.addRoute.secRteCfg, req->u.addRoute.nSapId2, req->u.addRoute.nSap2RteStatus);
#endif

#if (SS7_ANS96 || SS7_BELL05)
		dumpLen += sprintf(dumpBuf+dumpLen, " replicateMode:%d", req->u.addRemoteSsn.replicatedMode);
#endif

	}

	for(int i=0; i < req->u.addRoute.nmbBpc; ++i) {
		// check if we need to convert pc_char to decimal or
		// will get pc itself. 
		req->u.addRoute.bpcList[i].bpc   = g_convertPcToDec
					((char*)addRoute.bpcList[i].pc_char.in(), pcDet, addRoute.swtch);
		req->u.addRoute.bpcList[i].prior = addRoute.bpcList[i].prior;

		if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, " bpcList[%d]:{bpc:%d, prior:%d}, ",
					i, req->u.addRoute.bpcList[i].bpc, req->u.addRoute.bpcList[i].prior);
		}
	}

	for(int j=0; j < req->u.addRoute.nmbSsns; ++j) {
		req->u.addRoute.ssnList[j].ssn     = addRoute.ssnList[j].ssn;
		req->u.addRoute.ssnList[j].status  = SS_INACC;
		req->u.addRoute.ssnList[j].nmbBpc  = addRoute.ssnList[j].bpcList.length();
		req->u.addRoute.ssnList[j].nmbConPc= addRoute.ssnList[j].conPcList.length();

		if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, 
				" nmbSsns[%d]:{ssn:%d, nmbBpc:%d, nmbConPc:%d",
				j, req->u.addRoute.ssnList[j].ssn, req->u.addRoute.ssnList[j].nmbBpc, 
				req->u.addRoute.ssnList[j].nmbConPc);
		}

		for(int k=0; k < req->u.addRoute.ssnList[j].nmbBpc;++k) {
			req->u.addRoute.ssnList[j].bpcList[k].bpc   = g_convertPcToDec
					((char*)addRoute.ssnList[j].bpcList[k].pc_char.in(), pcDet, 
					addRoute.swtch);

			req->u.addRoute.ssnList[j].bpcList[k].prior = 
																				addRoute.ssnList[j].bpcList[k].prior;

			if(debugFlag) {
				dumpLen += sprintf(dumpBuf+dumpLen, " bpcList[%d]:{bpc:%d, Prior:%d}, ",
				k, req->u.addRoute.ssnList[j].bpcList[k].bpc, 
				req->u.addRoute.ssnList[j].bpcList[k].prior);
			}
		}

	for(int l=0; l < req->u.addRoute.ssnList[j].nmbConPc;++l) {
		req->u.addRoute.ssnList[j].conPc[l] = g_convertPcToDec
		 ((char*)addRoute.ssnList[j].conPcList[l].pc_char.in(), pcDet, addRoute.swtch);

			if(debugFlag) {
				dumpLen += sprintf(dumpBuf+dumpLen, " conPc[%d]:%d, ",
				l, req->u.addRoute.ssnList[j].conPc[l]);
			}
		}

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, " },");
		}
	}

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addRoute: %s", dumpBuf);
	}

	return 1;
}

void copyDelRoute(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelRoute &delRoute = cfgReq.union_type.delRute();

	req->cmd_type = DEL_ROUTE;
 
	U8 pcDet[3];
  if(delRoute.upSwtch == 6 || delRoute.upSwtch == 1) //ANSI96 & ANSI in MTP3 layer
    req->u.delRoute.upSwtch = 2; //ANSI
  else if(delRoute.upSwtch == 2) //ITU in MTP3 layer
    req->u.delRoute.upSwtch = 1; //ITU
  else
	  req->u.delRoute.upSwtch		= delRoute.upSwtch;

	req->u.delRoute.dpc				= g_convertPcToDec((char*)delRoute.dpc_char.in(),
  																				 pcDet, req->u.delRoute.upSwtch);
	req->u.delRoute.nSapId		= delRoute.nSapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"Delete Route Request: cmd_type:%d, dpc:%d-%s, nSapId:%d, upSwtch:%d",
		 req->cmd_type, req->u.delRoute.dpc, delRoute.dpc_char.in(), req->u.delRoute.nSapId, 
		 req->u.delRoute.upSwtch);
	}
}

void copyRouteStatus(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::RouteStatus &routeStatus = cfgReq.union_type.dpcStatus();

	req->cmd_type = STA_ROUTE;


  U8 pcDet[3];
  emsConfigInfo* conf = getEmsConfigInfo(routeStatus.nwkId);
  if(conf == NULL)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
    "Leaving AddLinkSet, No Info for nwId:%d", routeStatus.nwkId);
    return ;
  }

  req->u.dpcStatus.dpc = g_convertPcToDec( (char *)routeStatus.dpc_char.in() , pcDet, conf->proto);

	req->u.dpcStatus.nwkId  = routeStatus.nwkId;

	if(debugFlag) {
			logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
			"Route Status Request: cmd_type:%d, dpc:%s-%d, nwkId:%d",
			req->cmd_type, routeStatus.dpc_char.in(),req->u.dpcStatus.dpc, req->u.dpcStatus.nwkId);		
	}
}

void copyNodeStatus(Ss7SigtranSubsReq *req,  
															const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::NodeStatus &nodeStatus = cfgReq.union_type.ndStatus();

	req->cmd_type = STA_NODE;

	req->u.nodeStatus.entId 	= nodeStatus.entId;
	req->u.nodeStatus.instId 	= nodeStatus.instId;
	req->u.nodeStatus.procId 	= nodeStatus.procId;

  if(debugFlag) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
      "Node Status Request: cmd_type:%d, entId:%d, instId:%d, procId:%d",
      req->cmd_type, req->u.nodeStatus.entId, req->u.nodeStatus.instId,
			req->u.nodeStatus.procId);
  }
}

int copyAddUserPart(Ss7SigtranSubsReq *req,  
                              const RSIEmsTypes::AddUserPart &addUsrPart)
{
  emsConfigInfo* conf = getEmsConfigInfo(addUsrPart.nwId);
  if(conf == NULL) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
    "Leaving copyAddUserPart, No Info for nwId:%d", addUsrPart.nwId);
    return 0;
  }

	req->cmd_type = ADD_USERPART;

  req->u.addUserPart.ssf   					= addUsrPart.ssf;
  req->u.addUserPart.lnkType   			= addUsrPart.lnkType; // MTP2 Proto Var
  req->u.addUserPart.upSwtch   			= addUsrPart.upSwtch; // MTP3 Proto Var
  req->u.addUserPart.mtp3UsapId   	= addUsrPart.mtp3UsapId;
  req->u.addUserPart.nwId   				= addUsrPart.nwId;
  //req->u.addUserPart.suType   			= addUsrPart.suType;  // MTP3 Proto Var
  req->u.addUserPart.suType   			= 3; // LIT_SP_SCCP
  req->u.addUserPart.m3uaUsapId   	= addUsrPart.m3uaUsapId;
  req->u.addUserPart.sccpLsapId   	= addUsrPart.sccpLsapId;
  req->u.addUserPart.currentUserState  = addUsrPart.userId;
	
	if(addUsrPart.nwkType == 1) {           // MTP3 Configuration
		req->u.addUserPart.userPartType = MTP3_USER;
		req->u.addUserPart.m3uaUsapId = 0;
    conf->mtp3SccpLsapId          = addUsrPart.sccpLsapId;
	}
	else if(addUsrPart.nwkType == 2) {			// SIGTRAN Configuration
		req->u.addUserPart.userPartType = M3UA_USER;
		req->u.addUserPart.mtp3UsapId = 0;
    conf->m3uaSccpLsapId          = addUsrPart.sccpLsapId;
	}

	 if(debugFlag) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0,
      "Add UserPart Request for [%s]: cmd_type:%d, userId:%d, ssf:%d, lnkType:%d, "
			" upSwtch:%d, mtp3UsapId:%d, nwId:%d, suType:%d, m3uaUsapId:%d, "
			"sccpLsapId:%d, currentUserState: %d, nwkType:%d",
			(addUsrPart.nwkType == 1)?"MTP3":"SIGTRAN", req->cmd_type,
			addUsrPart.userId, req->u.addUserPart.ssf, req->u.addUserPart.lnkType,
			req->u.addUserPart.upSwtch, req->u.addUserPart.mtp3UsapId,
			req->u.addUserPart.nwId, req->u.addUserPart.suType,
			req->u.addUserPart.m3uaUsapId, req->u.addUserPart.sccpLsapId,
		 	req->u.addUserPart.currentUserState, addUsrPart.nwkType);
  }

	return 1;
}

int copyDelUserPart(Ss7SigtranSubsReq *req,  
															const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelUserPart &delUsrPart = cfgReq.union_type.delUsrPart();

	req->cmd_type = DEL_USR_PART;
  req->u.delUserPart.mtp3UsapId = delUsrPart.mtp3UsapId;
  req->u.delUserPart.m3uaUsapId = delUsrPart.m3uaUsapId;
  req->u.delUserPart.sccpLsapId = delUsrPart.sccpLsapId;
	
	if(delUsrPart.nwkType == 1) {           // MTP3 Configuration
		req->u.delUserPart.userPartType = MTP3_USER;
		req->u.delUserPart.m3uaUsapId = 0;
	}
	else if(delUsrPart.nwkType == 2) {			// SIGTRAN Configuration
		req->u.delUserPart.userPartType = M3UA_USER;
		req->u.delUserPart.mtp3UsapId = 0;
	}

   if(debugFlag) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0,
      "Delete UserPart Request:  cmd_type:%d, mtp3UsapId:%d, "
			"m3uaUsapId:%d, sccpLsapId:%d",
  		req->cmd_type, req->u.delUserPart.mtp3UsapId,
  		req->u.delUserPart.m3uaUsapId, req->u.delUserPart.sccpLsapId);
		}
	return 1;
}

int copyAddGtAddrMap(Ss7SigtranSubsReq *req, 
														const RSIEmsTypes::AddAddrMapCfg &addAddrMap)
{

	int  dumpLen =0;
	char dumpBuf[6000];
	memset(dumpBuf, 0, 6000);

	req->cmd_type = ADD_GTADDRMAP;
  req->u.addAddrMapCfg.nwkId     = addAddrMap.nwkId;
  req->u.addAddrMapCfg.sw     	= addAddrMap.sw;
  req->u.addAddrMapCfg.format  	= addAddrMap.format;
  req->u.addAddrMapCfg.replGt  	= addAddrMap.replGt;

  req->u.addAddrMapCfg.oddEven  = addAddrMap.oddEven;
  req->u.addAddrMapCfg.natAddr  = addAddrMap.natAddr;
  req->u.addAddrMapCfg.tType    = addAddrMap.tType;
  req->u.addAddrMapCfg.numPlan  = addAddrMap.numPlan;
  req->u.addAddrMapCfg.encSch   = addAddrMap.encSch;

  req->u.addAddrMapCfg.actn.nmbActns   = 1;
  req->u.addAddrMapCfg.actn.type 			 = addAddrMap.actn.type;
  req->u.addAddrMapCfg.actn.startDigit = addAddrMap.actn.startDigit;
  req->u.addAddrMapCfg.actn.endDigit   = addAddrMap.actn.endDigit;
  req->u.addAddrMapCfg.gtDigLen 			 = addAddrMap.gtDigLen;
	strncpy((char *)req->u.addAddrMapCfg.gtDigits,
			addAddrMap.gtDigits.in(), req->u.addAddrMapCfg.gtDigLen);

  req->u.addAddrMapCfg.mode		 = addAddrMap.mode;
  req->u.addAddrMapCfg.outNwId = addAddrMap.outNwId;

	if(debugFlag) {
  	dumpLen += sprintf(dumpBuf+dumpLen, 
				"Add GtAddrMap Request: cmd_type:%d, nwId:%d, sw:%d, format:%d, replGt:%d,"
				"oddEven:%d, natAddr:%d, tType:%d, numPlan:%d, encSch:%d "
				"nmbActns:%d Action[type:%d, startDig:%d, endDig:%d],"
				"gtDigits:%s, gtDigLen:%d", 
  			req->cmd_type, req->u.addAddrMapCfg.nwkId, req->u.addAddrMapCfg.sw,
				req->u.addAddrMapCfg.format, req->u.addAddrMapCfg.replGt,
				req->u.addAddrMapCfg.oddEven, req->u.addAddrMapCfg.natAddr,
				req->u.addAddrMapCfg.tType, req->u.addAddrMapCfg.numPlan,
				req->u.addAddrMapCfg.encSch, req->u.addAddrMapCfg.actn.nmbActns,
				req->u.addAddrMapCfg.actn.type, req->u.addAddrMapCfg.actn.startDigit,
				req->u.addAddrMapCfg.actn.endDigit,
				req->u.addAddrMapCfg.gtDigits, req->u.addAddrMapCfg.gtDigLen);
	}

  req->u.addAddrMapCfg.numEntity = addAddrMap.outAddress.length();

	for(int i=0; i < req->u.addAddrMapCfg.numEntity; ++i)
	{
		req->u.addAddrMapCfg.outAddr[i].spHdrOpt = addAddrMap.outAddress[i].spHdrOpt;
		req->u.addAddrMapCfg.outAddr[i].swtch    = addAddrMap.outAddress[i].swtch;
		req->u.addAddrMapCfg.outAddr[i].ssf      = addAddrMap.outAddress[i].ssf;
		req->u.addAddrMapCfg.outAddr[i].niInd    = addAddrMap.outAddress[i].niInd;
		req->u.addAddrMapCfg.outAddr[i].rtgInd   = addAddrMap.outAddress[i].rtgInd;
		req->u.addAddrMapCfg.outAddr[i].ssn      = addAddrMap.outAddress[i].ssn;

		// calculating following two values as EMS is not sending it. 
		if(req->u.addAddrMapCfg.outAddr[i].ssn != 0)
			req->u.addAddrMapCfg.outAddr[i].ssnInd = 1;

		if(addAddrMap.outAddress[i].pc_char.in() != NULL)
			req->u.addAddrMapCfg.outAddr[i].pcInd = 1;

		U8 pcDet[3];
		req->u.addAddrMapCfg.outAddr[i].pc = g_convertPcToDec
		((char*)addAddrMap.outAddress[i].pc_char.in(), pcDet, 
							req->u.addAddrMapCfg.sw);
		req->u.addAddrMapCfg.outAddr[i].swtch = req->u.addAddrMapCfg.sw;

		// rajeev 
		// req->u.addAddrMapCfg.outAddr[i].swtch);

		req->u.addAddrMapCfg.outAddr[i].format  = addAddrMap.outAddress[i].format;
		req->u.addAddrMapCfg.outAddr[i].oddEven = addAddrMap.outAddress[i].oddEven;
		req->u.addAddrMapCfg.outAddr[i].tType   = addAddrMap.outAddress[i].tType;
		req->u.addAddrMapCfg.outAddr[i].natAddr = addAddrMap.outAddress[i].natAddr;
		req->u.addAddrMapCfg.outAddr[i].numPlan = addAddrMap.outAddress[i].numPlan;
		req->u.addAddrMapCfg.outAddr[i].encSch  = addAddrMap.outAddress[i].encSch;
		req->u.addAddrMapCfg.outAddr[i].gtDigLen= addAddrMap.outAddress[i].gtDigLen;
		strncpy((char *)req->u.addAddrMapCfg.outAddr[i].gtDigits,
							addAddrMap.outAddress[i].gtDigits.in(), 
							req->u.addAddrMapCfg.outAddr[i].gtDigLen);

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, 
					"OutAddress [(%d):spHdrOpt:%d, swtch:%d, ssf:%d,"
					"niInd:%d, rtgInd:%d, ssnInd:%d, pcInd:%d, ssn:%d"
					", pc:%d-%s, format:%d, oddEven:%d, tType:%d, "
					"natAddr:%d, numPlan:%d, encSch:%d, gtDigLen:%d,"
					"gtDigits:%s", i,
						req->u.addAddrMapCfg.outAddr[i].spHdrOpt,
						req->u.addAddrMapCfg.outAddr[i].swtch,
						req->u.addAddrMapCfg.outAddr[i].ssf,
						req->u.addAddrMapCfg.outAddr[i].niInd,
						req->u.addAddrMapCfg.outAddr[i].rtgInd,
						req->u.addAddrMapCfg.outAddr[i].ssnInd,
						req->u.addAddrMapCfg.outAddr[i].pcInd,
						req->u.addAddrMapCfg.outAddr[i].ssn,
						req->u.addAddrMapCfg.outAddr[i].pc,
						addAddrMap.outAddress[i].pc_char.in(),
						req->u.addAddrMapCfg.outAddr[i].format,
						req->u.addAddrMapCfg.outAddr[i].oddEven,
						req->u.addAddrMapCfg.outAddr[i].tType,
						req->u.addAddrMapCfg.outAddr[i].natAddr,
						req->u.addAddrMapCfg.outAddr[i].numPlan,
						req->u.addAddrMapCfg.outAddr[i].encSch,
						req->u.addAddrMapCfg.outAddr[i].gtDigLen,
						req->u.addAddrMapCfg.outAddr[i].gtDigits);
		}
	}

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addGtRule: %s", dumpBuf);
	}

	return 1;
}

int copyDelGtAddrMap (Ss7SigtranSubsReq *req,  
																	const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelAddrMapCfg &delAddrMap = 
													cfgReq.union_type.delAddrMapConfig();

	int  dumpLen =0;
	char dumpBuf[6000];
	memset(dumpBuf, 0, 6000);

	req->cmd_type = DEL_GTADDRMAP;
  req->u.delAddrMapCfg.nwkId     = delAddrMap.nwkId;
  req->u.delAddrMapCfg.sw     	= delAddrMap.sw;
  req->u.delAddrMapCfg.format  	= delAddrMap.format;
  req->u.delAddrMapCfg.replGt  	= delAddrMap.replGt;

  req->u.delAddrMapCfg.oddEven  = delAddrMap.oddEven;
  req->u.delAddrMapCfg.natAddr  = delAddrMap.natAddr;
  req->u.delAddrMapCfg.tType    = delAddrMap.tType;
  req->u.delAddrMapCfg.numPlan  = delAddrMap.numPlan;
  req->u.delAddrMapCfg.encSch   = delAddrMap.encSch;

  req->u.delAddrMapCfg.actn.nmbActns   = 1;
  req->u.delAddrMapCfg.actn.type 			 = delAddrMap.actn.type;
  req->u.delAddrMapCfg.actn.startDigit = delAddrMap.actn.startDigit;
  req->u.delAddrMapCfg.actn.endDigit   = delAddrMap.actn.endDigit;

  req->u.delAddrMapCfg.gtDigLen = delAddrMap.gtDigLen;
  memset(req->u.delAddrMapCfg.gtDigits, '\0',MAX_GT_DIGITS);
  strncpy((char *)req->u.delAddrMapCfg.gtDigits,
							delAddrMap.gtDigits.in(), 
							req->u.delAddrMapCfg.gtDigLen);

	if(debugFlag) {
  	dumpLen += sprintf(dumpBuf+dumpLen, 
				"Add GT AddrMap Request: cmd_type:%d, nwId:%d, sw:%d, format:%d, replGt:%d,"
				"oddEven:%d, natAddr:%d, tType:%d, numPlan:%d, encSch:%d "
				"encSchPres:%d, nmbActns:%d Action[type:%d, startDig:%d, endDig:%d]", 
  			req->cmd_type, req->u.delAddrMapCfg.nwkId, req->u.delAddrMapCfg.sw,
				req->u.delAddrMapCfg.format, req->u.delAddrMapCfg.replGt,
				req->u.delAddrMapCfg.oddEven, req->u.delAddrMapCfg.natAddr,
				req->u.delAddrMapCfg.tType, req->u.delAddrMapCfg.numPlan,
				req->u.delAddrMapCfg.encSch, req->u.delAddrMapCfg.actn.nmbActns,
				req->u.delAddrMapCfg.actn.type, req->u.delAddrMapCfg.actn.startDigit,
				req->u.delAddrMapCfg.actn.endDigit);
	}


  req->u.delAddrMapCfg.numEntity = delAddrMap.outAddress.length();

	for(int i=0; i < req->u.delAddrMapCfg.numEntity; ++i)
	{
		req->u.delAddrMapCfg.outAddr[i].spHdrOpt = delAddrMap.outAddress[i].spHdrOpt;
		req->u.delAddrMapCfg.outAddr[i].swtch    = delAddrMap.outAddress[i].swtch;
		req->u.delAddrMapCfg.outAddr[i].ssf      = delAddrMap.outAddress[i].ssf;
		req->u.delAddrMapCfg.outAddr[i].niInd    = delAddrMap.outAddress[i].niInd;
		req->u.delAddrMapCfg.outAddr[i].rtgInd   = delAddrMap.outAddress[i].rtgInd;
		req->u.delAddrMapCfg.outAddr[i].ssnInd   = delAddrMap.outAddress[i].ssnInd;
		req->u.delAddrMapCfg.outAddr[i].pcInd    = delAddrMap.outAddress[i].pcInd;
		req->u.delAddrMapCfg.outAddr[i].ssn      = delAddrMap.outAddress[i].ssn;

		U8 pcDet[3];
		req->u.delAddrMapCfg.outAddr[i].pc = g_convertPcToDec
		((char*)delAddrMap.outAddress[i].pc_char.in(), pcDet, 
		req->u.delAddrMapCfg.outAddr[i].swtch);

		req->u.delAddrMapCfg.outAddr[i].format  = delAddrMap.outAddress[i].format;
		req->u.delAddrMapCfg.outAddr[i].oddEven = delAddrMap.outAddress[i].oddEven;
		req->u.delAddrMapCfg.outAddr[i].tType   = delAddrMap.outAddress[i].tType;
		req->u.delAddrMapCfg.outAddr[i].natAddr = delAddrMap.outAddress[i].natAddr;
		req->u.delAddrMapCfg.outAddr[i].numPlan = delAddrMap.outAddress[i].numPlan;
		req->u.delAddrMapCfg.outAddr[i].encSch  = delAddrMap.outAddress[i].encSch;
		req->u.delAddrMapCfg.outAddr[i].gtDigLen= delAddrMap.outAddress[i].gtDigLen;
    memset(req->u.delAddrMapCfg.outAddr[i].gtDigits, '\0',MAX_GT_DIGITS);
		strncpy((char *)req->u.delAddrMapCfg.outAddr[i].gtDigits,
							delAddrMap.outAddress[i].gtDigits.in(), 
							req->u.delAddrMapCfg.outAddr[i].gtDigLen);

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, 
					"OutAddress [(%d):spHdrOpt:%d, swtch:%d, ssf:%d,"
					"niInd:%d, rtgInd:%d, ssnInd:%d, pcInd:%d, ssn:%d"
					", pc:%d-%s, format:%d, oddEven:%d, tType:%d, "
					"natAddr:%d, numPlan:%d, encSch:%d, gtDigLen:%d,"
					"gtDigits:%s", i,
						req->u.delAddrMapCfg.outAddr[i].spHdrOpt,
						req->u.delAddrMapCfg.outAddr[i].swtch,
						req->u.delAddrMapCfg.outAddr[i].ssf,
						req->u.delAddrMapCfg.outAddr[i].niInd,
						req->u.delAddrMapCfg.outAddr[i].rtgInd,
						req->u.delAddrMapCfg.outAddr[i].ssnInd,
						req->u.delAddrMapCfg.outAddr[i].pcInd,
						req->u.delAddrMapCfg.outAddr[i].ssn,
						req->u.delAddrMapCfg.outAddr[i].pc,
						delAddrMap.outAddress[i].pc_char.in(),
						req->u.delAddrMapCfg.outAddr[i].format,
						req->u.delAddrMapCfg.outAddr[i].oddEven,
						req->u.delAddrMapCfg.outAddr[i].tType,
						req->u.delAddrMapCfg.outAddr[i].natAddr,
						req->u.delAddrMapCfg.outAddr[i].numPlan,
						req->u.delAddrMapCfg.outAddr[i].encSch,
						req->u.delAddrMapCfg.outAddr[i].gtDigLen,
						req->u.delAddrMapCfg.outAddr[i].gtDigits);
		}
	}

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addGtRule: %s", dumpBuf);
	}

	return 1;
}


void copyAddRule(Ss7SigtranSubsReq *req, const RSIEmsTypes::AddGtRule &addRule)
{

	int  dumpLen =0;
	char dumpBuf[4000];
	memset(dumpBuf, 0, 4000);

	req->cmd_type = ADD_GTRULE;
  req->u.addGtRule.nwId     		= addRule.nwId;
  req->u.addGtRule.sw     			= addRule.sw;
  req->u.addGtRule.formatPres   = addRule.formatPres;
  req->u.addGtRule.format     	= addRule.format;
  req->u.addGtRule.oddEven     	= addRule.oddEven;
  req->u.addGtRule.oddEvenPres  = addRule.oddEvenPres;
  req->u.addGtRule.natAddr     	= addRule.natAddr;
  req->u.addGtRule.natAddrPres 	= addRule.natAddrPres;
  req->u.addGtRule.tType     		= addRule.tType;
  req->u.addGtRule.tTypePres    = addRule.tTypePres;
  req->u.addGtRule.numPlan     	= addRule.numPlan;
  req->u.addGtRule.numPlanPres  = addRule.numPlanPres;
  req->u.addGtRule.encSch     	= addRule.encSch;
  req->u.addGtRule.encSchPres   = addRule.encSchPres;
  req->u.addGtRule.nmbActns     = addRule.actn.length();

  if(req->u.addGtRule.format == 1 || req->u.addGtRule.format == 2 || 
          req->u.addGtRule.format == 3|| req->u.addGtRule.format == 4)
    req->u.addGtRule.formatPres = 1;
  if(req->u.addGtRule.format == 2 || req->u.addGtRule.format == 3 || req->u.addGtRule.format == 4)
    req->u.addGtRule.tTypePres = 1;
  if(req->u.addGtRule.format == 3 || req->u.addGtRule.format == 4)
    req->u.addGtRule.encSchPres = 1;
  if(req->u.addGtRule.format == 3 || req->u.addGtRule.format == 4)
    req->u.addGtRule.numPlanPres = 1;
  if(req->u.addGtRule.format == 1 || req->u.addGtRule.format == 4)
    req->u.addGtRule.natAddrPres = 1;
  if(req->u.addGtRule.format == 1)
    req->u.addGtRule.oddEvenPres = 1;

	if(debugFlag) {
  	dumpLen += sprintf(dumpBuf+dumpLen, 
				"AddRule Request: cmd_type:%d, nwId:%d, sw:%d, formatPres:%d, format:%d, "
				"oddEven:%d, oddEvenPres:%d, natAddr:%d, natAddrPres:%d, "
				"tType:%d, tTypePres:%d, numPlan:%d, numPlanPres:%d, encSch:%d "
				"encSchPres:%d, nmbActns:%d", 
  			req->cmd_type, req->u.addGtRule.nwId, req->u.addGtRule.sw, req->u.addGtRule.formatPres,
  			req->u.addGtRule.format, req->u.addGtRule.oddEven, 
				req->u.addGtRule.oddEvenPres, req->u.addGtRule.natAddr, 
				req->u.addGtRule.natAddrPres, req->u.addGtRule.tType,
  			req->u.addGtRule.tTypePres, req->u.addGtRule.numPlan, 
				req->u.addGtRule.numPlanPres, req->u.addGtRule.encSch, 
				req->u.addGtRule.encSchPres, req->u.addGtRule.nmbActns);
	}

	for(int i=0; i < req->u.addGtRule.nmbActns; ++i) {
		req->u.addGtRule.actn[i].type 			= addRule.actn[i].type; 
		req->u.addGtRule.actn[i].startDigit = addRule.actn[i].startDigit;
		req->u.addGtRule.actn[i].endDigit   = addRule.actn[i].endDigit;

		if(debugFlag) {
			 dumpLen += sprintf(dumpBuf+dumpLen,
			 " actn[%d]: {type:%d, gtRuleId:%d, startDigit:%d, endDigit:%d}, ",
				i, req->u.addGtRule.actn[i].type, addRule.actn[i].gtRuleId,
				req->u.addGtRule.actn[i].startDigit, req->u.addGtRule.actn[i].endDigit);
		}
	}

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addGtRule: %s", dumpBuf);
	}
}

void copyDelRule(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelGtRule &delRule = cfgReq.union_type.delGtRle();

	int  dumpLen =0;
	char dumpBuf[4000];
	memset(dumpBuf, 0, 4000);

	req->cmd_type = DEL_GTRULE;
  req->u.delGtRule.nwId     		= delRule.nwId;
  req->u.delGtRule.sw     			= delRule.sw;
  req->u.delGtRule.formatPres   = delRule.formatPres;
  req->u.delGtRule.format     	= delRule.format;
  req->u.delGtRule.oddEven     	= delRule.oddEven;
  req->u.delGtRule.oddEvenPres  = delRule.oddEvenPres;
  req->u.delGtRule.natAddr     	= delRule.natAddr;
  req->u.delGtRule.natAddrPres 	= delRule.natAddrPres;
  req->u.delGtRule.tType     		= delRule.tType;
  req->u.delGtRule.tTypePres    = delRule.tTypePres;
  req->u.delGtRule.numPlan     	= delRule.numPlan;
  req->u.delGtRule.numPlanPres  = delRule.numPlanPres;
  req->u.delGtRule.encSch     	= delRule.encSch;
  req->u.delGtRule.encSchPres   = delRule.encSchPres;
  req->u.delGtRule.nmbActns     = delRule.nmbActns;

  if(req->u.delGtRule.format == 1 || req->u.delGtRule.format == 2 || 
          req->u.delGtRule.format == 3|| req->u.delGtRule.format == 4)
    req->u.delGtRule.formatPres = 1;
  if(req->u.delGtRule.format == 2 || req->u.delGtRule.format == 3 || req->u.delGtRule.format == 4)
    req->u.delGtRule.tTypePres = 1;
  if(req->u.delGtRule.format == 3 || req->u.delGtRule.format == 4)
    req->u.delGtRule.encSchPres = 1;
  if(req->u.delGtRule.format == 3 || req->u.delGtRule.format == 4)
    req->u.delGtRule.numPlanPres = 1;
  if(req->u.delGtRule.format == 1 || req->u.delGtRule.format == 4)
    req->u.delGtRule.natAddrPres = 1;
  if(req->u.delGtRule.format == 1)
    req->u.delGtRule.oddEvenPres = 1;


	if(debugFlag) {
  	dumpLen += sprintf(dumpBuf+dumpLen, 
				"DeleteGtRule Request: cmd_type:%d, nwId:%d, sw:%d, formatPres:%d, format:%d, "
				"oddEven:%d, oddEvenPres:%d, natAddr:%d, natAddrPres:%d, "
				"tType:%d, tTypePres:%d, numPlan:%d, numPlanPres:%d, encSch:%d "
				"encSchPres:%d, nmbActns:%d", 
  			req->cmd_type, req->u.delGtRule.nwId, req->u.delGtRule.sw, req->u.delGtRule.formatPres,
  			req->u.delGtRule.format, req->u.delGtRule.oddEven, req->u.delGtRule.oddEvenPres,
  			req->u.delGtRule.natAddr, req->u.delGtRule.natAddrPres, req->u.delGtRule.tType,
  			req->u.delGtRule.tTypePres, req->u.delGtRule.numPlan, req->u.delGtRule.numPlanPres,
  			req->u.delGtRule.encSch, req->u.delGtRule.encSchPres, req->u.delGtRule.nmbActns);
	}

	for(int i=0; i < req->u.delGtRule.nmbActns; ++i) {
		req->u.delGtRule.actn[i].type 			= delRule.actn[i].type; 
		req->u.delGtRule.actn[i].startDigit = delRule.actn[i].startDigit;
		req->u.delGtRule.actn[i].endDigit   = delRule.actn[i].endDigit;

		if(debugFlag) {
			 dumpLen += sprintf(dumpBuf+dumpLen,
			 " {type:%d, nmbActns:%d, startDigit:%d, endDigit:%d}, ",
				req->u.delGtRule.actn[i].type, req->u.delGtRule.actn[i].nmbActns,
				req->u.delGtRule.actn[i].startDigit, req->u.delGtRule.actn[i].endDigit);
		}
	}

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "delGtRule: %s", dumpBuf);
	}
}

string 
copyAddLocalSsn(Ss7SigtranSubsReq *req, const RSIEmsTypes::AddSsn &ssn) 
{

	int  dumpLen =0;
	char dumpBuf[4000];
	memset(dumpBuf, 0, 4000);

	emsConfigInfo* conf = getEmsConfigInfo(ssn.nwId);
	if(conf == NULL) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"Leaving AddLocalSsn, No Info for nwId:%d", ssn.nwId);
		return "NwId not available for SSN";
	}

	req->cmd_type 							= ADD_LOCAL_SSN;
	req->u.addLocalSsn.nwId  		= ssn.nwId;
	req->u.addLocalSsn.nmbBpc  	= ssn.bpcList.length();

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen,
			"Add LocalSSN Request: cmd_type:%d, Nwid:%d, nmbBpc:%d",
			req->cmd_type, req->u.addLocalSsn.nwId, req->u.addLocalSsn.nmbBpc);
	}

	U8 pcDet[3];
	for(int i=0; i < req->u.addLocalSsn.nmbBpc; ++i) {
		 req->u.addLocalSsn.bpcList[i].bpc 	 = 
			g_convertPcToDec((char*)ssn.bpcList[i].pc_char.in(), pcDet, conf->proto);
		 req->u.addLocalSsn.bpcList[i].prior = ssn.bpcList[i].prior;

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, 
			" bpcList[%d]:{bpc:%d:%s, prior:%d}, ",
			i, req->u.addLocalSsn.bpcList[i].bpc, ssn.bpcList[i].pc_char.in(),
			req->u.addLocalSsn.bpcList[i].prior);
		}
	}

	req->u.addLocalSsn.nmbConPc  	= ssn.conPcList.length();

	if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, " nmbConPc:%d, ",
			req->u.addLocalSsn.nmbConPc);
	}

	for(int i=0; i < req->u.addLocalSsn.nmbConPc; ++i) {
		req->u.addLocalSsn.conPc[i] = 
		g_convertPcToDec((char*)ssn.conPcList[i].pc_char.in(), pcDet, conf->proto);


		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen, " conPc[%d]:%d:%s,",
			i, req->u.addLocalSsn.conPc[i], ssn.conPcList[i].pc_char.in());
		}
	}

	req->u.addLocalSsn.sccpUsapId = ssn.sccpUsapId;
	req->u.addLocalSsn.swtch  		= ssn.swtch;
	req->u.addLocalSsn.ssn  			= ssn.ssn;
	req->u.addLocalSsn.tcapLsapId = ssn.tcapLsapId;
	req->u.addLocalSsn.tcapUsapId = ssn.tcapUsapId;
	req->u.addLocalSsn.currentSsnState = 0;

	unsigned long lpc =0;
	bool validOpc = INGwTcapProvider::getInstance().verifyOpcSsnInConfigList
					(lpc, req->u.addLocalSsn.ssn);

	if(validOpc == false) 
	{
		delete req;
		req = NULL;
		return "SSN is not configured as default configuration";
	}

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, " sccpUsapId:%d, swtch:%d, ssn:%d, "
		"tcapLsapId:%d, tcapUsapId:%d, currentSsnState:%d",
		req->u.addLocalSsn.sccpUsapId, req->u.addLocalSsn.swtch,
		req->u.addLocalSsn.ssn, req->u.addLocalSsn.tcapLsapId, 
		req->u.addLocalSsn.tcapUsapId,
		req->u.addLocalSsn.currentSsnState);

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addLocalSsn: %s", dumpBuf);
	}

	return "Success";
}

int copyDelLocalSsn(Ss7SigtranSubsReq *req,  
														const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::ModifySsn &ssn = cfgReq.union_type.modSsn();

	req->cmd_type 							= DEL_LOCAL_SSN;
	req->u.delLocalSsn.ssn = ssn.ssn;
	req->u.delLocalSsn.tcapLsapId = ssn.tcapLsapId;
	req->u.delLocalSsn.tcapUsapId = ssn.tcapUsapId;
	req->u.delLocalSsn.sccpUsapId = ssn.sccpUsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"DelLocalSsn: cmd_type:%d, ssn:%d, tcapLsapId:%d, tcapUsapId:%d, sccpUsapId:%d",
		req->cmd_type, req->u.delLocalSsn.ssn, req->u.delLocalSsn.tcapLsapId,
		req->u.delLocalSsn.tcapUsapId, req->u.delLocalSsn.sccpUsapId);
	}

	return 1;
}

void copyLocalSsnStatus(Ss7SigtranSubsReq *req,  
															const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::LocalSsnStatus &ssn = cfgReq.union_type.ssnStatus();

	req->u.ssnStatus.ssn 		= ssn.ssn;
	req->u.ssnStatus.dpc 		= ssn.dpc;
	req->u.ssnStatus.nwkId 	= ssn.nwkId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"LocalSsnStatus: ssn:%d, dpc:%d, nwkId:%d",
		req->u.ssnStatus.ssn, req->u.ssnStatus.dpc, req->u.ssnStatus.nwkId);
	}
}

void copyAddEndPoint(Ss7SigtranSubsReq *req, const RSIEmsTypes::AddEndPoint &ep)
{
	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 2048);

	req->cmd_type = ADD_ENDPOINT;
	req->u.addEp.endPointid	= ep.endPointid;
	req->u.addEp.srcPort 		= ep.srcPort;
	req->u.addEp.nmbAddrs 	= ep.nmbAddrs;

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, 
		"Add EndPoint cmd_type:%d, endPointid:%d, srcPort:%d, nmbAddrs:%d, ",
		req->cmd_type, req->u.addEp.endPointid, req->u.addEp.srcPort,
		req->u.addEp.nmbAddrs);
	}

	for(int i=0; i < req->u.addEp.nmbAddrs; ++i) {
		// Need to check if IP corresponds to IPV6 or IPV4
		string ipAddr = ep.nAddr[i].in();
		if(!ipAddr.empty()) {
			size_t found = ipAddr.find(":");
			if(found != string::npos) { // found
				req->u.addEp.nAddr[i].type = CM_TPTADDR_IPV6;
				memcpy(&req->u.addEp.nAddr[i].u.ipv6NetAddr, ipAddr.c_str(), 
							 ipAddr.size());

				if(debugFlag) {
					dumpLen += sprintf(dumpBuf+dumpLen, 
						"IPV6[%d]:%s, ", i,  ipAddr.c_str());
				}
			}
			else {
				req->u.addEp.nAddr[i].type = CM_TPTADDR_IPV4;
				req->u.addEp.nAddr[i].u.ipv4NetAddr = 
															ntohl(inet_addr((S8*)ipAddr.c_str()));
				if(debugFlag) {
					dumpLen += sprintf(dumpBuf+dumpLen, 
						"IPV4[%d]:%s, ", i,  ipAddr.c_str());
				}
			}
		}
	}

	req->u.addEp.sctpProcId 		= INGwTcapProvider::getInstance().
									                getProcIdForSubsysId(ep.procId);
	req->u.addEp.sctpLsapId 		= ep.sctpLsapId;
	req->u.addEp.sctpUsapId 		= ep.sctpUsapId;
	req->u.addEp.m3uaLsapId 		= ep.m3uaLsapId;
	req->u.addEp.tuclUsapId 		= ep.tuclUsapId;
	req->u.addEp.currentEpState = ep.currentEpState;

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, 
		" sctpProcId:%d, sctpLsapId:%d, sctpUsapId:%d, m3uaLsapId:%d,"
		"tuclUsapId:%d, currentEpState:%d",
		req->u.addEp.sctpProcId, req->u.addEp.sctpLsapId,
		req->u.addEp.sctpUsapId, req->u.addEp.m3uaLsapId,
		req->u.addEp.tuclUsapId, req->u.addEp.currentEpState);

		logger.logMsg(ALWAYS_FLAG, 0, "%s", dumpBuf);
	}
}

void copyDelEndPoint(Ss7SigtranSubsReq *req,  
																const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelEndPoint &ep = cfgReq.union_type.delep();

	req->cmd_type = DEL_ENDPOINT;
	req->u.delEp.sctpProcId     = INGwTcapProvider::getInstance().
									                getProcIdForSubsysId(ep.sctpProcId);
	req->u.delEp.sctpLsapId     = ep.sctpLsapId;
	req->u.delEp.sctpUsapId     = ep.sctpUsapId;
	req->u.delEp.m3uaLsapId     = ep.m3uaLsapId;
	req->u.delEp.tuclUsapId     = ep.tuclUsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"DelEndPoint: cmd_type:%d, sctpProcId:%d, sctpLsapId:%d, sctpUsapId:%d, m3uaLsapId:%d,"
		"tuclUsapId:%d", req->cmd_type, req->u.delEp.sctpProcId, req->u.delEp.sctpLsapId, 
		req->u.delEp.sctpUsapId, req->u.delEp.m3uaLsapId, req->u.delEp.tuclUsapId);
	}
}

void copyDisableEndPoint(Ss7SigtranSubsReq *req,  
																const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DisableEndPoint &ep = cfgReq.union_type.disableEp();

	req->cmd_type = DISABLE_ENDPOINT;
	req->u.disableEp.sctpProcId     = ep.sctpProcId;
	req->u.disableEp.sctpLsapId     = ep.sctpLsapId;
	req->u.disableEp.sctpUsapId     = ep.sctpUsapId;
	req->u.disableEp.m3uaLsapId     = ep.m3uaLsapId;
	req->u.disableEp.tuclUsapId     = ep.tuclUsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"DisableEndPoint: cmd_type:%d, sctpProcId:%d, sctpLsapId:%d, sctpUsapId:%d, m3uaLsapId:%d, tuclUsapId:%d",
		req->cmd_type, req->u.disableEp.sctpProcId, req->u.disableEp.sctpLsapId, req->u.disableEp.sctpUsapId, 
		req->u.disableEp.m3uaLsapId, req->u.disableEp.tuclUsapId);
	}
}

void copyEnableEndPoint(Ss7SigtranSubsReq *req,  
													const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::EnableEndPoint &ep = cfgReq.union_type.enableEp();

	req->cmd_type = ENABLE_ENDPOINT;
	req->u.enableEp.sctpProcId     = ep.sctpProcId;
	req->u.enableEp.sctpLsapId     = ep.sctpLsapId;
	req->u.enableEp.sctpUsapId     = ep.sctpUsapId;
	req->u.enableEp.m3uaLsapId     = ep.m3uaLsapId;
	req->u.enableEp.tuclUsapId     = ep.tuclUsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"EnableEndPoint: cmd_type:%d, sctpProcId:%d, sctpLsapId:%d, sctpUsapId:%d, m3uaLsapId:%d, tuclUsapId:%d",
		req->cmd_type, req->u.enableEp.sctpProcId, req->u.enableEp.sctpLsapId, req->u.enableEp.sctpUsapId, 
		req->u.enableEp.m3uaLsapId, req->u.enableEp.tuclUsapId);
	}
}

//void copyEnableSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
//{
//	const RSIEmsTypes::SsnEnable &ssn = cfgReq.union_type.ssnEnble();
//
//	req->u.ssnEnable.ssn     = ssn.ssn;
//	req->u.ssnEnable.tcapLsapId = ssn.tcapLsapId;
//	req->u.ssnEnable.tcapUsapId = ssn.tcapUsapId;
//	req->u.ssnEnable.sccpUsapId = ssn.sccpUsapId;
//
//	if(debugFlag) {
//		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
//		"SsnEnable: ssn:%d, tcapLsapId:%d, tcapUsapId:%d, sccpUsapId:%d",
//		req->u.ssnEnable.ssn, req->u.ssnEnable.tcapLsapId,
//		req->u.ssnEnable.tcapUsapId, req->u.ssnEnable.sccpUsapId);
//	}
//}
//
//void copyDisableSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
//{
//	const RSIEmsTypes::SsnDisable &ssn = cfgReq.union_type.ssnDsable();
//
//	req->u.ssnDisable.ssn     = ssn.ssn;
//	req->u.ssnDisable.tcapLsapId = ssn.tcapLsapId;
//	req->u.ssnDisable.tcapUsapId = ssn.tcapUsapId;
//	req->u.ssnDisable.sccpUsapId = ssn.sccpUsapId;
//
//	if(debugFlag) {
//		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
//		"SsnDisable: ssn:%d, tcapLsapId:%d, tcapUsapId:%d, sccpUsapId:%d",
//		req->u.ssnDisable.ssn, req->u.ssnDisable.tcapLsapId,
//		req->u.ssnDisable.tcapUsapId, req->u.ssnDisable.sccpUsapId);
//	}
//}

//void copyAddRemoteSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
//{
//	const RSIEmsTypes::AddRemoteSsn &ssn = cfgReq.union_type.addRmoteSsn();
//
//	int  dumpLen =0;
//	char dumpBuf[4000];
//	memset(dumpBuf, 0, 4000);
//
//	req->u.addRemoteSsn.dpc 				= ssn.dpc;
//	req->u.addRemoteSsn.swtchType		= ssn.swtchType;
//	req->u.addRemoteSsn.spType			= ssn.spType;
//	req->u.addRemoteSsn.upSwtch			= ssn.upSwtch;
//	req->u.addRemoteSsn.cmbLnkSetId	= ssn.cmbLnkSetId;
//	req->u.addRemoteSsn.dir					= ssn.dir;
//	req->u.addRemoteSsn.rteToAdjSp	= ssn.rteToAdjSp;
//	req->u.addRemoteSsn.ssf					= ssn.ssf;
//	req->u.addRemoteSsn.swtch				= ssn.swtch;
//	req->u.addRemoteSsn.status			= ssn.status;
//	req->u.addRemoteSsn.nmbBpc			= ssn.nmbBpc;
//	req->u.addRemoteSsn.nmbSsns			= ssn.nmbSsns;
//#if (SS7_ANS96 || SS7_BELL05)
//	req->u.addRemoteSsn.replicatedMode	= ssn.replicatedMode;
//#endif
//	req->u.addRemoteSsn.preferredOpc		= ssn.preferredOpc;
//	req->u.addRemoteSsn.nSapId					= ssn.nSapId;
//	req->u.addRemoteSsn.currentDpcState	= ssn.currentDpcState;
//
//	if(debugFlag) {
//		dumpLen += sprintf(dumpBuf+dumpLen, "Add RemoteSsn Request: dpc:%d, swtchType:%d"
//				", spType:%d, upSwtch:%d, cmbLnkSetId:%d, dir:%d, rteToAdjSp:%d"
//				", ssf:%d, swtch:%d, status:%d, nmbBpc:%d, nmbSsns:%d"
//				", preferredOpc:%d, nSapId:%d, currentDpcState:%d", 
//				req->u.addRemoteSsn.dpc, req->u.addRemoteSsn.swtchType, req->u.addRemoteSsn.spType,
//				req->u.addRemoteSsn.upSwtch, req->u.addRemoteSsn.cmbLnkSetId, req->u.addRemoteSsn.dir,
//				req->u.addRemoteSsn.rteToAdjSp, req->u.addRemoteSsn.ssf, req->u.addRemoteSsn.swtch	,
//				req->u.addRemoteSsn.status, req->u.addRemoteSsn.nmbBpc, req->u.addRemoteSsn.nmbSsns,
//				req->u.addRemoteSsn.preferredOpc, req->u.addRemoteSsn.nSapId		,
//				req->u.addRemoteSsn.currentDpcState);
//#if (SS7_ANS96 || SS7_BELL05)
//		dumpLen += sprintf(dumpBuf+dumpLen, " replicateMode:%d", req->u.addRemoteSsn.replicatedMode);
//#endif
//
//	}
//
//	for(int i=0; i < req->u.addRemoteSsn.nmbBpc; ++i) {
//		req->u.addRemoteSsn.bpcList[i].bpc   = ssn.bpcList[i].bpc;
//		req->u.addRemoteSsn.bpcList[i].prior = ssn.bpcList[i].prior;
//
//		if(debugFlag) {
//		dumpLen += sprintf(dumpBuf+dumpLen, " bpcList[%d]:{bpc:%d, prior:%d}, ",
//					i, req->u.addRemoteSsn.bpcList[i].bpc, req->u.addRemoteSsn.bpcList[i].prior);
//		}
//	}
//
//	for(int j=0; j < req->u.addRemoteSsn.nmbSsns; ++j) {
//		req->u.addRemoteSsn.ssnList[j].ssn      = ssn.ssnList[j].ssn;
//		req->u.addRemoteSsn.ssnList[j].status   = ssn.ssnList[j].status;
//		req->u.addRemoteSsn.ssnList[j].nmbBpc   = ssn.ssnList[j].nmbBpc;
//		req->u.addRemoteSsn.ssnList[j].nmbConPc = ssn.ssnList[j].nmbConPc;
//
//		if(debugFlag) {
//		dumpLen += sprintf(dumpBuf+dumpLen, " nmbSsns[%d]:{ssn:%d, status:%d, nmbBpc:%d, nmbConPc:%d",
//				j, req->u.addRemoteSsn.ssnList[j].ssn , req->u.addRemoteSsn.ssnList[j].status,
//				req->u.addRemoteSsn.ssnList[j].nmbBpc, req->u.addRemoteSsn.ssnList[j].nmbConPc);
//		}
//
//		for(int k=0; k < req->u.addRemoteSsn.ssnList[j].nmbBpc;++k) {
//			req->u.addRemoteSsn.ssnList[j].bpcList[k].bpc   = ssn.ssnList[j].bpcList[k].bpc;
//			req->u.addRemoteSsn.ssnList[j].bpcList[k].prior = ssn.ssnList[j].bpcList[k].prior;
//
//			if(debugFlag) {
//				dumpLen += sprintf(dumpBuf+dumpLen, " bpcList[%d]:{bpc:%d, Prior:%d}, ",
//				k, req->u.addRemoteSsn.ssnList[j].bpcList[k].bpc, 
//				req->u.addRemoteSsn.ssnList[j].bpcList[k].prior);
//			}
//		}
//
//// TBC
////	for(int l=0; l < req->u.addRemoteSsn.ssnList[j].nmbConPc;++l) {
////		req->u.addRemoteSsn.ssnList[j].conPc[l] = ssn.ssnList[j].conPc[l];
////
////			if(debugFlag) {
////				dumpLen += sprintf(dumpBuf+dumpLen, " conPc[%d]:%d, ",
////				l, req->u.addRemoteSsn.ssnList[j].conPc[l]);
////			}
////		}
//
//		if(debugFlag) {
//			dumpLen += sprintf(dumpBuf+dumpLen, " },");
//		}
//	}
//
//	if(debugFlag) {
//		logger.logINGwMsg(false, ALWAYS_FLAG, 0, "addRemoteSsn: %s", dumpBuf);
//	}
//}
//
//void copyDelRemoteSsn(Ss7SigtranSubsReq *req,  const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
//{
//	const RSIEmsTypes::DelRemoteSsn &ssn = cfgReq.union_type.delRmoteSsn();
//
//	req->u.delRemoteSsn.ssn 	 = ssn.ssn;
//	req->u.delRemoteSsn.nSapId = ssn.nSapId;
//	req->u.delRemoteSsn.dpc 	 = ssn.dpc;
//
//	if(debugFlag) {
//		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
//		"DelRemoteSsn: ssn:%d, nSapId:%d, dpc:%d",
//		req->u.delRemoteSsn.ssn, req->u.delRemoteSsn.nSapId,
//		req->u.delRemoteSsn.dpc);
//	}
//}
//
int copyAddPs(Ss7SigtranSubsReq *req, const RSIEmsTypes::AddPs &ps)
{
	int  dumpLen =0;
	char dumpBuf[4000];
	memset(dumpBuf, 0, 4000);

	emsConfigInfo* conf = getEmsConfigInfo(ps.nwkId);
	if(conf == NULL) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"Leaving AddPs, No Info for nwId:%d", ps.nwkId);
		return 0;
	}

	req->cmd_type 			= ADD_AS;
	req->u.addPs.psId 	= ps.psId;
	req->u.addPs.routCtx= ps.routCtx;
	req->u.addPs.nwkId 	= ps.nwkId;
	req->u.addPs.mode 	= ps.mode;
	req->u.addPs.loadShareMode = ps.loadShareMode;
	req->u.addPs.nmbActPspReqd = ps.nmbActPspReqd;
	req->u.addPs.nmbPsp = ps.nmbPsp;

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, 
			"Add Ps cmd_type:%d, psId:%d, routCtx:%d, nwId:%d, mode:%d, loadShareMode:%d,"
			"nmbActPspReqd:%d, nmbPsp:%d", req->cmd_type, req->u.addPs.psId, req->u.addPs.routCtx,
			req->u.addPs.nwkId, req->u.addPs.mode, req->u.addPs.loadShareMode ,
			req->u.addPs.nmbActPspReqd, req->u.addPs.nmbPsp);
	}

	for(int i=0; i< req->u.addPs.nmbPsp; ++i) {
		req->u.addPs.psp[i] = ps.psp[i];

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen,
				" psp[%d]:%d, ", i, req->u.addPs.psp[i]);
		}
	}

	U8 pcDet[3];
	req->u.addPs.lFlag 	= ps.lFlag;
	req->u.addPs.rtType = ps.rtType;
  
  if(conf->dpcLen == DPC16) {
	  req->u.addPs.dpcMask= 0xFFFF;
	  req->u.addPs.opcMask = 0xFFFF;
  }
  else if(conf->dpcLen == DPC14) {
	  req->u.addPs.dpcMask= 0x3FFF;
	  req->u.addPs.opcMask = 0x3FFF;
  }
  else if(conf->dpcLen == DPC24) {
	  req->u.addPs.dpcMask= 0xFFFFFF;
	  req->u.addPs.opcMask = 0xFFFFFF;
  }

	req->u.addPs.dpc 		=  g_convertPcToDec((char*)ps.dpc_char.in(), pcDet, 
																			conf->proto);
	req->u.addPs.opc = g_convertPcToDec((char*)ps.opc_char.in(), pcDet, 
																		 conf->proto);
	req->u.addPs.slsMask = ps.slsMask;
	req->u.addPs.sls 		 = ps.sls;
	req->u.addPs.sioMask = ps.sioMask;
	req->u.addPs.sio 		 = ps.sio;
	req->u.addPs.currentPsState = ps.currentPsState;

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen,
		" lFlag:%d, rtType:%d, dpcMask:%d, dpc:%s-%d, opcMask:%d, opc: %s-%d"
		", slsMask:%d, sls:%d, sioMask:%d, sio :%d, currentPsState:%d",
		req->u.addPs.lFlag, req->u.addPs.rtType, req->u.addPs.dpcMask,
		ps.dpc_char.in(), req->u.addPs.dpc, req->u.addPs.opcMask,
		ps.opc_char.in(), req->u.addPs.opc, req->u.addPs.slsMask,
		req->u.addPs.sls, req->u.addPs.sioMask, req->u.addPs.sio,
		req->u.addPs.currentPsState);

	}

	if(req->u.addPs.nmbPsp != ps.pspEpLst.length())
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"copyAddPs: ERROR nmbPSP and pspEpList Size is not Same");
	}

	for(int i=0; i < req->u.addPs.nmbPsp; ++i)
	{
		//req->u.addPs.pspEpLst[i].nmbEp = ps.pspEpLst[i].nmbEp;
		req->u.addPs.pspEpLst[i].nmbEp = ps.pspEpLst[i].endpIds.length();

		if(debugFlag) {
			dumpLen += sprintf(dumpBuf+dumpLen,
			" pspEpLst[%d] {nmbEp:%d, ", i, req->u.addPs.pspEpLst[i].nmbEp);
		}

		for(int j=0; j < req->u.addPs.pspEpLst[i].nmbEp; ++j)
		{
			req->u.addPs.pspEpLst[i].endpIds[j] = 
													ps.pspEpLst[i].endpIds[j];
			if(debugFlag) {
				dumpLen += sprintf(dumpBuf+dumpLen,
				"endpIds[%d]: %d, ", j, req->u.addPs.pspEpLst[i].endpIds[j]); 
			}
		}

		if(debugFlag) {
			 dumpLen += sprintf(dumpBuf+dumpLen, "}");
		}
	}

	if(debugFlag)
	{
		logger.logMsg(ALWAYS_FLAG, 0, "%s", dumpBuf);
	}

	return 1;
}

void copyDelPs(Ss7SigtranSubsReq *req,  
													const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelPs &ps = cfgReq.union_type.deletePs();

	req->cmd_type = DEL_AS;
	req->u.delPs.psId = ps.psId;
	req->u.delPs.nwkId = ps.nwkId;
  /** Rest of the parameters will be filled in processEmsReq() **/
	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"DelPs: cmd_type:%d, psId:%d, nwkId:%d", req->cmd_type, req->u.delPs.psId, req->u.delPs.nwkId );
	}
}

void copyPspStatus(Ss7SigtranSubsReq *req,  
														const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::PspStatus &psp = cfgReq.union_type.psp();

  req->cmd_type = STA_PSP;
	req->u.psp.pspId = psp.pspId;
	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"PspStatus: cmd_type:%d, pspId:%d", req->cmd_type, req->u.psp.pspId);
	}
}

void copyAddPsp(Ss7SigtranSubsReq *req, const RSIEmsTypes::AddPsp &psp) 
{
	int  dumpLen =0;
	char dumpBuf[2048];
	memset(dumpBuf, 0, 2048);

	req->cmd_type = ADD_ASP;
	req->u.addpsp.pspId 	= psp.pspId;
	req->u.addpsp.pspType = psp.pspType;
	req->u.addpsp.ipspMode= psp.ipspMode;
	req->u.addpsp.nmbAddr = psp.nmbAddr;

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, 
			"Add Asp: cmd_type:%d, pspId:%d, pspType:%d, ipspMode:%d, nmbAddr:%d, ",
			req->cmd_type, req->u.addpsp.pspId, req->u.addpsp.pspType,
			req->u.addpsp.ipspMode, req->u.addpsp.nmbAddr);
	}

	for(int i=0; i <req->u.addpsp.nmbAddr;++i) {
		string ipAddr = psp.addr[i].in();
		if(!ipAddr.empty()) {
			size_t found = ipAddr.find(":");
			if(found != string::npos) { // found
				req->u.addpsp.addr[i].type = CM_TPTADDR_IPV6;
				memcpy(&req->u.addpsp.addr[i].u.ipv6NetAddr, ipAddr.c_str(), 
							 ipAddr.size());

				if(debugFlag) {
					dumpLen += sprintf(dumpBuf+dumpLen, "IPV6[%d]: %s, ",
							i, ipAddr.c_str());
				}
			}
			else {
				req->u.addpsp.addr[i].type = CM_TPTADDR_IPV4;
				req->u.addpsp.addr[i].u.ipv4NetAddr = 
																		ntohl(inet_addr((S8*)ipAddr.c_str()));

				if(debugFlag) {
					dumpLen += sprintf(dumpBuf+dumpLen, "IPV4[%d]: %s, ",
							i, ipAddr.c_str());
				}
			}
		}
	}

	req->u.addpsp.dstPort 		= psp.dstPort;
	req->u.addpsp.nwkId 			= psp.nwkId;
	req->u.addpsp.includeRC 	= psp.includeRC;
	req->u.addpsp.cfgForAllLps= psp.cfgForAllLps;
	req->u.addpsp.currentPspState = psp.currentPspState;

	if(debugFlag) {
		dumpLen += sprintf(dumpBuf+dumpLen, 
		"dstPort:%d, nwkid:%d, includeRC:%d, cfgForAllLps:%d, currentPspState:%d",
		req->u.addpsp.dstPort, req->u.addpsp.nwkId, req->u.addpsp.includeRC,
		req->u.addpsp.cfgForAllLps, req->u.addpsp.currentPspState);

		logger.logMsg(ALWAYS_FLAG, 0, "%s", dumpBuf);
	}
}

void copyDelPsp(Ss7SigtranSubsReq *req,  
														const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DelPsp &psp = cfgReq.union_type.deletePsp();

	req->cmd_type = DEL_ASP;
	req->u.delPsp.pspId = psp.pspId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"DelPsp: cmd_type:%d, pspId:%d", req->cmd_type, req->u.delPsp.pspId);
	}
}

void copyPsStatus(Ss7SigtranSubsReq *req,  
																const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::PsStatus &ps  = cfgReq.union_type.ps();

  req->cmd_type = STA_PS;
	req->u.ps.psId = ps.psId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"PsStatus: cmd_type:%d, psId:%d", req->cmd_type, req->u.ps.psId);
	}
}

void copyDisableUserPart(Ss7SigtranSubsReq *req,  
												const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::DisableUserPart &usr = cfgReq.union_type.disableUsrPart();

	//req->cmd_type = DEL_USERPART;
	req->u.disableUserPart.mtp3UsapId = usr.mtp3UsapId;
	req->u.disableUserPart.m3uaUsapId = usr.m3uaUsapId;
	req->u.disableUserPart.sccpLsapId = usr.sccpLsapId;
	req->u.disableUserPart.nwkType 		= usr.nwkType;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"DisableUserPart: cmd_type:%d, mtp3UsapId:%d, m3uaUsapId:%d, "
		"sccpLsapId:%d, nwkType:%d",
		 req->cmd_type, req->u.disableUserPart.mtp3UsapId, 
		req->u.disableUserPart.m3uaUsapId, req->u.disableUserPart.sccpLsapId,
		req->u.disableUserPart.nwkType);
	}
}

void copyEnableUserPart(Ss7SigtranSubsReq *req,  
															const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::EnableUserPart &usr = cfgReq.union_type.enableUsrPart();

  req->cmd_type = ENABLE_USERPART;
	req->u.enableUserPart.mtp3UsapId = usr.mtp3UsapId;
	req->u.enableUserPart.m3uaUsapId = usr.m3uaUsapId;
	req->u.enableUserPart.sccpLsapId = usr.sccpLsapId;
	req->u.enableUserPart.nwkType 	 = usr.nwkType;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"EnableUserPart:  cmd_type:%d, mtp3UsapId:%d, m3uaUsapId:%d, sccpLsapId:%d, nwkType:%d",
		req->cmd_type, req->u.enableUserPart.mtp3UsapId, 
		req->u.enableUserPart.m3uaUsapId, req->u.enableUserPart.sccpLsapId,
		req->u.enableUserPart.nwkType);
	}
}

void copyM3uaAssocUp(Ss7SigtranSubsReq *req,  
														const RSIEmsTypes::M3uaAssocUp &m3ua)
{
	req->cmd_type = ASSOC_UP;
	req->u.m3uaAssocUp.assocId 		= m3ua.assocId;
	req->u.m3uaAssocUp.pspId 		  = m3ua.pspId;
	req->u.m3uaAssocUp.endPointId = m3ua.endPointId;
	req->u.m3uaAssocUp.m3uaLsapId = m3ua.m3uaLsapId;
	req->u.m3uaAssocUp.currentAssocState = m3ua.currentAssocState;
	req->u.m3uaAssocUp.isRetry = 0;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"M3uaAssocUp: cmd_type:%d, assocId:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d, currentAssocState:%d",
		req->cmd_type, req->u.m3uaAssocUp.assocId, req->u.m3uaAssocUp.pspId, req->u.m3uaAssocUp.endPointId,
		req->u.m3uaAssocUp.m3uaLsapId, req->u.m3uaAssocUp.currentAssocState);
	}
}

void copyM3uaAssocDown(Ss7SigtranSubsReq *req,  
															const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::M3uaAssocDown &m3ua = cfgReq.union_type.m3uaAssociationDown();

	req->cmd_type = ASSOC_DOWN;
	req->u.m3uaAssocDown.assocId 		= m3ua.assocId;
	req->u.m3uaAssocDown.pspId 		  = m3ua.pspId;
	req->u.m3uaAssocDown.endPointId = m3ua.endPointId;
	req->u.m3uaAssocDown.m3uaLsapId = m3ua.m3uaLsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"M3uaAssocDown: cmd_type:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d",
		req->cmd_type, req->u.m3uaAssocDown.pspId, req->u.m3uaAssocDown.endPointId,
		req->u.m3uaAssocDown.m3uaLsapId);
	}
}

void copyM3uaAspUp(Ss7SigtranSubsReq *req,  
													const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::M3uaAspUp &m3ua = cfgReq.union_type.m3uaAsp_Up();

	req->cmd_type = ASP_UP;
	req->u.m3uaAspUp.pspId 		  = m3ua.pspId;
	req->u.m3uaAspUp.endPointId = m3ua.endPointId;
	req->u.m3uaAspUp.m3uaLsapId = m3ua.m3uaLsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"m3uaAspUp: cmd_type:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d",
		req->cmd_type, req->u.m3uaAspUp.pspId, req->u.m3uaAspUp.endPointId,
		req->u.m3uaAspUp.m3uaLsapId);
	}
}

void copyM3uaAspDown(Ss7SigtranSubsReq *req,  
															const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::M3uaAspDown &m3ua = cfgReq.union_type.m3uaAsp_Down();

	req->cmd_type = ASP_DOWN;
	req->u.m3uaAssocDown.pspId 		  = m3ua.pspId;
	req->u.m3uaAssocDown.endPointId = m3ua.endPointId;
	req->u.m3uaAssocDown.m3uaLsapId = m3ua.m3uaLsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"m3uaAssocDown: cmd_type:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d",
		req->cmd_type, req->u.m3uaAssocDown.pspId, req->u.m3uaAssocDown.endPointId,
		req->u.m3uaAssocDown.m3uaLsapId);
	}
}

void copyM3uaAspAct(Ss7SigtranSubsReq *req,  
																const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::M3uaAspAct &m3ua = cfgReq.union_type.m3uaAspActive();

	req->cmd_type = ASP_ACTIVE;
	req->u.m3uaAspAct.psId 		   = m3ua.psId;
	req->u.m3uaAspAct.pspId 		 = m3ua.pspId;
	req->u.m3uaAspAct.endPointId = m3ua.endPointId;
	req->u.m3uaAspAct.m3uaLsapId = m3ua.m3uaLsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"m3uaAspAct: cmd_type:%d, psId:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d",
		req->cmd_type, req->u.m3uaAspAct.psId, req->u.m3uaAspAct.pspId,
		req->u.m3uaAspAct.endPointId, req->u.m3uaAspAct.m3uaLsapId);
	}
}

void copyM3uaAspInAct(Ss7SigtranSubsReq *req,  
																const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq)
{
	const RSIEmsTypes::M3uaAspInAct &m3ua = cfgReq.union_type.m3uaAspInactive();

	req->cmd_type = ASP_INACTIVE;
	req->u.m3uaAspInact.psId 		   = m3ua.psId;
	req->u.m3uaAspInact.pspId 		 = m3ua.pspId;
	req->u.m3uaAspInact.endPointId = m3ua.endPointId;
	req->u.m3uaAspInact.m3uaLsapId = m3ua.m3uaLsapId;

	if(debugFlag) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"m3uaAspInact: cmd_type:%d, psId:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d",
		req->cmd_type, req->u.m3uaAspInact.psId, req->u.m3uaAspInact.pspId,
		req->u.m3uaAspInact.endPointId, req->u.m3uaAspInact.m3uaLsapId);
	}
}

/*
* Callback for SS7 messages configured through BayConsole. 
* Convert it to format being handled by SmWrapper. 
*/
bool
ss7ConfigurationClbk(void* apContext, 
										const RSIEmsTypes::Ss7SigtranSubsReq &cfgReq,
										RSIEmsTypes::Ss7SigtranStackRespList_out respList) 
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, 
         "ss7ConfigurationClbk() : IN CmdType<%d>", cfgReq.union_type._d());

  int retVal = 1;
	respList = new RSIEmsTypes::Ss7SigtranStackRespList();
	respList->length(1); // allocate length of list
	unsigned int i =0;
	unsigned short nwId = 0;
	string cause ="Invalid Input";

	// return if my role is standby
	if(TCAP_SECONDARY == INGwTcapProvider::getInstance().myRole() ||
		configLoopBack) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
			"+VER+ ss7ConfigurationClbk(), Leaving as my role is Standby,loopback:%d",
			configLoopBack);

		respList[i].procid = 
				CORBA::UShort(INGwIfrPrParamRepository::getInstance().getSelfId());
		respList[i].status = (configLoopBack==true)?0:1; // 0 = OK, 1= NOK, 2= degraded
		respList[i].reasonCode = 1111;
		respList[i].reason = CORBA::string_dup("Standby Role");
		return true;
	}

	INGwSmWrapper *pSmWrapper= INGwTcapProvider::getInstance().getSmWrapperPtr();

	Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;

	req->procId   = cfgReq.procId;
	req->cmd_type = cfgReq.union_type._d();

	switch(cfgReq.union_type._d())
	{
		case RSIEmsTypes::ADDNETWORK_SS7     : 
		{
			nwId = cfgReq.nwId;
	    const RSIEmsTypes::AddNetwork addNw = cfgReq.union_type.addNwk();
			cause = copyAddNetwork(req, addNw);
			if(-1 == cause.find("Success"))
				req = NULL;
		}
		break;
		case RSIEmsTypes::DELNETWORK_SS7     : 
		{
			nwId = cfgReq.nwId;
			copyDelNetwork(req, cfgReq); 
		}
		break;
    case RSIEmsTypes::ADDLINK_SS7        :
    {
	    const RSIEmsTypes::AddLink &addLink = cfgReq.union_type.lnk();
      copyAddLink(req, addLink); 
    }
		break;

    case RSIEmsTypes::MODLINK_SS7        :  
    {
	    const RSIEmsTypes::AddLink &addLink = cfgReq.union_type.lnk();
      copyModLink(req, addLink); 
    }
    break;
    case RSIEmsTypes::MODLINKSET_SS7     :  
    {
	    const RSIEmsTypes::AddLinkSet &addLinkSet = cfgReq.union_type.lnkSet();
      copyModLinkSet(req, addLinkSet); 
    }
    break;
		case RSIEmsTypes::DELLINK_SS7        :  copyDelLink(req, cfgReq); break;

		case RSIEmsTypes::LINKSTATUS_SS7     : copyLinkStatus(req, cfgReq); break;
    case RSIEmsTypes::LINKENABLE_SS7     : copyEnableLink(req, cfgReq); break;
    case RSIEmsTypes::LINKDISABLE_SS7    : copyDisableLink(req,cfgReq); break;

		case RSIEmsTypes::ADDLINKSET_SS7     : 
    {
	    const RSIEmsTypes::AddLinkSet &addLnkset = cfgReq.union_type.lnkSet();
      copyAddLinkset(req, addLnkset); 
    }
    break;
		case RSIEmsTypes::DELLINKSET_SS7     : copyDelLinkset(req, cfgReq); break;
		case RSIEmsTypes::LINKSETSTATUS_SS7  : copyLinksetStatus(req, cfgReq); break;

		case RSIEmsTypes::ADDROUTE_SS7       :
    {
	    const RSIEmsTypes::AddRoute &addRoute = cfgReq.union_type.addRute();
      retVal = copyAddRoute(req, addRoute);
      if (retVal != 1) {
        //delete req;
        req = NULL;
      }
    }
    break;
		case RSIEmsTypes::DELROUTE_SS7       : copyDelRoute(req, cfgReq); break;
		case RSIEmsTypes::ROUTESTATUS_SS7    : copyRouteStatus(req, cfgReq); break;

		case RSIEmsTypes::NODESTATUS_SS7     : copyNodeStatus(req, cfgReq); break;

		case RSIEmsTypes::ADDUSERPART_SS7    : 
    {
	    const RSIEmsTypes::AddUserPart &addUsrPart = 
																						cfgReq.union_type.addUsrPart();
      copyAddUserPart(req, addUsrPart); 
    }
    break;
		case RSIEmsTypes::DELUSERPART_SS7    : copyDelUserPart(req, cfgReq); break;

		case RSIEmsTypes::ADDGTRULE_SS7      :
    {
	    const RSIEmsTypes::AddGtRule &addRule = cfgReq.union_type.addGtRle();
      copyAddRule(req, addRule);
    }
    break;
		case RSIEmsTypes::DELGTRULE_SS7      : copyDelRule(req, cfgReq); break;

		case RSIEmsTypes::ADDGTADDRMAP_SS7   : 
    {
	    const RSIEmsTypes::AddAddrMapCfg &addAddrMap = 
		  											cfgReq.union_type.addAddrMapConfig();
      copyAddGtAddrMap(req, addAddrMap); 
    }
    break;
		case RSIEmsTypes::DELGTADDRMAP_SS7   : copyDelGtAddrMap(req, cfgReq); break;

		case RSIEmsTypes::ADDREMOTESSN_SS7 	 : 
		case RSIEmsTypes::ADDLOCALSSN_SS7 	 :
    { 
	    const RSIEmsTypes::AddSsn &ssn = cfgReq.union_type.ssn();
      cause = copyAddLocalSsn(req, ssn);
    }
    break;

		case RSIEmsTypes::DELREMOTESSN_SS7   : 
		case RSIEmsTypes::DELLOCALSSN_SS7 	 : copyDelLocalSsn(req, cfgReq); break;

		case RSIEmsTypes::LOCALSSNSTATUS_SS7 : copyLocalSsnStatus(req, cfgReq); break;

		case RSIEmsTypes::ADDENDPOINT_SS7 	 : 
    {
	    const RSIEmsTypes::AddEndPoint &ep = cfgReq.union_type.addep();
      copyAddEndPoint(req, ep);
    }
    break;
		case RSIEmsTypes::DELENDPOINT_SS7 	 : copyDelEndPoint(req, cfgReq); break;
		case RSIEmsTypes::DISABLEENDPOINT_SS7: copyDisableEndPoint(req, cfgReq); break;
		case RSIEmsTypes::ENABLEENDPOINT_SS7 : copyEnableEndPoint(req, cfgReq); break;

		//case RSIEmsTypes::SSNENABLE_SS7      : copyEnableSsn(req, cfgReq); break;
		//case RSIEmsTypes::SSNDISABLE_SS7 		 : copyDisableSsn(req, cfgReq); break;

		//case RSIEmsTypes::ADDREMOTESSN_SS7   : copyAddRemoteSsn(req, cfgReq); break;
		//case RSIEmsTypes::DELREMOTESSN_SS7   : copyDelRemoteSsn(req, cfgReq); break;

    case RSIEmsTypes::ADDPS_SS7 				 :
    { 
	    const RSIEmsTypes::AddPs &ps = cfgReq.union_type.add_Ps();
      copyAddPs(req, ps); 
    }
    break;
		case RSIEmsTypes::DELPS_SS7 				 : copyDelPs(req, cfgReq); break;

		case RSIEmsTypes::PSPSTATUS_SS7 		 : copyPspStatus(req, cfgReq); break;
		case RSIEmsTypes::ADDPSP_SS7 			   :
    { 
	    const RSIEmsTypes::AddPsp &psp = cfgReq.union_type.add_psp();
      copyAddPsp(req, psp);
    }
    break;
		case RSIEmsTypes::DELPSP_SS7 				 : copyDelPsp(req, cfgReq); break;

		case RSIEmsTypes::PSSTATUS_SS7 			 : copyPsStatus(req, cfgReq); break;

		case RSIEmsTypes::DISABLEUSERPART_SS7: copyDisableUserPart (req, cfgReq); break;
		case RSIEmsTypes::ENABLEUSERPART_SS7 : copyEnableUserPart  (req, cfgReq); break;

		case RSIEmsTypes::M3UAASSOCUP_SS7    : 
		case RSIEmsTypes::M3UAASSOCADD_SS7    : 
		{
			const RSIEmsTypes::M3uaAssocUp &assoc = 
														cfgReq.union_type.m3uaAssociationUp();
			copyM3uaAssocUp(req, assoc); 
			break;
		}
		case RSIEmsTypes::M3UAASSOCDOWN_SS7  : copyM3uaAssocDown(req, cfgReq); break;
		case RSIEmsTypes::M3UAASPUP_SS7 		 : copyM3uaAspUp(req, cfgReq); break;
		case RSIEmsTypes::M3UAASPDOWN_SS7 	 : copyM3uaAspDown(req, cfgReq); break;
		case RSIEmsTypes::M3UAASPACT_SS7 	   : copyM3uaAspAct(req, cfgReq); break;
		case RSIEmsTypes::M3UAASPINACT_SS7   : copyM3uaAspInAct(req, cfgReq); break;


		//case RSIEmsTypes::STAT_SS7 :Stat status;
		//case RSIEmsTypes::ALARMENABLEDISABLE_SS7 :AlarmEnableDisable alarm;
		//case RSIEmsTypes::DEBUGENABLEDISABLE_SS7 :DebugEnableDisable debug;
		//case RSIEmsTypes::TRACEENABLEDISABLE_SS7 :TraceEnableDisable trace;

    default:
	    logger.logINGwMsg(false, ERROR_FLAG, 0, 
             "ss7ConfigurationClbk()::INVALID COMMAND TYPE <%d>",
             req->cmd_type);
  
	}

	if(req == NULL) {
		respList[i].procid = 
				CORBA::UShort(INGwIfrPrParamRepository::getInstance().getSelfId());
		respList[i].status = 1; // 0 = OK, 1= NOK, 2= degraded
		respList[i].reasonCode = 1000;
		respList[i].reason = CORBA::string_dup(cause.c_str());
  	LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::ss7ConfigurationClbk()");
		return true;
	}

	Ss7SigtranStackResp *stackResp = NULL;
	pSmWrapper->processEmsReq(req, stackResp, 0);

	if(stackResp != NULL) {
		respList[i].procid = 
		CORBA::UShort(INGwIfrPrParamRepository::getInstance().getSelfId());
		respList[i].status = stackResp->status; // 0 = OK, 1= NOK, 2= degraded
		respList[i].reasonCode = stackResp->reason;

		if(stackResp->status == 1) // Error scenario
		{
			// Need to remove entry incase configuration was for 
			// add nw
			if(cfgReq.union_type._d() == RSIEmsTypes::ADDNETWORK_SS7)
				removeEmsConfigEntry(nwId);

			if(stackResp->reasonStr)
				respList[i].reason = CORBA::string_dup(stackResp->reasonStr); 
			else
				respList[i].reason = CORBA::string_dup("Operation Failed: Unknown Reason");
		}
		else if(stackResp->status == 0)
    {
        if( (cfgReq.union_type._d() == RSIEmsTypes::LINKSTATUS_SS7) || 
             (cfgReq.union_type._d() == RSIEmsTypes::LINKSETSTATUS_SS7) ||
             (cfgReq.union_type._d() == RSIEmsTypes::ROUTESTATUS_SS7) ||
             (cfgReq.union_type._d() == RSIEmsTypes::PSPSTATUS_SS7) ||
             (cfgReq.union_type._d() == RSIEmsTypes::PSSTATUS_SS7) )  {
				  respList[i].reason = CORBA::string_dup(stackResp->reasonStr);
          if (cfgReq.union_type._d() == RSIEmsTypes::PSPSTATUS_SS7)
          {
            if(stackResp->reasonStr != NULL)
            {
              delete [] stackResp->reasonStr; // Fix for FMM
              stackResp->reasonStr = NULL;
            }
          }
        } 
        else
				  respList[i].reason = CORBA::string_dup("Operation Successful");
    }
		delete stackResp;
	}
	else {
		respList[i].procid = 
		CORBA::UShort(INGwIfrPrParamRepository::getInstance().getSelfId());
		respList[i].status = 0; // 0 = OK, 1= NOK, 2= degraded
		respList[i].reasonCode = 1000;
	}

  if(req)
    delete req;

  LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::ss7ConfigurationClbk()");
	return true;
}
