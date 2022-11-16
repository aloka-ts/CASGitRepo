package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumTariffChangeUsage;

public enum TariffChangeUsageEnum
{
UNIT_AFTER_TARIFF_CHANGE,
UNIT_BEFORE_TARIFF_CHANGE,
UNIT_INDETERMINATE;

private static Hashtable<TariffChangeUsageEnum,EnumTariffChangeUsage> stackMapping = new Hashtable<TariffChangeUsageEnum,EnumTariffChangeUsage>();
private static Hashtable<EnumTariffChangeUsage,TariffChangeUsageEnum> containerMapping = new Hashtable<EnumTariffChangeUsage,TariffChangeUsageEnum>();

 static {
stackMapping.put(TariffChangeUsageEnum.UNIT_AFTER_TARIFF_CHANGE, EnumTariffChangeUsage.UNIT_AFTER_TARIFF_CHANGE);
stackMapping.put(TariffChangeUsageEnum.UNIT_BEFORE_TARIFF_CHANGE, EnumTariffChangeUsage.UNIT_BEFORE_TARIFF_CHANGE);
stackMapping.put(TariffChangeUsageEnum.UNIT_INDETERMINATE, EnumTariffChangeUsage.UNIT_INDETERMINATE);

containerMapping.put(EnumTariffChangeUsage.UNIT_AFTER_TARIFF_CHANGE, TariffChangeUsageEnum.UNIT_AFTER_TARIFF_CHANGE);
containerMapping.put(EnumTariffChangeUsage.UNIT_BEFORE_TARIFF_CHANGE, TariffChangeUsageEnum.UNIT_BEFORE_TARIFF_CHANGE);
containerMapping.put(EnumTariffChangeUsage.UNIT_INDETERMINATE, TariffChangeUsageEnum.UNIT_INDETERMINATE);
}

public static final TariffChangeUsageEnum getContainerObj(EnumTariffChangeUsage stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumTariffChangeUsage getStackObj(TariffChangeUsageEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static TariffChangeUsageEnum fromCode(int value){
	return getContainerObj(EnumTariffChangeUsage.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumTariffChangeUsage.getName(key);
}

public static boolean isValid(int value){
	return EnumTariffChangeUsage.isValid(value);
}

public static int[] keys(){
	return EnumTariffChangeUsage.keys();
}
}
