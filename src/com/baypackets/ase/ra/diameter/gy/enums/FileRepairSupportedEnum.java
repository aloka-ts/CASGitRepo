package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumFileRepairSupported;

public enum FileRepairSupportedEnum
{
NOT_SUPPORTED,
SUPPORTED;

private static Hashtable<FileRepairSupportedEnum,EnumFileRepairSupported> stackMapping = new Hashtable<FileRepairSupportedEnum,EnumFileRepairSupported>();
private static Hashtable<EnumFileRepairSupported,FileRepairSupportedEnum> containerMapping = new Hashtable<EnumFileRepairSupported,FileRepairSupportedEnum>();

 static {
stackMapping.put(FileRepairSupportedEnum.NOT_SUPPORTED, EnumFileRepairSupported.NOT_SUPPORTED);
stackMapping.put(FileRepairSupportedEnum.SUPPORTED, EnumFileRepairSupported.SUPPORTED);

containerMapping.put(EnumFileRepairSupported.NOT_SUPPORTED, FileRepairSupportedEnum.NOT_SUPPORTED);
containerMapping.put(EnumFileRepairSupported.SUPPORTED, FileRepairSupportedEnum.SUPPORTED);
}

public static final FileRepairSupportedEnum getContainerObj(EnumFileRepairSupported stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumFileRepairSupported getStackObj(FileRepairSupportedEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static FileRepairSupportedEnum fromCode(int value){
	return getContainerObj(EnumFileRepairSupported.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumFileRepairSupported.getName(key);
}

public static boolean isValid(int value){
	return EnumFileRepairSupported.isValid(value);
}

public static int[] keys(){
	return EnumFileRepairSupported.keys();
}
}
