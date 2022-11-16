package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumInterfaceType;

public enum InterfaceTypeEnum
{
APPLICATION_ORIGINATING,
APPLICATION_TERMINATION,
MOBILE_ORIGINATING,
MOBILE_TERMINATING,
UNKNOWN;

private static Hashtable<InterfaceTypeEnum,EnumInterfaceType> stackMapping = new Hashtable<InterfaceTypeEnum,EnumInterfaceType>();
private static Hashtable<EnumInterfaceType,InterfaceTypeEnum> containerMapping = new Hashtable<EnumInterfaceType,InterfaceTypeEnum>();

 static {
stackMapping.put(InterfaceTypeEnum.APPLICATION_ORIGINATING, EnumInterfaceType.APPLICATION_ORIGINATING);
stackMapping.put(InterfaceTypeEnum.APPLICATION_TERMINATION, EnumInterfaceType.APPLICATION_TERMINATION);
stackMapping.put(InterfaceTypeEnum.MOBILE_ORIGINATING, EnumInterfaceType.MOBILE_ORIGINATING);
stackMapping.put(InterfaceTypeEnum.MOBILE_TERMINATING, EnumInterfaceType.MOBILE_TERMINATING);
stackMapping.put(InterfaceTypeEnum.UNKNOWN, EnumInterfaceType.Unknown);

containerMapping.put(EnumInterfaceType.APPLICATION_ORIGINATING, InterfaceTypeEnum.APPLICATION_ORIGINATING);
containerMapping.put(EnumInterfaceType.APPLICATION_TERMINATION, InterfaceTypeEnum.APPLICATION_TERMINATION);
containerMapping.put(EnumInterfaceType.MOBILE_ORIGINATING, InterfaceTypeEnum.MOBILE_ORIGINATING);
containerMapping.put(EnumInterfaceType.MOBILE_TERMINATING, InterfaceTypeEnum.MOBILE_TERMINATING);
containerMapping.put(EnumInterfaceType.Unknown, InterfaceTypeEnum.UNKNOWN);
}

public static final InterfaceTypeEnum getContainerObj(EnumInterfaceType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumInterfaceType getStackObj(InterfaceTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static InterfaceTypeEnum fromCode(int value){
	return getContainerObj(EnumInterfaceType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumInterfaceType.getName(key);
}

public static boolean isValid(int value){
	return EnumInterfaceType.isValid(value);
}

public static int[] keys(){
	return EnumInterfaceType.keys();
}
}
