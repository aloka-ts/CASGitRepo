package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumRedirectAddressType;

public enum RedirectAddressTypeEnum
{
URI,
URL;

private static Hashtable<RedirectAddressTypeEnum,EnumRedirectAddressType> stackMapping = new Hashtable<RedirectAddressTypeEnum,EnumRedirectAddressType>();
private static Hashtable<EnumRedirectAddressType,RedirectAddressTypeEnum> containerMapping = new Hashtable<EnumRedirectAddressType,RedirectAddressTypeEnum>();

 static {
stackMapping.put(RedirectAddressTypeEnum.URI, EnumRedirectAddressType.URI);
stackMapping.put(RedirectAddressTypeEnum.URL, EnumRedirectAddressType.URL);

containerMapping.put(EnumRedirectAddressType.URI, RedirectAddressTypeEnum.URI);
containerMapping.put(EnumRedirectAddressType.URL, RedirectAddressTypeEnum.URL);
}

public static final RedirectAddressTypeEnum getContainerObj(EnumRedirectAddressType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumRedirectAddressType getStackObj(RedirectAddressTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static RedirectAddressTypeEnum fromCode(int value){
	return getContainerObj(EnumRedirectAddressType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumRedirectAddressType.getName(key);
}

public static boolean isValid(int value){
	return EnumRedirectAddressType.isValid(value);
}

public static int[] keys(){
	return EnumRedirectAddressType.keys();
}
}
