/************************************************************************
     Name:     INAP Stack Manager Repository Messages - defines
 
     Type:     C include file
 
     Desc:     Defines required for using repository fuctions

     File:     INGwSmRepository.h

     Sid:      INGwSmRepository.h 0  -  03/27/03 

     Prg:      gs

************************************************************************/

#ifndef __BP_AINSMREPOSITORY_H__
#define __BP_AINSMREPOSITORY_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"

#include <xercesc/dom/DOMErrorHandler.hpp>
#include <xercesc/util/XMLString.hpp>
#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/parsers/AbstractDOMParser.hpp>
#include <xercesc/dom/DOMImplementation.hpp>
#include <xercesc/dom/DOMImplementationLS.hpp>
#include <xercesc/dom/DOMImplementationRegistry.hpp>
#include <xercesc/dom/DOMBuilder.hpp>
#include <xercesc/dom/DOMException.hpp>
#include <xercesc/dom/DOMDocument.hpp>
#include <xercesc/dom/DOMNodeList.hpp>
#include <xercesc/dom/DOMError.hpp>
#include <xercesc/dom/DOMLocator.hpp>
#include <xercesc/dom/DOMAttr.hpp>
#include <xercesc/dom/DOMNamedNodeMap.hpp>
#include <xercesc/dom/DOMElement.hpp>
#include <xercesc/dom/DOMNodeList.hpp>


XERCES_CPP_NAMESPACE_USE


/*
 * this class will serve as a local repository of config/control info.
 */

class INGwSmRepository 
{
  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmRepository();

  //default destructor 
  ~INGwSmRepository();

  int initialize ();

  S16 gttHexAddrToBcd(LngAddrs *inpBuf,ShrtAddrs *bcdBuf);
  //read the xml file containing initial configuration
  int parseXmlFile (const char *apcFileName);

  //get a new transaction Id
  int getTransactionId();

  //MTP2 DLSAP data retrieval.
  int getMtp2DlsapParams(
    int& devId, int& dlSapId, int& channelNum, int index);

  //MTP2 general data retrieval.
  int getMtp2NmbLinks(int& numberOfLinks);

  //get the Statistics transaction Id
  int getStsTransId ();

  //read the system task creation
  int getSystemTaskMask (int &aiMask);

  //get the memory dump flag
  bool getDumpMemory ();

  //set the debug level
  int setDebugLevel (int aiLayer, int aiLevel);

  //get the debug level
  int getDebugLevel (int aiLayer);

  //set the trace level
  int setTrcLevel (int aiLayer, int aiLevel);

  //get the statistics timer
  int getTrcLevel (int aiLayer);

  //set the alarm level
  int setAlarmLevel (int aiLayer, int aiLevel);

  //set the transport type
  int setTransportType (int transportType);

  //get the statistics timer
  int getAlarmLevel (int aiLayer);

  //set the statistics timer
  void setStsTimer (int aiTimer) { miSmStsTmr = aiTimer; }

  //get the statistics timer
  int getStsTimer () { return miSmStsTmr; }

  //set the statistics level
  void setStsLevel (int aiLevel) { miStsLevel = aiLevel; }

  //get the statistics level
  int getStsLevel () { return miStsLevel; }

  //get the M3UA Service Provider id
  int getItSpId () { return miItSpId; }

  //get the M3UA Service user id
  int getItSuId () { return miItSuId; }

  //get the timer resolution for start timer
  int getTimerRes () { return miTimerRes; }

  //get the timer for distributor
  int getMonitorTimer () { return miDistTimer; }

  //get the transaction id for distributor based Xactions
  int getDistTransId () { return miDistTransId; }

  //get the AIN_MEM_PROB_EXIT flag
  bool getAinMemProbExit() { return mbAinMemProbExit; }

