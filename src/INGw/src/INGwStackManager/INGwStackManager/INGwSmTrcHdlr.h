/************************************************************************
     Name:     INAP Stack Manager Trace Handler - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Trace Handler

     File:     INGwSmTrcHdlr.h

     Sid:      INGwSmTrcHdlr.h 0  -  03/27/03 

     Prg:      bd

************************************************************************/

#ifndef __BP_AINSMTRCHDLR_H__
#define __BP_AINSMTRCHDLR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include <libgen.h>
#include <time.h>
#include <sys/types.h>
#include <string>


/*
 * This class defines the object to be created for trace handling
 * of different layers.
 *
 * NOTE: although not implemented as a singleton, there would only
 * be a single instance per-Adaptor (created by Adaptor)
 * of this class.
 */

class INGwSmTrcHdlr
{

  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmTrcHdlr();

  //default destructor
  ~INGwSmTrcHdlr();

  //initialize the processr
  int initialize(int aMaxTrcFileSz, char *apTrcFilePath);

  //process the trace info 
  int handleTrace(INGwSmTrcInfo *aTrcInfo);


  /*
   * Protected interface
   */
  protected:

  // log the trace information in a file
  int logTrace(INGwSmTrcInfo *aTrcInfo);

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
  
  char   *mpTrcFilePath;

  /*
   * Private Data Members
   */
  private:

};

#endif /* __BP_AINSMTRCHDLR_H__ */
