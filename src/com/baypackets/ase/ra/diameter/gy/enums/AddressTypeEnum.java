package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumAddressType;

public enum AddressTypeEnum
{
ALPHANUMERICSHORTCODE,
EMAILADDRESS,
IPV4ADDRESS,
IPV6ADDRESS,
MSISDN,
NUMERICSHORTCODE,
OTHER;

private static Hashtable<AddressTypeEnum,EnumAddressType> stackMapping = new Hashtable<AddressTypeEnum,EnumAddressType>();
private static Hashtable<EnumAddressType,AddressTypeEnum> containerMapping = new Hashtable<EnumAddressType,AddressTypeEnum>();

 static {
stackMapping.put(AddressTypeEnum.ALPHANUMERICSHORTCODE, EnumAddressType.AlphanumericShortcode);
stackMapping.put(AddressTypeEnum.EMAILADDRESS, EnumAddressType.EMailAddress);
stackMapping.put(AddressTypeEnum.IPV4ADDRESS, EnumAddressType.IPv4Address);
stackMapping.put(AddressTypeEnum.IPV6ADDRESS, EnumAddressType.IPv6Address);
stackMapping.put(AddressTypeEnum.MSISDN, EnumAddressType.MSISDN);
stackMapping.put(AddressTypeEnum.NUMERICSHORTCODE, EnumAddressType.NumericShortcode);
stackMapping.put(AddressTypeEnum.OTHER, EnumAddressType.Other);

containerMapping.put(EnumAddressType.AlphanumericShortcode, AddressTypeEnum.ALPHANUMERICSHORTCODE);
containerMapping.put(EnumAddressType.EMailAddress, AddressTypeEnum.EMAILADDRESS);
containerMapping.put(EnumAddressType.IPv4Address, AddressTypeEnum.IPV4ADDRESS);
containerMapping.put(EnumAddressType.IPv6Address, AddressTypeEnum.IPV6ADDRESS);
containerMapping.put(EnumAddressType.MSISDN, AddressTypeEnum.MSISDN);
containerMapping.put(EnumAddressType.NumericShortcode, AddressTypeEnum.NUMERICSHORTCODE);
containerMapping.put(EnumAddressType.Other, AddressTypeEnum.OTHER);
}

public static final AddressTypeEnum getContainerObj(EnumAddressType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAddressType getStackObj(AddressTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AddressTypeEnum fromCode(int value){
	return getContainerObj(EnumAddressType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAddressType.getName(key);
}

public static boolean isValid(int value){
	return EnumAddressType.isValid(value);
}

public static int[] keys(){
	return EnumAddressType.keys();
}
}
