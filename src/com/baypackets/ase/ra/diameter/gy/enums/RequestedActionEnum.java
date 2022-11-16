package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumRequestedAction;

public enum RequestedActionEnum
{
CHECK_BALANCE,
DIRECT_DEBITING,
PRICE_ENQUIRY,
REFUND_ACCOUNT;

private static Hashtable<RequestedActionEnum,EnumRequestedAction> stackMapping = new Hashtable<RequestedActionEnum,EnumRequestedAction>();
private static Hashtable<EnumRequestedAction,RequestedActionEnum> containerMapping = new Hashtable<EnumRequestedAction,RequestedActionEnum>();

 static {
stackMapping.put(RequestedActionEnum.CHECK_BALANCE, EnumRequestedAction.CHECK_BALANCE);
stackMapping.put(RequestedActionEnum.DIRECT_DEBITING, EnumRequestedAction.DIRECT_DEBITING);
stackMapping.put(RequestedActionEnum.PRICE_ENQUIRY, EnumRequestedAction.PRICE_ENQUIRY);
stackMapping.put(RequestedActionEnum.REFUND_ACCOUNT, EnumRequestedAction.REFUND_ACCOUNT);

containerMapping.put(EnumRequestedAction.CHECK_BALANCE, RequestedActionEnum.CHECK_BALANCE);
containerMapping.put(EnumRequestedAction.DIRECT_DEBITING, RequestedActionEnum.DIRECT_DEBITING);
containerMapping.put(EnumRequestedAction.PRICE_ENQUIRY, RequestedActionEnum.PRICE_ENQUIRY);
containerMapping.put(EnumRequestedAction.REFUND_ACCOUNT, RequestedActionEnum.REFUND_ACCOUNT);
}

public static final RequestedActionEnum getContainerObj(EnumRequestedAction stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumRequestedAction getStackObj(RequestedActionEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static RequestedActionEnum fromCode(int value){
	return getContainerObj(EnumRequestedAction.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumRequestedAction.getName(key);
}

public static boolean isValid(int value){
	return EnumRequestedAction.isValid(value);
}

public static int[] keys(){
	return EnumRequestedAction.keys();
}
}
