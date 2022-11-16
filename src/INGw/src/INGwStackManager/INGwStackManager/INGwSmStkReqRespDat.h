#ifndef _STK_REQ_RESP_DAT_H
#define _STK_REQ_RESP_DAT_H


#include<lsp.h>
#include<lsn.h>
#include <list>
#include "INGwStackManager/INGwSmQueueMsg.h"
//using namespace std;

#define MAX_NUM_OF_ACTION 20
#define LSN_MAX_PREFFERED_LINKS 256 
#define MAXSPC 10
#define MAX_NUM_OF_ACTION 20
#define MAX_GT_DIGITS 32

#ifndef DEF_MAX_BUFFER_SIZE
#define  DEF_MAX_BUFFER_SIZE 10
#endif 

#define ENTZN ENTLAST+1
#define ENTZV ENTLAST+2
#define ENTZP ENTLAST+3
#define ENTZT ENTLAST+4



typedef enum _lnkLayers
{
   LINK_MTP3,
   LINK_MTP2
}LnkLayers;
typedef enum _nodeType
{
   NODE_SS7,
   NODE_SIGTRAN,
   NODE_SS7SIGTRAN
}SM_NodeType;

typedef struct _shutDownLayers {
  int   nmbEntity; 
	U8    entId[20];   /* list of layers which needs to be shutdown */
} ShutDownLayers;

typedef struct psStatus
{
   U32 psId;
}PsStatus;

typedef struct pspStatus
{
   U32 pspId;
}PspStatus;


typedef struct localSsnStatus
{
   U32 dpc;
   U8 ssn;
   U16 nwkId;   
}LocalSsnStatus;


/* structure to add Link */

typedef struct addLink
{
  U16 lnkId;              /* signalling link identity */
  U16 lnkSetId;           /* Link Set Id */
  U8 lnkName[20];         /* Link name */
  U8 dpcLen;              /* dpc or opc length */
  U32 opc;                /* Originating Point Code */
  U32 adjDpc;             /* Adlacent Destination Point Code */
  U16 physPort;           /* physical trunk for AIOCSETPORT */
  U16 timeSlot;           /* DS0 in the range 0-30 */
  U8 ssf;                 /* sub service field */
  U8 slc;                 /* link selection code for link test */
  U8 lnkType;             /* link type ANSI, ITU, BICI or CHINA */
  U8 lnkPrior;            /* link priority within the link set */

  S16 mtp2UsapId;         /* sap Id's */
  S16 mtp3LsapId;         /* sap Id's */
  U32 mtp2ProcId;

  U8 currentLinkState;        /* Current status of the link */
}AddLink;

/* Structure to delete Link */

typedef struct delLink
{
  U16 lnkId;
  U16 mtp2ProcId;
  S16 mtp2UsapId;
  S16 mtp3LsapId;
}DelLink;

/* Structure to get link status */

typedef struct linkStatus
{
  U16 lnkId;
  U8 layer;
  S16 mtp2UsapId;         /* sap Id's */
  S16 mtp3LsapId;         /* sap Id's */

}LinkStatus;

/* Structure to disable or enable link */

typedef struct linkEnable
{
  U16 lnkId;
  U16 lnkSetId;
  U16 procId;
  S16 mtp2UsapId;
  S16 mtp3LsapId;

}LinkEnable;

typedef struct linkDisable
{
  U16 lnkId;
  U16 procId;
  U16 lnkSetId;
  S16 mtp2UsapId;
  S16 mtp3LsapId;

}LinkDisable;

typedef struct snCmbineLnkSet
{
  U16 cmbLnkSetId;
  U8 lnkSetPrior;                               /* link set priority */
  U16 nmbPrefLinks;                             /* Num of preferred links for this combined */
  U16 prefLnkId[LSN_MAX_PREFFERED_LINKS];       /* LSN_MAX_PREFFERED_LINKS= 256 preferred links  */

}SnCmbineLnkSet;

/* Structure to add linkset */

typedef struct addLinkSet
{
  U16 lnkSetId;           /* Link Set Id */
  U8 lnkSetType;          /* lnkset type ANSI, ITU, BICI or CHINA */
  U32 adjDpc;             /* Adlacent Destination Point Code */
  U16 nmbActLnkReqd;      /* number of active links required in the linkset */
  U16 nmbCmbLnkSet;       /* number of combined link sets */
  SnCmbineLnkSet cmbLnkSet[LSN_MAXCMBLNK]; /* LSN_MAXCMBLNK=16 combined link set */
}AddLinkSet;

