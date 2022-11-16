/*------------------------------------------------------------------------------
         File: INGwFtTkFaultManager.C
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#include <Util/Logger.h>
LOG("INGwFtTalk");
#include <INGwFtTalk/INGwFtTkFaultManager.h>
#include <INGwFtTalk/INGwFtTkHostMonitor.h>
#include <INGwFtTalk/INGwFtTkChannelManager.h>
#include <INGwFtTalk/INGwFtTkCommunicator.h>
#include <INGwFtTalk/INGwFtTkChannel.h>
#include <INGwFtTalk/INGwFtTkFaultHandlerInf.h>
#include <INGwFtTalk/INGwFtTkUtil.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include <unistd.h>

INGwFtTkFaultManager::INGwFtTkFaultManager()
{
   pthread_mutex_init(&_lock, NULL);
   _no_of_readers = 0;
   _no_of_faultChannel = 0;

   INGwFtTkHostMonitor::getInstance().registerCallback(this);
   LogAlways(0, "FaultMgr Created.");
}

INGwFtTkFaultManager::~INGwFtTkFaultManager()
{
   INGwFtTkHostMonitor::getInstance().deregisterCallback(this);
   pthread_mutex_destroy(&_lock);
}

void INGwFtTkFaultManager::faultIdentified(INGwFtTkChannel *inChannel)
{
   INGwFtTkRefObjHolder holder(inChannel);
   inChannel->addRef();

   logger.logMsg(ALWAYS_FLAG, 0, "Fault channel [%d] [%x-%d] noted.",
                 inChannel->_peerID, inChannel->_peerListenerInfo.IP, 
                 inChannel->_peerListenerInfo.port);

   {
      INGwFtTkGuard(&_lock);

      bool foundFlag = false;

      for(int idx = 0; idx < _no_of_faultChannel; idx++)
      {
         INGwFtTkChannel *currChannel = _faultChannel[idx];

         if(currChannel->_peerID == inChannel->_peerID)
         {
            currChannel->removeRef();
            _faultChannel[idx] = inChannel;
            foundFlag = true;
         }
      }

      if(!foundFlag)
      {
         if(_no_of_faultChannel == BTK_MAX_SUBSYS_CONN)
         {
            logger.logMsg(ERROR_FLAG, 0, "Too many subsystem conn. max[%d]",
                          BTK_MAX_SUBSYS_CONN);

            for(int idx = 0; idx < BTK_MAX_SUBSYS_CONN; idx++)
            {
               logger.logMsg(ALWAYS_FLAG, 0, "Idx [%d] peer [%d] host [%x]", 
                             idx, _faultChannel[idx]->_peerID, 
                             _faultChannel[idx]->_peerListenerInfo.IP);
            }

            logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberately.");
            printf("INGwFtTk: Too many subsystem conn for fault identification.\n");
            exit(1);
         }

         _faultChannel[_no_of_faultChannel] = inChannel;
         _no_of_faultChannel++;
      }
   }

   INGwFtTkHostEventDetail evt;
   evt.hostIP = inChannel->_peerListenerInfo.IP;
   evt.interestedEvent = INGwFtTkHostEventDetail::AVAILABLE;

   INGwFtTkHostMonitor::getInstance().addMonitor(evt);
   return;
}

int INGwFtTkFaultManager::checkEstablishSuccessResponse(INGwFtTkChannel *newChannel)
{
   INGwFtTkRefObjHolder holder(newChannel);

   bool foundFlag = false;

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < _no_of_faultChannel; idx++)
      {
         INGwFtTkChannel *currChannel = _faultChannel[idx];

         if(currChannel->_peerID == newChannel->_peerID)
         {
            foundFlag = true;

            if(newChannel->_peerInstance.startTime == 
               currChannel->_peerInstance.startTime)
            {
               break;
            }

            _removeFaultEntry(newChannel->_peerID);
            return 0;
         }
      }

      if(foundFlag == false)
      {
         return 0;
      }
   }

   _informBogusFault(newChannel->_peerID);
   return -1;
}

int INGwFtTkFaultManager::checkEstablishFailedResponse(INGwFtTkChannel *newChannel)
{
   INGwFtTkRefObjHolder holder(newChannel);

   if(newChannel->_peerInstance.startTime == 0)
   {
      return 0;
   }

   bool foundFlag = false;

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < _no_of_faultChannel; idx++)
      {
         INGwFtTkChannel *currChannel = _faultChannel[idx];

         if(currChannel->_peerID == newChannel->_peerID)
         {
            foundFlag = true;

            if(newChannel->_peerInstance.startTime == 
               currChannel->_peerInstance.startTime)
            {
               break;
            }

            _removeFaultEntry(newChannel->_peerID);
            return 0;
         }
      }

      if(foundFlag == false)
      {
         return 0;
      }
   }

   _informBogusFault(newChannel->_peerID);
   return -1;
}

int INGwFtTkFaultManager::checkAcceptedConn(INGwFtTkControlMsg &msg)
{
   bool foundFlag = false;

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < _no_of_faultChannel; idx++)
      {
         INGwFtTkChannel *currChannel = _faultChannel[idx];

         if(currChannel->_peerID == msg.peerID)
         {
            foundFlag = true;

            if(currChannel->_peerInstance.startTime == msg.instanceID.startTime)
            {
               break;
            }

            _removeFaultEntry(msg.peerID);
            return 0;
         }
      }

      if(foundFlag == false)
      {
         return 0;
      }
   }

   _informBogusFault(msg.peerID);
   return -1;
}

void INGwFtTkFaultManager::receivedAction(unsigned int peerID)
{
   INGwFtTkGuard(&_lock);
   _removeFaultEntry(peerID);
   return;
}

void INGwFtTkFaultManager::sentAction(unsigned int peerID)
{
   INGwFtTkGuard(&_lock);
   _removeFaultEntry(peerID);
   return;
}

bool INGwFtTkFaultManager::isReaderAvailable(int peerID)
{
   INGwFtTkGuard(&_lock);

   for(int idx = 0; idx < _no_of_readers; idx++)
   {
      INGwFtTkChannel *currChannel = _readers[idx];

      if(currChannel->_peerID == peerID)
      {
         return true;
      }
   }

   return false;
}

void INGwFtTkFaultManager::readerStarted(INGwFtTkChannel *inChannel)
{
   INGwFtTkRefObjHolder holder(inChannel);
   inChannel->addRef();

   {
      INGwFtTkGuard(&_lock);

      bool foundFlag = false;

      for(int idx = 0; idx < _no_of_readers; idx++)
      {
         INGwFtTkChannel *currChannel = _readers[idx];

         if(currChannel->_peerID == inChannel->_peerID)
         {
            currChannel->removeRef();
            _readers[idx] = inChannel;
            foundFlag = true;
         }
      }

      if(!foundFlag)
      {
         if(_no_of_readers == BTK_MAX_SUBSYS_CONN)
         {
            logger.logMsg(ERROR_FLAG, 0, "Too many subsystem conn. max[%d]",
                          BTK_MAX_SUBSYS_CONN);

            for(int idx = 0; idx < BTK_MAX_SUBSYS_CONN; idx++)
            {
               logger.logMsg(ALWAYS_FLAG, 0, "Idx [%d] peer [%d] host [%x]", 
                             idx, _readers[idx]->_peerID, 
                             _readers[idx]->_peerListenerInfo.IP);
            }

            logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberately.");
            printf("INGwFtTk: Too many subsystem conn for fault identification.\n");
            exit(1);
         }

         _readers[_no_of_readers] = inChannel;
         _no_of_readers++;
      }
   }

   INGwFtTkHostEventDetail evt;
   evt.hostIP = inChannel->_peerListenerInfo.IP;
   evt.interestedEvent = INGwFtTkHostEventDetail::NOT_AVAILABLE;

   INGwFtTkHostMonitor::getInstance().addMonitor(evt);
   return;
}

void INGwFtTkFaultManager::readerStopped(INGwFtTkChannel *inChannel)
{
   INGwFtTkRefObjHolder holder(inChannel);

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < _no_of_readers; idx++)
      {
         INGwFtTkChannel *currChannel = _readers[idx];

         if(currChannel->_peerID == inChannel->_peerID)
         {
            currChannel->removeRef();

            memmove(_readers + idx, _readers + idx + 1, 
                    sizeof(INGwFtTkChannel *) * (_no_of_readers - idx  - 1));
            _no_of_readers--;

            return;
         }
      }
   }

/*
 *If there raises a small interruption in network with time comparable to the 
 *our faultdetection time, then channel closed by one end (Timeout + close) is 
 *reached the other end as proper close (read failure).
 *
 *To have complete bogus fault verification we have to store the read failure as 
 *fault. This makes the INGwFtTalk channels are meant for non breakable connections.
 *This assumption is valid for the Baypackets apps.

 *-Suriya
 */

   if(!inChannel->isAppClose())
   {
      faultIdentified(inChannel->clone());
   }

   return;
}