  /* 
   * Accessor interface for Management structures
   */

#if 0
  int getIeMngmt (int aiType, IeMngmt &aeMgmt, int aiIndex = -1);
#endif
  int getStMngmt (int aiType, StMngmt &aeMgmt, int aiIndex = -1);
  int getSpMngmt (int aiType, SpMngmt &aeMgmt, int aiIndex = -1);
  int getItMgmt  (int aiType, ItMgmt &aeMgmt, int aiIndex = -1);
  int getSbMgmt  (int aiType, SbMgmt &aeMgmt, int aiIndex = -1);
  int getHiMngmt (int aiType, HiMngmt &aeMgmt, int aiIndex = -1);
  int getSdMngmt (int aiType, SdMngmt &aeMgmt, int aiIndex = -1);
  int getSnMgmt (int aiType, SnMngmt &aeMgmt, int aiIndex = -1, int* apiCount = NULL);
  
  int getZtMngmt (int aiType, ZtMngmt &ztMgmt, int aiIndex = -1,int *apiCount = NULL);
  int getZpMngmt (int aiType, ZpMngmt &zpMgmt, int aiIndex = -1,int* apiCount = NULL);
  int getZnMngmt (int aiType, ZnMngmt &znMgmt, int aiIndex = -1,int* apiCount = NULL);
  int getZvMngmt (int aiType, ZvMngmt &zvMgmt, int aiIndex = -1,int* apiCount = NULL);
  int getDnMngmt (int aiType, LdnMngmt &dnMgmt, int aiIndex = -1, int *apiCount = NULL);
  int getDvMngmt (int aiType, LdvMngmt &dvMgmt, int aiIndex = -1, int *apiCount = NULL);
  int getSgMngmt (int aiType, SgMngmt &sgMgmt, int aiIndex = -1, int *apiCount = NULL);
  int getShMngmt (int aiType, ShMngmt &shMgmt, int aiIndex = -1, int *apiCount = NULL);
  int getMrMngmt (int aiType, MrMngmt &mrMgmt, int aiIndex = -1, int *apiCount = NULL);
  int getRyMngmt (int aiType, RyMngmt &ryMgmt, int aiIndex = -1, int *apiCount = NULL);
	  
#if 0
  int getZtMngmt (int aiType, ZtMngmt &ztMgmt, int aiIndex = -1,int *apiCount);
  int getZpMngmt (int aiType, ZpMngmt &zpMgmt, int aiIndex = -1,int* apiCount);
  int getZnMngmt (int aiType, ZnMngmt &znMgmt, int aiIndex = -1,int* apiCount);
  int getZvMngmt (int aiType, ZvMngmt &zvMgmt, int aiIndex = -1, int *apiCount);
  int getDnMngmt (int aiType, LdnMngmt &dnMgmt, int aiIndex = -1, int *apiCount);
  int getDvMngmt (int aiType, LdvMngmt &dvMgmt, int aiIndex = -1, int *apiCount);
  int getSgMngmt (int aiType, SgMngmt &sgMgmt, int aiIndex = -1, int *apiCount);
  int getShMngmt (int aiType, ShMngmt &shMgmt, int aiIndex = -1, int *apiCount);
  int getMrMngmt (int aiType, MrMngmt &mrMgmt, int aiIndex = -1, int *apiCount);
#endif
  /*
   * for MTP3 - get the point code, linkId and channel associated with
   * the SAPId of MTP3
   */
  int getSnLinkInfo (int aiSapId, int &aiOpc, int &aiDpc, 
                     int &aiLinkSetId, int &aiDevId, int &aiChannelNum);

  //accessor for statistics OID list
  int getStatisticsList (INGwSmStsOidList &aeOidList);

  //accessor function for post structure
  Pst *getPst (int aiLayerId);

  //accessor function for post structure
  int getTransportType () { return miTransportType; }

  //get state transition vector for the scenario
  int getStateTransitionList (int aiScenario, std::vector<int> &aeStateVector);

  //get the SM log file path
  std::string getSmLogFile ();

  //get the PSP Id List
  std::vector <INGwSmPeerId> &getPSPIdList () { return mePSPIdList; }

  //get the PSP State
  INGwSmPspState *getPspState (int aiPspId);

  //set the PSP State
  int setPspState (int aiPspId, int aiState);

  //add the PSP and state
  int addPspState (int aiPspId, int aiState);

  //get the PSP Id from the context or the list
  int getPspId (INGwSmRequestContext *apContext, int &aiIndex);

