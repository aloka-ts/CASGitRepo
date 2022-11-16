package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumLCSClientType;

public enum LCSClientTypeEnum
{
EMERGENCY_SERVICES,
LAWFUL_INTERCEPT_SERVICES,
PLMN_OPERATOR_SERVICES,
VALUE_ADDED_SERVICES;

private static Hashtable<LCSClientTypeEnum,EnumLCSClientType> stackMapping = new Hashtable<LCSClientTypeEnum,EnumLCSClientType>();
private static Hashtable<EnumLCSClientType,LCSClientTypeEnum> containerMapping = new Hashtable<EnumLCSClientType,LCSClientTypeEnum>();

 static {
stackMapping.put(LCSClientTypeEnum.EMERGENCY_SERVICES, EnumLCSClientType.EMERGENCY_SERVICES);
stackMapping.put(LCSClientTypeEnum.LAWFUL_INTERCEPT_SERVICES, EnumLCSClientType.LAWFUL_INTERCEPT_SERVICES);
stackMapping.put(LCSClientTypeEnum.PLMN_OPERATOR_SERVICES, EnumLCSClientType.PLMN_OPERATOR_SERVICES);
stackMapping.put(LCSClientTypeEnum.VALUE_ADDED_SERVICES, EnumLCSClientType.VALUE_ADDED_SERVICES);

containerMapping.put(EnumLCSClientType.EMERGENCY_SERVICES, LCSClientTypeEnum.EMERGENCY_SERVICES);
containerMapping.put(EnumLCSClientType.LAWFUL_INTERCEPT_SERVICES, LCSClientTypeEnum.LAWFUL_INTERCEPT_SERVICES);
containerMapping.put(EnumLCSClientType.PLMN_OPERATOR_SERVICES, LCSClientTypeEnum.PLMN_OPERATOR_SERVICES);
containerMapping.put(EnumLCSClientType.VALUE_ADDED_SERVICES, LCSClientTypeEnum.VALUE_ADDED_SERVICES);
}

public static final LCSClientTypeEnum getContainerObj(EnumLCSClientType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumLCSClientType getStackObj(LCSClientTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static LCSClientTypeEnum fromCode(int value){
	return getContainerObj(EnumLCSClientType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumLCSClientType.getName(key);
}

public static boolean isValid(int value){
	return EnumLCSClientType.isValid(value);
}

public static int[] keys(){
	return EnumLCSClientType.keys();
}
}
