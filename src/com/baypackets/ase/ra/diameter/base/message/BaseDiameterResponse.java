package com.baypackets.ase.ra.diameter.base.message;


public interface BaseDiameterResponse extends BaseDiameterMessage {


	// TODO this method is already defined in resource.Response class. 
	//	public BaseDiameterMessage getRequest();

	public boolean isPerformFailover();

	public void setPerformFailover(boolean performFailover); 

}
