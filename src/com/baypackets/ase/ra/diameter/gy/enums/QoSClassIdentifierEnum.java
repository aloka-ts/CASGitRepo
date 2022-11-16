package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumQoSClassIdentifier;

public enum QoSClassIdentifierEnum
{
BACKGROUND,
CONVERSATIONALSPEEC,
CONVERSATIONALUNKNOWN,
INTERACTIVETHP1,
INTERACTIVETHP1SINGALINGINDICATION,
INTERACTIVETHP2,
INTERACTIVETHP3,
STREAMINGSPEEC,
STREAMINGUNKNOWN;

private static Hashtable<QoSClassIdentifierEnum,EnumQoSClassIdentifier> stackMapping = new Hashtable<QoSClassIdentifierEnum,EnumQoSClassIdentifier>();
private static Hashtable<EnumQoSClassIdentifier,QoSClassIdentifierEnum> containerMapping = new Hashtable<EnumQoSClassIdentifier,QoSClassIdentifierEnum>();

 static {
stackMapping.put(QoSClassIdentifierEnum.BACKGROUND, EnumQoSClassIdentifier.Background);
stackMapping.put(QoSClassIdentifierEnum.CONVERSATIONALSPEEC, EnumQoSClassIdentifier.ConversationalSpeec);
stackMapping.put(QoSClassIdentifierEnum.CONVERSATIONALUNKNOWN, EnumQoSClassIdentifier.ConversationalUnknown);
stackMapping.put(QoSClassIdentifierEnum.INTERACTIVETHP1, EnumQoSClassIdentifier.InteractiveTHP1);
stackMapping.put(QoSClassIdentifierEnum.INTERACTIVETHP1SINGALINGINDICATION, EnumQoSClassIdentifier.InteractiveTHP1SingalingIndication);
stackMapping.put(QoSClassIdentifierEnum.INTERACTIVETHP2, EnumQoSClassIdentifier.InteractiveTHP2);
stackMapping.put(QoSClassIdentifierEnum.INTERACTIVETHP3, EnumQoSClassIdentifier.InteractiveTHP3);
stackMapping.put(QoSClassIdentifierEnum.STREAMINGSPEEC, EnumQoSClassIdentifier.StreamingSpeec);
stackMapping.put(QoSClassIdentifierEnum.STREAMINGUNKNOWN, EnumQoSClassIdentifier.StreamingUnknown);

containerMapping.put(EnumQoSClassIdentifier.Background, QoSClassIdentifierEnum.BACKGROUND);
containerMapping.put(EnumQoSClassIdentifier.ConversationalSpeec, QoSClassIdentifierEnum.CONVERSATIONALSPEEC);
containerMapping.put(EnumQoSClassIdentifier.ConversationalUnknown, QoSClassIdentifierEnum.CONVERSATIONALUNKNOWN);
containerMapping.put(EnumQoSClassIdentifier.InteractiveTHP1, QoSClassIdentifierEnum.INTERACTIVETHP1);
containerMapping.put(EnumQoSClassIdentifier.InteractiveTHP1SingalingIndication, QoSClassIdentifierEnum.INTERACTIVETHP1SINGALINGINDICATION);
containerMapping.put(EnumQoSClassIdentifier.InteractiveTHP2, QoSClassIdentifierEnum.INTERACTIVETHP2);
containerMapping.put(EnumQoSClassIdentifier.InteractiveTHP3, QoSClassIdentifierEnum.INTERACTIVETHP3);
containerMapping.put(EnumQoSClassIdentifier.StreamingSpeec, QoSClassIdentifierEnum.STREAMINGSPEEC);
containerMapping.put(EnumQoSClassIdentifier.StreamingUnknown, QoSClassIdentifierEnum.STREAMINGUNKNOWN);
}

public static final QoSClassIdentifierEnum getContainerObj(EnumQoSClassIdentifier stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumQoSClassIdentifier getStackObj(QoSClassIdentifierEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static QoSClassIdentifierEnum fromCode(int value){
	return getContainerObj(EnumQoSClassIdentifier.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumQoSClassIdentifier.getName(key);
}

public static boolean isValid(int value){
	return EnumQoSClassIdentifier.isValid(value);
}

public static int[] keys(){
	return EnumQoSClassIdentifier.keys();
}
}
