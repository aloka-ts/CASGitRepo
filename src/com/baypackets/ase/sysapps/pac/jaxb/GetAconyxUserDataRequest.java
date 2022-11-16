package com.baypackets.ase.sysapps.pac.jaxb;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"aconyxUsername"
		})

@XmlRootElement(name="Get-Aconyx-User-Data")
public class GetAconyxUserDataRequest {

	@XmlElement(name = "AconyxUsername",required=true)
	private String aconyxUsername;
	
	public GetAconyxUserDataRequest(){}
	
	public GetAconyxUserDataRequest(String aconyxUsername) {
		super();
		this.aconyxUsername = aconyxUsername;
	}

	/**
	 * @param aconyxUsername the aconyxUsername to set
	 */
	public void setAconyxUsername(String aconyxUsername) {
		this.aconyxUsername = aconyxUsername;
	}

	/**
	 * @return the aconyxUsername
	 */
	public String getAconyxUsername() {
		return aconyxUsername;
	}
}
