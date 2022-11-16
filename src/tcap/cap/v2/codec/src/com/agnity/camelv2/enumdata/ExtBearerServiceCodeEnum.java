package com.agnity.camelv2.enumdata;

/**
 * This enum represents the Bearer Service Code.
 * @author nkumar
 *
 */
public enum ExtBearerServiceCodeEnum {

	/**
	 * 0- allBearerServices
	 * 
	 * 16- allDataCDA-Services
	 * 17-dataCDA-300bps
	 * 18-dataCDA-1200bps
	 * 19-dataCDA-1200-75bps
	 * 20-dataCDA-2400bps
	 * 21-dataCDA-4800bps
	 * 22-dataCDA-9600bps
	 * 23-general-dataCDA
	 * 
	 * 24-allDataCDS-Services
	 * 26-dataCDS-1200bps
	 * 28-dataCDS-2400bps
	 * 29-dataCDS-4800bps
	 * 30-dataCDS-9600bps
	 * 31-general-dataCDS
	 * 
	 * 32-allPadAccessCA-Services
	 * 33-padAccessCA-300bps
	 * 34-padAccessCA-1200bps
	 * 35-padAccessCA-1200-75bps
	 * 36-padAccessCA-2400bps
	 * 37-padAccessCA-4800bps
	 * 38-padAccessCA-9600bps
	 * 39-general-padAccessCA
	 * 
	 * 40-allDataPDS-Services
	 * 44-dataPDS-2400bps
	 * 45-dataPDS-4800bps
	 * 46-dataPDS-9600bps
	 * 47-general-dataPDS	
	 * 
	 * 48-allAlternateSpeech-DataCDA
	 * 56-allAlternateSpeech-DataCDS
	 * 64-allSpeechFollowedByDataCDA
	 * 72-allSpeechFollowedByDataCDS
	 * 80-allDataCircuitAsynchronous
	 * 88-allDataCircuitSynchronous
	 * 96-allAsynchronousServices
	 * 104-allSynchronousServices
	 * 
	 * 208-allPLMN-specificBS
	 * 209-plmn-specificBS-1
	 * 210-plmn-specificBS-2
	 * 211-plmn-specificBS-3
	 * 212-plmn-specificBS-4
	 * 213-plmn-specificBS-5
	 * 214-plmn-specificBS-6
	 * 215-plmn-specificBS-7
	 * 216-plmn-specificBS-8
	 * 217-plmn-specificBS-9
	 * 218-plmn-specificBS-A
	 * 219-plmn-specificBS-B
	 * 220-plmn-specificBS-C
	 * 221-plmn-specificBS-D
	 * 222-plmn-specificBS-E
	 * 223-plmn-specificBS-F
	 */
	
	ALLBEARERSERVICES(0),ALLDATACDA_SERVICES(16),DATACDA_300BPS	(17),DATACDA_1200(18),DATACDA_1200_75BPS(19),
	
	DATACDA_2400BPS	(20),DATACDA_4800(21),DATACDA_9600(22),GENERAL_DATACDA(23),ALLDATACDS_SERVICES(24),DATACDS_1200BPS(26),
	
	DATACDS_2400BPS(28),DATACDS_4800BPS(29),DATACDS_9600BPS(30),GENERAL_DATACDS(31),ALLPADACCESSCA_SERVICES(32),PADACCESSCA_300BPS(33),
	
	PADACCESSCA_1200BPS(34),PADACCESSCA_1200_75BPS(35),PADACCESSCA_2400BPS(36),PADACCESSCA_4800BPS(37),PADACCESSCA_9600BPS(38),
	
	GENERAL_PADACCESSCA(39),ALLDATAPDS_SERVICES(40),DATAPDS_2400BPS(44),DATAPDS_4800BPS(45),DATAPDS_9600BPS(46),GENERAL_DATAPDS(47),
	
	ALLALTERNATESPEECH_DATACDA(48),ALLALTERNATESPEECH_DATACDS(56),ALLSPEECHFOLLOWEDBYDATACDA(64),ALLSPEECHFOLLOWEDBYDATACDS(72),
	
	ALLDATACIRCUITASYNCHRONOUS(80),ALLDATACIRCUITSYNCHRONOUS(88),ALLASYNCHRONOUSSERVICES(96),ALLSYNCHRONOUSSERVICES(104),
	
