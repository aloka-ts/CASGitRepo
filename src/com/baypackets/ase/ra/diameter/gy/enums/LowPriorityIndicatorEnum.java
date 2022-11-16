package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumLowPriorityIndicator;

public enum LowPriorityIndicatorEnum
{
	MBMSGWAddressAVP   ,
	NO,
	YES ;

private static Hashtable<LowPriorityIndicatorEnum,EnumLowPriorityIndicator> stackMapping = new Hashtable<LowPriorityIndicatorEnum,EnumLowPriorityIndicator>();
private static Hashtable<EnumLowPriorityIndicator,LowPriorityIndicatorEnum> containerMapping = new Hashtable<EnumLowPriorityIndicator,LowPriorityIndicatorEnum>();

 static {
stackMapping.put(LowPriorityIndicatorEnum.MBMSGWAddressAVP , EnumLowPriorityIndicator.MBMSGWAddressAVP );
stackMapping.put(LowPriorityIndicatorEnum.NO , EnumLowPriorityIndicator.NO );
stackMapping.put(LowPriorityIndicatorEnum.YES , EnumLowPriorityIndicator.YES );

containerMapping.put(EnumLowPriorityIndicator.MBMSGWAddressAVP , LowPriorityIndicatorEnum.MBMSGWAddressAVP );
containerMapping.put(EnumLowPriorityIndicator.NO , LowPriorityIndicatorEnum.NO );
containerMapping.put(EnumLowPriorityIndicator.YES , LowPriorityIndicatorEnum.YES );

}

public static final LowPriorityIndicatorEnum getContainerObj(EnumLowPriorityIndicator stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumLowPriorityIndicator getStackObj(LowPriorityIndicatorEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static LowPriorityIndicatorEnum fromCode(int value){
	return getContainerObj(EnumLowPriorityIndicator.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumLowPriorityIndicator.getName(key);
}

public static boolean isValid(int value){
	return EnumLowPriorityIndicator.isValid(value);
}

public static int[] keys(){
	return EnumLowPriorityIndicator.keys();
}
}


