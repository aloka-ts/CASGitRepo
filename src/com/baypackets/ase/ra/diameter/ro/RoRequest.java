package com.baypackets.ase.ra.diameter.ro;

import java.util.List;

import com.baypackets.ase.ra.diameter.base.avp.BaseAvp;
import com.baypackets.ase.ra.diameter.base.message.BaseDiameterRequest;
import com.baypackets.ase.resource.Request;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterMessage;



public interface RoRequest extends Request, BaseDiameterRequest{

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
	//public java.util.List<? extends BaseAvp> getAvpList();
	public java.util.List<DiameterAVP> getAvpList(long avpCode, long vendorId);
	public java.util.List<DiameterAVP> getAvpList(long vendorId);
	public java.util.List<DiameterAVP> getAvps();
	public long getEndToEndIdentifier();
	public long getHopByHopIdentifier();
//	public java.lang.String getOriginHost() ;
//	public java.lang.String getOriginRealm() ;
	public boolean isError();
	public boolean isProxiable();
	public boolean isRequest();
	public boolean isReTransmitted();
	public void setReTransmitted(boolean value);
	//public java.lang.String getDestinationRealm() ;
	public void setProxiable(boolean value);


}