void INGwFtTkFaultManager::_removeFaultEntry(unsigned int peerID)
{
   for(int idx = 0; idx < _no_of_faultChannel; idx++)
   {
      INGwFtTkChannel *currChannel = _faultChannel[idx];

      if(currChannel->_peerID == peerID)
      {
         currChannel->removeRef();

         memmove(_faultChannel + idx, _faultChannel + idx + 1, 
                 sizeof(INGwFtTkChannel *) * (_no_of_faultChannel - idx  - 1));
         _no_of_faultChannel--;

         logger.logMsg(ALWAYS_FLAG, 0, "Removed fault entry for [%d]", peerID);
         return;
      }
   }

   logger.logMsg(ALWAYS_FLAG, 0, "No fault entry for [%d]", peerID);
   return;
}

extern "C" void * INGwFtTkFaultManager::_bogusFaultNotifierThreadStart(void *data)
{
   INGwFtTkCommunicator::setSignal();
   pthread_detach(pthread_self());

   unsigned int peerID = *(unsigned int *)data;
   delete (unsigned int *)data;

   INGwFtTkFaultHandlerInf *callback = INGwFtTkCommunicator::getInstance()._faultCallback;

   if(callback)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Notifying bogus fault for [%d].", peerID);
      callback->bogusFault(peerID);
      logger.logMsg(ALWAYS_FLAG, 0, "Bogus fault for [%d] notified.", peerID);
   }
   else
   {
      logger.logMsg(ERROR_FLAG, 0, "No callback registered to propagate "
                                   "bogus fault to [%d]", peerID);
   }

   return NULL;
}

