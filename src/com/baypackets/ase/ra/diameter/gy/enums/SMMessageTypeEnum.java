package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumSMMessageType;

public enum SMMessageTypeEnum
{
	DELIVERY_REPORT ,
	SUBMISSION ;

private static Hashtable<SMMessageTypeEnum,EnumSMMessageType> stackMapping = new Hashtable<SMMessageTypeEnum,EnumSMMessageType>();
private static Hashtable<EnumSMMessageType,SMMessageTypeEnum> containerMapping = new Hashtable<EnumSMMessageType,SMMessageTypeEnum>();

 static {
stackMapping.put(SMMessageTypeEnum.DELIVERY_REPORT, EnumSMMessageType.DELIVERY_REPORT);
stackMapping.put(SMMessageTypeEnum.SUBMISSION, EnumSMMessageType.SUBMISSION);

containerMapping.put(EnumSMMessageType.DELIVERY_REPORT, SMMessageTypeEnum.DELIVERY_REPORT);
containerMapping.put(EnumSMMessageType.SUBMISSION, SMMessageTypeEnum.SUBMISSION);
}

public static final SMMessageTypeEnum getContainerObj(EnumSMMessageType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSMMessageType getStackObj(SMMessageTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SMMessageTypeEnum fromCode(int value){
	return getContainerObj(EnumSMMessageType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSMMessageType.getName(key);
}

public static boolean isValid(int value){
	return EnumSMMessageType.isValid(value);
}

public static int[] keys(){
	return EnumSMMessageType.keys();
}
}
