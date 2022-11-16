package com.baypackets.ase.sysapps.cab.jaxb;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "contactViewList",
        "errors"
})
@XmlRootElement(name="Created-Contact-Views")
public class CreateContactViewsResponse {

	@XmlElement(name ="Contact-View")
	private List<ContactView> contactViewList;

	@XmlElement(name = "Error-List")
	private Errors errors;
	
	public CreateContactViewsResponse(){}	

	/**
	 * @param contactViewList
	 * @param errors
	 */
	public CreateContactViewsResponse(List<ContactView> contactViewList,
			Errors errors) {
		this.contactViewList = contactViewList;
		this.errors = errors;
	}

	/**
	 * @param contactViewList the contactViewList to set
	 */
	public void setContactViewList(List<ContactView> contactViewList) {
		this.contactViewList = contactViewList;
	}

	/**
	 * @return the contactViewList
	 */
	public List<ContactView> getContactViewList() {
		return contactViewList;
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
