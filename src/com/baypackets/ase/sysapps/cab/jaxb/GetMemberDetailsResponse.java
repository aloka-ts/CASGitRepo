package com.baypackets.ase.sysapps.cab.jaxb;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.baypackets.ase.sysapps.cab.jaxb.Errors;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "aconyxUsername",
		"pccList",
        "errors"
})
@XmlRootElement(name="Get-Member-Details")
public class GetMemberDetailsResponse {

	@XmlElement(name = "AconyxUsername")
	private String aconyxUsername;
	
	@XmlElementWrapper(name="Members")
	@XmlElement(name ="PCC")
	private List<PersonalContactCard> pccList;
	
	@XmlElement(name = "Error-List")
	private Errors errors;

	public GetMemberDetailsResponse(){}
	 
	/**
	 * @param aconyxUsername
	 * @param pccList
	 * @param errors
	 */
	public GetMemberDetailsResponse(String aconyxUsername,
			List<PersonalContactCard> pccList, Errors errors) {
		this.aconyxUsername = aconyxUsername;
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
