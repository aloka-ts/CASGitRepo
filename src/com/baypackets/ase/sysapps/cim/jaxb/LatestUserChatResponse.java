/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/
package com.baypackets.ase.sysapps.cim.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxuser",
		"latestMessages",
		"errors"
})
@XmlRootElement(name="LatestUserChatResponse")
public class LatestUserChatResponse {

	@XmlElement(name = "Aconyxuser")
	private String aconyxuser;
	
	@XmlElementWrapper(name = "LatestMessages")
	@XmlElement(name = "LatestMessage")
	private List<LatestMessage> latestMessages;
	
	@XmlElement(name = "Errors")
	protected Errors errors;
	
	public LatestUserChatResponse() {
		super();
	}


	/**
	 * @param aconyxuser
	 * @param latestMessages
	 */
	public LatestUserChatResponse(String aconyxuser,
			List<LatestMessage> latestMessages) {
		super();
		this.aconyxuser = aconyxuser;
		this.setLatestMessages(latestMessages);
	}

	public String getAconyxuser() {
		return aconyxuser;
	}
	
	public void setAconyxuser(String aconyxuser) {
		this.aconyxuser = aconyxuser;
	}

	public List<LatestMessage> getLatestMessages() {
		return latestMessages;
	}

	public void setLatestMessages(List<LatestMessage> latestMessages) {
		this.latestMessages = latestMessages;
	}
	
	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
	
}


