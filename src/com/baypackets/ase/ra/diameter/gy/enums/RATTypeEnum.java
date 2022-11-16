package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumLowPriorityIndicator;
import com.traffix.openblox.diameter.gy.generated.enums.EnumRATType;

public enum RATTypeEnum
{
	CDMA2000_1X    ,
	EUTRAN ,
	GAN    ,
	GERAN  ,
	HRPD     ,
	HSPA_EVOLUTION  ,
	UMB      ,
	UTRAN   ,
	WLAN   ;

private static Hashtable<RATTypeEnum,EnumRATType> stackMapping = new Hashtable<RATTypeEnum,EnumRATType>();
private static Hashtable<EnumRATType,RATTypeEnum> containerMapping = new Hashtable<EnumRATType,RATTypeEnum>();

 static {
stackMapping.put(RATTypeEnum.CDMA2000_1X , EnumRATType.CDMA2000_1X );
stackMapping.put(RATTypeEnum.EUTRAN , EnumRATType.EUTRAN );
stackMapping.put(RATTypeEnum.GAN , EnumRATType.GAN );
stackMapping.put(RATTypeEnum.GERAN , EnumRATType.GERAN );
stackMapping.put(RATTypeEnum.HRPD , EnumRATType.HRPD );
stackMapping.put(RATTypeEnum.HSPA_EVOLUTION , EnumRATType.HSPA_EVOLUTION );
stackMapping.put(RATTypeEnum.UMB , EnumRATType.UMB );
stackMapping.put(RATTypeEnum.UTRAN , EnumRATType.UTRAN );
stackMapping.put(RATTypeEnum.HSPA_EVOLUTION , EnumRATType.HSPA_EVOLUTION );

containerMapping.put(EnumRATType.CDMA2000_1X , RATTypeEnum.CDMA2000_1X );
containerMapping.put(EnumRATType.EUTRAN , RATTypeEnum.EUTRAN );
containerMapping.put(EnumRATType.GAN , RATTypeEnum.GAN );
containerMapping.put(EnumRATType.GERAN , RATTypeEnum.GERAN );
containerMapping.put(EnumRATType.HRPD , RATTypeEnum.HRPD );
containerMapping.put(EnumRATType.HSPA_EVOLUTION , RATTypeEnum.HSPA_EVOLUTION );
containerMapping.put(EnumRATType.UMB , RATTypeEnum.UMB );
containerMapping.put(EnumRATType.UTRAN , RATTypeEnum.UTRAN );
containerMapping.put(EnumRATType.WLAN , RATTypeEnum.WLAN );

}

public static final RATTypeEnum getContainerObj(EnumRATType stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumRATType getStackObj(RATTypeEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static RATTypeEnum fromCode(int value){
	return getContainerObj(EnumRATType.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumRATType.getName(key);
}

public static boolean isValid(int value){
	return EnumRATType.isValid(value);
}

public static int[] keys(){
	return EnumRATType.keys();
}
}



