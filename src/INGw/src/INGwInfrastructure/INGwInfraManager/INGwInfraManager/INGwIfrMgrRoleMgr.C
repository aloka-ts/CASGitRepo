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
//     File:     INGwIfrMgrRoleMgr.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");

#include <Util/imAlarmCodes.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>

#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrWorkerThread.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraUtil/INGwIfrUtlLock.h>
#include <INGwFtPacket/INGwFtPktRoleMsg.h>
#include <INGwTcapProvider/INGwTcapProvider.h>

using namespace std;
#include <sstream>

INGwIfrMgrRoleMgr* INGwIfrMgrRoleMgr::m_RoleMgrSelf = NULL;

//Singleton

INGwIfrMgrRoleMgr& 
INGwIfrMgrRoleMgr::getInstance()
{
	if (NULL == m_RoleMgrSelf)
	{
		m_RoleMgrSelf = new INGwIfrMgrRoleMgr();
		m_RoleMgrSelf->initialize();
	}
	return *m_RoleMgrSelf;
}

//
//When we need to start the role resolution
//First function to be called. Self role shd be unknown and
//role resolution shd be unknown
//
int
INGwIfrMgrRoleMgr::startRoleResolution()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::startRoleResolution");

  INGwIfrUtlWGuard(&m_RoleLock);

	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
										"INGwIfrMgrRoleMgr::startRoleResolution "
										"Starting role resolution process. Role parameters [%s]",
										lLogMsg.c_str());
  int retVal = -1;
	if( (m_CurrentRole == Role_Unkown )&& 
		  (m_RoleResolutionState == RoleResoState_Unknown) )
  {
		if(enqueueRoleTimer() == 0)
		{
		  //m_RoleResolutionState = RoleResoState_TimerStarted;
			retVal = 0;
    }
		else
		{
		  logger.logINGwMsg(false, ERROR_FLAG, -1, 
		  									"INGwIfrMgrRoleMgr::startRoleResolution. "
		  									"ERROR : Failed to Start Role Timer.");
			retVal = -1;
		}
	}
	else
	{
		logger.logINGwMsg(false, ERROR_FLAG, -1,
											"INGwIfrMgrRoleMgr::startRoleResolution "
											"ERROR. Wrong state. Could not start role resolution process.");
		retVal = -1;
	}
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::startRoleResolution");
	return retVal;
}

//
//When connection with peer is extablished
//
int
INGwIfrMgrRoleMgr::peerUp()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::peerUp");

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 1");
  INGwIfrUtlWGuard(&m_RoleLock);

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 2");

	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
										"INGwIfrMgrRoleMgr::peerUp "
										"Recvd peerUp message by Role manager. Role parameters [%s]",
										lLogMsg.c_str());

  INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
																						 __FILE__, __LINE__,
																						 INGW_PEER_RESUME,
																						 "Peer Status Alarm", 0,
																						 " - Peer INGw is Up");

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 3");
  int retVal = -1;
	if(m_PeerState == PeerState_Up)
	{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 4");
		// May be we didn't recv peer down and recv peer up
		// so we need to handle it as per our state
		logger.logINGwMsg(false, ERROR_FLAG, -1, 
											"PEER is alreay UP but recv peerUp message by Role manager. "
											"Will continue with normal processing");
	}
	else
	{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 5");
		m_PeerState = PeerState_Up;
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 6");

	if( (m_CurrentRole == Role_Unkown )&& 
		  (m_RoleResolutionState == RoleResoState_Unknown) )
  {
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 7");
		if(enqueueRoleTimer() == 0)
		{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 8");
		  //m_RoleResolutionState = RoleResoState_TimerStarted;
			retVal = 0;
    }
		else
		{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 9");
		  logger.logINGwMsg(false, ERROR_FLAG, -1, 
		  									"INGwIfrMgrRoleMgr::peerUp. "
		  									"ERROR : Failed to Start Role Timer.");
			retVal = -1;
		}
  }
	else if(m_CurrentRole == Role_Active)  
	{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 11");
    // Wait for Active pending msg from peer, don't send now
    // We will send Active role at that point of time
/*
		logger.logINGwMsg(false, TRACE_FLAG, -1, 
											"Self is Active so Sending Active Role Msg to Peer.");

		// Send its Active state to peer
		INGwIfrPrParamRepository&  lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		INGwFtPktRoleMsg lRolePacket;
		lRolePacket.initialize(ACTIVE_ROLE_MSG_TYPE, 
													 lParamRepository.getSelfId(),
													 lParamRepository.getPeerId());
		INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
*/
/*
		//Inform Infra Manager that peer is now connected in our active state
		INGwIfrMgrManager::getInstance().handlePeerConnect();
*/
		retVal = 0;
  }
	else
	{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 12");
		retVal = 0;
		logger.logINGwMsg(false, TRACE_FLAG, -1, 
											"No Action taken for peerup at this state.");
	}

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerUp Check point 13");
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::peerUp");
	return retVal;
}

