package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumSDPType;

public enum SDPTypeEnum
{
SDPANSWER,
SDPOFFER;

private static Hashtable<SDPTypeEnum,EnumSDPType> stackMapping = new Hashtable<SDPTypeEnum,EnumSDPType>();
private static Hashtable<EnumSDPType,SDPTypeEnum> containerMapping = new Hashtable<EnumSDPType,SDPTypeEnum>();

 static {
stackMapping.put(SDPTypeEnum.SDPANSWER, EnumSDPType.SDPAnswer);
stackMapping.put(SDPTypeEnum.SDPOFFER, EnumSDPType.SDPOffer);

containerMapping.put(EnumSDPType.SDPAnswer, SDPTypeEnum.SDPANSWER);
containerMapping.put(EnumSDPType.SDPOffer, SDPTypeEnum.SDPOFFER);
}

public static final SDPTypeEnum getContainerObj(EnumSDPType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSDPType getStackObj(SDPTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SDPTypeEnum fromCode(int value){
	return getContainerObj(EnumSDPType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSDPType.getName(key);
}

public static boolean isValid(int value){
	return EnumSDPType.isValid(value);
}

public static int[] keys(){
	return EnumSDPType.keys();
}
}
