package com.baypackets.ase.ra.diameter.ro.enums;


public enum CCRequestTypeEnum
{
INITIAL_REQUEST,
UPDATE_REQUEST,
TERMINATION_REQUEST,
EVENT_REQUEST;


public static java.lang.String getName(int key) {
	String code=null;
	switch(key){
	
	case 1: 
		code= INITIAL_REQUEST.name();
		break;
	case 2: 
		code= UPDATE_REQUEST.name();
		break;
	case 3:
		code= TERMINATION_REQUEST.name();
		break;
	case 4:
		code=EVENT_REQUEST.name();
		break;
	}
	return code;
}

public static int getCode(CCRequestTypeEnum action) {
	int code=-1;
	switch(action){
	
	case INITIAL_REQUEST: 
		code= 1;
		break;
	case UPDATE_REQUEST: 
		code= 2;
		break;
	case TERMINATION_REQUEST:
		code= 3;
		break;
	case EVENT_REQUEST:
		code=4;
		break;
	}
	return code;
}

public static CCRequestTypeEnum fromCode(int code) {
	CCRequestTypeEnum enumA=null;
	switch(code){
	
	case 1: 
		enumA= INITIAL_REQUEST;
		break;
	case 2: 
		enumA= UPDATE_REQUEST;
		break;
	case 3:
		enumA= TERMINATION_REQUEST;
		break;
	case 4:
		enumA=EVENT_REQUEST;
		break;
	}
	return enumA;
}


}