//
//When connection with peer is disconnected
//
int
INGwIfrMgrRoleMgr::peerDown()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::peerDown");

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 1");
  INGwIfrUtlWGuard(&m_RoleLock);

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 2");
  INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
																						 __FILE__, __LINE__,
																						 INGW_PEER_FAILURE,
																						 "Peer Status Alarm", 0,
																						 " - Peer INGw is Down");
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 3");
	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
								"INGwIfrMgrRoleMgr::peerDown "
								"Recvd peerdown message by Role manager. Role parameters [%s]",
										lLogMsg.c_str());

  int retVal = -1;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 4");
  m_PeerState = PeerState_Down; 
	INGwIfrPrParamRepository::getInstance().setPeerStatus(0, 0,"INGwIfrMgrRoleMgr::peerDown");

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 5");
	if(m_CurrentRole == Role_Active)
	{
    setSbyToActive(false);
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 6");
	  	logger.logINGwMsg(false, TRACE_FLAG, -1, 
	  										" Self Role Active. Peer state change to PeerState_Down");
	  	retVal = 0;
	}
	else
	{
    setSbyToActive(true);
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 7");
		switch(m_RoleResolutionState)
		{
			case RoleResoState_Unknown :
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 8");
			case RoleResoState_Completed :
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 9");
			case RoleResoState_TimerExpired :
			{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 10");
				break;
			}
			case RoleResoState_TimerStarted :
			{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 11");
				if(m_RoleTimerId)
				{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 12");
				  stopRoleTimer();
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 13");
					m_RoleTimerId = 0;
        }
				break;
			}
			case RoleResoState_ActivePending :
			{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 14");
				if(m_RoleResTimerId)
				{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 15");
				  stopRoleResponseTimer();
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 16");
					m_RoleResTimerId = 0;
				}
				break;
			}
		}

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 17");
    INGwIfrMgrManager::getInstance().setSelfMode(true);
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 18");
		m_CurrentRole = Role_Active;
    logger.logINGwMsg(false,ALWAYS_FLAG,0,
                      "peerDown(): m_CurrentRole set to Role_Active");
		m_RoleResolutionState = RoleResoState_Completed;
		retVal = 0;
	}

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"peerDown Check point 19");
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::peerDown");
	return retVal;
}

//
//When no peer is configured
// value of Peer state will be PeerState_No_Peer
//

int
INGwIfrMgrRoleMgr::setPeerState(t_PeerState p_PeerState)
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::setPeerState");
  INGwIfrUtlWGuard(&m_RoleLock);
	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
										"INGwIfrMgrRoleMgr::setPeerState "
										"Recvd setPeerState message by Role manager. Role parameters [%s]",
										lLogMsg.c_str());

  int retVal = -1;
	if(p_PeerState != PeerState_No_Peer)
	{
		//It is for handling No_peer configured case
		logger.logINGwMsg(false, ERROR_FLAG, -1, 
											" Peer state recv is not  PeerState_No_Peer "
											"so Not handling this state change");
	}

  m_PeerState = PeerState_No_Peer; 
	INGwIfrPrParamRepository::getInstance().setPeerStatus(0, 0,"INGwIfrMgrRoleMgr::setPeerState");

	retVal = 0;
	if(m_CurrentRole == Role_Active)
	{
		logger.logINGwMsg(false, TRACE_FLAG, -1, 
											" Self Role Active. Peer state change to PeerState_No_Peer");
	  LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::setPeerState")
	  return retVal;
	}
	else
	{
		switch(m_RoleResolutionState)
		{
			case RoleResoState_Unknown :
			case RoleResoState_Completed :
			case RoleResoState_TimerExpired :
			{
				break;
			}
			case RoleResoState_TimerStarted :
			{
				if(m_RoleTimerId)
				{
				  stopRoleTimer();
					m_RoleTimerId = 0;
        }
				break;
			}
			case RoleResoState_ActivePending :
			{
				if(m_RoleResTimerId)
				{
				  stopRoleResponseTimer();
					m_RoleResTimerId = 0;
				}
				break;
			}
		}

    INGwIfrMgrManager::getInstance().setSelfMode(true);
		m_CurrentRole = Role_Active;
    logger.logINGwMsg(false,ALWAYS_FLAG,0,
                      "setPeerState(): m_CurrentRole set to Role_Active");
		m_RoleResolutionState = RoleResoState_Completed;
	}
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::setPeerState");
	return retVal;
}

//
//When Peer Active message is recv by INGW Talk
//