/* Structure to delete linkset */

typedef struct delLinkSet
{
  U16 lnkSetId;

}DelLinkSet;

/* Structure to get linkset status */

typedef struct linkSetStatus
{
  U16 lnkSet;
  U16 cmbLnkSetId;
}LinkSetStatus;


typedef struct _spSsnCfg
{
   U8 ssn;                    /* subsystem number */
   U8 status;                  /* status */
#if (SS7_ANS96 || SS7_BELL05)
   U8 replicatedMode;          /* Mode of replicated node/subsystem :
                                  DOMINANT, LOADSHARE, DOMINANT_ALTERNATRE,
                                  LOADSHARE_ALTERNAE */
#endif /* (SS7_ANS96 || SS7_BELL05) */
   U16 nmbBpc;                 /* number of backup point codes */
   SpBpcCfg bpcList[MAXNUMBPC]; /* backup point code list */
   U16 nmbConPc;               /* number of concerned point codes */
   Dpc conPc[MAXCONPC];        /* concerned point codes */
} SPSsnCfg;

typedef struct sp_bpc_cfg
{
   U32 bpc;
   U8  prior;
}Sp_Bpc_Cfg;

typedef struct _addRoute
{
	U32 dpc;                   /* destination point code */

  /* MTP3 parameters */
  U8 swtchType;                /* switch type */
  U8 spType;                   /* Signalling point type */
  U8 upSwtch;                  /* user part switch type */
  U16 cmbLnkSetId;             /* combined link set ID */
  U8 dir;                      /* direction */
  Bool rteToAdjSp;             /* flag indicating this route to adjacent SP */
  U8 ssf;                      /* sub service field */

  /* SCCP Parameters */
  S16 swtch;
  U8 status;                  /* adjacent flag */
  U16 nmbBpc;                 /* number of backup point codes */
  Sp_Bpc_Cfg bpcList[MAXNUMBPC]; /* backup point code list */
  U8 nmbSsns;                 /* number of subsystems */
  SPSsnCfg ssnList[MAXNUMSSN];/* subsystems for this dpc */
#if (SS7_ANS96 || SS7_BELL05)
  U8 replicatedMode;          /* Mode of replicated node/subsystem :   DOMINANT, LOADSHARE, DOMINANT_ALTERNATRE,
                                                                      LOADSHARE_ALTERNAE */
#endif
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05)
  U8 slsMask;
#endif /* SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05 */
  U32 preferredOpc;          /* preferred OPC */
  S16 nSapId;                    /* network SAP identifier */

  U8 currentDpcState;        /* Current status of the DPC */

#ifdef LSPV3_1
   S16  defaultRoutenSapId;   /* Indicates which of SS7 and SIGTRAN route has
                                  priority - Reconfigurable */
   Bool  secRteCfg;            /* Set to TRUE if secondary route is configured
                                  - Reconfigurable */
   S16  nSapId2;              /* NSap id of route using Service provider2  -
                                  - Reconfigurable */
   U8    nSap1RteStatus;       /* Status of route through service provider1 */
   U8    nSap2RteStatus;       /* Status of route through service provider2 */
#endif /* LSPV3_1 */

} AddRoute;

typedef struct _delRoute
{
  U32 dpc;
  S16 nSapId;
  U8 upSwtch;
}DelRoute;

typedef struct _routeStatus
{
	U32 dpc;
	U16 nwkId;

}RouteStatus;

typedef struct _nodeStatus
{
	U8          entId;                          /* entity identifier    */
	U8         instId;                         /* instance identifier  */
	U16       procId;                         /* processor identifier */
}NodeStatus;

typedef enum _userPartType {
  MTP3_USER = 1,
  M3UA_USER
} UserPartType;
/* Structure to add User Part */


typedef struct _addUserPart
{
  /* MTP3 Parameters */
  U8 userPartType;             /* MTP3 uaer of M3UA user */
  U8 ssf;                      /* sub service field */
  U8 lnkType;                  /* link type ANSI, ITU, BICI or CHINA */
  U8 upSwtch;                  /* user part switch type */
  S16 mtp3UsapId;
    
  /* M3UA Parameters */
  U16 nwId;
  U8  suType;                  /* service user protocol type */
 
  S16 m3uaUsapId;
  S16 sccpLsapId;

  U8 currentUserState;        /* Current user state */ 

}AddUserPart;


