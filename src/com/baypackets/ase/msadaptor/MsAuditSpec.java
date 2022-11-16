/*
 * MsAuditSpec.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.msadaptor;
import com.baypackets.ase.msadaptor.MsOperationSpec;
/**
 * The MsAuditSpec class defines the specification for a msml audit element as per RFC 5707.
 * This class provides methods for defining query id, statelist and mark attributes of audit
 * element of msml for container. 
 */
public class MsAuditSpec extends MsOperationSpec {
	
	private static final long serialVersionUID = 1543653264563545L;
	
	//Constants for statelist attribute of audit element 
	public static final String CONF_CONFCONFIG_ALL="audit.conf.confconfig.*";
	public static final String CONF_CONFCONFIG="audit.conf.confconfig";
	
	public static final String CONF_CONFCONFIG_AUDIOMIX_ALL="audit.conf.confconfig.audiomix.*";
	public static final String CONF_CONFCONFIG_AUDIOMIX="audit.conf.confconfig.audiomix";	
	public static final String CONF_CONFCONFIG_AUDIOMIX_ASN="audit.conf.confconfig.audiomix.asn";
	public static final String CONF_CONFCONFIG_AUDIOMIX_N_LOUDEST="audit.conf.confconfig.audiomix.n-loudest";
	
	public static final String CONF_CONFCONFIG_VIDEOLAYOUT_ALL="audit.conf.confconfig.videolayout.*";
	public static final String CONF_CONFCONFIG_VIDEOLAYOUT="audit.conf.confconfig.videolayout";
	public static final String CONF_CONFCONFIG_VIDEOLAYOUT_ROOT="audit.conf.confconfig.videolayout.root";
	public static final String CONF_CONFCONFIG_VIDEOLAYOUT_SELECTOR="audit.conf.confconfig.videolayout.selector";

	public static final String CONF_CONFCONFIG_CONTROLLER="audit.conf.confconfig.controller";
	
	public static final String CONF_DIALOG_ALL="audit.conf.dialog.*";
	public static final String CONF_DIALOG="audit.conf.dialog";
	public static final String CONF_DIALOG_DURATION="audit.conf.dialog.duration";
	public static final String CONF_DIALOG_PREMITIVE="audit.conf.dialog.premitive";
	public static final String CONF_DIALOG_CONTROLLER="audit.conf.dialog.controller";
	
	public static final String CONF_STREAM_ALL="audit.conf.stream.*";
	public static final String CONF_STREAM="audit.conf.stream";
	public static final String CONF_STREAM_CLAMP="audit.conf.stream.clamp";
	public static final String CONF_STREAM_GAIN="audit.conf.stream.gain";
	public static final String CONF_STREAM_VISUAL="audit.conf.stream.visual";
	
	
	
	private String queryId;
	private String stateList;
	private String mark;
	
	public MsAuditSpec(){
		
	}
	
	public MsAuditSpec(String queryId, String stateList) {
		this.queryId = queryId;
		this.stateList = stateList;
	}
	/**
	 * This method sets statelist attribute of audit element.
	 * @param stateList the stateList to set
	 */
	public void setStateList(String stateList) {
		this.stateList = stateList;
	}
	/**
	 * This method returns statelist attribute of audit element.
	 * @return the stateList
	 */
	public String getStateList() {
		return stateList;
	}
	/** 
	 * This method sets queryid attribute of audit element that is an identifier of the MSML object being
	 *  queried by the MSML client. Mandatory. Supported object types: conference or connection  
	 * @param queryId the queryId to set
	 */
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	/**
	 * This method returns queryid attribute of audit element that is an identifier of the MSML object being
	 *  queried by the MSML client. Mandatory. Supported object types: conference or connection  
	 * @return the queryId
	 */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * This method sets optional attribute mark for audit element.
	 * @param mark the mark to set
	 */
	public void setMark(String mark) {
		this.mark = mark;
	}

	/**
	 * This method returns optional attribute mark for audit element.
	 * @return the mark
	 */
	public String getMark() {
		return mark;
	}
	
}
