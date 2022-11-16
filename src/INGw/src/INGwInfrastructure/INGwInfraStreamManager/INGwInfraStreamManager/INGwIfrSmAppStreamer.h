/*------------------------------------------------------------------------------
         File: INGwIfrSmAppStreamer.h
  Application: General utility
    Component: StreamMgr
   Programmer: S.Suriya prakash
      Written: 19-Aug-2003
  Description: Application Output manager
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __APP_STREAMER_H__
#define __APP_STREAMER_H__

#include <string>
#include <pthread.h>

namespace BpGenUtil
{
   class INGwIfrSmFileHandler;
   class INGwIfrSmStreamMgr;

   class INGwIfrSmAppStreamer
   {
      private:

         std::string  _name;

         bool _logStatus;

         INGwIfrSmFileHandler *_handler;

         pthread_mutex_t _lock;

      public:

         INGwIfrSmAppStreamer(const char *inname, const char *filename = NULL);
         ~INGwIfrSmAppStreamer();

         std::string getName();

         bool isLoggable();
         void setLoggable(bool);

         int log(const char *, ...);

         std::string toLog();

      public:

         void setFileHandle(INGwIfrSmFileHandler *inhandler);
         INGwIfrSmFileHandler * getHandler();

   };

   extern INGwIfrSmAppStreamer *defaultStreamer;
};

#endif
