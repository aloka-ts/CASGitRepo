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
//     File:     INGwIfrUtlAlgorithm.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_UTL_ALGORITHM_H_
#define INGW_UTL_ALGORITHM_H_

template <typename BaseData, typename OutputIterator>
OutputIterator INGwAlgTokenizer(const BaseData &data, const BaseData &pattern,
                              OutputIterator result)
{
   typename BaseData::const_iterator dataStart = data.begin();
   typename BaseData::const_iterator dataEnd   = data.end();
   typename BaseData::const_iterator patternStart = pattern.begin();
   typename BaseData::const_iterator patternEnd   = pattern.end();

   typename BaseData::const_iterator dataCurr = dataStart;

   while(dataCurr != dataEnd)
   {
      typename BaseData::const_iterator patternCurr = patternStart;

      while(patternCurr != patternEnd)
      {
         if(*dataCurr == *patternCurr)
         {
            *result++ = BaseData(dataStart, dataCurr);
            dataStart = dataCurr;
            dataStart++;
            break;
         }

         ++patternCurr;
      }

      ++dataCurr;
   }

   *result++ = BaseData(dataStart, dataCurr);

   return result;
}

#endif //INGW_UTL_ALGORITHM_H_