int
INGwIfrMgrRoleMgr::handlePeerActiveMsg()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::handlePeerActiveMsg");
  INGwIfrUtlWGuard(&m_RoleLock);
	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
										"INGwIfrMgrRoleMgr::handlePeerActiveMsg "
										"Recvd Role Active message from peer . Role parameters [%s]",
										lLogMsg.c_str());

  int retVal = -1;
	if(m_CurrentRole == Role_Active)
	{
		logger.logINGwMsg(false, ALWAYS_FLAG, -1,
											"Recvd Role Active message but self state is Active"
											"QUITTING DELIBERATLY");


		exit(1);
		// Send its Active state to peer
		INGwIfrPrParamRepository&  lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		INGwFtPktRoleMsg lRolePacket;
		lRolePacket.initialize(ACTIVE_ROLE_MSG_TYPE, 
													 lParamRepository.getSelfId(),
													 lParamRepository.getPeerId());
		INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
		retVal = 0;
	}
	else if(m_CurrentRole == Role_Standby)
	{
		logger.logINGwMsg(false, ALWAYS_FLAG, -1,
											"Recvd Role Active message but self state is Stanby"
											"Sending Role Active Ack message to peer");

		// Send Active Ack msg to peer
		INGwIfrPrParamRepository&  lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		INGwFtPktRoleMsg lRolePacket;
		lRolePacket.initialize(ACTIVE_ROLE_ACK_MSG_TYPE, 
													 lParamRepository.getSelfId(),
													 lParamRepository.getPeerId());
		INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
		retVal = 0;
	}
	else
	{
		bool lIsBecomeStandby = false;
		retVal = 0;
		switch(m_RoleResolutionState)
		{
			case RoleResoState_ActivePending :
			{
				// case where Active pending message sent by self
				// and we have recvd ACTIVE role of peer in response
				// we should become standby

		    logger.logINGwMsg(false, ALWAYS_FLAG, -1,
									      "Recvd Role Active message from peer. "
									      "Also sent Role Active message to peer. " 
												"We will become standby.");
				if(m_RoleResTimerId)
				{
				  stopRoleResponseTimer();
				  m_RoleResTimerId = 0;
				}
				lIsBecomeStandby = true;
				break;
			}

			case RoleResoState_TimerStarted :
			{
				if(m_RoleTimerId)
				{
				  stopRoleTimer();
					m_RoleTimerId = 0;
        }
				lIsBecomeStandby = true;
				break;
			}
			case RoleResoState_Unknown :
			case RoleResoState_TimerExpired :
			{
				lIsBecomeStandby = true;
				break;
			}

			default :
			{
		      logger.logINGwMsg(false, ERROR_FLAG, -1,
											      "Recvd Role Active message from peer in wrong state");
		      retVal = -1;
			}
		}// end of switch m_RoleResolutionState

		if(lIsBecomeStandby)
		{
      INGwIfrMgrManager::getInstance().setSelfMode(false);
			//set self role
			m_CurrentRole = Role_Standby;
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
             "handlePeerActiveMsg(): m_CurrentRole set to Role_Standby");
			//set reso state
			m_RoleResolutionState = RoleResoState_Completed;

		  // Send Active Ack msg to peer
		  INGwIfrPrParamRepository& lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		  INGwFtPktRoleMsg lRolePacket;
		  lRolePacket.initialize(ACTIVE_ROLE_ACK_MSG_TYPE, 
		  											 lParamRepository.getSelfId(),
		  											 lParamRepository.getPeerId());
		  INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);

		}
	}//end of else m_CurrentRole

	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::handlePeerActiveMsg");
	return retVal;
}

string
INGwIfrMgrRoleMgr::toLog() const
{
	ostringstream strStream;
	strStream << " DesignatedRole : [" << RoleTypeStr[m_DesignatedRole] << "]";
	strStream << " CurrentRole : [" << RoleTypeStr[m_CurrentRole] << "]";
	strStream << " RoleResolutionState : [" << 
								 RoleResolutionStateStr[m_RoleResolutionState] << "]";
	strStream << " PeerState : [" << PeerStateStr[m_PeerState] << "]";
  return strStream.str();
}

