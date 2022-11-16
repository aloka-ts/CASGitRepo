package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumUserEquipmentInfoType;

public enum UserEquipmentInfoTypeEnum
{
EUI64,
IMEISV,
MAC,
MODIFIED_EUI64;

private static Hashtable<UserEquipmentInfoTypeEnum,EnumUserEquipmentInfoType> stackMapping = new Hashtable<UserEquipmentInfoTypeEnum,EnumUserEquipmentInfoType>();
private static Hashtable<EnumUserEquipmentInfoType,UserEquipmentInfoTypeEnum> containerMapping = new Hashtable<EnumUserEquipmentInfoType,UserEquipmentInfoTypeEnum>();

 static {
stackMapping.put(UserEquipmentInfoTypeEnum.EUI64, EnumUserEquipmentInfoType.EUI64);
stackMapping.put(UserEquipmentInfoTypeEnum.IMEISV, EnumUserEquipmentInfoType.IMEISV);
stackMapping.put(UserEquipmentInfoTypeEnum.MAC, EnumUserEquipmentInfoType.MAC);
stackMapping.put(UserEquipmentInfoTypeEnum.MODIFIED_EUI64, EnumUserEquipmentInfoType.MODIFIED_EUI64);

containerMapping.put(EnumUserEquipmentInfoType.EUI64, UserEquipmentInfoTypeEnum.EUI64);
containerMapping.put(EnumUserEquipmentInfoType.IMEISV, UserEquipmentInfoTypeEnum.IMEISV);
containerMapping.put(EnumUserEquipmentInfoType.MAC, UserEquipmentInfoTypeEnum.MAC);
containerMapping.put(EnumUserEquipmentInfoType.MODIFIED_EUI64, UserEquipmentInfoTypeEnum.MODIFIED_EUI64);
}

public static final UserEquipmentInfoTypeEnum getContainerObj(EnumUserEquipmentInfoType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumUserEquipmentInfoType getStackObj(UserEquipmentInfoTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static UserEquipmentInfoTypeEnum fromCode(int value){
	return getContainerObj(EnumUserEquipmentInfoType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumUserEquipmentInfoType.getName(key);
}

public static boolean isValid(int value){
	return EnumUserEquipmentInfoType.isValid(value);
}

public static int[] keys(){
	return EnumUserEquipmentInfoType.keys();
}
}
