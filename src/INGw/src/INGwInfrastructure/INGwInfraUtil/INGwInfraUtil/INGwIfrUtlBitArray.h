//********************************************************************
//
//     File:    INGwIfrUtlBitArray.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 14/06/12       Initial Creation
//********************************************************************
#ifndef _INGW_UTL_BYTE_ARRAY_
#define _INGW_UTL_BYTE_ARRAY_
#define USE_LOOKUP_TABLE
using namespace std;
#include<vector>
#include<exception>
#include<stdlib.h>
class INGwIfrUtlBitArray{

  private:
  void 
#ifdef  USE_LOOKUP_TABLE
  createLookUpHashTable();
  static unsigned char lookUp[256];
#endif

  unsigned char *mByteArray;
  int mRange;
  int mOffset;
  int mSizeInBytes;
  unsigned char* getBitArray() const;
  int getRange()  const;
  int getOffset() const; 
  void setSize(int pSizeInBytes);

  public:
  
  int  getSize() const;

  INGwIfrUtlBitArray(int pOffSet, int pRange);
  INGwIfrUtlBitArray(const INGwIfrUtlBitArray&);
  INGwIfrUtlBitArray();

  ~INGwIfrUtlBitArray();
  int 
  getSetBitsCountFast(int pIndex); 
 
#ifdef  USE_LOOKUP_TABLE
  int
  getSetBitsCountLookUp(int pLen = -1);
#endif

  void 
  printBits(int pLen);

  void
  setBitState(int pIndex); 

  void
  resetBitState(int pIndex);

  void
  resetAllBits();

  int 
  getBitState(int pIndex);

  vector<int> 
  getAllSetBitIndex();

  void
  resetArray();

  void
  updateClone(INGwIfrUtlBitArray &pOrigArray);
};
#endif /*_INGW_UTL_BYTE_ARRAY_*/
