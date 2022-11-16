package com.baypackets.ase.sysapps.cim.jaxb;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "aconyxUsernameList"
})

@XmlRootElement(name="Get-PCC")
public class GetPCCRequest {

	@XmlElement(name ="AconyxUsername",required=true)
	private List<String> aconyxUsernameList;

	
	/**
	 * Default Constructor
	 */
	public GetPCCRequest() {}

	/**
	 * @param aconyxUsernameList
	 */
	public GetPCCRequest(List<String> aconyxUsernameList) {
		this.aconyxUsernameList = aconyxUsernameList;
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