/* Structure to delete User part */

typedef struct _delUserPart
{
  U8 userPartType;             /* MTP3 uaer of M3UA user */
  S16 mtp3UsapId;
  S16 m3uaUsapId;
  S16 sccpLsapId;

}DelUserPart;

typedef struct _enableUserPart
{
  S16 mtp3UsapId;
  S16 m3uaUsapId;
  S16 sccpLsapId;
  U8 nwkType;  /* SS7 or SIGTRAN */

}EnableUserPart;

typedef struct _disableUserPart
{
  S16 mtp3UsapId;
  S16 m3uaUsapId;
  S16 sccpLsapId;
  U8 nwkType;  /* SS7 or SIGTRAN */

}DisableUserPart;

/* Structure to add network */

typedef struct _addNetwork
{
  /* SCCP Paramters */
  U16 nwId;                  /* network identifier */
  U16 variant;              /* switch */
  Bool spcBroadcastOn;        /* flag to control broadcast of mngt msgs */
  U32 defaultPc;              /* Default SPC for this network */
  U8  nmbSpcs;                /* Total Number of SPCs */
  U32 selfPc[MAXSPC];         /* array of self point code for this network */
  U8 niInd;                   /* mgmt messages use this bit in addr ind */
  U8 subService;              /* sub-service field used in SIO */

  /* M3UA Paramters */
  U32 nwkApp[LIT_MAX_PSP]; /* network appearance code */
  U8  ssf;           /* sub service field */
  U8  dpcLen;        /* dpc or opc length */
  U8  slsLen;        /* sls length */
  S16 suSwtch;       /* protocol variant of service user */
  S16 su2Swtch;      /* protocol variant of user of */
  S16 protoType;     /* MTP2=0, SIGTRAN=1, HYBRID=2 rxed from EMS */
}AddNetwork;

/* Structure to delete network */

typedef struct _delNetwork
{
  U16 nwkId;
  U16 variant;
}DelNetwork;

typedef struct gtAction
{
   U8 type;
   U8 nmbActns;
   U8 startDigit;
   U8 endDigit;
}GtAction;
/* Structure to add gtrule */

typedef struct _addGtRule
{
  U16 nwId;
  S16 sw;          /* To identify the Rule */
  U8 formatPres;
  U8 format;                  /* format */
  Bool oddEven;       /* odd/even indicator */
  U8 oddEvenPres;
  U8 natAddr;         /* nature of address */
  U8 natAddrPres;
  U8 tType;           /* translation type */
  U8 tTypePres;
  U8 numPlan;         /* numbering plan */
  U8 numPlanPres;
  U8 encSch;          /* encoding scheme */
  U8 encSchPres;
U8 nmbActns;
  GtAction actn[MAX_NUM_OF_ACTION];

}AddGtRule;

typedef struct _delGtRule
{
  U16 nwId;
  S16 sw;          /* To identify the Rule */
  U8 formatPres;
  U8 format;                  /* format */
  U8 oddEvenPres;
  U8 natAddrPres;
  Bool oddEven;       /* odd/even indicator */
  U8 natAddr;         /* nature of address */
  U8 tTypePres;
  U8 tType;           /* translation type */
  U8 numPlanPres;
  U8 numPlan;         /* numbering plan */
  U8 encSch;          /* encoding scheme */
  U8 encSchPres;
U8 nmbActns;
  GtAction actn[MAX_NUM_OF_ACTION];

}DelGtRule;

typedef struct _outAddr
{
   U8 spHdrOpt;
   S16 swtch;
   U8 ssf;             /* Sub-Service field */
   Bool niInd;         /* national/international indicator */
   U8 rtgInd;          /* routing indicator */
   Bool ssnInd;        /* subsystem number indicator */
   Bool pcInd;         /* point code indicator */
   U8 ssn;             /* subsystem number */
   U32 pc;             /* point code */
   U8 format;          /* format */
   Bool oddEven;       /* odd/even indicator */
   U8 tType;           /* translation type */
   U8 natAddr;         /* nature of address */
   U8 numPlan;         /* numbering plan */
   U8 encSch;          /* encoding scheme */
   U8 gtDigLen;
   U8 gtDigits[SHRTADRLEN];  /* SHRTADRLEN 32 */
}OutAddr;

