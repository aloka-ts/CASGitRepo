//============================================================================
// Name        : util.h
// Author      : sanjay kumar
// Version     :
// Copyright   : 
// Description : various utility functions 
//============================================================================

#include <iostream>
#include "util.h"

namespace AGUtil {

  ServerNameType CUtil::isHostNameOrIp( string server ){
    ServerNameType retType = eHostName;
    const int numIp4Tokens = 4;
    string ipv4Token (".");

    vector<string> tokens  = tokenize( server, ipv4Token );
   
    
    if( tokens.size() == numIp4Tokens ){ //could be an ip address
      retType = eIpV4Address; //assuming ipv4 adddress as server type
      for( vector<string>::const_iterator itr = tokens.begin() ;
           itr != tokens.end() ; ++itr ) { 
        if( atoi( (*itr).c_str() ) <= 0 || atoi( (*itr).c_str() ) > 256 ){
          //double check for "0"  
          if( (*itr).compare("0") != 0 ){
            retType = eHostName; //Not a valid ipv4 address breaking & assuming it to be hostname/fqdn
            break;
          }
        }
      }
    } 
    return retType;
  }

  vector<string> CUtil::tokenize(string aStr, string aToken){
    vector<string> retVect;
    if( aStr.empty() ) return retVect;


    char *pStr = new char[aStr.length() + 1];
    char *pTok = new char[aStr.length() + 1];
    if( pStr ){
      strcpy ( pStr,aStr.c_str() );
      pTok = strtok( pStr, aToken.c_str() );
      while( pTok ) {
        retVect.push_back( string(pTok) );
        pTok = strtok( NULL, aToken.c_str() );
      }
    }

    delete[] pStr;
    delete[] pTok;
    return retVect;
  }

  bool CUtil::isInteger( string aStr ){
//cout<<" isInteger "<<aStr<<endl;
    bool retVal = true;
    if ( aStr.compare("0") == 0 ) {
      retVal = true;
    }
    else if( atoi( aStr.c_str() ) == 0 ) {  //conversion not possible
//cout<<" setting false"<<endl;
      retVal = false;
    }
    return retVal;
  }

  string CUtil::getCurrentTime(){
    struct tm* now ;
    time_t currTime;
    time(&currTime);
    now = localtime(&currTime);
    char timeBuf[32];

    sprintf(timeBuf,"%d-%d-%d %d:%d:%d ", now->tm_mday, now->tm_mon, now->tm_year, now->tm_hour, now->tm_min, now->tm_sec);
    return timeBuf;
  }
}

/**   UT Test Cases 
using namespace AGUtil;
main(){
  string str = "192.168.0.1";
  string token = ".";

  vector<string> tokens = AGUtil::CUtil::tokenize(str,token);
  cout<<"Total tokens returned = "<<tokens.size()<<endl;
  for( vector<string>::const_iterator itr = tokens.begin(); itr != tokens.end(); ++itr) {
    cout<<*itr<<endl;
  }

  switch( AGUtil::CUtil::isHostNameOrIp( str )  ){
    case eHostName:
      cout<<str<<" is a hostname/fqdn "<<endl;
      break;
    case eIpV4Address:
      cout<<str<<" is a ipv4 ip address"<<endl;
      break;
  }
}
*/
