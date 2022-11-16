package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumIPRealmDefaultIndication;

public enum IPRealmDefaultIndicationEnum
{
	DefaultIPRealmNotused ,
	DefaultIPrealmused  ;

private static Hashtable<IPRealmDefaultIndicationEnum,EnumIPRealmDefaultIndication> stackMapping = new Hashtable<IPRealmDefaultIndicationEnum,EnumIPRealmDefaultIndication>();
private static Hashtable<EnumIPRealmDefaultIndication,IPRealmDefaultIndicationEnum> containerMapping = new Hashtable<EnumIPRealmDefaultIndication,IPRealmDefaultIndicationEnum>();

 static {
stackMapping.put(IPRealmDefaultIndicationEnum.DefaultIPRealmNotused , EnumIPRealmDefaultIndication.DefaultIPRealmNotused );
stackMapping.put(IPRealmDefaultIndicationEnum.DefaultIPrealmused , EnumIPRealmDefaultIndication.DefaultIPrealmused );

containerMapping.put(EnumIPRealmDefaultIndication.DefaultIPRealmNotused , IPRealmDefaultIndicationEnum.DefaultIPRealmNotused );
containerMapping.put(EnumIPRealmDefaultIndication.DefaultIPrealmused , IPRealmDefaultIndicationEnum.DefaultIPrealmused );

}

public static final IPRealmDefaultIndicationEnum getContainerObj(EnumIPRealmDefaultIndication stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumIPRealmDefaultIndication getStackObj(IPRealmDefaultIndicationEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static IPRealmDefaultIndicationEnum fromCode(int value){
	return getContainerObj(EnumIPRealmDefaultIndication.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumIPRealmDefaultIndication.getName(key);
}

public static boolean isValid(int value){
	return EnumIPRealmDefaultIndication.isValid(value);
}

public static int[] keys(){
	return EnumIPRealmDefaultIndication.keys();
}
}