/* Structure to add Address Map */

typedef struct _addAddrMapCfg
{
   U16 nwkId;
   S16 sw;             /* To identify the Rule */
   U8 format;          /* format */
   Bool oddEven;       /* odd/even indicator */
   U8 natAddr;         /* nature of address */
   U8 tType;           /* translation type */
   U8 numPlan;         /* numbering plan */
   U8 encSch;          /* encoding scheme */
   GtAction actn;
   
   U8 gtDigLen;
   U8 gtDigits[MAX_GT_DIGITS]; 
   Bool replGt;     /* If TRUE replace outgoing Addr with the incoming one */
   U8 mode;         /* mode of operation of SCCP entities: DOMINANT/LOADSHARE */
#ifdef GTT_PER_NWK   
   U16 outNwId;
#endif  /* GTT_PER_NWK */
   U8 numEntity;    /* number of SCCP entities */
   OutAddr outAddr[MAXENTITIES];
   
}AddAddrMapCfg;

typedef struct _delAddrMapCfg
{
   U16 nwkId;
   S16 sw;             /* To identify the Rule */
   U8 format;          /* format */
   Bool oddEven;       /* odd/even indicator */
   U8 natAddr;         /* nature of address */
   U8 tType;           /* translation type */
   U8 numPlan;         /* numbering plan */
   U8 encSch;          /* encoding scheme */
   GtAction actn;
   
   U8 gtDigLen;
   U8 gtDigits[MAX_GT_DIGITS]; 
   Bool replGt;     /* If TRUE replace outgoing Addr with the incoming one */
   U8 mode;         /* mode of operation of SCCP entities: DOMINANT/LOADSHARE */
#ifdef GTT_PER_NWK   
   U16 outNwId;
#endif  /* GTT_PER_NWK */
   U8 numEntity;    /* number of SCCP entities */
   OutAddr outAddr[MAXENTITIES];
   
}DelAddrMapCfg;
/* Structure to add local ssn */

typedef struct _addLocalSsn
{
  /* SCCP Parameters */
  U16 nwId;
  U16 nmbBpc;                   /* number of backup point codes */
  SpBpcCfg bpcList[MAXNUMBPC];  /* backup point code list */
  U16 nmbConPc;                 /* number of concerned point codes */
  U32 conPc[MAXCONPC];          /* number of concerned point codes */
  S16 sccpUsapId;

  /*TCAP Parameters */
  U8  swtch;
  U8  ssn;
  S16 tcapLsapId;               /* sap Id's */
  S16 tcapUsapId;

  U8 currentSsnState;                 /* Current state of SSN */
}AddLocalSsn;


/* Structure to delete local ssn */

typedef struct _delLocalSsn
{
  U8 ssn;
  S16 tcapLsapId;
  S16 tcapUsapId;
  S16 sccpUsapId;

}DelLocalSsn;


/* Structure to unbind saps for del local ssn */

typedef struct _disSap
{
  S16 tcapLsapId;
  S16 tcapUsapId;
  S16 sccpUsapId; 

}DisSap;

/* Structure to delete remote ssn */

typedef struct _delRemoteSsn
{
  U8 ssn;
  S16 nSapId;                    /* network SAP identifier */
  U32 dpc;
}DelRemoteSsn;

/* Structure to enable or disable ssn */

typedef struct _ssnDisable
{
  U8 ssn;
  S16 tcapLsapId;
  S16 tcapUsapId;
  S16 sccpUsapId;

}SsnDisable;

typedef struct _ssnEnable
{
  U8 ssn;
  S16 tcapLsapId;
  S16 tcapUsapId;
  S16 sccpUsapId;

}SsnEnable;

typedef struct itPspEp
{
   U32       nmbEp;
   U32       endpIds[LIT_MAX_SEP];
}ITPspEp;

/* Structure to add PS */

