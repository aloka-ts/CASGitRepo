/*------------------------------------------------------------------------------
         File: INGwFtTkHostMonitor.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_HOST_MONITOR_H__
#define __BTK_HOST_MONITOR_H__

#include <INGwFtTalk/INGwFtTkSingleton.h>
#include <INGwFtTalk/INGwFtTkHostMonitorInf.h>
#include <pthread.h>

#define BTK_MAX_HOST_EVENT 100
#define BTK_MAX_SELF_IPS 20

class INGwFtTkHostMonitor : public INGwFtTkSingleton<INGwFtTkHostMonitor>
{
   private:

      INGwFtTkHostEventDetail _events[BTK_MAX_HOST_EVENT];
      int _no_of_events;

      INGwFtTkHostMonitorInf *_callback;

      pthread_t _monitorThread;
      pthread_mutex_t _lock;
      pthread_cond_t _cond;

      bool _stopStatus;

      unsigned int _selfIP[BTK_MAX_SELF_IPS];

   private:

      static void * _monitorThreadStart(void *);

      void _monitor();
      INGwFtTkHostEventDetail::INGwFtTkHostEvent _getStatus(unsigned int IP, int timeout);

   public:

      INGwFtTkHostMonitor();
      ~INGwFtTkHostMonitor();

      int addMonitor(INGwFtTkHostEventDetail event);
      int stopMonitor(INGwFtTkHostEventDetail event);

      void registerCallback(INGwFtTkHostMonitorInf *inf);
      void deregisterCallback(INGwFtTkHostMonitorInf *inf);
};

#endif