  //number of mtp3 dlsaps. Used for ctntrl
  //request looping.
  int miNumberOfMTP3Dlsaps;

  /* 
   * Protected interface
   */
  protected:

  //get the resource usage limits
  int getlimits();

  //get the value of the attribute for a DOM Element
  int getAttrVal (DOMElement *apNode, char *apTag, char *apVal);

  //get the OID for the configuration
  int getCfgOid (DOMElement *apNode, char *apNameTag,
                char *apName, char *apValTag, char *apValue);

  //get the OID for statistics
  int getStsOid (DOMElement *apNode, char *apcId, char*apcLayer,
                      char *apcType, char *apcElement, char *apcLevel);


  int getLayerStrToVal (char *apcLayerName);

  int getOperStrToVal (char *apcOperation);

  int getSpBpc (DOMNode *apNode, SpBpcCfg *aeBpcList, int &aiCount);

  int getSpConPc (DOMNode *apNode, Dpc *aeConPc, int &aiCount);

  int getSpSsnCfg (DOMNode *apNode, SpSsnCfg *aeSsn, int &aiCount);

  int getItNwkApp (DOMNode *apNode, ItNwkApp *aeNwkApp);

  int getItPsp (DOMNode *apNode, ItPspId *aePspList, int &aiCount);

  int getItAddrLst (DOMNode *apNode, SctNetAddrLst *aeAddrLst,
                                 int &aiCount);
  int getItAddrLst (DOMNode *apNode,CmNetAddr *aeAddrLst,int &aiCount);


  DOMNode* getCfgNode (char *apEl, char *apLayer, char *apType);

  int getLayerOpStr (int aiLayerOp, std::string &astrLayer);

  int addSnLinkInfo (INGwSmLinkInfo *apLinkInfo);
  int addSdChannelInfo (INGwSmLinkInfo *apLinkInfo);
  int getDvSap (DOMNode *apNode, LdvSapCfg *aeDvSingleSap, int numSaps);
  int getDvSingleSap (DOMNode *apNode, LdvSingleSapCfg *aeDvSingleSap);
  int getDvSingleNwk (DOMNode *apNode, LdvSingleNwkCfg *aeDvSingleNwk);
  int getDvNwks (DOMNode *apNode, LdvNwkCfg *aeDvSingleNwk, int numNwks);
  int getAllNwkApp (DOMNode *apNode, U8 nwkApp[] );

  void getEntCfg(DOMNode *lpNode,char *lpName, char *lpValue, SgHiEntCfg *entCfg);
  int getSpDefSnri (DOMNode *apNode, U8 *defSNRI,int &aiCount);
  int getZvDvKyVal (DOMNode *apNode, CmZvDvKyVal *aeKyVal);
  int getU32List (DOMNode *apNode, Dpc *aePc,int &aiCount);
  int getU8ParmList (DOMNode *apNode, U8 *aeParm,int &aiCount);
  int getZnDnKyVal (DOMNode *apNode, CmZnDnKyVal *aeZnDnKyVal);
  int getZvDvDynamic (DOMNode *apNode, CmZvDvDynamic *aeDyn);
  int getZvDvRsetMap (DOMNode *apNode, CmZvDvRsetMap *aeRsetMap);
  int getDnSingleSap (DOMNode *apNode, LdnSingleSapCfg *aeDnSingleSap);
  int getDnSap (DOMNode *apNode, LdnSapCfg *aeDnSingleSap, int numSaps);
  int getZnDnRsetMap (DOMNode *apNode, CmZnDnRsetMap *aeZnDnRsetMap);
  int getZnDnDynamic (DOMNode *apNode, CmZnDnDynamic *aeZnDnDyn);
  int getZvDvRsetDefn (DOMNode *apNode, CmZvDvRsetDefn *aeRsetDefn);
  int getZnDnRsetDefn (DOMNode *apNode, CmZnDnRsetDefn *aeZnDnRsetDefn);
  int getZnDnRsetGen (DOMNode *apNode, CmZnDnRsetGenCfg *aeRsetGen);
  int getZvDvRsetGen (DOMNode *apNode, CmZvDvRsetGenCfg *aeRsetGen);
  int getSpSelfPc (DOMNode *apNode, Dpc *aeSelfPc,int &aiCount);
#ifdef LSPV2_8
  int getSpAspSsn (DOMNode *apNode, SpAsSsn *aspList,int &aiCount);
  int getSpAspPcSsnList(DOMNode *apNode, SpAspPcSsnList *aspList,int &aiCount);
#endif
  int getItSelfAspId (DOMNode *apNode, U32 *selfAspId,int &aiCount);
  int getHiRsetSpec (DOMNode *apNode, SgHiRsetSpec *aeParm,int &aiCount);
  int getHiPrngFile (DOMNode *apNode, S8 *file, int &aiCount);
  int getEpIds (DOMNode *apNode, U32 *aeEpIds, int &aiCount);
  int getHiPrngSeed (DOMNode *apNode, U8 *seed, int &aiCount);
  int getItPspEpList (DOMNode *apNode, ItPspEp *pspEpList, int &aiCount);



