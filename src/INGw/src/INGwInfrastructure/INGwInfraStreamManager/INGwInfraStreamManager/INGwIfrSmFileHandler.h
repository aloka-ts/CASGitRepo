/*------------------------------------------------------------------------------
         File: INGwIfrSmFileHandler.h
  Application: General utility
    Component: StreamMgr
   Programmer: S.Suriya prakash
      Written: 19-Aug-2003
  Description: Application Output manager
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __FILE_HANDLER_H__
#define __FILE_HANDLER_H__

#include <string>
#include <stdio.h>
#include <pthread.h>
#include <stdarg.h>

namespace BpGenUtil
{
   class INGwIfrSmFileHandler
   {
      private:

         std::string _name;
         std::string _filename;

         bool _logStatus;

         int _fd;
         FILE *_fp;

         int _len;
         unsigned int _limit;

         pthread_mutex_t _lock;

      public:

         INGwIfrSmFileHandler(const char *inname);
         ~INGwIfrSmFileHandler();

         int log(const char *, va_list arg);
         void setLoggable(bool);
         int changeFile(const char *filename);

         void setLimit(unsigned int inLimit);

         bool isLoggable();

         std::string toLog();
         std::string getFilename();
         std::string getName();

         void checkLimit();

      private:

         int _openFile();
         void _checkLimit();
   };
};

#endif