typedef struct _addPs
{
  U32   psId;          /* Peer Server ID */
  U32   routCtx;       /* Routing Context */
  U8    nwkId;         /* Network ID */
  U8    mode;          /* Active/Standby or load sharing */
  U8    loadShareMode; /* Round robin, SLS mapping etc. */
  U16   nmbActPspReqd; /* number of active PSPs sharing load */
  U16   nmbPsp;        /* number of entries in PSP list */
  U16   psp[LIT_MAX_PSP]; /* ordered list of PSPs */
  Bool  lFlag;       /* PS type is local if set */
  ITPspEp  pspEpLst[LIT_MAX_PSP];
  /* route entry configuration */
  U8    rtType;        /* Route Type */
  U32   dpcMask;       /* DPC wildcard mask */
  U32   dpc;           /* Destination Point Code */
  U32   opcMask;       /* OPC wildcard mask */
  U32   opc;           /* Originator Point Code */
  U8    slsMask;       /* Link Selector wildcard mask */
  U8    sls;           /* Link Selector */
  U8    sioMask;       /* SIO wildcard mask */
  U8    sio;           /* Service Identifier Octet */

  U8    currentPsState;      /*Current state of PS */
}AddPs;

/* Structure to delete PS */

typedef struct _delPs
{
  U32   psId;
  /* Route entry config */
  U8    nwkId;
  U8    rtType;        /* Route Type */
  U32   dpcMask;       /* DPC wildcard mask */
  U32   dpc;           /* Destination Point Code */
  U32   opcMask;       /* OPC wildcard mask */
  U32   opc; 
}DelPs;

typedef U8  CmIpAddr6[16];

/* Structure to delete PSP */

typedef struct _delPsp
{
  U32 pspId;
}DelPsp;

typedef struct ipAddr
{
   U8   type;                 /* type of network address */
   union
   {
      U32  ipv4NetAddr;       /* IP network address */
      CmIpAddr6  ipv6NetAddr; /* IPv6 network address */
   }u;
} IPAddr;

/* Structure to add PSP */

typedef struct _addPsp
{
  U32  pspId;                      /* PSP id */
  U8   pspType;                    /* PSP client/server type */   
  U8   ipspMode;                   /* IPSP single ended/double ended type */   
  U8   nmbAddr;
  IPAddr addr[SCT_MAX_NET_ADDRS];   /* SCT_MAX_NET_ADDRS = 5 */
  U16  dstPort;                    /* destination port */
  U8   nwkId;
  Bool includeRC;                  /*The value of this parameter controls whether RC has to be included in the outgoing messages where RC is optional.*/ 
  Bool cfgForAllLps;               /* Configure for all local PSs? */

  U8   currentPspState;                   /* Curent state of psp */
}AddPsp;


/* Structure to send assoc up or down */

typedef struct _m3uaAssocDown
{
	U32 assocId;
  U32 pspId;
  U16 endPointId;
  S16 m3uaLsapId;
}M3uaAssocDown;

typedef struct _m3uaAssocAbort
{
	U32 assocId;
  U32 pspId;
#ifdef IT_ABORT_ASSOC
  U8 abrtFlag;
#endif
  S16 m3uaLsapId;
}M3uaAssocAbort;

typedef struct _unbindSap {
  int stackLayer;
  S16 sapId;
} UnbindSap;

typedef struct _bindSap {
  int stackLayer;
  S16 sapId;
} BindSap;


typedef struct _m3uaAssocUp
{
	U32 assocId;
  U32 pspId;
  U16 endPointId;
  S16 m3uaLsapId;
  U16 currentAssocState;
  bool isRetry;
}M3uaAssocUp;

/* Structure to send asp up or down */
typedef struct _m3uaAsp_up
{
  U32 pspId;
  U16 endPointId;
  S16 m3uaLsapId;

}M3uaAspUp;

typedef struct _m3uaAsp_Down
{
  U32 pspId;
  U16 endPointId;
  S16 m3uaLsapId;

}M3uaAspDown;

/* Structure to send asp-ac or asp-inac */
typedef struct m3uaAspAct
{
  U32 psId;
  U32 pspId;
  U16 endPointId;
  S16 m3uaLsapId;

}M3uaAspAct;

typedef struct m3uaAspInAct
{
  U32 psId;
  U8  nmbPs;
  U32 psLst[LIT_MAX_PSID];
  U32 pspId;
  U16 endPointId;
  S16 m3uaLsapId;

}M3uaAspInAct;

