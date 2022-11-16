package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.ro.generated.enums.Enum3GPPPDPType;


public enum PDPType3GPPEnum
{
	IPV4,
	IPV6,
	PPP;

	private static Hashtable<PDPType3GPPEnum,Enum3GPPPDPType> stackMapping = new Hashtable<PDPType3GPPEnum,Enum3GPPPDPType>();
	private static Hashtable<Enum3GPPPDPType,PDPType3GPPEnum> containerMapping = new Hashtable<Enum3GPPPDPType,PDPType3GPPEnum>();

	static {
		stackMapping.put(PDPType3GPPEnum.IPV4, Enum3GPPPDPType.IPv4);
		stackMapping.put(PDPType3GPPEnum.IPV6, Enum3GPPPDPType.IPv6);
		stackMapping.put(PDPType3GPPEnum.PPP, Enum3GPPPDPType.PPP);

		containerMapping.put(Enum3GPPPDPType.IPv4, PDPType3GPPEnum.IPV4);
		containerMapping.put(Enum3GPPPDPType.IPv6, PDPType3GPPEnum.IPV6);
		containerMapping.put(Enum3GPPPDPType.PPP, PDPType3GPPEnum.PPP);
	}

	public static final PDPType3GPPEnum getContainerObj(Enum3GPPPDPType stkEnum){
		return containerMapping.get(stkEnum);
	}

	public static final Enum3GPPPDPType getStackObj(PDPType3GPPEnum cntrEnum){
		return stackMapping.get(cntrEnum);
	}

	public static PDPType3GPPEnum fromCode(int value){
		return getContainerObj(Enum3GPPPDPType.fromCode(value));
	}

	public static java.lang.String getName(int key){
		return Enum3GPPPDPType.getName(key);
	}

	public static boolean isValid(int value){
		return Enum3GPPPDPType.isValid(value);
	}

	public static int[] keys(){
		return Enum3GPPPDPType.keys();
	}
}
