package com.baypackets.ase.sysapps.cim.util;

public class ErrorCodes {

	public static final String ERROR_001="CIM_001";	
	public static final String ERROR_001_DESC="ApplicationId is a mandatory field.";

	public static final String ERROR_002="CIM_002";	
	public static final String ERROR_002_DESC="No Application Id found.";
	
	public static final String ERROR_003="CIM_003";	
	public static final String ERROR_003_DESC="AconyxUserName is a mandatory field.";
	
	public static final String ERROR_004="CIM_004";	
	public static final String ERROR_004_DESC="No AconyxUserName found.";

	public static final String ERROR_005="CIM_005";	
	public static final String ERROR_005_DESC="Incorrect state(Only ENABLE/DISABLE permitted)";
	
	public static final String ERROR_006="CIM_006";	
	public static final String ERROR_006_DESC="State is required";
	
	public static final String ERROR_007="CIM_007";	
	public static final String ERROR_007_DESC="Error while executing query.";

	public static final String ERROR_008="CIM_008";	
	public static final String ERROR_008_DESC="AconyxUserName length exceeded maximum allowed:";
	
	public static final String ERROR_009="CIM_009";	
	public static final String ERROR_009_DESC="Restricted character used in AconyxUserName:";
	
}
