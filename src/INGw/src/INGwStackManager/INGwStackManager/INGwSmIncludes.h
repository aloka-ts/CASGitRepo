/************************************************************************
     Name:     INAP Stack Manager Includes - defines
 
     Type:     C include file
 
     Desc:     Common include files needed for stack

     File:     INGwSmIncludes.h

     Sid:      INGwSmIncludes.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMINCLUDES_H__
#define __BP_AINSMINCLUDES_H__

//#include "CCMUtil/BpThread.h"
#include "INGwInfraUtil/INGwIfrUtlThread.h"
//#include "CCMUtil/BpConfigurable.h"
#include "INGwInfraUtil/INGwIfrUtlConfigurable.h"
//#include "ccm/BpCCM.h"
#include "INGwIwf/INGwIwfBaseProvider.h"
//#include "INGwIwf/INGwIwfProvider.h"


/* header include files (.h) */
#include "semaphore.h"

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"        /* environment options */
  #include "envdep.h"        /* environment dependent */
  #include "envind.h"        /* environment independent */
  
  #include "gen.h"           /* general layer */
  #include "cm_ss7.h"        /* ss7 layer */
  #include "cm_tpt.h"        /* common transport */
  #include "cm_ftha.h"
  #include "cm_pftha.h"
  #include "cmzndn.h"
  #include "cmzndnlb.h"
  #include "cmzvdv.h"
  #include "cmzvdvlb.h"
  /* Cisco stack */
  #include "cm_dns.h"        /* common file */
  /* Cisco stack */
  #include "ssi.h"           /* system services */
  #include "sht.h"
  #include "snt.h"           /* SNT */
  #include "sct.h"           /* SCT */
  #include "hit.h"           /* HIT */
  #include "sht.h"           /* SHT interface */
  #include "lsp.h"           /* layer management */
  #include "lit.h"           /* layer management */
  #include "lsb.h"           /* layer management */
  #include "lhi.h"           /* layer management */
  #include "lsn.h"           /* layer management */
  #include "lsd.h"           /* layer management */
  #include "cm_err.h"        /* common error */
  #include "cm5.h"           /* common timers */
  
  
  /* header/extern include files (.x) */
  
  #include "gen.x"           /* general layer */
  #include "mt_ss.h"
  #include "ssi.x"           /* system services */
  #include "cm_lib.x"        /* common */
  #include "cm_ss7.x"        /* general SS7 layer */
  #include "cm_tpt.x"        /* common transport */
  #include "cm_ftha.x"
  #include "cm_pftha.x"
  #include "cmzndn.x"
  #include "cmzvdv.x"
  /* Cisco stack */
  #include "cm_dns.x"        /* common file */
  /* Cisco stack */
  #include "sht.x"           /* SHT interface */
  #include "snt.x"           /* SNT */
  #include "sct.x"           /* SCT */
  #include "hit.x"           /* HIT */
  #include "lsp.x"           /* layer management */
  #include "lit.x"           /* layer management */
  #include "lsb.x"           /* layer management */
  #include "lhi.x"           /* layer management */
  #include "lsn.x"           /* layer management */
  #include "lsd.x"           /* layer management */
  
  /* header include files (.h) */
  
  #include "envopt.h"        /* environment options */
  #include "envdep.h"        /* environment dependent */
  #include "envind.h"        /* environment independent */
  #include "gen.h"           /* general layer */
  #include "ssi.h"           /* system services */
  #include "cm5.h"           /* Common timer */
  #include "cm_lib.x"        /* common library functions */
  #include "cm_ss7.h"
  #include "cm_hash.h"       /* common hash */
  #include "cm_err.h"        /* common error */
  #if 1  /* ie017.12: addition */
  /*#ifdef IE_FTHA*/
  /*#endif  IE_FTHA */
  #endif /* ie017.12: addition */
  /*#include "iet.h"            inap upper interface */
  #include "stu.h"           /* tcap upper interface */
  /*#include "lie.h"           layer management, INAP */
  #include "cm_asn.h"        /* common asn.1 defines */
  
  /* header/extern include files (.x) */
                       
  #include "gen.x"           /* general layer */
  #include "ssi.x"           /* system services */
  #include "cm5.x"           /* common timer */
  #include "cm_ss7.x"        /* Common */
  #include "cm_hash.x"       /* Common hash */
  #if 1  /* ie017.12: addition */
  
  #endif /* ie017.12: addition */
  /*#include "iet.x"            inap upper interface */
  #include "stu.x"           /* tcap upper interface */
  /*#include "lie.x"           Layer management, inap */
  #include "cm_asn.x"        /* common asn.1 defines */
  
  /* header include files (.h) */
  
  #include "envopt.h"        /* environment options */
  #include "envdep.h"        /* environment dependent */
  #include "envind.h"        /* environment independent */
  #include "gen.h"           /* general layer */
  #include "ssi.h"           /* system services */
  #include "cm5.h"
  #include "cm_ss7.h"
  #include "cm_hash.h"       /* common hash */
  #include "cm_err.h"        /* common error */
  #include "stu.h"           /* tcap services user */
  #include "spt.h"           /* sccp layer */ 
  #include "lst.h"           /* layer management, TCAP */
  
  /* header/extern include files (.x) */
                          
  #include "gen.x"           /* general layer */
  #include "ssi.x"           /* system services */
  #include "cm5.x"     
  #include "cm_ss7.x"        /* Common */
  #include "cm_hash.x"       /* Common hash */
  #include "stu.x"           /* Tcap layer */
  #include "lst.x"           /* Layer management, TCAP */
  #include "spt.x"           /* Sccp layer */
  
  #include "lsg.h"
  #include "lsh.h"
  #include "lmr.h"
  #include "lzn.h"
  #include "ldn.h"
  #include "lzv.h"
  #include "ldv.h"
  #include "lzp.h"
  #include "lzt.h"
  #include "lry.h"
  #include "lmr.x"
  #include "lsh.x"
  #include "lsg.x"
  #include "lzn.x"
  #include "ldn.x"
  #include "lzv.x"
  #include "ldv.x"
  #include "lzp.x"
  #include "lzt.x"
  #include "lry.x"
#ifndef __CCPU_CPLUSPLUS
}
#endif

#include <iostream>
#include <cstdlib>
#include <vector>
#include <map>



#define ConfigOpType   INGwIfrUtlConfigurable::ConfigOpType

#include "INGwStackManager/INGwSmCommon.h"
#include "INGwStackManager/INGwSmQueueMsg.h"

#include "INGwStackManager/INGwSmWrapper.h"
#include "INGwStackManager/INGwSmDistributor.h"

#ifdef ALM_TESTING
#include "INGwStackManager/INGwSmAlmHdlr.h"  // for UT : BD
#endif /* ALM_TESTING */

#endif /* __BP_AINSMINCLUDES_H__ */
