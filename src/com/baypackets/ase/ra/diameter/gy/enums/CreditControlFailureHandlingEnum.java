package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumCreditControlFailureHandling;

public enum CreditControlFailureHandlingEnum
{
CONTINUE,
RETRY_AND_TERMINATE,
TERMINATE;

private static Hashtable<CreditControlFailureHandlingEnum,EnumCreditControlFailureHandling> stackMapping = new Hashtable<CreditControlFailureHandlingEnum,EnumCreditControlFailureHandling>();
private static Hashtable<EnumCreditControlFailureHandling,CreditControlFailureHandlingEnum> containerMapping = new Hashtable<EnumCreditControlFailureHandling,CreditControlFailureHandlingEnum>();

 static {
stackMapping.put(CreditControlFailureHandlingEnum.CONTINUE, EnumCreditControlFailureHandling.CONTINUE);
stackMapping.put(CreditControlFailureHandlingEnum.RETRY_AND_TERMINATE, EnumCreditControlFailureHandling.RETRY_AND_TERMINATE);
stackMapping.put(CreditControlFailureHandlingEnum.TERMINATE, EnumCreditControlFailureHandling.TERMINATE);

containerMapping.put(EnumCreditControlFailureHandling.CONTINUE, CreditControlFailureHandlingEnum.CONTINUE);
containerMapping.put(EnumCreditControlFailureHandling.RETRY_AND_TERMINATE, CreditControlFailureHandlingEnum.RETRY_AND_TERMINATE);
containerMapping.put(EnumCreditControlFailureHandling.TERMINATE, CreditControlFailureHandlingEnum.TERMINATE);
}

public static final CreditControlFailureHandlingEnum getContainerObj(EnumCreditControlFailureHandling stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCreditControlFailureHandling getStackObj(CreditControlFailureHandlingEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CreditControlFailureHandlingEnum fromCode(int value){
	return getContainerObj(EnumCreditControlFailureHandling.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCreditControlFailureHandling.getName(key);
}

public static boolean isValid(int value){
	return EnumCreditControlFailureHandling.isValid(value);
}

public static int[] keys(){
	return EnumCreditControlFailureHandling.keys();
}
}
