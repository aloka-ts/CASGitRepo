package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumRedirectHostUsage;

public enum RedirectHostUsageEnum
{
ALL_APPLICATION,
ALL_HOST,
ALL_REALM,
ALL_SESSION,
ALL_USER,
DONT_CACHE,
REALM_AND_APPLICATION;

private static Hashtable<RedirectHostUsageEnum,EnumRedirectHostUsage> stackMapping = new Hashtable<RedirectHostUsageEnum,EnumRedirectHostUsage>();
private static Hashtable<EnumRedirectHostUsage,RedirectHostUsageEnum> containerMapping = new Hashtable<EnumRedirectHostUsage,RedirectHostUsageEnum>();

 static {
stackMapping.put(RedirectHostUsageEnum.ALL_APPLICATION, EnumRedirectHostUsage.ALL_APPLICATION);
stackMapping.put(RedirectHostUsageEnum.ALL_HOST, EnumRedirectHostUsage.ALL_HOST);
stackMapping.put(RedirectHostUsageEnum.ALL_REALM, EnumRedirectHostUsage.ALL_REALM);
stackMapping.put(RedirectHostUsageEnum.ALL_SESSION, EnumRedirectHostUsage.ALL_SESSION);
stackMapping.put(RedirectHostUsageEnum.ALL_USER, EnumRedirectHostUsage.ALL_USER);
stackMapping.put(RedirectHostUsageEnum.DONT_CACHE, EnumRedirectHostUsage.DONT_CACHE);
stackMapping.put(RedirectHostUsageEnum.REALM_AND_APPLICATION, EnumRedirectHostUsage.REALM_AND_APPLICATION);

containerMapping.put(EnumRedirectHostUsage.ALL_APPLICATION, RedirectHostUsageEnum.ALL_APPLICATION);
containerMapping.put(EnumRedirectHostUsage.ALL_HOST, RedirectHostUsageEnum.ALL_HOST);
containerMapping.put(EnumRedirectHostUsage.ALL_REALM, RedirectHostUsageEnum.ALL_REALM);
containerMapping.put(EnumRedirectHostUsage.ALL_SESSION, RedirectHostUsageEnum.ALL_SESSION);
containerMapping.put(EnumRedirectHostUsage.ALL_USER, RedirectHostUsageEnum.ALL_USER);
containerMapping.put(EnumRedirectHostUsage.DONT_CACHE, RedirectHostUsageEnum.DONT_CACHE);
containerMapping.put(EnumRedirectHostUsage.REALM_AND_APPLICATION, RedirectHostUsageEnum.REALM_AND_APPLICATION);
}

public static final RedirectHostUsageEnum getContainerObj(EnumRedirectHostUsage stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumRedirectHostUsage getStackObj(RedirectHostUsageEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static RedirectHostUsageEnum fromCode(int value){
	return getContainerObj(EnumRedirectHostUsage.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumRedirectHostUsage.getName(key);
}

public static boolean isValid(int value){
	return EnumRedirectHostUsage.isValid(value);
}

public static int[] keys(){
	return EnumRedirectHostUsage.keys();
}
}