//
//When Role timer started is expired
//
int
INGwIfrMgrRoleMgr::handleTimerExpiry(unsigned int p_TimerId, 
																		 INGwIfrRoleTimerContext* p_RoleTimerContext)
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::handleTimerExpiry");
  INGwIfrUtlWGuard(&m_RoleLock);
	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
										"INGwIfrMgrRoleMgr::handleTimerExpiry "
										"Recvd Timer Expiry. Role parameters [%s]",
										lLogMsg.c_str());

  int retVal = -1;
	switch(p_RoleTimerContext->m_RoleTimerType)
	{
		case TimerType_RoleTimer :
		{
	    logger.logINGwMsg(false, TRACE_FLAG, -1, 
					    					"INGwIfrMgrRoleMgr::handleTimerExpiry "
						    				"TimerType_RoleTimer Expired. ");
      if( (RoleResoState_TimerStarted == m_RoleResolutionState) &&
					( Role_Unkown == m_CurrentRole) )
      {
				// Initial role timer has expired
				// Send self Active Msg to Peer
				// and start response time for receiving the ACK
				
	      logger.logINGwMsg(false, TRACE_FLAG, -1, 
			      							"INGwIfrMgrRoleMgr::handleTimerExpiry "
				      						"TimerType_RoleTimer Expired. ");
		    m_RoleTimerId = 0;
		    m_RoleTimerThreadId = -1;
        m_RoleResolutionState = RoleResoState_TimerExpired;

				if(m_PeerState == PeerState_Up)
				{
		      // Send Active state to peer
		      INGwIfrPrParamRepository&  lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		      INGwFtPktRoleMsg lRolePacket;
		      lRolePacket.initialize(ACTIVE_PENDING_ROLE_MSG_TYPE, 
		  	    										 lParamRepository.getSelfId(),
		  		    									 lParamRepository.getPeerId());
		      INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
          logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
                            "INGwIfrMgrRoleMgr::handleTimerExpiry "
                            " ACTIVE_PENDING_ROLE_MSG_TYPE Pkt sent. <%s> ",
                            lRolePacket.toLog().c_str());
				}

		    if(enqueueRoleResponseTimer() == 0)
		    {
		      //m_RoleResolutionState = RoleResoState_ActivePending;
			    retVal = 0;
        }
		    else
		    {
		      logger.logINGwMsg(false, ERROR_FLAG, -1, 
		  									    "INGwIfrMgrRoleMgr::handleTimerExpiry. "
		  									    "ERROR : Failed to Start Role Response Timer.");
			    retVal = -1;
		    }
			} // end of if state check
			else
			{
	      logger.logINGwMsg(false, ERROR_FLAG, -1, 
			      							"INGwIfrMgrRoleMgr::handleTimerExpiry "
				      						"ERROR : TimerType_RoleTimer Expired in wrong state. ");
		    retVal = -1;
			}
		}
		break;

		case TimerType_RoleResTimer :
		{
	    logger.logINGwMsg(false, TRACE_FLAG, -1, 
					    					"INGwIfrMgrRoleMgr::handleTimerExpiry "
						    				"TimerType_RoleResTimer Expired. ");
      if( (RoleResoState_ActivePending == m_RoleResolutionState) &&
					( Role_Unkown == m_CurrentRole) )
      {
        std::string sgRole = 
             INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole();

        logger.logINGwMsg(false,ALWAYS_FLAG,0, 
                          "handleTimerExpiry(): Self SG Role <%s>",
                          sgRole.c_str());
        if (sgRole != "SG_STA_OOS") {
          logger.logINGwMsg(false, ERROR_FLAG,0,
	               "OUT handleTimerExpiry(): SG Role in Wrong state. COMMITING SUICIDE");
          char lpcTime[64];
          memset(lpcTime, 0, sizeof(lpcTime));
          lpcTime[0] = '1';
          g_getCurrentTime(lpcTime);
          printf("[+INC+] %s handleTimerExpiry():SG Role Wrong state<%s>."
              "COMMITING SUICIDE\n", lpcTime, sgRole.c_str()); fflush(stdout);
          exit(0);
	        return retVal;
        }

	      logger.logINGwMsg(false, ERROR_FLAG, -1, 
			      							"INGwIfrMgrRoleMgr::handleTimerExpiry "
				      						"TimerType_RoleResTimer Expired. Setting self as ACTIVE");

        INGwIfrMgrManager::getInstance().setSelfMode(true);
		    m_CurrentRole = Role_Active;
        logger.logINGwMsg(false,ALWAYS_FLAG,0,
               "handleTimerExpiry(): m_CurrentRole set to Role_Active");
		    m_RoleResolutionState = RoleResoState_Completed;
		    m_RoleResTimerId = 0;
		    m_RoleResTimerThreadId = -1;
				if(m_PeerState == PeerState_Up)
				{
		      // Send Active state to peer
		      INGwIfrPrParamRepository&  lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		      INGwFtPktRoleMsg lRolePacket;
		      lRolePacket.initialize(ACTIVE_ROLE_MSG_TYPE, 
		  	    										 lParamRepository.getSelfId(),
		  		    									 lParamRepository.getPeerId());
		      INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
          logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
                            "INGwIfrMgrRoleMgr::handleTimerExpiry "
                            " ACTIVE_ROLE_MSG_TYPE Pkt sent. <%s> ",
                            lRolePacket.toLog().c_str());
          
				}

		    retVal = 0;
			}
			else
			{
	      logger.logINGwMsg(false, ERROR_FLAG, -1, 
			      							"INGwIfrMgrRoleMgr::handleTimerExpiry "
				      						"ERROR : TimerType_RoleResTimer Expired in wrong state. ");
		    retVal = -1;
			}
		}
		break;
	}
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::handleTimerExpiry");
	return retVal;
}


