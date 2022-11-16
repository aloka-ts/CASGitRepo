/*
 * ErrorCodes.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.cab.util;
/**
 * This class defines static constants for error code and their description
 */
public class ErrorCodes {
	public static final String ERROR_001="CAB_001";	
	public static final String ERROR_001_DESC="AconyxUsername is a mandatory field.";

	public static final String ERROR_002="CAB_002";	
	public static final String ERROR_002_DESC="No Aconyx User found.";
	
	public static final String ERROR_003="CAB_003";	
	public static final String ERROR_003_DESC="Missing Personal Contact Card Mandatory Field :";
	
	public static final String ERROR_004="CAB_004";	
	public static final String ERROR_004_DESC="Incorrect value for Personal Contact Card Field:";

	public static final String ERROR_005="CAB_005";	
	public static final String ERROR_005_DESC="No Personal Contact Card defined in request.";
	
	public static final String ERROR_006="CAB_006";	
	public static final String ERROR_006_DESC="No Personal Contact Card found for Aconyx User:";

	public static final String ERROR_007="CAB_007";	
	public static final String ERROR_007_DESC="Contact View name already used.";

	public static final String ERROR_008="CAB_008";	
	public static final String ERROR_008_DESC="Contact View Name must be specified.";

	public static final String ERROR_009="CAB_009";	
	public static final String ERROR_009_DESC="No field specified in Contact View:";
	
	public static final String ERROR_010="CAB_010";	
	public static final String ERROR_010_DESC="No Contact View field exists by Name:";
	
	public static final String ERROR_011="CAB_011";	
	public static final String ERROR_011_DESC="Contact View field must be specified.";
	

	public static final String ERROR_012="CAB_012";	
	public static final String ERROR_012_DESC="Unable to delete Contact View it is associated with some group.";

	public static final String ERROR_013="CAB_013";	
	public static final String ERROR_013_DESC="No Contact View defined in request.";
	
	public static final String ERROR_014="CAB_014";	
	public static final String ERROR_014_DESC="No Contact View found for Aconyx User:";

	public static final String ERROR_015="CAB_015";	
	public static final String ERROR_015_DESC="Address Book Group name already used.";

	public static final String ERROR_016="CAB_016";	
	public static final String ERROR_016_DESC="Address Book Group name must be specified.";
	
	public static final String ERROR_017="CAB_017";	
	public static final String ERROR_017_DESC="Contact View must be specified for Address Book Group.";

	public static final String ERROR_018="CAB_018";	
	public static final String ERROR_018_DESC="No Address Book Group exists as:";

	public static final String ERROR_019="CAB_019";	
	public static final String ERROR_019_DESC="No Address Book Group exists for:";

	public static final String ERROR_020="CAB_020";	
	public static final String ERROR_020_DESC="No Member List specified in Address Book Group.";

	public static final String ERROR_021="CAB_021";	
	public static final String ERROR_021_DESC="Error while executing sql query.";
	
	public static final String ERROR_022="CAB_022";	
	public static final String ERROR_022_DESC="Restricted special character used. Only . _0 - and @ are allowed in:"; 
	
	public static final String ERROR_023="CAB_023";	
	public static final String ERROR_023_DESC="Contact View Field specified more then one time:"; 
	
	public static final String ERROR_024="CAB_024";	
	public static final String ERROR_024_DESC="No Address Book Group defined in request.";
	
	public static final String ERROR_025="CAB_025";	
	public static final String ERROR_025_DESC="Address Book Group Member must be specified.";
	
	public static final String ERROR_026="CAB_026";	
	public static final String ERROR_026_DESC="Conatct View Operation is not allowed for Contact View:";
	
	public static final String ERROR_027="CAB_027";	
	public static final String ERROR_027_DESC="Missing "+Constants.DEFAULT_CONTACT_VIEW_NAME+" Conatct View's mandatory field: ";
	
	public static final String ERROR_028="CAB_027";	
	public static final String ERROR_028_DESC="Missing Search User's mandatory field:";
	
	public static final String ERROR_029="CAB_029";	
	public static final String ERROR_029_DESC="Incorrect Value for SearchBy field. Possible values are:";
	
	public static final String ERROR_030="CAB_030";	
	public static final String ERROR_030_DESC="CAB Service Temporary Unavailable for this operation.";	
	
	public static final String ERROR_031="CAB_031";	
	public static final String ERROR_031_DESC="Address Book Group Operation is not allowed for Address Book Group:";
	
	public static final String ERROR_032="CAB_032";	
	public static final String ERROR_032_DESC="Address Book Group Non Aconyx Member must be specified.";
	
	public static final String ERROR_033="CAB_033"; 
    public static final String ERROR_033_DESC="Invalid value for SIP Address:";
    
    public static final String ERROR_034="CAB_034";	
	public static final String ERROR_034_DESC="Missing Non Aconyx User's mandatory field:";
     
}