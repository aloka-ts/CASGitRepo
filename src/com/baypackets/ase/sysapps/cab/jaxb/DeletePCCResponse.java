package com.baypackets.ase.sysapps.cab.jaxb;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.baypackets.ase.sysapps.cab.jaxb.Errors;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "pccList",
        "errors"
})

@XmlRootElement(name="Deleted-PCC")
public class DeletePCCResponse {
	@XmlElement(name ="PCC")
	private List<PersonalContactCard> pccList;
	@XmlElement(name = "Error-List")
	private Errors errors;
	
	public DeletePCCResponse(){}
	
	/**
	 * @param pccList
	 * @param errors
	 */
	public DeletePCCResponse(List<PersonalContactCard> pccList, Errors errors) {
		this.pccList = pccList;
		this.errors = errors;
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

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	/**
	 * @return the errors
	 */
	public Errors getErrors() {
		return errors;
	}
}
