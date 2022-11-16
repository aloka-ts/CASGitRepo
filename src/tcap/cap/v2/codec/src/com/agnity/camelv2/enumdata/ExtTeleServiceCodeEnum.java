package com.agnity.camelv2.enumdata;

/**
 * This enum represents the TeleService Code.
 * @author nkumar
 *
 */
public enum ExtTeleServiceCodeEnum {
	
	/**
	 * 0- allTeleservices
	 * 16- allSpeechTransmissionServices
	 * 17-telephony
	 * 18-emergencyCalls
	 * 32-allShortMessageServices
	 * 33-shortMessageMT-PP
	 * 34-shortMessageMO-PP
	 * 96-allFacsimileTransmissionServices
	 * 97-facsimileGroup3AndAlterSpeech
	 * 98-automaticFacsimileGroup3
	 * 99-facsimileGroup4
	 * 112-allDataTeleservices
	 * 128-allTeleservices-ExeptSMS
	 * 144-allVoiceGroupCallServices
	 * 145-voiceGroupCall
	 * 146-voiceBroadcastCall
	 * 208-allPLMN-specificTS
	 * 209-plmn-specificTS-1
	 * 210-plmn-specificTS-2
	 * 211-plmn-specificTS-3
	 * 212-plmn-specificTS-4
	 * 213-plmn-specificTS-5
	 * 214-plmn-specificTS-6
	 * 215-plmn-specificTS-7
	 * 216-plmn-specificTS-8
	 * 217-plmn-specificTS-9
	 * 218-plmn-specificTS-A
	 * 219-plmn-specificTS-B
	 * 220-plmn-specificTS-C
	 * 221-plmn-specificTS-D
	 * 222-plmn-specificTS-E
	 * 223-plmn-specificTS-F
	 */
	
		ALLTELESERVICES(0), ALLSPEECHTRANSMISSIONSERVICES(16), TELEPHONY(17), EMERGENCYCALLS(18), ALLSHORTMESSAGESERVICES(32),
		
	SHORTMESSAGEMT_PP(33), SHORTMESSAGEMO_PP(34), ALLFACSIMILETRANSMISSIONSERVICES(96), FACSIMILEGROUP3ANDALTERSPEECH(97),
	
	AUTOMATICFACSIMILEGROUP3(98), FACSIMILEGROUP4(99), ALLDATATELESERVICES(112), ALLTELESERVICES_EXEPTSMS(128), ALLVOICEGROUPCALLSERVICES(144),
	
	VOICEGROUPCALL(145), VOICEBROADCASTCALL(146), ALLPLMN_SPECIFICTS(208), PLMN_SPECIFICTS_1(209), PLMN_SPECIFICTS_2(210), 
	
	PLMN_SPECIFICTS_3(211),PLMN_SPECIFICTS_4(212), PLMN_SPECIFICTS_5(213), PLMN_SPECIFICTS_6(214), PLMN_SPECIFICTS_7(215),
	
	PLMN_SPECIFICTS_8(216), PLMN_SPECIFICTS_9(217),PLMN_SPECIFICTS_A(218), PLMN_SPECIFICTS_B(219), PLMN_SPECIFICTS_C(220),
	
	PLMN_SPECIFICTS_D(221), PLMN_SPECIFICTS_E(222), PLMN_SPECIFICTS_F(223) ;
		
		private int code;

		private ExtTeleServiceCodeEnum(int c) {
			code = c;
		}

		public int getCode() {
			return code;
		}
		
		public static ExtTeleServiceCodeEnum fromInt(int num) {
			switch (num) {
				case 0: { return ALLTELESERVICES; }
				case 16: { return ALLSPEECHTRANSMISSIONSERVICES; }
				case 17: { return TELEPHONY; }
				case 18: { return EMERGENCYCALLS; }
				case 32: { return ALLSHORTMESSAGESERVICES; }
				case 33: { return SHORTMESSAGEMT_PP; }
				case 34: { return SHORTMESSAGEMO_PP; }
				case 96: { return ALLFACSIMILETRANSMISSIONSERVICES; }
				case 97: { return FACSIMILEGROUP3ANDALTERSPEECH; }
				case 98: { return AUTOMATICFACSIMILEGROUP3; }
				case 99: { return FACSIMILEGROUP4; }
				case 112: { return ALLDATATELESERVICES; }
				case 128: { return ALLTELESERVICES_EXEPTSMS; }
				case 144: { return ALLVOICEGROUPCALLSERVICES; }
				case 145: { return VOICEGROUPCALL; }
				case 146: { return VOICEBROADCASTCALL; }
				case 208: { return ALLPLMN_SPECIFICTS; }
				case 209: { return PLMN_SPECIFICTS_1; }
				case 210: { return PLMN_SPECIFICTS_2; }
				case 211: { return PLMN_SPECIFICTS_3; }
				case 212: { return PLMN_SPECIFICTS_4; }
				case 213: { return PLMN_SPECIFICTS_5; }
				case 214: { return PLMN_SPECIFICTS_6; }
				case 215: { return PLMN_SPECIFICTS_7; }
				case 216: { return PLMN_SPECIFICTS_8; }
				case 217: { return PLMN_SPECIFICTS_9; }
				case 218: { return PLMN_SPECIFICTS_A; }
				case 219: { return PLMN_SPECIFICTS_B; }
				case 220: { return PLMN_SPECIFICTS_C; }
				case 221: { return PLMN_SPECIFICTS_D; }
				case 222: { return PLMN_SPECIFICTS_E; }
				case 223: { return PLMN_SPECIFICTS_F; }
				default: { return null; }
			}
		}
	
}