INGwIfrMgrRoleMgr::INGwIfrMgrRoleMgr():
											 m_DesignatedRole(Role_Unkown),
											 m_CurrentRole(Role_Unkown),
											 m_PeerState(PeerState_Unkown),
											 m_RoleResolutionState(RoleResoState_Unknown),
											 m_RoleTimerTimeOut(20000),
											 m_RoleTimerId(0),
											 m_RoleTimerThreadId(-1),
											 m_RoleResTimerTimeOut(10000),
											 m_RoleResTimerId(0),
											 m_RoleResTimerThreadId(-1),
                       m_sbyToActive(false) 
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::INGwIfrMgrRoleMgr");
	initialize();
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::INGwIfrMgrRoleMgr");
}

INGwIfrMgrRoleMgr::~INGwIfrMgrRoleMgr()
{
	pthread_rwlock_destroy(&m_RoleLock);
}

INGwIfrMgrRoleMgr::INGwIfrMgrRoleMgr(const INGwIfrMgrRoleMgr& self)
{
}

INGwIfrMgrRoleMgr& 
INGwIfrMgrRoleMgr::operator=(const INGwIfrMgrRoleMgr& self)
{
	return *this;
}

void 
INGwIfrMgrRoleMgr::initialize()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::initialize");
	pthread_rwlock_init(&m_RoleLock, NULL);
  std::string isprimarystr = "";
  INGwIfrPrParamRepository::getInstance().getValue(ingwIS_PRIMARY, 
																									 isprimarystr);
  m_DesignatedRole = (isprimarystr.empty()) ? Role_Standby : 
                     (atoi(isprimarystr.c_str()) == 1) ? Role_Active :Role_Standby;

  //Get role time from env variable
  char *pRoleTmrTmout = getenv("ROLE_TIME_TIMEOUT_MSEC");
	if(pRoleTmrTmout != NULL)
	{
		int liRoleTmrTmout = atoi(pRoleTmrTmout);
		m_RoleTimerTimeOut = liRoleTmrTmout;
	}

  //Get role response time from env variable
  char *pRoleResTmrTmout = getenv("ROLE_RES_TIME_TIMEOUT_MSEC");
	if(pRoleResTmrTmout != NULL)
	{
		int liRoleResTmrTmout = atoi(pRoleResTmrTmout);
		m_RoleResTimerTimeOut = liRoleResTmrTmout;
	}
  //if designated secondry double the role timer value
	if(m_DesignatedRole == Role_Standby )
	{
		m_RoleTimerTimeOut = m_RoleTimerTimeOut * 2;
  }
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::initialize");
}

int
INGwIfrMgrRoleMgr::startRoleTimer()
{
	LogINGwTrace(false, 0, "IN startRoleTimer()");
  INGwIfrUtlWGuard(&m_RoleLock);

	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, "startRoleTimer() "
                    "Recvd startRoleTimer request. "
                    "Role parameters [%s] RoleTimerValue[%d]",
                    lLogMsg.c_str(), m_RoleTimerTimeOut);
  int retVal = -1;

	if( (m_CurrentRole == Role_Unkown )&& 
		  (m_RoleResolutionState == RoleResoState_Unknown) )
  {
	  INGwIfrRoleTimerContext* lTmrContext = new INGwIfrRoleTimerContext;
  	lTmrContext->m_RoleTimerType = TimerType_RoleTimer;
    m_RoleTimerThreadId = 
	  		INGwIfrMgrThreadMgr::getInstance().getCurrentThread().getThreadId();
    lTmrContext->m_ThreadId = m_RoleTimerThreadId;

  	retVal = INGwIfrMgrThreadMgr::getInstance().startTimer(
  																							m_RoleTimerTimeOut,
  																							(void *)lTmrContext,
  																							&INGwIfrMgrManager::getInstance(),
  																							m_RoleTimerId);
    if(retVal < 0)
  	{
  		logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "startRoleTimer() "
                        "ERROR : Could Not Start Role Timer");
      m_RoleTimerThreadId = -1;
	  	m_RoleTimerId = 0;
	  	delete lTmrContext;
  	}
  	else
  	{
  		retVal = 0;
      logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, "startRoleTimer() "
             "Started Role Timer: TimerId <%u>, ThreadID <%d>, time <%d> msec",
             m_RoleTimerId, m_RoleTimerThreadId, m_RoleTimerTimeOut);

      m_RoleResolutionState = RoleResoState_TimerStarted;
  	}
  }
  else
  {
  	retVal = 0;
  	logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
  				 "startRoleTimer() "
  				 "Not starting Role Timer as role state has moved forward.");
  }

	LogINGwTrace(false, 0, "OUT startRoleTimer()");
	return retVal;
}

