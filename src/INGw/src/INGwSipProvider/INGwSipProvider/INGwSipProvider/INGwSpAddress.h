//////////////////////////////////////////////////////////////////////////
//
//        Copyright (c) 2000, Bay Packets Inc.
//        All rights reserved.
//
//        FILE_NAME: INGwSpAddress.h
//
//////////////////////////////////////////////////////////////////////////

#ifndef INGW_SP_ADDRESS_H_
#define INGW_SP_ADDRESS_H_

#include <sys/types.h>
#include <netinet/in.h>
#include <inttypes.h>

#include <sstream>

#include <INGwFtPacket/INGwFtPktMsgDefine.h>

class INGwSpAddress
{
   public :

      enum INGwSpAddressInfoTag
      {
         ADDRESS             = 0x001,
         TYPE_OF_ADDR        = 0x002,
         NATURE_OF_ADDR      = 0x004,
         BG_ID               = 0x008,
         BG_TYPE             = 0x010,
         DISPLAY_NAME        = 0x020
      };

      enum INGwTypeOfAddress
      {
         UNKNOWN_TYPE_ADDR = 0,
         URL          = 1,
         E164         = 2,
         CAIN_PRIVATE      = 5
      };

      enum INGwNatureOfAddress
      {
         UNKNOWN_NATURE_ADDR = 0,
         LOCAL         		 = 1,
         NATIONAL      		 = 2, // Filled as 3 in AIN.
         INTERNATIONAL 		 = 3, // Filled as 4 in AIN.
         CAIN_ACCOUNT_CODE   = 61, 
         OPRREQ_SUBSCRIBER   = 113,
         OPRREQ_NATIONAL     = 114,
         OPRREQ_INTERNATIONAL= 115,
         OPRREQ_NO_NUM       = 116,
         CARRIER_NO_NUM      = 117,
         NOA_950_CALL		 = 118,
         TEST_LINE_CODE      = 119
      };

   private:

      char              mAddr[SIZE_OF_ADDR + 1];
      char              mpcDisplayName [SIZE_OF_DISPLAY_NAME + 1];
      INGwTypeOfAddress   mTypeOfAddr;
      INGwNatureOfAddress mNatureOfAddr;
      char              mBGId[SIZE_OF_BG_ID + 1];
      int               mBGType;
      int               mBitMask;

   public:

      INGwSpAddress() 
      { 
         initObject(true);
      }

      INGwSpAddress(const char *inAddr)
      {
         initObject(true);
         setAddress(inAddr);
      }

      void initObject(bool consFlag = false) 
      {
         if(consFlag)
         {
            mAddr[SIZE_OF_ADDR] = '\0';
            mBGId[SIZE_OF_BG_ID] = '\0';
            mpcDisplayName[SIZE_OF_DISPLAY_NAME] = '\0';
         }

         mAddr[0] = '\0';
         mpcDisplayName [0] = '\0';
         mTypeOfAddr   = UNKNOWN_TYPE_ADDR;
         mNatureOfAddr = UNKNOWN_NATURE_ADDR;
         mBGId[0]  = '\0';
         mBGType = 0;
         mBitMask = 0;
      }

      bool serialize
           (unsigned char*       apcData,
            int                  aiOffset,
            int&                 aiNewOffset,
            int                  aiMaxSize,
            bool                 abForceFullSerialization) 
      {
         // No check being done for max size and
         // full/partial serialization
         bool retVal = true;
         aiNewOffset = aiOffset;

         memcpy(apcData + aiNewOffset, mAddr, SIZE_OF_ADDR);
         aiNewOffset += SIZE_OF_ADDR;

         mTypeOfAddr = static_cast<INGwTypeOfAddress>(htonl(mTypeOfAddr));
         memcpy(apcData + aiNewOffset, &mTypeOfAddr, SIZE_OF_INT);
         mTypeOfAddr = static_cast<INGwTypeOfAddress>(ntohl(mTypeOfAddr));
         aiNewOffset += SIZE_OF_INT;

         mNatureOfAddr = static_cast<INGwNatureOfAddress>(htonl(mNatureOfAddr));
         memcpy(apcData + aiNewOffset, &mNatureOfAddr, SIZE_OF_INT);
         mNatureOfAddr = static_cast<INGwNatureOfAddress>(ntohl(mNatureOfAddr));
         aiNewOffset += SIZE_OF_INT;

         memcpy(apcData + aiNewOffset, mBGId, SIZE_OF_BG_ID);
         aiNewOffset += SIZE_OF_BG_ID;

         mBGType = htonl(mBGType);
         memcpy(apcData + aiNewOffset, &mBGType, SIZE_OF_INT);
         mBGType = ntohl(mBGType);
         aiNewOffset += SIZE_OF_INT;

         memcpy(apcData + aiNewOffset, mpcDisplayName, SIZE_OF_DISPLAY_NAME);
         aiNewOffset += SIZE_OF_DISPLAY_NAME;

         return retVal;
      }

