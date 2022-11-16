package com.baypackets.ase.sysapps.cim.jaxb;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"userName",
		"result",
		"errors"
})

@XmlRootElement(name="DeleteHistoryResponse")
public class DeleteHistoryResponse {
	
	@XmlElement(name = "AconyxUsername")
	private String userName;
	
	@XmlElement(name ="Result")
	private String result;
	
	@XmlElement(name = "Errors")
	protected Errors errors;
	
	/**
	 * Default Constructor
	 */
	public DeleteHistoryResponse() {}

	/**
	 * @param aconyxUsernameList
	 */
	public DeleteHistoryResponse(String userName,String result) {
		this.userName=userName;
		this.result = result;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @param aconyxUsernameList the aconyxUsernameList to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the aconyxUsernameList
	 */
	public String getResult() {
		return result;
	}
	
	public Errors getErrors() {
		return errors;
	}

	public void setErrors(Errors errors) {
		this.errors = errors;
	}
}
