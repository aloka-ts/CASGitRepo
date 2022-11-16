/*------------------------------------------------------------------------------
         File: INGwFtTkFaultManager.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_FAULT_MANANGER_H__
#define __BTK_FAULT_MANANGER_H__

#include <INGwFtTalk/INGwFtTkHostMonitorInf.h>
#include <pthread.h>

class INGwFtTkControlMsg;
class INGwFtTkChannel;

#define BTK_MAX_SUBSYS_CONN 200

class INGwFtTkFaultManager : public INGwFtTkHostMonitorInf
{
   private:

      INGwFtTkChannel *_readers[BTK_MAX_SUBSYS_CONN];
      INGwFtTkChannel *_faultChannel[BTK_MAX_SUBSYS_CONN];
      int _no_of_readers;
      int _no_of_faultChannel;

      pthread_mutex_t _lock;

   private:

      void _removeFaultEntry(unsigned int peerID);

      void _informBogusFault(unsigned int peerID);

      void _handleMachAvailable(unsigned int IP);
      void _handleMachUnavailable(unsigned int IP);

   private:

      static void * _bogusFaultNotifierThreadStart(void *);

   public:

      INGwFtTkFaultManager();
      ~INGwFtTkFaultManager();

      //WriteFailed.
      void faultIdentified(INGwFtTkChannel *);

      //OpenConnection.
      int checkEstablishSuccessResponse(INGwFtTkChannel *);
      int checkEstablishFailedResponse(INGwFtTkChannel *);
      bool isReaderAvailable(int peerID);

      //AcceptConnection.
      int checkAcceptedConn(INGwFtTkControlMsg &msg);

      //Action.
      void receivedAction(unsigned int peerID);
      void sentAction(unsigned int peerID);

      //Reader.
      void readerStarted(INGwFtTkChannel *);
      void readerStopped(INGwFtTkChannel *);

      void generatedEvent(INGwFtTkHostEventDetail event);
};

#endif
