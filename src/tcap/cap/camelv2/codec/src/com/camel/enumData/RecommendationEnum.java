package com.camel.enumData;

/**
 * This enum represents the type of recommendation.
 * @author nkumar
 *
 */
public enum RecommendationEnum {

	/**
	 * 0- Q.931
	 * 3- X.21
	 *  4- X.25
	 *  5- public land mobile networks, Q.1031/Q.1051
	 *  All other values are reserved.
	*/
	
	Q_931(0), X_21(3), X_25(4), Q_1031AndQ_1051(5);
	
	private int code;

	private RecommendationEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RecommendationEnum fromInt(int num) {
		switch (num) {
		case 0: { return Q_931 ; }
		case 3: { return X_21 ; }
		case 4: { return X_25 ; }
		case 5: { return Q_1031AndQ_1051 ; }
		default : { return Q_931 ; }
		}
	}
}
