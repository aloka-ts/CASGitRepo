package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumOnlineChargingFlag;

public enum OnlineChargingFlagEnum
{
	ECFaddressNotProvided ,
	ECFaddressProvided ;

private static Hashtable<OnlineChargingFlagEnum,EnumOnlineChargingFlag> stackMapping = new Hashtable<OnlineChargingFlagEnum,EnumOnlineChargingFlag>();
private static Hashtable<EnumOnlineChargingFlag,OnlineChargingFlagEnum> containerMapping = new Hashtable<EnumOnlineChargingFlag,OnlineChargingFlagEnum>();

 static {
stackMapping.put(OnlineChargingFlagEnum.ECFaddressNotProvided, EnumOnlineChargingFlag.ECFaddressNotProvided);
stackMapping.put(OnlineChargingFlagEnum.ECFaddressProvided, EnumOnlineChargingFlag.ECFaddressProvided);

containerMapping.put(EnumOnlineChargingFlag.ECFaddressNotProvided, OnlineChargingFlagEnum.ECFaddressNotProvided);
containerMapping.put(EnumOnlineChargingFlag.ECFaddressProvided, OnlineChargingFlagEnum.ECFaddressProvided);
}

public static final OnlineChargingFlagEnum getContainerObj(EnumOnlineChargingFlag stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumOnlineChargingFlag getStackObj(OnlineChargingFlagEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static OnlineChargingFlagEnum fromCode(int value){
	return getContainerObj(EnumOnlineChargingFlag.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumOnlineChargingFlag.getName(key);
}

public static boolean isValid(int value){
	return EnumOnlineChargingFlag.isValid(value);
}

public static int[] keys(){
	return EnumOnlineChargingFlag.keys();
}
}
