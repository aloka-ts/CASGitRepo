//============================================================================
// Name        : errcodes.h
// Author      : sanjay kumar
// Version     :
// Copyright   : 
// Description : enums defining the error codes
//============================================================================

#ifndef __AG_ERR_CODES__
#define __AG_ERR_CODES__

namespace AG{

  enum RetStatus{
    eSuccess,
    eFailure
  };

  enum ServerNameType{
    eHostName,
    eIpAddress
  };
}

#endif
