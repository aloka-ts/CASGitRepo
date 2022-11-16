//********************************************************************
//
//     File:  INGwAppProtoParamDecoder.h 
//
//     Desc:  Binary Decoder Classes for App Proto Parameters
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 04/03/13       Initial Creation
//********************************************************************

#ifndef _INGW_APP_PROTO_PARAM_DEC_
#define _INGW_APP_PROTO_PARAM_DEC_

#define EXCEPTION_UNKNOWN_TAG   20
#define EXCEPTION_TAG_NOT_FOUND 21
#define SIZE_OF_INT   4
#define SIZE_OF_SHORT 2
#define SIZE_OF_LONG  4

#include "INGwCommonTypes/INCCommons.h"
#include "INGwTcapProvider/INGwAppProtoInclude.h"

typedef unsigned char byte;


class NonAsnParam
{
  
  protected:
  byte tag[3];

  public:
  
  unsigned char* getTag()
  {
    return tag;
  }

  virtual void setParamTag() = 0;
  virtual void decode(unsigned char* pcParamBuff) = 0; 
};

/*
|<--origMarketId-->|<--origSwitchNo-->|<--IdNumber-->|<--segmentCntr-->|
       2 B                 1 B              3 B             1 B  
*/
class NonASNBillingId : public NonAsnParam
{
  private:
  short origMarketId; 
  byte  origSwitchNo;
  int   IdNumber;
  byte  segmentCntr;

  public:
  
  int getBillingNo()
  {
    return IdNumber;
  }
  
  void setBillingNo(int & piBillingNo)
  {
    IdNumber = piBillingNo;
  }

  void setParamTag()
  {
    tag[0] = 0x81;
    tag[1] = 0x00;
    tag[2] = 0x00;
  }

  NonASNBillingId()
  {
    setParamTag();
  }

  void operator=(const NonASNBillingId& obj)
  {
    origMarketId = obj.origMarketId;
    origSwitchNo = obj.origSwitchNo;
    IdNumber     = obj.IdNumber;
    segmentCntr  = obj.segmentCntr;
  }

  void decode(unsigned char* pcParamBuff);
  void logBillingId(int);
   
};


struct ORREQ
{
#if 0
  BillingId m_billingId;
  DialledDigits m_DialledDigits;
  ElecSerialNo  m_elecSerialNo;
  OrigMSCID     m_origMSCId;
  MSId          m_msId;
  OriginationTriggers    m_origTriggers;
  TransactionCapability  m_tcap;
#endif
};

struct ANLYZD
{
};

struct OANSWER
{
};

struct ODISCONNECT
{
};

struct CCDIR
{
};

struct SM_ANALYZED
{
};

unsigned char* splitTag(unsigned char* pcTag, int piTag);

template <class AppProtoParam>
AppProtoParam getNonAsnAppProtoParam(PtrStr& pParamBuff, 
                             AppProtoParam& pAppProtoParam) 
{
  /*Index 2 MSB, Index 0 LSB*/
  byte *tag = pAppProtoParam.getTag();
  int liParamLen = 0; 

  while ( liParamLen < pParamBuff.len )
  {
    if(   pParamBuff.string[liParamLen] >=  FIRST_OCTET_1B_MIN 
       && pParamBuff.string[liParamLen] <=  FIRST_OCTET_1B_MAX)
    {
      if (tag[0] != pParamBuff.string[liParamLen]) 
      {
        liParamLen += pParamBuff.string[liParamLen + 1] + 2;
      }
      else
      {
        pAppProtoParam.decode(&(pParamBuff.string[liParamLen + 2]));
        return pAppProtoParam;
      }
    }
    if( pParamBuff.string[liParamLen] == FIRST_OCTET_2B)
    {
       liParamLen++; 
       if( pParamBuff.string[liParamLen] <=  SEC_OCTET_2B_MAX
           && pParamBuff.string[liParamLen] >=  SEC_OCTET_2B_MIN)
       {
         if (tag[1] != pParamBuff.string[liParamLen]) 
         {
           liParamLen += pParamBuff.string[liParamLen + 1] + 2;
         }
         else
         {
           pAppProtoParam.decode(&(pParamBuff.string[liParamLen + 2]));
           return pAppProtoParam;
         }           
       }
    }
    else
    {
      throw EXCEPTION_UNKNOWN_TAG;
    }

    if(pParamBuff.string[liParamLen] <= SEC_OCTET_3B_MAX &&
       pParamBuff.string[liParamLen] >= SEC_OCTET_3B_MIN)
    {
       liParamLen++;

       if( pParamBuff.string[liParamLen] <= THIRD_OCTET_3B_MAX 
           && pParamBuff.string[liParamLen] >=  THIRD_OCTET_3B_MIN )
       {
         if (tag[2] != pParamBuff.string[liParamLen]) 
         {
           liParamLen += pParamBuff.string[liParamLen + 1] + 2;
         }
         else
         {
           pAppProtoParam.decode(&(pParamBuff.string[liParamLen + 2]));
           return pAppProtoParam;
         }           
       }
    }
    else
    {
      throw EXCEPTION_UNKNOWN_TAG;
    }
  }

  throw  EXCEPTION_TAG_NOT_FOUND; 
}

