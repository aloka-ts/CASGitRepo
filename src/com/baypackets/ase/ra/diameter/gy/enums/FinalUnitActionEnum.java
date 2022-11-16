package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumFinalUnitAction;

public enum FinalUnitActionEnum
{
REDIRECT,
RESTRICT_ACCESS,
TERMINATE;

private static Hashtable<FinalUnitActionEnum,EnumFinalUnitAction> stackMapping = new Hashtable<FinalUnitActionEnum,EnumFinalUnitAction>();
private static Hashtable<EnumFinalUnitAction,FinalUnitActionEnum> containerMapping = new Hashtable<EnumFinalUnitAction,FinalUnitActionEnum>();

 static {
stackMapping.put(FinalUnitActionEnum.REDIRECT, EnumFinalUnitAction.REDIRECT);
stackMapping.put(FinalUnitActionEnum.RESTRICT_ACCESS, EnumFinalUnitAction.RESTRICT_ACCESS);
stackMapping.put(FinalUnitActionEnum.TERMINATE, EnumFinalUnitAction.TERMINATE);

containerMapping.put(EnumFinalUnitAction.REDIRECT, FinalUnitActionEnum.REDIRECT);
containerMapping.put(EnumFinalUnitAction.RESTRICT_ACCESS, FinalUnitActionEnum.RESTRICT_ACCESS);
containerMapping.put(EnumFinalUnitAction.TERMINATE, FinalUnitActionEnum.TERMINATE);
}

public static final FinalUnitActionEnum getContainerObj(EnumFinalUnitAction stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumFinalUnitAction getStackObj(FinalUnitActionEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static FinalUnitActionEnum fromCode(int value){
	return getContainerObj(EnumFinalUnitAction.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumFinalUnitAction.getName(key);
}

public static boolean isValid(int value){
	return EnumFinalUnitAction.isValid(value);
}

public static int[] keys(){
	return EnumFinalUnitAction.keys();
}
}
