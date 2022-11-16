package com.baypackets.ase.ra.diameter.sh;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameterGrouped;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.resource.ResourceException;
import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public interface ShUserDataResponse extends ShResponse {

	public static final int code = 271;
	public static final String name = "UDA";
	public static final long applicationId = 15L;
	
	public int getCommandCode();

	public long getApplicationId();

	public String getName();

	public String getSessionId();

	////////////////////////////////////////////////////////////////
	//// RO Method implementation starts ///////////////////////////
	////////////////////////////////////////////////////////////////

	/**
	 *  Adding AuthApplicationId AVP of type Unsigned32 to the message.
	 */
	public void addAuthApplicationId(long value) throws ShResourceException ;

	public ArrayList<DiameterAVP> getGroupedProxyInfos() throws ShResourceException ;

	/**
	 *  Retrieving multiple DiameterIdentity values from RouteRecord AVPs.
	 */
	public ArrayList<DiameterAVP> getRouteRecords() throws ShResourceException ;
	void addDiameterInteger32AVP(String name, int value,
								 String vendorName);

	void addDiameterInteger64AVP(String name, long value,
								 String vendorName);

	void addDiameterUnsigned32AVP(String name, long value,
								  String vendorName);

	void addDiameterUnsigned64AVP(String name,
								  BigInteger value, String vendorName);

	void addDiameterFloat32AVP(String name, String vendorName,
							   float value);

	void addDiameterFloat64AVP(String name, String vendorName,
							   double value);

	void addDiameterGenericAVP(long avpCode, long vendorId,
							   byte[] value);

	void addDiameterOctetStringAVP(String name,
								   String vendorName, byte[] value);

	void addDiameterOctetStringAVP(String name,
								   String vendorName, String value);

	AvpDiameterGrouped addDiameterGroupedAVP(String avpName, String vendorName);

	void addDiameterAVPs(ArrayList<DiameterAVP> groupedAvps);

	DiameterShMessageFactory getDiameterShMessageFactory();

	void addDiameterGroupedAVP(String avpName, String vendorName,
							   List<DiameterAVP> groupAvps);

	void setDestinationHost(String host);

	void setServiceContextId(String contextId);

}