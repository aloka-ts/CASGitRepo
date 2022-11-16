package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumDeliveryReportRequested;

public enum DeliveryReportRequestedEnum
{
NO,
YES;

private static Hashtable<DeliveryReportRequestedEnum,EnumDeliveryReportRequested> stackMapping = new Hashtable<DeliveryReportRequestedEnum,EnumDeliveryReportRequested>();
private static Hashtable<EnumDeliveryReportRequested,DeliveryReportRequestedEnum> containerMapping = new Hashtable<EnumDeliveryReportRequested,DeliveryReportRequestedEnum>();

 static {
stackMapping.put(DeliveryReportRequestedEnum.NO, EnumDeliveryReportRequested.No);
stackMapping.put(DeliveryReportRequestedEnum.YES, EnumDeliveryReportRequested.Yes);

containerMapping.put(EnumDeliveryReportRequested.No, DeliveryReportRequestedEnum.NO);
containerMapping.put(EnumDeliveryReportRequested.Yes, DeliveryReportRequestedEnum.YES);
}

public static final DeliveryReportRequestedEnum getContainerObj(EnumDeliveryReportRequested stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumDeliveryReportRequested getStackObj(DeliveryReportRequestedEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static DeliveryReportRequestedEnum fromCode(int value){
	return getContainerObj(EnumDeliveryReportRequested.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumDeliveryReportRequested.getName(key);
}

public static boolean isValid(int value){
	return EnumDeliveryReportRequested.isValid(value);
}

public static int[] keys(){
	return EnumDeliveryReportRequested.keys();
}
}
