package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumMMBoxStorageRequested;

public enum MMBoxStorageRequestedEnum
{
NO,
YES;

private static Hashtable<MMBoxStorageRequestedEnum,EnumMMBoxStorageRequested> stackMapping = new Hashtable<MMBoxStorageRequestedEnum,EnumMMBoxStorageRequested>();
private static Hashtable<EnumMMBoxStorageRequested,MMBoxStorageRequestedEnum> containerMapping = new Hashtable<EnumMMBoxStorageRequested,MMBoxStorageRequestedEnum>();

 static {
stackMapping.put(MMBoxStorageRequestedEnum.NO, EnumMMBoxStorageRequested.No);
stackMapping.put(MMBoxStorageRequestedEnum.YES, EnumMMBoxStorageRequested.Yes);

containerMapping.put(EnumMMBoxStorageRequested.No, MMBoxStorageRequestedEnum.NO);
containerMapping.put(EnumMMBoxStorageRequested.Yes, MMBoxStorageRequestedEnum.YES);
}

public static final MMBoxStorageRequestedEnum getContainerObj(EnumMMBoxStorageRequested stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumMMBoxStorageRequested getStackObj(MMBoxStorageRequestedEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static MMBoxStorageRequestedEnum fromCode(int value){
	return getContainerObj(EnumMMBoxStorageRequested.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumMMBoxStorageRequested.getName(key);
}

public static boolean isValid(int value){
	return EnumMMBoxStorageRequested.isValid(value);
}

public static int[] keys(){
	return EnumMMBoxStorageRequested.keys();
}
}
