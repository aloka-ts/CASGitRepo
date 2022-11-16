/*------------------------------------------------------------------------------
         File: INGwIfrSmAppStreamer.C
  Application: General utility
    Component: INGwIfrSmStreamMgr
   Programmer: S.Suriya prakash
      Written: 19-Aug-2003
  Description: Application Output manager
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#include <Util/Logger.h>
LOG("INGwInfraStreamManager");

#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>
#include <INGwInfraStreamManager/INGwIfrSmStreamMgr.h>
#include <INGwInfraStreamManager/INGwIfrSmFileHandler.h>

namespace BpGenUtil
{

INGwIfrSmAppStreamer *defaultStreamer = NULL;

INGwIfrSmAppStreamer::INGwIfrSmAppStreamer(const char *inname, const char *filename)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Initializing INGwIfrSmAppStreamer [%s]", 
                 _name.c_str());

   _name = inname;
   _logStatus = false;

   pthread_mutex_init(&_lock, NULL);

   try
   {
      if(filename == NULL)
      {
         _handler = INGwIfrSmStreamMgr::getInstance().registerStreamer(this, "Stdout");
      }
      else
      {
         _handler = INGwIfrSmStreamMgr::getInstance().registerStreamer(this, filename);
      }
   }
   catch(...)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "INGwIfrSmAppStreamer [%s] initialization failed", 
                    _name.c_str());
      pthread_mutex_destroy(&_lock);
      throw;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Initialized INGwIfrSmAppStreamer [%s]", _name.c_str());
}

INGwIfrSmAppStreamer::~INGwIfrSmAppStreamer()
{
   INGwIfrSmStreamMgr::getInstance().deregisterStreamer(this);
   pthread_mutex_destroy(&_lock);

   logger.logMsg(ALWAYS_FLAG, 0, "INGwIfrSmAppStreamer [%s] destroyed.", _name.c_str());
}

std::string INGwIfrSmAppStreamer::getName()
{
   return _name;
}

void INGwIfrSmAppStreamer::setFileHandle(INGwIfrSmFileHandler *inhandler)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Changing File [%s] for [%s].", 
                 inhandler->toLog().c_str(), _name.c_str());

   pthread_mutex_lock(&_lock);
   _handler = inhandler;
   pthread_mutex_unlock(&_lock);
}

INGwIfrSmFileHandler * INGwIfrSmAppStreamer::getHandler()
{
   return _handler;
}

int INGwIfrSmAppStreamer::log(const char *format, ...)
{
   if(!_logStatus)
   {
      return 0;
   }

   va_list varg;
   va_start(varg, format);

   pthread_mutex_lock(&_lock);

   int ret = _handler->log(format, varg);

   pthread_mutex_unlock(&_lock);

   va_end(varg);

   return ret;
}

bool INGwIfrSmAppStreamer::isLoggable()
{
   return _logStatus;
}

void INGwIfrSmAppStreamer::setLoggable(bool inStatus)
{
   logger.logMsg(ALWAYS_FLAG, 0, "INGwIfrSmAppStreamer [%s] logStatus [%s]", 
                 _name.c_str(), _logStatus ? "True" : "False");

   _logStatus = inStatus;

   INGwIfrSmStreamMgr::getInstance().checkFileHandlerStatus(_handler);
}

std::string INGwIfrSmAppStreamer::toLog()
{
   std::string output = "INGwIfrSmAppStreamer[ Name :";
   output += _name;
   output += " LogStatus :";
   output += (_logStatus ? "True" : "False");
   output += _handler->toLog();
   output += " ] ";
   return output;
}

};
