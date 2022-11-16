package com.baypackets.ase.ra.diameter.common.enums;

import java.util.Hashtable;

import com.traffix.openblox.core.enums.Standard;


public enum StandardEnum {
	base,
	BaseAccounting ,
	Core,
	Rf,
	Ro,
	Sh;

	private static Hashtable<StandardEnum, Standard> stackMapping = new Hashtable<StandardEnum, Standard>();
	private static Hashtable<Standard, StandardEnum> containerMapping = new Hashtable<Standard, StandardEnum>();
	static{
		stackMapping.put(StandardEnum.base, Standard.Base);
		stackMapping.put(StandardEnum.BaseAccounting , Standard.BaseAccounting );
		stackMapping.put(StandardEnum.Core, Standard.Core);
		stackMapping.put(StandardEnum.Rf, Standard.Rf);
		stackMapping.put(StandardEnum.Ro, Standard.Ro);
		stackMapping.put(StandardEnum.Sh, Standard.Sh);


		containerMapping.put(Standard.Base, StandardEnum.base);
		containerMapping.put(Standard.BaseAccounting , StandardEnum.BaseAccounting );
		containerMapping.put(Standard.Core, StandardEnum.Core);
		containerMapping.put(Standard.Rf, StandardEnum.Rf);
		containerMapping.put(Standard.Ro, StandardEnum.Ro);
		containerMapping.put(Standard.Sh, StandardEnum.Sh);
	}

	public static final StandardEnum getContainerObj(Standard shEnum){
		return containerMapping.get(shEnum);
	}

	public static final Standard getStackObj(StandardEnum shEnum){
		return stackMapping.get(shEnum);
	}

	public static StandardEnum fromAppIdAndVendorId(long applicationId, long vendorId){
		return getContainerObj(Standard.fromAppIdAndVendorId(applicationId,vendorId));
	}
    
	public static StandardEnum fromOrdinal(int ordinal){
		return getContainerObj(Standard.fromOrdinal(ordinal));
	}
	           
//	public long getApplicationId(){
//		return Standard.getApplicationId();
//	}
//	           
//	public ApplicationTypeEnum getApplicationType(){
//		return ApplicationTypeEnum.getContainerObj(Standard.getApplicationType());
//	}
//	           
//	public FSMType getFsmType(StackType stackType){
//		return getContainerObj(EnumIdentitySet.fromCode(value));
//	}
//	           
//	public Standard[] getReferences(){
//		return getContainerObj(EnumIdentitySet.fromCode(value));
//	}
//	           
//	public StandardTypeEnum getType(){
//		return getContainerObj(Standard.getType(value));
//	}
//	           
//	public long getVendorId(){
//		return getContainerObj(Standard.getVendorId());
//	}

	//	public static CurrentLocationEnum 	valueOf(java.lang.String name){
	//	return CurrentLocationEnum.valueOf(name);
	//}
	//
	//static CurrentLocationEnum[] values(){
	//	return EnumCurrentLocation.values();
	//}
}