# Environment variable for CCPU stack integration
export SIP_TRANSPORT_TYPE="UDP"
export INGW_INVOKE_TIMER=0
#LinkConfiguration: High speed / Low speed link
export LINK_TYPE_LSL0_HSL1=0
#Link sequence Normal sequence and extended sequence
export LINK_FRMT_NORM0_EXT1=0
#Type of stack: SS7 / SIGTRAN / HYBRID
export STACK_TYPE_SS70_SIGTRAN1_BOTH2=2

#Stack configuration has to be done through 
#EMS or through XML file. 0: Through XML file, 1: Through EMS
export STACK_CONFIG_TYPE=1
export INGW_FT_DEBUG_FILE="ingwFt.out"

#Configuration File for SS7, SIGTRAN and FT/HA core components
export SM_XML_FILE=$INSTALLROOT/$SUBSYS_DIR/sol28g/conf/INGwSm_CCM1.xml

# Possible values MTP: 0, SIGTRAN: 1 and BOTH: 2
export SM_TRANSPORT_TYPE=2
export SM_CFG_IF_TYPE=3
export PROTO="NTT"

# Possible values of DEF_STK_LOG_MASK are:
# MSG_FLOW 0x00000001|RY 0x00000002|SG 0x00000004|SH 0x00000008|MR 0x00000010| 
# MT 0x00000020|SS 0x00000040|SM 0x00000080|CM 0x00000100|TCAP 0x00000200|SCCP 
# 0x00000400|PSF_TCAP 0x00000800|PSF_SCCP 0x00001000|MTP3 0x00002000|LDF_MTP3
# 0x00004000|PSF_MTP3 0x00008000|MTP2 0x00010000|M3UA 0x00020000|LDF_M3UA 
# 0x00040000|PSF_M3UA 0x00080000|SCTP 0x00100000|TUCL 0x00200000|MEM 0x00400000
#export DEF_STK_LOG_MASK=8388607

export SCCP_SEQ_CNTRL=1 #possible val:0 or 1

# Default value 8 is "Return Message on Error"
export SCCP_RET_OPT=8

# RSN-RSA SSN for JAPAN
#export DEF_RSN_SSN=190

# No of Dialogues used in RSN-RSA SAP
#export NUM_RSN_DLGS=50

# MTP2DlSap configuration parameters Starts
export MTP2_TMR_T1=1500
export MTP2_TMR_T2=500
export MTP2_TMR_T3=300
export MTP2_TMR_T5=20
export MTP2_TMR_T6=500
export MTP2_TMR_T7=200
export MTP2_TMR_T8=0
export MTP2_SDTE=0
export MTP2_SDUE=0
export MTP2_SDDE=0
export MTP2_PROVEMRGCY=300
export MTP2_PROVNORMAL=300
export MTP2_MAXFRMLEN=273
export MTP2_SUERM_THRESH=5
export MTP2_SUERM_ERR_RATE=285
export MTP2_SDTIE=1
export MTP2_SDTIN=1
export MTP2_SDN2=8000
export MTP2_SDCP=5
export MTP2_TMR_TF=24
export MTP2_TMR_TO=24
export MTP2_TMR_TA=24
export MTP2_TMR_TS=24
export MTP2_SDTFLCSTARTTR=200
export MTP2_SDTFLCENDTR=175

# MTP2DlSap configuration parameters Ends
# Used to delete SS7 configuration data done through EMS console.
# In case of any error is deleting data this shall forcefully delete 
# SS7 configuration data from EMS database
#export LOOPBACK=1

# To Enable / Disable INGw TCAP message replication to peer INGw.
# Value 1 and 0 shall disable and enable replication respectively.
export INGW_DISABLE_MSG_FT=0

# Unit is 100s of millis secs.
export INVOKE_TIMER=0


# MAX SIZE for log file rollover
export MAX_STREAM_SIZE=50000000

#DEF_DBG_MASK values: 131071 -> all layers, bits representation for layers (right to left):
#TCAP,PSF_TCAP,SCCP,PSF_SCCP,M3UA,PSF_M3UA,LDF_M3UA,SCTP,TUCL,MTP3,
#PSF_MTP3,LDF_MTP3,MTP2,RELAY,SG,MR,SH
#RESET:0, ALL:131071, SH+MR+SG+RELAY: 122880
#export DEF_DBG_MASK=131071
#U-ABORT User Information octet stream NTT standard
export NTT_UABORT_USR_INFO="280f06080283386603020600a0030a0100"
#Time since no SIP message has been received from SAS, 
#used for connectivity failure detection, because ideally
#NOTIFY or OPTIONS should keep on flowing if both INC and SAS are up.
export INGW_NW_ALIVE_TIME=30

#define this to parameter buffer string of RC
export NTT_RC_PARAM_BUF="040283A9"

#define this to alter abort cause (default 0x01)
export NTT_ABRT_CAUSE=1

#define as 1 to generate all ABORT
#define as 2 to generate ABORT/END-RC in error scenarios (based on transaction state)
export NTT_DLG_CLOSE_OPT=2
