#ifndef _INCCommons_
#define _INCCommons_

#include "INGwTcapProvider/INGwTcapInclude.h"
#include "INGwTcapProvider/INGwSil.h"

typedef unsigned char U8;
typedef char S8;
typedef unsigned short U16;
typedef unsigned long U32;
typedef short S16;

#define INC_ENC_TYP_INT         1
#define INC_ENC_TYP_OID         2

#define SS7                     0
#define SIGTRAN                 1
#define SS7_SIGTRAN             2

#define ENT_INC_SS7             1
#define ENT_INC_M3UA            2
#define ENT_INC_SCTP_TUCL       3
#define ENT_INC_SCCP_TCAP       4
#define ENT_INC_MTP3            5
#define ENT_INC_MTP2_MTP3       6
#define ENT_INC_M3UA_SCTP_TUCL  7


#define MAX_INC_STRING 256
 
  typedef struct appInstId{
    U8 appId;
    U8 instId;
		S16	suId;
		S16 spId;
  }AppInstId;

  typedef struct _PcSsnStatusData {
     U8   ssn;
     U8   ustat;
     U8   sps;
     U8   smi;
     unsigned int    dpc;
     char            state;
  } PcSsnStatusData;

typedef enum {
  TCAP_UNKNOWN =0,
  TCAP_NOT_REGISTERED,
  TCAP_REGSITER_INPGRS,
  TCAP_REGISTERED,
  TCAP_DEREGISTERED,
	TCAP_REGISTER_PENDING
} g_tcapSsnState;

typedef struct t_tcapRegInfo
{
  S16 m_suId;
  int m_regType;
  int m_index;
}TcapRegInfo;

typedef struct t_ssnInfo
{
  U8              ssn;
  short           suId;
  short           spId;
  g_tcapSsnState  regState;

  bool            pcStateValid;
  PcSsnStatusData pcState;

  bool            ssnStateValid;
  PcSsnStatusData ssnState;

  t_ssnInfo()
  {
    ssn  = 0;
    suId = -1;
    spId = -1;
    regState = TCAP_UNKNOWN;
    ssnStateValid = pcStateValid = false;
    memset(&ssnState, 0, sizeof(PcSsnStatusData));
    memset(&pcState, 0, sizeof(PcSsnStatusData));
  }
}SsnInfo;

  typedef struct _appIdInfo {
    U8   appId;
    U32             pc;              /* Point Code in decimal format */
  	U8		m_pcDetail[3];   /* PC in Zone-Cluster-Member format */
    U8              ssn;             /* Subsystem Number */
    U8              tcapProtoVar;    /* Tcap Protocol variant */
    U8              sccpProtoVar;    /* Sccp Protocol variant */
    g_tcapSsnState	m_isRegistered;
    _appIdInfo() 
    {
      appId = 255;
      pc    = 0;
      memset(m_pcDetail,0,3);
      ssn = 0;
      tcapProtoVar = 0;
      sccpProtoVar = 0;
      m_isRegistered = TCAP_NOT_REGISTERED;
    }
  } AppIdInfo;

  typedef struct t_tcapAppIdInfo
  {
  	U8		m_appId;
    U8   m_instId;//INCTBD
  	U8		m_ssn;
  	U32		          m_pc;
  	U8		m_pcDetail[3];

  	g_tcapSsnState	m_isRegistered;
  
  	bool					  m_pcStateValid;
  	PcSsnStatusData		  m_pcState;
  
  	bool					  m_ssnStateValid;
  	PcSsnStatusData		  m_ssnState;
  
  	t_tcapAppIdInfo(){
  		m_appId				= 0;
  		m_instId			= 0;
  		m_ssn					= 0;
  		m_pc					= 0;
  		m_isRegistered= TCAP_NOT_REGISTERED;
  		m_pcStateValid = false;
  		m_ssnStateValid= false;
  
  		memset(&m_pcState,  0, sizeof(PcSsnStatusData));
  		memset(&m_ssnState, 0, sizeof(PcSsnStatusData));
  	};
  } TcapAppIfInfo;

/*
  typedef enum _PROTO_TYPE {
       ANSI=1, 
       ITU, 
       JAPAN_TTC, 
       JAPAN_NTT, 
       CHINA
       } PROTO_TYPE;
*/


  // This structure shall be used to determine whether
  // SS7/SIGTRAN configuration 
  // (during startup/new request from user)requests 
  // shall be send to peer INC.
  typedef struct _procStatus {
    // State of Peer INC notified by EMS
    int   stateFromEms;
    // Stack Relay connectivity to the peer stack through Private IP
    int   stateFromRelay;
    // INC detect the connectivity with peer INC
    int   stateFromInc;

    _procStatus () {
      stateFromEms   = 0; // Down
      stateFromRelay = 0; // Disconnected
      stateFromInc   = 0; // Disconnected
    };
  } ProcStatus;
  
  typedef struct tcapQosSet
  {
     U8     msgPrior;
     U8        retOpt;
     U8        seqCtl;
  /* stu_x_001.main_30 REUSE_SLS feature addition */
  #ifdef SS7_REUSE_SLS
     S16        lnkSel;
  #endif
  }  TcapQosSet;
  
  struct INcOctet {
  bool	pres;
  char		octet;
        INcOctet(){
        this->pres = false;
        }
  };
  
