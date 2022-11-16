/*------------------------------------------------------------------------------
         File: INGwIfrSmStreamInf.C
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

#include <INGwInfraStreamManager/INGwIfrSmStreamInf.h>
#include <INGwInfraStreamManager/INGwIfrSmStreamMgr.h>
#include <unistd.h>
#include <strings.h>
#include <string.h>

namespace BpGenUtil
{

int INGwIfrSmStreamInf::menu(const char *head, StrVector detail, int fd)
{
   logger.logMsg(TRACE_FLAG, 0, "Menu : %s", head);
   char data[128];

   while(true)
   {
      bzero(data, 128);
      sprintf(data, "\n\n%.40s\n\n", head);
      write(fd, data, strlen(data));

      int count = 1;

      for(StrVectorIt it = detail.begin(); it != detail.end(); it++, count++)
      {
         bzero(data, 128);
         sprintf(data, "  %d)\t%.40s\n", count, (*it).c_str());
         write(fd, data, strlen(data));
      }

      bzero(data, 128);
      sprintf(data, "  0)\tReturn\n\nEnter the choice[0-%d]:", detail.size());
      write(fd, data, strlen(data));

      int ret = read(fd, data, 10);
      data[10] = '\0';

      if(ret <= 0)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Inf manage channel broke.");
         return -1;
      }

      ret = atoi(data);

      if(ret < 0 || ret > detail.size())
      {
         continue;
      }

      if(ret == 0)
      {
         return detail.size();
      }

      return ret - 1;
   }
}

int INGwIfrSmStreamInf::getnewFile(const char *head, std::string &result, int fd)
{
   char data[128];
   bzero(data, 128);
   sprintf(data, "\n\n%.40s", head);
   write(fd, data, strlen(data));

   char c = 0;
   int index = 0;

   for( ; index < 40; ) 
   {
      if(read(fd, &c, 1) < 0) 
      {
         return -1;
      }

      if(c == 13) 
      {
         continue;
      }
      else if(c == 10) 
      {
         break;
      }
      else 
      {
         data[index++] = c;
      }
   }

   data[index] = '\0';
   result = data;

   return 0;
}

int INGwIfrSmStreamInf::getFlag(const char *head, bool &flag, int fd)
{
   while(true)
   {
      std::string result;

      if(getnewFile(head, result, fd) < 0)
      {
         return -1;
      }

      if(result == "true")
      {
         flag = true;
         return 0;
      }

      if(result == "false")
      {
         flag = false;
         return 0;
      }
   }

   return -1;
}

void INGwIfrSmStreamInf::manage(int fd)
{
   logger.logMsg(TRACE_FLAG, 0, "Manage using [%d]", fd);

   StrVector mydata;

   mydata.push_back(std::string("Stream Detail"));
   mydata.push_back(std::string("File Detail"));
   mydata.push_back(std::string("Limits"));

   while(true)
   {
      int ret = menu("Stream Manager", mydata, fd);

      switch(ret)
      {
         case 0:
         {
            if(manageStream(fd) == -1)
            {
               return;
            }
            continue;
         }
         break;

         case 1:
         {
            if(manageFiles(fd) == -1)
            {
               return;
            }
            continue;
         }
         break;

         case 2:
         {
            if(manageLimits(fd) == -1)
            {
               return;
            }
            continue;
         }
         break;

         default:
            LogTrace(0, "End of manage.");
            return;
      }
   }
   return;
}

int INGwIfrSmStreamInf::manageLimits(int fd)
{
   while(true)
   {
      StrVector mydata;

      char value[128];
      bzero(value, 128);
      sprintf(value, "Modify Limit:\tCurr[%d]", 
              INGwIfrSmStreamMgr::getInstance().getLimit());
      mydata.push_back(value);

      int ret = menu("Stream Limits", mydata, fd);

      if(ret < 0)
      {
         return ret;
      }

      if(ret == mydata.size())
      {
         return 0;
      }

      if(manageStreamLimit(fd) == -1)
      {
         return -1;
      }
   }

   return 0;
}

int INGwIfrSmStreamInf::manageStreamLimit(int fd)
{
   std::string newLimit;

   if(getnewFile("Enter new Stream limit:", newLimit, fd) < 0)
   {
      return -1;
   }

   INGwIfrSmStreamMgr::getInstance().setLimit(atoi(newLimit.c_str()));
   return 0;
}

int INGwIfrSmStreamInf::manageStream(int fd)
{
   while(true)
   {
      DetailList detail = INGwIfrSmStreamMgr::getInstance().getStreamList();

      StrVector mydata;

      int maxTab = 0;

      for(DetailListIt it = detail.begin(); it != detail.end(); it++)
      {
         const ScreenDetail &currDetail = (*it);

         int len = currDetail.name1.length();
         len >>= 3;

         if(len > maxTab)
         {
            maxTab = len;
         }
      }

      maxTab++;

      logger.logMsg(TRACE_FLAG, 0, "MaxTab: [%d] ", maxTab);

      for(DetailListIt it = detail.begin(); it != detail.end(); it++)
      {
         const ScreenDetail &currDetail = (*it);

         std::string currData = currDetail.name1;

         int tabCount = currDetail.name1.length();
         tabCount >>= 3;

         for(int idx = tabCount; idx < maxTab; idx++)
         {
            currData += "\t";
         }
         currData += (currDetail.flag ? "True" : "False");
         currData += "\t";
         currData += currDetail.name2;

         mydata.push_back(currData);
      }

      int ret = menu("Stream Detail", mydata, fd);

      if(ret < 0)
      {
         return ret;
      }

      if(ret == detail.size())
      {
         return 0;
      }

      if(manageStreamDetail(detail[ret], fd) == -1)
      {
         return -1;
      }
   }

   return 0;
}

int INGwIfrSmStreamInf::manageStreamDetail(ScreenDetail detail, int fd)
{
   StrVector mydata;

   std::string tempData = "Change file: \t\t[Curr:";
   tempData += detail.name2;
   tempData += "]";

   mydata.push_back(tempData);

   tempData = "Change logStatus: \t[Curr:";
   tempData += (detail.flag ? "True]" : "False]");

   mydata.push_back(tempData);

   int ret = menu(detail.name1.c_str(), mydata, fd);

   if(ret < 0)
   {
      return ret;
   }

   if(ret == 2)
   {
      return 0;
   }

   switch(ret)
   {
      case 0:
      {
         std::string newFile;

         if(getnewFile("Enter new filename:", newFile, fd) < 0)
         {
            return -1;
         }

         INGwIfrSmStreamMgr::getInstance().changeStreamDetail(detail.name1, newFile);
      }
      break;

      case 1:
      {
         bool flag;

         if(getFlag("LogStatus: [true/false]: ", flag, fd) < 0)
         {
            return -1;
         }

         INGwIfrSmStreamMgr::getInstance().setStreamLog(detail.name1, flag);
      }
   }

   return 0;
}

int INGwIfrSmStreamInf::manageFiles(int fd)
{
   while(true)
   {
      DetailList detail = INGwIfrSmStreamMgr::getInstance().getFileList();

      StrVector mydata;

      int maxTab = 0;

      for(DetailListIt it = detail.begin(); it != detail.end(); it++)
      {
         const ScreenDetail &currDetail = (*it);

         int len = currDetail.name1.length();
         len >>= 3;

         if(len > maxTab)
         {
            maxTab = len;
         }
      }

      maxTab++;

      logger.logMsg(TRACE_FLAG, 0, "MaxTab: [%d] ", maxTab);

      for(DetailListIt it = detail.begin(); it != detail.end(); it++)
      {
         const ScreenDetail &currDetail = (*it);

         std::string currData = currDetail.name1;

         int len = currDetail.name1.length();
         len >>= 3;

         for(int idx = len; idx < maxTab; idx++)
         {
            currData += "\t";
         }

         currData += (currDetail.flag ? "True" : "False");
         currData += "\t";
         currData += currDetail.name2;

         mydata.push_back(currData);
      }

      int ret = menu("File Detail", mydata, fd);

      if(ret < 0)
      {
         return ret;
      }

      if(ret == detail.size())
      {
         return 0;
      }

      if(manageFileDetail(detail[ret], fd) == -1)
      {
         return -1;
      }
   }

   return 0;
}

int INGwIfrSmStreamInf::manageFileDetail(ScreenDetail detail, int fd)
{
   StrVector mydata;

   std::string tempData = "Change file: \t\t[Curr:";
   tempData += detail.name2;
   tempData += "]";

   mydata.push_back(tempData);

   tempData = "Change logStatus: \t[Curr:";
   tempData += (detail.flag ? "True]" : "False]");

   mydata.push_back(tempData);

   int ret = menu(detail.name1.c_str(), mydata, fd);

   if(ret < 0)
   {
      return ret;
   }

   if(ret == 2)
   {
      return 0;
   }

   switch(ret)
   {
      case 0:
      {
         std::string newFile;

         if(getnewFile("Enter new filename:", newFile, fd) < 0)
         {
            return -1;
         }

         INGwIfrSmStreamMgr::getInstance().changeFileDetail(detail.name1, newFile);
      }
      break;

      case 1:
      {
         bool flag;

         if(getFlag("LogStatus: [true/false]: ", flag, fd) < 0)
         {
            return -1;
         }

         INGwIfrSmStreamMgr::getInstance().setFileLog(detail.name1, flag);
      }
   }

   return 0;
}

};
