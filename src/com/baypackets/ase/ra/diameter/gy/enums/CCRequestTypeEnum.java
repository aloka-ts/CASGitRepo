package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumCCRequestType;

public enum CCRequestTypeEnum
{
EVENT_REQUEST,
INITIAL_REQUEST,
TERMINATION_REQUEST,
UPDATE_REQUEST;

private static Hashtable<CCRequestTypeEnum,EnumCCRequestType> stackMapping = new Hashtable<CCRequestTypeEnum,EnumCCRequestType>();
private static Hashtable<EnumCCRequestType,CCRequestTypeEnum> containerMapping = new Hashtable<EnumCCRequestType,CCRequestTypeEnum>();

 static {
stackMapping.put(CCRequestTypeEnum.EVENT_REQUEST, EnumCCRequestType.EVENT_REQUEST);
stackMapping.put(CCRequestTypeEnum.INITIAL_REQUEST, EnumCCRequestType.INITIAL_REQUEST);
stackMapping.put(CCRequestTypeEnum.TERMINATION_REQUEST, EnumCCRequestType.TERMINATION_REQUEST);
stackMapping.put(CCRequestTypeEnum.UPDATE_REQUEST, EnumCCRequestType.UPDATE_REQUEST);

containerMapping.put(EnumCCRequestType.EVENT_REQUEST, CCRequestTypeEnum.EVENT_REQUEST);
containerMapping.put(EnumCCRequestType.INITIAL_REQUEST, CCRequestTypeEnum.INITIAL_REQUEST);
containerMapping.put(EnumCCRequestType.TERMINATION_REQUEST, CCRequestTypeEnum.TERMINATION_REQUEST);
containerMapping.put(EnumCCRequestType.UPDATE_REQUEST, CCRequestTypeEnum.UPDATE_REQUEST);
}

public static final CCRequestTypeEnum getContainerObj(EnumCCRequestType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCCRequestType getStackObj(CCRequestTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CCRequestTypeEnum fromCode(int value){
	return getContainerObj(EnumCCRequestType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCCRequestType.getName(key);
}

public static boolean isValid(int value){
	return EnumCCRequestType.isValid(value);
}

public static int[] keys(){
	return EnumCCRequestType.keys();
}
}
