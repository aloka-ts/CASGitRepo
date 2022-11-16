/*
 * MsMonitorSpec.java
 *
 * @author Amit Baxi
 */
/**
 * The MsSelectorSpec class defines the specification for a msml monitor element as per RFC 5707.
 * This class provides methods for defining attributes of monitor element of msml. 
 */
package com.baypackets.ase.sbb;
import java.io.Serializable;
/**
 * The MsMonitorSpec class defines the specification for a msml monitor element as per RFC 5707.
 * This class provides methods for defining id1,id2, mark, compressed and other attributes of monitor element of msml. 
 * 
 */
public class MsMonitorSpec implements Serializable{

	
	private String monitorId;//an identifier of the connection to be monitored.  Mandatory
	private String monitoringId;// an identifier of the object that is to receive the copy of the media destined to monitorId. monitoringId may be a connection or a conference
	private String mark;
	private boolean compressed;

	/**
	 * This method sets id1 attribute of monitor.
	 * @param monitorId the monitorId to set
	 */
	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}
	
	/**
	 * This method returns id1 attribute of monitor.
	 * @return the monitorId
	 */
	public String getMonitorId() {
		return monitorId;
	}
	
	/**
	 * This method sets id2 attribute of monitor.
	 * @param monitoringId the monitoringId to set
	 */
	public void setMonitoringId(String monitoringId) {
		this.monitoringId = monitoringId;
	}
	
	/**
	 * This method returns id2 attribute of monitor.
	 * @return the monitoringId
	 */
	public String getMonitoringId() {
		return monitoringId;
	}
	/**
	 * This method sets compressed attribute of monitor that specifies whether the 
	 * monitor uses compressed media.Defaut is false.
	 * @param compressed the compressed to set
	 */
	public void setCompressed(boolean compressed) {
		this.compressed = compressed;
	}

	/**This method returns compressed attribute of monitor that specifies whether the 
	 * monitor uses compressed media.Defaut is false.
	 * @return the compressed
	 */
	public boolean isCompressed() {
		return compressed;
	}
	
	/**
	 * This method sets optional attribute mark.
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}
	/**
	 * This method returns optional attribute mark.
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}
	
}