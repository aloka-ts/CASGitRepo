package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumPSAppendFreeFormatData;

public enum PSAppendFreeFormatDataEnum
{
APPEND,
OVERWRITE;

private static Hashtable<PSAppendFreeFormatDataEnum,EnumPSAppendFreeFormatData> stackMapping = new Hashtable<PSAppendFreeFormatDataEnum,EnumPSAppendFreeFormatData>();
private static Hashtable<EnumPSAppendFreeFormatData,PSAppendFreeFormatDataEnum> containerMapping = new Hashtable<EnumPSAppendFreeFormatData,PSAppendFreeFormatDataEnum>();

 static {
stackMapping.put(PSAppendFreeFormatDataEnum.APPEND, EnumPSAppendFreeFormatData.Append);
stackMapping.put(PSAppendFreeFormatDataEnum.OVERWRITE, EnumPSAppendFreeFormatData.Overwrite);

containerMapping.put(EnumPSAppendFreeFormatData.Append, PSAppendFreeFormatDataEnum.APPEND);
containerMapping.put(EnumPSAppendFreeFormatData.Overwrite, PSAppendFreeFormatDataEnum.OVERWRITE);
}

public static final PSAppendFreeFormatDataEnum getContainerObj(EnumPSAppendFreeFormatData stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPSAppendFreeFormatData getStackObj(PSAppendFreeFormatDataEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PSAppendFreeFormatDataEnum fromCode(int value){
	return getContainerObj(EnumPSAppendFreeFormatData.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPSAppendFreeFormatData.getName(key);
}

public static boolean isValid(int value){
	return EnumPSAppendFreeFormatData.isValid(value);
}

public static int[] keys(){
	return EnumPSAppendFreeFormatData.keys();
}
}