//recommended only if a few parameters are required to decoded
template <class AppProtoParam>
AppProtoParam getNonAsnWinParamNew(PtrStr& pParamBuff, 
                             AppProtoParam& pAppProtoParam) 
{
  /*Index 2 MSB, Index 0 LSB*/
  byte *tag = pAppProtoParam.getTag();

  int liParamLen = 0; 
  unsigned char * lpcParamBuff = pParamBuff.string;
  lpcParamBuff += 2;

  int liTotalLen = pParamBuff.len - 2;

  while ( liParamLen < liTotalLen )
  {
    if(   lpcParamBuff[liParamLen] >=  FIRST_OCTET_1B_MIN 
       && lpcParamBuff[liParamLen] <=  FIRST_OCTET_1B_MAX)
    {
      if (tag[0] != lpcParamBuff[liParamLen]) 
      {
        liParamLen += lpcParamBuff[liParamLen + 1] + 2;
        continue;
      }
      else
      {
        pAppProtoParam.decode(&(lpcParamBuff[liParamLen + 2]));
        return pAppProtoParam;
      }
    }
    if( lpcParamBuff[liParamLen] == FIRST_OCTET_2B)
    {
       liParamLen++; 
       if( lpcParamBuff[liParamLen] <=  SEC_OCTET_2B_MAX
           && lpcParamBuff[liParamLen] >=  SEC_OCTET_2B_MIN)
       {
         if (tag[1] != lpcParamBuff[liParamLen]) 
         {
           liParamLen += lpcParamBuff[liParamLen + 1] + 2;
           continue;
         }
         else
         {
           pAppProtoParam.decode(&(lpcParamBuff[liParamLen + 2]));
           return pAppProtoParam;
         }           
       }
    }
    else
    {
      throw EXCEPTION_UNKNOWN_TAG;
    }

    if(lpcParamBuff[liParamLen] <= SEC_OCTET_3B_MAX &&
       lpcParamBuff[liParamLen] >= SEC_OCTET_3B_MIN)
    {
       liParamLen++;

       if( lpcParamBuff[liParamLen] <= THIRD_OCTET_3B_MAX 
           && lpcParamBuff[liParamLen] >=  THIRD_OCTET_3B_MIN )
       {
         if (tag[2] != lpcParamBuff[liParamLen]) 
         {
           liParamLen += lpcParamBuff[liParamLen + 1] + 2;
           continue;
         }
         else
         {
           pAppProtoParam.decode(&(lpcParamBuff[liParamLen + 2]));
           return pAppProtoParam;
         }           
       }
    }
    else
    {
      throw EXCEPTION_UNKNOWN_TAG;
    }
  }

  throw  EXCEPTION_TAG_NOT_FOUND; 
}

#endif /*_INGW_APP_PROTO_PARAM_DEC_*/
