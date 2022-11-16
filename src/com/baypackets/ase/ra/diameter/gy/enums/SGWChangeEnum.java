package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumSGWChange;

public enum SGWChangeEnum
{
	ACR_Start_due_to_SGW_Change ,
	ACR_Start_NOT_due_to_SGW_Change ;

private static Hashtable<SGWChangeEnum,EnumSGWChange> stackMapping = new Hashtable<SGWChangeEnum,EnumSGWChange>();
private static Hashtable<EnumSGWChange,SGWChangeEnum> containerMapping = new Hashtable<EnumSGWChange,SGWChangeEnum>();

 static {
stackMapping.put(SGWChangeEnum.ACR_Start_due_to_SGW_Change, EnumSGWChange.ACR_Start_due_to_SGW_Change);
stackMapping.put(SGWChangeEnum.ACR_Start_NOT_due_to_SGW_Change, EnumSGWChange.ACR_Start_NOT_due_to_SGW_Change);

containerMapping.put(EnumSGWChange.ACR_Start_due_to_SGW_Change, SGWChangeEnum.ACR_Start_due_to_SGW_Change);
containerMapping.put(EnumSGWChange.ACR_Start_NOT_due_to_SGW_Change, SGWChangeEnum.ACR_Start_NOT_due_to_SGW_Change);
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
