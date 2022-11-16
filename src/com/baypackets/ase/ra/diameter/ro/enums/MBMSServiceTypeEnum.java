package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumMBMSServiceType;

public enum MBMSServiceTypeEnum
{
BROADCAST,
MULTICAST;

private static Hashtable<MBMSServiceTypeEnum,EnumMBMSServiceType> stackMapping = new Hashtable<MBMSServiceTypeEnum,EnumMBMSServiceType>();
private static Hashtable<EnumMBMSServiceType,MBMSServiceTypeEnum> containerMapping = new Hashtable<EnumMBMSServiceType,MBMSServiceTypeEnum>();

 static {
stackMapping.put(MBMSServiceTypeEnum.BROADCAST, EnumMBMSServiceType.BROADCAST);
stackMapping.put(MBMSServiceTypeEnum.MULTICAST, EnumMBMSServiceType.MULTICAST);

containerMapping.put(EnumMBMSServiceType.BROADCAST, MBMSServiceTypeEnum.BROADCAST);
containerMapping.put(EnumMBMSServiceType.MULTICAST, MBMSServiceTypeEnum.MULTICAST);
}

public static final MBMSServiceTypeEnum getContainerObj(EnumMBMSServiceType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumMBMSServiceType getStackObj(MBMSServiceTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static MBMSServiceTypeEnum fromCode(int value){
	return getContainerObj(EnumMBMSServiceType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumMBMSServiceType.getName(key);
}

public static boolean isValid(int value){
	return EnumMBMSServiceType.isValid(value);
}

public static int[] keys(){
	return EnumMBMSServiceType.keys();
}
}
