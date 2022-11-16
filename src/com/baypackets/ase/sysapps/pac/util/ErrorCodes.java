package com.baypackets.ase.sysapps.pac.util;
public class ErrorCodes {

	public static final String ERROR_001="PAC_001";	
	public static final String ERROR_001_DESC="ApplicationId is a mandatory field.";

	public static final String ERROR_002="PAC_002";	
	public static final String ERROR_002_DESC="No Application Id found.";
	
	public static final String ERROR_003="PAC_003";	
	public static final String ERROR_003_DESC="AconyxUserName is a mandatory field.";
	
	public static final String ERROR_004="PAC_004";	
	public static final String ERROR_004_DESC="No AconyxUserName found.";

	public static final String ERROR_005="PAC_005";	
	public static final String ERROR_005_DESC="Password is a mandatory field.";

	public static final String ERROR_006="PAC_006";	
	public static final String ERROR_006_DESC="Encrypted is a mandatory field.";

	public static final String ERROR_007="PAC_007";	
	public static final String ERROR_007_DESC="Invalid value for Encrypted field. Value can be either Yes or No";

	public static final String ERROR_008="PAC_008";	
	public static final String ERROR_008_DESC="ChannelUsername is a mandatory field.";
	
	public static final String ERROR_026="PAC_026";	
	public static final String ERROR_026_DESC="Invalid Channel Username name.";
	
	public static final String ERROR_009="PAC_009";	
	public static final String ERROR_009_DESC="No Channel found for the given AconyxUserName.";
	

	public static final String ERROR_010="PAC_010";	
	public static final String ERROR_010_DESC="ChannelName is a mandatory field.";

	public static final String ERROR_011="PAC_011";	
	public static final String ERROR_011_DESC="Invalid Channel name.";

	public static final String ERROR_012="PAC_012";	
	public static final String ERROR_012_DESC="Channel URL is a mandatory field.";

	public static final String ERROR_013="PAC_013";	
	public static final String ERROR_013_DESC="Invalid value for Channel URL.";

	public static final String ERROR_014="PAC_014";	
	public static final String ERROR_014_DESC="Role is a mandatory field.";

//	public static final String ERROR_015="PAC_015";	
//	public static final String ERROR_015A_DESC="Invalid value for Port."; 

	public static final String ERROR_016="PAC_016";	
	public static final String ERROR_016_DESC="The channel configuration is already present.";

	public static final String ERROR_017="PAC_017";	
	public static final String ERROR_017_DESC="No channel configuration found for the given ChannelName and ChannelUsername.";

	public static final String ERROR_018="PAC_018";	
	public static final String ERROR_018_DESC="Error while retrieving presence using current channel configuration.";

	public static final String ERROR_019="PAC_019";	
	public static final String ERROR_019_DESC="Error while retrieving aggregated presence using current channel configurations.";

	public static final String ERROR_020="PAC_020";	
	public static final String ERROR_020_DESC="Status is a mandatory field.";

	public static final String ERROR_021="PAC_021";	
	public static final String ERROR_021_DESC="Invalid value for Status field.";
	
	public static final String ERROR_022="PAC_022";	
	public static final String ERROR_022_DESC="No Channel defined.";
	
	public static final String ERROR_023="PAC_023";	
	public static final String ERROR_023_DESC="Found nothing to be modified.";
	
	public static final String ERROR_024="PAC_024";	
	public static final String ERROR_024_DESC="Invalid value for Role field. Value can be either 'PAC Admin' or 'PAC User'.";
	
	public static final String ERROR_025="PAC_025";	
	public static final String ERROR_025_DESC="Error while executing query.";
	
	public static final String ERROR_027="PAC_027";	
	public static final String ERROR_027_DESC="PAC Service Temporary Unavailable.";	
	
	public static final String ERROR_028="PAC_028";	
	public static final String ERROR_028_DESC="Restricted special character used. Only . _ - and @ are allowed in: ";
	
	public static final String ERROR_029="PAC_029";	
	public static final String ERROR_029_DESC="Aconyx User is already present.";	

	public static final String ERROR_030="PAC_030"; 
	public static final String ERROR_030_DESC="Restricted character used in AconyxUserName:";    
	
	public static final String ERROR_031="PAC_031"; 
	public static final String ERROR_031_DESC="AconyxUserName length exceeded maximum allowed:"; 
}
