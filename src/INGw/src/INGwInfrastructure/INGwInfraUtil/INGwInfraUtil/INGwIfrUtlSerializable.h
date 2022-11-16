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
//     File:     INGwIfrUtlSerializable.h
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <strings.h>
#include <string>


#ifndef INCLUDE_INGwIfrUtlSerializable
#define INCLUDE_INGwIfrUtlSerializable

#define INGwIfrUtlSerializable_SIZE_OF_SHORT     2
#define INGwIfrUtlSerializable_SIZE_OF_INT       4


class INGwIfrUtlSerializable
{
   public:

      enum SerializationReadyState
      {
         COMP_SERI_NOT_READY,
         COMP_SERI_READY
      };

      typedef enum 
      {
         SER_NOT_REQD,
         INC_SER_REQD,
         FULL_SER_REQD
      } SerializationType;

      typedef enum 
      {
         REP_CREATE,
         REP_DELETE,
         REP_REPLICATE
      } ReplicationOperationType;


      virtual ~INGwIfrUtlSerializable();

      virtual bool serialize
          (unsigned char*       apcData,
           int                  aiOffset,
           int&                 aiNewOffset,
           int                  aiMaxSize, 
           bool                 abForceFullSerialization = false) = 0;
      virtual bool deserialize
          (const unsigned char* apcData,
           int                  aiOffset,
           int&                 aiNewOffset,
           int                  aiMaxSize) = 0;

      INGwIfrUtlSerializable::SerializationType getSerializationType(void);
      void setSerializationType(INGwIfrUtlSerializable::SerializationType aeSerType);
      virtual INGwIfrUtlSerializable::SerializationType isSerializationReqd(void);

      static int serializeInt(int aiData, char* apcToStr);
      static int serializeShort(short asData, char* apcToStr);
      static int deserializeInt(const char* apcFromStr, int& ariData);
      static int deserializeShort(const char* apcFromStr, short& arsData);
      static int serializeVLString(std::string& arData, char* apcToStr);
      static int deserializeVLString(const char* apcFromStr, std::string& arData);
      static int deserializeVLString(const char* apcFromStr, char** apcToStr);
      static int serializeFLString(std::string& arData, short asSize, 
                                   char* apcToStr);
      static int deserializeFLString(const char* apcFromStr, short asSize, 
                                     std::string& arData);

      static int serializeStruct(const void *indata, short structLen,
                                 char *serialBuf);
      static int deserializeStruct(const char *serialBuf, void *indata, 
                                   short structLen);
   protected:

      INGwIfrUtlSerializable(void);
      virtual void initObject(bool consFlag = false);

   public:

      SerializationReadyState serializationReadyFlag;
      SerializationType meSerType;
      ReplicationOperationType operType;

   private:

      /** Assignment operator (Not implemented)
      */
      INGwIfrUtlSerializable&
      operator= (const INGwIfrUtlSerializable& arSelf);

      /** Copy constructor (Not implemented)
      */
      INGwIfrUtlSerializable(const INGwIfrUtlSerializable& arSelf);
};

#endif 

// EOF INGwIfrUtlSerializable.h