  /*
   * Private interface
   */
  private:

   int
   fillMtpCtlStopStates(INGwSmIntVector &aeStateVector);

   int
   fillMtpCtlAdtStates(INGwSmIntVector &aeStateVector);

   int
   fillMtpCtlSapStates(INGwSmIntVector &aeStateVector);

   int
   fillMtpCfgStates(INGwSmIntVector &aeStateVector);

   int
   fillCommonCfgStates(INGwSmIntVector &aeStateVector);

   int
   fillCommonCtlAdtStates(INGwSmIntVector &aeStateVector);

   int
   fillCommonCtlSapStates(INGwSmIntVector &aeStateVector);

   int
   fillSigtranCtlSapStates(INGwSmIntVector &aeStateVector);

   int
   fillSigtranCfgStates(INGwSmIntVector &aeStateVector);

   int
   fillSigtranCtlAdtStates(INGwSmIntVector &aeStateVector);

   int
   fillSgCtlStates(INGwSmIntVector &aeStateVector);

   int
   fillSigtranCtlStopStates(INGwSmIntVector &aeStateVector);

   int
   fillAddM3uaNetworkState(INGwSmIntVector &aeStateVector);
   int
   fillDelM3uaNetworkState(INGwSmIntVector &aeStateVector);
   int
   fillDelSccpNetworkState(INGwSmIntVector &aeStateVector);
   int
   fillAddSccpNetworkState(INGwSmIntVector &aeStateVector);
    
