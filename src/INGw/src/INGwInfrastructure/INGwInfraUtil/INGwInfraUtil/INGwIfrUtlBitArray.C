//********************************************************************
//
//     File:    INGwIfrUtlBitArray.C
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 14/06/12       Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlBitArray.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwIfrUtlBitArray");
unsigned char  INGwIfrUtlBitArray::lookUp[256];
INGwIfrUtlBitArray::INGwIfrUtlBitArray(int pOffSet, int pRange){

    int lSize = ((pRange - pOffSet + 1)>>3) + ((((pRange+1) & 7) != 0)?1:0);
    lSize = (0==lSize)?1:lSize;

    
    logger.logINGwMsg(false,TRACE_FLAG,0,"INGwIfrUtlBitArray() size "
    "of BitArray<%d bytes>, pRange<%d> pOffSet<%d>", 
    lSize, pRange, pOffSet);

    mByteArray = new unsigned char[lSize];
    mSizeInBytes = lSize;
    memset(mByteArray, 0, lSize);
    mOffset = pOffSet;

    if(!mByteArray) {
     
    }

    mRange = pRange;
#ifdef  USE_LOOKUP_TABLE
    createLookUpHashTable();
#endif
}

INGwIfrUtlBitArray::INGwIfrUtlBitArray(const INGwIfrUtlBitArray& pObj) {
  mRange = pObj.getRange();
  mOffset = pObj.getOffset();
  mSizeInBytes = pObj.getSize();

  int lSize = mSizeInBytes;   
  logger.logINGwMsg(false,TRACE_FLAG,0, "INGwIfrUtlBitArray() copy constructor "
  "mRange<%d> mOffset<%d> lSize<%d>", mRange, mOffset, lSize);
  try{
    mByteArray = new unsigned char[lSize];
    memcpy(mByteArray,pObj.getBitArray(),lSize);
  }
  catch(bad_alloc& ba ){
    logger.logINGwMsg(false,TRACE_FLAG,0,
    "INGwIfrUtlBitArray HEAP MEM EXHAUSTED Exception:<%s>", ba.what());
    //raise an alarm
    exit(1);
  }
  
}

INGwIfrUtlBitArray::INGwIfrUtlBitArray() {
  
}

INGwIfrUtlBitArray::~INGwIfrUtlBitArray() {
  if(NULL != mByteArray) {
    delete [] mByteArray;
    mByteArray = NULL;
  }  
}

#ifdef  USE_LOOKUP_TABLE 
void 
INGwIfrUtlBitArray::createLookUpHashTable(){
  unsigned char x;
  memset(lookUp,0,sizeof(lookUp));
  for(unsigned char i=0;i<=254;i++) {
    x = 0x01; 
    while(x!=0 && x<=i) {
      if(x & i) {
        lookUp[i]++;
      }
      x<<=1;
    }
  }
  lookUp[255] = 8;
}
#endif

//count number of set bits
int 
INGwIfrUtlBitArray::getSetBitsCountFast(int pIndex) {
int retVal = 0;
int lNumOfInt = pIndex>>2;
  int x; 
  for(int i=0;i<lNumOfInt;i++){
    x = *(int*)(&(mByteArray[(i<<2)]));
    x = (x & 0x55555555) + ((x & 0xaaaaaaaa) >> 1);// 0..2 ones in 2 bits
    x = (x & 0x33333333) + ((x & 0xcccccccc) >> 2);// 0..4 ones in 4 bits
    x = (x & 0x0f0f0f0f) + ((x & 0xf0f0f0f0) >> 4);// 0..8 ones in 8 bits
    x =  x  + (x >> 8); // 0..16 ones in 8 bits
    x =  x  + (x >> 16);// 0..32 ones in 8 bits
    x &= 0xff;
    retVal += (x);
  }

  //for the bytes those are left
  unsigned char byte;
  for(int bytesLeft = pIndex - (pIndex & 3); bytesLeft <= pIndex; bytesLeft++) {
#ifdef  USE_LOOKUP_TABLE 
    retVal += lookUp[mByteArray[bytesLeft]];
#else
    byte = 0x01; 
    while(byte != 0 && byte <= mByteArray[bytesLeft]) {
      if(byte & mByteArray[bytesLeft]) {
        retVal++;  
      }
      byte <<=1;
    }

#endif
  }
  
  return retVal;
}

#ifdef  USE_LOOKUP_TABLE 
int
INGwIfrUtlBitArray::getSetBitsCountLookUp(int pLen) {
  int retVal = 0;
  if(-1 == pLen) {
    pLen = mSizeInBytes; 
  }
  for(int i=0;i<pLen;i++){
    retVal += lookUp[mByteArray[i]];
  }
  return retVal;
}
#endif

