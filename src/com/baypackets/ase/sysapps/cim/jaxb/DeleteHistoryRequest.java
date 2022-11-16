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
		"aconyxUsernameList"
})

@XmlRootElement(name="DeleteHistoryRequest")
public class DeleteHistoryRequest {
	
	@XmlElement(name = "AconyxUsername")
	private String userName;
	
	@XmlElement(name ="Buddy",required=true)
	private List<String> aconyxUsernameList;
	
	/**
	 * Default Constructor
	 */
	public DeleteHistoryRequest() {}

	/**
	 * @param aconyxUsernameList
	 */
	public DeleteHistoryRequest(String userName,List<String> aconyxUsernameList) {
		this.userName=userName;
		this.aconyxUsernameList = aconyxUsernameList;
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
	public void setAconyxUsernameList(List<String> aconyxUsernameList) {
		this.aconyxUsernameList = aconyxUsernameList;
	}

	/**
	 * @return the aconyxUsernameList
	 */
	public List<String> getAconyxUsernameList() {
		return aconyxUsernameList;
	}
}
