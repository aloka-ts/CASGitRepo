#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

#include <signal.h>

#include <INGwStackManager/INGwSmBlkConfig.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwFtPacket/INGwFtPktMsgDefine.h>
#include <INGwFtPacket/INGwFtPktAddEp.h>
#include <INGwFtPacket/INGwFtPktAddLink.h>
#include <INGwFtPacket/INGwFtPktAddLinkset.h>
#include <INGwFtPacket/INGwFtPktAddNw.h>
#include <INGwFtPacket/INGwFtPktAddPs.h>
#include <INGwFtPacket/INGwFtPktAspActive.h>
#include <INGwFtPacket/INGwFtPktAspInActive.h>
#include <INGwFtPacket/INGwFtPktAddPsp.h>
#include <INGwFtPacket/INGwFtPktAddRoute.h>
#include <INGwFtPacket/INGwFtPktAddSsn.h>
#include <INGwFtPacket/INGwFtPktAddUserPart.h>
#include <INGwFtPacket/INGwFtPktDelEp.h>
#include <INGwFtPacket/INGwFtPktDelLink.h>
#include <INGwFtPacket/INGwFtPktDelLinkset.h>
#include <INGwFtPacket/INGwFtPktDelNw.h>
#include <INGwFtPacket/INGwFtPktDelPs.h>
#include <INGwFtPacket/INGwFtPktDelPsp.h>
#include <INGwFtPacket/INGwFtPktDelRoute.h>
#include <INGwFtPacket/INGwFtPktDelSsn.h>
#include <INGwFtPacket/INGwFtPktDelUserPart.h>
#include <INGwFtPacket/INGwFtPktM3uaAssocDown.h>
#include <INGwFtPacket/INGwFtPktM3uaAssocUp.h>
#include <INGwFtPacket/INGwFtPktAddGtRule.h>
#include <INGwFtPacket/INGwFtPktAddGtAddrMap.h>
#include <INGwFtPacket/INGwFtPktDelGtRule.h>
#include <INGwFtPacket/INGwFtPktDelGtAddrMap.h>
#include <INGwFtPacket/INGwFtPktModLink.h>
#include <INGwFtPacket/INGwFtPktModLinkset.h>
#include <INGwFtPacket/INGwFtPktModPs.h>
#include <INGwFtPacket/INGwFtPktStkConfigStatus.h>

#include <INGwTcapProvider/INGwTcapMsgLogger.h>

#define FROM_EMS 0

INGwSmBlkConfig* INGwSmBlkConfig::m_selfPtr = NULL;

extern "C"
void*
launchStackConfig(void *arg)
{
  LogINGwTrace(false, 0,"IN launchStackConfig");
  INGwSmBlkConfig* blk =  static_cast<INGwSmBlkConfig*> (arg);

  blk->execute();

  LogINGwTrace(false, 0,"OUT launchStackConfig");
  return 0;
}


INGwSmBlkConfig&
INGwSmBlkConfig::getInstance()
{
  if (NULL == m_selfPtr) {
    m_selfPtr = new INGwSmBlkConfig();
  }
  return *(INGwSmBlkConfig::m_selfPtr);
}

INGwSmBlkConfig::INGwSmBlkConfig():m_relayStatus(false),
					 m_relayStatCheckCnt(6), m_relayRetryTime(10),
					 m_peerEnabled(false), m_isSctpTuclEnabled(false),
					 m_toggleFlag(true)
{
	pthread_mutex_init(&m_lock, NULL);
	pthread_mutex_init(&m_Qlock, NULL);

	m_selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
	m_peerId = INGwIfrPrParamRepository::getInstance().getPeerId();

	m_selfProcId = INGwTcapProvider::getInstance().getProcIdForSubsysId(m_selfId);
  if (m_peerId) {
	  m_peerProcId = 
          INGwTcapProvider::getInstance().getProcIdForSubsysId(m_peerId);
  }
  else {
	  m_peerProcId = -1;
  }
  logger.logMsg (ALWAYS_FLAG, 0, "INGwSmBlkConfig(): "
         "m_selfState OldVal<%d> NewVal UNINITIALIZED, "
         "m_peerState OldVal<%d> NewVal UNINITIALIZED",
         m_selfState, m_peerState);

	m_selfState = INGwSmBlkConfig::UNINITIALIZED;
	m_peerState = INGwSmBlkConfig::UNINITIALIZED;

	m_mode = (m_peerId != 0)?true:false;
	m_relayChannelRole = 1;

	if(m_mode) {
		if(m_selfId > m_peerId)
			m_relayChannelRole = 2;
	}

	logger.logMsg (ALWAYS_FLAG, 0, 
	"INGwSmBlkConfig::++VER++ C'tor m_selfId[%d] m_selfProcId[%d] m_peerId[%d]"
	" m_peerProcId[%d] relayStatus[%d] INCMode[%s] RelayChannelRole[%d]" , 
	m_selfId, m_selfProcId, m_peerId, m_peerProcId, m_relayStatus,
	(m_mode == true)?"FT":"Non-FT", m_relayChannelRole);
}

void
INGwSmBlkConfig::initialize()
{
	pthread_t thdId;
 	if (0 != pthread_create(&thdId, 0, launchStackConfig,
					&INGwSmBlkConfig::getInstance())) 
	{
   	logger.logINGwMsg(false, ERROR_FLAG, 0,
   	"Thread creation failed. launchStackConfig");
   	LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::C'tor()");
	}
}

INGwSmBlkConfig::~INGwSmBlkConfig()
{
	pthread_mutex_destroy(&m_lock);
	pthread_mutex_destroy(&m_Qlock);
}

bool
INGwSmBlkConfig::getMode()
{
	return m_mode; // FT=true or Non-FT=false
}

int 
INGwSmBlkConfig::getRelayChannelRole()
{
		return m_relayChannelRole;
}

// Will Add if node doesn't exist on addition
// Delete is already exist on deletion
int 
INGwSmBlkConfig::updateNode(Ss7SigtranSubsReq *req, int from)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateNode, "
	"nodeType[%d]", req->cmd_type);

	int retVal =1;

	// Assumption: Duplication shall be taken care by EMS
	switch(req->cmd_type) 
	{
		case ADD_NETWORK:
		{
			AddNetwork addNw;
			memcpy(&addNw, &(req->u.addNwk), sizeof(AddNetwork));
			m_nwList.push_back(addNw);

			if(from == FROM_EMS)
			{
				INGwFtPktAddNw ftAddNw;
				ftAddNw.initialize(m_selfId, m_peerId, addNw);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddNw);
			}
		}
		break;

		case ADD_LINKSET:
		{
			AddLinkSet addLnkSet;
			memcpy(&addLnkSet, &(req->u.lnkSet), sizeof(AddLinkSet));
			m_linksetList.push_back(addLnkSet);

			if(from == FROM_EMS)
			{
				INGwFtPktAddLinkset ftAddLs;
				ftAddLs.initialize(m_selfId, m_peerId, addLnkSet);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddLs);
			}
		}
		break;

		case ADD_LINK:
		{
			AddLink addLnk;
			memcpy(&addLnk, &(req->u.lnk), sizeof(AddLink));
			m_linkList.push_back(addLnk);

			if(req->u.lnk.mtp2ProcId == m_peerProcId)
				m_peerLinkList.push_back(addLnk);

			if(from == FROM_EMS)
			{
				INGwFtPktAddLink ftAddLnk;
				ftAddLnk.initialize(m_selfId, m_peerId, addLnk);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddLnk);
			}
		}
		break;

		case ADD_USERPART:
		{
			AddUserPart up;
			memcpy(&up, &(req->u.addUserPart), sizeof(AddUserPart));
			m_userPartList.push_back(up);
			if(from == FROM_EMS)
			{
				INGwFtPktAddUserPart ftAddUp;
				ftAddUp.initialize(m_selfId, m_peerId, up);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddUp);
			}
		}
		break;

		case ADD_GTRULE:
		{
			AddGtRule gt;
			memcpy(&gt, &(req->u.addGtRule), sizeof(AddGtRule));
			m_gtRuleList.push_back(gt);
			if(from == FROM_EMS)
			{
				INGwFtPktAddGtRule ftAddGt;
				ftAddGt.initialize(m_selfId, m_peerId, gt);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddGt);
			}
		}
		break;

		case ADD_GTADDRMAP:
		{
			AddAddrMapCfg addrCfg;
			memcpy(&addrCfg, &(req->u.addAddrMapCfg), sizeof(AddAddrMapCfg));
			m_gtAddrMapSeq.push_back(addrCfg);
			if(from == FROM_EMS)
			{
				INGwFtPktAddGtAddrMap ftAddGt;
				ftAddGt.initialize(m_selfId, m_peerId, addrCfg);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddGt);
			}
		}
		break;

		case ADD_ROUTE:
		{
			AddRoute rte;
			memcpy(&rte, &(req->u.addRoute), sizeof(AddRoute));
			m_routeList.push_back(rte);
			if(from == FROM_EMS)
			{
				INGwFtPktAddRoute ftAddRte;
				ftAddRte.initialize(m_selfId, m_peerId, rte);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddRte);
			}
		}
		break;

		case ADD_LOCAL_SSN:
		{
			AddLocalSsn ssn;
			memcpy(&ssn, &(req->u.addLocalSsn), sizeof(AddLocalSsn));
			m_localSsnList.push_back(ssn);

			// Need to update SSN map updated with SpId
			// without this no registeration would happen
			bool validSsn = INGwTcapProvider::getInstance().updateSsnInfo
				(req->u.addLocalSsn.ssn, req->u.addLocalSsn.tcapUsapId);

			if(validSsn == false)
			{
				logger.logMsg(ERROR_FLAG, 0, 
				"Should Never Happen: SSN is not configured as default configuration");
			}
			if(from == FROM_EMS)
			{
				INGwFtPktAddSsn ftAddSsn;
				ftAddSsn.initialize(m_selfId, m_peerId, ssn);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddSsn);
			}
		}
		break;

		case ADD_AS:
		{
			AddPs ps;
			memcpy(&ps, &(req->u.addPs), sizeof(AddPs));
			m_asList.push_back(ps);
			if(from == FROM_EMS)
			{
				INGwFtPktAddPs ftAddPs;
				ftAddPs.initialize(m_selfId, m_peerId, ps);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddPs);
			}
		}
		break;

		case ADD_ASP:
		{
			AddPsp psp;
			memcpy(&psp, &(req->u.addpsp), sizeof(AddPsp));
			m_aspList.push_back(psp);
			if(from == FROM_EMS)
			{
				INGwFtPktAddPsp ftAddPsp;
				ftAddPsp.initialize(m_selfId, m_peerId, psp);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddPsp);
			}
		}
		break;

		case ADD_ENDPOINT:
		{
			AddEndPoint ep;
			memcpy(&ep, &(req->u.addEp), sizeof(AddEndPoint));
      addEp(ep);
			//m_epList.push_back(ep);
      
			if(from == FROM_EMS)
			{
				INGwFtPktAddEp ftAddEp;
				ftAddEp.initialize(m_selfId, m_peerId, ep);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAddEp);
			}
		}
		break;

		case DEL_NETWORK:
			delNetwork(req->u.delNwk);
			if(from == FROM_EMS)
			{
				INGwFtPktDelNw ftDelNw;
				ftDelNw.initialize(m_selfId, m_peerId, req->u.delNwk);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelNw);
			}
			break;

		case DEL_LINKSET:
			delLinkset(req->u.delLnkSet);
			if(from == FROM_EMS)
			{
				INGwFtPktDelLinkset ftDelLs;
				ftDelLs.initialize(m_selfId, m_peerId, req->u.delLnkSet);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelLs);
			}
			break;

		case DEL_LINK:
			delLink(req->u.delLnk);
			if(from == FROM_EMS)
			{
				INGwFtPktDelLink ftDelLnk;
				ftDelLnk.initialize(m_selfId, m_peerId, req->u.delLnk);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelLnk);
			}
			break;

		case DEL_USR_PART:
			delUserPart(req->u.delUserPart);
			if(from == FROM_EMS)
			{
				INGwFtPktDelUserPart ftDelUp;
				ftDelUp.initialize(m_selfId, m_peerId, req->u.delUserPart);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelUp);
			}
			break;

		case DEL_GTRULE:
			delGtRule(req->u.delGtRule);
			if(from == FROM_EMS)
			{
				INGwFtPktDelGtRule ftDelGt;
				ftDelGt.initialize(m_selfId, m_peerId, req->u.delGtRule);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelGt);
			}
			break;

		case DEL_GTADDRMAP:
			delGtAddrMap(req->u.delAddrMapCfg);
			if(from == FROM_EMS)
			{
				INGwFtPktDelGtAddrMap ftDelGt;
				ftDelGt.initialize(m_selfId, m_peerId, req->u.delAddrMapCfg);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelGt);
			}
			break;

		case DEL_ROUTE:
			delRoute(req->u.delRoute);
			if(from == FROM_EMS)
			{
				INGwFtPktDelRoute ftDelRte;
				ftDelRte.initialize(m_selfId, m_peerId, req->u.delRoute);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelRte);
			}
			break;

		case DEL_LOCAL_SSN:
			delLocalSsn(req->u.delLocalSsn);
			if(from == FROM_EMS)
			{
				INGwFtPktDelSsn ftDelSsn;
				ftDelSsn.initialize(m_selfId, m_peerId, req->u.delLocalSsn);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelSsn);
			}
			break;

		case DEL_AS:
			delAs(req->u.delPs);
			if(from == FROM_EMS)
			{
				INGwFtPktDelPs ftDelPs;
				ftDelPs.initialize(m_selfId, m_peerId, req->u.delPs);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelPs);
			}
			break;

		case DEL_ASP:
			delAsp(req->u.delPsp);
			if(from == FROM_EMS)
			{
				INGwFtPktDelPsp ftDelPsp;
				ftDelPsp.initialize(m_selfId, m_peerId, req->u.delPsp);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelPsp);
			}
			break;

		case DEL_ENDPOINT:
			delEp(req->u.delEp);	
			if(from == FROM_EMS)
			{
				INGwFtPktDelEp ftDelEp;
				ftDelEp.initialize(m_selfId, m_peerId, req->u.delEp);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftDelEp);
			}
			break;

		case ASSOC_UP:
      addM3uaAssocUp(req->u.m3uaAssocUp);
			if(from == FROM_EMS)
			{
				INGwFtPktM3uaAssocUp ftAssocUp;
				ftAssocUp.initialize(m_selfId, m_peerId, req->u.m3uaAssocUp);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAssocUp);
			}
			break;

		case ASSOC_DOWN:
      delM3uaAssocUp(req->u.m3uaAssocDown);
			if(from == FROM_EMS)
			{
				INGwFtPktM3uaAssocDown ftAssocDn;
				ftAssocDn.initialize(m_selfId, m_peerId, req->u.m3uaAssocDown);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAssocDn);
			}
			break;

		case MODIFY_LINK:
			AddLink addLnk;
			memcpy(&addLnk, &(req->u.lnk), sizeof(AddLink));
			modLink(addLnk);

			if(from == FROM_EMS)
			{
				INGwFtPktModLink ftModLnk;
				ftModLnk.initialize(m_selfId, m_peerId, addLnk);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftModLnk);
			}
			break;

		case MODIFY_LINKSET:
			AddLinkSet addLnkSet;
			memcpy(&addLnkSet, &(req->u.lnkSet), sizeof(AddLinkSet));
			modLinkset(addLnkSet);

			if(from == FROM_EMS)
			{
				INGwFtPktModLinkset ftModLs;
				ftModLs.initialize(m_selfId, m_peerId, addLnkSet);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftModLs);
			}
			break;

		case MODIFY_AS:
		{
			AddPs ps;
			memcpy(&ps, &(req->u.addPs), sizeof(AddPs));
			modAs(ps);

			if(from == FROM_EMS)
			{
				INGwFtPktModPs ftModPs;
				ftModPs.initialize(m_selfId, m_peerId, ps);
				INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftModPs);
			}
		}
		break;

		default:
			retVal = 0;
			logger.logMsg (ERROR_FLAG, 0, 
			"OUT INGwSmBlkConfig::delNode Invalid node[%d]", req->cmd_type);
	}
	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::updateNode, retVal[%d]",
	retVal);
	return retVal;
}