void INGwFtTkFaultManager::_informBogusFault(unsigned int peerID)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Bogus fault for [%d]", peerID);
   unsigned int *data = new unsigned int;
   *data = peerID;

   if(INGwFtTkCommunicator::getInstance()._faultCallback == NULL)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "No Handler to process bogus fault [%d]", 
                    peerID);
      printf("INGwFtTk: No bogus fault handler.\n");
      raise(9);
      sleep(5);
   }

   pthread_t bogusFaultNotifier;
   pthread_create(&bogusFaultNotifier, NULL, _bogusFaultNotifierThreadStart, 
                  data);
}

void INGwFtTkFaultManager::generatedEvent(INGwFtTkHostEventDetail event)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Received event [%d] for [%x] from "
                                 "hostMonitor.", event.interestedEvent, 
                 event.hostIP);

   if(event.interestedEvent == INGwFtTkHostEventDetail::AVAILABLE)
   {
      _handleMachAvailable(event.hostIP);
   }
   else
   {
      _handleMachUnavailable(event.hostIP);
   }
}

void INGwFtTkFaultManager::_handleMachUnavailable(unsigned int IP)
{
   unsigned int peerIDs[BTK_MAX_SUBSYS_CONN];

   int count = 0;

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < _no_of_readers; idx++)
      {
         if(IP == _readers[idx]->_peerListenerInfo.IP)
         {
            peerIDs[count] = _readers[idx]->_peerID;
            count++;
         }
      }
   }

   if(count == 0)
   {
      INGwFtTkHostEventDetail event;
      event.interestedEvent = INGwFtTkHostEventDetail::NOT_AVAILABLE;
      event.hostIP = IP;
      INGwFtTkHostMonitor::getInstance().stopMonitor(event);
      return;
   }

   //Get the max last read time.
   hrtime_t currTime = gethrtime();
   hrtime_t boundry = currTime - 5000000000L;

   hrtime_t maxTime = 0;

   INGwFtTkCommunicator &btk = INGwFtTkCommunicator::getInstance();
   for(int idx = 0; idx < count; idx++)
   {
      INGwFtTkChannel *currChannel = btk._channelMgr->getChannel(peerIDs[idx]);

      if(currChannel == NULL)
      {
         logger.logMsg(ERROR_FLAG, 0, "No channel for [%d] ", peerIDs[idx]);
         continue;
      }

      INGwFtTkRefObjHolder holder(currChannel);

      hrtime_t rdTime = currChannel->getLastReadTime();

      if(maxTime < rdTime)
      {
         maxTime = rdTime;
      }
   }

   if(maxTime > boundry)
   {
      logger.logMsg(VERBOSE_FLAG, 0, "Last read [%lld] now [%lld] fault report "
                                     "from hostMonitor.", maxTime, currTime);
      return;
   }

   INGwFtTkHostEventDetail event;
   event.interestedEvent = INGwFtTkHostEventDetail::NOT_AVAILABLE;
   event.hostIP = IP;
   INGwFtTkHostMonitor::getInstance().stopMonitor(event);

   for(int idx = 0; idx < count; idx++)
   {
      INGwFtTkChannel *currChannel = btk._channelMgr->getChannel(peerIDs[idx]);

      if(currChannel == NULL)
      {
         continue;
      }

      INGwFtTkRefObjHolder holder(currChannel);

      logger.logMsg(ALWAYS_FLAG, 0, "Closing channel for [%d] on mach unavail.",
                    peerIDs[idx]);

      faultIdentified(currChannel->clone());
      currChannel->disconnect();
   }

   return;
}

