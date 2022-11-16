package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumAddresseeType;

public enum AddresseeTypeEnum
{
BCC,
CC,
TO;

private static Hashtable<AddresseeTypeEnum,EnumAddresseeType> stackMapping = new Hashtable<AddresseeTypeEnum,EnumAddresseeType>();
private static Hashtable<EnumAddresseeType,AddresseeTypeEnum> containerMapping = new Hashtable<EnumAddresseeType,AddresseeTypeEnum>();

 static {
stackMapping.put(AddresseeTypeEnum.BCC, EnumAddresseeType.BCC);
stackMapping.put(AddresseeTypeEnum.CC, EnumAddresseeType.CC);
stackMapping.put(AddresseeTypeEnum.TO, EnumAddresseeType.TO);

containerMapping.put(EnumAddresseeType.BCC, AddresseeTypeEnum.BCC);
containerMapping.put(EnumAddresseeType.CC, AddresseeTypeEnum.CC);
containerMapping.put(EnumAddresseeType.TO, AddresseeTypeEnum.TO);
}

public static final AddresseeTypeEnum getContainerObj(EnumAddresseeType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAddresseeType getStackObj(AddresseeTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AddresseeTypeEnum fromCode(int value){
	return getContainerObj(EnumAddresseeType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAddresseeType.getName(key);
}

public static boolean isValid(int value){
	return EnumAddresseeType.isValid(value);
}

public static int[] keys(){
	return EnumAddresseeType.keys();
}
}
