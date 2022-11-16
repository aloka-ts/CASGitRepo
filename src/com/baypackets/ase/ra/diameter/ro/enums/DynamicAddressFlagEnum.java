package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumDynamicAddressFlag;

public enum DynamicAddressFlagEnum
{
DYNAMIC,
STATIC;

private static Hashtable<DynamicAddressFlagEnum,EnumDynamicAddressFlag> stackMapping = new Hashtable<DynamicAddressFlagEnum,EnumDynamicAddressFlag>();
private static Hashtable<EnumDynamicAddressFlag,DynamicAddressFlagEnum> containerMapping = new Hashtable<EnumDynamicAddressFlag,DynamicAddressFlagEnum>();

 static {
stackMapping.put(DynamicAddressFlagEnum.DYNAMIC, EnumDynamicAddressFlag.Dynamic);
stackMapping.put(DynamicAddressFlagEnum.STATIC, EnumDynamicAddressFlag.Static);

containerMapping.put(EnumDynamicAddressFlag.Dynamic, DynamicAddressFlagEnum.DYNAMIC);
containerMapping.put(EnumDynamicAddressFlag.Static, DynamicAddressFlagEnum.STATIC);
}

public static final DynamicAddressFlagEnum getContainerObj(EnumDynamicAddressFlag stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumDynamicAddressFlag getStackObj(DynamicAddressFlagEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static DynamicAddressFlagEnum fromCode(int value){
	return getContainerObj(EnumDynamicAddressFlag.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumDynamicAddressFlag.getName(key);
}

public static boolean isValid(int value){
	return EnumDynamicAddressFlag.isValid(value);
}

public static int[] keys(){
	return EnumDynamicAddressFlag.keys();
}
}
