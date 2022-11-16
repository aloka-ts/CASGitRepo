/*------------------------------------------------------------------------------
         File: VersionSet.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 03-Jan-2004
  Description: App version negotiaion.
      History: Maintained in clearcase
    Copyright: 2004, BayPackets
------------------------------------------------------------------------------*/

#ifndef __VERSION_SET_H__
#define __VERSION_SET_H__

#include <set>
#include <functional>

typedef std::set<int, std::greater<int> > VersionInfo;

class INGwFtTkVersionSet
{
   private:

      VersionInfo _info;

   public:

      int findVersion(int subCompID, const INGwFtTkVersionSet &peerVersion) const;
      std::string toString() const;
      bool contains(int version) const;

      void insert(int);

   public:

      static INGwFtTkVersionSet toVersionSet(const std::string &data);
};

#endif