// Will Add if node doesn't exist on addition Delete is already exist on 
// deletion.This method is called from Wrapper when any new addition/deletion
// request is received from Peer INC. 
int 
INGwSmBlkConfig::updateNode(INGwFtPktMsg *msg)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateNode, "
	"msgType[%d]", msg->getMsgType());

	int retVal =1;

	// Assumption: Duplication shall be taken care by EMS
	switch(msg->getMsgType()) 
	{
		case MSG_ADD_NW:
		{
			AddNetwork addNw;
			INGwFtPktAddNw *nw = dynamic_cast<INGwFtPktAddNw*>(msg);
			nw->getNwData(addNw);

			m_nwList.push_back(addNw);
		}
		break;

    case MSG_ASP_ACTV:
    {
      M3uaAspAct aspList;
      INGwFtPktAspActive *asp = dynamic_cast<INGwFtPktAspActive*>(msg);
      asp->getPspData(aspList);

      m_actAspList.push_back(aspList);

      EpSeq::iterator it;
      for(it=m_epList.begin(); it != m_epList.end(); ++it) {
        if (((*it).m3uaLsapId == aspList.m3uaLsapId) &&
            ((*it).sctpProcId) == m_peerProcId) {

       m_peerActAspList.push_back(aspList);
       logger.logMsg (ALWAYS_FLAG, 0, "addActvAsp, peerActvAspList size[%d]",
             m_peerActAspList.size());
        }
      }
     }
     break;

    case MSG_ASP_INACTV:
    {
      M3uaAspInAct aspList;
      INGwFtPktAspInActive *asp = dynamic_cast<INGwFtPktAspInActive*>(msg);
      asp->getPspData(aspList);

      delActvAsp(aspList);
    }
    break;

		case MSG_ADD_LINKSET:
		{
			AddLinkSet addLnkSet;
			INGwFtPktAddLinkset *lnkSet = dynamic_cast<INGwFtPktAddLinkset*>(msg);
			lnkSet->getLinksetData(addLnkSet);

			m_linksetList.push_back(addLnkSet);
		}
		break;

		case MSG_ADD_LINK:
		{
			AddLink addLnk;
			INGwFtPktAddLink *lnk = dynamic_cast<INGwFtPktAddLink*>(msg);
			lnk->getLinkData(addLnk);

			m_linkList.push_back(addLnk);

			if(addLnk.mtp2ProcId == m_peerProcId)
				m_peerLinkList.push_back(addLnk);
		}
		break;

		case MSG_ADD_USER_PART:
		{
			AddUserPart up;
			INGwFtPktAddUserPart *addUp = dynamic_cast<INGwFtPktAddUserPart*>(msg);
			addUp->getUserPartData(up);

			m_userPartList.push_back(up);
		}
		break;

		case MSG_ADD_RULE:
		{
			AddGtRule gt;
			INGwFtPktAddGtRule *addGt = dynamic_cast<INGwFtPktAddGtRule*>(msg);
			addGt->getGtRuleData(gt);
			m_gtRuleList.push_back(gt);
		}
		break;

		case MSG_ADD_ADDRMAP:
		{
			AddAddrMapCfg addrCfg;
			INGwFtPktAddGtAddrMap *addGt = dynamic_cast<INGwFtPktAddGtAddrMap*>(msg);
			addGt->getGtAddrMapData(addrCfg);
			m_gtAddrMapSeq.push_back(addrCfg);
		}
		break;

		case MSG_ADD_ROUTE:
		{
			AddRoute rte;
			INGwFtPktAddRoute *addRte = dynamic_cast<INGwFtPktAddRoute*>(msg);
			addRte->getRouteData(rte);

			m_routeList.push_back(rte);
		}
		break;

		case MSG_ADD_LOCAL_SSN:
		{
			AddLocalSsn ssn;
			INGwFtPktAddSsn *addSsn = dynamic_cast<INGwFtPktAddSsn*>(msg);
			addSsn->getSsnData(ssn);

			m_localSsnList.push_back(ssn);
		}
		break;

		case MSG_ADD_PS:
		{
			AddPs ps;
			INGwFtPktAddPs *addPs = dynamic_cast<INGwFtPktAddPs*>(msg);
			addPs->getPsData(ps);

			m_asList.push_back(ps);
		}
		break;

		case MSG_ADD_PSP:
		{
			AddPsp psp;
			INGwFtPktAddPsp *addPsp = dynamic_cast<INGwFtPktAddPsp*>(msg);
			addPsp->getPspData(psp);

			m_aspList.push_back(psp);
		}
		break;

		case MSG_ADD_ENDPOINT:
		{
			AddEndPoint ep;
			INGwFtPktAddEp *lAddEp = dynamic_cast<INGwFtPktAddEp*>(msg);
			lAddEp->getEpData(ep);

      addEp(ep);
			//m_epList.push_back(ep);
		}
		break;

		case MSG_DEL_NW:
		{
			DelNetwork nw;
			INGwFtPktDelNw *delNw = dynamic_cast<INGwFtPktDelNw*>(msg);
			delNw->getNwData(nw);

			delNetwork(nw);
		}
		break;

		case MSG_DEL_LINKSET:
		{
			DelLinkSet ls;
			INGwFtPktDelLinkset *delLs = dynamic_cast<INGwFtPktDelLinkset*>(msg);
			delLs->getLinksetData(ls);

			delLinkset(ls);
		}
			break;

		case MSG_DEL_LINK:
		{
			DelLink lnk;
			INGwFtPktDelLink *delLnk = dynamic_cast<INGwFtPktDelLink*>(msg);
			delLnk->getLinkData(lnk);

			delLink(lnk);
		}
		break;

		case MSG_DEL_USER_PART:
		{
			DelUserPart up;
			INGwFtPktDelUserPart *delUp = dynamic_cast<INGwFtPktDelUserPart*>(msg);
			delUp->getUserPartData(up);

			delUserPart(up);
		}
		break;

		case MSG_DEL_RULE:
		{
			DelGtRule gt;
			INGwFtPktDelGtRule *delGt = dynamic_cast<INGwFtPktDelGtRule*>(msg);
			delGt->getGtRuleData(gt);
			delGtRule(gt);
		}
		break;

		case MSG_DEL_ADDRMAP:
		{
			DelAddrMapCfg gt;
			INGwFtPktDelGtAddrMap *delGt = dynamic_cast<INGwFtPktDelGtAddrMap*>(msg);
			delGt->getGtAddrMapData(gt);
			delGtAddrMap(gt);
		}
		break;

		case MSG_DEL_ROUTE:
		{
			DelRoute rte;
			INGwFtPktDelRoute *delRte = dynamic_cast<INGwFtPktDelRoute*>(msg);
			delRte->getRouteData(rte);

			delRoute(rte);
		}
		break;

		case MSG_DEL_LOCAL_SSN:
		{
			DelLocalSsn ssn;
			INGwFtPktDelSsn *delSsn = dynamic_cast<INGwFtPktDelSsn*>(msg);
			delSsn->getSsnData(ssn);

			delLocalSsn(ssn);
		}
		break;

		case MSG_DEL_PS:
		{
			DelPs ps;
			INGwFtPktDelPs *delPs = dynamic_cast<INGwFtPktDelPs*>(msg);
			delPs->getPsData(ps);

			delAs(ps);
		}
		break;

		case MSG_DEL_PSP:
		{
			DelPsp psp;
			INGwFtPktDelPsp *delPsp = dynamic_cast<INGwFtPktDelPsp*>(msg);
			delPsp->getPspData(psp);

			delAsp(psp);
		}
		break;

		case MSG_DEL_ENDPOINT:
		{
			DelEndPoint ep;
			INGwFtPktDelEp *delEndPoint = dynamic_cast<INGwFtPktDelEp*>(msg);
			delEndPoint->getEpData(ep);
			delEp(ep);
		}
		break;

		case MSG_M3UA_ASSOC_UP:
		{
			M3uaAssocUp assoc;
			INGwFtPktM3uaAssocUp *assocUp = dynamic_cast<INGwFtPktM3uaAssocUp*>(msg);
			assocUp->getM3uaAssocUpData(assoc);

      addM3uaAssocUp(assoc);
		}
		break;

		case MSG_M3UA_ASSOC_DOWN:
		{
			M3uaAssocDown assoc;
			INGwFtPktM3uaAssocDown *assocDn = 
																dynamic_cast<INGwFtPktM3uaAssocDown*>(msg);
			assocDn->getNwData(assoc);
      delM3uaAssocUp(assoc);
		}
		break;

		case MSG_MOD_LINK:
		{
			AddLink addLnk;
			INGwFtPktModLink *modLnk =
													dynamic_cast<INGwFtPktModLink*>(msg);
			modLnk->getLinkData(addLnk);
			modLink(addLnk);
		}
		break;

		case MSG_MOD_LINKSET:
		{
			AddLinkSet addLnkSet;
			INGwFtPktModLinkset *modLs =
													dynamic_cast<INGwFtPktModLinkset*>(msg);
			modLs-> getLinksetData(addLnkSet);
			modLinkset(addLnkSet);
		}
		break;

		case MSG_MOD_PS:
		{
			AddPs ps;
			INGwFtPktModPs *modPs = dynamic_cast<INGwFtPktModPs*>(msg);
			modPs->getPsData(ps);
			modAs(ps);
		}
		break;

		case MSG_CONFIG_STATUS:
		{
			StkConfigStatus stkConfig;
			INGwFtPktStkConfigStatus *stk = dynamic_cast<INGwFtPktStkConfigStatus*>(msg);
			stk->getStatusData(stkConfig);
			updateState(stkConfig);
		}

		default:
			retVal = 0;
			logger.logMsg (ERROR_FLAG, 0, 
			"OUT INGwSmBlkConfig::delNode Invalid Msg[%d]", msg->getMsgType());
	}
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateNode, retVal[%d]",
	retVal);
	return retVal;
}

int 
INGwSmBlkConfig::modNode(Ss7SigtranSubsReq *req, int nodeType)
{
	logger.logMsg (TRACE_FLAG, 0, "INGwSmBlkConfig::modeNode");
	return 1;
}

