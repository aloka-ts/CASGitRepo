/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/

package com.agnity.map.enumdata;

public enum SupplementaryServicesMapEnum {
    /*
     This type is used to represent the code identifying a single
     supplementary service, a group of supplementary services, or
     all supplementary services. The services and abbreviations
     used are defined in TS 3GPP TS 22.004 [5]. The internal structure is
     defined as follows:
     bits 87654321: 
     group (bits 8765), and 
     specific service (bits 4321)
    
     '00000000'B - allSS (reserved for possible future use, all SS)
     '00010000'B - allLineIdentificationSS (rerved for possible future use, all line identification SS)
     '00010001'B - clip (calling line identification presentation)
     '00010010'B - clir (calling line identification restriction)
     '00010011'B - colp (connected line identification presentation)
     '00010100'B - colr (connected line identification restriction)
     '00010101'B - mci (reserved for possible future use, malicious call identification)
     '00011000'B - allNameIdentificationSS (all name identification SS)
     '00011001'B - cnap (calling name presentation)

     SS-Codes '00011010'B to '00011111'B are reserved for future
     NameIdentification Supplementary Service use.

     '00100000'B - allForwardingSS (all forwarding SS)
     '00100001'B - cfu (call forwarding unconditional)     
     '00101000'B - allCondForwardingSS (all conditional forwarding SS)
     '00101001'B - cfb (call forwarding on mobile subscriber busy)
     '00101010'B - cfnry (call forwarding on no reply)
     '00101011'B - cfnrc (call forwarding on mobile subscriber not reachable)
     '00100100'B - cd (call deflection)

     '00110000'B - allCallOfferingSS (reserved for possible future use, all call offering SS includes also all forwarding SS)
     '00110001'B - ect (explicit call transfer)
     '00110010'B - mah (reserved for possible future use, mobile access hunting)

     '01000000'B - allCallCompletionSS (reserved for possible future use, all Call completion SS)
     '01000001'B - cw (call waiting)
     '01000010'B - hold (call hold) 
     '01000011'B - ccbs-A 
                   completion of call to busy subscribers, originating side
                   this SS-Code is used only in InsertSubscriberData, DeleteSubscriberData
                   and InterrogateSS

     '01000100'B - ccbs-B 
                   completion of call to busy subscribers, destination side
                   this SS-Code is used only in InsertSubscriberData and DeleteSubscriberData

     '01000101'B - mc (multicall)


      '01010000'B - allMultiPartySS (reserved for possible future use, all multiparty SS)
      '01010001'B - multiPTY (multiparty)

      '01100000'B - allCommunityOfInterest-SS (reserved for possible future use, all community of interest SS )
      '01100001'B - cug (closed user group)
     

      '01110000'B - allChargingSS (reserved for possible future use, all charging SS)
      '01110001'B - aoci (advice of charge information)
      '01110010'B - aocc (advice of charge charging)
    

      '10000000'B - allAdditionalInfoTransferSS(reserved for possible future use, all additional information transfer SS)
      '10000001'B - uus1 (UUS1 user-to-user signalling)
      '10000010'B - uus2 (UUS2 user-to-user signalling)
      '10000011'B - uus3 (UUS3 user-to-user signalling)


      '10010000'B - allBarringSS (all barring SS)
      '10010001'B - barringOfOutgoingCalls (barring of outgoing calls)
      '10010010'B - baoc (barring of all outgoing calls)
      '10010011'B - boic (barring of outgoing international calls)
      '10010100'B - boicExHC 
                    barring of outgoing international calls except those directed
                    to the home PLMN Country
      '10011001'B - barringOfIncomingCalls (barring of incoming calls)
      '10011010'B - baic (barring of all incoming calls)
      '10011011'B - bicRoam
                    barring of incoming calls when roaming outside home PLMN
                    Country

      '11110000'B - allPLMN-specificSS
      '11110001'B - plmn-specificSS-1
      '11110010'B - plmn-specificSS-2
      '11110011'B - plmn-specificSS-3
      '11110100'B - plmn-specificSS-4
      '11110101'B - plmn-specificSS-5
      '11110110'B - plmn-specificSS-6
      '11110111'B - plmn-specificSS-7
      '11111000'B - plmn-specificSS-8
      '11111001'B - plmn-specificSS-9
      '11111010'B - plmn-specificSS-A
      '11111011'B - plmn-specificSS-B
      '11111100'B - plmn-specificSS-C
      '11111101'B - plmn-specificSS-D
      '11111110'B - plmn-specificSS-E
      '11111111'B - plmn-specificSS-F


      '10100000'B - allCallPrioritySS (reserved for possible future use, all call priority SS)
      '10100001'B - emlpp (enhanced Multilevel Precedence Pre-emption (EMLPP) service)
 
      '10110000'B - allLCSPrivacyException (all LCS Privacy Exception Classes)
      '10110001'B - universal (allow location by any LCS client)
      '10110010'B - callSessionRelated
                    allow location by any value added LCS client to which a call/session
                    is established from the target MS
      '10110011'B - callSessionUnrelated
                    allow location by designated external value added LCS clients

      '10110100'B - plmnoperator (allow location by designated PLMN operator LCS clients)
      '10110101'B - serviceType (allow location by LCS clients of a designated LCS service type)
  
      '11000000'B - allMOLR-SS (all Mobile Originating Location Request Classes)
      '11000001'B - basicSelfLocation (allow an MS to request its own location)
      '11000010'B - autonomousSelfLocation
                    allow an MS to perform self location without interaction
                    with the PLMN for a predetermined period of time
      '11000011'B - transferToThirdParty
                    allow an MS to request transfer of its location to another LCS client

    */

