package com.baypackets.ase.ra.diameter.base.message;

import com.baypackets.ase.resource.ResourceException;

public interface BaseDiameterRequest extends BaseDiameterMessage {

	public java.lang.String getDestinationRealm() throws ResourceException;

	public void setProxiable(boolean value) throws ResourceException; 

}
