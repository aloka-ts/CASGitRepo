 package com.baypackets.ase.sysapps.cab.jaxb;
 
 import java.util.List;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 import javax.xml.bind.annotation.XmlType;
 
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name="", propOrder={"groupList", "errors"})
 @XmlRootElement(name="Modified-Address-Book-Group-Members")
 public class ModifyAddressBookGroupMembersResponse
 {
 
   @XmlElement(name="ModifyGroup")
   private List<ModifyAddressBookGroup> groupList;
 
   @XmlElement(name="Error-List")
   private Errors errors;
 
   public ModifyAddressBookGroupMembersResponse()
   {
   }
 
   public ModifyAddressBookGroupMembersResponse(List<ModifyAddressBookGroup> groupList, Errors errors)
   {
	     this.groupList = groupList;
	    this.errors = errors;
   }
 
   public void setGroupList(List<ModifyAddressBookGroup> groupList)
   {
	    this.groupList = groupList;
   }
 
   public List<ModifyAddressBookGroup> getGroupList()
   {
	    return this.groupList;
   }
 
   public void setErrors(Errors errors)
   {
	    this.errors = errors;
   }
 
   public Errors getErrors()
   {
	     return this.errors;
   }
 }