    ALL_SS(0), ALL_LINE_IDENTIFICATION_SS(16), CLIP_SS(17), CLIR_SS(18), COLP_SS(19), COLR_SS(20), 
    MCI_SS(21), ALL_NAME_IDENTIFICATION_SS(24), CNAP_SS(25),
    ALL_FORWARDING_SS(32), CFU_SS(33), ALL_COND_FORWARDING_SS(40), CFB_SS(41), CFNRY_SS(42), CFNRC_SS(43), CD_SS(36),
    ALL_CALL_OFFERING_SS(48), ECT_SS(49), MAH_SS(50),
    ALL_CALL_COMPLETION_SS(64), CW_SS(65), HOLD_SS(66), CCBS_A_SS(67), CCBS_B_SS(68), MC_SS(69),
    ALL_MULTIPARTY_SS(80), MULTIPTY_SS (81), 
    ALL_COMMUNITY_OF_INTEREST_SS (96), CUG_SS(97),
    ALL_CHARGING_SS(112), AOCI_SS(113), AOCC_SS(114),
    ALL_ADDITIONAL_INFO_TRANSFER_SS(128), UUS1_SS(129), UUS2_SS(130), UUS3_SS(131),
    ALL_BARRING_SS(144), BARRING_OF_OUTGOING_CALLS_SS(145), BAOC_SS(146), BOIC_SS(147), BOICEXHC_SS(148), 
    BARRING_OF_INCOMING_CALLS(153), BAIC_SS(154), BICROAM_SS(155),
    ALL_PLMN_SPECIFIC_SS(240), PLMN_SPECIFIC_SS_1(241), PLMN_SPECIFIC_SS_2(242), PLMN_SPECIFIC_SS_3(243),
    PLMN_SPECIFIC_SS_4(244), PLMN_SPECIFIC_SS_5(245), PLMN_SPECIFIC_SS_6(246), PLMN_SPECIFIC_SS_7(247),
    PLMN_SPECIFIC_SS_8(248), PLMN_SPECIFIC_SS_9(249), PLMN_SPECIFIC_SS_A(250), PLMN_SPECIFIC_SS_B(251),
    PLMN_SPECIFIC_SS_C(252), PLMN_SPECIFIC_SS_D(253), PLMN_SPECIFIC_SS_E(254), PLMN_SPECIFIC_SS_F(255),
    ALL_CALL_PRIORITY_SS(160), EMLPP_SS(161), ERRICSON_CUSTOM1(162),
    ALL_LCS_PRIVACY_EXCEPTION(176), UNIVERSAL_SS(177), CALL_SESSION_RELATED_SS(178), CALL_SESSION_UNRELATED_SS(179),
    PLMNOPERATOR_SS(180), SERVICE_TYPE_SS(181), 
    ALL_MOLR_SS(192), BASIC_SELF_LOCATION_SS(193), AUTONOMOUS_SELF_LOCATION_SS(194), TRANSFER_TO_THIRDPARTY_SS(195);

   
    private int code;
  