   int fillAddLnkSetState(INGwSmIntVector &aeStateVector);
   int fillAddMtp3RouteState(INGwSmIntVector &aeStateVector);
   int fillAddSccpRouteState(INGwSmIntVector &aeStateVector);
   int fillAddSccpLocalSsnState(INGwSmIntVector &aeStateVector);
   int fillAddTcapLocalSsnUsapState(INGwSmIntVector &aeStateVector);
   int fillAddTcapLocalSsnLsapState(INGwSmIntVector &aeStateVector);
   int fillAddSccpRemoteSsnState(INGwSmIntVector &aeStateVector);
   int fillAddM3uaAsState(INGwSmIntVector &aeStateVector);
   int fillAddM3uaRouteState(INGwSmIntVector &aeStateVector);
   int fillAddM3uaAspState(INGwSmIntVector &aeStateVector);
   int fillAddM3uaEpState(INGwSmIntVector &aeStateVector);
   int fillAddSctpTsapState(INGwSmIntVector &aeStateVector);
   int fillAddSctpSctsapState(INGwSmIntVector &aeStateVector);
   int fillDelMtp3LinkState(INGwSmIntVector &aeStateVector);
   int fillDelLdfMtp3LinkState(INGwSmIntVector &aeStateVector);
   int fillDelMtp2LinkState(INGwSmIntVector &aeStateVector);
   int fillDelMtp3LinkSetState(INGwSmIntVector &aeStateVector);
   int fillDelM3uaNwkState(INGwSmIntVector &aeStateVector);
   int fillDelSccpNwkState(INGwSmIntVector &aeStateVector);
   int fillDelSccpUpState(INGwSmIntVector &aeStateVector);
   int fillDelM3uaUpState(INGwSmIntVector &aeStateVector);
   int fillDelMtp3UpState(INGwSmIntVector &aeStateVector);
   int fillDelLdfM3uaUpState(INGwSmIntVector &aeStateVector);
   int fillDelLdfMtp3UpState(INGwSmIntVector &aeStateVector);
   int fillDelSccpRSsnState(INGwSmIntVector &aeStateVector);
   int fillDelSccpLSsnState(INGwSmIntVector &aeStateVector);
   int fillDelTcapLsapState(INGwSmIntVector &aeStateVector);
   int fillDelTcapUsapState(INGwSmIntVector &aeStateVector);
   int fillDelM3uaRouteState(INGwSmIntVector &aeStateVector);
   int fillDelMtp3RouteState(INGwSmIntVector &aeStateVector);
   int fillDelSccpRouteState(INGwSmIntVector &aeStateVector);
   int fillDelM3uaEpState(INGwSmIntVector &aeStateVector);
   int fillDelSctpEpState(INGwSmIntVector &aeStateVector);
   int fillDelTuclEpState(INGwSmIntVector &aeStateVector);
   int fillDelM3uaAspState(INGwSmIntVector &aeStateVector);
   int fillDelM3uaAsState(INGwSmIntVector &aeStateVector);
   int fillDelM3uaAsRteState(INGwSmIntVector &aeStateVector);
   int fillAddSccpGtAddrMapState(INGwSmIntVector &aeStateVector);
   int fillAddSccpGtRuleState(INGwSmIntVector &aeStateVector);
   int fillDelSccpGtAddrMapState(INGwSmIntVector &aeStateVector);
   int fillDelSccpGtRuleState(INGwSmIntVector &aeStateVector);
   
  /*
   * Public Data Members
  *
   * Public Data Members
   */
  public:

  /* 
   * Protected Data Members
   */
  protected:

  //tranport type (MTP or SIGTRAN)
  int miTransportType;

  //whether the XML files is to be overridden or not
  bool mbOverrideXml;

  //exit on memory problems
  bool mbAinMemProbExit;

  //transactionId to be created to correlate between request and response
  int miTransactionId;

  /*
   * data members specific to statistics Mgr
   */
  //transaction Id for the Statistics request
  int miStsTransId;

  //the statistics level
  int miStsLevel;

  //whether the memory tables of the stack need to be dumped
  bool mbDumpMemory;

  //the alarm level for individual layers
  int miIeAlmLevel;
  int miStAlmLevel;
  int miSpAlmLevel;
  int miItAlmLevel;
  int miSbAlmLevel;
  int miHiAlmLevel;
  int miSnAlmLevel;
  int miSdAlmLevel;

  //the debug level for individual layers
  int miIeDbgLevel;
  int miStDbgLevel;
  int miSpDbgLevel;
  int miItDbgLevel;
  int miSbDbgLevel;
  int miHiDbgLevel;
  int miSnDbgLevel;
  int miSdDbgLevel;

  //the trace level for individual layers
  int miIeTrcLevel;
  int miStTrcLevel;
  int miSpTrcLevel;
  int miItTrcLevel;
  int miSbTrcLevel;
  int miHiTrcLevel;
  int miSnTrcLevel;
  int miSdTrcLevel;

  //M3UA Service Provider id for Upper SAP
  int miItSpId;

  //M3UA Service User id for Lower SAP
  int miItSuId;

  //the point code and SSNs for the CCM are stored here
  struct {
    //point code for self
    INGwSmPc miSelfPC;

    //network type for self
    int miSelfNetworkType;

    //the ASP Id of CCM
    int miSelfAspId;


    //port for listening
    int selfPort;
 
    //the ip addresses of the CCM
    AddressVector  meSelfAddress;

    //the SSNs supported by seld
    INGwSmSsn  meSelfSsn;
  } self;

  //Routes configured to DPC in SCCP are stored here
  std::map <INGwSmPc, std::vector<INGwSmDestRoute*> > meDpcRoute;

  //the ip addresses of the peer
  std::map<INGwSmPeerId, AddressVector*>  mePeerAddress;