int
INGwIfrMgrRoleMgr::stopRoleTimer()
{
	LogINGwTrace(false, 0, "IN stopRoleTimer()");
	logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
										"stopRoleTimer() "
										"Stopping Role Timer: TimerId <%u>, ThreadID <%d>",
										m_RoleTimerId, m_RoleTimerThreadId);

  int ret = INGwIfrMgrThreadMgr::getInstance().stopTimer(m_RoleTimerId,
																												 m_RoleTimerThreadId);
  if(ret < 0)
	{
		logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
											"stopRoleTimer() "
											"Could not Stop Role Timer <%u>  ThreadID <%d>" ,
											m_RoleTimerId, m_RoleTimerThreadId);
	}
	else
	{
		m_RoleTimerId = 0;
		m_RoleTimerThreadId = -1;
		ret = 0;
	}
	LogINGwTrace(false, 0, "OUT stopRoleTimer()");
	return ret;
}

int
INGwIfrMgrRoleMgr::startRoleResponseTimer()
{
	LogINGwTrace(false, 0, "IN startRoleResponseTimer()");

  INGwIfrUtlWGuard(&m_RoleLock);

	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, "startRoleResponseTimer() "
				            "Recvd startRoleResponseTimer request. "
                    "Role parameters [%s] RoleResTimerValue[%d]",
				            lLogMsg.c_str(), m_RoleResTimerTimeOut);
  int retVal = -1;

	if( m_RoleResolutionState == RoleResoState_TimerExpired )
  {
  	INGwIfrRoleTimerContext* lTmrContext = new INGwIfrRoleTimerContext;
  	lTmrContext->m_RoleTimerType = TimerType_RoleResTimer;
    m_RoleResTimerThreadId = 
  			INGwIfrMgrThreadMgr::getInstance().getCurrentThread().getThreadId();
    lTmrContext->m_ThreadId = m_RoleResTimerThreadId;

  	retVal = INGwIfrMgrThreadMgr::getInstance().startTimer(
  																							 m_RoleResTimerTimeOut,
  																							 (void *)lTmrContext,
  																							 &INGwIfrMgrManager::getInstance(),
  																							 m_RoleResTimerId);
    if(retVal < 0)
  	{
  		logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
  											"startRoleResponseTimer() "
  											"ERROR : Could Not Start Role Response Timer");
      m_RoleResTimerThreadId = -1;
	  	m_RoleResTimerId = 0;
	  	delete lTmrContext;
  	}
  	else
  	{
  		retVal = 0;
  		logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
  											"startRoleResponseTimer() "
  											"Started Role Res Timer: TimerId <%u>, ThreadID <%d> "
  											"for  time <%d> msec",
  											m_RoleResTimerId, m_RoleResTimerThreadId, m_RoleResTimerTimeOut);

		  m_RoleResolutionState = RoleResoState_ActivePending;
  	}
  } // if m_RoleResolutionState is  RoleResoState_TimerExpired 
  else
  {
  	logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
  										"startRoleResponseTimer() "
  										"Not starting Role Response Timer as role state has moved forward.");
    retVal = 0;
  }
	LogINGwTrace(false, 0, "OUT startRoleResponseTimer()");
	return retVal;
}

int
INGwIfrMgrRoleMgr::stopRoleResponseTimer()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::stopRoleResponseTimer");
	logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
										"INGwIfrMgrRoleMgr::stopRoleResponseTimer "
										"Stopping Role Response Timer: TimerId <%u>, ThreadID <%d>",
										m_RoleResTimerId, m_RoleResTimerThreadId);

  int ret = INGwIfrMgrThreadMgr::getInstance().stopTimer(m_RoleResTimerId,
																												 m_RoleResTimerThreadId);
  if(ret < 0)
	{
		logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
											"INGwIfrMgrRoleMgr::stopRoleResponseTimer "
											"Could not Stop Role Timer <%u>  ThreadID <%d>" ,
											m_RoleResTimerId, m_RoleResTimerThreadId);
	}
	else
	{
		m_RoleResTimerId = 0;
		m_RoleResTimerThreadId = -1;
		ret = 0;
	}
	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::stopRoleResponseTimer")
	return ret;
}

//
//When ACK of Active message is recv from peer
//

