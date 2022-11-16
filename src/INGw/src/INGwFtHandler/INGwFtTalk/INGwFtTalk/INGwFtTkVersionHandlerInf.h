/*------------------------------------------------------------------------------
         File: INGwFtTkVersionHandlerInf.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 03-Jan-2004
  Description: App version negotiaion.
      History: Maintained in clearcase
    Copyright: 2004, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_VERSION_HANDLER_INF_H__
#define __BTK_VERSION_HANDLER_INF_H__

#include <Util/ObjectId.h>
#include <INGwFtTalk/INGwFtTkVersionSet.h>

class INGwFtTkVersionHandlerInf
{
   public:

      virtual int negotiateVersion(const ObjectId &peer, 
                                   const INGwFtTkVersionSet& peerVersion) = 0;
};

#endif
