package com.baypackets.ase.sysapps.cab.jaxb;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "contactViewList"
})
@XmlRootElement(name="Modify-Contact-Views")
public class ModifyContactViewsRequest {

	@XmlElement(name ="Contact-View",required=true)
	private List<ContactView> contactViewList;

	public ModifyContactViewsRequest(){}
	/**
	 * @param contactViewList
	 */
	public ModifyContactViewsRequest(List<ContactView> contactViewList) {
		this.contactViewList = contactViewList;
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
	
}
