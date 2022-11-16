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
//     File:     ResMonitorMain.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************

#include <iostream.h>
#include <Util/imOid.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraResourceMonitor/INGwIfrResMonResourceMonitor.h>

main()
{
  INGwIfrPrParamRepository::getInstance().setValue(ingwMAX_NORM_CPU_UTIL, "50");
  INGwIfrPrParamRepository::getInstance().setValue(ingwCPU_UTIL_DELTA, "50");
  INGwIfrPrParamRepository::getInstance().setValue(ingwGRAD_FOR_HIGH_CPU_UTIL, 
		  					"50");
  INGwIfrPrParamRepository::getInstance().setValue(ingwRESOURCE_USG_MONITORING_DUR, 							
																							"50");

  INGwIfrResMonResourceMonitor *resMon = new INGwIfrResMonResourceMonitor();
  resMon->initialize();

  while (1) {
    sleep(50);
  }

  resMon->shutdown();

  sleep(10);
}
