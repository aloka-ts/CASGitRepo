/************************************************************************
     Name:     INAP Stack Manager Statistics Map - defines
 
     Type:     C include file
 
     Desc:     Defines required for INGwSmStsMap class

     File:     INGwSmStsMap.h

     Sid:      INGwSmStsMap.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMSTSMAP_H__
#define __BP_AINSMSTSMAP_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"


/*
 * etc.
 */

class INGwSmStsMap 
{
  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmStsMap();

  //default destructor 
  ~INGwSmStsMap();

  //initialize the table
  int initialize ();

  //insert entry into map
  int insert (long alKey, INGwSmStsOid *apValue);

  //get an entry from the Map - resets the cached vector
  int get (long alKey, int aiPosition, INGwSmStsOid *(&apRetVal));

  //get next entry from the cached -list
  int getNext (INGwSmStsOid *(&apRetVal));

  //get by OID Value
  int getByValue (long alKey, std::string &apOidValue, INGwSmStsOid *(&apRetVal));

  //remove entry from Map
  int remove (long alKey, INGwSmStsOid *(&apValue));

  //remove by value
  int removeByValue (long alKey, std::string &apOidValue, INGwSmStsOid *(&apRetVal));

  long getStsHashKey (int aiLayer, int aiOper, int aiLevel);

  //get the OID List from the hash key
  INGwSmStsOidList* getList (long alKey, int &liRetVal);

  /* 
   * Protected interface
   */
  protected:

  /*
   * Private interface
   */
  private:

  /*
   * Public Data Members
   */
  public:

  /* 
   * Protected Data Members
   */
  protected:

  typedef std::vector <INGwSmStsOid*> INGwSmOidVector;

  typedef std::map <long, INGwSmOidVector*> INGwSmOidMap;

  INGwSmOidMap meOidMap;

  INGwSmOidVector *meCachedVector;
  int miCachedPosition;

  /* 
   * Private Data Members
   */
  private:

};

#endif /* __BP_AINSMSTSMAP_H__ */
