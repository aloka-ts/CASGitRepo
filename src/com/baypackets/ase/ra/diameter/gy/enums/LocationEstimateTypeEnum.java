package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumLocationEstimateType;

public enum LocationEstimateTypeEnum
{
ACTIVATE_DEFERRED_LOCATION,
CANCEL_DEFERRED_LOCATION,
CURRENT_LAST_KNOWN_LOCATION,
CURRENT_LOCATION,
INITIAL_LOCATION;

private static Hashtable<LocationEstimateTypeEnum,EnumLocationEstimateType> stackMapping = new Hashtable<LocationEstimateTypeEnum,EnumLocationEstimateType>();
private static Hashtable<EnumLocationEstimateType,LocationEstimateTypeEnum> containerMapping = new Hashtable<EnumLocationEstimateType,LocationEstimateTypeEnum>();

 static {
stackMapping.put(LocationEstimateTypeEnum.ACTIVATE_DEFERRED_LOCATION, EnumLocationEstimateType.ACTIVATE_DEFERRED_LOCATION);
stackMapping.put(LocationEstimateTypeEnum.CANCEL_DEFERRED_LOCATION, EnumLocationEstimateType.CANCEL_DEFERRED_LOCATION);
stackMapping.put(LocationEstimateTypeEnum.CURRENT_LAST_KNOWN_LOCATION, EnumLocationEstimateType.CURRENT_LAST_KNOWN_LOCATION);
stackMapping.put(LocationEstimateTypeEnum.CURRENT_LOCATION, EnumLocationEstimateType.CURRENT_LOCATION);
stackMapping.put(LocationEstimateTypeEnum.INITIAL_LOCATION, EnumLocationEstimateType.INITIAL_LOCATION);

containerMapping.put(EnumLocationEstimateType.ACTIVATE_DEFERRED_LOCATION, LocationEstimateTypeEnum.ACTIVATE_DEFERRED_LOCATION);
containerMapping.put(EnumLocationEstimateType.CANCEL_DEFERRED_LOCATION, LocationEstimateTypeEnum.CANCEL_DEFERRED_LOCATION);
containerMapping.put(EnumLocationEstimateType.CURRENT_LAST_KNOWN_LOCATION, LocationEstimateTypeEnum.CURRENT_LAST_KNOWN_LOCATION);
containerMapping.put(EnumLocationEstimateType.CURRENT_LOCATION, LocationEstimateTypeEnum.CURRENT_LOCATION);
containerMapping.put(EnumLocationEstimateType.INITIAL_LOCATION, LocationEstimateTypeEnum.INITIAL_LOCATION);
}

public static final LocationEstimateTypeEnum getContainerObj(EnumLocationEstimateType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumLocationEstimateType getStackObj(LocationEstimateTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static LocationEstimateTypeEnum fromCode(int value){
	return getContainerObj(EnumLocationEstimateType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumLocationEstimateType.getName(key);
}

public static boolean isValid(int value){
	return EnumLocationEstimateType.isValid(value);
}

public static int[] keys(){
	return EnumLocationEstimateType.keys();
}
}
