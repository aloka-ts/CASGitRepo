package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumSubscriberRole;

public enum SubscriberRoleEnum
{
ORIGINATING,
TERMINATING;

private static Hashtable<SubscriberRoleEnum,EnumSubscriberRole> stackMapping = new Hashtable<SubscriberRoleEnum,EnumSubscriberRole>();
private static Hashtable<EnumSubscriberRole,SubscriberRoleEnum> containerMapping = new Hashtable<EnumSubscriberRole,SubscriberRoleEnum>();

 static {
stackMapping.put(SubscriberRoleEnum.ORIGINATING, EnumSubscriberRole.ORIGINATING);
stackMapping.put(SubscriberRoleEnum.TERMINATING, EnumSubscriberRole.TERMINATING);

containerMapping.put(EnumSubscriberRole.ORIGINATING, SubscriberRoleEnum.ORIGINATING);
containerMapping.put(EnumSubscriberRole.TERMINATING, SubscriberRoleEnum.TERMINATING);
}

public static final SubscriberRoleEnum getContainerObj(EnumSubscriberRole stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSubscriberRole getStackObj(SubscriberRoleEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SubscriberRoleEnum fromCode(int value){
	return getContainerObj(EnumSubscriberRole.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSubscriberRole.getName(key);
}

public static boolean isValid(int value){
	return EnumSubscriberRole.isValid(value);
}

public static int[] keys(){
	return EnumSubscriberRole.keys();
}
}
