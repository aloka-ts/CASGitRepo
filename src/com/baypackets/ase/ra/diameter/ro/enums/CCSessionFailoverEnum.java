package com.baypackets.ase.ra.diameter.ro.enums;


public enum CCSessionFailoverEnum
{
FAILOVER_NOT_SUPPORTED,
FAILOVER_SUPPORTED;

public static java.lang.String getName(int key) {
	String code=null;
	switch(key){
	
	case 0: 
		code= FAILOVER_NOT_SUPPORTED.name();
		break;
	case 1: 
		code= FAILOVER_SUPPORTED.name();
		break;
	}
	return code;
}

public static int getCode(CCSessionFailoverEnum action) {
	int code=-1;
	switch(action){
	
	case FAILOVER_NOT_SUPPORTED: 
		code= 0;
		break;
	case FAILOVER_SUPPORTED: 
		code= 1;
		break;
	}
	return code;
}

public static CCSessionFailoverEnum fromCode(int code) {
	CCSessionFailoverEnum enumA=null;
	switch(code){
	
	case 0: 
		enumA= FAILOVER_NOT_SUPPORTED;
		break;
	case 1: 
		enumA= FAILOVER_SUPPORTED;
		break;
	}
	return enumA;
}

}
