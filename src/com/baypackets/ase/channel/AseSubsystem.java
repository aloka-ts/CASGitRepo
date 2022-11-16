package com.baypackets.ase.channel;

import com.baypackets.ase.util.AseStrings;


public class AseSubsystem {
	
	private String id;
	private String host;
	private String signalIp;
	private short port;
	private short mode;
	private String version;
	private int emsSubsystemId;
	private boolean connected;
	
	private boolean seperateVlanEnabled;
	private String seperateVlanIp;

	// Stores time stamp when this subsystem joined the group
	// Every system fills this value for itself ONLY before sending
	// its AseSubsystem object to any other system
	private String timeStamp;

	
	
	public AseSubsystem (String id){
		this.id = id;
		//reception_status = NASCENT;
	}

	public String getTimestamp()
	{
		return timeStamp;
	}

	public void setTimeStamp( String tStamp)
	{
		timeStamp = tStamp;
	}

	/*synchronized public int getReceptionStatus()
	{
		return reception_status;
	}

	synchronized public void setReceptionStatus( int sts)
	{
		reception_status  = sts;
	}*/
	
	public String getId(){
		return this.id;
	}

	public void setMode (short mode){
		this.mode = mode;
	}
	
	public short getMode(){
		return mode;
	}
	
	public void setVersion (String version){
		this.version = version;
	}
	public String getVersion(){
		return version;
	}

	public String getHost() {
		return host;
	}

	public short getPort() {
		return port;
	}

	public void setHost(String string) {
		host = string;
	}

	public void setPort(short s) {
		port = s;
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean b) {
		connected = b;
	}
	
	public int getEmsSubsystemId() {
		return emsSubsystemId;
	}

	public void setEmsSubsystemId(int i) {
		emsSubsystemId = i;
	}
	public String getSignalIp()	{
		return signalIp;
	}

	public void setSignalIp(String signalIp)	{
		this.signalIp = signalIp;
	}
	
	/**
	 * @return the seperateVlanEnabled
	 */
	public boolean isSeperateVlanEnabled() {
		return seperateVlanEnabled;
	}

	/**
	 * @param seperateVlanEnabled the seperateVlanEnabled to set
	 */
	public void setSeperateVlanEnabled(boolean seperateVlanEnabled) {
		this.seperateVlanEnabled = seperateVlanEnabled;
	}

	/**
	 * @return the seperateVlanSelfIp
	 */
	public String getSeperateVlanIp() {
		return seperateVlanIp;
	}

	/**
	 * @param seperateVlanSelfIp the seperateVlanSelfIp to set
	 */
	public void setSeperateVlanIp(String seperateVlanIp) {
		this.seperateVlanIp = seperateVlanIp;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("AseSubsystem(");
		buffer.append(""+this.id);
		buffer.append(AseStrings.COMMA);
		buffer.append(this.host);
		buffer.append(AseStrings.COMMA);
		buffer.append(this.signalIp);
		buffer.append(AseStrings.COMMA);
		buffer.append(this.seperateVlanEnabled);
		buffer.append(AseStrings.COMMA);
		buffer.append(this.seperateVlanIp);
		buffer.append(AseStrings.PARENTHESES_CLOSE);
		
		return buffer.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AseSubsystem other = (AseSubsystem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

