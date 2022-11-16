package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumDRMContent;

public enum DRMContentEnum
{
NO,
YES;

private static Hashtable<DRMContentEnum,EnumDRMContent> stackMapping = new Hashtable<DRMContentEnum,EnumDRMContent>();
private static Hashtable<EnumDRMContent,DRMContentEnum> containerMapping = new Hashtable<EnumDRMContent,DRMContentEnum>();

 static {
stackMapping.put(DRMContentEnum.NO, EnumDRMContent.No);
stackMapping.put(DRMContentEnum.YES, EnumDRMContent.Yes);

containerMapping.put(EnumDRMContent.No, DRMContentEnum.NO);
containerMapping.put(EnumDRMContent.Yes, DRMContentEnum.YES);
}

public static final DRMContentEnum getContainerObj(EnumDRMContent stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumDRMContent getStackObj(DRMContentEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static DRMContentEnum fromCode(int value){
	return getContainerObj(EnumDRMContent.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumDRMContent.getName(key);
}

public static boolean isValid(int value){
	return EnumDRMContent.isValid(value);
}

public static int[] keys(){
	return EnumDRMContent.keys();
}
}
