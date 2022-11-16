package com.baypackets.ase.ra.diameter.base.message;

import com.baypackets.ase.ra.diameter.common.enums.StandardEnum;
import com.baypackets.ase.ra.diameter.common.exception.ValidationRecord;

public interface BaseMessage {

	public boolean equals(java.lang.Object obj);

	public long getApplicationId();
	            
	public byte[] getByteArray();
	            
	public int getCommandCode();
	            
	public  java.lang.String 	getDestinationHost();

//	public Peer getDestinationPeer();

	//public int getHeaderLength();
		            
	public long getHopIdentifier();
		            
	public int getMessageLength();

	public String getName();

	//public int getOffset();

	//public Peer getOriginPeer();

	public String getSessionId();

	public StandardEnum getStandard();

	//public void readExternal(java.io.ObjectInput input);

	//public void resetIdentifier();

	//public void resetIdentifier(long identifier);

//	public void setDestinationPeer(Peer destinationPeer);

//	public void setOriginPeer(Peer originPeer);

	public void toXML(java.lang.StringBuilder builder);

	public ValidationRecord validate();

	//public void write(java.nio.ByteBuffer otherBuffer);

	//public void writeExternal(java.io.ObjectOutput output); 
}