int
INGwIfrMgrRoleMgr::handlePeerActiveAckMsg()
{
	LogINGwTrace(false, 0, "IN handlePeerActiveAckMsg()");
  INGwIfrUtlWGuard(&m_RoleLock);
	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
										"handlePeerActiveAckMsg(): "
										"Recvd Role Active ACK message from peer . Role parameters [%s]",
										lLogMsg.c_str());

  int retVal = -1;
	if(m_CurrentRole == Role_Active)
	{
		logger.logINGwMsg(false, TRACE_FLAG, -1, "handlePeerActiveAckMsg(): "
											"Recvd Role Active ACK message from peer in Active State.");

		//Inform Infra Manager that peer is now connected in our active state
		INGwIfrMgrManager::getInstance().handlePeerConnect();
		retVal = 0;
	}
	else if(m_CurrentRole == Role_Standby)
	{
		logger.logINGwMsg(false, ERROR_FLAG, -1, "handlePeerActiveAckMsg(): "
											"ERROR : Recvd Role Active ACK message "
											"from peer in Standby State.");

		retVal = -1;
	}
	else
	{
		bool lIsBecomeStandby = false;
		retVal = 0;
		switch(m_RoleResolutionState)
		{
			case RoleResoState_ActivePending :
			{
        std::string sgRole = 
             INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole();

        logger.logINGwMsg(false,ALWAYS_FLAG,0,
               "handlePeerActiveAckMsg(): Self SG Role <%s>",
                          sgRole.c_str());
        if (sgRole != "SG_STA_OOS") {
          logger.logINGwMsg(false, ERROR_FLAG,0,
	               "OUT handlePeerActiveAckMsg(): SG Role in Wrong state");
          char lpcTime[64];
          memset(lpcTime, 0, sizeof(lpcTime));
          lpcTime[0] = '1';
          g_getCurrentTime(lpcTime);
          printf("[+INC+] %s handlePeerActiveAckMsg():SG Role Wrong state<%s>."
              "COMMITING SUICIDE\n", lpcTime, sgRole.c_str()); fflush(stdout);
          exit(0);
          
	        return retVal;
        }
        
		    logger.logINGwMsg(false, ALWAYS_FLAG, -1, "handlePeerActiveAckMsg(): "
											    "Recvd Role Active ACK message from peer. "
											    "Self State is known and resolution state is Active Pending. " 
													"Setting self as Active");

				if(m_RoleResTimerId)
				{
				  stopRoleResponseTimer();
				  m_RoleResTimerId = 0;
				}

        INGwIfrMgrManager::getInstance().setSelfMode(true);
		    m_CurrentRole = Role_Active;
        logger.logINGwMsg(false,ALWAYS_FLAG,0,
               "handlePeerActiveAckMsg(): m_CurrentRole set to Role_Active");
		    m_RoleResolutionState = RoleResoState_Completed;
		    m_RoleResTimerId = 0;
		    m_RoleResTimerThreadId = -1;
		    retVal = 0;
				break;
			}

			default :
			{
		      logger.logINGwMsg(false, ERROR_FLAG, -1,
											      "handlePeerActiveAckMsg(): Recvd Role Active ACK message from peer in wrong state");
		      retVal = -1;
			}
		}// end of switch m_RoleResolutionState

	}//end of else m_CurrentRole

	LogINGwTrace(false, 0, "OUT handlePeerActiveAckMsg()");
	return retVal;
}

int
INGwIfrMgrRoleMgr::enqueueRoleTimer()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::enqueueRoleTimer");

  std::string lDummyStr = "DUMMY_CALL_ID_TIMER";
  char* l_callId = new char[lDummyStr.length() + 1];
  strcpy(l_callId,  lDummyStr.c_str());

  INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
  lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::START_ROLE_TIMER;
  lWorkUnit->mpcCallId = l_callId;
  lWorkUnit->mpWorkerClbk = &INGwIfrMgrManager::getInstance();
  lWorkUnit->getHash();
  INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);

	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::enqueueRoleTimer");
  return 0;
}

int
INGwIfrMgrRoleMgr::enqueueRoleResponseTimer()
{
	LogINGwTrace(false, 0, "IN INGwIfrMgrRoleMgr::enqueueRoleResponseTimer");

  std::string lDummyStr = "DUMMY_CALL_ID_TIMER";
  char* l_callId = new char[lDummyStr.length() + 1];
  strcpy(l_callId,  lDummyStr.c_str());

  INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
  lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::START_ROLE_RES_TIMER;
  lWorkUnit->mpcCallId = l_callId;
  lWorkUnit->mpWorkerClbk = &INGwIfrMgrManager::getInstance();
  lWorkUnit->getHash();
  INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);

	LogINGwTrace(false, 0, "OUT INGwIfrMgrRoleMgr::enqueueRoleResponseTimer");
  return 0;
}


//
//When Peer Active Pending message is recv by INGW Talk
//

