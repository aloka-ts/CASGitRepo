 package com.baypackets.ase.sysapps.cab.jaxb;
 
 import java.util.List;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 import javax.xml.bind.annotation.XmlType;
 
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name="", propOrder={"groupList"})
 @XmlRootElement(name="Modify-Address-Book-Group-Members")
 public class ModifyAddressBookGroupMembersRequest
 {
 
   @XmlElement(name="ModifyGroup", required=true)
   private List<ModifyAddressBookGroup> groupList;
 
   public ModifyAddressBookGroupMembersRequest()
   {
   }
 
   public ModifyAddressBookGroupMembersRequest(List<ModifyAddressBookGroup> groupList)
   {
      this.groupList = groupList;
   }
 
   public void setGroupList(List<ModifyAddressBookGroup> groupList)
   {
     this.groupList = groupList;
   }
 
   public List<ModifyAddressBookGroup> getGroupList()
   {
      return this.groupList;
   }
 }