	ALLPLMN_SPECIFICBS(208),PLMN_SPECIFICBS_1(209),PLMN_SPECIFICBS_2(210),PLMN_SPECIFICBS_3(211),PLMN_SPECIFICBS_4(212),PLMN_SPECIFICBS_5(213),
	
	PLMN_SPECIFICBS_6(214),PLMN_SPECIFICBS_7(215),PLMN_SPECIFICBS_8(216),PLMN_SPECIFICBS_9(217),PLMN_SPECIFICBS_A(218),
	
	PLMN_SPECIFICBS_B(219),PLMN_SPECIFICBS_C(220),PLMN_SPECIFICBS_D(221),PLMN_SPECIFICBS_E(222),PLMN_SPECIFICBS_F(223);
	
	private int code;

	private ExtBearerServiceCodeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ExtBearerServiceCodeEnum fromInt(int num) {
		switch (num) {
			case 0: { return ALLBEARERSERVICES; }
			case 16: { return ALLDATACDA_SERVICES; }
			case 17: { return DATACDA_300BPS; }
			case 18: { return DATACDA_1200; }
			case 19: { return DATACDA_1200_75BPS; }
			case 20: { return DATACDA_2400BPS; }
			case 21: { return DATACDA_4800; }
			case 22: { return DATACDA_9600; }
			case 23: { return GENERAL_DATACDA; }
			case 24: { return ALLDATACDS_SERVICES; }
			case 26: { return DATACDS_1200BPS; }
			case 28: { return DATACDS_2400BPS; }
			case 29: { return DATACDS_4800BPS; }
			case 30: { return DATACDS_9600BPS; }
			case 31: { return GENERAL_DATACDS; }
			case 32: { return ALLPADACCESSCA_SERVICES; }
			case 33: { return PADACCESSCA_300BPS; }
			case 34: { return PADACCESSCA_1200BPS; }
			case 35: { return PADACCESSCA_1200_75BPS; }
			case 36: { return PADACCESSCA_2400BPS; }
			case 37: { return PADACCESSCA_4800BPS; }
			case 38: { return PADACCESSCA_9600BPS; }
			case 39: { return GENERAL_PADACCESSCA; }
			case 40: { return ALLDATAPDS_SERVICES; }
			case 44: { return DATAPDS_2400BPS; }
			case 45: { return DATAPDS_4800BPS; }
			case 46: { return DATAPDS_9600BPS; }
			case 47: { return GENERAL_DATAPDS; }
			case 48: { return ALLALTERNATESPEECH_DATACDA; }
			case 56: { return ALLALTERNATESPEECH_DATACDS; }
			case 64: { return ALLSPEECHFOLLOWEDBYDATACDA; }
			case 72: { return ALLSPEECHFOLLOWEDBYDATACDS; }
			case 80: { return ALLDATACIRCUITASYNCHRONOUS; }
			case 88: { return ALLDATACIRCUITSYNCHRONOUS; }
			case 96: { return ALLASYNCHRONOUSSERVICES; }
			case 104: { return ALLSYNCHRONOUSSERVICES; }
			case 208: { return ALLPLMN_SPECIFICBS; }
			case 209: { return PLMN_SPECIFICBS_1; }
			case 210: { return PLMN_SPECIFICBS_2; }
			case 211: { return PLMN_SPECIFICBS_3; }
			case 212: { return PLMN_SPECIFICBS_4; }
			case 213: { return PLMN_SPECIFICBS_5; }
			case 214: { return PLMN_SPECIFICBS_6; }
			case 215: { return PLMN_SPECIFICBS_7; }
			case 216: { return PLMN_SPECIFICBS_8; }
			case 217: { return PLMN_SPECIFICBS_9; }
			case 218: { return PLMN_SPECIFICBS_A; }
			case 219: { return PLMN_SPECIFICBS_B; }
			case 220: { return PLMN_SPECIFICBS_C; }
			case 221: { return PLMN_SPECIFICBS_D; }
			case 222: { return PLMN_SPECIFICBS_E; }
			case 223: { return PLMN_SPECIFICBS_F; }
			default: { return null; }
		}
	}
	
}
