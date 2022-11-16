package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumDirectDebitingFailureHandling;
public enum DirectDebitingFailureHandlingEnum
{
CONTINUE,
TERMINATE_OR_BUFFER;

private static Hashtable<DirectDebitingFailureHandlingEnum,EnumDirectDebitingFailureHandling> stackMapping = new Hashtable<DirectDebitingFailureHandlingEnum,EnumDirectDebitingFailureHandling>();
private static Hashtable<EnumDirectDebitingFailureHandling,DirectDebitingFailureHandlingEnum> containerMapping = new Hashtable<EnumDirectDebitingFailureHandling,DirectDebitingFailureHandlingEnum>();

 static {
stackMapping.put(DirectDebitingFailureHandlingEnum.CONTINUE, EnumDirectDebitingFailureHandling.CONTINUE);
stackMapping.put(DirectDebitingFailureHandlingEnum.TERMINATE_OR_BUFFER, EnumDirectDebitingFailureHandling.TERMINATE_OR_BUFFER);

containerMapping.put(EnumDirectDebitingFailureHandling.CONTINUE, DirectDebitingFailureHandlingEnum.CONTINUE);
containerMapping.put(EnumDirectDebitingFailureHandling.TERMINATE_OR_BUFFER, DirectDebitingFailureHandlingEnum.TERMINATE_OR_BUFFER);
}

public static final DirectDebitingFailureHandlingEnum getContainerObj(EnumDirectDebitingFailureHandling stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumDirectDebitingFailureHandling getStackObj(DirectDebitingFailureHandlingEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static DirectDebitingFailureHandlingEnum fromCode(int value){
	return getContainerObj(EnumDirectDebitingFailureHandling.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumDirectDebitingFailureHandling.getName(key);
}

public static boolean isValid(int value){
	return EnumDirectDebitingFailureHandling.isValid(value);
}

public static int[] keys(){
	return EnumDirectDebitingFailureHandling.keys();
}
}
