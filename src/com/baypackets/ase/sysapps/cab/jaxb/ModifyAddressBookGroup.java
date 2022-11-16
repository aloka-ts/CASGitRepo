package com.baypackets.ase.sysapps.cab.jaxb;
 
 import java.util.List;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlElementWrapper;
 import javax.xml.bind.annotation.XmlRootElement;
 import javax.xml.bind.annotation.XmlType;
 
 @XmlAccessorType(XmlAccessType.FIELD)
 @XmlType(name="", propOrder={"aconyxUsername", "name", "nonAconyxMemberList", "status"})
 @XmlRootElement(name="ModifyGroup")
 public class ModifyAddressBookGroup
 {
 
   @XmlElement(name="AconyxUsername")
   private String aconyxUsername;
 
   @XmlElement(name="Name")
   private String name;
 
   @XmlElementWrapper(name="NonAconyxMembers")
   @XmlElement(name="NonAconyxMember")
   private List<NonAconyxMember> nonAconyxMemberList;
 
   @XmlElement(name="Status")
   private String status;
 
   public ModifyAddressBookGroup()
   {
   }
 
   public ModifyAddressBookGroup(String aconyxUsername, String name, String contactViewName, List<NonAconyxMember> nonAconyxMemberList, String status)
   {
	   		  this.aconyxUsername = aconyxUsername;
	   		  this.name = name;
	   		  this.nonAconyxMemberList = nonAconyxMemberList;
			  setStatus(status);
   }
 
   public void setAconyxUsername(String aconyxUsername)
   {
/*  59 */     this.aconyxUsername = aconyxUsername;
   }
 
   public String getAconyxUsername()
   {
	   		return this.aconyxUsername;
   }
 
   public void setName(String name)
   {
	   this.name = name;
   }
 
   public String getName()
   {
	   return this.name;
   }
 
   public void setNonAconyxMemberList(List<NonAconyxMember> nonAconyxMemberList)
   {
	   this.nonAconyxMemberList = nonAconyxMemberList;
   }
 
   public List<NonAconyxMember> getNonAconyxMemberList()
   {
	    return this.nonAconyxMemberList;
   }
 
   public void setStatus(String status)
   {
	     this.status = status;
   }
 
   public String getStatus()
   {
	   return this.status;
   }
 }