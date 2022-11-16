package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumReasonCode;

public enum ReasonCodeEnum
{
ADDONCHARGE,
COMMUNICATIONATTEMPTCHARGE,
SETUPCHARGE,
UNKNOWN,
USAGE;

private static Hashtable<ReasonCodeEnum,EnumReasonCode> stackMapping = new Hashtable<ReasonCodeEnum,EnumReasonCode>();
private static Hashtable<EnumReasonCode,ReasonCodeEnum> containerMapping = new Hashtable<EnumReasonCode,ReasonCodeEnum>();

 static {
stackMapping.put(ReasonCodeEnum.ADDONCHARGE, EnumReasonCode.ADDONCHARGE);
stackMapping.put(ReasonCodeEnum.COMMUNICATIONATTEMPTCHARGE, EnumReasonCode.COMMUNICATIONATTEMPTCHARGE);
stackMapping.put(ReasonCodeEnum.SETUPCHARGE, EnumReasonCode.SETUPCHARGE);
stackMapping.put(ReasonCodeEnum.UNKNOWN, EnumReasonCode.UNKNOWN);
stackMapping.put(ReasonCodeEnum.USAGE, EnumReasonCode.USAGE);

containerMapping.put(EnumReasonCode.ADDONCHARGE, ReasonCodeEnum.ADDONCHARGE);
containerMapping.put(EnumReasonCode.COMMUNICATIONATTEMPTCHARGE, ReasonCodeEnum.COMMUNICATIONATTEMPTCHARGE);
containerMapping.put(EnumReasonCode.SETUPCHARGE, ReasonCodeEnum.SETUPCHARGE);
containerMapping.put(EnumReasonCode.UNKNOWN, ReasonCodeEnum.UNKNOWN);
containerMapping.put(EnumReasonCode.USAGE, ReasonCodeEnum.USAGE);
}

public static final ReasonCodeEnum getContainerObj(EnumReasonCode stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumReasonCode getStackObj(ReasonCodeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ReasonCodeEnum fromCode(int value){
	return getContainerObj(EnumReasonCode.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumReasonCode.getName(key);
}

public static boolean isValid(int value){
	return EnumReasonCode.isValid(value);
}

public static int[] keys(){
	return EnumReasonCode.keys();
}
}
