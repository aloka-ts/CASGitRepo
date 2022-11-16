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
//     File:     INGwIfrMgrRoleMgr.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_MGR_ROLE_MGR_H_
#define _INGW_IFR_MGR_ROLE_MGR_H_

#include <string>
#include<pthread.h>

typedef enum
{
	Role_Unkown = 0,
	Role_Active,
	Role_Standby
} t_RoleType;

static const char* RoleTypeStr [] = 
{
	"Role_Unkown",
	"Role_Active",
	"Role_Standby"
};

typedef enum
{
	RoleResoState_Unknown = 0,
	RoleResoState_Completed,
	RoleResoState_NotStarted,
	RoleResoState_TimerStarted,
	RoleResoState_TimerExpired,
	RoleResoState_ActivePending,
	RoleResoState_StandbyPending
} t_RoleResolutionState;


static const char* RoleResolutionStateStr [] = 
{
	"RoleResoState_Unknown",
	"RoleResoState_Completed",
	"RoleResoState_NotStarted",
	"RoleResoState_TimerStarted",
	"RoleResoState_TimerExpired",
	"RoleResoState_ActivePending",
	"RoleResoState_StandbyPending"
};

typedef enum
{
	PeerState_Unkown = 0,
	PeerState_Up,
	PeerState_Down,
	PeerState_No_Peer
} t_PeerState;

static const char* PeerStateStr [] = 
{
	"PeerState_Unkown",
	"PeerState_Up",
	"PeerState_Down",
	"PeerState_No_Peer"
};


typedef enum
{
	TimerType_RoleTimer,
	TimerType_RoleResTimer
} t_RoleTimerType;

struct INGwIfrRoleTimerContext
{
	t_RoleTimerType m_RoleTimerType;
	int m_ThreadId;
};

class INGwIfrMgrRoleMgr
{
  public :

    //Singleton
    static INGwIfrMgrRoleMgr& getInstance();
		static INGwIfrMgrRoleMgr* m_RoleMgrSelf;

    //
		//When we need to start the role resolution
		//First function to be called. Self role shd be unknown and 
		//role resolution shd be unknown
		//
		int
		startRoleResolution();

    //
		//When connection with peer is extablished
		//
		int
		peerUp();

    //
		//When connection with peer is disconnected
		//
		int
		peerDown();

    //
		//When no peer is configured
		// value of Peer state will be PeerState_No_Peer
		//
		int
		setPeerState(t_PeerState p_PeerState);

    //
		//When Peer Active message is recv by INGW Talk
		//
		int
		handlePeerActiveMsg();

    //
		//When Peer Active Pending message is recv by INGW Talk
		//
		int
		handlePeerActivePendingMsg();

    //
		//When ACK of Active message is recv from peer
		//
		int
		handlePeerActiveAckMsg();

    //
		//When Role timer started is expired
		//
		int
		handleTimerExpiry(unsigned int p_TimerId, 
													INGwIfrRoleTimerContext* p_RoleTimerContext);

		inline t_RoleType getDesignatedRole()
		{
			return m_DesignatedRole;
    }

		inline t_RoleType getCurrentRole()
		{
			return m_CurrentRole;
    }

		inline t_RoleResolutionState getRoleResolutionState()
		{
			return m_RoleResolutionState;
    }

		inline t_PeerState getPeerState()
		{
			return m_PeerState;
    }

    inline void setDesignatedRole(t_RoleType p_DesignatedRole)
		{
			m_DesignatedRole = p_DesignatedRole;
    }
		string
		toLog() const;

		int 
		startRoleTimer();

		int 
		startRoleResponseTimer();

    bool 
    getSbyToActive();

    void
    setSbyToActive(bool p_val);
    
  private :
    //Singleton
    INGwIfrMgrRoleMgr();
		~INGwIfrMgrRoleMgr();
		INGwIfrMgrRoleMgr(const INGwIfrMgrRoleMgr& self);
		INGwIfrMgrRoleMgr& operator=(const INGwIfrMgrRoleMgr& self);

		void 
		initialize();

		int 
		enqueueRoleTimer();
 
    bool
    m_sbyToActive;

		int 
		enqueueRoleResponseTimer();

		int
		stopRoleTimer();

		int
		stopRoleResponseTimer();

		t_RoleType m_DesignatedRole;
		t_RoleType m_CurrentRole;
		t_RoleResolutionState m_RoleResolutionState;
		t_PeerState m_PeerState;

		int m_RoleTimerTimeOut;
		unsigned int m_RoleTimerId;
		int m_RoleTimerThreadId;

		int m_RoleResTimerTimeOut;
		unsigned int m_RoleResTimerId;
		int m_RoleResTimerThreadId;

		pthread_rwlock_t m_RoleLock;
};

#endif //_INGW_IFR_MGR_ROLE_MGR_H_
