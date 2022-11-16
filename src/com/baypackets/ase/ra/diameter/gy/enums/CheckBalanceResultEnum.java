package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumCheckBalanceResult;

public enum CheckBalanceResultEnum
{
	ENOUGH_CREDIT  ,
	NO_CREDIT ;

private static Hashtable<CheckBalanceResultEnum,EnumCheckBalanceResult> stackMapping = new Hashtable<CheckBalanceResultEnum,EnumCheckBalanceResult>();
private static Hashtable<EnumCheckBalanceResult,CheckBalanceResultEnum> containerMapping = new Hashtable<EnumCheckBalanceResult,CheckBalanceResultEnum>();

 static {
stackMapping.put(CheckBalanceResultEnum.ENOUGH_CREDIT, EnumCheckBalanceResult.ENOUGH_CREDIT);
stackMapping.put(CheckBalanceResultEnum.NO_CREDIT, EnumCheckBalanceResult.NO_CREDIT);

containerMapping.put(EnumCheckBalanceResult.ENOUGH_CREDIT, CheckBalanceResultEnum.ENOUGH_CREDIT);
containerMapping.put(EnumCheckBalanceResult.NO_CREDIT, CheckBalanceResultEnum.NO_CREDIT);

}

public static final CheckBalanceResultEnum getContainerObj(EnumCheckBalanceResult stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCheckBalanceResult getStackObj(CheckBalanceResultEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CheckBalanceResultEnum fromCode(int value){
	return getContainerObj(EnumCheckBalanceResult.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCheckBalanceResult.getName(key);
}

public static boolean isValid(int value){
	return EnumCheckBalanceResult.isValid(value);
}

public static int[] keys(){
	return EnumCheckBalanceResult.keys();
}
}
