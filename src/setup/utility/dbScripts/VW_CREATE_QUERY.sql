CREATE VIEW CDR_DATA_VW AS (
SELECT 
SWITCH_CODE || ',' || STATUS_IND_ATTMPTD_CALL || ',' || STATUS_IND_FAILED_CALL || ',' || STATUS_IND_INTRMDT_CALL || ',' || 
STATUS_IND_NON_CHRGD_CALL || ',' || CHARGE_TABLE_TYPE || ',' || CALLING_ID_MA || ',' || CALLED_ID || ',' || 
START_TIME  || ',' || ANSWER_TIME || ',' || DISCONNECT_TIME || ',' || TERM_INTERWORKING_IND || ',' || 
ORIG_INTERWORKING_IND || ',' || TERM_ISUP1_LINK_IND || ',' || ORIG_ISUP1_LINK_IND || ',' || TERM_ISDN_ACCESS_IND || ',' || 
ORIG_ISDN_ACCESS_IND || ',' || CHARGE_INDICATOR || ',' || CALLING_PARTY_CATEGORY || ',' || 
CHARGE_ID_OPTION || ',' || RECEIVED_INFORMATION_OPTION || ',' || SERVICE_INDICATOR || ',' || ATTEMPTED_OPTION || ',' ||
CALLING_ID_OPTION || ',' || SIGNALING_NW_USAGE_OPTION || ',' || VPN_OPTION || ',' || ORIG_TERM_CA_OPTION || ',' ||
FW_DIR_CIT_INFORMATION || ',' || BW_DIR_CIT_INFORMATION || ',' || UAN_OPTION || ',' || ACPC AS CDR,  
DISCONNECT_TIME, FILENAME, IS_SFTPED, CREATED_ON, SENT_FILENAME  
FROM CDR_DATA_TBL);
