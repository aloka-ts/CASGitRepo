package com.agnity.map.enumdata;

public enum TeleServiceCodeMapEnum {
	ALLTELESERVICES(0),

	ALLSPEECHTRANSMISSIONSERVICES(16),
	TELEPHONY(17),
	EMERGENCYCALLS(18),

	ALLSHORTMESSAGESERVICES (32),
	SHORTMESSAGEMT_PP(33),
	SHORTMESSAGEMO_PP(34),

	ALLFACSIMILETRANSMISSIONSERVICES(96),
	FACSIMILEGROUP3ANDALTERSPEECH(97),
	AUTOMATICFACSIMILEGROUP3(98),
	FACSIMILEGROUP4(99),

	ALLDATATELESERVICES     (112),
	ALLTELESERVICES_EXEPTSMS        (128),
	ALLVOICEGROUPCALLSERVICES       (144),
	VOICEGROUPCALL(145),
	VOICEBROADCASTCALL(146),

	ALLPLMN_SPECIFICTS(208),
	PLMN_SPECIFICTS_1(209),
	PLMN_SPECIFICTS_2(210),
	PLMN_SPECIFICTS_3(211),
	PLMN_SPECIFICTS_4(212),
	PLMN_SPECIFICTS_5(213),
	PLMN_SPECIFICTS_6(214),
	PLMN_SPECIFICTS_7(215),
	PLMN_SPECIFICTS_8(216),
	PLMN_SPECIFICTS_9(217),
	PLMN_SPECIFICTS_A(218),
	PLMN_SPECIFICTS_B(219),
	PLMN_SPECIFICTS_C(220),
	PLMN_SPECIFICTS_D(221),
	PLMN_SPECIFICTS_E(222),
	PLMN_SPECIFICTS_F(223);
	
	private int code;
	private TeleServiceCodeMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static TeleServiceCodeMapEnum getValue(int tag) {
		switch (tag) {
		case 0:
			return ALLTELESERVICES;
		case 16:
			return ALLSPEECHTRANSMISSIONSERVICES;
		case 17:
			return TELEPHONY;
		case 18:
			return EMERGENCYCALLS;

		case 32:
			return ALLSHORTMESSAGESERVICES;
		case 33:
			return SHORTMESSAGEMT_PP;
		case 34:
			return SHORTMESSAGEMO_PP;

		case 96:
			return ALLFACSIMILETRANSMISSIONSERVICES;
		case 97:
			return FACSIMILEGROUP3ANDALTERSPEECH;
		case 98:
			return AUTOMATICFACSIMILEGROUP3;
		case 99:
			return FACSIMILEGROUP4;

		case 112:
			return ALLDATATELESERVICES;
		case 128:
			return ALLTELESERVICES_EXEPTSMS;
		case 144:
			return ALLVOICEGROUPCALLSERVICES;
		case 145:
			return VOICEGROUPCALL;
		case 146:
			return VOICEBROADCASTCALL;

		case 208:
			return ALLPLMN_SPECIFICTS;
		case 209:
			return PLMN_SPECIFICTS_1;
		case 210:
			return PLMN_SPECIFICTS_2;
		case 211:
			return PLMN_SPECIFICTS_3;
		case 212:
			return PLMN_SPECIFICTS_4;
		case 213:
			return PLMN_SPECIFICTS_5;
		case 214:
			return PLMN_SPECIFICTS_6;
		case 215:
			return PLMN_SPECIFICTS_7;
		case 216:
			return PLMN_SPECIFICTS_8;
		case 217:
			return PLMN_SPECIFICTS_9;
		case 218:
			return PLMN_SPECIFICTS_A;
		case 219:
			return PLMN_SPECIFICTS_B;
		case 220:
			return PLMN_SPECIFICTS_C;
		case 221:
			return PLMN_SPECIFICTS_D;
		case 222:
			return PLMN_SPECIFICTS_E;
		case 223:
			return PLMN_SPECIFICTS_F;
		default:
			return null;
		}
	}
}
