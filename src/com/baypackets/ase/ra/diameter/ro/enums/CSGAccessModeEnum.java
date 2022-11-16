package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumCSGAccessMode;

public enum CSGAccessModeEnum
{
CLOSEDMODE,
HYBRIDMODE;

private static Hashtable<CSGAccessModeEnum,EnumCSGAccessMode> stackMapping = new Hashtable<CSGAccessModeEnum,EnumCSGAccessMode>();
private static Hashtable<EnumCSGAccessMode,CSGAccessModeEnum> containerMapping = new Hashtable<EnumCSGAccessMode,CSGAccessModeEnum>();

 static {
stackMapping.put(CSGAccessModeEnum.CLOSEDMODE, EnumCSGAccessMode.ClosedMode);
stackMapping.put(CSGAccessModeEnum.HYBRIDMODE, EnumCSGAccessMode.HybridMode);

containerMapping.put(EnumCSGAccessMode.ClosedMode, CSGAccessModeEnum.CLOSEDMODE);
containerMapping.put(EnumCSGAccessMode.HybridMode, CSGAccessModeEnum.HYBRIDMODE);
}

public static final CSGAccessModeEnum getContainerObj(EnumCSGAccessMode stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCSGAccessMode getStackObj(CSGAccessModeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CSGAccessModeEnum fromCode(int value){
	return getContainerObj(EnumCSGAccessMode.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCSGAccessMode.getName(key);
}

public static boolean isValid(int value){
	return EnumCSGAccessMode.isValid(value);
}

public static int[] keys(){
	return EnumCSGAccessMode.keys();
}
}
