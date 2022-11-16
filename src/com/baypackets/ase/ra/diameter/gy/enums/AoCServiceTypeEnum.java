package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumAoCServiceType;

public enum AoCServiceTypeEnum
{
AOCD,
AOCE,
AOCS,
NONE;

private static Hashtable<AoCServiceTypeEnum,EnumAoCServiceType> stackMapping = new Hashtable<AoCServiceTypeEnum,EnumAoCServiceType>();
private static Hashtable<EnumAoCServiceType,AoCServiceTypeEnum> containerMapping = new Hashtable<EnumAoCServiceType,AoCServiceTypeEnum>();

 static {
stackMapping.put(AoCServiceTypeEnum.AOCD, EnumAoCServiceType.AOCD);
stackMapping.put(AoCServiceTypeEnum.AOCE, EnumAoCServiceType.AOCE);
stackMapping.put(AoCServiceTypeEnum.AOCS, EnumAoCServiceType.AOCS);
stackMapping.put(AoCServiceTypeEnum.NONE, EnumAoCServiceType.NONE);

containerMapping.put(EnumAoCServiceType.AOCD, AoCServiceTypeEnum.AOCD);
containerMapping.put(EnumAoCServiceType.AOCE, AoCServiceTypeEnum.AOCE);
containerMapping.put(EnumAoCServiceType.AOCS, AoCServiceTypeEnum.AOCS);
containerMapping.put(EnumAoCServiceType.NONE, AoCServiceTypeEnum.NONE);
}

public static final AoCServiceTypeEnum getContainerObj(EnumAoCServiceType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAoCServiceType getStackObj(AoCServiceTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AoCServiceTypeEnum fromCode(int value){
	return getContainerObj(EnumAoCServiceType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAoCServiceType.getName(key);
}

public static boolean isValid(int value){
	return EnumAoCServiceType.isValid(value);
}

public static int[] keys(){
	return EnumAoCServiceType.keys();
}
}
