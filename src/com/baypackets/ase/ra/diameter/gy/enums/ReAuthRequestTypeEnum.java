package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumReAuthRequestType;

public enum ReAuthRequestTypeEnum
{
AUTHORIZE_AUTHENTICATE,
AUTHORIZE_ONLY;

private static Hashtable<ReAuthRequestTypeEnum,EnumReAuthRequestType> stackMapping = new Hashtable<ReAuthRequestTypeEnum,EnumReAuthRequestType>();
private static Hashtable<EnumReAuthRequestType,ReAuthRequestTypeEnum> containerMapping = new Hashtable<EnumReAuthRequestType,ReAuthRequestTypeEnum>();

 static {
stackMapping.put(ReAuthRequestTypeEnum.AUTHORIZE_AUTHENTICATE, EnumReAuthRequestType.AUTHORIZE_AUTHENTICATE);
stackMapping.put(ReAuthRequestTypeEnum.AUTHORIZE_ONLY, EnumReAuthRequestType.AUTHORIZE_ONLY);

containerMapping.put(EnumReAuthRequestType.AUTHORIZE_AUTHENTICATE, ReAuthRequestTypeEnum.AUTHORIZE_AUTHENTICATE);
containerMapping.put(EnumReAuthRequestType.AUTHORIZE_ONLY, ReAuthRequestTypeEnum.AUTHORIZE_ONLY);
}

public static final ReAuthRequestTypeEnum getContainerObj(EnumReAuthRequestType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumReAuthRequestType getStackObj(ReAuthRequestTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ReAuthRequestTypeEnum fromCode(int value){
	return getContainerObj(EnumReAuthRequestType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumReAuthRequestType.getName(key);
}

public static boolean isValid(int value){
	return EnumReAuthRequestType.isValid(value);
}

public static int[] keys(){
	return EnumReAuthRequestType.keys();
}
}
