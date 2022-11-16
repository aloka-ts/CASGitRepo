package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumOnlineChargingFlag;

public enum OnlineChargingFlagEnum
{
ECFADDRESSNOTPROVIDED,
ECFADDRESSPROVIDED;

private static Hashtable<OnlineChargingFlagEnum,EnumOnlineChargingFlag> stackMapping = new Hashtable<OnlineChargingFlagEnum,EnumOnlineChargingFlag>();
private static Hashtable<EnumOnlineChargingFlag,OnlineChargingFlagEnum> containerMapping = new Hashtable<EnumOnlineChargingFlag,OnlineChargingFlagEnum>();

 static {
stackMapping.put(OnlineChargingFlagEnum.ECFADDRESSNOTPROVIDED, EnumOnlineChargingFlag.ECFAddressNotProvided);
stackMapping.put(OnlineChargingFlagEnum.ECFADDRESSPROVIDED, EnumOnlineChargingFlag.ECFAddressProvided);

containerMapping.put(EnumOnlineChargingFlag.ECFAddressNotProvided, OnlineChargingFlagEnum.ECFADDRESSNOTPROVIDED);
containerMapping.put(EnumOnlineChargingFlag.ECFAddressProvided, OnlineChargingFlagEnum.ECFADDRESSPROVIDED);
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
