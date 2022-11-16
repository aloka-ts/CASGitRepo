package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumSubscriptionIdType;

public enum SubscriptionIdTypeEnum
{
END_USER_E164,
END_USER_IMSI,
END_USER_NAI,
END_USER_PRIVATE,
END_USER_SIP_URI;

private static Hashtable<SubscriptionIdTypeEnum,EnumSubscriptionIdType> stackMapping = new Hashtable<SubscriptionIdTypeEnum,EnumSubscriptionIdType>();
private static Hashtable<EnumSubscriptionIdType,SubscriptionIdTypeEnum> containerMapping = new Hashtable<EnumSubscriptionIdType,SubscriptionIdTypeEnum>();

 static {
stackMapping.put(SubscriptionIdTypeEnum.END_USER_E164, EnumSubscriptionIdType.END_USER_E164);
stackMapping.put(SubscriptionIdTypeEnum.END_USER_IMSI, EnumSubscriptionIdType.END_USER_IMSI);
stackMapping.put(SubscriptionIdTypeEnum.END_USER_NAI, EnumSubscriptionIdType.END_USER_NAI);
stackMapping.put(SubscriptionIdTypeEnum.END_USER_PRIVATE, EnumSubscriptionIdType.END_USER_PRIVATE);
stackMapping.put(SubscriptionIdTypeEnum.END_USER_SIP_URI, EnumSubscriptionIdType.END_USER_SIP_URI);

containerMapping.put(EnumSubscriptionIdType.END_USER_E164, SubscriptionIdTypeEnum.END_USER_E164);
containerMapping.put(EnumSubscriptionIdType.END_USER_IMSI, SubscriptionIdTypeEnum.END_USER_IMSI);
containerMapping.put(EnumSubscriptionIdType.END_USER_NAI, SubscriptionIdTypeEnum.END_USER_NAI);
containerMapping.put(EnumSubscriptionIdType.END_USER_PRIVATE, SubscriptionIdTypeEnum.END_USER_PRIVATE);
containerMapping.put(EnumSubscriptionIdType.END_USER_SIP_URI, SubscriptionIdTypeEnum.END_USER_SIP_URI);
}

public static final SubscriptionIdTypeEnum getContainerObj(EnumSubscriptionIdType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSubscriptionIdType getStackObj(SubscriptionIdTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SubscriptionIdTypeEnum fromCode(int value){
	return getContainerObj(EnumSubscriptionIdType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSubscriptionIdType.getName(key);
}

public static boolean isValid(int value){
	return EnumSubscriptionIdType.isValid(value);
}

public static int[] keys(){
	return EnumSubscriptionIdType.keys();
}
}
