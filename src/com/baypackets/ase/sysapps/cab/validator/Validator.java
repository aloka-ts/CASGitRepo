/*
 * Validator.java
 * @author Amit Baxi
 */
package com.baypackets.ase.sysapps.cab.validator;
import javax.servlet.sip.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import org.apache.log4j.Logger;
import com.baypackets.ase.sysapps.cab.jaxb.*;
import com.baypackets.ase.sysapps.cab.manager.CABManager;
import com.baypackets.ase.sysapps.cab.receiver.CABSIPServlet;
import com.baypackets.ase.sysapps.cab.util.*;
/**
 * This class provides method for validation of REST Requests for CAb Application.
 */
public class Validator {	
	private static Logger logger = Logger.getLogger(Validator.class.getName());
	private Errors errorList=new Errors();
	
	 public enum EnumOperation {
		   CREATE,MODIFY,DELETE
		 }
	 
	public Errors getErrorList() {
		return errorList;
	}	

	public boolean validateCreatePCCRequest(CreatePCCRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<PersonalContactCard> pccList = request.getPCCList();
			if (pccList == null || pccList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_005, ErrorCodes.ERROR_005_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateModifyPCCRequest(ModifyPCCRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<PersonalContactCard> pccList = request.getPCCList();
			if (pccList == null || pccList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_005, ErrorCodes.ERROR_005_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}	


	public void validatePCCFields(PersonalContactCard personalContactCard) {
		String aconyxUsername=personalContactCard.getAconyxUsername();
		String firstName=personalContactCard.getFirstName();		
		String lastName=personalContactCard.getLastName();
		String contact1=personalContactCard.getContact1();
		String contact2=personalContactCard.getContact2();
		String address=personalContactCard.getAddress();
		String city=personalContactCard.getCity();
		String state=personalContactCard.getState();
		String country=personalContactCard.getCountry();
		String designation=personalContactCard.getDesignation();
		String company=personalContactCard.getCompany();
		String department=personalContactCard.getDepartment();		
		String gender=personalContactCard.getGender();
		String dob=personalContactCard.getDob();
		String email1=personalContactCard.getEmail1();
		String email2=personalContactCard.getEmail2();
		
		if(this.validateAconyxUsername(aconyxUsername))
			return;	
			if(firstName==null || firstName.trim().length()==0)
				errorList.addError(ErrorCodes.ERROR_003, ErrorCodes.ERROR_003_DESC+"FirstName");
			if(lastName==null || lastName.trim().length()==0)
				errorList.addError(ErrorCodes.ERROR_003, ErrorCodes.ERROR_003_DESC+"LastName");
		
		if(address==null || address.trim().length()==0)
		personalContactCard.setAddress(null);
		
		
		if(city==null || city.trim().length()==0)
		personalContactCard.setCity(null);
		
		
		if(state==null || state.trim().length()==0)
		personalContactCard.setState(null);
		
		
		if(country==null || country.trim().length()==0)
		personalContactCard.setCountry(null);
		
		
		if(company==null || company.trim().length()==0)
		personalContactCard.setCompany(null);
		
		
		if(department==null || department.trim().length()==0)
		personalContactCard.setDepartment(null);
		
		
		if(designation==null || designation.trim().length()==0)
		personalContactCard.setDesignation(null);
		
		if(contact1!=null && contact1.trim().length()!=0)
			this.validateByPattern(contact1,"Contact1",Constants.PATTERN_CONTACT);
		else 
			personalContactCard.setContact1(null);
		
		if(contact2!=null && contact2.trim().length()!=0)
			this.validateByPattern(contact2,"Contact2",Constants.PATTERN_CONTACT);
		else 
			personalContactCard.setContact2(null);
		
		if(gender!=null && gender.trim().length()!=0)
			this.validateGender(gender);
		else 
			personalContactCard.setGender(null);
		
		if(email1!=null && email1.trim().length()!=0)
			this.validateByPattern(email1,"Email1",Constants.PATTERN_EMAIL);
		else 
			personalContactCard.setEmail1(null);
		
		if(email2!=null && email2.trim().length()!=0)
			this.validateByPattern(email2,"Email2",Constants.PATTERN_EMAIL);
		else 
			personalContactCard.setEmail2(null);
		
		if(dob!=null && dob.trim().length()!=0)
			this.validateByPattern(dob,"DOB",Constants.PATTERN_DATE);
		else 
			personalContactCard.setDob(null);
		
	}

	private void validateByPattern(String field,String fieldName,String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(field);
		if (!m.matches()) {
			errorList.addError(ErrorCodes.ERROR_004, ErrorCodes.ERROR_004_DESC+fieldName);
		}
	}

	private void validateGender(String gender) {
		if (!(gender.equalsIgnoreCase("Male")||gender.equalsIgnoreCase("Female"))) {
			errorList.addError(ErrorCodes.ERROR_004, ErrorCodes.ERROR_004_DESC+"Gender");
		}	
	}

	public boolean validateDeletePCCRequest(DeletePCCRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<String> aconyxUsernameList = request.getAconyxUsernameList();
			if (aconyxUsernameList == null || aconyxUsernameList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
				checkNull = true;
			}else{		
				int size=aconyxUsernameList.size();
				for(int i=0;i<size;i++){
					if(validateAconyxUsername(aconyxUsernameList.get(i)))
						checkNull=true;		
				}
			} 

		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateGetPCCRequest(GetPCCRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<String> aconyxUsernameList = request.getAconyxUsernameList();
			if (aconyxUsernameList == null || aconyxUsernameList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
				checkNull = true;
			}else{		
				int size=aconyxUsernameList.size();
				for(int i=0;i<size;i++){
					if(validateAconyxUsername(aconyxUsernameList.get(i)))
						checkNull=true;		
				}
			} 

		}else{
			checkNull=true;
		}
		return checkNull;
	}
	public boolean validateGetContactViewsRequest(GetContactViewsRequest request){
		boolean checkNull=false;		
		if (request != null) {
			List<String> aconyxUsernameList = request.getAconyxUsernameList();
			if (aconyxUsernameList == null || aconyxUsernameList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
				checkNull = true;
			}else{		
				int size=aconyxUsernameList.size();
				for(int i=0;i<size;i++){
					if(validateAconyxUsername(aconyxUsernameList.get(i)))
						checkNull=true;		
				}
			} 

		}else{
			checkNull=true;
		}
		return checkNull;
	}

	private boolean validateAconyxUsername(String aconyxUserName){
		boolean checkNull=false;
		if(aconyxUserName == null || aconyxUserName.trim().length() == 0){
			errorList.addError(ErrorCodes.ERROR_001, ErrorCodes.ERROR_001_DESC);
			checkNull=true;
		}
		else if(this.validateForSpecialChars("AconyxUsername", aconyxUserName))
			checkNull=true;
		return checkNull;
	}

	private boolean validateForSpecialChars(String attributeName,String attributeVal){
		boolean isInvalid=false;
		if(attributeName!=null && attributeVal!=null){
			Pattern p = Pattern.compile(Constants.SPECIAL_CHAR_PATTERN);
			Matcher m = p.matcher(attributeVal);
			if (!m.matches()) {
				errorList.addError(ErrorCodes.ERROR_022, ErrorCodes.ERROR_022_DESC+attributeName);
				isInvalid=true;
			}	
		}
		return isInvalid;
	}

	public void validateContactViews(List<ContactView> contactViewList,EnumOperation operation) {
		if(contactViewList!=null && contactViewList.size()!=0){
			for(ContactView cView:contactViewList){
				if(cView!=null){
					if(this.validateAconyxUsername(cView.getAconyxUsername()))
						return;
					String name=cView.getName();
					List <String> fieldList=cView.getFieldList(); 
					List <String> validatedFields=new LinkedList<String>();
					if(name!=null && name.trim().length()!=0){
						this.validateForSpecialChars("Name",name);
						if(operation.equals(EnumOperation.DELETE) && Constants.DEFAULT_CONTACT_VIEW_NAME.equals(name))
							errorList.addError(ErrorCodes.ERROR_026, ErrorCodes.ERROR_026_DESC+Constants.DEFAULT_CONTACT_VIEW_NAME);
					}
					else
						errorList.addError(ErrorCodes.ERROR_008, ErrorCodes.ERROR_008_DESC);
					if(!operation.equals(EnumOperation.DELETE)){
						if(fieldList!=null)
						{
							for(String fieldName:fieldList){							
								if(fieldName==null || fieldName.trim().isEmpty()){
									errorList.addError(ErrorCodes.ERROR_011, ErrorCodes.ERROR_011_DESC);	
								}
								else if(!validatedFields.contains(fieldName)){
									if(!CABManager.FIELD_ID_MAP.containsKey(fieldName))
										errorList.addError(ErrorCodes.ERROR_010, ErrorCodes.ERROR_010_DESC+fieldName+". Valid fields are:"+CABManager.FIELD_ID_MAP.keySet());
									validatedFields.add(fieldName);
								}else{
									errorList.addError(ErrorCodes.ERROR_023, ErrorCodes.ERROR_023_DESC+fieldName);
								}

							}
							if(operation.equals(EnumOperation.MODIFY)&& Constants.DEFAULT_CONTACT_VIEW_NAME.equals(name)){
								for(String fieldName:CABManager.DEFAULT_CONTACT_VIEW_FIELDS){
									if(!validatedFields.contains(fieldName))
										errorList.addError(ErrorCodes.ERROR_027, ErrorCodes.ERROR_027_DESC+fieldName);
								}
							}
							validatedFields.clear();
						}
						else
							errorList.addError(ErrorCodes.ERROR_009, ErrorCodes.ERROR_009_DESC);	
					}				
				}
			}
		}else {
			errorList.addError(ErrorCodes.ERROR_013, ErrorCodes.ERROR_013_DESC);
		}
	}

	public boolean validateCreateAddressBookGroupsRequest(
			CreateAddressBookGroupsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<AddressBookGroup> groupList = request.getGroupList();
			if (groupList == null || groupList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public void validateAddressBookGroup(AddressBookGroup group,boolean checkNameReq,boolean checkContactView,boolean checkMembers,boolean isDelete) {
		String aconyxUsername=group.getAconyxUsername();
		String name=group.getName();
		String contactViewName=group.getContactViewName();
		List <String> memberList=group.getMemberList();
		List <NonAconyxMember> nonAconyxmemberList=group.getNonAconyxMemberList();
		if(this.validateAconyxUsername(aconyxUsername))
			return;
		if(name!=null && name.trim().length()!=0){
			this.validateForSpecialChars("Address Book Group Name",name);
			if(isDelete &&Constants.DEFAULT_ADDRESS_BOOK_GROUP_NAME.equals(name))
				errorList.addError(ErrorCodes.ERROR_031, ErrorCodes.ERROR_031_DESC+Constants.DEFAULT_ADDRESS_BOOK_GROUP_NAME);
		}
		else if(!checkNameReq)
			group.setName(Constants.DEFAULT_ADDRESS_BOOK_GROUP_NAME);
		else
			errorList.addError(ErrorCodes.ERROR_016, ErrorCodes.ERROR_016_DESC);
		
		if(checkContactView){
			if(contactViewName!=null && contactViewName.trim().length()!=0){
				this.validateForSpecialChars("Contact View Name",contactViewName);
			}
			else
				errorList.addError(ErrorCodes.ERROR_008, ErrorCodes.ERROR_008_DESC);
		}
		
		
		if((memberList!=null && memberList.size()!=0)||(nonAconyxmemberList!=null && nonAconyxmemberList.size()!=0)){
			if(memberList!=null){
			for(String member:memberList){
				if((member==null || member.trim().length()==0)){
					errorList.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);	
				}
				else 
					this.validateForSpecialChars("Address Book Group Member", member);
			}
			}if(nonAconyxmemberList!=null){
				for(NonAconyxMember nonAconyxMember:nonAconyxmemberList){
					if(nonAconyxMember==null){
						errorList.addError(ErrorCodes.ERROR_032, ErrorCodes.ERROR_032_DESC);	
					}
					else 
						this.validateNonAconyxMember(nonAconyxMember);
				}
			}
		}else if(checkMembers)
				errorList.addError(ErrorCodes.ERROR_020, ErrorCodes.ERROR_020_DESC);
		
	}

	private void validateNonAconyxMember(NonAconyxMember member) {
		String name=member.getName();
		String contact=member.getContact();
		String sipAddress=member.getSIPAddress();
		if(name!=null && name.trim().length()!=0 )
			this.validateForSpecialChars("Address Book Group Non Aconyx Member", name);
		else
			errorList.addError(ErrorCodes.ERROR_034, ErrorCodes.ERROR_034_DESC+"Name");
		
		if(contact!=null && contact.trim().length()!=0){
			//this.validateByPattern(contact,"Contact",Constants.PATTERN_CONTACT);
		}
		else 
			member.setContact(null);

		if(sipAddress!=null && sipAddress.trim().length()!=0)
		{
			SipFactory fac = CABSIPServlet.getSipFactory();
			try {
				URI uri=fac.createURI(sipAddress);
				if(uri.isSipURI())
					return;
			} catch (ServletParseException e) {
				logger.debug("Exception while creating URI....");
			}
			errorList.addError(ErrorCodes.ERROR_033, ErrorCodes.ERROR_033_DESC+sipAddress);
		}
		else 
			member.setSIPAddress(null);

	}

	public boolean validateDeleteAddressBookGroupsRequest(
			DeleteAddressBookGroupsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<AddressBookGroup> groupList = request.getGroupList();
			if (groupList == null || groupList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateAddToAddressBookGroupsRequest(
			AddToAddressBookGroupsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<AddressBookGroup> groupList = request.getGroupList();
			if (groupList == null || groupList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateRemoveFromAddressBookGroupsRequest(
			RemoveFromAddressBookGroupsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<AddressBookGroup> groupList = request.getGroupList();
			if (groupList == null || groupList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateAssociateContactViewsRequest(
			AssociateContactViewsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<AddressBookGroup> groupList = request.getGroupList();
			if (groupList == null || groupList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateGetAddressBookGroupsRequest(
			GetAddressBookGroupsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			List<AddressBookGroup> groupList = request.getGroupList();
			if (groupList == null || groupList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
				checkNull = true;
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}
	
	public boolean validateGetAllAddressBookGroupsRequest(
			GetAllAddressBookGroupsRequest request)
	{
		boolean checkNull=false;		
		if (request != null) {
			List<String> aconyxUsernameList = request.getAconyxUsernameList();
			if (aconyxUsernameList == null || aconyxUsernameList.size() == 0) {
				errorList.addError(ErrorCodes.ERROR_002, ErrorCodes.ERROR_002_DESC);
				checkNull = true;
			}else{		
				int size=aconyxUsernameList.size();
				for(int i=0;i<size;i++){
					if(validateAconyxUsername(aconyxUsernameList.get(i)))
						checkNull=true;		
				}
			} 
		}else{
			checkNull=true;
		}
		return checkNull;
	}

	public boolean validateGetMemberDetailsRequest(GetMemberDetailsRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			if(this.validateAconyxUsername(request.getAconyxUsername()))
				checkNull=true;
			List<String> memberList=request.getMemberList();
			if(memberList==null || memberList.size()==0){
				errorList.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);
				checkNull=true;
			}
		} else{
			checkNull=true;
		}
		return checkNull;
	}
	
	public void validateMemberList(List<String> memberList){
		for(String member:memberList){
			if((member==null || member.trim().length()==0)){
				errorList.addError(ErrorCodes.ERROR_025, ErrorCodes.ERROR_025_DESC);	
			}
			else 
				this.validateForSpecialChars("Address Book Group Member", member);
	}
		}

	public boolean validateSearchUserRequest(SearchUsersRequest request) {
		boolean checkNull=false;		
		if (request != null) {
			if(this.validateAconyxUsername(request.getAconyxUsername()))
				checkNull=true;
			if(request.getSearchBy()==null || request.getSearchBy().trim().length()==0){
				errorList.addError(ErrorCodes.ERROR_028, ErrorCodes.ERROR_028_DESC+"SearchBy");
				checkNull=true;
			}
			if(request.getSearchValue()==null || request.getSearchValue().trim().length()==0){
				errorList.addError(ErrorCodes.ERROR_028, ErrorCodes.ERROR_028_DESC+"SearchValue");
				checkNull=true;
			}
		} else{
			checkNull=true;
		}
		return checkNull;
	}

	public void validateSearchBy(String searchBy) {
		if(!CABManager.FIELD_ID_MAP.containsKey(searchBy))
			errorList.addError(ErrorCodes.ERROR_029, ErrorCodes.ERROR_029_DESC+CABManager.FIELD_ID_MAP.keySet());
	}

	public boolean validateModifyAddressBookGroupMembersRequest(ModifyAddressBookGroupMembersRequest request) {
		 boolean checkNull = false;
		 if (request != null) {
		        List<ModifyAddressBookGroup> groupList = request.getGroupList();
		        if ((groupList == null) || (groupList.size() == 0)) {
		          this.errorList.addError(ErrorCodes.ERROR_024, ErrorCodes.ERROR_024_DESC);
		         checkNull = true;
		       }
		      } else {
		       checkNull = true;
		     }
		      return checkNull;
	}

	public void validateModifyAddressBookGroup(ModifyAddressBookGroup group) {
		 String aconyxUsername = group.getAconyxUsername();
		       String name = group.getName();
		      List <NonAconyxMember> nonAconyxmemberList = group.getNonAconyxMemberList();
		      if (validateAconyxUsername(aconyxUsername))
		        return;
		      if ((name != null) && (name.trim().length() != 0)) {
		        validateForSpecialChars("Address Book Group Name", name);
		      }
		      else {
		        group.setName("Default");
		      }
		      if (nonAconyxmemberList != null) {
		        for (NonAconyxMember nonAconyxMember : nonAconyxmemberList) {
		           if (nonAconyxMember == null) {
		            this.errorList.addError(ErrorCodes.ERROR_032, ErrorCodes.ERROR_032_DESC);
		         }
		          else
		           validateNonAconyxMember(nonAconyxMember);
		         }
		     }
		      else
		         this.errorList.addError(ErrorCodes.ERROR_020, ErrorCodes.ERROR_020_DESC);
		   }
}