int 
INGwSmBlkConfig::addNetwork(AddNetwork &addNw)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addNw");

	int retVal =0;
	m_nwList.push_back(addNw);
	retVal = m_nwList.size();

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::addNw, retVal[%d]", 
	retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delNetwork(DelNetwork &delNw)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delNetwork");
	int retVal =1;

	if(m_nwList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delNetwork, No entry in map");
		return retVal;
	}

	// UniqueKey: NwId
	AddNetworkSeq::iterator it;
	for(it=m_nwList.begin(); it != m_nwList.end(); ++it) 
	{
		if((*it).nwId == delNw.nwkId) 
		{
			m_nwList.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delNw");
	return retVal;
}

AddNetworkSeq* 
INGwSmBlkConfig::getNetworkList()
{
	return &m_nwList;
}

M3uaAspActSeq* 
INGwSmBlkConfig::getActvAspList()
{
	return &m_actAspList;
}

M3uaAspActSeq* 
INGwSmBlkConfig::getPeerActvAspList()
{
	return &m_peerActAspList;
}

int 
INGwSmBlkConfig::addLinkset(AddLinkSet &addLnkset)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addLinkset");

	int retVal =0;
	m_linksetList.push_back(addLnkset);
	retVal = m_linksetList.size();

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::addLinkset, size[%d]",
	retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delLinkset(DelLinkSet &delLnkset)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delLinkset");
	int retVal =1;

	if(m_linksetList.size() == 0) {
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delLinkset, Empty Linkset");
		return retVal;
	}

	// UniqueKey: lnkSetId
	LnkSetSeq::iterator it;
	for(it=m_linksetList.begin(); it != m_linksetList.end(); ++it) 
	{
		if((*it).lnkSetId == delLnkset.lnkSetId) 
		{
			m_linksetList.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delLinkset");
	return retVal;
}

int 
INGwSmBlkConfig::modLinkset(AddLinkSet &addLnkset)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::modLinkset");

	int retVal =0;

	DelLinkSet delLnkSet;
	
	delLnkSet.lnkSetId = addLnkset.lnkSetId;
	delLinkset(delLnkSet);

	m_linksetList.push_back(addLnkset);
	retVal = m_linksetList.size();

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::modLinkset, size[%d]",
	retVal);
	return retVal;
}

LnkSetSeq* 
INGwSmBlkConfig::getLinkSetList()
{
	return &m_linksetList;
}

int 
INGwSmBlkConfig::addUserPart(AddUserPart &addUp)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addUserPart");

	int retVal=0;
	m_userPartList.push_back(addUp);
	retVal = m_userPartList.size();

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::addUserPart, size[%d]",
	retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delUserPart(DelUserPart &delUp)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delUserPart");
	int retVal =1;

	if(m_userPartList.size() == 0) {
		logger.logMsg (TRACE_FLAG, 0, 
			"OUT INGwSmBlkConfig::delUserPart Empty Userpart");
		return retVal;
	}

	// UniqueKey: mtp3UsapId, sccpLsapId
	// m3uaUsapId, sccpLsapId
	UserPartSeq::iterator it;
	for(it=m_userPartList.begin(); it != m_userPartList.end(); ++it) 
	{
		if((*it).userPartType == MTP3_USER)
		{
			if((*it).mtp3UsapId == delUp.mtp3UsapId && 
													(*it).sccpLsapId == delUp.sccpLsapId)
			{
				m_userPartList.erase(it);
				break;
			}
		}
		else
		{
			if((*it).m3uaUsapId == delUp.m3uaUsapId && 
													(*it).sccpLsapId == delUp.sccpLsapId)
			{
				m_userPartList.erase(it);
				break;
			}
			
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delUserPart");
	return retVal;
}

UserPartSeq* 
INGwSmBlkConfig::getUserPartList()
{
	return &m_userPartList;
}

int 
INGwSmBlkConfig::addLocalSsn(AddLocalSsn &addSsn)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addLocalSsn");

	int retVal =0;
	m_localSsnList.push_back(addSsn);
	retVal = m_localSsnList.size();

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::addLocalSsn size[%d]",
	retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delLocalSsn(DelLocalSsn &delSsn)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delLocalSsn");
	int retVal =1;

	if(m_localSsnList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delLocalSsn Empty LocalSsn");
		return retVal;
	}

	// UniqueKey: lnkSetId
	LocalSsnSeq::iterator it;
	for(it=m_localSsnList.begin(); it != m_localSsnList.end(); ++it) 
	{
		if((*it).ssn == delSsn.ssn &&
			 (*it).tcapLsapId == delSsn.tcapLsapId &&
			 (*it).tcapUsapId == delSsn.tcapUsapId &&
			 (*it).sccpUsapId == delSsn.sccpUsapId)
		{
			m_localSsnList.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delLocalSsn");
	return retVal;
}

LocalSsnSeq* 
INGwSmBlkConfig::getLocalSsnList()
{
	return &m_localSsnList;
}

int 
INGwSmBlkConfig::addRoute(AddRoute &addRte)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addRoute");

	int retVal =0;
	m_routeList.push_back(addRte);
	retVal = m_routeList.size();

	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::addRoute, size[%d]", retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delRoute(DelRoute &delRte)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delRoute");

	int retVal =1;

	if(m_routeList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delRoute Empty Route");
		return retVal;
	}

	// UniqueKey: dpc
	RouteSeq::iterator it;
	for(it=m_routeList.begin(); it != m_routeList.end(); ++it) 
	{
		if((*it).dpc == delRte.dpc &&
			 (*it).nSapId == delRte.nSapId)
		{
			m_routeList.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delRoute");
	return retVal;
}

RouteSeq* 
INGwSmBlkConfig::getRouteList()
{
	return &m_routeList;
}

int 
INGwSmBlkConfig::addLink(AddLink &addLnk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addLink");

	int retVal=0;
	m_linkList.push_back(addLnk);

	if(addLnk.mtp2ProcId == m_peerProcId)
		m_peerLinkList.push_back(addLnk);

	retVal=m_linkList.size();
	logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::addLink, size[%d]", retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delLink(DelLink &delLnk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delLink");
	int retVal =1;

	if(m_linkList.size() == 0) {
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delLink Empty LinkList");
		return retVal;
	}

	// UniqueKey: lnkSetId
	bool found = false;
	LnkSeq::iterator it;
	for(it=m_linkList.begin(); it != m_linkList.end(); ++it) 
	{
		if((*it).lnkId == delLnk.lnkId )
		{
			if((*it).mtp2ProcId == m_peerProcId)
				found = true;
			m_linkList.erase(it);
			break;
		}
	}

	if(found) 
	{
		for(it=m_peerLinkList.begin(); it != m_peerLinkList.end(); ++it)
  	{
			if((*it).lnkId == delLnk.lnkId )
   	 	{
				m_peerLinkList.erase(it);
				break;
   	 	}
  	}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delLink");
	return retVal;
}

int 
INGwSmBlkConfig::modLink(AddLink &addLnk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::modLink");

	int retVal=0;
	DelLink lnk;
	memset(&addLnk, 0, sizeof(AddLink));

	lnk.lnkId 			= addLnk.lnkId;
	lnk.mtp2ProcId = addLnk.mtp2ProcId;
	lnk.mtp2UsapId = addLnk.mtp2UsapId;
	lnk.mtp3LsapId = addLnk.mtp3LsapId;

	delLink(lnk);
	addLink(addLnk);

	retVal=m_linkList.size();

	logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::modLink, size[%d]", retVal);
	return retVal;
}

LnkSeq* 
INGwSmBlkConfig::getLinkList()
{
	return &m_linkList;
}

// This method is being used while getting alarms 
// from MTP3 Layer
bool INGwSmBlkConfig::getLinkInfo(U16 p_dlSapId, AddLink &p_linkInfo)
{
	logger.logMsg (TRACE_FLAG, 0, "In INGwSmBlkConfig::getLinkInfo dlSapId <%d>",
	p_dlSapId);
  for(int i=0; i< m_linkList.size(); i++)
  {
    if(p_dlSapId == m_linkList[i].mtp2UsapId)
    {
	    logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::getLinkInfo");
      memcpy(&p_linkInfo,&(m_linkList[i]),sizeof(AddLink));
      return true; 
    }
  }
	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::getLinkInfo cannot find linkInfo");
  return false;
}

bool 
INGwSmBlkConfig::getLinkInfoForLinkId(U16 p_lnkId, AddLink &p_linkInfo)
{
	logger.logMsg (TRACE_FLAG, 0, 
	"In INGwSmBlkConfig::getLinkInfoForLinkId linkId <%d>", p_lnkId);

  for(int i=0; i< m_linkList.size(); i++)
  {
    if(p_lnkId == m_linkList[i].lnkId)
    {
	    logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::getLinkInfoForLinkId");
      memcpy(&p_linkInfo,&(m_linkList[i]),sizeof(AddLink));
      return true; 
    }
  }
	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::getLinkInfoForLinkId cannot find linkInfo");
  return false;
}

LnkSeq* 
INGwSmBlkConfig::getPeerLinkList()
{
	return &m_peerLinkList;
}

int 
INGwSmBlkConfig::addGtRule(AddGtRule &addGtRule)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addGtRule");

	m_gtRuleList.push_back(addGtRule);
	int retVal = m_gtRuleList.size();

	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::addGtRule, size[%d]", retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delGtRule(DelGtRule &delGtRule)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delGtRule");
	int retVal =1;

	if(m_gtRuleList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delGtRule Empty GtRuleSeq");
		return retVal;
	}

	GtRuleSeq::iterator it;
	for(it=m_gtRuleList.begin(); it != m_gtRuleList.end(); ++it) 
	{
		if((*it).nwId == delGtRule.nwId &&
			 (*it).format == delGtRule.format &&
			 (*it).sw == delGtRule.sw)
		{
			m_gtRuleList.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delGtRule");
	return retVal;
}

GtRuleSeq* 
INGwSmBlkConfig::getGtRuleList()
{
	return &m_gtRuleList;
}

int 
INGwSmBlkConfig::addGtAddrMap(AddAddrMapCfg &addGtMap)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addGtAddrMap");
	m_gtAddrMapSeq.push_back(addGtMap);
	int retVal = m_gtAddrMapSeq.size();
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::addGtAddrMap size[%d]",
	retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delGtAddrMap(DelAddrMapCfg &delGtMap)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delGtAddrMap");
	int retVal =1;

	if(m_gtAddrMapSeq.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delGtAddrMap, Empty GtAddrMap");
		return retVal;
	}

	AddrMapSeq::iterator it;
	for(it=m_gtAddrMapSeq.begin(); it != m_gtAddrMapSeq.end(); ++it) 
	{
		if((*it).nwkId == delGtMap.nwkId &&
			 (*it).format == delGtMap.format)
		{
			m_gtAddrMapSeq.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delGtAddrMap");
	return retVal;
}

AddrMapSeq* 
INGwSmBlkConfig::getGtAddrMapList()
{
	return &m_gtAddrMapSeq;
}

int 
INGwSmBlkConfig::addEp(AddEndPoint &addEp)
{
	logger.logMsg (TRACE_FLAG, 0, "IN addEp()::");

	m_epList.push_back(addEp);
	int retVal = m_epList.size();

	logger.logMsg (TRACE_FLAG, 0, 
	"OUT addEp():: size[%d] peerEpList Size<%d>", 
                retVal, m_peerEpList.size());

  if (addEp.sctpProcId == m_peerProcId) {

	  m_peerEpList.push_back(addEp);
	  logger.logMsg (ALWAYS_FLAG, 0, "addEp():: peerEpList size[%d]",
           m_peerEpList.size());
  }
	return retVal;
}

int 
INGwSmBlkConfig::delEp(DelEndPoint &delEp)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delEp");
	int retVal =1;
	bool found = false;

	if(m_epList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delEp Empty EpSeq");
		return retVal;
	}

	EpSeq::iterator it;
	for(it=m_epList.begin(); it != m_epList.end(); ++it) 
	{
		if((*it).sctpProcId == delEp.sctpProcId &&
			(*it).sctpLsapId  == delEp.sctpLsapId &&
			(*it).sctpUsapId  == delEp.sctpUsapId &&
			(*it).m3uaLsapId  == delEp.m3uaLsapId &&
			(*it).tuclUsapId  == delEp.tuclUsapId )
		{
			m_epList.erase(it);
      found = true;
			break;
		}
	}

	if(found) 
	{
		for(it=m_peerEpList.begin(); it != m_peerEpList.end(); ++it)
  	{
		  if((*it).sctpProcId == delEp.sctpProcId &&
		  	(*it).sctpLsapId  == delEp.sctpLsapId &&
		  	(*it).sctpUsapId  == delEp.sctpUsapId &&
		  	(*it).m3uaLsapId  == delEp.m3uaLsapId &&
		  	(*it).tuclUsapId  == delEp.tuclUsapId )
   	 	{
				m_peerEpList.erase(it);
				break;
   	 	}
  	}
	}
  

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delEp");
	return retVal;
}

EpSeq* 
INGwSmBlkConfig::getEpList()
{
	return &m_epList;
}


EpSeq* 
INGwSmBlkConfig::getPeerEpList()
{
	return &m_peerEpList;
}

int 
INGwSmBlkConfig::addAsp(AddPsp &addPsp)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addAsp");
	m_aspList.push_back(addPsp);
	int retVal = m_aspList.size();
	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::addAsp, size[%d]", retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delAsp(DelPsp &delPsp)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delAsp");
	int retVal =1;

	if(m_aspList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delAsp Empty PspList");
		return retVal;
	}

	AspSeq::iterator it;
	for(it=m_aspList.begin(); it != m_aspList.end(); ++it) 
	{
		if((*it).pspId == delPsp.pspId)
		{
			m_aspList.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delAsp");
	return retVal;
}

AspSeq* 
INGwSmBlkConfig::getAspList()
{
	return &m_aspList;
}

bool 
INGwSmBlkConfig::getAsp(U32 pspId, AddPsp &psp)
{
	bool found = false;
	AspSeq::iterator it;
	for(it=m_aspList.begin(); it != m_aspList.end(); ++it) 
	{
		if((*it).pspId == pspId)
		{
			memcpy(&(psp), it, sizeof(AddPsp));
			found = true;
			break;
		}
	}
	return found;
}

int 
INGwSmBlkConfig::addAs(AddPs &addPs)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::addAs");
	m_asList.push_back(addPs);
	int retVal =  m_asList.size();
	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::addAs, size[%d]", retVal);
	return retVal;
}

int 
INGwSmBlkConfig::modAs(AddPs &addPs)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::modAs");

	DelPs del;
	del.psId  = addPs.psId;
	del.nwkId = addPs.nwkId;

	delAs(del);
	addAs(addPs);

	int retVal = m_asList.size();
	logger.logMsg (TRACE_FLAG, 0, 
	"OUT INGwSmBlkConfig::modAs, size[%d]", retVal);
	return retVal;
}

