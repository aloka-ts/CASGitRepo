package com.baypackets.ase.ra.diameter.sh;
import com.baypackets.ase.ra.diameter.base.message.BaseDiameterResponse;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;
import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterMessage;

import java.util.List;

public interface ShResponse extends Response, BaseDiameterResponse {

	public List<DiameterAVP> getVendorSpecificAvps();

	public DiameterAVP getAvp(long avpCode, long vendorId);
	public long getApplicationId();
	public int getCommandCode();
	public java.lang.String getDestinationHost();
	public long getHopIdentifier();
	public String getName();
	public String getSessionId();
	public DiameterAVP get(int index);
	public java.util.List<DiameterAVP> getAvp(long avpCode);
	public java.util.List<DiameterAVP> getAvpList(long avpCode, long vendorId);
	public java.util.List<DiameterAVP> getAvpList(long vendorId);
	public java.util.List<DiameterAVP> getAvps();
	public long getEndToEndIdentifier();
	public long getHopByHopIdentifier();
	public boolean isError();
	public boolean isProxiable();
	public boolean isRequest();
	public boolean isReTransmitted();
	public void setReTransmitted(boolean value);
	public java.lang.String getDestinationRealm() ;
	public void setProxiable(boolean value);
public ValidationRecord validate();
	public String getResultCode(boolean experimental) throws ShResourceException;
	public String getOriginHost() ;
	public String getOriginRealm() throws ResourceException;
public String getUserData();
public void setUserData(String uda);
}