int
INGwIfrMgrRoleMgr::handlePeerActivePendingMsg()
{
	LogINGwTrace(false, 0, "IN handlePeerActivePendingMsg()");
  INGwIfrUtlWGuard(&m_RoleLock);
	string lLogMsg = toLog();
	logger.logINGwMsg(false, ALWAYS_FLAG, -1, 
										"handlePeerActivePendingMsg() "
										"Recvd Role Active message from peer . Role parameters [%s]",
										lLogMsg.c_str());

  int retVal = -1;
	if(m_CurrentRole == Role_Active)
	{
		logger.logINGwMsg(false, ALWAYS_FLAG, -1, "handlePeerActivePendingMsg() "
											"Recvd Role Active message but self state is Active"
											"Also sending Role Active message to peer");

		// Send its Active state to peer
		INGwIfrPrParamRepository&  lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		INGwFtPktRoleMsg lRolePacket;
		lRolePacket.initialize(ACTIVE_ROLE_MSG_TYPE, 
													 lParamRepository.getSelfId(),
													 lParamRepository.getPeerId());
		INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
	  logger.logINGwMsg(false, ALWAYS_FLAG, -1, "handlePeerActivePendingMsg() "
		    						" ACTIVE_ROLE_MSG_TYPE Pkt sent. <%s> ",
										lRolePacket.toLog().c_str());
		retVal = 0;
	}
	else if(m_CurrentRole == Role_Standby)
	{
		logger.logINGwMsg(false, ALWAYS_FLAG, -1, "handlePeerActivePendingMsg() "
											"Recvd Role Active message but self state is Stanby"
											"Sending Role Active Ack message to peer");

		// Send Active Ack msg to peer
		INGwIfrPrParamRepository&  lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		INGwFtPktRoleMsg lRolePacket;
		lRolePacket.initialize(ACTIVE_ROLE_ACK_MSG_TYPE, 
													 lParamRepository.getSelfId(),
													 lParamRepository.getPeerId());
		INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
	  logger.logINGwMsg(false, ALWAYS_FLAG, -1, "handlePeerActivePendingMsg() "
		    						" ACTIVE_ROLE_ACK_MSG_TYPE Pkt sent. <%s> ",
										lRolePacket.toLog().c_str());
		retVal = 0;
	}
	else
	{
		bool lIsBecomeStandby = false;
		retVal = 0;
		switch(m_RoleResolutionState)
		{
			case RoleResoState_ActivePending :
			{
				// case where role timer of both system expires at the same time
				// Check the designated Role
				// if self role is active then we should remain active
				// if self role is standby we should allow designated primary
				// to become active

				if(Role_Active == m_DesignatedRole)
				{
					// Don't stop the response timer here.
					// It will be stopped when peer will ack its Active Role.
					// Or it will become Active on timer expiry.

		      logger.logINGwMsg(false, ALWAYS_FLAG, -1,  "handlePeerActivePendingMsg() "
											      "Recvd Role Active message from peer. "
											      "Also sent Role Active message to peer. " 
														"We are designated Primary so we will take preference ");
					lIsBecomeStandby = false;
				}
        else if(Role_Standby == m_DesignatedRole)
				{
		      logger.logINGwMsg(false, ALWAYS_FLAG, -1, "handlePeerActivePendingMsg() "
											      "Recvd Role Active message from peer. "
											      "Also sent Role Active message to peer. " 
														"We are designated Standby so we will become standby.");
				  if(m_RoleResTimerId)
				  {
				    stopRoleResponseTimer();
					  m_RoleResTimerId = 0;
				  }
					lIsBecomeStandby = true;
				}
				break;
			}

			case RoleResoState_TimerStarted :
			{
				if(m_RoleTimerId)
				{
				  stopRoleTimer();
					m_RoleTimerId = 0;
        }
				lIsBecomeStandby = true;
				break;
			}
			case RoleResoState_Unknown :
			case RoleResoState_TimerExpired :
			{
				lIsBecomeStandby = true;
				break;
			}

			default :
			{
		      logger.logINGwMsg(false, ERROR_FLAG, -1, "handlePeerActivePendingMsg() "
											      "Recvd Role Active message from peer in wrong state");
		      retVal = -1;
			}
		}// end of switch m_RoleResolutionState

		if(lIsBecomeStandby)
		{
      INGwIfrMgrManager::getInstance().setSelfMode(false);
			//set self role
			m_CurrentRole = Role_Standby;
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
             "handlePeerActivePendingMsg(): m_CurrentRole set to Role_Standby");
			//set reso state
			m_RoleResolutionState = RoleResoState_Completed;

		  // Send Active Ack msg to peer
		  INGwIfrPrParamRepository& lParamRepository = 
													INGwIfrPrParamRepository::getInstance();
		  INGwFtPktRoleMsg lRolePacket;
		  lRolePacket.initialize(ACTIVE_ROLE_ACK_MSG_TYPE, 
		  											 lParamRepository.getSelfId(),
		  											 lParamRepository.getPeerId());
		  INGwIfrMgrManager::getInstance().sendMsgToINGW(&lRolePacket);
	    logger.logINGwMsg(false, ALWAYS_FLAG, -1, "handlePeerActivePendingMsg() "
		      						" ACTIVE_ROLE_ACK_MSG_TYPE Pkt sent. <%s> ",
		  								lRolePacket.toLog().c_str());
		}
	}//end of else m_CurrentRole

	LogINGwTrace(false, 0, "OUT handlePeerActivePendingMsg()");
	return retVal;
}

bool 
INGwIfrMgrRoleMgr::getSbyToActive()
{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In getSbyToActive<%s>",
  ((m_sbyToActive==true)?"TRUE":"FALSE"));

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out getSbyToActive");
  return m_sbyToActive;
}

void
INGwIfrMgrRoleMgr::setSbyToActive(bool p_val)
{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In setSbyToActive");
    m_sbyToActive = p_val;
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out setSbyToActive<%s>",
  ((m_sbyToActive==true)?"TRUE":"FALSE"));
}
