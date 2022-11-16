package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumOriginator;

public enum OriginatorEnum
{
CALLEDPARTY,
CALLINGPARTY;

private static Hashtable<OriginatorEnum,EnumOriginator> stackMapping = new Hashtable<OriginatorEnum,EnumOriginator>();
private static Hashtable<EnumOriginator,OriginatorEnum> containerMapping = new Hashtable<EnumOriginator,OriginatorEnum>();

 static {
stackMapping.put(OriginatorEnum.CALLEDPARTY, EnumOriginator.CalledParty);
stackMapping.put(OriginatorEnum.CALLINGPARTY, EnumOriginator.CallingParty);

containerMapping.put(EnumOriginator.CalledParty, OriginatorEnum.CALLEDPARTY);
containerMapping.put(EnumOriginator.CallingParty, OriginatorEnum.CALLINGPARTY);
}

public static final OriginatorEnum getContainerObj(EnumOriginator stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumOriginator getStackObj(OriginatorEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static OriginatorEnum fromCode(int value){
	return getContainerObj(EnumOriginator.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumOriginator.getName(key);
}

public static boolean isValid(int value){
	return EnumOriginator.isValid(value);
}

public static int[] keys(){
	return EnumOriginator.keys();
}
}