void 
INGwIfrUtlBitArray::printBits(int pLen) {
  unsigned char x;
  char lBuf[1024];
  int lBufLen = 0;
  
  lBufLen = sprintf(lBuf,"%s","\nINGwIfrUtlBitArray::printBits\n");
  int i;
  for (i = 0;i<pLen;i++) {
    x=0x80;
    while(x!=0) {
      if(lBufLen >= 1000) {
        printf("%d :==> %s",i-1,lBuf);fflush(stdout);
      }
      lBufLen += sprintf(lBuf,"%d",(((mByteArray[i] & x) !=0)?1:0));
      x>>=1;
    }
  }
  printf("%d :==> %s",i-1,lBuf);fflush(stdout);
}

void 
INGwIfrUtlBitArray::setBitState(int pIndex) {
  unsigned char x = 0x80;
  pIndex = pIndex - mOffset;
   
  (mByteArray[(pIndex>>3)]) |= (x>>(pIndex & 7));
  logger.logINGwMsg(false,TRACE_FLAG,0, 
  "setBitState pBitIndex <%d> internalIndex<%d>byteValue<%d>",
   pIndex, (x>>(pIndex & 7)), mByteArray[(pIndex>>3)]);
}

void 
INGwIfrUtlBitArray::resetBitState(int pIndex) {
  unsigned char x = 0xff;
  pIndex = pIndex - mOffset; 

  logger.logINGwMsg(false,TRACE_FLAG,0,"resetBitState dlgId <%d>",pIndex);
  mByteArray[pIndex>>3]  &= (x ^ (0x80>>(pIndex & 7)));

  logger.logINGwMsg(false,TRACE_FLAG,0,
  "resetBitState pBitIndex <%d> byteValue<%d>", pIndex, 
  mByteArray[(pIndex>>3)]);
}

int 
INGwIfrUtlBitArray::getBitState(int pIndex){
  unsigned char x = 0x80;
  pIndex = pIndex - mOffset; 
  return (((mByteArray[(pIndex>>3)]) & (x>>(pIndex & 7))) != 0)?1:0;
}
 
void
INGwIfrUtlBitArray::resetAllBits() {
  memset(mByteArray,0,getSize());
}

vector<int>
INGwIfrUtlBitArray::getAllSetBitIndex() {
  int sizeOfLongLong = sizeof(long long);
  register long long lLong = 0;
  register unsigned char x = 0x01;
  vector<int> lvector;
  //search in frames of 64 bits 
  int i;
  int  j = 0; int k = 7;

  int lRange = getSize();

  for(i = 0; /*i<20 &&*/(lRange - (i<<3)) >= sizeOfLongLong; i++) {
     i<<=3;
    if(0 == (lLong = (*(long long*)&(mByteArray[i])))) {
      continue;
    }

    int lBasebyteIndex = i;
    for(j=0;j<8;j++)
    {
      k = 8;
      if(!mByteArray[lBasebyteIndex]) {
        lBasebyteIndex++;continue;
      }

      while( (k--) && (mByteArray[lBasebyteIndex] >= x))
 
      {
        
        if(x & mByteArray[lBasebyteIndex]) {
          lvector.push_back((lBasebyteIndex<<3) + k + mOffset);
        }
        x<<=1;
      }
      x = 0x01;
      lBasebyteIndex++;
    }
  }

  j = lRange - (i<<3);x= 0x01;
  while(j < lRange) {
    for(j= lRange - i;j<lRange;j++)
    {
      k = 7;
      while((x & mByteArray[j]) 
             && (mByteArray[j] <= x)) 
      {
        if(x & mByteArray[j]) {
          lvector.push_back((j<<3) + k + mOffset);
        }
        --k;
        x<<=1;
      }
      x = 0x01;
    }    
  }
  return lvector;
}

int
INGwIfrUtlBitArray::getRange() const{
  return mRange;
}

int
INGwIfrUtlBitArray::getOffset() const{
  return mOffset;
}

unsigned char*
INGwIfrUtlBitArray::getBitArray() const{
  return mByteArray;
}

int
INGwIfrUtlBitArray::getSize() const{
  return mSizeInBytes;
}

void
INGwIfrUtlBitArray::setSize(int pSizeInBytes) {
  mSizeInBytes = pSizeInBytes;
}

void
INGwIfrUtlBitArray::updateClone(INGwIfrUtlBitArray &pOrigArray) 
{
  memcpy(mByteArray, pOrigArray.getBitArray(), pOrigArray.getSize());
}
