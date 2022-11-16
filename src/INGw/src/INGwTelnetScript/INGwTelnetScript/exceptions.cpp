//============================================================================
// Name        : exceptions.h
// Author      : sanjay kumar
// Version     :
// Copyright   : 
// Description : General Exceptions Raised By the System
//============================================================================

#include "exceptions.h"

namespace AG{
  std::ostream& operator<<( std::ostream &aCout,CException &aExRef){
    aCout<<aExRef.getDesc()<<endl;
    printstack( 2 );
    return aCout;
  }

  std::ostream& operator<<( CException &aExRef, std::ostream &aCout){
    aCout<<aExRef.getDesc()<<endl;
    printstack( 2 );
    return aCout;
  }
}
