package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumSGWChange;

public enum SGWChangeEnum
{
ACRSTARTDUESWGCHANGE,
ACRSTARTNOTDUESWGCHANGE;

private static Hashtable<SGWChangeEnum,EnumSGWChange> stackMapping = new Hashtable<SGWChangeEnum,EnumSGWChange>();
private static Hashtable<EnumSGWChange,SGWChangeEnum> containerMapping = new Hashtable<EnumSGWChange,SGWChangeEnum>();

 static {
stackMapping.put(SGWChangeEnum.ACRSTARTDUESWGCHANGE, EnumSGWChange.ACRStartDueSWGChange);
stackMapping.put(SGWChangeEnum.ACRSTARTNOTDUESWGCHANGE, EnumSGWChange.ACRStartNotDueSWGChange);

containerMapping.put(EnumSGWChange.ACRStartDueSWGChange, SGWChangeEnum.ACRSTARTDUESWGCHANGE);
containerMapping.put(EnumSGWChange.ACRStartNotDueSWGChange, SGWChangeEnum.ACRSTARTNOTDUESWGCHANGE);
}

public static final SGWChangeEnum getContainerObj(EnumSGWChange stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumSGWChange getStackObj(SGWChangeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static SGWChangeEnum fromCode(int value){
	return getContainerObj(EnumSGWChange.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumSGWChange.getName(key);
}

public static boolean isValid(int value){
	return EnumSGWChange.isValid(value);
}

public static int[] keys(){
	return EnumSGWChange.keys();
}
}
