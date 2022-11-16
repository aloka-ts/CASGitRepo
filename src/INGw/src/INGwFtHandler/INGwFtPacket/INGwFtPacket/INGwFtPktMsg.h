//////////////////////////////////////////////////////////////////////////
//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwFtPktMsg.h
//
//     Desc:     Base class for replication msg
//               
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   23/11/07     Initial Creation
//********************************************************************


#ifndef INGW_FT_PKT_MSG_H_
#define INGW_FT_PKT_MSG_H_

#include <sys/types.h>
#include <netinet/in.h>
#include <inttypes.h>
#include <time.h>
#include <stdlib.h>
#include <string.h>

#include <string>
#include <sstream>

#include <INGwFtPacket/INGwFtPktMsgDefine.h>


/** This is the base class for all the messages excahnged between
 *  INGw and partner INGw. It contains the fields
 *  which form the header of the message.
 */
class INGwFtPktMsg
{
    public:

        /** The constructor
         */
        INGwFtPktMsg(void);

        /** The constructor
         */
        inline virtual ~INGwFtPktMsg();

        /** The accessor method for message type
         */
        inline short getMsgType(void) const;

        /** The mutator method for message type
         */
        inline void setMsgType(short asMsgType);

        /** The accessor method for sender Id
         */
        inline short getSender(void) const;

        /** The mutator method for sender Id
         */
        inline void setSender(short asSenderId);

        /** The accessor method for receiver Id
         */
        inline short getReceiver(void) const;

        /** The mutator method for receiver Id
         */
        inline void setReceiver(short asReceiverId);

        /** The accessor method for the major sequence number
         */
        inline short getMajor(void) const;

        /** The mutator method for the major sequence number
         */
        inline void setMajor(short asMajor);

        /** The accessor method for the minor sequence number
         */
        inline short getMinor(void) const;

        /** The mutator method for the minor sequence number
         */
        inline void setMinor(short asMinor);

        inline int getMsgStatus(void) const;
        inline bool getLoggingStatus(void) const;
        inline void setLoggingStatus(bool abStatus);
        inline bool getReplayStatus(void) const;
        inline void setReplayStatus(bool abStatus);
        inline bool getMsgAbortStatus(void) const;
        inline void setMsgAbortStatus(bool abStatus);

        void markMsgGenTime(void);
        void markMsgSendRecvTime(void);

        /** This method depacketizes the message. The packet
         *  is not assumed to be NULL terminated. It returns the
         *  the number of bytes depacketized.
         */
        virtual int depacketize(const char* apcData, int asSize, 
                                  int version) = 0;

        /** This method packetizes the message. The packet
         *  is not NULL terminated. It returns the size of the packet.
         */
        virtual int packetize(char** apcData, int version) = 0;

        /** This method sets the values of the data members after
         *  converting them to the relevant data types.
         */
        virtual void setValue(short asId, const char* apcValue);

        /** This method dumps the message in the return string
         */
        virtual std::string toLog(void) const;

        static short 
        getVLString(const char* apcData, short asOffset,
            std::string& arString);

        static short 
        getFLString(const char* apcData,
            short asOffset, short asSize, std::string& arString);

    protected:

        /** This method packetizes the information in the INGwFtPktMsg.
         *  However the size of the buffer allocated is as per the
         *  argument passed.  The packet is not NULL terminated.
         *  It returns the bytes packetized.
         */
        virtual int createPacket(int asSize, char** apcData, int version);
        virtual int bcreatePacket(int asSize, char** apcData, int version);

        struct INGwFtPktMsg_struct {
             short msMsgType;
             short msSender;
             short msReceiver;
             short msMajorSeqNum;
             short msMinorSeqNum;
             long  mlMsgGenTimeStamp;
             int   miMsgStatus;

             INGwFtPktMsg_struct() {
                  msMsgType         = 0;
                  msSender          = 0;
                  msReceiver        = 0;
                  msMajorSeqNum     = 0;
                  msMinorSeqNum     = 0;
                  mlMsgGenTimeStamp = 0;
                  miMsgStatus       = 0;
             }
        };

        INGwFtPktMsg_struct mMsgData;

        /** Time stamp just for logging
         */
        char mcSendRecvTimeStamp[64];

    private:

        /** Assignment operator (Not implemented)
         */
        INGwFtPktMsg& operator= (const INGwFtPktMsg& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwFtPktMsg(const INGwFtPktMsg& arSelf);

};

INGwFtPktMsg::~INGwFtPktMsg() { }

short
INGwFtPktMsg::getMsgType(void) const { return mMsgData.msMsgType; }

void
INGwFtPktMsg::setMsgType(short asMsgType) { mMsgData.msMsgType = asMsgType; }

short
INGwFtPktMsg::getSender(void) const { return mMsgData.msSender; }

void
INGwFtPktMsg::setSender(short asSenderId) { mMsgData.msSender = asSenderId; }

short
INGwFtPktMsg::getReceiver(void) const { return mMsgData.msReceiver; }

void
INGwFtPktMsg::setReceiver(short asReceiverId) { mMsgData.msReceiver = asReceiverId; }

short 
INGwFtPktMsg::getMajor(void) const { return mMsgData.msMajorSeqNum; }

void
INGwFtPktMsg::setMajor(short asMajor) { mMsgData.msMajorSeqNum = asMajor; }

short 
INGwFtPktMsg::getMinor(void) const { return mMsgData.msMinorSeqNum; }

void
INGwFtPktMsg::setMinor(short asMinor) { mMsgData.msMinorSeqNum = asMinor; }

int 
INGwFtPktMsg::getMsgStatus(void) const { return mMsgData.miMsgStatus; }

bool 
INGwFtPktMsg::getLoggingStatus(void) const { return (mMsgData.miMsgStatus & BPMSG_MSK_LOGGING_STATUS); }

void 
INGwFtPktMsg::setLoggingStatus(bool abStatus)
{
  if(abStatus)
    mMsgData.miMsgStatus = (mMsgData.miMsgStatus | BPMSG_MSK_LOGGING_STATUS);
  else
    mMsgData.miMsgStatus = (mMsgData.miMsgStatus & ~BPMSG_MSK_LOGGING_STATUS);
}

bool 
INGwFtPktMsg::getReplayStatus(void) const { return (mMsgData.miMsgStatus & BPMSG_MSK_REPLAY_STATUS); }

void INGwFtPktMsg::setReplayStatus(bool abStatus)
{
  if(abStatus)
    mMsgData.miMsgStatus = (mMsgData.miMsgStatus | BPMSG_MSK_REPLAY_STATUS);
  else
    mMsgData.miMsgStatus = (mMsgData.miMsgStatus & ~BPMSG_MSK_REPLAY_STATUS);
}

bool
INGwFtPktMsg::getMsgAbortStatus(void) const { return (mMsgData.miMsgStatus & BPMSG_MSK_ABORT_STATUS); }

void INGwFtPktMsg::setMsgAbortStatus(bool abStatus)
{
  if(abStatus)
    mMsgData.miMsgStatus = (mMsgData.miMsgStatus | BPMSG_MSK_ABORT_STATUS);
  else
    mMsgData.miMsgStatus = (mMsgData.miMsgStatus & ~BPMSG_MSK_ABORT_STATUS);
}

// EOF INGwFtPktMsg.icc
#endif 

// EOF INGwFtPktMsg.h
