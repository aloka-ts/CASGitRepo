export SIPSTACKDEBUG=1
export OUT_GATEWAY=PSX_PRI_IP
export OUT_PORT=PSX_PRI_PORT
export OUT_GATEWAY2=PSX_SEC_IP
export OUT_PORT2=PSX_SEC_PORT
export sipSTACK_USER_PROFILE=INSTALL_ROOT/INGw/PLATFORM_DIR/conf/up.xml
export SIP_LISTENER_HOST=INGW_FIP
export SIP_LISTENER_PORT=5060
export FROMHDR_TREATMENT=FROMHDR_COPYUSER
export TOHDR_TREATMENT=TOHDR_DEFAULT_TARGET
export SDPTYPE=SDPTYPE_DEFAULT
export INITRESPTIMER=5000
export sipINIT_RESP_TIMER=5000
export TBCT_ON=1
export RTP_TUNNELING=1
export FROMINFO_USERNAME=1230000000
export FROMINFO_ADDR=INGW_FIP
export FROMINFO_PORT=5060
export CONTACTINFO_USERNAME=1230000000
export CONTACTINFO_ADDR=INGW_FIP
export CONTACTINFO_PORT=5060
export SIP_SUPPORT_EARLY_VXML_MEDIA=0
export sipHOLD_SIP_MSG=ENABLED
#----------- PRIVACY RELATED CHANGES --------------------

#valid values are "rfc3323" and "rpid". Default "rpid"
export SIP_PRIVACY_PROTOCOL=rfc3323

#this privacy will be considered (not applied) when Service sets the privacy on. "," separated list
#export SIP_DEFAULT_PRIVACY=id

#this privacy will not be applied by CMM. "," separated list.
#export SIP_PRIVACY_PASSTHROUGH=user

export SIP_PRIVACY_DEFAULT_REQ=id
export SIP_PRIVACY_GW_CAPABILITY=id


#----------- PRIVACY RELATED CHANGES END-----------------


#this will enable short form of headers in sip messages 
#1 only for content len
#2 only shortHdr form
#3 both content len and shortHdr form
#export SIP_HDR_OPT=2
