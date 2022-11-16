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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxuser"
})
@XmlRootElement(name="LatestUserChatRequest")
public class LatestUserChatRequest {

	@XmlElement(name = "Aconyxuser")
	private String aconyxuser;
	
	public LatestUserChatRequest() {
		super();
	}
	public LatestUserChatRequest(String aconyxuser) {
		super();
		this.aconyxuser = aconyxuser;
	}
	public String getAconyxuser() {
		return aconyxuser;
	}
	public void setAconyxuser(String aconyxuser) {
		this.aconyxuser = aconyxuser;
	}
}