struct INcUOctet{
    bool	pres;
    U8	octet;
     INcUOctet(){
      pres = false;
    }
  };
  
  struct INcStr{
    U16 len;
    U8	string[MAX_INC_STRING];

    INcStr(){
       this->len = 0;
    }
  };
  
  typedef struct ptrString{
    U8  len; 
    U8  *string;
    ptrString(){

      this->len = 0;
      this->string = 0;
    }
  }PtrStr;
  
struct stringBuff{
    unsigned short  len; 
    U8  *string;

    stringBuff(){
      this->len = 0;
      this->string = 0;
    }
  };
            
  typedef struct shortAddr
  {
     U8 length;	
     U8 strg[32];

     shortAddr(){
       this->length = 0;
       memset(strg, 0, sizeof(strg));
     }
  } ShortAddr;
      
  typedef struct incGlbTi 
  {
     U8 format;               /* format */
     union
     {
        struct 
        {
         	// odd/even indicator, used in address parameter of sccp 
           bool oddEven;
          // nature of address, used in address parameter of sccp message 
           U8 natAddr;        
        }f1;

        struct 
        {
           U8 tType;          /* translation type */
        }f2;
  
        struct 
        {
           U8 tType;          /* translation type */
           U8 numPlan;        /* numbering plan */
           U8 encSch;         /* encoding scheme */
        }f3; 
  
        struct 
        {
           U8 tType;          /* translation type */
           U8 numPlan;        /* numbering plan */
           U8 encSch;         /* encoding scheme */
           U8 natAddr;        /* nature of address*/
        }f4; 
  
        struct
        {
           U32 ipAddr;        /* IP address used by TCAP 
                                 over TCP/IP */
        }f5; 
  
     }gt;
  		
  	ShortAddr addr;	          /*address digits*/


  } INcGlbTi;

typedef SpAddr  SccpAddr;

typedef struct TcapDlgEvent
{
  U8       pres;
  U8       dlgType;
  U8       acnType;
  int      intAcn;
  int      intSec;    
  bool     resultPres;
  U8       result;
  U8       reason;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
  stAnsiDlgEv ansiDlgEv;
#endif

  INcStr       objAcn;

  stringBuff   uInfo;
  stringBuff   dlgPortnUInfo;
  
  //this flag specifies basic/prearranged end
  bool         endFlag;

  U32          opc;
  U32          dpc;
  U32          sudlgId;
  U32          spdlgId;

  SccpAddr     *dstAddr;

  SccpAddr     *srcAddr;

  //Cause for abort and notice
  U8           cause;
  int          miBillingNo;
  TcapQosSet   qosSet;

   ~TcapDlgEvent()
   {
   }

}TcapDlg;

  
typedef struct TcapCompInfo
{
  U8            compType;    
  U8            opTag;       
  U8            errTag;      
  U8            probTag;     
  U8            paramTag;
  INcOctet      invIdItu;   
  INcOctet      linkedId;
  INcUOctet     invIdAnsi;
  INcUOctet     corrId;
  
  U8            opClass;               
  U16           invokeTimer;
  bool          lastComp;
  bool          cancelFlg; 
  bool          update;      //update peer
  INcStr        opCode;     
  INcStr        errCode;     
  
  INcStr        probCode;              
  PtrStr        param;   
  //this field is to identify different reject resources
  short int     status;
  ~TcapCompInfo()
   {
   }

}TcapComp;

 

  //forward declaration
  class TcapMessage;
            
  
  typedef struct INCSS7SteMgmt
  {
     U8      evntType;
     union
     {
       struct
       {
         U8     aSsn;       /* affected sub system number */ 
       }cordReq;
       struct
       {
         U8     aSsn;       /* affected sub system number */
         U8     smi;        /* Subsystem multiplicity Indicator */
       }cordInd;
       struct
       {
         U8     aSsn;       /* affected sub system number */ 
       }cordRsp;
       struct
       {
         U8     aSsn;       /* affected sub system number */
         U8     smi;        /* Subsystem multiplicity Indicator */ 
       }cordCfm;
       struct  {
         U8    aSsn;        /* affected sub system number */
         U8  uStat;         /* Status of ssn */
       }steReq;
       struct
       {
         unsigned int    aDpc; /* Affected point code */
         U8    aSsn;           /* affected sub system number */
         U8  uStat;       /* Status of ssn */
         U8    smi;       /* Subsystem multiplicity Indicator */
       }steInd;
       struct
       {
         unsigned int    dpc;  /* destination point code */
         U8    sps;            /* Point Code Status */
       }PCSteInd;
       #ifdef STU2
       struct
       {
         U8     status;        /* Status of the SteReq */
         unsigned int    dpc;  /* destination point code */
         U8    ssn;            /* Subsystem number */
       }staReq;
       struct
       {
         U8     status;      /* Status of the SteReq */
       }steCfm;
       struct
       {
         U8     status;    /* Type of Status requested */
         U8    ssn;        /* Subsystem number */
         unsigned int dpc; /* destination point code */
         U8  ustat;        /* Status of point code/subsystem */
         U8    smi;        /* Subsystem multiplicity indicator */
       }staCfm;
  #endif /* STU2 */
     }mgmt;
  }INCSS7SteMgmt;

#endif


