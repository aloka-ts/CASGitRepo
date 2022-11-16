package com.baypackets.ase.sysapps.cab.jaxb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "pccList"
})
@XmlRootElement(name="Create-PCC")
public class CreatePCCRequest {
	@XmlElement(name ="PCC",required=true)
	private List<PersonalContactCard> pccList;

	public CreatePCCRequest(){}
	
	/**
	 * @param pccList
	 */
	public CreatePCCRequest(List<PersonalContactCard> pccList) {
		this.pccList = pccList;
	}

	/**
	 * @param pccList the pccList to set
	 */
	public void setPCCList(List<PersonalContactCard> pccList) {
		this.pccList = pccList;
	}

	/**
	 * @return the pccList
	 */
	public List<PersonalContactCard> getPCCList() {
		return pccList;
	}
	
}
