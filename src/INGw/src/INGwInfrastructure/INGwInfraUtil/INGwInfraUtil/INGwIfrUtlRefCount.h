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
//     File:     INGwIfrUtlRefCount.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef __BP_REF_COUNT_H__
#define __BP_REF_COUNT_H__

#include <pthread.h>
#include <stdlib.h>


#include <string>
#include <sstream>

/** This class represents a reference counting mechanism.
 */
class INGwIfrUtlRefCount
{
    public:

        /** Increments the reference count of the object by "1"
         *  This method is thread safe.
         */
        void getRef(void);

        void resetRef(void);

        /** Decrements the reference count of the object by "1"
         *  This method is thread safe.
         */
        virtual void releaseRef(void);

        virtual std::string 
        toLog(void) const;

    protected:

        /** The constructor
         */
        INGwIfrUtlRefCount(void);
        virtual void initObject(bool consFlag = false);

        /** The destructor
         */
        virtual ~INGwIfrUtlRefCount();

        /** Identifier for the ref counted object
         */
        std::string          mId;

        /** Mutex lock (for reference count)
         */
        pthread_mutex_t mRefMutex;

        /** Reference count of the call object
         */
        short           msRefCount;

    public:

       inline short getRefHoldersNum()
       {
          return msRefCount;
       }
    private:

        /** Assignment operator (Not implemented)
         */
        INGwIfrUtlRefCount&
        operator= (const INGwIfrUtlRefCount& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwIfrUtlRefCount(const INGwIfrUtlRefCount& arSelf);

};

class INGwIfrUtlRefCount_var {

    public :

        INGwIfrUtlRefCount_var(INGwIfrUtlRefCount* apPtr);
        ~INGwIfrUtlRefCount_var();

    protected:

        INGwIfrUtlRefCount* mpPtr;

    private:

        /** Assignment operator (Not implemented)
         */
        INGwIfrUtlRefCount_var& operator= (const INGwIfrUtlRefCount_var& arSelf);

        /** Copy constructor (Not implemented)
         */
        INGwIfrUtlRefCount_var(const INGwIfrUtlRefCount_var& arSelf);

};

#endif 

// EOF INGwIfrUtlRefCount.h