int 
INGwSmBlkConfig::delAs(DelPs &delPs)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delAs");
	int retVal =1;

	if(m_asList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delAs, Empty PsList");
		return retVal;
	}

	AsSeq::iterator it;
	for(it=m_asList.begin(); it != m_asList.end(); ++it) 
	{
		if((*it).psId  == delPs.psId &&
			 (*it).nwkId == delPs.nwkId )
		{
			m_asList.erase(it);
			break;
		}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delAs");
	return retVal;
}

AsSeq* 
INGwSmBlkConfig::getAsList()
{
	return &m_asList;
}

void
INGwSmBlkConfig::getActiveProcIds(vector<int> &procIdList)
{
	procIdList.push_back(m_selfProcId);

	// Insert peer procId is up
	if(INGwIfrPrParamRepository::getInstance().getPeerStatus() &&  
		getRelayStatus()) {
		procIdList.push_back(m_peerProcId);
	}

	logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
	"++VER++INGwSmBlkConfig::getActiveProcIds Returning"
	" Size[%d] ProcIds[%d-%d]",
	procIdList.size(), procIdList[0], 
	(procIdList.size() == 2)?procIdList[1]:0);
}

void
INGwSmBlkConfig::getPeerProcId(vector<int> &procId)
{
	procId.push_back(m_peerProcId);
}

void
INGwSmBlkConfig::getSelfProcId(vector<int> &procId)
{
	procId.push_back(m_selfProcId);
}

void 
INGwSmBlkConfig::getAllProcId(vector<int> &procId)
{
	procId.push_back(m_selfProcId);
	if(m_peerProcId != 0)
		procId.push_back(m_peerProcId);
}

void 
INGwSmBlkConfig::roleChangedToActive()
{
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::roleChangedToActive()");

  logger.logMsg (ALWAYS_FLAG, 0, "roleChangedToActive(): "
         "m_selfState <%d>, m_peerState OldVal<%d> NewVal UNINITIALIZED",
         m_selfState, m_peerState);

	// Here m_selfState corresponds to peer INC state
	// whose role has been updates as ACTIVE
	// Common: mark peerState as UNINITIALIZED so that once peerUp is 
	// rxed then we should perform stack config for peer. 
	// case 1: If state is INITIALIZED then ignore and return
	// case 2: If state is other then INITIALIZED then perform Initialization
	m_peerState = UNINITIALIZED;

	if(m_selfState == INITIALIZED) {
		INGwTcapProvider::getInstance().registerAllAppWithStack();
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++INGwSmBlkConfig::roleChangedToActive selfState[INITIALIZED]"
		"Ignoring stack configuration");
    INGwSmWrapper *lpSmWrapper = 
		  INGwTcapProvider::getInstance().getSmWrapperPtr() ;
    lpSmWrapper->startSendStsReq();
		return;
	}
  //else {
  //  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
  //         "roleChangedToActive(): selfState[%d] "
  //         "Self stack state not initialized. COMMITING SUICIDE", m_selfState);

  //  char lpcTime[64];
  //  memset(lpcTime, 0, sizeof(lpcTime));
  //  lpcTime[0] = '1';
  //  g_getCurrentTime(lpcTime);

  //  printf("[+INC+] %s:%d launchThrdToChkSuicide(): %s "
  //         "roleChangedToActive(): selfState[%d] "
  //         "Self stack state not initialized. COMMITING SUICIDE",
  //         __FILE__, __LINE__, lpcTime, m_selfState); fflush(stdout);
  //  raise(9);
  //}

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++roleChangedToActive() "
         "m_peerState<%d>, m_selfState OldVal<%d> NewVal UNINITIALIZED. "
	       "Enqueuing ROLE_CHANGED_ACTIVE", m_peerState, m_selfState);

	m_selfState = UNINITIALIZED;

	msgQStruct *req = new msgQStruct();
	req->m_action   = ROLE_CHANGED_ACTIVE;

	enQ(req);

	logger.logMsg (ALWAYS_FLAG, 0, 
		"INGwSmBlkConfig::++VER++ EnQueuing ROLE_CHANGED_ACTIVE");

  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::roleChangedToActive()");
}

void 
INGwSmBlkConfig::peerUp()
{
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::peerUp()");

	// If self State is UNINITIALIZED, then it means
	// this is rxed for first time. Ignore it as it 
	// would be handled through executeStackInit
	// If PeerUP received and peer state is not UNINITIALIZED then it is 
	// bogus UP indication
	if(m_selfState == UNINITIALIZED || m_peerState != UNINITIALIZED) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++INGwSmBlkConfig::peerUp Rxed selfState[%d] PeerState [%d]"
		"Bogus PeerUp Rxed, either selfstate or peerState not in correct State. "
    "SelfSGRole[%s]", m_selfState, m_peerState,
    INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole().c_str());

		return;
	}

  logger.logMsg (ALWAYS_FLAG, 0, "peerUp(): "
         "m_selfState <%d>, m_peerState OldVal<%d> NewVal CONFIG_INPROGRESS",
         m_selfState, m_peerState);
	// Initiate Peer Configuration
	m_peerState = CONFIG_INPROGRESS;

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
	"++VER++ peerUp() configuring stack for peer Enqueuing PEER_UP");

	msgQStruct *req = new msgQStruct();
	req->m_action   = PEER_UP;;

	enQ(req);

	logger.logMsg (ALWAYS_FLAG, 0, 
		"INGwSmBlkConfig::++VER++ EnQueuing PEER_UP");

  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::peerUp()");
}

void 
INGwSmBlkConfig::peerDown()
{
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::peerDown()");
	msgQStruct *req = new msgQStruct();
	req->m_action   = PEER_DOWN;

	enQ(req);

	logger.logMsg (ALWAYS_FLAG, 0, 
		"INGwSmBlkConfig::++VER++ EnQueuing PEER_DOWN");

  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::peerDown()");
}


// This method handles following cases 
// Case 1 - Stack Initialization - This will happen when CCM is coming up for
// 		      first time. Following would be the logic
//				  a. If myRole is Active and state is UNINITIALIZED start 
//					   configuration for self and peer
//				  b. If myRole is Standby and state is UNINITIALIZED then populate
//					   Internal DB
void 
INGwSmBlkConfig::execute()
{
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::execute()");
	m_isRunning = true;

	while(m_isRunning) {

		msgQStruct* req = NULL;

		if (NULL == (req = dQ()))
		{
			sleep (2);
			logger.logINGwMsg(false, VERBOSE_FLAG, 0,
			"INGwSmBlkConfig::execute msgQ empty..continuing");

			continue;
		}

		vector<int> procIdList;

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++INGwSmBlkConfig::execute Action[%s]",
		(req->m_action == STACK_INIT)?"STACK_INIT":
		(req->m_action == PEER_UP)?"PEER_UP":
		(req->m_action == PEER_DOWN)?"PEER_DOWN":
		(req->m_action == ROLE_CHANGED_ACTIVE)?"ROLE_CHANGED_ACTIVE":"UNKNOWN");

		switch(req->m_action) 
		{
			case STACK_INIT:
				executeStackInit();
				break;

			case PEER_UP:
				stackConfigForPeer();
				break;

			case PEER_DOWN:
	     logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ execute() "
              "m_selfState<%d>,  m_peerState OldVal<%d> NewVal UNINITIALIZED",
	            m_selfState, m_peerState);
				m_peerState = UNINITIALIZED;
				break;

			case ROLE_CHANGED_ACTIVE:
			{
				getSelfProcId(procIdList);
				performStackConfig(procIdList, false);
				INGwTcapProvider::getInstance().registerAllAppWithStack();
        INGwSmWrapper *lpSmWrapper = 
		      INGwTcapProvider::getInstance().getSmWrapperPtr() ;
        lpSmWrapper->startSendStsReq();
	      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ execute() "
               "m_peerState<%d>, m_selfState OldVal<%d> NewVal INITIALIZED",
	             m_peerState, m_selfState);
				m_selfState = INITIALIZED;
			}
			break;
		}

		if(req != NULL)
			delete req;
	}
  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::execute()");
}

void
INGwSmBlkConfig::executeStackInit()
{
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::executeStackInit()");

	int selfRole = INGwTcapProvider::getInstance().myRole();
	int cnt = m_relayStatCheckCnt;

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ executeStackInit() "
         "selfRole<%d> m_peerState<%d>, m_selfState<%d> "
         "ryStatChkCnt<%d> ryRetryTime<%d>", selfRole, m_peerState, m_selfState,
         m_relayStatCheckCnt, m_relayRetryTime);

	if(selfRole == 1 && m_selfState==UNINITIALIZED) 
	{
		// check if installed in FT or NON-FT mode
		// if FT mode check if both Peer and Relay are UP
		if(m_peerId != 0) 
		{
			if(INGwIfrPrParamRepository::getInstance().getPeerStatus()) 
			{
				while(!getRelayStatus() && cnt !=0){
					sleep(m_relayRetryTime);
					cnt--;
				}
			}
		}

		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++INGwSmBlkConfig::executeStackInit() cnt[%d] %s PeerId[%d]",
		cnt, (!INGwIfrPrParamRepository::getInstance().getPeerStatus())?
		"Peer is not UP":
		(cnt == 0 && INGwIfrPrParamRepository::getInstance().getPeerStatus())?
		"Peer is UP but Relay is not UP": "Both Peer and Relay is UP",
		m_peerId);

		INGwSmWrapper *lpSmWrapper = 
		INGwTcapProvider::getInstance().getSmWrapperPtr() ;

		vector<int> procIdList;
		getActiveProcIds(procIdList);
		lpSmWrapper->initializeNode(procIdList);
		lpSmWrapper->initializeAlarms(procIdList);

		if(procIdList.size() == 2)
			updatePeerEnabled(true);

		INGwIfrMgrManager::getInstance().fetchSs7InitialConfig(1, 1); // 1 = ALL

    lpSmWrapper->initializeStats();//Reading Oids for statistics

    lpSmWrapper->startSendStsReq();//starts timer for stats and start sending stsReq to Stack

	  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ executeStackInit() "
           "m_peerState<%d>, m_selfState OldVal<%d> NewVal INITIALIZED",
	         m_peerState, m_selfState);
		// change State
		m_selfState = INITIALIZED;

		if(procIdList.size() == 2) {
	    logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ executeStackInit() "
             "m_peerState<%d>, m_selfState OldVal<%d> NewVal INITIALIZED",
	           m_peerState, m_selfState);

			m_peerState = INITIALIZED;
    }
	}
	else if( (selfRole == 0) && 
           ((m_selfState==UNINITIALIZED || m_selfState == CONFIG_INPROGRESS)) )
	{
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++INGwSmBlkConfig::executeStackInit() Standby fetching Data");

		INGwIfrMgrManager::getInstance().fetchSs7InitialConfig(1, 0); // 1= ALL		
		INGwSmWrapper *lpSmWrapper = 
		  INGwTcapProvider::getInstance().getSmWrapperPtr() ;
    lpSmWrapper->initializeStats();//Reading Oids for statistics at secondory node also
	}
	else 
	{
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++INGwSmBlkConfig::executeStackInit() state is not in "
		" UNINITIALIZED state ignoring, m_selfState[%d]", m_selfState);
	}
  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::executeStackInit()");
}

void 
INGwSmBlkConfig::stackConfigForPeer()
{
  int retVal = 1;
	vector<int> procIdList;

  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::stackConfigForPeer()");

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
	       "++VER++ stackConfigForPeer() m_selfState[%d] m_peerState[%d]",
	       m_selfState, m_peerState);

  INGwSmWrapper *lpSmWrapper = 
		INGwTcapProvider::getInstance().getSmWrapperPtr() ;

	INGwSmConfigQMsg  *lqueueMsg = NULL;
	Ss7SigtranSubsReq *req       = NULL;

	// check if Relay Is UP. Wait it comes up
	int cnt = m_relayStatCheckCnt;
	while(!getRelayStatus() && cnt !=0){
		sleep(m_relayRetryTime);
		cnt--;
	}

	// return if Relay is not up
	if(cnt == 0 && !getRelayStatus()) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		       "++VER++ stackConfigForPeer() PeerUp rxed relay is no UP"
		       " Not configuring Peer cnt[%d]. "
           "m_selfState<%d>, m_peerState OldVal<%d> NewVal UNINITIALIZED",
           cnt, m_selfState, m_peerState);

		m_peerState = UNINITIALIZED;
		return;	
	}

	getPeerProcId(procIdList);
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "wait 5 secs for stack...");
	sleep(5);

	// This has been commented till we get confirmation 
	// from CCPU. Currently ccpu has asked to change order
	// of initialization
	performStackConfig(procIdList, true);
	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ executeStackInit() "
         "m_selfState<%d>, m_peerState OldVal<%d> NewVal INITIALIZED",
	       m_selfState, m_peerState);
	m_peerState = INITIALIZED;

  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::stackConfigForPeer()");
}
		
