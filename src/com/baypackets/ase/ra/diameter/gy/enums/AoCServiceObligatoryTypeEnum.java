package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumAoCServiceObligatoryType;

public enum AoCServiceObligatoryTypeEnum
{
BINDING,
NON_BINDING;

private static Hashtable<AoCServiceObligatoryTypeEnum,EnumAoCServiceObligatoryType> stackMapping = new Hashtable<AoCServiceObligatoryTypeEnum,EnumAoCServiceObligatoryType>();
private static Hashtable<EnumAoCServiceObligatoryType,AoCServiceObligatoryTypeEnum> containerMapping = new Hashtable<EnumAoCServiceObligatoryType,AoCServiceObligatoryTypeEnum>();

 static {
stackMapping.put(AoCServiceObligatoryTypeEnum.BINDING, EnumAoCServiceObligatoryType.BINDING);
stackMapping.put(AoCServiceObligatoryTypeEnum.NON_BINDING, EnumAoCServiceObligatoryType.NON_BINDING);

containerMapping.put(EnumAoCServiceObligatoryType.BINDING, AoCServiceObligatoryTypeEnum.BINDING);
containerMapping.put(EnumAoCServiceObligatoryType.NON_BINDING, AoCServiceObligatoryTypeEnum.NON_BINDING);
}

public static final AoCServiceObligatoryTypeEnum getContainerObj(EnumAoCServiceObligatoryType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAoCServiceObligatoryType getStackObj(AoCServiceObligatoryTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AoCServiceObligatoryTypeEnum fromCode(int value){
	return getContainerObj(EnumAoCServiceObligatoryType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAoCServiceObligatoryType.getName(key);
}

public static boolean isValid(int value){
	return EnumAoCServiceObligatoryType.isValid(value);
}

public static int[] keys(){
	return EnumAoCServiceObligatoryType.keys();
}
}
