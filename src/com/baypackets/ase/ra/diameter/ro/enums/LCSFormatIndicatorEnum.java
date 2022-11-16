package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumLCSFormatIndicator;

public enum LCSFormatIndicatorEnum
{
EMAIL_ADDRESS,
LOGICAL_NAME,
MSISDN,
URL;

private static Hashtable<LCSFormatIndicatorEnum,EnumLCSFormatIndicator> stackMapping = new Hashtable<LCSFormatIndicatorEnum,EnumLCSFormatIndicator>();
private static Hashtable<EnumLCSFormatIndicator,LCSFormatIndicatorEnum> containerMapping = new Hashtable<EnumLCSFormatIndicator,LCSFormatIndicatorEnum>();

 static {
stackMapping.put(LCSFormatIndicatorEnum.EMAIL_ADDRESS, EnumLCSFormatIndicator.EMAIL_ADDRESS);
stackMapping.put(LCSFormatIndicatorEnum.LOGICAL_NAME, EnumLCSFormatIndicator.LOGICAL_NAME);
stackMapping.put(LCSFormatIndicatorEnum.MSISDN, EnumLCSFormatIndicator.MSISDN);
stackMapping.put(LCSFormatIndicatorEnum.URL, EnumLCSFormatIndicator.URL);

containerMapping.put(EnumLCSFormatIndicator.EMAIL_ADDRESS, LCSFormatIndicatorEnum.EMAIL_ADDRESS);
containerMapping.put(EnumLCSFormatIndicator.LOGICAL_NAME, LCSFormatIndicatorEnum.LOGICAL_NAME);
containerMapping.put(EnumLCSFormatIndicator.MSISDN, LCSFormatIndicatorEnum.MSISDN);
containerMapping.put(EnumLCSFormatIndicator.URL, LCSFormatIndicatorEnum.URL);
}

public static final LCSFormatIndicatorEnum getContainerObj(EnumLCSFormatIndicator stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumLCSFormatIndicator getStackObj(LCSFormatIndicatorEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static LCSFormatIndicatorEnum fromCode(int value){
	return getContainerObj(EnumLCSFormatIndicator.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumLCSFormatIndicator.getName(key);
}

public static boolean isValid(int value){
	return EnumLCSFormatIndicator.isValid(value);
}

public static int[] keys(){
	return EnumLCSFormatIndicator.keys();
}
}