void 
INGwSmBlkConfig::performStackConfig(vector<int> &procIdList,  bool isForPeer)
{
	logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
         "IN INGwSmBlkConfig::performStackConfig() forPeer<%d>", isForPeer);

	INGwSmConfigQMsg  *lqueueMsg = NULL;
	Ss7SigtranSubsReq *req       = NULL;
  char lpcTime[64];

  INGwSmWrapper *lpSmWrapper = 
	INGwTcapProvider::getInstance().getSmWrapperPtr() ;

  lpSmWrapper->setAllowCfgFromEms(false,
            (char *)"INGwSmBlkConfig::performStackConfig()", __LINE__);

  //if ( ! isForPeer ) {
  //  //In case of role change to active, before starting self config
  //  //always clean self config (including gen cfg).
  //  //This will flush the memory of each layers.
  //  req = new Ss7SigtranSubsReq;
  //  memset ((U8*)req, 0, sizeof (Ss7SigtranSubsReq));
  //  req->cmd_type  = SHUTDOWN_LAYERS;
  // 
  //  int nmbEntity = 0;
  //  req->u.shutDownLayers.entId[nmbEntity++] = (U8)BP_AIN_SM_TCA_LAYER;
  //  req->u.shutDownLayers.entId[nmbEntity++] = (U8)BP_AIN_SM_SCC_LAYER;
  //  req->u.shutDownLayers.entId[nmbEntity++] = (U8)BP_AIN_SM_MTP3_LAYER;
  //  req->u.shutDownLayers.entId[nmbEntity++] = (U8)BP_AIN_SM_M3U_LAYER;
  //  req->u.shutDownLayers.entId[nmbEntity++] = (U8)BP_AIN_SM_SCT_LAYER;

  //  req->u.shutDownLayers.nmbEntity = nmbEntity;

  //  lqueueMsg      =  new INGwSmConfigQMsg;
  //  lqueueMsg->req = req;
  //  lqueueMsg->src = BP_AIN_SM_SRC_CCM;
  //  lqueueMsg->procIdList = procIdList;
  //  lqueueMsg->from = 1;

  //  lpSmWrapper->postMsg(lqueueMsg,true);
  //  
  //}

	// Required only incase of Peer UP
	// Not required incase of Role change
	if(procIdList[0] == m_peerProcId)
	{
			lqueueMsg =  new INGwSmConfigQMsg;
    	lqueueMsg->req = NULL;
    	lqueueMsg->src = BP_AIN_SM_STACK_CONFIG_START;
    	lqueueMsg->from = (isForPeer)?2:1;

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] performStackConfig(): %s "
             "Enqueuing BP_AIN_SM_STACK_CONFIG_START. From<%d>\n",
             lpcTime, lqueueMsg->from); fflush(stdout);
	    logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
             "performStackConfig(), Enqueuing BP_AIN_SM_STACK_CONFIG_START. "
             "From<%d>", lqueueMsg->from);
  
    	lpSmWrapper->postMsg(lqueueMsg,true);
	}

  if(m_nwList.size() == 0)
	{
		if(procIdList[0] == m_peerProcId)
		{
      INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
      lqueueMsg->req = NULL;
      lqueueMsg->src = BP_AIN_SM_STACK_CONFIG_END;
    	lqueueMsg->from = (isForPeer)?2:1;

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] performStackConfig(): %s "
             "Enqueuing BP_AIN_SM_STACK_CONFIG_END. From<%d>\n",
             lpcTime, lqueueMsg->from); fflush(stdout);
	    logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
             "performStackConfig(), Enqueuing BP_AIN_SM_STACK_CONFIG_END. "
             "From<%d>", lqueueMsg->from);
      
      lpSmWrapper->postMsg(lqueueMsg,true);
		}

		if(isForPeer)
			initializePeerNode();

	  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ executeStackInit() "
           "m_peerState<%d>, m_selfState OldVal<%d> NewVal INITIALIZED",
	         m_peerState, m_selfState);
		// change State back to Initialized
		m_selfState = INITIALIZED;

    logger.logINGwMsg(false,ALWAYS_FLAG,0,
			"++VER++Leaving INGwSmBlkConfig::performStackConfig() NO NETWORK INFO AVAILABLE");
    return ;
	}

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
	"++VER++ INGwSmBlkConfig::performStackConfig() NwList size <%d>",
	m_nwList.size());

  for (int i=0; i < m_nwList.size(); ++i)
  {
    req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addNwk), &m_nwList[i], sizeof(AddNetwork));
		req->cmd_type  = ADD_NETWORK;
    lqueueMsg      =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() UserPart size <%d>",
		m_userPartList.size());

	for (int i=0; i <m_userPartList.size(); ++i)
	{
		req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addUserPart), &m_userPartList[i], 
		sizeof(AddUserPart));

		req->cmd_type  = ADD_USERPART;
    lqueueMsg 		 =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() Linkset size <%d>",
		m_linksetList.size());

	for (int i=0; i <m_linksetList.size(); ++i)
  {
		req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.lnkSet), &m_linksetList[i], sizeof(AddLinkSet));

		req->cmd_type  = ADD_LINKSET;
    lqueueMsg 		 =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() LocalSSN size <%d>",
		m_localSsnList.size());

	for (int i=0; i <m_localSsnList.size(); ++i)
	{
    req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addLocalSsn), &m_localSsnList[i], sizeof(AddLocalSsn));

		req->cmd_type  = ADD_LOCAL_SSN;
    lqueueMsg 		 =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() Route size <%d>",
		m_routeList.size());

	for (int i=0; i <m_routeList.size(); ++i)
  {
		req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addRoute), &m_routeList[i], sizeof(AddRoute));
		req->cmd_type  = ADD_ROUTE;
    lqueueMsg 		 =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() Link size <%d>",
		m_linkList.size());

	for (int i=0; i < m_linkList.size(); ++i)
	{
		req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.lnk), &m_linkList[i], sizeof(AddLink));
		req->cmd_type  = ADD_LINK;
		lqueueMsg 	   =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

   	lpSmWrapper->postMsg(lqueueMsg,true);
	}

	// Enabling of Node must be performed only after 
	// addition of Data and before adding/enabling links.
	// This is beause the mtp3 layer is active-active and
	// that should be enable before enabling link which 
	// binds mtp3-mtp2. 
	//if(!isPeerEnabled())
	//lpSmWrapper->initializeNode(procIdList);
	if(isForPeer)
		initializePeerNode();

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() GtRule size <%d>",
		m_gtRuleList.size());

	for (int i=0; i <m_gtRuleList.size(); ++i)
	{
		req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addGtRule), &m_gtRuleList[i], sizeof(AddGtRule));

		req->cmd_type  = ADD_GTRULE;
		lqueueMsg      = new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() GtAddrMap size <%d>",
		m_gtAddrMapSeq.size());

	for (int i=0; i <m_gtAddrMapSeq.size(); ++i)
	{	
    req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addAddrMapCfg), &m_gtAddrMapSeq[i], 
		sizeof(AddAddrMapCfg));

		req->cmd_type  = ADD_GTADDRMAP;
    lqueueMsg      =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
	"++VER++ INGwSmBlkConfig::performStackConfig() ASP size <%d>",
	m_aspList.size());

	for (int i=0; i <m_aspList.size(); ++i)
  {
		req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addpsp), &m_aspList[i], 
		sizeof(AddPsp));

		req->cmd_type  = ADD_ASP;
    lqueueMsg =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }


  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() EndPoint size <%d>",
		m_epList.size());

	for (int i=0; i <m_epList.size(); ++i)
	{
    req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addEp), &m_epList[i], 
			sizeof(AddEndPoint));
 
		req->cmd_type  = ADD_ENDPOINT;
    lqueueMsg 		 =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() As size <%d>",
		m_asList.size());

	for (int i=0; i <m_asList.size(); ++i)
	{
    req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.addPs), &m_asList[i], 
		sizeof(AddPs));

		req->cmd_type  = ADD_AS;
    lqueueMsg 		 = new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }


	if(isForPeer) {
    enaSgtrnLyrs(2);
  }
 
	if(isForPeer) {
    logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
	  	"++VER++ INGwSmBlkConfig::performStackConfig() BIND SCTSAP");
	  for (int i=0; i < m_peerAssocUpList.size(); ++i)
    {
      req = new Ss7SigtranSubsReq;
      cmMemset((U8 *)req, 0,sizeof(Ss7SigtranSubsReq));

      req->u.bindSap.sapId = m_peerAssocUpList[i].m3uaLsapId;

		  req->cmd_type  = BND_M3UA;
      lqueueMsg 		 = new INGwSmConfigQMsg;
      lqueueMsg->req = req;
      lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      lqueueMsg->procIdList = procIdList;
      lqueueMsg->from       = 2;

      lpSmWrapper->postMsg(lqueueMsg,true);
    }


    logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
	  	"++VER++ INGwSmBlkConfig::performStackConfig() PEER OPEN ENDPOINT size<%d>",
      m_peerEpList.size());
	  for (int i=0; i < m_peerEpList.size(); ++i)
    {
      req = new Ss7SigtranSubsReq;
      cmMemset((U8 *)req, 0,sizeof(Ss7SigtranSubsReq));

      req->u.openEp.suId = m_peerEpList[i].sctpLsapId;

	    logger.logMsg(ALWAYS_FLAG, 0, 
             "performStackConfig(): open peer EP sctpLSapId <%d>", 
             req->u.openEp.suId);

		  req->cmd_type  = OPEN_ENDPOINT;
      lqueueMsg 		 = new INGwSmConfigQMsg;
      lqueueMsg->req = req;
      lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      lqueueMsg->procIdList = procIdList;
      lqueueMsg->from       = 2;

      lpSmWrapper->postMsg(lqueueMsg,true);
    }
  }

  logger.logMsg (ALWAYS_FLAG, 0, 
         "performStackConfig():: Sleep 5 secs. to let OPEN ENDPOINT complete...");
  sleep(5);

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"++VER++ INGwSmBlkConfig::performStackConfig() Assoc size <%d>",
		m_assocUpList.size());

	for (int i=0; i <m_assocUpList.size(); ++i)
	{
    req = new Ss7SigtranSubsReq;

		memcpy(&(req->u.m3uaAssocUp), &m_assocUpList[i], 
		       sizeof(M3uaAssocUp));

		req->cmd_type  = ASSOC_UP;
    lqueueMsg 		 = new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    lpSmWrapper->postMsg(lqueueMsg,true);
  }
  

	if(procIdList[0] == m_peerProcId)
	{
  	lqueueMsg =  new INGwSmConfigQMsg;
  	lqueueMsg->req = NULL;
  	lqueueMsg->src = BP_AIN_SM_STACK_CONFIG_END;
  	lqueueMsg->procIdList = procIdList;
    lqueueMsg->from = (isForPeer)?2:1;

    memset(lpcTime, 0, sizeof(lpcTime));
    lpcTime[0] = '1';
    g_getCurrentTime(lpcTime);
    printf("[+INC+] performStackConfig(): %s "
           "Enqueuing BP_AIN_SM_STACK_CONFIG_END. From<%d>\n",
           lpcTime, lqueueMsg->from); fflush(stdout);
	  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
           "performStackConfig(), Enqueuing BP_AIN_SM_STACK_CONFIG_END. "
           "From<%d>", lqueueMsg->from);
    
  	lpSmWrapper->postMsg(lqueueMsg,true);
	}

  
  //updating the Active ASP's list from active to standby node(which has just come up)
  int li_selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
	int li_peerId = INGwIfrPrParamRepository::getInstance().getPeerId();
  if(li_peerId != 0)
  {
    M3uaAspAct aspAct;

    for(int i=0; i<m_actAspList.size(); i++) 
    {
      logger.logMsg (VERBOSE_FLAG, 0, 
         "performStackConfig():: Updating the node for active asps at active node");
      memset(&aspAct, '\0', sizeof(M3uaAspAct));
      memcpy(&aspAct, &m_actAspList[i], sizeof(M3uaAspAct));

      INGwFtPktAspActive ftAspAct;
		  ftAspAct.initialize(li_selfId, li_peerId, aspAct);
		  INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAspAct);
    }
  } 
	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "++VER++ performStackConfig() "
         "m_peerState<%d>, m_selfState OldVal<%d> NewVal INITIALIZED",
	       m_peerState, m_selfState);
	// change State back to Initialized
	m_selfState = INITIALIZED;

  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::performStackConfig()");
  return;
}

void
INGwSmBlkConfig::updateRelayStatus(bool status)
{
  logger.logMsg(TRACE_FLAG, 0,"IN INGwSmBlkConfig::updateRelayStatus, status[%d]",
	status);

	pthread_mutex_lock (&m_lock);
	m_relayStatus = status;
	pthread_mutex_unlock (&m_lock);

  LogINGwTrace(false, 0,"OUT INGwSmBlkConfig::updateRelayStatus");
}

bool
INGwSmBlkConfig::getRelayStatus()
{
	bool retVal = false;

  LogINGwTrace(false, 0,"IN INGwSmBlkConfig::getRelayStatus");

	pthread_mutex_lock (&m_lock);
	retVal = m_relayStatus;
	pthread_mutex_unlock (&m_lock);

  logger.logMsg(TRACE_FLAG, 0,"OUT INGwSmBlkConfig::getRelayStatus, status[%d]",
	retVal);
	return retVal;
}

