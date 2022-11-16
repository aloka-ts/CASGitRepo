package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumIMSIUnauthenticatedFlag;

public enum IMSIUnauthenticatedFlagEnum
{
AUTHENTICATED,
UNAUTHENTICATED;

private static Hashtable<IMSIUnauthenticatedFlagEnum,EnumIMSIUnauthenticatedFlag> stackMapping = new Hashtable<IMSIUnauthenticatedFlagEnum,EnumIMSIUnauthenticatedFlag>();
private static Hashtable<EnumIMSIUnauthenticatedFlag,IMSIUnauthenticatedFlagEnum> containerMapping = new Hashtable<EnumIMSIUnauthenticatedFlag,IMSIUnauthenticatedFlagEnum>();

 static {
stackMapping.put(IMSIUnauthenticatedFlagEnum.AUTHENTICATED, EnumIMSIUnauthenticatedFlag.Authenticated);
stackMapping.put(IMSIUnauthenticatedFlagEnum.UNAUTHENTICATED, EnumIMSIUnauthenticatedFlag.Unauthenticated);

containerMapping.put(EnumIMSIUnauthenticatedFlag.Authenticated, IMSIUnauthenticatedFlagEnum.AUTHENTICATED);
containerMapping.put(EnumIMSIUnauthenticatedFlag.Unauthenticated, IMSIUnauthenticatedFlagEnum.UNAUTHENTICATED);
}

public static final IMSIUnauthenticatedFlagEnum getContainerObj(EnumIMSIUnauthenticatedFlag stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumIMSIUnauthenticatedFlag getStackObj(IMSIUnauthenticatedFlagEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static IMSIUnauthenticatedFlagEnum fromCode(int value){
	return getContainerObj(EnumIMSIUnauthenticatedFlag.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumIMSIUnauthenticatedFlag.getName(key);
}

public static boolean isValid(int value){
	return EnumIMSIUnauthenticatedFlag.isValid(value);
}

public static int[] keys(){
	return EnumIMSIUnauthenticatedFlag.keys();
}
}