      bool deserialize
          (const unsigned char* apcData,
           int                  aiOffset,
           int&                 aiNewOffset,
           int                  aiMaxSize) 
      {
         bool retVal = true;
         // No check being done for max size
         aiNewOffset = aiOffset;

         memcpy(mAddr, apcData + aiNewOffset, SIZE_OF_ADDR);
         aiNewOffset += SIZE_OF_ADDR;

         memcpy(&mTypeOfAddr, apcData + aiNewOffset, SIZE_OF_INT);
         mTypeOfAddr = static_cast<INGwTypeOfAddress>(ntohl(mTypeOfAddr));
         aiNewOffset += SIZE_OF_INT;

         memcpy(&mNatureOfAddr, apcData + aiNewOffset, SIZE_OF_INT);
         mNatureOfAddr = static_cast<INGwNatureOfAddress>(ntohl(mNatureOfAddr));
         aiNewOffset += SIZE_OF_INT;

         memcpy(mBGId, apcData + aiNewOffset, SIZE_OF_BG_ID);
         aiNewOffset += SIZE_OF_BG_ID;

         memcpy(&mBGType, apcData + aiNewOffset, SIZE_OF_INT);
         mBGType = ntohl(mBGType);
         aiNewOffset += SIZE_OF_INT;

         memcpy(mpcDisplayName, apcData + aiNewOffset, SIZE_OF_DISPLAY_NAME);
         mpcDisplayName [SIZE_OF_DISPLAY_NAME] = '\0';
         aiNewOffset += SIZE_OF_DISPLAY_NAME;

         mBitMask = 0x1F;

         return retVal;
      }

   public:

      INGwSpAddress & operator = (const INGwSpAddress &inAddress)
      {
         if(this == &inAddress)
         {
            return *this;
         }

         strcpy(mAddr, inAddress.mAddr);
         strcpy(mpcDisplayName, inAddress.mpcDisplayName);
         mTypeOfAddr = inAddress.mTypeOfAddr;
         mNatureOfAddr = inAddress.mNatureOfAddr;
         strcpy(mBGId, inAddress.mBGId);
         mBGType = inAddress.mBGType;
         mBitMask = inAddress.mBitMask;

         return *this;
      }

      INGwSpAddress(const INGwSpAddress &inAddress) :
         mTypeOfAddr(inAddress.mTypeOfAddr),
         mNatureOfAddr(inAddress.mNatureOfAddr),
         mBGType(inAddress.mBGType),
         mBitMask(inAddress.mBitMask)
      {
         strcpy(mAddr, inAddress.mAddr);
         strcpy(mpcDisplayName, inAddress.mpcDisplayName);
         strcpy(mBGId, inAddress.mBGId);
      }

// ACCESSORS.

      const char* getAddress(void) const
      {
         return mAddr;
      }

      const char* getDisplayName (void) const
      {
         return mpcDisplayName;
      }

      int getAddressType(void) const
      { 
         return mTypeOfAddr; 
      }

      int getAddressNature(void) const
      { 
         return mNatureOfAddr; 
      }

      const char* getBGId(void) const
      { 
         return mBGId; 
      }

      int getBGType(void) const
      { 
         return mBGType; 
      }

      int getBitMask() const
      {
         return mBitMask;
      }

// Mutators.

      void setAddress(const char* apcAddr)
      {
         mBitMask |= ADDRESS;
         strncpy(mAddr, apcAddr, SIZE_OF_ADDR);
      }

      void setDisplayName (const char* apcAddr)
      {
         mBitMask |= DISPLAY_NAME;
         strncpy(mpcDisplayName, apcAddr, SIZE_OF_DISPLAY_NAME);
      }

      void setAddressType(int aValue)
      {
         mBitMask |= TYPE_OF_ADDR;
         memcpy(&mTypeOfAddr, &aValue, SIZE_OF_INT);
      }

