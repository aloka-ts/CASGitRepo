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
		"chatHistories",
		"errors"
})
@XmlRootElement(name="ChatHistoryResponse")
public class ChatHistoryResponse {

	@XmlElement(name = "Aconyxuser")
	private String aconyxuser;

	@XmlElementWrapper(name = "ChatHistories")
	@XmlElement(name = "ChatHistory")
	private List<ChatHistory> chatHistories;
	
	@XmlElement(name = "Errors")
	protected Errors errors;

	public ChatHistoryResponse() {
		super();
	}

	public ChatHistoryResponse(String aconyxuser,
			List<ChatHistory> chatHistories, Errors errors) {
		super();
		this.aconyxuser = aconyxuser;
		this.chatHistories = chatHistories;
		this.errors = errors;
	}

	public String getAconyxuser() {
		return aconyxuser;
	}

	public void setAconyxuser(String aconyxuser) {
		this.aconyxuser = aconyxuser;
	}

	public List<ChatHistory> getChatHistories() {
		return chatHistories;
	}

	public void setChatHistories(List<ChatHistory> chatHistories) {
		this.chatHistories = chatHistories;
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}


