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
		"buddy",
		"content",
		"timestamp",
		"type",
		"direction"
})
@XmlRootElement(name="LatestMessage")
public class LatestMessage {

	@XmlElement(name = "Buddy")
	private String buddy;
		
	@XmlElement(name = "Content")
	private String content;

	@XmlElement(name = "Timestamp")
	private String timestamp;
	
	@XmlElement(name = "Type")
	private String type;
	
	@XmlElement(name = "Direction")
	private String direction;
	
	public LatestMessage() {

	}



	/**
	 * @param buddy
	 * @param firstName
	 * @param lastName
	 * @param content
	 * @param timestamp
	 * @param type
	 * @param direction
	 */
	public LatestMessage(String buddy,String content, String timestamp, String type, String direction) {
		super();
		this.setBuddy(buddy);
		this.content = content;
		this.timestamp = timestamp;
		this.type = type;
		this.direction = direction;
	}



	public String getBuddy() {
		return buddy;
	}

	public void setBuddy(String buddy) {
		this.buddy = buddy;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

}


