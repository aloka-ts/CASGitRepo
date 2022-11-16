package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumAdaptations;

public enum AdaptationsEnum
{
NO,
YES;

private static Hashtable<AdaptationsEnum,EnumAdaptations> stackMapping = new Hashtable<AdaptationsEnum,EnumAdaptations>();
private static Hashtable<EnumAdaptations,AdaptationsEnum> containerMapping = new Hashtable<EnumAdaptations,AdaptationsEnum>();

 static {
stackMapping.put(AdaptationsEnum.NO, EnumAdaptations.No);
stackMapping.put(AdaptationsEnum.YES, EnumAdaptations.Yes);

containerMapping.put(EnumAdaptations.No, AdaptationsEnum.NO);
containerMapping.put(EnumAdaptations.Yes, AdaptationsEnum.YES);
}

public static final AdaptationsEnum getContainerObj(EnumAdaptations stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAdaptations getStackObj(AdaptationsEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AdaptationsEnum fromCode(int value){
	return getContainerObj(EnumAdaptations.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAdaptations.getName(key);
}

public static boolean isValid(int value){
	return EnumAdaptations.isValid(value);
}

public static int[] keys(){
	return EnumAdaptations.keys();
}
}
