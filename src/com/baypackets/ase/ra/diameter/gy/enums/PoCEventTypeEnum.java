package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumPoCEventType;

public enum PoCEventTypeEnum
{
EARLYSSESSIONSETTINGUPEVENT,
INSTANTPPERSONALAALERTEVENT,
NORMAL,
POCGROUPADVERTISEMENTEVENT,
POCTALKBURST;

private static Hashtable<PoCEventTypeEnum,EnumPoCEventType> stackMapping = new Hashtable<PoCEventTypeEnum,EnumPoCEventType>();
private static Hashtable<EnumPoCEventType,PoCEventTypeEnum> containerMapping = new Hashtable<EnumPoCEventType,PoCEventTypeEnum>();

 static {
stackMapping.put(PoCEventTypeEnum.EARLYSSESSIONSETTINGUPEVENT, EnumPoCEventType.EarlySsessionSettingUpEvent);
stackMapping.put(PoCEventTypeEnum.INSTANTPPERSONALAALERTEVENT, EnumPoCEventType.InstantPpersonalAalertEvent);
stackMapping.put(PoCEventTypeEnum.NORMAL, EnumPoCEventType.Normal);
stackMapping.put(PoCEventTypeEnum.POCGROUPADVERTISEMENTEVENT, EnumPoCEventType.PoCGroupAdvertisementEvent);
stackMapping.put(PoCEventTypeEnum.POCTALKBURST, EnumPoCEventType.PoCTalkBurst);

containerMapping.put(EnumPoCEventType.EarlySsessionSettingUpEvent, PoCEventTypeEnum.EARLYSSESSIONSETTINGUPEVENT);
containerMapping.put(EnumPoCEventType.InstantPpersonalAalertEvent, PoCEventTypeEnum.INSTANTPPERSONALAALERTEVENT);
containerMapping.put(EnumPoCEventType.Normal, PoCEventTypeEnum.NORMAL);
containerMapping.put(EnumPoCEventType.PoCGroupAdvertisementEvent, PoCEventTypeEnum.POCGROUPADVERTISEMENTEVENT);
containerMapping.put(EnumPoCEventType.PoCTalkBurst, PoCEventTypeEnum.POCTALKBURST);
}

public static final PoCEventTypeEnum getContainerObj(EnumPoCEventType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumPoCEventType getStackObj(PoCEventTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static PoCEventTypeEnum fromCode(int value){
	return getContainerObj(EnumPoCEventType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumPoCEventType.getName(key);
}

public static boolean isValid(int value){
	return EnumPoCEventType.isValid(value);
}

public static int[] keys(){
	return EnumPoCEventType.keys();
}
}
