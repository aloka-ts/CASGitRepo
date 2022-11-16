/**
 * 
 */
package com.baypackets.ase.container;

import com.baypackets.ase.util.AseStrings;


/**
 * The Class MonitoredIP.
 * 
 * This class will Contain IP and monitor type
 *
 * @author saneja
 */
public class MonitoredIP  {
	
	public static enum MonitorActionType {

		PLUMB(1), PING(2), UNKNOWN(-1);

		private MonitorActionType(int i) {
			this.code = i;
		}

		private int	code;

		public int getCode() {
			return code;
		}

		public static MonitorActionType fromInt(int num) {
			MonitorActionType action = UNKNOWN;
			switch (num) {
				case 1: {
					action = PLUMB;
					break;
				}
				case 2: {
					action = PING;
					break;
				}
				default: {
					action = UNKNOWN;
					break;
				}
			}//@End Switch
			return action;
		}
	}
	
	
	private MonitorActionType actionType;
	private String ipAddress;
	private int counter = 0;
	
	private int retries = 3;
	private long frequency = 10;
	private int timeout = 2000;
	
	
	//done as different ip's in sma ethread can have have diffenrt interval
	private long ticks=1;
	private long tickCounter=-1;
	

	public MonitoredIP(String ipAddress,MonitorActionType actionType) {
		this.actionType = actionType;
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the actionType
	 */
	public MonitorActionType getActionType() {
		return actionType;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	
	/**
	 * @return the ipAddress
	 */
	public int getCounter() {
		return counter;
	}
	
	/**
	 * @return the ipAddress
	 */
	public void incrementCounter() {
		++counter;
	}
	
	/**
	 * @return the ipAddress
	 */
	public void resetCounter() {
		counter =0;
	}
	
	/**
	 * @return the retries
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 * @return the frequency
	 */
	public long getFrequency() {
		return frequency;
	}

	/**
	 * @param retries the retries to set
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}

	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public boolean incrementAndCheckTicks(long sleepInterval){
		if(tickCounter == -1){
			tickCounter =0;
			ticks = frequency/sleepInterval;
		}
		++tickCounter;
		if(tickCounter == ticks){
			tickCounter=0;
			return true;
		}else{
			return false;
		}
		
		
	}

	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("MonitoredIP deatils IP::<");
		builder.append(ipAddress);
		builder.append(AseStrings.ANGLE_BRACKET_CLOSE);
		builder.append(" ACTION::<");
		builder.append(actionType.toString());
		builder.append(AseStrings.ANGLE_BRACKET_CLOSE);
		builder.append(" RETRIES::<");
		builder.append(retries);
		builder.append(AseStrings.ANGLE_BRACKET_CLOSE);
		builder.append(" INTERVAL::<");
		builder.append(frequency);
		builder.append(AseStrings.ANGLE_BRACKET_CLOSE);
		builder.append(" COUNT::<");
		builder.append(counter);
		builder.append(">  ");
		builder.append("TIMEOUT::<");
		builder.append(timeout);
		builder.append(AseStrings.ANGLE_BRACKET_CLOSE);
		builder.append(" TICKS::<");
		builder.append(ticks);
		builder.append(AseStrings.ANGLE_BRACKET_CLOSE);
		builder.append(" TICKCOUNTER::<");
		builder.append(tickCounter);
		builder.append(AseStrings.ANGLE_BRACKET_CLOSE);
		return builder.toString();
		
		
	}
	
	
}