#ifdef INC_ASP_SNDDAUD
typedef struct _daud {
  U8     nwkId;
  U32    dpc;
  U32    psId;
} Daud;
#endif


typedef struct cMNetAddr
{
   U8   type;      /* type of network address */
   union
   {
      U32 ipv4NetAddr; /* IP network address */
      CmIpAddr6  ipv6NetAddr; /* IPv6 network address */
   }u;
} CMNetAddr;

/* structure to add enpoint */
typedef struct _addEndPoint
{
	/* M3UA Parameters */
   U8 	         endPointid;
   U16           srcPort;                    /* source port for listening endpoint */
   U8            nmbAddrs;                        /* Number of Network Addresses */
   CMNetAddr     nAddr[SCT_MAX_NET_ADDRS];   /* List of Network Addresses */
   U16           sctpProcId;                     /* processor ID */
   S16           sctpLsapId;                 /* sctp lower sapId */
   S16           sctpUsapId;                 /* sctp Upper sapId spId*/
   S16           m3uaLsapId;                 /* M3UA lower sapId suId*/
   S16           tuclUsapId;                 /* TUCL upper sapId */

   U8            currentEpState;             /* Current state of EndPoint */
}AddEndPoint;

/* structure to delelt EndPoint */

typedef struct _delEndPoint
{
	U16 sctpProcId;
	S16 sctpLsapId;
	S16 sctpUsapId;
	S16 m3uaLsapId;
	S16 tuclUsapId;

}DelEndPoint;

typedef struct _disableEndPoint
{
	U16 sctpProcId;
	S16 sctpLsapId;
	S16 sctpUsapId;
	S16 m3uaLsapId;
	S16 tuclUsapId;

}DisableEndPoint;

typedef struct _enableEndPoint
{
	U16 sctpProcId;
	S16 sctpLsapId;
	S16 sctpUsapId;
	S16 m3uaLsapId;
	S16 tuclUsapId;

}EnableEndPoint;

typedef struct _openEp
{
	S16 suId;

}OpenEp;


/* structure to get statistics */

typedef struct _stat
{
  int layer;
  U8 entity;
}Stat;


/* structure to enable or disable alarm */

typedef struct _alarmEnableDisable
{
  U8 Layer;
}AlarmEnableDisable;

#ifdef INC_DLG_AUDIT
/* Audit api  */
typedef struct _auditTcap
{
  U32 tcapSapId;
}AuditTcap;
#endif

/* structure to enable or disable */

typedef struct _debugEnableDisable
{
  U8 layer;
  U8 level;
}DebugEnableDisable;

/* structure to enable or disable trcae */

typedef struct _traceEnableDisable
{
	U8 layer;/* (M) */
	U32 level;/* (O) */
	U32 spNSapId;/* SCCP N SAP */
	U32 snDLSap;/* MTP3 SAP ID */
}TraceEnableDisable;

/* structure for SG enable/disable node commands */
typedef struct _sgNode
{
  U32  procId;  /* proc ID of the node to be enabled*/
  bool lastProc; /* Is this the last proc to be enabled*/
  U8   entId;
}SgNode;


/* When the user provide the add remote ssn command in that case the EMS should fetch the route configuration detail based on the dpc value provided
   by the user and then fill the fetched values in the corresponding place holders of the structure AddRemoteSsn expect those values 
   provided by the user.
   
   And Values which are provided by the user should be filled with in AddRemoteSsn.ssnList[] 
 */
typedef AddRoute AddRemoteSsn;


typedef list<AddLink> LnkList;
typedef list<AddLinkSet> LnkSetList;
typedef list<AddRoute> RouteList;
typedef list<AddUserPart> UserPartList;
typedef list<AddGtRule> GtRuleList;
typedef list<AddAddrMapCfg> AddrMapList;
typedef list<AddLocalSsn> LocalSsnList;
typedef list<AddRemoteSsn> RemoteSsnList;
typedef list<AddNetwork> AddNetworkList;
typedef list<AddEndPoint> EpList;
typedef list<AddPsp> AspList;
typedef list<AddPs> AsList;


/** This shall be used when INGw is comming up
 *  and it fetches all the SS7 and SIGTRAN 
 *  configuration from EMS database 
 */
