/*------------------------------------------------------------------------------
         File: INGwIfrSmFileHandler.C
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

#include <INGwInfraStreamManager/INGwIfrSmFileHandler.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>

namespace BpGenUtil
{

INGwIfrSmFileHandler::INGwIfrSmFileHandler(const char *inname)
{
   logger.logMsg(ALWAYS_FLAG, 0, "INGwIfrSmFileHandler [%s] constructor.", inname);

   _len = 0;
   _limit = 0;
   _name = inname;
   _filename = inname;
   _logStatus = true;
   _fd = -1;
   _fp = NULL;

   if(_name == "Stdout")
   {
      _fd = 1;
      _fp = fdopen(_fd, "a");

      struct stat mystat;
      memset(&mystat, 0, sizeof(mystat));
      fstat(_fd, &mystat);

      _len = mystat.st_size;
   }
   else if(_name == "NoOut")
   {
      _fd = open("/dev/null", O_WRONLY | O_CREAT, 0644);

      if(_fd != -1)
      {
         _fp = fdopen(_fd, "a");
      }
   }
   else
   {
      _logStatus = false;
   }

   if(_logStatus && (_fp == NULL))
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Error constructing INGwIfrSmFileHandler [%s]", 
                    inname);
      throw ("Construct error.");
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Successfully constructed [%s].", inname);
   pthread_mutex_init(&_lock, NULL);
}

INGwIfrSmFileHandler::~INGwIfrSmFileHandler()
{
   if(_fp != NULL)
   {
      fclose(_fp);
      _fp = NULL;
   }

   _logStatus = false;
   pthread_mutex_destroy(&_lock);
}

void INGwIfrSmFileHandler::setLimit(unsigned int inlimit)
{
   pthread_mutex_lock(&_lock);

   _limit = inlimit;
   _checkLimit();

   pthread_mutex_unlock(&_lock);
}

void INGwIfrSmFileHandler::checkLimit()
{
   pthread_mutex_lock(&_lock);

   _checkLimit();

   pthread_mutex_unlock(&_lock);
}

int INGwIfrSmFileHandler::log(const char *format, va_list arg)
{
   pthread_mutex_lock(&_lock);

   if(!_logStatus)
   {
      pthread_mutex_unlock(&_lock);
      return 0;
   }

   int ret = vfprintf(_fp, format, arg);
   fflush(_fp);

   _len += ret;

   if(_len > _limit)
   {
      _checkLimit();
   }

   pthread_mutex_unlock(&_lock);

   return ret;
}

int INGwIfrSmFileHandler::_openFile()
{
   if(_fp != NULL)
   {
      fclose(_fp);
   }

   _fp = NULL;
   _len = 0;

   const char *logDir = getenv("LOG_OUTPUT_DIR");

   std::string fullpath;

   if(logDir)
   {
      fullpath = logDir;
   }
   else
   {
      fullpath = ".";
   }

   fullpath += "/";
   fullpath += _filename;

   int newFD = open(fullpath.c_str(), O_WRONLY | O_CREAT | O_APPEND, 0644);

   if(newFD == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error opening file [%s] [%s]", 
                    fullpath.c_str(), strerror(errno));
      return -1;
   }

   _fp = fdopen(newFD, "a");

   if(_fp == NULL)
   {
      close(newFD);

      return -1;
   }

   _fd = newFD;

   struct stat mystat;
   memset(&mystat, 0, sizeof(mystat));
   fstat(_fd, &mystat);

   _len = mystat.st_size;

   logger.logMsg(ALWAYS_FLAG, 0, "Size of open stream [%s] [%d]", 
                 fullpath.c_str(), mystat.st_size);

   return 0;
}

void INGwIfrSmFileHandler::setLoggable(bool inStatus)
{
   pthread_mutex_lock(&_lock);

   if(inStatus == _logStatus)
   {
      pthread_mutex_unlock(&_lock);
      return;
   }

   if((_name == "NoOut") || (_name == "Stdout"))
   {
      _logStatus = inStatus;
      pthread_mutex_unlock(&_lock);
      return;
   }

   if(inStatus == false)
   {
      _logStatus = false;
      fclose(_fp);

      _fp = NULL;
      _fd = -1;
      _len = 0;
      pthread_mutex_unlock(&_lock);
      return;
   }

   if(_openFile() == -1)
   {
      _logStatus = false;
   }
   else
   {
      _logStatus = true;
   }

   _checkLimit();

   pthread_mutex_unlock(&_lock);
   return;
}

bool INGwIfrSmFileHandler::isLoggable()
{
   return _logStatus;
}

int INGwIfrSmFileHandler::changeFile(const char *filename)
{
   pthread_mutex_lock(&_lock);

   if(_name == "NoOut")
   {
      pthread_mutex_unlock(&_lock);
      return 0;
   }

   const char *logDir = getenv("LOG_OUTPUT_DIR");

   std::string fullpath;

   if(logDir)
   {
      fullpath = logDir;
   }
   else
   {
      fullpath = ".";
   }

   fullpath += "/";
   fullpath += filename;

   int newFD = open(fullpath.c_str(), O_WRONLY | O_CREAT | O_APPEND, 0644);

   if(newFD == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error opening file [%s] [%s]", 
                    fullpath.c_str(), strerror(errno));
      pthread_mutex_unlock(&_lock);
      return -1;
   }

   if((_name != "Stdout") && (_logStatus == false))
   {
      _name     = filename;
      _filename = filename;

      close(newFD);

      pthread_mutex_unlock(&_lock);
      return 0;
   }

   if(_name == "Stdout")
   {
      if(dup2(newFD, 2) == -1)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Error changing error stream. [%s]",
                       strerror(errno));
         close(newFD);
         pthread_mutex_unlock(&_lock);
         return -1;
      }
   }
   else
   {
      _name = filename;
   }

   if(dup2(newFD, _fd) == -1)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Error changing file. [%s] [%s]",
                    fullpath.c_str(), strerror(errno));
      close(newFD);
      pthread_mutex_unlock(&_lock);
      return -1;
   }

   close(newFD);
   _filename = filename;

   struct stat mystat;
   memset(&mystat, 0, sizeof(mystat));
   fstat(_fd, &mystat);

   _len = mystat.st_size;

   _checkLimit();

   pthread_mutex_unlock(&_lock);

   return 0;
}

std::string INGwIfrSmFileHandler::toLog()
{
   std::string output = "INGwIfrSmFileHandler[ Name :";
   output += _name;
   output += " File :";
   output += _filename;
   output += " ] ";

   return output;
}

std::string INGwIfrSmFileHandler::getFilename()
{
   return _filename;
}

std::string INGwIfrSmFileHandler::getName()
{
   return _name;
}

void INGwIfrSmFileHandler::_checkLimit()
{
   if(_name == "Stdout")
   {
      struct stat mystat;
      memset(&mystat, 0, sizeof(mystat));
      fstat(_fd, &mystat);

      _len = mystat.st_size;
   }
   else if(_logStatus == false)
   {
      return;
   }

   if(_len < _limit)
   {
      return;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Stream [%s] [%d] exceeds limit [%d]", 
                 _name.c_str(), _len, _limit);

   if(_name == "Stdout")
   {
      printf("File size exceed limit [%d] redirecting to /dev/null", _limit);
      fflush(stdout);

      int newFD = open("/dev/null", O_WRONLY | O_CREAT, 0644);

      if(newFD == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error opening /dev/null [%s]", 
                       strerror(errno));
         return;
      }

      if(dup2(newFD, 2) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error changing error stream [%s]", 
                       strerror(errno));
         close(newFD);
         return;
      }

      if(dup2(newFD, 1) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error changing output stream [%s]", 
                       strerror(errno));
         close(newFD);
         return;
      }

      _logStatus = false;

      close(newFD);
      _filename = "dev_null";
      logger.logMsg(ALWAYS_FLAG, 0, "Successfully changed the output to null.");
      return;
   }

   if(_fp != NULL)
   {
      fclose(_fp);
   }

   _fp = NULL;
   _len = 0;
   _fd = -1;

   const char *logDir = getenv("LOG_OUTPUT_DIR");

   std::string fullpath;

   if(logDir)
   {
      fullpath = logDir;
   }
   else
   {
      fullpath = ".";
   }

   fullpath += "/";
   fullpath += _filename;

   std::string newpath = fullpath;
   newpath += ".old";

   unlink(newpath.c_str());
   link(fullpath.c_str(), newpath.c_str());
   unlink(fullpath.c_str());

   if(_openFile() == -1)
   {
      _logStatus = false;
   }
   else
   {
      _logStatus = true;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Stream [%s] recreated", _name.c_str()); 

   return;
}

};