void
INGwSmBlkConfig::initializeStack()
{
  LogINGwTrace(false, 0,"IN INGwSmBlkConfig::initializeStack");
  logger.logMsg (ALWAYS_FLAG, 0, "initializeStack(): "
         "m_selfState<%d> m_peerState<%d>", m_selfState, m_peerState);
	if(m_selfState != INITIALIZED) 
	{
		msgQStruct *req = new msgQStruct();
		req->m_action   = STACK_INIT;

		logger.logMsg (ALWAYS_FLAG, 0, 
		       "++VER++ initializeStack() EnQueuing STACK_INIT");
		enQ(req);
	}
  logger.logMsg(TRACE_FLAG, 0,"OUT INGwSmBlkConfig::initializeStack, m_selfState[%d]",
	m_selfState);
}

void
INGwSmBlkConfig::enQ(msgQStruct *req)
{
	pthread_mutex_lock (&m_Qlock);
	m_msgQ.push(req);
	pthread_mutex_unlock (&m_Qlock);
}

INGwSmBlkConfig::msgQStruct*
INGwSmBlkConfig::dQ()
{
	msgQStruct *req = NULL;
	pthread_mutex_lock (&m_Qlock);
	if(m_msgQ.empty() == false)
	{
		req = m_msgQ.front();
		m_msgQ.pop();
	}
	pthread_mutex_unlock (&m_Qlock);

	return req;
}

int 
INGwSmBlkConfig::addM3uaAssocUp(M3uaAssocUp &addM3ua)
{
	logger.logMsg (TRACE_FLAG, 0, "IN addM3uaAssocUp");

	int retVal =0;
	m_assocUpList.push_back(addM3ua);
	retVal = m_assocUpList.size();

	logger.logMsg (ALWAYS_FLAG, 0, "addM3uaAssocUp, size[%d] peerAssocUpList size[%d]",
	retVal, m_peerAssocUpList.size());

	EpSeq::iterator it;
	for(it=m_epList.begin(); it != m_epList.end(); ++it) {
    if (((*it).m3uaLsapId == addM3ua.m3uaLsapId) &&
        ((*it).sctpProcId) == m_peerProcId) {

	    m_peerAssocUpList.push_back(addM3ua);
	    logger.logMsg (ALWAYS_FLAG, 0, "addM3uaAssocUp, peerAssocUpList size[%d]",
             m_peerAssocUpList.size());
    }
  }

	logger.logMsg (TRACE_FLAG, 0, "OUT addM3uaAssocUp");

	return retVal;
}

int 
INGwSmBlkConfig::addActvAsp(M3uaAspAct &actAsp)
{
  logger.logMsg (TRACE_FLAG, 0, "IN addActvAsp");

  int retVal =0;
  m_actAspList.push_back(actAsp);
  retVal = m_actAspList.size();

  logger.logMsg (ALWAYS_FLAG, 0, "addActvAsp size[%d], peerActvAspList[%d] ",
  retVal, m_peerActAspList.size());

  EpSeq::iterator it;
  for(it=m_epList.begin(); it != m_epList.end(); ++it) {
    if (((*it).m3uaLsapId == actAsp.m3uaLsapId) &&
        ((*it).sctpProcId) == m_peerProcId) {

    m_peerActAspList.push_back(actAsp);
    logger.logMsg (ALWAYS_FLAG, 0, "addActvAsp, peerActvAspList size[%d]",
             m_peerActAspList.size());
    }
  }

  logger.logMsg (TRACE_FLAG, 0, "OUT addActvAsp");

  return retVal;
}

int 
INGwSmBlkConfig::delActvAsp(M3uaAspInAct &inactAsp)
{

  logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delActvAsp");
  int retVal =1;
  bool found = false;

  if(m_actAspList.size() == 0){
    logger.logMsg (TRACE_FLAG, 0, 
    "OUT INGwSmBlkConfig::delActvAsp, No entry in map");
    return retVal;
  }

  M3uaAspActSeq::iterator it;
  for(it=m_actAspList.begin(); it != m_actAspList.end(); ++it) 
  {
    if(((*it).pspId == inactAsp.pspId) && 
      ((*it).m3uaLsapId == inactAsp.m3uaLsapId)) 
    {
      m_actAspList.erase(it);
      found = true;
      break;
    }
  }

  if(found) 
  {
    for(it=m_peerActAspList.begin(); it != m_peerActAspList.end(); ++it)
    { 
      if(((*it).pspId == inactAsp.pspId) && 
        ((*it).m3uaLsapId == inactAsp.m3uaLsapId))
      {
        m_peerActAspList.erase(it);
        break;
      }
    } 
  }

  logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::delActvAsp");
  return retVal;
}


int 
INGwSmBlkConfig::delM3uaAssocUp(M3uaAssocDown &delM3ua)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::delM3uaAssocUp");
	int retVal =1;
	bool found = false;

	if(m_assocUpList.size() == 0){
		logger.logMsg (TRACE_FLAG, 0, 
		"OUT INGwSmBlkConfig::delM3uaAssocUp, No entry in map");
		return retVal;
	}

	M3uaAssocUpSeq::iterator it;
	for(it=m_assocUpList.begin(); it != m_assocUpList.end(); ++it) 
	{
		if((*it).assocId == delM3ua.assocId) 
		{
			m_assocUpList.erase(it);
			found = true;
			break;
		}
	}

	if(found) 
	{
		for(it=m_peerAssocUpList.begin(); it != m_peerAssocUpList.end(); ++it)
  	{
			if((*it).assocId == delM3ua.assocId)
   	 	{
				m_peerAssocUpList.erase(it);
				break;
   	 	}
  	}
	}

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::m_assocUpList");
	return retVal;
}

M3uaAssocUpSeq* 
INGwSmBlkConfig::getM3uaAssocUpList()
{
	return &m_assocUpList;
}

M3uaAssocUpSeq* 
INGwSmBlkConfig::getPeerM3uaAssocUpList()
{
	return &m_peerAssocUpList;
}

U16
INGwSmBlkConfig::getAssocProcId(M3uaAssocUp &addM3ua)
{
  EpSeq* epList = getEpList();
  EpSeq::iterator it;
  U16 procId = 0;
  
  for(it=epList->begin(); it != epList->end(); ++it) {
  
    if ((*it).m3uaLsapId == addM3ua.m3uaLsapId) {
      logger.logMsg (ALWAYS_FLAG, 0, 
             "getAssocProcId()::assocId <%d> m3uaLsapId<%d> sctpProcId<%d>",
             addM3ua.assocId, (*it).m3uaLsapId, (*it).sctpProcId);
      procId = (*it).sctpProcId;
      break;
    }
  }

  if (procId == 0) {
      logger.logMsg (ERROR_FLAG, 0, 
        "getAssocProcId()::m3uaLsapId mismatch between endpoint and assoc "
        "for assocId <%d> m3uaLsapId<%d>",
        addM3ua.assocId, addM3ua.m3uaLsapId);
  }
  return procId;
}


U16
INGwSmBlkConfig::getAssocProcId_Aspup(M3uaAspUp &addM3ua)
{
  EpSeq* epList = getEpList();
  EpSeq::iterator it;
  U16 procId = 0;
  
  for(it=epList->begin(); it != epList->end(); ++it) {
  
    if ((*it).m3uaLsapId == addM3ua.m3uaLsapId) {
      logger.logMsg (ALWAYS_FLAG, 0, 
             "getAssocProcId_Aspup()::m3uaLsapId<%d> sctpProcId<%d>",
              (*it).m3uaLsapId, (*it).sctpProcId);
      procId = (*it).sctpProcId;
      break;
    }
  }

  if (procId == 0) {
      logger.logMsg (ERROR_FLAG, 0, 
        "getAssocProcId_Aspup()::m3uaLsapId mismatch between endpoint and assoc "
        "for  m3uaLsapId<%d>",
        addM3ua.m3uaLsapId);
  }
  return procId;
}

U16
INGwSmBlkConfig::getAssocProcId_Aspactv(M3uaAspAct &addM3ua)
{
  EpSeq* epList = getEpList();
  EpSeq::iterator it;
  U16 procId = 0;
  
  for(it=epList->begin(); it != epList->end(); ++it) {
  
    if ((*it).m3uaLsapId == addM3ua.m3uaLsapId) {
      logger.logMsg (ALWAYS_FLAG, 0, 
             "getAssocProcId_Aspactv():: m3uaLsapId<%d> sctpProcId<%d>",
              (*it).m3uaLsapId, (*it).sctpProcId);
      procId = (*it).sctpProcId;
      break;
    }
  }

  if (procId == 0) {
      logger.logMsg (ERROR_FLAG, 0, 
        "getAssocProcId_Aspactv()::m3uaLsapId mismatch between endpoint and assoc "
        "for  m3uaLsapId<%d>",
         addM3ua.m3uaLsapId);
  }
  return procId;
}



// This method is used to mark if Enabling of Node has been performed 
// for peer proc id. Based on this PEER_DOWN shall decide whether 
// Disabling of node would be needed or not. 
// Scenario 1: Active INC is UP and Standby INC is DOWN for first time.
// Scenario 2: ACtive INC and Standby INC comes UP first Time
void
INGwSmBlkConfig::updatePeerEnabled(bool flag)
{
	m_peerEnabled = flag;
}

bool
INGwSmBlkConfig::isPeerEnabled()
{
	return m_peerEnabled;
}

bool
INGwSmBlkConfig::isSctpTuclEnabled()
{
	return m_isSctpTuclEnabled;
}

void
INGwSmBlkConfig::setSctpTuclEnabled(bool flag)
{
	logger.logMsg(ALWAYS_FLAG, 0, 
         "setSctpTuclEnabled() <%d>", flag);
	m_isSctpTuclEnabled = flag;
}

void
INGwSmBlkConfig::initializePeerNode(int aSpStIt)
{
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::initializePeerNode()");

	INGwSmConfigQMsg  *lqueueMsg = NULL;
	Ss7SigtranSubsReq *req       = NULL;

  INGwSmWrapper *lpSmWrapper = 
		INGwTcapProvider::getInstance().getSmWrapperPtr() ;

	req = new Ss7SigtranSubsReq;

	req->cmd_type  = INITIALIZE_ENA_LINK;
	lqueueMsg      =  new INGwSmConfigQMsg;
  lqueueMsg->req = req;
  lqueueMsg->src = BP_AIN_SM_SRC_CCM;
  lqueueMsg->from= 2;

	lpSmWrapper->postMsg(lqueueMsg,true);
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::initializePeerNode()");
}


void
INGwSmBlkConfig::enaSgtrnLyrs(int from, int action)
{
  LogINGwTrace(false, 0, "IN INGwSmBlkConfig::enaSgtrnLyrs()");

	INGwSmConfigQMsg  *lqueueMsg = NULL;
	Ss7SigtranSubsReq *req       = NULL;

  INGwSmWrapper *lpSmWrapper = 
		INGwTcapProvider::getInstance().getSmWrapperPtr() ;

	req = new Ss7SigtranSubsReq;

	req->cmd_type   = ENA_SGTRN_LYRS;
	lqueueMsg       =  new INGwSmConfigQMsg;
  lqueueMsg->req  = req;
  lqueueMsg->src  = BP_AIN_SM_SRC_CCM;
  lqueueMsg->from = from;

	lpSmWrapper->postMsg(lqueueMsg,true);
  LogINGwTrace(false, 0, "OUT INGwSmBlkConfig::enaSgtrnLyrs()");
}


void 
INGwSmBlkConfig::log()
{
	if(m_nwList.size() == 0)
	{
		logger.logMsg(ALWAYS_FLAG, 0, 
		"+VER+INGwSmBlkConfig::log No network Information Configured");
		return;
	}

	std::ostringstream msg;
	msg << dumpNw();
	msg << "---------------------------------" << endl;
	msg << dumpUserPart();
	msg << "---------------------------------" << endl;
	msg << dumpLinkset();
	msg << "---------------------------------" << endl;
	msg << dumpLink();
	msg << "---------------------------------" << endl;
	msg << dumpRoute();
	msg << "---------------------------------" << endl;
	msg << dumpLocalSsn();
	msg << "---------------------------------" << endl;
	msg << dumpRule();
	msg << "---------------------------------" << endl;
	msg << dumpAddrMap();
	msg << "---------------------------------" << endl;
	msg << dumpEp();
	msg << "---------------------------------" << endl;
	msg << dumpAs();
	msg << "---------------------------------" << endl;
	msg << dumpAsp();
	msg << "---------------------------------" << endl;
	msg << dumpAssocUp();
	msg << "---------------------------------" << endl;
	msg << dumpActvAsp();
	msg << "---------------------------------" << endl;
  INGwTcapMsgLogger::getInstance().dumpMsg((char *)msg.str().c_str(), 100);
}

string 
INGwSmBlkConfig::dumpNw()
{
	std::ostringstream msg;
	for(int i=0; i<m_nwList.size(); ++i)
	{
		msg << "\nNetwork Infor [" << (i+1) << "]" << endl;
		msg << "NwId          :" << m_nwList[i].nwId    << endl;
		msg << "Variant       :" << m_nwList[i].variant << endl;
		msg << "SpcBroadcastOn:" << 
						string((1 == m_nwList[i].spcBroadcastOn)?"TRUE":"FALSE") << endl;
		msg << "DefaultPc     :" << m_nwList[i].defaultPc    		<< endl;
		msg << "NmbSpc        :" << (int)m_nwList[i].nmbSpcs 		<< endl;

		msg << "SelfPc List   :" << endl;
		for(int j=0; j < m_nwList[i].nmbSpcs; ++j)
			msg << "\t" << "SelfPc [" << j << "] : " << m_nwList[i].selfPc[j] << endl;

		msg << "NiInd         :" << (int)m_nwList[i].niInd 			<< endl;
		msg << "SubService    :" << (int)m_nwList[i].subService << endl;

		msg << "Nw App List   :" << "\n\t" <<  "nwkApp[";

		for(int k=0; k < LIT_MAX_PSP; ++k)
			msg << m_nwList[i].nwkApp[k] << "-";

		msg << "]" << endl;
		msg << "ssf           :" << (int)m_nwList[i].ssf 	  << endl;
		msg << "dpcLen        :" << (int)m_nwList[i].dpcLen << endl;
		msg << "slsLen        :" << (int)m_nwList[i].slsLen << endl;
		msg << "suSwtch       :" << m_nwList[i].suSwtch 	  << endl;
		msg << "su2Swtch      :" << m_nwList[i].su2Swtch 	  << endl;
	}

	return msg.str();
}

