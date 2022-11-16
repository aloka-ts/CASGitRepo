/*------------------------------------------------------------------------------
         File: INGwIfrSmStreamInf.h
  Application: General utility
    Component: StreamMgr
   Programmer: S.Suriya prakash
      Written: 19-Aug-2003
  Description: Application Output manager
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __STREAM_INF_H__
#define __STREAM_INF_H__

#include <INGwInfraStreamManager/INGwIfrSmStreamMgr.h>

#include <vector>
#include <string>

namespace BpGenUtil
{
   typedef std::vector<std::string> StrVector;
   typedef StrVector::iterator StrVectorIt;
   typedef StrVector::const_iterator StrVectorCIt;

   class INGwIfrSmStreamInf
   {
      private:

         static int menu(const char *head, StrVector detail, int fd);
         static int getnewFile(const char *head, std::string &result, int fd);
         static int getFlag(const char *head, bool &flag, int fd);
         static int manageStream(int fd);
         static int manageStreamDetail(ScreenDetail detail, int fd);
         static int manageFiles(int fd);
         static int manageFileDetail(ScreenDetail detail, int fd);
         static int manageLimits(int fd);
         static int manageStreamLimit(int fd);

      public:

         static void manage(int fd);
   };
};

#endif
