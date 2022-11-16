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
		"buddy",
		"messages"
})
@XmlRootElement(name="ChatHistory")
public class ChatHistory {

	@XmlElement(name = "Buddy")
	private String buddy;

	@XmlElementWrapper(name = "Messages")
	@XmlElement(name = "Message")
	private List<Message> messages;

	public ChatHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ChatHistory(String buddy, List<Message> messages) {
		super();
		this.buddy = buddy;
		this.messages = messages;
	}

	public String getBuddy() {
		return buddy;
	}

	public void setBuddy(String buddy) {
		this.buddy = buddy;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	
}


