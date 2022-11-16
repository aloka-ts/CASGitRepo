//============================================================================
// Name        : util.h
// Author      : sanjay kumar
// Version     :
// Copyright   : 
// Description : various utility functions 
//============================================================================

#ifndef __AG_UTIL__
#define __AG_UTIL__

#include <vector>

using namespace std;

namespace AGUtil {

  enum ServerNameType{
    eHostName,
    eIpV4Address
  };

class CUtil{
  public:
    static ServerNameType isHostNameOrIp( string aServer );
    static vector <string> tokenize( string aStr, string aToken );
    static bool isInteger(string aStr);
    static string getCurrentTime();
};

}
#endif
