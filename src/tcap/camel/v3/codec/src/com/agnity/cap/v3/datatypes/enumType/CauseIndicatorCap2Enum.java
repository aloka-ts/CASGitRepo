package com.agnity.cap.v3.datatypes.enumType;

public enum CauseIndicatorCap2Enum {

	
 Normal_Event(0),Normal_Event_callclearing(1),Resource_Unavailable(2), ServiceorOption_Notavailable(3),
ServiceorOption_Notimplemented(4),Invalid_Message(5),Protocol_Error(6),Interworking(7);
	
	private int code;
	
	private CauseIndicatorCap2Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static CauseIndicatorCap2Enum getValue(int tag){
		switch (tag) {
		case 0: return Normal_Event; 
		case 1: return Normal_Event_callclearing; 
		case 2: return Resource_Unavailable;
		case 3: return ServiceorOption_Notavailable;
		case 4: return ServiceorOption_Notimplemented;
		case 5: return Invalid_Message;
		case 6: return Protocol_Error;
		case 7: return Interworking;
		default: return null;
		}
	}
}
