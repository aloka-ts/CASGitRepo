package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumLowBalanceIndication;

public enum LowBalanceIndicationEnum
{
NOTAPPLICABLE,
YES;

private static Hashtable<LowBalanceIndicationEnum,EnumLowBalanceIndication> stackMapping = new Hashtable<LowBalanceIndicationEnum,EnumLowBalanceIndication>();
private static Hashtable<EnumLowBalanceIndication,LowBalanceIndicationEnum> containerMapping = new Hashtable<EnumLowBalanceIndication,LowBalanceIndicationEnum>();

 static {
stackMapping.put(LowBalanceIndicationEnum.NOTAPPLICABLE, EnumLowBalanceIndication.NOTAPPLICABLE);
stackMapping.put(LowBalanceIndicationEnum.YES, EnumLowBalanceIndication.YES);

containerMapping.put(EnumLowBalanceIndication.NOTAPPLICABLE, LowBalanceIndicationEnum.NOTAPPLICABLE);
containerMapping.put(EnumLowBalanceIndication.YES, LowBalanceIndicationEnum.YES);
}

public static final LowBalanceIndicationEnum getContainerObj(EnumLowBalanceIndication stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumLowBalanceIndication getStackObj(LowBalanceIndicationEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static LowBalanceIndicationEnum fromCode(int value){
	return getContainerObj(EnumLowBalanceIndication.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumLowBalanceIndication.getName(key);
}

public static boolean isValid(int value){
	return EnumLowBalanceIndication.isValid(value);
}

public static int[] keys(){
	return EnumLowBalanceIndication.keys();
}
}