void INGwFtTkFaultManager::_handleMachAvailable(unsigned int IP)
{
   INGwFtTkChannel * faultsStored[BTK_MAX_SUBSYS_CONN];
   int count = 0;

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < _no_of_faultChannel; idx++)
      {
         if(IP == _faultChannel[idx]->_peerListenerInfo.IP)
         {
            faultsStored[count] = _faultChannel[idx];
            _faultChannel[idx]->addRef();
            count++;
         }
      }
   }

   if(count == 0)
   {
      INGwFtTkHostEventDetail event;
      event.interestedEvent = INGwFtTkHostEventDetail::AVAILABLE;
      event.hostIP = IP;
      INGwFtTkHostMonitor::getInstance().stopMonitor(event);
      return;
   }

   for(int idx = 0; idx < count; idx++)
   {
      INGwFtTkRefObjHolder holder(faultsStored[idx]);
      INGwFtTkChannel *currChannel = faultsStored[idx];

      logger.logMsg(ALWAYS_FLAG, 0, "Now checking. Peer[%d] IP[%x-%d] time[%d]",
                    currChannel->_peerID, currChannel->_peerListenerInfo.IP, 
                    currChannel->_peerListenerInfo.port, 
                    currChannel->_peerInstance.startTime);

      INGwFtTkChannel *newChannel = new INGwFtTkChannel(currChannel->_peerID, 0, 0);
      INGwFtTkRefObjHolder newChannelHolder(newChannel);
      newChannel->setEndPointInfo(currChannel->getEndPointInfo());

      INGwFtTkChannel::INGwFtTkFaultMgrQueryState result;
      newChannel->makeFaultQuery(*(INGwFtTkCommunicator::getInstance()._selfAbout), 
                                 result);

      if(result == INGwFtTkChannel::QUERY_TIMEOUT)
      {
         logger.logMsg(ERROR_FLAG, 0, "Machine is up, but query timed out.");
         logger.logMsg(ALWAYS_FLAG, 0, "Might be [%d] is stopped.", 
                       currChannel->_peerID);
         continue;
      }

      if(result == INGwFtTkChannel::QUERY_FAILED)
      {
         _removeFaultEntry(currChannel->_peerID);
         continue;
      }

      if(newChannel->_peerInstance.startTime == 
         currChannel->_peerInstance.startTime)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Peer [%d] is reavailable.", 
                       currChannel->_peerID);
         _informBogusFault(currChannel->_peerID);
      }
      else
      {
         _removeFaultEntry(currChannel->_peerID);
      }
   }

   return;
}
