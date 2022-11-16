package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumLocalGWInsertedIndication;

public enum LocalGWInsertedIndicationEnum
{
	LocalGWInserted  ,
	LocalGWNotInserted   ;

private static Hashtable<LocalGWInsertedIndicationEnum,EnumLocalGWInsertedIndication> stackMapping = new Hashtable<LocalGWInsertedIndicationEnum,EnumLocalGWInsertedIndication>();
private static Hashtable<EnumLocalGWInsertedIndication,LocalGWInsertedIndicationEnum> containerMapping = new Hashtable<EnumLocalGWInsertedIndication,LocalGWInsertedIndicationEnum>();

 static {
stackMapping.put(LocalGWInsertedIndicationEnum.LocalGWInserted , EnumLocalGWInsertedIndication.LocalGWInserted );
stackMapping.put(LocalGWInsertedIndicationEnum.LocalGWNotInserted , EnumLocalGWInsertedIndication.LocalGWNotInserted );

containerMapping.put(EnumLocalGWInsertedIndication.LocalGWInserted , LocalGWInsertedIndicationEnum.LocalGWInserted );
containerMapping.put(EnumLocalGWInsertedIndication.LocalGWNotInserted , LocalGWInsertedIndicationEnum.LocalGWNotInserted );

}

public static final LocalGWInsertedIndicationEnum getContainerObj(EnumLocalGWInsertedIndication stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumLocalGWInsertedIndication getStackObj(LocalGWInsertedIndicationEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static LocalGWInsertedIndicationEnum fromCode(int value){
	return getContainerObj(EnumLocalGWInsertedIndication.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumLocalGWInsertedIndication.getName(key);
}

public static boolean isValid(int value){
	return EnumLocalGWInsertedIndication.isValid(value);
}

public static int[] keys(){
	return EnumLocalGWInsertedIndication.keys();
}
}

