/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/

package com.agnity.map.datatypes;

import com.agnity.map.enumdata.AccessModeMapEnum;
import com.agnity.map.enumdata.CmiMapEnum;
import com.agnity.map.util.Util;

/**
 * Class to represent Closed User Group Information
 * @author sanjay
 *
 */
public class UserCsgInformationMap {
	/**
	 * TODO: Explore if CGID this could be wrapped in a class
	 */
	// Mandatory attribute
	private byte[] csgId; 
	
	// Optional attribute
	private AccessModeMapEnum accessMode;
	private CmiMapEnum cmi;
	// TODO: ExtensionContainer
	
	public UserCsgInformationMap(byte[] csgId){
		this.csgId = csgId;
	}
	
	public void setCsgId(byte[] csgId) {
		this.csgId = csgId;
	}
	
	public byte[] getCsgId() {
		return this.csgId;
	}
	
	public void setAccessMode(AccessModeMapEnum accessMode) {
		this.accessMode = accessMode;
	}
	
	public AccessModeMapEnum getAccessMode() {
		return this.accessMode;
	}
	
	public void setCmi(CmiMapEnum cmi) {
		this.cmi = cmi;
	}
	
	public CmiMapEnum getCmi() {
		return this.cmi;
	}
	
	public String toString() {
		StringBuilder state = new StringBuilder();
		state.append("CSG ID = ").append(Util.formatBytes(this.csgId)).append("\n");
		state.append("Access Mode = ").append(this.accessMode).append("\n");
		state.append("CMI  = ").append(this.cmi).append("\n");
		
		return state.toString();
	}
}
