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
//     File:     INGwSpSipCallMap.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIPCALL_MAP_H_
#define INGW_SP_SIPCALL_MAP_H_

#include <pthread.h>
#include <map>
#include <string>

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipCall.h>


class INGwSpSipCallMap
{
	public :
    typedef	std::map<std::string, INGwSpSipCall*> t_SipCallMap;
    typedef t_SipCallMap::iterator t_SipCallMapItr;

		INGwSpSipCallMap()
		{
			pthread_rwlock_init(&m_CallMapLock, 0);
		}
		~INGwSpSipCallMap()
		{
			pthread_rwlock_destroy(&m_CallMapLock);
    }

		int addCall(std::string& p_CallIdStr, INGwSpSipCall* p_SipCall)
		{
			int ret = 0;
			pthread_rwlock_wrlock(&m_CallMapLock);

			t_SipCallMapItr iter = m_SipCallMap.find(p_CallIdStr);
			if(iter != m_SipCallMap.end())
			{
				ret = -1;
			}
			else
			{
			  m_SipCallMap[p_CallIdStr] = p_SipCall;
        p_SipCall->getRef();
				ret = 0;
			}

			pthread_rwlock_unlock(&m_CallMapLock);

			return ret;
		}

		int removeCall(std::string& p_CallIdStr)
		{
			int ret = 0;
			pthread_rwlock_wrlock(&m_CallMapLock);

			t_SipCallMapItr iter = m_SipCallMap.find(p_CallIdStr);
			if(iter != m_SipCallMap.end())
			{
				INGwSpSipCall* lSipCall = m_SipCallMap[p_CallIdStr];
				m_SipCallMap.erase(iter);
        lSipCall->releaseRef();
				ret = 0;
			}
			else
			{
				ret = -1;
			}

			pthread_rwlock_unlock(&m_CallMapLock);

			return ret;
    }

		INGwSpSipCall* getCall(std::string& p_CallIdStr)
		{
			pthread_rwlock_rdlock(&m_CallMapLock);
      INGwSpSipCall* retCall = NULL;

			t_SipCallMapItr iter = m_SipCallMap.find(p_CallIdStr);
			if(iter != m_SipCallMap.end())
			{
				retCall = m_SipCallMap[p_CallIdStr];
        retCall->getRef();
			}
			else
			{
				retCall = NULL;
			}

			pthread_rwlock_unlock(&m_CallMapLock);

			return retCall;
    }

    static unsigned long hash(const char *apKey)
    {
      return static_cast<unsigned long>(elf_hash(apKey));
    }

  private :
		pthread_rwlock_t m_CallMapLock;
		t_SipCallMap m_SipCallMap;

};

#endif //INGW_SP_SIPCALL_MAP_H_

