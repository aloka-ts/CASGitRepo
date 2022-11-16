package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumMultipleServicesIndicator;

public enum MultipleServicesIndicatorEnum
{
	MULTIPLE_SERVICES_NOT_SUPPORTED,
	MULTIPLE_SERVICES_SUPPORTED;

	private static Hashtable<MultipleServicesIndicatorEnum,EnumMultipleServicesIndicator> stackMapping = new Hashtable<MultipleServicesIndicatorEnum,EnumMultipleServicesIndicator>();
	private static Hashtable<EnumMultipleServicesIndicator,MultipleServicesIndicatorEnum> containerMapping = new Hashtable<EnumMultipleServicesIndicator,MultipleServicesIndicatorEnum>();

	static {
		stackMapping.put(MultipleServicesIndicatorEnum.MULTIPLE_SERVICES_NOT_SUPPORTED, EnumMultipleServicesIndicator.MULTIPLE_SERVICES_NOT_SUPPORTED);
		stackMapping.put(MultipleServicesIndicatorEnum.MULTIPLE_SERVICES_SUPPORTED, EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED);

		containerMapping.put(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_NOT_SUPPORTED, MultipleServicesIndicatorEnum.MULTIPLE_SERVICES_NOT_SUPPORTED);
		containerMapping.put(EnumMultipleServicesIndicator.MULTIPLE_SERVICES_SUPPORTED, MultipleServicesIndicatorEnum.MULTIPLE_SERVICES_SUPPORTED);
	}

	public static MultipleServicesIndicatorEnum getContainerObj(EnumMultipleServicesIndicator stkEnum){
		return containerMapping.get(stkEnum);
	}

	public static EnumMultipleServicesIndicator getStackObj(MultipleServicesIndicatorEnum cntrEnum){
		return stackMapping.get(cntrEnum);
	}

	public static MultipleServicesIndicatorEnum fromCode(int value){
		return getContainerObj(EnumMultipleServicesIndicator.fromCode(value));
	}

	public static java.lang.String getName(int key){
		return EnumMultipleServicesIndicator.getName(key);
	}

	public static boolean isValid(int value){
		return EnumMultipleServicesIndicator.isValid(value);
	}

	public static int[] keys(){
		return EnumMultipleServicesIndicator.keys();
	}
}
