package com.baypackets.ase.ra.diameter.common.enums;

import java.util.Hashtable;

import com.traffix.openblox.core.enums.AvpFormat;

public enum AvpFormatEnum
{
	Address,
	DiameterIdentity,
	DiameterURI,
	Enumerated,
	Float32,
	Float64,
	Grouped,
	Integer,
	Integer32,
	Integer64,
	IPFilterRule,
	OctetString,
	QoSFilterRule,
	QoSFilterRuleBase,
	String,
	Text,
	Time,
	Unknown,
	Unsigned32,
	Unsigned64,
	UTF8String,
	VendorSpecific;

	private static Hashtable<AvpFormatEnum, AvpFormat> stackMapping = new Hashtable<AvpFormatEnum, AvpFormat>();
	private static Hashtable<AvpFormat, AvpFormatEnum> containerMapping = new Hashtable<AvpFormat, AvpFormatEnum>();
	static{
		stackMapping.put(AvpFormatEnum.Address, AvpFormat.Address);
		stackMapping.put(AvpFormatEnum.DiameterIdentity, AvpFormat.DiameterIdentity);
		stackMapping.put(AvpFormatEnum.DiameterURI, AvpFormat.DiameterURI);
		stackMapping.put(AvpFormatEnum.Enumerated, AvpFormat.Enumerated);
		stackMapping.put(AvpFormatEnum.Float32, AvpFormat.Float32);
		stackMapping.put(AvpFormatEnum.Float64, AvpFormat.Float64);
		stackMapping.put(AvpFormatEnum.Grouped, AvpFormat.Grouped);
		stackMapping.put(AvpFormatEnum.Integer, AvpFormat.Integer);
		stackMapping.put(AvpFormatEnum.Integer32, AvpFormat.Integer32);
		stackMapping.put(AvpFormatEnum.Integer64, AvpFormat.Integer64);
		stackMapping.put(AvpFormatEnum.IPFilterRule, AvpFormat.IPFilterRule);
		stackMapping.put(AvpFormatEnum.OctetString, AvpFormat.OctetString);
		stackMapping.put(AvpFormatEnum.QoSFilterRule, AvpFormat.QoSFilterRule);
		stackMapping.put(AvpFormatEnum.QoSFilterRuleBase, AvpFormat.QoSFilterRuleBase);
		stackMapping.put(AvpFormatEnum.String, AvpFormat.String);
		stackMapping.put(AvpFormatEnum.Text, AvpFormat.Text);
		stackMapping.put(AvpFormatEnum.Time, AvpFormat.Time);
		stackMapping.put(AvpFormatEnum.Unknown, AvpFormat.Unknown);
		stackMapping.put(AvpFormatEnum.Unsigned32, AvpFormat.Unsigned32);
		stackMapping.put(AvpFormatEnum.Unsigned64, AvpFormat.Unsigned64);
		stackMapping.put(AvpFormatEnum.UTF8String, AvpFormat.UTF8String);
		stackMapping.put(AvpFormatEnum.VendorSpecific, AvpFormat.VendorSpecific);

		containerMapping.put(AvpFormat.Address, AvpFormatEnum.Address);
		containerMapping.put(AvpFormat.DiameterIdentity, AvpFormatEnum.DiameterIdentity);
		containerMapping.put(AvpFormat.DiameterURI, AvpFormatEnum.DiameterURI);
		containerMapping.put(AvpFormat.Enumerated, AvpFormatEnum.Enumerated);
		containerMapping.put(AvpFormat.Float32, AvpFormatEnum.Float32);
		containerMapping.put(AvpFormat.Float64, AvpFormatEnum.Float64);
		containerMapping.put(AvpFormat.Grouped, AvpFormatEnum.Grouped);
		containerMapping.put(AvpFormat.Integer, AvpFormatEnum.Integer);
		containerMapping.put(AvpFormat.Integer32, AvpFormatEnum.Integer32);
		containerMapping.put(AvpFormat.Integer64, AvpFormatEnum.Integer64);
		containerMapping.put(AvpFormat.IPFilterRule, AvpFormatEnum.IPFilterRule);
		containerMapping.put(AvpFormat.OctetString, AvpFormatEnum.OctetString);
		containerMapping.put(AvpFormat.QoSFilterRule, AvpFormatEnum.QoSFilterRule);
		containerMapping.put(AvpFormat.QoSFilterRuleBase, AvpFormatEnum.QoSFilterRuleBase);
		containerMapping.put(AvpFormat.String, AvpFormatEnum.String);
		containerMapping.put(AvpFormat.Text, AvpFormatEnum.Text);
		containerMapping.put(AvpFormat.Time, AvpFormatEnum.Time);
		containerMapping.put(AvpFormat.Unknown, AvpFormatEnum.Unknown);
		containerMapping.put(AvpFormat.Unsigned32, AvpFormatEnum.Unsigned32);
		containerMapping.put(AvpFormat.Unsigned64, AvpFormatEnum.Unsigned64);
		containerMapping.put(AvpFormat.UTF8String, AvpFormatEnum.UTF8String);
		containerMapping.put(AvpFormat.VendorSpecific, AvpFormatEnum.VendorSpecific);
	}

	public static final AvpFormatEnum getContainerObj(AvpFormat shEnum){
		return containerMapping.get(shEnum);
	}

	public static final AvpFormat getStackObj(AvpFormatEnum shEnum){
		return stackMapping.get(shEnum);
	}

	public static AvpFormatEnum approx(java.lang.String valueName){
		return getContainerObj(AvpFormat.approx(valueName));
	}

	public static AvpFormatEnum retrieve(java.lang.String valueName){
		return getContainerObj(AvpFormat.retrieve(valueName));
	}

	//	public static CurrentLocationEnum 	valueOf(java.lang.String name){
	//	return CurrentLocationEnum.valueOf(name);
	//}
	//
	//static CurrentLocationEnum[] values(){
	//	return EnumCurrentLocation.values();
	//}
}