package com.baypackets.ase.sysapps.cim.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"content",
		"timestamp",
		"type",
		"direction"
})
@XmlRootElement(name="Message")
public class Message {

	@XmlElement(name = "Content")
	private String content;

	@XmlElement(name = "Timestamp")
	private String timestamp;
	
	@XmlElement(name = "Type")
	private String type;
	
	@XmlElement(name = "Direction")
	private String direction;
	
	public Message() {

	}

	public Message(String content, String timestamp, String type,
			String direction) {
		super();
		this.content = content;
		this.timestamp = timestamp;
		this.type = type;
		this.direction = direction;
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


