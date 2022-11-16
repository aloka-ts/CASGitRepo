package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumTranscoderInsertedIndication;

public enum TranscoderInsertedIndicationEnum
{
	TranscoderInserted     ,
	TranscoderNotInserted    ;

private static Hashtable<TranscoderInsertedIndicationEnum,EnumTranscoderInsertedIndication> stackMapping = new Hashtable<TranscoderInsertedIndicationEnum,EnumTranscoderInsertedIndication>();
private static Hashtable<EnumTranscoderInsertedIndication,TranscoderInsertedIndicationEnum> containerMapping = new Hashtable<EnumTranscoderInsertedIndication,TranscoderInsertedIndicationEnum>();

 static {
stackMapping.put(TranscoderInsertedIndicationEnum.TranscoderInserted , EnumTranscoderInsertedIndication.TranscoderInserted );
stackMapping.put(TranscoderInsertedIndicationEnum.TranscoderNotInserted , EnumTranscoderInsertedIndication.TranscoderNotInserted );


containerMapping.put(EnumTranscoderInsertedIndication.TranscoderInserted , TranscoderInsertedIndicationEnum.TranscoderInserted );
containerMapping.put(EnumTranscoderInsertedIndication.TranscoderNotInserted , TranscoderInsertedIndicationEnum.TranscoderNotInserted );

}

public static final TranscoderInsertedIndicationEnum getContainerObj(EnumTranscoderInsertedIndication stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumTranscoderInsertedIndication getStackObj(TranscoderInsertedIndicationEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static TranscoderInsertedIndicationEnum fromCode(int value){
	return getContainerObj(EnumTranscoderInsertedIndication.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumTranscoderInsertedIndication.getName(key);
}

public static boolean isValid(int value){
	return EnumTranscoderInsertedIndication.isValid(value);
}

public static int[] keys(){
	return EnumTranscoderInsertedIndication.keys();
}
}
