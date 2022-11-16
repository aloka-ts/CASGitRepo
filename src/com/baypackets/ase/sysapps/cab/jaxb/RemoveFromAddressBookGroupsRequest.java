package com.baypackets.ase.sysapps.cab.jaxb;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "groupList"
})
@XmlRootElement(name="Remove-From-Address-Book-Groups")
public class RemoveFromAddressBookGroupsRequest {

	@XmlElement(name ="Group",required=true)
	private List<AddressBookGroup> groupList;
	
	public RemoveFromAddressBookGroupsRequest(){}
	/**
	 * @param groupList
	 */
	public RemoveFromAddressBookGroupsRequest(List<AddressBookGroup> groupList) {
		this.groupList = groupList;
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
	
}
