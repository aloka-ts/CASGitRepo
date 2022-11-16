package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumPDPContextType;

public enum PDPContextTypeEnum
{
PRIMARY,
SECONDARY;

private static Hashtable<PDPContextTypeEnum,EnumPDPContextType> stackMapping = new Hashtable<PDPContextTypeEnum,EnumPDPContextType>();
private static Hashtable<EnumPDPContextType,PDPContextTypeEnum> containerMapping = new Hashtable<EnumPDPContextType,PDPContextTypeEnum>();

 static {
stackMapping.put(PDPContextTypeEnum.PRIMARY, EnumPDPContextType.PRIMARY);
stackMapping.put(PDPContextTypeEnum.SECONDARY, EnumPDPContextType.SECONDARY);

containerMapping.put(EnumPDPContextType.PRIMARY, PDPContextTypeEnum.PRIMARY);
containerMapping.put(EnumPDPContextType.SECONDARY, PDPContextTypeEnum.SECONDARY);
}

public static final PDPContextTypeEnum getContainerObj(EnumPDPContextType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPDPContextType getStackObj(PDPContextTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PDPContextTypeEnum fromCode(int value){
	return getContainerObj(EnumPDPContextType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPDPContextType.getName(key);
}

public static boolean isValid(int value){
	return EnumPDPContextType.isValid(value);
}

public static int[] keys(){
	return EnumPDPContextType.keys();
}
}
