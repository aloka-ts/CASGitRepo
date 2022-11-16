package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumAccountingRealtimeRequired;

public enum AccountingRealtimeRequiredEnum
{
	DELIVER_AND_GRANT ,
	GRANT_AND_LOSE ,
	GRANT_AND_STORE ;

private static Hashtable<AccountingRealtimeRequiredEnum,EnumAccountingRealtimeRequired> stackMapping = new Hashtable<AccountingRealtimeRequiredEnum,EnumAccountingRealtimeRequired>();
private static Hashtable<EnumAccountingRealtimeRequired,AccountingRealtimeRequiredEnum> containerMapping = new Hashtable<EnumAccountingRealtimeRequired,AccountingRealtimeRequiredEnum>();

 static {
stackMapping.put(AccountingRealtimeRequiredEnum.DELIVER_AND_GRANT, EnumAccountingRealtimeRequired.DELIVER_AND_GRANT);
stackMapping.put(AccountingRealtimeRequiredEnum.GRANT_AND_LOSE, EnumAccountingRealtimeRequired.GRANT_AND_LOSE);
stackMapping.put(AccountingRealtimeRequiredEnum.GRANT_AND_STORE, EnumAccountingRealtimeRequired.GRANT_AND_STORE);

containerMapping.put(EnumAccountingRealtimeRequired.DELIVER_AND_GRANT, AccountingRealtimeRequiredEnum.DELIVER_AND_GRANT);
containerMapping.put(EnumAccountingRealtimeRequired.GRANT_AND_LOSE, AccountingRealtimeRequiredEnum.GRANT_AND_LOSE);
containerMapping.put(EnumAccountingRealtimeRequired.GRANT_AND_STORE, AccountingRealtimeRequiredEnum.GRANT_AND_STORE);

}

public static final AccountingRealtimeRequiredEnum getContainerObj(EnumAccountingRealtimeRequired stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumAccountingRealtimeRequired getStackObj(AccountingRealtimeRequiredEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static AccountingRealtimeRequiredEnum fromCode(int value){
	return getContainerObj(EnumAccountingRealtimeRequired.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumAccountingRealtimeRequired.getName(key);
}

public static boolean isValid(int value){
	return EnumAccountingRealtimeRequired.isValid(value);
}

public static int[] keys(){
	return EnumAccountingRealtimeRequired.keys();
}
}

