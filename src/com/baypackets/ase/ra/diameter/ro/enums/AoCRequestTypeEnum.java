package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumAoCRequestType;

public enum AoCRequestTypeEnum
{
AOC_COST_ONLY,
AOC_FULL,
AOC_NOT_REQUESTED,
AOC_TARIFF_ONLY;

private static Hashtable<AoCRequestTypeEnum,EnumAoCRequestType> stackMapping = new Hashtable<AoCRequestTypeEnum,EnumAoCRequestType>();
private static Hashtable<EnumAoCRequestType,AoCRequestTypeEnum> containerMapping = new Hashtable<EnumAoCRequestType,AoCRequestTypeEnum>();

 static {
stackMapping.put(AoCRequestTypeEnum.AOC_COST_ONLY, EnumAoCRequestType.AoC_COST_ONLY);
stackMapping.put(AoCRequestTypeEnum.AOC_FULL, EnumAoCRequestType.AoC_FULL);
stackMapping.put(AoCRequestTypeEnum.AOC_NOT_REQUESTED, EnumAoCRequestType.AoC_NOT_REQUESTED);
stackMapping.put(AoCRequestTypeEnum.AOC_TARIFF_ONLY, EnumAoCRequestType.AoC_TARIFF_ONLY);

containerMapping.put(EnumAoCRequestType.AoC_COST_ONLY, AoCRequestTypeEnum.AOC_COST_ONLY);
containerMapping.put(EnumAoCRequestType.AoC_FULL, AoCRequestTypeEnum.AOC_FULL);
containerMapping.put(EnumAoCRequestType.AoC_NOT_REQUESTED, AoCRequestTypeEnum.AOC_NOT_REQUESTED);
containerMapping.put(EnumAoCRequestType.AoC_TARIFF_ONLY, AoCRequestTypeEnum.AOC_TARIFF_ONLY);
}

public static final AoCRequestTypeEnum getContainerObj(EnumAoCRequestType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAoCRequestType getStackObj(AoCRequestTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AoCRequestTypeEnum fromCode(int value){
	return getContainerObj(EnumAoCRequestType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAoCRequestType.getName(key);
}

public static boolean isValid(int value){
	return EnumAoCRequestType.isValid(value);
}

public static int[] keys(){
	return EnumAoCRequestType.keys();
}
}