    private SupplementaryServicesMapEnum(int code) {
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }

    public static SupplementaryServicesMapEnum getValue(int tag) {
        switch (tag) {
            case 0  : return ALL_SS;
            case 16 : return ALL_LINE_IDENTIFICATION_SS;
            case 17 : return CLIP_SS;
            case 18 : return CLIR_SS;
            case 19 : return COLP_SS;
            case 20 : return COLR_SS;
            case 21 : return MCI_SS;
            case 24 : return ALL_NAME_IDENTIFICATION_SS;
            case 25 : return CNAP_SS;
            case 32 : return ALL_FORWARDING_SS;
            case 33 : return CFU_SS;
            case 40 : return ALL_COND_FORWARDING_SS;
            case 41 : return CFB_SS;
            case 42 : return CFNRY_SS;
            case 43 : return CFNRC_SS;
            case 36 : return CD_SS;
            case 48 : return ALL_CALL_OFFERING_SS;
            case 49 : return ECT_SS;
            case 50 : return MAH_SS;
            case 64 : return ALL_CALL_COMPLETION_SS;
            case 65 : return CW_SS;
            case 66 : return HOLD_SS;
            case 67 : return CCBS_A_SS;
            case 68 : return CCBS_B_SS;
            case 69 : return MC_SS;
            case 80 : return ALL_MULTIPARTY_SS;
            case 81 : return MULTIPTY_SS;
            case 96 : return ALL_COMMUNITY_OF_INTEREST_SS;
            case 97 : return CUG_SS;
            case 112 : return ALL_CHARGING_SS;
            case 113 : return AOCI_SS;
            case 114 : return AOCC_SS;
            case 128 : return ALL_ADDITIONAL_INFO_TRANSFER_SS;
            case 129 : return UUS1_SS;
            case 130 : return UUS2_SS;
            case 131 : return UUS3_SS;
            case 144 : return ALL_BARRING_SS;
            case 145 : return BARRING_OF_OUTGOING_CALLS_SS;
            case 146 : return BAOC_SS;
            case 147 : return BOIC_SS;
            case 148 : return BOICEXHC_SS;
            case 153 : return BARRING_OF_INCOMING_CALLS;
            case 154 : return BAIC_SS;
            case 155 : return BICROAM_SS;
            case 240 : return ALL_PLMN_SPECIFIC_SS;
            case 241 : return PLMN_SPECIFIC_SS_1;
            case 242 : return PLMN_SPECIFIC_SS_2;
            case 243 : return PLMN_SPECIFIC_SS_3;
            case 244 : return PLMN_SPECIFIC_SS_4;
            case 245 : return PLMN_SPECIFIC_SS_5;
            case 246 : return PLMN_SPECIFIC_SS_6;
            case 247 : return PLMN_SPECIFIC_SS_7;
            case 248 : return PLMN_SPECIFIC_SS_8;
            case 249 : return PLMN_SPECIFIC_SS_9;
            case 250 : return PLMN_SPECIFIC_SS_A;
            case 251 : return PLMN_SPECIFIC_SS_B;
            case 252 : return PLMN_SPECIFIC_SS_C;
            case 253 : return PLMN_SPECIFIC_SS_D;
            case 254 : return PLMN_SPECIFIC_SS_E;
            case 255 : return PLMN_SPECIFIC_SS_F;
            case 160 : return ALL_CALL_PRIORITY_SS;
            case 161 : return EMLPP_SS;
            case 176 : return ALL_LCS_PRIVACY_EXCEPTION;
            case 177 : return UNIVERSAL_SS;
            case 178 : return CALL_SESSION_RELATED_SS;
            case 179 : return CALL_SESSION_UNRELATED_SS;
            case 180 : return PLMNOPERATOR_SS;
            case 181 : return SERVICE_TYPE_SS;
            case 192 : return ALL_MOLR_SS;
            case 193 : return BASIC_SELF_LOCATION_SS;
            case 194 : return AUTONOMOUS_SELF_LOCATION_SS;
            case 195 : return TRANSFER_TO_THIRDPARTY_SS;
	    case 162 : return ERRICSON_CUSTOM1;
            default  : return null;
        }
    }
}
