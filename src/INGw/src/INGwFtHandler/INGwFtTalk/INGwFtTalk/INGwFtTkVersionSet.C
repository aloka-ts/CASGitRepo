/*------------------------------------------------------------------------------
         File: INGwFtTkVersionSet.C
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 03-Jan-2004
  Description: App version negotiaion.
      History: Maintained in clearcase
    Copyright: 2004, BayPackets
------------------------------------------------------------------------------*/

#include <Util/Logger.h>
LOG("INGwFtTalk");
#include <INGwFtTalk/INGwFtTkVersionSet.h>

#include <string>
#include <strstream>
#include <algorithm>
#include <iterator>

using namespace std;

typedef VersionInfo::const_iterator VersionInfoCIt;

ostream& operator<<(ostream& out, const VersionInfo& inSet)
{
   copy(inSet.begin(), inSet.end(), 
        ostream_iterator<VersionInfo::value_type, char>(out, ","));
   return out;
}

string INGwFtTkVersionSet::toString() const
{
   ostrstream stringData;
   stringData << _info;
   stringData <<'\0' ;

   string ret;

   char *data = stringData.str();

   if(data != NULL)
   {
      data[strlen(data) - 1] = '\0';
      ret = data;
      delete []data;
   }

   return ret;
}

INGwFtTkVersionSet INGwFtTkVersionSet::toVersionSet(const string &data) 
{
   INGwFtTkVersionSet ret;

   if(data.empty())
   {
      return ret;
   }

   string::const_iterator dataStart = data.begin();
   string::const_iterator dataEnd   = data.end();
   string::const_iterator dataCurr  = dataStart;

   while(dataCurr != dataEnd)
   {
      if(*dataCurr == ',')
      {
         string result = string(dataStart, dataCurr);
         ret._info.insert(atoi(result.c_str()));
         dataStart = dataCurr;
         ++dataStart;
      }

      ++dataCurr;
   }

   string result = string(dataStart, dataCurr);
   ret._info.insert(atoi(result.c_str()));

   ret._info.erase(0);
   return ret;
}

int INGwFtTkVersionSet::findVersion(int subCompID, const INGwFtTkVersionSet &peerVersion) const
{
   string output = toString();
   logger.logMsg(ALWAYS_FLAG, 0, "SubCompID [%d] our Version[%s]", subCompID, 
                 output.c_str());

   output = peerVersion.toString();
   logger.logMsg(ALWAYS_FLAG, 0, "SubCompID [%d] peer Version[%s]", subCompID, 
                 output.c_str());

   VersionInfo result;

   set_intersection(_info.begin(), _info.end(),
                    peerVersion._info.begin(), peerVersion._info.end(),
                    inserter(result, result.begin()), greater<int>());

   result.erase(0);

   if(result.empty())
   {
      logger.logMsg(ALWAYS_FLAG, 0, "No compatible version for [%d]", 
                    subCompID);
      return 0;
   }

   VersionInfoCIt cit = result.begin();

   logger.logMsg(ALWAYS_FLAG, 0, "Negotiated version [%d] [%d]", 
                 subCompID, (*cit));

   return (*cit);
}

bool INGwFtTkVersionSet::contains(int version) const
{
   if(_info.find(version) == _info.end())
   {
      return false;
   }

   return true;
}

void INGwFtTkVersionSet::insert(int version)
{
   if(version == 0)
   {
      return;
   }

   _info.insert(version);
   return;
}
