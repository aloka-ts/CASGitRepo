package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumCCSessionFailover;

public enum CCSessionFailoverEnum
{
FAILOVER_NOT_SUPPORTED,
FAILOVER_SUPPORTED;

private static Hashtable<CCSessionFailoverEnum,EnumCCSessionFailover> stackMapping = new Hashtable<CCSessionFailoverEnum,EnumCCSessionFailover>();
private static Hashtable<EnumCCSessionFailover,CCSessionFailoverEnum> containerMapping = new Hashtable<EnumCCSessionFailover,CCSessionFailoverEnum>();

 static {
stackMapping.put(CCSessionFailoverEnum.FAILOVER_NOT_SUPPORTED, EnumCCSessionFailover.FAILOVER_NOT_SUPPORTED);
stackMapping.put(CCSessionFailoverEnum.FAILOVER_SUPPORTED, EnumCCSessionFailover.FAILOVER_SUPPORTED);

containerMapping.put(EnumCCSessionFailover.FAILOVER_NOT_SUPPORTED, CCSessionFailoverEnum.FAILOVER_NOT_SUPPORTED);
containerMapping.put(EnumCCSessionFailover.FAILOVER_SUPPORTED, CCSessionFailoverEnum.FAILOVER_SUPPORTED);
}

public static final CCSessionFailoverEnum getContainerObj(EnumCCSessionFailover stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumCCSessionFailover getStackObj(CCSessionFailoverEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static CCSessionFailoverEnum fromCode(int value){
	return getContainerObj(EnumCCSessionFailover.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumCCSessionFailover.getName(key);
}

public static boolean isValid(int value){
	return EnumCCSessionFailover.isValid(value);
}

public static int[] keys(){
	return EnumCCSessionFailover.keys();
}
}