typedef struct _ss7SigtranInitilaReq {
  /* list's of differnt add command */
  LnkList          lnkList;
  LnkSetList       lnkSetList;
  RouteList        routeList;
  UserPartList     userPartList;
  GtRuleList       gtRuleList;
  AddrMapList      addrMapList;
  LocalSsnList     localSsnList;
  RemoteSsnList    remoteSsnList;
  AddNetworkList   addNetworkList;
  EpList           epList;
} Ss7SigtranInitilaReq;

/** This shall be used when INGw is up and running
 *  and user request some SS7 or SIGTRAN configuration changes 
 */
typedef struct _ss7SigtranSubsReq
{
   /* top level structure for all the commands 
   based on the union type the corresponding 
   sub structured is read */
   U8   union_type;            /* to indentify the union */
   U8   cmd_type;              /* Add/Modify/Delete/Cotroll */
   U16  procId;
   union
   {
     //LnkList lnk;
     AddLink lnk;
     DelLink delLnk;
     LinkStatus lnkstatus;
     LinkEnable lnkEnable;
     LinkDisable lnkDisable;

     //LnkSetList lnkSet;
     AddLinkSet lnkSet;
     DelLinkSet delLnkSet;
     LinkSetStatus lnkStatus;

     //RouteList addRoute;
     AddRoute addRoute;
     DelRoute delRoute;
     RouteStatus dpcStatus;

     NodeStatus nodeStatus;
     //UserPartList addUserPart;
     AddUserPart addUserPart;
     DelUserPart delUserPart;

     //AddNetworkList addNwk; 
     AddNetwork addNwk;
     DelNetwork delNwk;

     //GtRuleList addGtRule;
     AddGtRule addGtRule;
     DelGtRule delGtRule;

     //AddrMapList addaddrMapcfg;
     AddAddrMapCfg addAddrMapCfg;
     DelAddrMapCfg delAddrMapCfg;
       
     //LocalSsnList  addLocalSsn;
     AddLocalSsn addLocalSsn;
     DelLocalSsn  delLocalSsn;
     LocalSsnStatus ssnStatus;
     DisSap       disableSap;
       
     //EpList addep;
     AddEndPoint addEp;
     DelEndPoint delEp;
     DisableEndPoint disableEp; 
     EnableEndPoint enableEp; 
     OpenEp         openEp;

     SsnEnable ssnEnable;
     SsnDisable ssnDisable;

     //RemoteSsnList addRemoteSsn;
     AddRemoteSsn addRemoteSsn;
     DelRemoteSsn delRemoteSsn;

     AddPs addPs;
     DelPs delPs;
     PspStatus psp;

     AddPsp addpsp;
     DelPsp delPsp;
     PsStatus ps;

     DisableUserPart disableUserPart;
     EnableUserPart enableUserPart;
     M3uaAssocUp m3uaAssocUp;
     M3uaAssocDown m3uaAssocDown;
     M3uaAssocAbort m3uaAssocAbort;

     UnbindSap unbindSap;
     BindSap   bindSap;

     M3uaAspUp m3uaAspUp;
     M3uaAspDown m3uaAspDown;
     
	   M3uaAspAct m3uaAspAct;
	   M3uaAspInAct m3uaAspInact;

#ifdef INC_ASP_SNDDAUD
     Daud daud;
#endif


     Stat stat;
     AlarmEnableDisable alarm;
     DebugEnableDisable debug;
     TraceEnableDisable trace;
     SgNode             sgNode;

#ifdef INC_DLG_AUDIT
     AuditTcap          audit;
#endif

     ShutDownLayers     shutDownLayers;
   }u;
} Ss7SigtranSubsReq;



// Response structure for EMS
typedef struct _ss7SigtranStackResp
{
  U16 procId;     /* proc Id */
  U8  status;     /* status returned SUCCESS or FAILURE */
  U16 reason;
  char *reasonStr;     /* Reason if status is FAILURE */
  char *stackLayer;
  
} Ss7SigtranStackResp;

// EMS response list
typedef list<Ss7SigtranStackResp> Ss7SigtranStackRespList;

typedef enum _TxnType {
  GEN_CFG_TXN = 1,
  NORMAL_TXN,
  ROLLBACK_TXN
} TxnType;

typedef enum _TxnStatus {
  INPROGRESS = 1,
  COMPLETED,
  FAILED
} TxnStatus;

