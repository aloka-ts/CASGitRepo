package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumMBMSUserServiceType;

public enum MBMSUserServiceTypeEnum
{
DOWNLOAD,
STREAMING;

private static Hashtable<MBMSUserServiceTypeEnum,EnumMBMSUserServiceType> stackMapping = new Hashtable<MBMSUserServiceTypeEnum,EnumMBMSUserServiceType>();
private static Hashtable<EnumMBMSUserServiceType,MBMSUserServiceTypeEnum> containerMapping = new Hashtable<EnumMBMSUserServiceType,MBMSUserServiceTypeEnum>();

 static {
stackMapping.put(MBMSUserServiceTypeEnum.DOWNLOAD, EnumMBMSUserServiceType.DOWNLOAD);
stackMapping.put(MBMSUserServiceTypeEnum.STREAMING, EnumMBMSUserServiceType.STREAMING);

containerMapping.put(EnumMBMSUserServiceType.DOWNLOAD, MBMSUserServiceTypeEnum.DOWNLOAD);
containerMapping.put(EnumMBMSUserServiceType.STREAMING, MBMSUserServiceTypeEnum.STREAMING);
}

public static final MBMSUserServiceTypeEnum getContainerObj(EnumMBMSUserServiceType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumMBMSUserServiceType getStackObj(MBMSUserServiceTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static MBMSUserServiceTypeEnum fromCode(int value){
	return getContainerObj(EnumMBMSUserServiceType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumMBMSUserServiceType.getName(key);
}

public static boolean isValid(int value){
	return EnumMBMSUserServiceType.isValid(value);
}

public static int[] keys(){
	return EnumMBMSUserServiceType.keys();
}
}
