package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumAoCFormat;

public enum AoCFormatEnum
{
CAI,
MONETARY,
NON_MONETARY;

private static Hashtable<AoCFormatEnum,EnumAoCFormat> stackMapping = new Hashtable<AoCFormatEnum,EnumAoCFormat>();
private static Hashtable<EnumAoCFormat,AoCFormatEnum> containerMapping = new Hashtable<EnumAoCFormat,AoCFormatEnum>();

 static {
stackMapping.put(AoCFormatEnum.CAI, EnumAoCFormat.CAI);
stackMapping.put(AoCFormatEnum.MONETARY, EnumAoCFormat.MONETARY);
stackMapping.put(AoCFormatEnum.NON_MONETARY, EnumAoCFormat.NON_MONETARY);

containerMapping.put(EnumAoCFormat.CAI, AoCFormatEnum.CAI);
containerMapping.put(EnumAoCFormat.MONETARY, AoCFormatEnum.MONETARY);
containerMapping.put(EnumAoCFormat.NON_MONETARY, AoCFormatEnum.NON_MONETARY);
}

public static final AoCFormatEnum getContainerObj(EnumAoCFormat stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAoCFormat getStackObj(AoCFormatEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AoCFormatEnum fromCode(int value){
	return getContainerObj(EnumAoCFormat.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAoCFormat.getName(key);
}

public static boolean isValid(int value){
	return EnumAoCFormat.isValid(value);
}

public static int[] keys(){
	return EnumAoCFormat.keys();
}
}
