/************************************************************************
     Name:     Measurement Update Msg - includes

     Type:     C include file

     Desc:     This file provides access to Measurement update msg

     File:     MsrUpdateMsg.h

     Sid:      MsrUpdateMsg.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_UPDATE_MSG_H_
#define _MSR_UPDATE_MSG_H_

#include <iostream>
#include <vector>
#include <string>

class MsrUpdateMsg
{
  public:
    MsrUpdateMsg () {}
    ~MsrUpdateMsg () {}

    enum UpdateMsgType 
    {
      addParam,
      addEntity,
      addSet,
      addCounter,
      deleteSet,
      reconfigSet,
      reconfigCounter
    };

    UpdateMsgType meMsgType;

    struct
    {
      struct 
      {
          std::string mstrId;
          std::string mstrVersion;
          int miExecutionPriority;
          int miTimerInterval;
          int miMaxSetsInQueue;
          int miEnable;
          bool mbReset;
          std::string mstrEntityType;
          std::vector <std::string> mCounterList;
      } set;

      struct 
      {
          std::string mstrId;
          std::string mstrOid;
          std::string mstrMode;
          int miExecutionPriority;
          int miTimerInterval;
          int miCounterType;
          int miEnable;
      } counter;

      struct
      {
        std::string mstrCounter;
        std::string mstrEntity;
        std::string mstrId;
        int miPoolIndex;
      } param;

      struct 
      {
        std::string mstrCounter;
        std::string mstrId;
      } ent;

      struct
      {
        struct
        {
          std::string mstrId;
          std::string mstrVersion;
          int miExecutionPriority;
          int miTimerInterval;
          int miMaxSetsInQueue;
          std::vector <std::string> mCounterList;
          int miEnable;
          bool mbReset;
          std::string mstrEntityType;
        } set;

        struct
        {
          std::string mstrId;
          int miExecutionPriority;
          int miTimerInterval;
          int miEnable;
        } ctr;
      } recfg;
    } upd;
};

#endif /* _MSR_UPDATE_MSG_H_ */
