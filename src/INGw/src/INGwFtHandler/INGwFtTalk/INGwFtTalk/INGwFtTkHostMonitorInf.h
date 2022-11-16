/*------------------------------------------------------------------------------
         File: INGwFtTkHostMonitorInf.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_HOST_MONITOR_INF_H__
#define __BTK_HOST_MONITOR_INF_H__

class INGwFtTkHostEventDetail
{
   public:

      enum INGwFtTkHostEvent
      {
         AVAILABLE,
         NOT_AVAILABLE
      };
   
   public:

      INGwFtTkHostEvent interestedEvent;
      unsigned int hostIP;

   public:

      INGwFtTkHostEventDetail()
      {
         interestedEvent = AVAILABLE;
         hostIP = 0;
      }

      bool operator == (const INGwFtTkHostEventDetail &inevt)
      {
         return ((interestedEvent == inevt.interestedEvent) &&
                 (hostIP == inevt.hostIP));
      }
};

class INGwFtTkHostMonitorInf
{
   public:

      virtual void generatedEvent(INGwFtTkHostEventDetail event) = 0;
};

#endif