  std::vector <INGwSmPeerId> mePSPIdList;

  //map for PSP -> States
  typedef std::map <int, INGwSmPspState *> INGwSmPspStateMap;

  INGwSmPspStateMap mePspStateMap;

  typedef std::map <int, INGwSmLinkInfo *> INGwSmLinkInfoMap;

  INGwSmLinkInfoMap mLinkMap;
  pthread_rwlock_t mLinkLock;

  //duration for statistics collection
  int miSmStsTmr;

  //duration for timer resolution
  int miTimerRes;

  //duration for distributor timer
  int miDistTimer;

  //transaction id for distributor
  int miDistTransId;
  int miAuditTransId;

  //file name for storing traces of the Stack
  std::string mstrSmLogFile;

  //file name for reading the Statistics OID mapping
  std::string mstrSmXmlFile;

  //post structure SM --> Layers
  Pst  liePst;
  Pst  lstPst;
  Pst  lspPst;
  Pst  litPst;
  Pst  lsbPst;
  Pst  lhiPst;
  Pst  lsnPst;
  Pst  lsdPst;
  Pst  lsgPst;
  Pst  lshPst;
  Pst  lmrPst;
  Pst  ldnPst;
  Pst  ldvPst;
  Pst  lzvPst;
  Pst  lznPst;
  Pst  lztPst;
  Pst  lzpPst;
  Pst  lryPst;

  /* 
   * Private Data Members
   */
  private:

  AbstractDOMParser::ValSchemes valScheme;
  bool       doNamespaces;
  bool       doSchema;
  bool       schemaFullChecking;
  bool       recognizeNEL;
  char       localeStr[64];
  DOMBuilder  *parser;
  DOMDocument *doc;
  DOMImplementation *impl;
  DOMNode    *mpRootNode;

  bool       mbAnyParsingErrors;

  // ---------------------------------------------------------------------------
  //  Simple error handler deriviative to install on parser
  // ---------------------------------------------------------------------------
  class DOMCountErrorHandler : public DOMErrorHandler
  {   
    public:
    // -----------------------------------------------------------------------
    //  Constructors and Destructor
    // -----------------------------------------------------------------------
    DOMCountErrorHandler() { fSawErrors = false; }
    ~DOMCountErrorHandler() {}

    
    // -----------------------------------------------------------------------
    //  Getter methods
    // -----------------------------------------------------------------------
    bool getSawErrors() const { return fSawErrors; }

    
    // -----------------------------------------------------------------------
    //  Implementation of the DOM ErrorHandler interface
    // -----------------------------------------------------------------------
    bool handleError(const DOMError& domError)
    {
      fSawErrors = true;

      if (domError.getSeverity() == DOMError::DOM_SEVERITY_WARNING)
        std::cerr << "\nWarning at file ";
      else if (domError.getSeverity() == DOMError::DOM_SEVERITY_ERROR)
        std::cerr << "\nError at file ";
      else
        std::cerr << "\nXML Fatal Error at file ";

      char *lpcUri = XMLString::transcode 
                      (domError.getLocation()->getURI());
      char *lpcMsg = XMLString::transcode 
                      (domError.getMessage ());

      std::cerr << lpcUri << ", line " 
         << domError.getLocation()->getLineNumber()
         << ", char " << domError.getLocation()->getColumnNumber()
         << "\n  Message: " << lpcMsg << std::endl;

      XMLString::release (&lpcUri);
      XMLString::release (&lpcMsg);

      return true;
    }

    void resetErrors() { fSawErrors = false; }
    

    private :
    // -----------------------------------------------------------------------
    //  Unimplemented constructors and operators
    // -----------------------------------------------------------------------
    DOMCountErrorHandler(const DOMCountErrorHandler&);
    void operator=(const DOMCountErrorHandler&);
    
    
    // -----------------------------------------------------------------------
    //  Private data members
    // 
    //  fSawErrors
    //      This is set if we get any errors, and is queryable via a getter
    //      method. Its used by the main code to suppress output if there are
    //      errors.
    // -----------------------------------------------------------------------
    bool    fSawErrors;
  };

};

#endif /* __BP_AINSMREPOSITORY_H__ */
