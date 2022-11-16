package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumClassIdentifier;

public enum ClassIdentifierEnum
{
ADVERTISEMENT,
AUTO,
INFORMATIONAL,
PERSONAL;

private static Hashtable<ClassIdentifierEnum,EnumClassIdentifier> stackMapping = new Hashtable<ClassIdentifierEnum,EnumClassIdentifier>();
private static Hashtable<EnumClassIdentifier,ClassIdentifierEnum> containerMapping = new Hashtable<EnumClassIdentifier,ClassIdentifierEnum>();

 static {
stackMapping.put(ClassIdentifierEnum.ADVERTISEMENT, EnumClassIdentifier.Advertisement);
stackMapping.put(ClassIdentifierEnum.AUTO, EnumClassIdentifier.Auto);
stackMapping.put(ClassIdentifierEnum.INFORMATIONAL, EnumClassIdentifier.Informational);
stackMapping.put(ClassIdentifierEnum.PERSONAL, EnumClassIdentifier.Personal);

containerMapping.put(EnumClassIdentifier.Advertisement, ClassIdentifierEnum.ADVERTISEMENT);
containerMapping.put(EnumClassIdentifier.Auto, ClassIdentifierEnum.AUTO);
containerMapping.put(EnumClassIdentifier.Informational, ClassIdentifierEnum.INFORMATIONAL);
containerMapping.put(EnumClassIdentifier.Personal, ClassIdentifierEnum.PERSONAL);
}

public static final ClassIdentifierEnum getContainerObj(EnumClassIdentifier stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumClassIdentifier getStackObj(ClassIdentifierEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static ClassIdentifierEnum fromCode(int value){
	return getContainerObj(EnumClassIdentifier.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumClassIdentifier.getName(key);
}

public static boolean isValid(int value){
	return EnumClassIdentifier.isValid(value);
}

public static int[] keys(){
	return EnumClassIdentifier.keys();
}
}