string 
INGwSmBlkConfig::dumpUserPart()
{
	std::ostringstream msg;

	if(m_userPartList.empty())
		msg << "Userpart Not Configured" << endl;

	for(int i=0; i < m_userPartList.size(); ++i)
	{
		msg << "USER PART [" << (i+1) << "]" << endl;
		msg << "NwId		    : " << m_userPartList[i].nwId << endl;
		msg << "UserPartType: " << 
					string((1 == m_userPartList[i].userPartType)?"MTP3_USER":"M3UA_USER") 					<<endl;
		msg << "Ssf         : " << (int)m_userPartList[i].ssf     << endl;
		msg << "LnkType     : " << (int)m_userPartList[i].lnkType << endl;
		msg << "UpSwtch     : " << (int)m_userPartList[i].upSwtch << endl;
		msg << "SuType      : " << (int)m_userPartList[i].suType  << endl;
		msg << "Mtp3UsapId  : " << m_userPartList[i].mtp3UsapId   << endl;
		msg << "M3uaUsapId  : " << m_userPartList[i].m3uaUsapId   << endl;
		msg << "SccpLsapId  : " << m_userPartList[i].sccpLsapId   << endl;
	}

	return msg.str();
}

string 
INGwSmBlkConfig::dumpLinkset()
{
	std::ostringstream msg;

	if(m_linksetList.empty())
		msg << "Linkset Not Configured" << endl;

	for(int i=0; i < m_linksetList.size(); ++i)
	{
		msg << "LINKSET [" << (i+1) << "]" << endl;
		msg << "LnkSetId     : " << m_linksetList[i].lnkSetId 			 << endl;		
		msg << "lnkSetType   : " << (int)m_linksetList[i].lnkSetType << endl;
		msg << "AdjDpc       : " << m_linksetList[i].adjDpc          << endl;
		msg << "NmbActLnkReqd: " << m_linksetList[i].nmbActLnkReqd   << endl;
		msg << "NmbCmbLnkSet : " << m_linksetList[i].nmbCmbLnkSet    << endl;
		msg << "Combined LinkSet List " << endl;

		for(int j=0; j < m_linksetList[i].nmbCmbLnkSet; ++j)
		{
			msg << "\tElement [" << (j+1) << "]" << endl;
			msg << "\t\t" << "CmbLnkSetId : " << 
											m_linksetList[i].cmbLnkSet[j].cmbLnkSetId << endl;
			msg << "\t\t" << "LnkSetPrior : " << 
											m_linksetList[i].cmbLnkSet[j].cmbLnkSetId << endl;
			msg << "\t\t" << "NmbPrefLinks: " << 
											m_linksetList[i].cmbLnkSet[j].nmbPrefLinks << endl;
			msg << "\t\t" << "Preferred Link List " << endl;
			msg << "\t\t\t" << "Element[" ;

			for(int k=0; k < m_linksetList[i].cmbLnkSet[j].nmbPrefLinks; ++k)
			{
				msg << m_linksetList[i].cmbLnkSet[j].prefLnkId[k] << "-";
			}
			msg << "]" << endl;
		}
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpLink()
{
	std::ostringstream msg;

	if(m_linkList.empty())
		msg << "Link Not Configured" << endl;

	for(int i=0; i < m_linkList.size(); ++i)
	{
		msg << "LINK [" << (i+1) << "]" << endl;
		msg << "LnkId     : " << m_linkList[i].lnkId        << endl;
		msg << "LnkSetId  : " << m_linkList[i].lnkSetId     << endl;
		msg << "Opc       : " << m_linkList[i].opc          << endl;
		msg << "AdjDpc    : " << m_linkList[i].adjDpc       << endl;
		msg << "PhysPort  : " << m_linkList[i].physPort     << endl;
		msg << "TimeSlot  : " << m_linkList[i].timeSlot     << endl;
		msg << "Slc       : " << (int)m_linkList[i].slc     << endl;
		msg << "Ssf       : " << (int)m_linkList[i].ssf     << endl;
		msg << "DpcLen    : " << (int)m_linkList[i].dpcLen  << endl;
		msg << "LnkType   : " << (int)m_linkList[i].lnkType << endl;
		msg << "LnkPrior  : " << (int)m_linkList[i].lnkPrior<< endl;
		msg << "Mtp2UsapId: " << m_linkList[i].mtp2UsapId   << endl;
		msg << "Mtp3LsapId: " << m_linkList[i].mtp3LsapId   << endl;
		msg << "Mtp2ProcId: " << m_linkList[i].mtp2ProcId   << endl;

	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpRoute()
{
	std::ostringstream msg;

	if(m_routeList.empty())
		msg << "Route Not Configured" << endl;

	for(int i=0; i<m_routeList.size(); ++i)
	{
		msg << "ROUTE  [" << (i+1) << "]" << endl;
		msg << "Dpc           : " <<  m_routeList[i].dpc						<< endl;
		msg << "SwtchType     : " <<  (int)m_routeList[i].swtchType << endl;
		msg << "SpType        : " <<  (int)m_routeList[i].spType 	  << endl;
		msg << "UpSwtch       : " <<  (int)m_routeList[i].upSwtch   << endl;
		msg << "CmbLnkSetId   : " <<  m_routeList[i].cmbLnkSetId    << endl;
		msg << "Dir           : " <<  (int)m_routeList[i].dir       << endl;
		msg << "RteToAdjSp    : " <<  (int)m_routeList[i].rteToAdjSp<< endl;
		msg << "Ssf           : " <<  (int)m_routeList[i].ssf       << endl;
		msg << "Swtch	        : " <<  (int)m_routeList[i].swtch 		<< endl;
		msg << "Status        : " <<  m_routeList[i].status 				<< endl;
#if (SS7_ANS96 || SS7_BELL05)
		msg << "ReplicatedMode: " <<  m_routeList[i].replicatedMode << endl;
#endif
		msg << "PreferredOpc	: " <<  m_routeList[i].preferredOpc 	<< endl;
		msg << "NSapId        : " <<  m_routeList[i].nSapId 			  << endl;
		msg << "NmbBpc        : " <<  m_routeList[i].nmbBpc 			  << endl;

		msg << "Backup Pointcode List " << endl;
		for(int j=0; j < m_routeList[i].nmbBpc; ++j)
		{
			msg << "\tBPC[" << (j+1) << "] Pc: " << m_routeList[i].bpcList[j].bpc 
					<< " Prior: " << (int)m_routeList[i].bpcList[j].prior << endl;
		}

		msg << "nmbSsns       : " <<  (int)m_routeList[i].nmbSsns << endl;

		msg << " SSN for DPC " << endl;
		for(int k=0; k < m_routeList[i].nmbBpc; ++k)
		{
			msg << "SSN[" << (k+1) << "]" << endl;
			msg << "\tSsn       : " << (int)m_routeList[i].ssnList[k].ssn << endl;
			msg << "\tStatus    : " << (int)m_routeList[i].ssnList[k].status << endl;
			msg << "\tNmbBpc    : " << m_routeList[i].ssnList[k].nmbBpc  << endl;
			msg << "\tBackup Point Code" << endl;

			for(int jj=0; jj < m_routeList[i].ssnList[k].nmbBpc; ++jj)
			{
        msg << "Bpc [" << (jj+1) << "] Pc: " << 
            m_routeList[i].ssnList[k].bpcList[jj].bpc << 
            " Prior: " << m_routeList[i].ssnList[k].bpcList[jj].prior << endl;
			}

			msg << "\tnmbConPc	: " << m_routeList[i].ssnList[k].nmbConPc << endl;

			msg << "\tConcerned Point Code " << endl;
			for(int ii=0; ii < m_routeList[i].ssnList[k].nmbConPc; ++ii)
			{
				msg << "\t\tPC: " << m_routeList[i].ssnList[k].conPc[ii] << endl;
			}
		}
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpLocalSsn()
{
	std::ostringstream msg;
		
	if(m_localSsnList.empty())
		msg << "Local Ssn Not Configured" << endl;

	for(int i=0; i < m_localSsnList.size(); ++i)
	{
		msg << "LOCAL SSN [" << (i+1) << "]" << endl;
    msg << "NwId        : " << m_localSsnList[i].nwId       << endl;
    msg << "Ssn         : " << (int)m_localSsnList[i].ssn   << endl;
    msg << "Swtch       : " << (int)m_localSsnList[i].swtch << endl;
    msg << "SccpUsapId  : " << m_localSsnList[i].sccpUsapId << endl;
    msg << "TcapLsapId  : " << m_localSsnList[i].tcapLsapId << endl;
    msg << "TcapUsapId  : " << m_localSsnList[i].tcapUsapId << endl;
    msg << "NmbBpc      : " << m_localSsnList[i].nmbBpc     << endl;
    
    msg << "Backup Point Code " << endl;
    for(int j=0; j < m_localSsnList[i].nmbBpc; ++j)
    {
      msg << "Bpc [" <<(j+1) << "] Pc: " << m_localSsnList[i].bpcList[j].bpc
          << " Prior: " << m_localSsnList[i].bpcList[j].prior << endl;
    }

    msg << "nmbConPc    : " << m_localSsnList[i].nmbConPc   << endl;

    msg << "Concerned Point Code " << endl;
    for(int k=0; k < m_localSsnList[i].nmbConPc; ++k)
    {
      msg << "Cpc [" << (k+1) << "] Pc: " << m_localSsnList[i].conPc[k]
          << endl;
    }
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpRule()
{
	std::ostringstream msg;
	
	if(m_gtRuleList.empty())
		msg << "Gt Rule List Not Configured" << endl;

	for(int i=0; i<m_gtRuleList.size(); ++i)
	{
		msg << "GT RULE [" << (i+1) << "]" << endl;
    msg << "NwId        : " << m_gtRuleList[i].nwId             << endl;
    msg << "Sw          : " << m_gtRuleList[i].sw               << endl;
    msg << "FormatPres  : " << (int)m_gtRuleList[i].formatPres  << endl;
    msg << "Format      : " << (int)m_gtRuleList[i].format      << endl;
    msg << "OddEven     : " << (int)m_gtRuleList[i].oddEven     << endl;
    msg << "OddEvenPres : " << (int)m_gtRuleList[i].oddEvenPres << endl;
    msg << "NatAddr     : " << (int)m_gtRuleList[i].natAddr     << endl;
    msg << "NatAddrPres : " << (int)m_gtRuleList[i].natAddrPres << endl;
    msg << "TType       : " << (int)m_gtRuleList[i].tType       << endl;
    msg << "TTypePres   : " << (int)m_gtRuleList[i].tTypePres   << endl;
    msg << "NumPlan     : " << (int)m_gtRuleList[i].numPlan     << endl;
    msg << "NumPlanPres : " << (int)m_gtRuleList[i].numPlanPres << endl;
    msg << "EncSch      : " << (int)m_gtRuleList[i].encSch      << endl;
    msg << "EncSchPres  : " << (int)m_gtRuleList[i].encSchPres  << endl;
    msg << "NmbActns    : " << (int)m_gtRuleList[i].nmbActns    << endl;

    for(int j=0; j < m_gtRuleList[i].nmbActns; ++j)
    {
       msg << "Action [" << (j+1) << "]" << endl;
       msg << "\tType      : " << (int)m_gtRuleList[i].actn[j].type     << endl;
       msg << "\tNmbActns  : " << (int)m_gtRuleList[i].actn[j].nmbActns  <<endl;
       msg << "\tStartDigit: " << (int)m_gtRuleList[i].actn[j].startDigit<<endl;
       msg << "\tEndDigit  : " << (int)m_gtRuleList[i].actn[j].endDigit  <<endl;
    }
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpAddrMap()
{
	std::ostringstream msg;
	if(m_gtAddrMapSeq.empty())
		msg << "Gt Addr Map Not Conrfigured" << endl;

	for(int i=0; i < m_gtAddrMapSeq.size(); ++i)
	{
		msg << "GT ADDRESS MAP [" << (i+1) << "]" << endl;
    msg << "NwId        : " << m_gtAddrMapSeq[i].nwkId          << endl;
    msg << "Sw          : " << m_gtAddrMapSeq[i].sw             << endl;
    msg << "Format      : " << (int)m_gtAddrMapSeq[i].format    << endl;
    msg << "OddEven     : " << (int)m_gtAddrMapSeq[i].oddEven   << endl;
    msg << "NatAddr     : " << (int)m_gtAddrMapSeq[i].natAddr   << endl;
    msg << "TType       : " << (int)m_gtAddrMapSeq[i].tType     << endl;
    msg << "NumPlan     : " << (int)m_gtAddrMapSeq[i].numPlan   << endl;
    msg << "EncSch      : " << (int)m_gtAddrMapSeq[i].encSch    << endl;
    msg << "GtDigLen    : " << (int)m_gtAddrMapSeq[i].gtDigLen  << endl;
    msg << "GtDigits    : " << (int)m_gtAddrMapSeq[i].gtDigits  << endl;
    msg << "ReplGt      : " << (int)m_gtAddrMapSeq[i].replGt    << endl;
    msg << "Mode        : " << (int)m_gtAddrMapSeq[i].mode      << endl;
    msg << "OutNwId     : " << m_gtAddrMapSeq[i].outNwId        << endl;
    msg << "Action " << endl;
    msg << "\tType      : " << (int)m_gtAddrMapSeq[i].actn.type      <<endl;
    msg << "\tNmbActns  : " << (int)m_gtAddrMapSeq[i].actn.nmbActns  <<endl;
    msg << "\tStartDigit: " << (int)m_gtAddrMapSeq[i].actn.startDigit<<endl;
    msg << "\tEndDigit  : " << (int)m_gtAddrMapSeq[i].actn.endDigit  <<endl;
    msg << "numEntity   : " << (int)m_gtAddrMapSeq[i].numEntity      <<endl;

    msg << "Out Address " << endl;
    for(int j=0; j < m_gtAddrMapSeq[i].numEntity; ++j)
    {
      msg << "OutAddr [" << (j+1) << "]" << endl;
      msg << "\tSpHdrOpt  : "<<(int)m_gtAddrMapSeq[i].outAddr[j].spHdrOpt<<endl;
      msg << "\tSwtch     : "<<m_gtAddrMapSeq[i].outAddr[j].swtch        <<endl;
      msg << "\tSsf       : "<<(int)m_gtAddrMapSeq[i].outAddr[j].ssf     <<endl;
      msg << "\tNiInd     : "<<(int)m_gtAddrMapSeq[i].outAddr[j].niInd   <<endl;
      msg << "\tRtgInd    : "<<(int)m_gtAddrMapSeq[i].outAddr[j].rtgInd  <<endl;
      msg << "\tSsnInd    : "<<(int)m_gtAddrMapSeq[i].outAddr[j].ssnInd  <<endl;
      msg << "\tSsn       : "<<(int)m_gtAddrMapSeq[i].outAddr[j].ssn     <<endl;
      msg << "\tPcInd     : "<<(int)m_gtAddrMapSeq[i].outAddr[j].pcInd   <<endl;
      msg << "\tPc        : "<<m_gtAddrMapSeq[i].outAddr[j].pc           <<endl;
      msg << "\tFormat    : "<<(int)m_gtAddrMapSeq[i].outAddr[j].format  <<endl;
      msg << "\tOddEven   : "<<(int)m_gtAddrMapSeq[i].outAddr[j].oddEven <<endl;
      msg << "\tTType     : "<<(int)m_gtAddrMapSeq[i].outAddr[j].tType   <<endl;
      msg << "\tNatAddr   : "<<(int)m_gtAddrMapSeq[i].outAddr[j].natAddr <<endl;
      msg << "\tNumPlan   : "<<(int)m_gtAddrMapSeq[i].outAddr[j].numPlan <<endl;
      msg << "\tEncSch    : "<<(int)m_gtAddrMapSeq[i].outAddr[j].encSch  <<endl;
      msg << "\tGtDigLen  : "<<(int)m_gtAddrMapSeq[i].outAddr[j].gtDigLen<<endl;
      msg << "\tGtDigits  : "<<m_gtAddrMapSeq[i].outAddr[j].gtDigits     <<endl;
    }
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpEp()
{
	std::ostringstream msg;

	if(m_epList.empty())
		msg << "EndPoint Not Configured" << endl;

	for(int i=0; i < m_epList.size(); ++i)
	{
		msg << "ENDPOINT [" << (i+1) << "]" << endl;
		msg << "EndPointid : " << (int)m_epList[i].endPointid  << endl;
		msg << "SrcPort    : " <<  m_epList[i].srcPort 				 << endl;
		msg << "SctpProcId : " <<  m_epList[i].sctpProcId 		 << endl;
		msg << "SctpLsapId : " <<  m_epList[i].sctpLsapId 		 << endl;
		msg << "SctpUsapId : " <<  m_epList[i].sctpUsapId 		 << endl;
		msg << "M3uaLsapId : " <<  m_epList[i].m3uaLsapId 		 << endl;
		msg << "TuclUsapId : " <<  m_epList[i].tuclUsapId 		 << endl;
		msg << "NmbAddrs   : " <<  (int)m_epList[i].nmbAddrs   << endl;

		msg << "IP Addresses " << endl;
		for(int j=0; j < m_epList[i].nmbAddrs; ++j)
		{
			msg << "\t" << "IPAddress [" << (j+1) << "]";
			if(m_epList[i].nAddr[j].type == CM_TPTADDR_IPV4)
			{
				msg << " Type: IPV4 IP: " << m_epList[i].nAddr[j].u.ipv4NetAddr << endl;
			}
			else {
				msg << " Type: IPV6 IP: " << m_epList[i].nAddr[j].u.ipv6NetAddr << endl;
			}
		}
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpAs()
{
	std::ostringstream msg;

	if(m_asList.empty())
		msg << "As Not Configured" << endl;

	for(int i=0; i < m_asList.size(); ++i)
	{
		msg << "AS [" << (i+1) << "]" << endl;
		msg << "PsId					: " << m_asList[i].psId						  << endl;
		msg << "NwkId					: " << (int)m_asList[i].nwkId				<< endl;
		msg << "RoutCtx				: " << m_asList[i].routCtx					<< endl;
		msg << "Mode					: " << (int)m_asList[i].mode				<< endl;
		msg << "LoadShareMode	: " << (int)m_asList[i].loadShareMode<< endl;
		msg << "NmbActPspReqd	: " << m_asList[i].nmbActPspReqd		<< endl;
		msg << "LFlag					: " << (int)m_asList[i].lFlag				<< endl;
		msg << "RtType				: " << (int)m_asList[i].rtType			<< endl;
		msg << "DpcMask				: " << m_asList[i].dpcMask					<< endl;
		msg << "Dpc						: " << m_asList[i].dpc						  << endl;
		msg << "OpcMask				: " << m_asList[i].opcMask					<< endl;
		msg << "Opc						: " << m_asList[i].opc						  << endl;
		msg << "SlsMask				: " << (int)m_asList[i].slsMask		  << endl;
		msg << "Sls						: " << (int)m_asList[i].sls					<< endl;
		msg << "SioMask				: " << (int)m_asList[i].sioMask			<< endl;
		msg << "Sio						: " << (int)m_asList[i].sio					<< endl;
		msg << "NmbPsp				: " << m_asList[i].nmbPsp						<< endl;

		msg << "PSP List " << "\n\t" << "Psp [" << endl;
		for(int j=0; j < m_asList[i].nmbPsp; ++j)
		{
			msg << m_asList[i].psp[j] << "-";
		}
		msg << "]" << endl;

		msg << "PSP Ep List " << "\n\t" << "PspEp [" << endl;
		for(int k=0; k < m_asList[i].nmbPsp; ++k)
		{
			msg << "NmbEp   : " << m_asList[i].pspEpLst[k].nmbEp << endl;

			for(int l=0; l < m_asList[i].pspEpLst[k].nmbEp; ++l)
			{
				msg << "\tEndpIds :" << m_asList[i].pspEpLst[k].endpIds[l] << endl;
			}
		}
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpAsp()
{
	std::ostringstream msg;

	if(m_aspList.empty())
		msg << "Asp Not Configured" << endl;

	for(int i=0; i < m_aspList.size(); ++i)
	{
		msg << "ASP [" << (i+1) << "]" << endl;
		msg << "PspId				: " << m_aspList[i].pspId						<< endl;
		msg << "NwkId				: " << m_aspList[i].nwkId						<< endl;
		msg << "PspType			: " << (int)m_aspList[i].pspType		<< endl;
		msg << "IpspMode		: " << (int)m_aspList[i].ipspMode		<< endl;
		msg << "IncludeRC		: " << (int)m_aspList[i].includeRC	<< endl;
		msg << "CfgForAllLps: " << (int)m_aspList[i].cfgForAllLps<< endl;
		msg << "NmbAddr			: " << (int)m_aspList[i].nmbAddr		<< endl;

		msg << "IP Addresses " << endl;
		for(int j=0; j < m_aspList[i].nmbAddr; ++j)
		{
			msg << "\t" << "IPAddress [" << (j+1) << "]";
			if(m_aspList[i].addr[j].type == CM_TPTADDR_IPV4)
			{
				msg << " Type: IPV4 IP: " << m_aspList[i].addr[j].u.ipv4NetAddr << endl;
			}
			else {
				msg << " Type: IPV6 IP: " << m_aspList[i].addr[j].u.ipv6NetAddr << endl;
			}
		}
		msg << "DstPort			: " << m_aspList[i].dstPort << endl;
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpAssocUp()
{
	std::ostringstream msg;
	if(m_assocUpList.empty())
		msg << "M3uaAssocUp Not Configured" << endl;

	for(int i=0; i < m_assocUpList.size(); ++i)
	{
		msg << "M3UAASSOCUP [" << (i+1) << "]" << endl;
		msg << "AssocId    : " << m_assocUpList[i].assocId    << endl;
		msg << "PspId      : " << m_assocUpList[i].pspId      << endl;
		msg << "EndPointId : " << m_assocUpList[i].endPointId << endl;
		msg << "M3uaLsapId : " << m_assocUpList[i].m3uaLsapId << endl;
	}
	return msg.str();
}

string 
INGwSmBlkConfig::dumpActvAsp()
{
	std::ostringstream msg;
	if(m_actAspList.size() == 0)
		msg << "No ASPs are active" << endl;

	for(int i=0; i < m_actAspList.size(); ++i)
	{
    msg << "ACTVASP [" << (i+1) << "]" << endl;
		//msg << "PsId    : " << m_actAspList[i].psId    << endl;
		msg << "PspId      : " << m_actAspList[i].pspId      << endl;
		//msg << "EndPointId : " << m_actAspList[i].endPointId << endl;
		msg << "M3uaLsapId : " << m_actAspList[i].m3uaLsapId << endl;
	}

  if(m_peerActAspList.size() == 0)
		msg << "No Peer ASPs are active" << endl;

	for(int i=0; i < m_peerActAspList.size(); ++i)
	{
    msg << "PEERACTVASP [" << (i+1) << "]" << endl;
		//msg << "PsId    : " << m_peerActAspList[i].psId    << endl;
		msg << "PspId      : " << m_peerActAspList[i].pspId      << endl;
		//msg << "EndPointId : " << m_peerActAspList[i].endPointId << endl;
		msg << "M3uaLsapId : " << m_peerActAspList[i].m3uaLsapId << endl;
	}

	return msg.str();
}


void
INGwSmBlkConfig::updateState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateState()");

	switch(stk.cmdType)
	{
		case OPR_STATUS : updateOperState   (stk); break;
		case ADD_NETWORK: updateNetworkState(stk); break;
		case ADD_LINKSET: updateLinksetState(stk); break;
		case ADD_LINK   : updateLinkState   (stk); break;
		case ADD_GTRULE : updateGtRuleState (stk); break;
		case ASSOC_UP   : updateAssocUpState(stk); break;
		case ADD_ROUTE  : updateRouteState  (stk); break;
		case ADD_AS     : updateAsState     (stk); break;
		case ADD_ASP    : updateAspState    (stk); break;
		case ADD_LOCAL_SSN: updateSsnState  (stk); break;
		case ADD_ENDPOINT : updateEpState   (stk); break;
		case ADD_USERPART : updateUserPartState (stk); break;
		case ADD_GTADDRMAP: updateGtAddrMapState(stk); break;
		default:
			logger.logMsg(ERROR_FLAG, 0, 
			"Erroneous Command Type received from Peer");
	}
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateState()");
}

void 
INGwSmBlkConfig::updateNetworkState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateNetworkState");

	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateNetworkState");
}

void 
INGwSmBlkConfig::updateLinkState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateLinkState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateLinkState");
}

void 
INGwSmBlkConfig::updateUserPartState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateUserPartState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateUserPartState");
}

void 
INGwSmBlkConfig::updateSsnState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateSsnState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateSsnState");
}

void 
INGwSmBlkConfig::updateRouteState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateRouteState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateRouteState");
}

void 
INGwSmBlkConfig::updateLinksetState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateLinksetState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateLinksetState");
}

void 
INGwSmBlkConfig::updateGtRuleState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateGtRuleState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateGtRuleState");
}

void 
INGwSmBlkConfig::updateGtAddrMapState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateGtAddrMapState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateGtAddrMapState");
}

void 
INGwSmBlkConfig::updateEpState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateEpState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateEpState");
}

void 
INGwSmBlkConfig::updateAsState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateAsState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateAsState");
}

void 
INGwSmBlkConfig::updateAspState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateAspState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateAspState");
}

void 
INGwSmBlkConfig::updateAssocUpState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateAssocUpState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateAssocUpState");
}

void 
INGwSmBlkConfig::updateOperState(StkConfigStatus &stk)
{
	logger.logMsg (TRACE_FLAG, 0, "IN INGwSmBlkConfig::updateOperState");
	logger.logMsg (TRACE_FLAG, 0, "OUT INGwSmBlkConfig::updateOperState");
}

S16 
INGwSmBlkConfig::getProtoTypeForNwId(U16 nwId)
{
  S16 protoType = 0;
	bool found = false;
	AddNetworkSeq::iterator it;
	for(it=m_nwList.begin(); it != m_nwList.end(); ++it) 
	{
		if((*it).nwId == nwId) 
		{
			found = true;
			protoType = (*it).protoType;
			break;
		}
	}

	if(found == false)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwSmBlkConfig::getProtoTypeForNwId FATAL couldn't find entry for nwId:%d",
		nwId);
	}

  return protoType;
}
