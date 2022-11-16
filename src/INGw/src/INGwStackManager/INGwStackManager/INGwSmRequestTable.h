/************************************************************************
     Name:     INAP Stack Manager RequestTable Messages - defines
 
     Type:     C include file
 
     Desc:     Defines required for INGwSmRequestTable class

     File:     INGwSmRequestTable.h

     Sid:      INGwSmRequestTable.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMREQUSTTABLE_H__
#define __BP_AINSMREQUSTTABLE_H__


//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"


class INGwSmRequest;

/*
 * etc.
 */

class INGwSmRequestTable 
{
  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmRequestTable();

  //default destructor 
  ~INGwSmRequestTable();

  //initialize the table
  int initialize ();

  //lookup a SmRequest
  INGwSmRequest* getRequest(int aiTransId);

  //add a SmRequest
  int addRequest(int aiTransId, INGwSmRequest *apSmRequest);

  //remove a SmRequest. apSmRequest would contain the removed
  // request on return.
  int removeRequest(int aiTransId, INGwSmRequest *(&apSmRequest));

  //dump the request table
  int dump ();

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

  typedef std::map <int, INGwSmRequest*> INGwSmRequestMap;

  INGwSmRequestMap meReqTable;

  /* 
   * Private Data Members
   */
  private:

};

#endif /* __BP_AINSMREQUSTTABLE_H__ */
