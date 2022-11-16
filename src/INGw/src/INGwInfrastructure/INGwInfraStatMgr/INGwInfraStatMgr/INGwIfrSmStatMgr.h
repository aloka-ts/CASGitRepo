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
//     File:     INGwIfrMgrAgentClbkImpl.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev arya    07/12/07     Initial Creation
//********************************************************************
#ifndef INGW_IFR_STAT_MGR_H_
#define INGW_IFR_STAT_MGR_H_

#include <string>
#include <map>
#include <fstream.h>
#include <strstream>

#include <Util/imOid.h>
#include <INGwInfraStatMgr/INGwIfrSmCommon.h>
#include <INGwInfraStatMgr/INGwIfrSmStatParam.h>
#include <INGwInfraStatMgr/INGwIfrSmDeferredThread.h>

//////////////////////////// STATISTICS PARAMETERS /////////////////////////////
#define STATPARAM_SIP_NUM_SUCC_CALLS            ccmSIP_NUM_SUCCESS_CALLS
#define STATPARAM_SIP_NUM_CANCELLED_CALLS       ccmSIP_NUM_CANCELLED_CALLS
#define STATPARAM_SIP_NUM_FAILED_CALLS          ccmSIP_NUM_FAILED_CALLS
#define STATPARAM_SIP_NUM_SESSIONEXPIRED_CALLS  ccmSIP_NUM_SESSIONEXPIRED_CALLS
#define STATPARAM_SIP_NUM_NEW_INVS_RECD         ccmSIP_NUM_NEW_INVS_RECVD
#define STATPARAM_SIP_NUM_STACK_TIMEOUTS        ccmSIP_NUM_STACK_TIMEOUTS
#define STATPARAM_SIP_NUM_RETRANSMISSIONS       ccmSIP_NUM_RETRANSMISSIONS
#define STATPARAM_SIP_NUM_ACTIVE_IVR_CONNS      ccmSIP_NUM_ACTIVE_IVR_CONNS
#define STATPARAM_SIP_NUM_IVR_CONNS             ccmSIP_NUM_IVR_CONNS
#define STATPARAM_SIP_NUM_IVR_REQRESP_TIMEOUTS  ccmSIP_NUM_IVR_REQRESP_TIMEOUTS
#define STATPARAM_SIP_NUM_IVR_CONN_FAILURES     ccmSIP_NUM_IVR_CONN_FAILURES
#define STATPARAM_SIP_NUM_MSGS_RECD             ccmSIP_NUM_MSGS_RECVD
#define STATPARAM_SIP_NUM_MSGS_SENT             ccmSIP_NUM_MSGS_SENT
#define STATPARAM_SIP_CALLSETUPTIME             ccmSIP_CALL_SETUP_TIME
#define STATPARAM_SIP_NUM_INVITE_IN_PROGRESS    ccmSIP_NUM_INVITE_IN_PROGRESS

#define STATPARAM_MGCP_NUM_TRANS_TIMEOUTS       ccmMGCP_NUM_TRANS_TIMEOUTS
#define STATPARAM_MGCP_NUM_IVR_CONNS            ccmMGCP_NUM_IVR_CONNS
#define STATPARAM_MGCP_NUM_ACTIVE_IVR_CONNS     ccmMGCP_NUM_ACTIVE_CONN 
#define STATPARAM_MGCP_NUM_REQRESP_TIMEOUTS     ccmMGCP_NUM_REQRESP_TIMEOUTS
#define STATPARAM_MGCP_NUM_CONN_FAILURES        ccmMGCP_NUM_CONN_FAILURES

#define STATPARAM_CCM_NUM_CALLS                 ccmCCM_NUM_TOTAL_CALLS
#define STATPARAM_CCM_NUM_ACTIVE_CALLS          ccmCCM_NUM_ACTIVE_CALLS
#define STATPARAM_CCM_NUM_CALLS_GAPPED          ccmNUM_CALLS_GAPPED
#define STATPARAM_NUM_CDRS_GENERATED            ccmNUM_CDRS_GENERATED

//////////////////////////// STATISTICS PARAMETERS /////////////////////////////

class INGwIfrSmXmlParse;
typedef std::map<std::string, int> ParamIndexMap;
typedef std::map<std::string, int> EmsParamMap;

class INGwIfrSmStatMgr
{
  public:
    // This method returns an instance of the statistics manager.  This
    // class is singleton so an object cannot be explicitly created.
    static INGwIfrSmStatMgr& instance();

