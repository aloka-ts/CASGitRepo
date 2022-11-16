/*------------------------------------------------------------------------------
         File: INGwIfrSmStreamMgr.h
  Application: General utility
    Component: INGwIfrSmStreamMgr
   Programmer: S.Suriya prakash
      Written: 19-Aug-2003
  Description: Application Output manager
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __STREAM_MGR_H__
#define __STREAM_MGR_H__

#include <string>
#include <map>
#include <vector>
#include <set>
#include <INGwInfraStreamManager/INGwIfrSmFileHandler.h>
#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

namespace BpGenUtil
{
   typedef std::map<std::string, INGwIfrSmAppStreamer *> AppStreamerMap;
   typedef AppStreamerMap::iterator AppStreamerMapIt;
   typedef AppStreamerMap::const_iterator AppStreamerMapCIt;

   typedef std::set<INGwIfrSmFileHandler *> FileList;
   typedef FileList::iterator FileListIt;
   typedef FileList::const_iterator FileListCIt;

   typedef struct
   {
      std::string name1;
      std::string name2;
      bool flag;
   }ScreenDetail;

   typedef std::vector<ScreenDetail> DetailList;
   typedef DetailList::iterator DetailListIt;
   typedef DetailList::const_iterator DetailListCIt;

   class INGwIfrSmStreamMgr
   {
      private:

         static INGwIfrSmStreamMgr *_instance;
         static void * _streamMonitor(void *);

      private:

         INGwIfrSmStreamMgr();
         ~INGwIfrSmStreamMgr();

      public:

         static INGwIfrSmStreamMgr & getInstance();

      private:

         AppStreamerMap _appStreamers;
         FileList _fileList;
         pthread_mutex_t _lock;
         pthread_mutexattr_t _lockattr;

         int _limit;

      public:

         INGwIfrSmFileHandler * registerStreamer(INGwIfrSmAppStreamer *appStream, 
                                        const char *filename);
         void deregisterStreamer(INGwIfrSmAppStreamer *appStream);

         void checkFileHandlerStatus(INGwIfrSmFileHandler *);

      private:

         INGwIfrSmFileHandler * _findHandle(const char *filename);
         bool _validateHandle(INGwIfrSmFileHandler *);

         void _startMonitor();

      public:

         DetailList getStreamList(); //Stream name, Filehandler name
         DetailList getFileList();   //Filehandler name, Filehandler filename

         void setStreamLog(std::string streamName, bool flag);
         void setFileLog(std::string handlerName, bool flag);

         void changeStreamDetail(std::string streamName, std::string fileName);
         void changeFileDetail(std::string handlerName, 
                               std::string newFilename);
         
         unsigned int getLimit();
         void setLimit(unsigned int limit);
         std::string toLog();
   };
};

#endif
