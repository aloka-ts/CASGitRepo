/*------------------------------------------------------------------------------
         File: INGwIfrSmStreamMgr.C
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
#include <INGwInfraStreamManager/INGwIfrSmStreamMgr.h>
#include <unistd.h>

namespace BpGenUtil
{

#if 0
   We are setting the max stream size to 1.7G we are maintaining a safe limit 
of around 400MB, This is needed as, stdout can be used without the INGwIfrSmStreamMgr. 
By the time INGwIfrSmStreamMgr checks (20 secs interval) the filesize, there can be 
additional data written on the stdout. If we havent kept the safelimit write 
into stdout by other part of application can cause core.
#endif

#define MAX_STREAM_SIZE_LIMIT   1700000000
#define DEF_STREAM_SIZE_LIMIT   1000000

INGwIfrSmStreamMgr * INGwIfrSmStreamMgr::_instance = NULL;

INGwIfrSmStreamMgr & INGwIfrSmStreamMgr::getInstance()
{
   if(_instance != NULL)
   {
      return *_instance;
   }

   _instance = new INGwIfrSmStreamMgr();
   return *_instance;
}

extern "C" void * INGwIfrSmStreamMgr::_streamMonitor(void *)
{
   pthread_detach(pthread_self());
   INGwIfrSmStreamMgr::getInstance()._startMonitor();
   return NULL;
}

INGwIfrSmStreamMgr::INGwIfrSmStreamMgr()
{
   LogAlways(0, "Constructing INGwIfrSmStreamMgr.");

   _limit = 0;

   const char *limitValue = getenv("MAX_STREAM_SIZE");

   if(limitValue == NULL)
   {
      _limit = MAX_STREAM_SIZE_LIMIT;
   }
   else
   {
      _limit = atoi(limitValue);
   }

   if(_limit > MAX_STREAM_SIZE_LIMIT || _limit <= 0)
   {
      _limit = MAX_STREAM_SIZE_LIMIT;
   }

   INGwIfrSmFileHandler *devnull = new INGwIfrSmFileHandler("NoOut");
   _fileList.insert(devnull);
   devnull->setLimit(_limit);

   INGwIfrSmFileHandler *outfile = new INGwIfrSmFileHandler("Stdout");
   _fileList.insert(outfile);
   outfile->setLimit(_limit);

   pthread_mutexattr_init(&_lockattr);
   pthread_mutexattr_settype(&_lockattr, PTHREAD_MUTEX_RECURSIVE);
   pthread_mutex_init(&_lock, &_lockattr);

   _instance = this;

   LogAlways(0, "INGwIfrSmStreamMgr constructed.");

   defaultStreamer = new INGwIfrSmAppStreamer("Default");
   defaultStreamer->setLoggable(true);

   pthread_t monitorThread;
   pthread_create(&monitorThread, NULL, _streamMonitor, NULL);
}

INGwIfrSmStreamMgr::~INGwIfrSmStreamMgr()
{
   pthread_mutex_destroy(&_lock);
}

INGwIfrSmFileHandler * INGwIfrSmStreamMgr::registerStreamer(INGwIfrSmAppStreamer *appStream,
                                          const char *filename)
{
   INGwIfrSmFileHandler *ret = NULL;

   pthread_mutex_lock(&_lock);

   try
   {
      std::string streamName = appStream->getName();

      if(_appStreamers.find(streamName) != _appStreamers.end())
      {
         logger.logMsg(ERROR_FLAG, 0, "INGwIfrSmAppStreamer for [%s] already exists.",
                       streamName.c_str());
         throw "Duplicate entry";
      }

      ret = _findHandle(filename);

      if(ret == NULL)
      {
         ret = new INGwIfrSmFileHandler(filename);
         _fileList.insert(ret);
         ret->setLimit(_limit);
      }

      _appStreamers[streamName] = appStream;
      appStream->setFileHandle(ret);

      checkFileHandlerStatus(ret);
   }
   catch(...)
   {
      logger.logMsg(ERROR_FLAG, 0, "Exception in stream register.");
      pthread_mutex_unlock(&_lock);
      throw;
   }

   pthread_mutex_unlock(&_lock);

   logger.logMsg(ALWAYS_FLAG, 0, "%s", toLog().c_str());
   return ret;
}

void INGwIfrSmStreamMgr::deregisterStreamer(INGwIfrSmAppStreamer *appStream)
{
   pthread_mutex_lock(&_lock);

   std::string streamName = appStream->getName();

   if(_appStreamers.find(streamName) == _appStreamers.end())
   {
      logger.logMsg(ERROR_FLAG, 0, "INGwIfrSmAppStreamer for [%s] doesnt exists.",
                    streamName.c_str());
      pthread_mutex_unlock(&_lock);
      return;
   }

   _appStreamers.erase(streamName);

   INGwIfrSmFileHandler *handle = appStream->getHandler();

   if(_validateHandle(handle) == false)
   {
      _fileList.erase(handle);
      delete handle;
   }

   pthread_mutex_unlock(&_lock);
   logger.logMsg(ALWAYS_FLAG, 0, "%s", toLog().c_str());
   return;
}

void INGwIfrSmStreamMgr::checkFileHandlerStatus(INGwIfrSmFileHandler *handle)
{
   pthread_mutex_lock(&_lock);

   if((handle->getName() == "Stdout") || (handle->getName() == "NoOut"))
   {
      pthread_mutex_unlock(&_lock);
      return;
   }

   bool status = false;

   for(AppStreamerMapIt it = _appStreamers.begin(); it != _appStreamers.end(); 
       it++)
   {
      if(it->second->getHandler() == handle)
      {
         status = it->second->isLoggable();

         if(status)
         {
            handle->setLoggable(true);
            pthread_mutex_unlock(&_lock);
            return;
         }
      }
   }

   handle->setLoggable(false);

   pthread_mutex_unlock(&_lock);
   return;
}

INGwIfrSmFileHandler * INGwIfrSmStreamMgr::_findHandle(const char *filename)
{
   for(FileListIt it = _fileList.begin(); it != _fileList.end(); it++)
   {
      INGwIfrSmFileHandler *curr = (*it);

      if((curr->getFilename() == filename) ||
         (curr->getName() == filename))
      {
         return curr;
      }
   }

   return NULL;
}

bool INGwIfrSmStreamMgr::_validateHandle(INGwIfrSmFileHandler *handle)
{
   if((handle->getName() == "NoOut") ||
      (handle->getName() == "Stdout"))
   {
      return true;
   }

   for(AppStreamerMapIt it = _appStreamers.begin(); it != _appStreamers.end(); 
       it++)
   {
      INGwIfrSmAppStreamer *curr = it->second;

      if(curr->getHandler() == handle)
      {
         return true;
      }
   }

   return false;
}

DetailList INGwIfrSmStreamMgr::getStreamList()
{
   DetailList ret;

   pthread_mutex_lock(&_lock);

   for(AppStreamerMapIt it = _appStreamers.begin(); it != _appStreamers.end(); 
       it++)
   {
      ScreenDetail currDetail;
      currDetail.name1 = it->first;

      INGwIfrSmFileHandler *handler = it->second->getHandler();
      currDetail.flag = it->second->isLoggable();
      currDetail.name2 = handler->getName();

      ret.push_back(currDetail);
   }

   pthread_mutex_unlock(&_lock);

   return ret;
}

DetailList INGwIfrSmStreamMgr::getFileList()
{
   DetailList ret;

   pthread_mutex_lock(&_lock);

   for(FileListIt it = _fileList.begin(); it != _fileList.end(); it++)
   {
      INGwIfrSmFileHandler *curr = (*it);

      ScreenDetail currDetail;

      currDetail.name1 = curr->getName();
      currDetail.name2 = curr->getFilename();
      currDetail.flag = curr->isLoggable();

      ret.push_back(currDetail);
   }

   pthread_mutex_unlock(&_lock);
   return ret;
}

void INGwIfrSmStreamMgr::setStreamLog(std::string streamName, bool flag)
{
   pthread_mutex_lock(&_lock);

   AppStreamerMapIt it = _appStreamers.find(streamName);

   if(it != _appStreamers.end())
   {
      it->second->setLoggable(flag);
   }

   pthread_mutex_unlock(&_lock);
   logger.logMsg(ALWAYS_FLAG, 0, "%s", toLog().c_str());
   return;
}

void INGwIfrSmStreamMgr::setFileLog(std::string handlerName, bool flag)
{
   pthread_mutex_lock(&_lock);

   for(FileListIt it = _fileList.begin(); it != _fileList.end(); it++)
   {
      INGwIfrSmFileHandler *curr = (*it);

      if((curr->getName() == handlerName) ||
         (curr->getFilename() == handlerName))
      {
         curr->setLoggable(flag);
         break;
      }
   }

   pthread_mutex_unlock(&_lock);
   logger.logMsg(ALWAYS_FLAG, 0, "%s", toLog().c_str());
   return;
}

void INGwIfrSmStreamMgr::changeStreamDetail(std::string streamName, std::string fileName)
{
   pthread_mutex_lock(&_lock);

   AppStreamerMapIt it = _appStreamers.find(streamName);

   if(it == _appStreamers.end())
   {
      pthread_mutex_unlock(&_lock);
      return;
   }

   INGwIfrSmAppStreamer *currappStream = it->second;
   INGwIfrSmFileHandler *prevHandler = currappStream->getHandler();

   if((prevHandler->getName() == fileName) ||
      (prevHandler->getFilename() == fileName))
   {
      pthread_mutex_unlock(&_lock);
      return;
   }

   INGwIfrSmFileHandler *newHandler = _findHandle(fileName.c_str());

   if(newHandler == NULL)
   {
      try
      {
         newHandler = new INGwIfrSmFileHandler(fileName.c_str());
         _fileList.insert(newHandler);
         newHandler->setLimit(_limit);
      }
      catch(...)
      {
         pthread_mutex_unlock(&_lock);
         return;
      }
   }

   currappStream->setFileHandle(newHandler);

   checkFileHandlerStatus(newHandler);
   checkFileHandlerStatus(prevHandler);

   if(_validateHandle(prevHandler) == false)
   {
      _fileList.erase(prevHandler);
      delete prevHandler;
   }

   pthread_mutex_unlock(&_lock);
   logger.logMsg(ALWAYS_FLAG, 0, "%s", toLog().c_str());
   return;
}

void INGwIfrSmStreamMgr::changeFileDetail(std::string handlerName, 
                                 std::string newFileName)
{
   pthread_mutex_lock(&_lock);

   INGwIfrSmFileHandler *oldHandle = _findHandle(handlerName.c_str());

   if(oldHandle == NULL)
   {
      pthread_mutex_unlock(&_lock);
      return;
   }

   INGwIfrSmFileHandler *newHandle = _findHandle(newFileName.c_str());

   if(oldHandle == newHandle)
   {
      pthread_mutex_unlock(&_lock);
      return;
   }

   if(newHandle == NULL)
   {
      oldHandle->changeFile(newFileName.c_str());
      pthread_mutex_unlock(&_lock);
      logger.logMsg(ALWAYS_FLAG, 0, "%s", toLog().c_str());
      return;
   }

   for(AppStreamerMapIt it = _appStreamers.begin(); it != _appStreamers.end(); 
       it++)
   {
      if(it->second->getHandler() == oldHandle)
      {
         it->second->setFileHandle(newHandle);
      }
   }

   checkFileHandlerStatus(oldHandle);
   checkFileHandlerStatus(newHandle);

   if(_validateHandle(oldHandle) == false)
   {
      _fileList.erase(oldHandle);
      delete oldHandle;
   }

   pthread_mutex_unlock(&_lock);
   logger.logMsg(ALWAYS_FLAG, 0, "%s", toLog().c_str());
}

std::string INGwIfrSmStreamMgr::toLog()
{
   std::string output = "INGwIfrSmStreamMgr: \n Streams [\n";

   pthread_mutex_lock(&_lock);

   for(AppStreamerMapIt it = _appStreamers.begin(); it != _appStreamers.end(); 
       it++)
   {
      output += "\t";
      output += it->second->toLog();
      output += "\n";
   }

   output += "]\nFiles [\n";

   for(FileListIt it = _fileList.begin(); it != _fileList.end(); it++)
   {
      INGwIfrSmFileHandler *curr = (*it);
      output += "\t";
      output += (*it)->toLog();
      output += "\n";
   }

   output += "]\n";

   pthread_mutex_unlock(&_lock);
   return output;
}

unsigned int INGwIfrSmStreamMgr::getLimit()
{
   pthread_mutex_lock(&_lock);
   unsigned int ret = _limit;
   pthread_mutex_unlock(&_lock);
   return ret;
}

void INGwIfrSmStreamMgr::setLimit(unsigned int limit)
{
   if(limit > MAX_STREAM_SIZE_LIMIT)
   {
      limit = MAX_STREAM_SIZE_LIMIT;
   }

   pthread_mutex_lock(&_lock);

   _limit = limit;

   for(FileListIt it = _fileList.begin(); it != _fileList.end(); it++)
   {
      INGwIfrSmFileHandler *curr = (*it);
      curr->setLimit(limit);
   }

   pthread_mutex_unlock(&_lock);

   return;
}

void INGwIfrSmStreamMgr::_startMonitor()
{
   while(true)
   {
      pthread_mutex_lock(&_lock);

      for(FileListIt it = _fileList.begin(); it != _fileList.end(); it++)
      {
         INGwIfrSmFileHandler *curr = (*it);
         curr->checkLimit();
      }

      pthread_mutex_unlock(&_lock);

      sleep(20);
   }
}

};