typedef enum {
  BP_AIN_SM_CCMOP_OIDCHANGED   = 0,
  BP_AIN_SM_CCMOP_CHANGESTATE  = 1,
  BP_AIN_SM_CCMOP_CONFIGURE    = 2,
  BP_AIN_SM_CCMOP_PEERFAILED   = 3,
  BP_AIN_SM_CCMOP_ENABLE_NODE  = 4
} INGwSmCcmOpType;

typedef struct _stackReqResp {
    U16                  procId;
    U16                  dummyShort;
    int                  dummyInt;
    int                  stackLayer;
    int                  subOpr;
    INGwSmCcmOpType      mOpType;
    int                  miRequestId; // correlates the synch-record used by Wrapper
    U8                   cmdType;
    TxnType              txnType;
    TxnStatus            txnStatus;
    Ss7SigtranSubsReq    req;
    Ss7SigtranStackResp  resp;
}StackReqResp;

//the context used for blocking the thread to make the operation
//synchronous. The return Value is set by the INGwSmRequest.
typedef struct {
  int returnValue;
  int requestId; //cud be the threadId
  Ss7SigtranStackResp	resp;
  int   status; // used for communicating link/linkset status
} INGwSmBlockingContext;

typedef map<int, StackReqResp*> StackReqRespMap;

typedef struct _retryAssoc {
  int cmdType; //1->m3uaAssocUp, 2->m3uaAspUp, 3->m3uaAspAct
  int procId;
  M3uaAssocUp m3uaAssocUp;
  M3uaAspUp m3uaAspUp;
  M3uaAspAct m3uaAspAct;
}INGwSmRetryAssoc; 

typedef struct addNetworkStatus
{
  U16 nwId;
} AddNetworkStatus;

typedef struct addUserPartStatus
{
  U16 nwId;
  S16 mtp3UsapId;
  S16 m3uaUsapId;
  S16 sccpLsapId;
} AddUserPartStatus;

typedef struct addLinkSetStatus
{
  U16 lnkSetId;
} AddLinkSetStatus;

typedef struct addLocalSsnStatus
{
  U8  ssn;
} AddLocalSsnStatus;

typedef struct AddLinkStatus
{
  U16 lnkId;
} AddLinkStatus;

typedef struct addRouteStatus
{
	U32 dpc;                   
  S16 nSapId;                   
} AddRouteStatus;

typedef struct addGtRuleStatus
{
  U16 nwId;
  S16 sw;          
  U8 format;     
  Bool oddEven;   
  U8 natAddr;      
  U8 tType;         
  U8 numPlan;        
  U8 encSch;          
	U8 nmbActns;
} AddGtRuleStatus;

typedef struct addAddrMapCfgStatus
{
   U16 nwkId;
   S16 sw;             
   U8 format;         
   Bool oddEven;     
   U8 natAddr;      
   U8 tType;       
   U8 numPlan;    
   U8 encSch;    
   U8 gtDigLen;
   U8 gtDigits[MAX_GT_DIGITS]; 
} AddAddrMapCfgStatus;

typedef struct addEndPointStatus
{
   U8 	         endPointid;
} AddEndPointStatus;

typedef struct addPspStatus
{
  U32  pspId;
} AddPspStatus;

typedef struct addPsStatus
{
  U32   psId;    
} AddPsStatus;

typedef struct m3uaAssocUpStatus
{
	U32 assocId;
  U32 pspId;
  U16 endPointId;
  S16 m3uaLsapId;
} M3uaAssocUpStatus;

typedef struct oprStatus
{
	int oprType;
} OperationStatus;

typedef struct stkConfigStatus
{
   U16   cmdType;
	 Bool  procId[2];
   union
   {
			OperationStatus     oprSts;
			AddNetworkStatus  	nwSts;
			AddUserPartStatus 	upSts;
			AddLinkSetStatus  	lnkSetSts;
			AddLocalSsnStatus 	ssnSts;
			AddLinkStatus				lnkSts;
			AddRouteStatus			rteSts;
			AddGtRuleStatus	  	ruleSts;
			AddAddrMapCfgStatus addrMapSts;
			AddEndPointStatus   epSts;
			AddPspStatus				pspSts;
			AddPsStatus					psSts;
			M3uaAssocUpStatus		assocUp;
   }u;
} StkConfigStatus;
#endif
