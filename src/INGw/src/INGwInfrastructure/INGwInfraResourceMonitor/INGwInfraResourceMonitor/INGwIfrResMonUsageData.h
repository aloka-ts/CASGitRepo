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
//     File:     INGwLdDstBucket.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************`
#ifndef _INGW_IFR_RES_MON_UAGE_DATA_H_
#define _INGW_IFR_RES_MON_UAGE_DATA_H_

class BpResUsageData
{
   public :

      enum BpResUsageCPUUtilLevel
      {
         ABOVE_NORMAL_BAND     = 0x001,
         BELOW_NORMAL_BAND     = 0x002,
         WITHIN_NORMAL_BAND    = 0x004,
         UNKNOWN_CPUUTIL_LEVEL = 0x008
      };

   private:

      BpResUsageCPUUtilLevel mCPUUtilLevel;
      bool                   mProjLevelChange;
      double                 mCurrentCPUUtil;
      double                 mMaxNormalCPUUtil;

   public:

      BpResUsageData(void) 
      { 
          mCPUUtilLevel = UNKNOWN_CPUUTIL_LEVEL;
          mProjLevelChange = false;
          mCurrentCPUUtil = 0;
          mMaxNormalCPUUtil = 0;
      }

      BpResUsageCPUUtilLevel getCPUUtilLevel(void) const
      { 
         return mCPUUtilLevel; 
      }

      bool getProjLevelChange(void) const
      { 
         return mProjLevelChange; 
      }

      double getCurrentCPUUtil(void) const
      { 
         return mCurrentCPUUtil; 
      }

      double getMaxNormalCPUUtil() const
      {
         return mMaxNormalCPUUtil;
      }

      void setCPUUtilLevel(BpResUsageCPUUtilLevel aValue)
      {
         mCPUUtilLevel = aValue;
      }

      void setProjLevelChange(bool aValue)
      {
         mProjLevelChange = aValue;
      }

      void setCurrentCPUUtil(double aValue) 
      { 
         mCurrentCPUUtil = aValue;
      }

      void setMaxNormalCPUUtil(double aValue)
      {
         mMaxNormalCPUUtil = aValue;
      }

};

#endif

// EOF BpResUsageData.h