    // This method starts the deferred thread, and also loads statistics
    // from the specified xml file.  Returns a false status if the
    // initialization failed for any reason.
    bool initialize(std::string aConfigFileName);

    // Reads a specified XML file containing all the parameter
    // configurations and creates the relevant parameter structures.
    // Returns false on failure.  This reading is hierarchical, in that
    // the StatMgr in turn calls loadStatistics on the SINGwIfrSmStatParam
    // objects.
    bool loadStatistics(std::string aConfigFileName);

    // Reads only one parameter from an xml template file.  User modules
    // can make use of this instead of writing complex code to create
    // a parameter and fill them with correct attributes and thresholds.
    INGwIfrSmStatParam* loadStatParamFromXmlTemplate(std::string aXmlTemplateFileName);

    // This method adds a statistics parameter to the list of
    // parameters.  Upon successful addition, the index of the added
    // parameter is returned.  Upon failure, -1 is returned.
    int addStatParam(INGwIfrSmStatParam* aParam);

    // These methods retrieve a parameter based on the index (returned
    // from the addStatParam) or based on the OID.  EMS parameter
    // indices are not available to the respective modules on the first
    // attempt to retrieve them, as those parameters are loaded from
    // configuration file, and not by calling addStatParam().  In this
    // case, the parameter can be retrieved based on OID, and the index
    // can be retrieved from the parameter itself, and stored for all
    // subsequent uses.
    int getParamIndex(std::string aOidString, bool abAddParamIfAbsent = true);

    // Accessor methods for the current statistics level of CCM.
    int getStatLevel();
    void setStatLevel(int aLevel);

    int getCount(int aIndex)
    {
      int cur = -1, avg = -1, max = -1, min = -1;
      mStatParamList[aIndex].getValue(cur,avg,max,min);
      return cur;
    }

    int startDeferredProcessing   (std::string& aOutput);
    int startDisplayProcessing    (std::string& aOutput);
    int startStatsDisplayProcessing    (std::string& lfile);
    int startDisplayProcessing    (std::string& aOid, std::string& aOutput);

    ThresholdIndication setValue  (int aIndex, int aValue);
    ThresholdIndication increment (int aIndex, int& aCurValue, int aValue = 1);
    ThresholdIndication decrement (int aIndex, int& aCurValue, int aValue = 1);

    void INGwIfrSmStatMgr::dump(std::string& aOutput);
    int getDeferredDuration();

    char* getOidName(const char *oid);
    void processTelnetCommand
      (const std::string& arInputStr, char** apcOutput, int& size);

    void getValue
      (int aIndex,
       int& aCurValue,
       int& aAvgValue,
       int& aMinValue,
       int& aMaxValue);
    void getCurValue(int aIndex, int& aValue);
    void getAvgValue(int aIndex, int& aValue);
    void getMinValue(int aIndex, int& aValue);
    void getMaxValue(int aIndex, int& aValue);

    void getEmsParams(EmsParamMap &params);

  private:
    static INGwIfrSmStatMgr* mpSelf;

    // Initializes the statistics manager.  Allocates the maximum
    // number of parameter objects.
    INGwIfrSmStatMgr();

    // Updates the parameter at given index with the new given parameter
    void loadNewParam(INGwIfrSmStatParam* aNewParam, int aIndex);

    // Only threshold object requires the internal value access.
    friend class INGwIfrSmStatThreshold;
    void getInternalValue(int aIndex, int& aValue, int& aMaxValue,
                          int& aMinValue, bool& aIsSnapShot);

    // This thread performs all the deferred processing.
    INGwIfrSmDeferredThread mrDeferredThread;
    int miDeferredThreadDuration;

    // Instance of the xml parser.
    INGwIfrSmXmlParse* mXmlParser;

    // This map maps from the std::string form of the parameter key to the
    // integer index in the array of parameters.  The map is guarded, since
    // additions can happen to the map at runtime.
    ParamIndexMap mParamIndexMap;
    pthread_rwlock_t mParamIndexMapLock;

    // This value indicates the current statistics collection level.
    // This value is not guarded, but is runtime configurable.
    int miStatLevel;

    // The array of statistics paramters and its size
    INGwIfrSmStatParam* mStatParamList;
    int miMaxNumberOfStatParams;
}; // End of INGwIfrSmStatMgr class

#endif
