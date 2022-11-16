package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumMBMS2G3GIndicator;

public enum MBMS2G3GIndicatorEnum
{
NO_2G,
NO_2GAND3G,
NO_3G;

private static Hashtable<MBMS2G3GIndicatorEnum,EnumMBMS2G3GIndicator> stackMapping = new Hashtable<MBMS2G3GIndicatorEnum,EnumMBMS2G3GIndicator>();
private static Hashtable<EnumMBMS2G3GIndicator,MBMS2G3GIndicatorEnum> containerMapping = new Hashtable<EnumMBMS2G3GIndicator,MBMS2G3GIndicatorEnum>();

 static {
stackMapping.put(MBMS2G3GIndicatorEnum.NO_2G, EnumMBMS2G3GIndicator.NO_2G);
stackMapping.put(MBMS2G3GIndicatorEnum.NO_2GAND3G, EnumMBMS2G3GIndicator.NO_2GAND3G);
stackMapping.put(MBMS2G3GIndicatorEnum.NO_3G, EnumMBMS2G3GIndicator.NO_3G);

containerMapping.put(EnumMBMS2G3GIndicator.NO_2G, MBMS2G3GIndicatorEnum.NO_2G);
containerMapping.put(EnumMBMS2G3GIndicator.NO_2GAND3G, MBMS2G3GIndicatorEnum.NO_2GAND3G);
containerMapping.put(EnumMBMS2G3GIndicator.NO_3G, MBMS2G3GIndicatorEnum.NO_3G);
}

public static final MBMS2G3GIndicatorEnum getContainerObj(EnumMBMS2G3GIndicator stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumMBMS2G3GIndicator getStackObj(MBMS2G3GIndicatorEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static MBMS2G3GIndicatorEnum fromCode(int value){
	return getContainerObj(EnumMBMS2G3GIndicator.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumMBMS2G3GIndicator.getName(key);
}

public static boolean isValid(int value){
	return EnumMBMS2G3GIndicator.isValid(value);
}

public static int[] keys(){
	return EnumMBMS2G3GIndicator.keys();
}
}
