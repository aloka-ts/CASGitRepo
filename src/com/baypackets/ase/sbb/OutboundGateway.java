/*
 * OutboundGateway.java
 */
package com.baypackets.ase.sbb;


/**
 * The OuboundGateway class extends the ExternalDevice class 
 */
public interface OutboundGateway extends ExternalDevice {
	
	/**
	 * This method will return groupId for outbound gateway
	 * @return GroupId for this outbound gateway
	 */
	public String getGroupId();
	
	/**
	 * This method will set groupId for outbound gateway
	 * @param groupId : GroupId to be set
	 */
	public void setGroupId(String groupId);
	
	public int getIsRemote();

	public void setIsRemote(int isRemote) ;
	
}