      void setAddressNature(int aValue)
      {
         mBitMask |= NATURE_OF_ADDR;
         memcpy(&mNatureOfAddr, &aValue, SIZE_OF_INT);
      }

      void setBGId(const char* apcBGId)
      { 
         mBitMask |= BG_ID; 
         memcpy(mBGId, apcBGId, SIZE_OF_BG_ID); 
      }

      void setBGType(int aValue) 
      { 
         mBitMask |= BG_TYPE; 
         memcpy(&mBGType, &aValue, SIZE_OF_INT); 
      }

      void setBitMask(int inMask)
      {
         mBitMask = mBitMask;
      }

   public:

      std::string toLog() const
      {
         std::ostringstream oStr;

         if(mBitMask == 0)
         {
            oStr << " -Address : NULL ";
            return oStr.str();
         }

         oStr << " -Address : "
              << ((mBitMask & ADDRESS) ? mAddr : "X");

         oStr << "-";
         if(mBitMask & DISPLAY_NAME)
         {
            oStr << mpcDisplayName;
         }
         else
         {
            oStr << "X";
         }

         oStr << "-";
         if(mBitMask & TYPE_OF_ADDR) 
         {
            oStr << mTypeOfAddr; 
         }
         else
         {
            oStr << "X";
         }

         oStr << "-";
         if(mBitMask & NATURE_OF_ADDR) 
         {
            oStr << mNatureOfAddr; 
         }
         else
         {
            oStr << "X";
         }

         oStr << "-"
              << ((mBitMask & BG_ID) ? mBGId : "X");

         oStr << "-";
         if(mBitMask & BG_TYPE) 
         {
            oStr << mBGType; 
         }
         else
         {
            oStr << "X";
         }

         return oStr.str();
      }

   // Formatted output to Call Trace is provided by the following function
      std::string toCTrace(int aNumTabs, int aUserMask) const
      {
         std::ostringstream oStr;
         std::string lTabSpace = "";
         for (int i = 0; i < aNumTabs; i++)
            lTabSpace += "\t";

         std::string lFieldPrefix = " ";
         std::string lPrefix = "";
//          std::string lPrefix = "\n" + lTabSpace;

         oStr << lPrefix;
         
         if(mBitMask == 0)
         {
            oStr << lFieldPrefix << "Address Not Present";
            return oStr.str();
         }

         if(aUserMask & ADDRESS != 0) {
            if(mBitMask & ADDRESS == 0) {
               oStr << lFieldPrefix << "Digits - []";
            }
            else {
               oStr << lFieldPrefix << "Digits - [" << mAddr << "]";
            }
         }

         if(aUserMask & NATURE_OF_ADDR != 0) {
            if(mBitMask & NATURE_OF_ADDR == 0) {
               oStr << lFieldPrefix << "Nature Of Number - []";
            }
            else {
               if ( mNatureOfAddr == UNKNOWN_NATURE_ADDR )
                  oStr << lFieldPrefix << "Nature Of Number - [Unknown]";
               else if ( mNatureOfAddr == LOCAL )
                  oStr << lFieldPrefix << "Nature Of Number - [Local]";
               else if ( mNatureOfAddr == NATIONAL )
                  oStr << lFieldPrefix << "Nature Of Number - [National]";
               else if ( mNatureOfAddr == INTERNATIONAL )
                  oStr << lFieldPrefix << "Nature Of Number - [IntNational]";
            }
         }
         if(aUserMask & DISPLAY_NAME != 0) {
				if(mBitMask & DISPLAY_NAME == 0) {
					oStr << lFieldPrefix << "Display Name - []";
				}
				else {
					oStr << lFieldPrefix << "Display Name - [" << mpcDisplayName << "]";
				}
			}


         if(aUserMask & BG_ID != 0) {
            if(mBitMask & BG_ID == 0) {
               oStr << lFieldPrefix << "BG Id - []";
            }
            else {
               oStr << lFieldPrefix << "BG Id - [" << mBGId << "]";
            }
         }

         if(aUserMask & BG_TYPE != 0) {
            if(mBitMask & BG_TYPE == 0) {
               oStr << lFieldPrefix << "BG Type - []";
            }
            else {
               oStr << lFieldPrefix << "BG Type - [" << mBGType << "]";
            }
         }

         return oStr.str();
      }

};

#endif //INGW_SP_ADDRESS_H_

// EOF INGwSpAddress.h
