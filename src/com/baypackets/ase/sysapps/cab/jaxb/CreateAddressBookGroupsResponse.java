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
        "groupList",
        "errors"
})
@XmlRootElement(name="Created-Address-Book-Groups")
public class CreateAddressBookGroupsResponse {

	@XmlElement(name ="Group")
	private List<AddressBookGroup> groupList;
	
	@XmlElement(name = "Error-List")
	private Errors errors;

	public CreateAddressBookGroupsResponse(){}
	/**
	 * @param groupList
	 * @param errors
	 */
	public CreateAddressBookGroupsResponse(List<AddressBookGroup> groupList,
			Errors errors) {
		this.groupList = groupList;
		this.errors = errors;
	}

	/**
	 * @param groupList the groupList to set
	 */
	public void setGroupList(List<AddressBookGroup> groupList) {
		this.groupList = groupList;
	}

	/**
	 * @return the groupList
	 */
	public List<AddressBookGroup> getGroupList() {
		return groupList;
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
