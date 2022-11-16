/*------------------------------------------------------------------------------
         File: INGwFtTkSingleton.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_SINGLETON_H__
#define __BTK_SINGLETON_H__

template <class T>
class INGwFtTkSingleton
{
   private:

      static T * _instance;

   protected:

      INGwFtTkSingleton()
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

      virtual ~INGwFtTkSingleton()
      {
         _instance = NULL;
      }

      INGwFtTkSingleton(const INGwFtTkSingleton &){}
      INGwFtTkSingleton & operator = (const INGwFtTkSingleton &){ return *this;}

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

template <class T> T * INGwFtTkSingleton<T>::_instance = NULL;

#endif
