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
//     File:     INGwIfrUtlSerializable.C
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraUtil");

#include "INGwInfraUtil/INGwIfrUtlSerializable.h"

using namespace std;

int 
INGwIfrUtlSerializable::serializeInt(int aiData, char* apcToStr)
{
    bcopy(&aiData, apcToStr, INGwIfrUtlSerializable_SIZE_OF_INT);
    return  INGwIfrUtlSerializable_SIZE_OF_INT;
}

int 
INGwIfrUtlSerializable::serializeShort(short asData, char* apcToStr)
{
    bcopy(&asData, apcToStr, INGwIfrUtlSerializable_SIZE_OF_SHORT);
    return INGwIfrUtlSerializable_SIZE_OF_SHORT;
}

int 
INGwIfrUtlSerializable::deserializeInt(const char* apcFromStr, int& ariData)
{
    bcopy(apcFromStr, &ariData, INGwIfrUtlSerializable_SIZE_OF_INT);
    return INGwIfrUtlSerializable_SIZE_OF_INT;
}

int 
INGwIfrUtlSerializable::deserializeShort(const char* apcFromStr, short& arsData)
{
    bcopy(apcFromStr, &arsData, INGwIfrUtlSerializable_SIZE_OF_SHORT);
    return INGwIfrUtlSerializable_SIZE_OF_SHORT;
}

int 
INGwIfrUtlSerializable::serializeVLString(string& arData, char* apcToStr)
{
    int size = arData.length();
    int offset = serializeInt(size, apcToStr);
    bcopy(arData.c_str(), apcToStr + offset, size);
    return (offset + size);
}

int  
INGwIfrUtlSerializable::deserializeVLString(const char* apcFromStr, string& arData)
{
    int size = 0;
    int offset = deserializeInt(apcFromStr, size);
    arData = string(apcFromStr + offset, size);
    return (offset + size);
}

int 
INGwIfrUtlSerializable::deserializeVLString(const char* apcFromStr, char** apcToStr)
{
     int size = 0;
/**************
     int offset = deserializeInt(apcFromStr, size);
     if(NULL == *apcToStr) {
          *apcToStr = BP_NEW_CHAR(size + 1);
          memset(*apcToStr, 0, size + 1);
     }
     bcopy(apcFromStr, *apcToStr, size);
**************/
     return size;
}

int
INGwIfrUtlSerializable::serializeFLString(string& arData, short asSize, char* apcToStr)
{
    bcopy(arData.c_str(), apcToStr, asSize);
    return asSize;
}

int 
INGwIfrUtlSerializable::deserializeFLString(const char* apcFromStr, short asSize, string& arData)
{
    arData = string(apcFromStr, asSize);
    return asSize;
}

INGwIfrUtlSerializable::INGwIfrUtlSerializable(void) 
{
   INGwIfrUtlSerializable::initObject(true);
}

void INGwIfrUtlSerializable::initObject(bool consFlag)
{
   serializationReadyFlag = COMP_SERI_NOT_READY;
   meSerType = FULL_SER_REQD; 
   operType = REP_CREATE;
}

INGwIfrUtlSerializable::~INGwIfrUtlSerializable() 
{ 
}

INGwIfrUtlSerializable::SerializationType 
INGwIfrUtlSerializable::getSerializationType(void) { return meSerType; }

void 
INGwIfrUtlSerializable::setSerializationType(INGwIfrUtlSerializable::SerializationType aeSerType) { 
    meSerType = aeSerType; 
}

INGwIfrUtlSerializable::SerializationType 
INGwIfrUtlSerializable::isSerializationReqd(void) { return meSerType; }

int INGwIfrUtlSerializable::serializeStruct(const void *indata, short structLen,
                                    char *serialBuf)
{
   memcpy(serialBuf, indata, structLen);
   return structLen;
}

int INGwIfrUtlSerializable::deserializeStruct(const char *serialBuf, void *indata,
                                      short structLen)
{
   memcpy(indata, serialBuf, structLen);
   return structLen;
}

// EOF INGwIfrUtlSerializable.C
