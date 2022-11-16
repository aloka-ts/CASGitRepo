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
//     File:     INGwIfrUtlSingleton.h
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef __BP_SINGLETON_H__
#define __BP_SINGLETON_H__

namespace RSI_NSP_CCM
{

template <class T>
class INGwIfrUtlSingleton
{
   private:

      static T * _instance;

   protected:

      INGwIfrUtlSingleton()
      {
         if(_instance != NULL)
         {
            throw ("Singleton property violated.");
         }

         //Usage of static_cast is not advisable for up cast. Since its the 
         //constructor where upcast is needed and derv obj not yet consturcted 
         //there is no other way to carry.
         //As static_cast is unchecked we assume usage is right.
         _instance = static_cast<T *>(this);
      }

      virtual ~INGwIfrUtlSingleton()
      {
         _instance = NULL;
      }

      INGwIfrUtlSingleton(const INGwIfrUtlSingleton &){}
      INGwIfrUtlSingleton & operator = (const INGwIfrUtlSingleton &){ return *this;}

   public:

      static T & getInstance()
      {
         return *_instance;
      }

      static bool isInstantiated()
      {
         return (_instance != NULL);
      }
};

template <class T> T * INGwIfrUtlSingleton<T>::_instance = NULL;

};
#endif
