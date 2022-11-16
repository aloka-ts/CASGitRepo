
package com.genband.m5.maps.sp.mbeans;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jboss.portal.identity.MembershipModule;
import org.jboss.portal.identity.RoleModule;
import org.jboss.portal.identity.User;
import org.jboss.portal.identity.UserModule;
import org.jboss.portal.identity.db.HibernateRoleImpl;
import org.jboss.security.SecurityAssociation;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.common.MgmtPortletUtil;
import com.genband.m5.maps.common.SS_Constants;
import com.genband.m5.maps.common.entity.Country;
import com.genband.m5.maps.common.entity.Organization;
import com.genband.m5.maps.common.entity.OrganizationAddress;
import com.genband.m5.maps.common.entity.OrganizationContactInfo;
import com.genband.m5.maps.identity.GBUserPrincipal;

/**
		This is the managed Bean class for Organization 
		@Genband.com
*/
public class  CreateMBean_270 {

		private static final Logger LOG = Logger.getLogger(CreateMBean_270.class);
		private UserModule userModule;
		private RoleModule roleModule;
		private MembershipModule membershipModule;

		private java.lang.String customerId;
		
		private java.lang.String name;
		
		private java.lang.String displayName;
		
		private java.lang.String domainName;
		
		private java.lang.String description;
		
		private java.util.Date activationDate;
		
		private java.util.Date expirationDate;
		
		private java.util.Date lastUpdated;
		
		private java.lang.String timezone;
		
		private java.lang.Integer status;
		
		private java.lang.String address2StreetAddress1;
		
		private java.lang.String address2StreetAddress2;
		
		private java.lang.String address2City;
		
		private java.lang.String address2State;
		
		private java.lang.Long address2country;
		
		private java.lang.String address2Zip;
		
		private java.lang.String contactInfo1ContactPerson;
		
		private java.lang.String contactInfo1EmailId1;
		
		private java.lang.String contactInfo1EmailId2;
		
		private java.lang.String contactInfo1PhoneNumber;
		
		private java.lang.String contactInfo1AlternatePhoneNumber;
		
		private java.lang.String contactInfo1MobileNumber;
		
		private java.lang.String contactInfo1FaxNumber;
		
		private java.lang.String contactInfo2ContactPerson;
		
		private java.lang.String contactInfo2EmailId1;
		
		private java.lang.String contactInfo2EmailId2;
		
		private java.lang.String contactInfo2PhoneNumber;
		
		private java.lang.String contactInfo2AlternatePhoneNumber;
		
		private java.lang.String contactInfo2MobileNumber;
		
		private java.lang.String contactInfo2FaxNumber;
		
		private java.lang.String address1StreetAddress1;
		
		private java.lang.String address1StreetAddress2;
		
		private java.lang.String address1City;
		
		private java.lang.String address1State;
		
		private java.lang.Long country;
		
		private java.lang.String address1Zip;
		
		private static List<SelectItem> bstatusTV;
		
		private int selectedStatus;
		private Long address2PKValue;
		private Long contactInfo1PKValue;
		private Long contactInfo2PKValue;
		private Long address1PKValue;
		private List<SelectItem> address2CountryIsoCode;
		private List<SelectItem> address1CountryIsoCode;
		private int selectedCountry;
		private int selectedCountry2;
		private int mode;
		
		private Character account_Type = null;
		
		private Long primarykeyValue;
		
		private int operationId;
		
		private String userRole;
		
		private List<Boolean> listVisibility;
		
		private Criteria criteria;
		
		private int pageNo = 1;
		
		private Boolean secondaryDetails = false;
		
//Varibles declaration End...........................................

		public CreateMBean_270 () {
		}
		
			//Loading Static data...
		static {
			SelectItem temp = null;

			bstatusTV = new ArrayList<SelectItem>();
			temp = new SelectItem();
			temp.setValue("3");
			temp.setLabel("Suspended");
			bstatusTV.add(temp);
			temp = new SelectItem();
			temp.setValue("2");
			temp.setLabel("In-Active");
			bstatusTV.add(temp);
			temp = new SelectItem();
			temp.setValue("1");
			temp.setLabel("Active");
			bstatusTV.add(temp);
			
		}	
		public java.lang.String getCustomerId () {
			return this.customerId;
		}
		 
		public java.lang.String getName () {
			return this.name;
		}
		 
		public java.lang.String getDisplayName () {
			return this.displayName;
		}
		 
		public java.lang.String getDomainName () {
			return this.domainName;
		}
		 
		public java.lang.String getDescription () {
			return this.description;
		}
		 
		public java.util.Date getActivationDate () {
			return this.activationDate;
		}
		 
		public java.util.Date getExpirationDate () {
			return this.expirationDate;
		}
		 
		public java.util.Date getLastUpdated () {
			return this.lastUpdated;
		}
		 
		public java.lang.String getTimezone () {
			return this.timezone;
		}
		 
		public java.lang.Integer getStatus () {
			return this.status;
		}
		 
		public java.lang.String getAddress2StreetAddress1 () {
			return this.address2StreetAddress1;
		}
		 
		public java.lang.String getAddress2StreetAddress2 () {
			return this.address2StreetAddress2;
		}
		 
		public java.lang.String getAddress2City () {
			return this.address2City;
		}
		 
		public java.lang.String getAddress2State () {
			return this.address2State;
		}
		 
		public java.lang.Long getAddress2country () {
			return this.address2country;
		}
		 
		public java.lang.String getAddress2Zip () {
			return this.address2Zip;
		}
		 
		public java.lang.String getContactInfo1ContactPerson () {
			return this.contactInfo1ContactPerson;
		}
		 
		public java.lang.String getContactInfo1EmailId1 () {
			return this.contactInfo1EmailId1;
		}
		 
		public java.lang.String getContactInfo1EmailId2 () {
			return this.contactInfo1EmailId2;
		}
		 
		public java.lang.String getContactInfo1PhoneNumber () {
			return this.contactInfo1PhoneNumber;
		}
		 
		public java.lang.String getContactInfo1AlternatePhoneNumber () {
			return this.contactInfo1AlternatePhoneNumber;
		}
		 
		public java.lang.String getContactInfo1MobileNumber () {
			return this.contactInfo1MobileNumber;
		}
		 
		public java.lang.String getContactInfo1FaxNumber () {
			return this.contactInfo1FaxNumber;
		}
		 
		public java.lang.String getContactInfo2ContactPerson () {
			return this.contactInfo2ContactPerson;
		}
		 
		public java.lang.String getContactInfo2EmailId1 () {
			return this.contactInfo2EmailId1;
		}
		 
		public java.lang.String getContactInfo2EmailId2 () {
			return this.contactInfo2EmailId2;
		}
		 
		public java.lang.String getContactInfo2PhoneNumber () {
			return this.contactInfo2PhoneNumber;
		}
		 
		public java.lang.String getContactInfo2AlternatePhoneNumber () {
			return this.contactInfo2AlternatePhoneNumber;
		}
		 
		public java.lang.String getContactInfo2MobileNumber () {
			return this.contactInfo2MobileNumber;
		}
		 
		public java.lang.String getContactInfo2FaxNumber () {
			return this.contactInfo2FaxNumber;
		}
		 
		public java.lang.String getAddress1StreetAddress1 () {
			return this.address1StreetAddress1;
		}
		 
		public java.lang.String getAddress1StreetAddress2 () {
			return this.address1StreetAddress2;
		}
		 
		public java.lang.String getAddress1City () {
			return this.address1City;
		}
		 
		public java.lang.String getAddress1State () {
			return this.address1State;
		}
		 
		public java.lang.Long getCountry () {
			return this.country;
		}
		 
		public java.lang.String getAddress1Zip () {
			return this.address1Zip;
		}
		 
		public Long getAddress2PKValue () {
			return this.address2PKValue;
		}
		public Long getContactInfo1PKValue () {
			return this.contactInfo1PKValue;
		}
		public Long getContactInfo2PKValue () {
			return this.contactInfo2PKValue;
		}
		public Long getAddress1PKValue () {
			return this.address1PKValue;
		}
		public List<SelectItem> getAddress2CountryIsoCode () {
			return this.address2CountryIsoCode;
		}
		public List<SelectItem> getAddress1CountryIsoCode () {
			return this.address1CountryIsoCode;
		}
		public int getSelectedCountry2 () {
			return this.selectedCountry2;
		}
		public int getSelectedCountry () {
			return this.selectedCountry2;
		}
		public List<SelectItem> getBstatusTV () {
			return this.bstatusTV;
		} 
		
		public int getSelectedStatus () {
			return this.selectedStatus;
		}
		public int getMode () {
			return this.mode;
		}
		
		public Long getPrimarykeyValue () {
			return this.primarykeyValue;
		}
		
		public String getUserRole() {
			return userRole;
		}
		
		public List<Boolean> getListVisibility() {
			return listVisibility;
		}
		
		public Criteria getCriteria() {
			return criteria;
		}

		public void setCustomerId (java.lang.String customerId) {
			this.customerId = customerId;
		}
		 
		public void setName (java.lang.String name) {
			this.name = name;
		}
		 
		public void setDisplayName (java.lang.String displayName) {
			this.displayName = displayName;
		}
		 
		public void setDomainName (java.lang.String domainName) {
			this.domainName = domainName;
		}
		 
		public void setDescription (java.lang.String description) {
			this.description = description;
		}
		 
		public void setActivationDate (java.util.Date activationDate) {
			this.activationDate = activationDate;
		}
		 
		public void setExpirationDate (java.util.Date expirationDate) {
			this.expirationDate = expirationDate;
		}
		 
		public void setLastUpdated (java.util.Date lastUpdated) {
			this.lastUpdated = lastUpdated;
		}
		 
		public void setTimezone (java.lang.String timezone) {
			this.timezone = timezone;
		}
		 
		public void setStatus (java.lang.Integer status) {
			this.status = status;
		}
		 
		public void setAddress2StreetAddress1 (java.lang.String address2StreetAddress1) {
			this.address2StreetAddress1 = address2StreetAddress1;
		}
		 
		public void setAddress2StreetAddress2 (java.lang.String address2StreetAddress2) {
			this.address2StreetAddress2 = address2StreetAddress2;
		}
		 
		public void setAddress2City (java.lang.String address2City) {
			this.address2City = address2City;
		}
		 
		public void setAddress2State (java.lang.String address2State) {
			this.address2State = address2State;
		}
		 
		public void setAddress2country (java.lang.Long address2country) {
			this.address2country = address2country;
		}
		 
		public void setAddress2Zip (java.lang.String address2Zip) {
			this.address2Zip = address2Zip;
		}
		 
		public void setContactInfo1ContactPerson (java.lang.String contactInfo1ContactPerson) {
			this.contactInfo1ContactPerson = contactInfo1ContactPerson;
		}
		 
		public void setContactInfo1EmailId1 (java.lang.String contactInfo1EmailId1) {
			this.contactInfo1EmailId1 = contactInfo1EmailId1;
		}
		 
		public void setContactInfo1EmailId2 (java.lang.String contactInfo1EmailId2) {
			this.contactInfo1EmailId2 = contactInfo1EmailId2;
		}
		 
		public void setContactInfo1PhoneNumber (java.lang.String contactInfo1PhoneNumber) {
			this.contactInfo1PhoneNumber = contactInfo1PhoneNumber;
		}
		 
		public void setContactInfo1AlternatePhoneNumber (java.lang.String contactInfo1AlternatePhoneNumber) {
			this.contactInfo1AlternatePhoneNumber = contactInfo1AlternatePhoneNumber;
		}
		 
		public void setContactInfo1MobileNumber (java.lang.String contactInfo1MobileNumber) {
			this.contactInfo1MobileNumber = contactInfo1MobileNumber;
		}
		 
		public void setContactInfo1FaxNumber (java.lang.String contactInfo1FaxNumber) {
			this.contactInfo1FaxNumber = contactInfo1FaxNumber;
		}
		 
		public void setContactInfo2ContactPerson (java.lang.String contactInfo2ContactPerson) {
			this.contactInfo2ContactPerson = contactInfo2ContactPerson;
		}
		 
		public void setContactInfo2EmailId1 (java.lang.String contactInfo2EmailId1) {
			this.contactInfo2EmailId1 = contactInfo2EmailId1;
		}
		 
		public void setContactInfo2EmailId2 (java.lang.String contactInfo2EmailId2) {
			this.contactInfo2EmailId2 = contactInfo2EmailId2;
		}
		 
		public void setContactInfo2PhoneNumber (java.lang.String contactInfo2PhoneNumber) {
			this.contactInfo2PhoneNumber = contactInfo2PhoneNumber;
		}
		 
		public void setContactInfo2AlternatePhoneNumber (java.lang.String contactInfo2AlternatePhoneNumber) {
			this.contactInfo2AlternatePhoneNumber = contactInfo2AlternatePhoneNumber;
		}
		 
		public void setContactInfo2MobileNumber (java.lang.String contactInfo2MobileNumber) {
			this.contactInfo2MobileNumber = contactInfo2MobileNumber;
		}
		 
		public void setContactInfo2FaxNumber (java.lang.String contactInfo2FaxNumber) {
			this.contactInfo2FaxNumber = contactInfo2FaxNumber;
		}
		 
		public void setAddress1StreetAddress1 (java.lang.String address1StreetAddress1) {
			this.address1StreetAddress1 = address1StreetAddress1;
		}
		 
		public void setAddress1StreetAddress2 (java.lang.String address1StreetAddress2) {
			this.address1StreetAddress2 = address1StreetAddress2;
		}
		 
		public void setAddress1City (java.lang.String address1City) {
			this.address1City = address1City;
		}
		 
		public void setAddress1State (java.lang.String address1State) {
			this.address1State = address1State;
		}
		 
		public void setCountry (java.lang.Long country) {
			this.country = country;
		}
		 
		public void setAddress1Zip (java.lang.String address1Zip) {
			this.address1Zip = address1Zip;
		}
		 
		public void setAddress2PKValue (Long address2PKValue) {
			this.address2PKValue = address2PKValue;
		}
		public void setContactInfo1PKValue (Long contactInfo1PKValue) {
			this.contactInfo1PKValue = contactInfo1PKValue;
		}
		public void setContactInfo2PKValue (Long contactInfo2PKValue) {
			this.contactInfo2PKValue = contactInfo2PKValue;
		}
		public void setAddress1PKValue (Long address1PKValue) {
			this.address1PKValue = address1PKValue;
		}
		public void setAddress2CountryIsoCode(List<SelectItem> address2CountryIsoCode) {
			this.address2CountryIsoCode = address2CountryIsoCode;
		}
		public void setAddress1CountryIsoCode(List<SelectItem> address1CountryIsoCode) {
			this.address1CountryIsoCode = address1CountryIsoCode;
		}
		public void setSelectedCountry2 (int selectedCountry2) {
			this.selectedCountry2 = selectedCountry2;
		}
		public void setSelectedCountry (int selectedCountry) {
			this.selectedCountry = selectedCountry;
		}
		public void setBstatusTV (List<SelectItem> temp) {
			this.bstatusTV = bstatusTV;
		}
		
		public void setSelectedStatus (int selectedStatus) {
			this.selectedStatus = selectedStatus;
		}
		public void setMode (int mode) {
			this.mode = mode;
		}
		
		public void setPrimarykeyValue (Long primarykeyValue) {
			this.primarykeyValue = primarykeyValue;
		}
			
		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}

		public void setListVisibility(List<Boolean> listVisibility) {
			this.listVisibility = listVisibility;
		}

		public void setCriteria(Criteria criteria) {
			this.criteria = criteria;
		}
		
		public int getPageNo() {
			return pageNo;
		}

		public void setPageNo(int pageNo) {
			this.pageNo = pageNo;
		}

		public Boolean getSecondaryDetails() {
			return secondaryDetails;
		}

		public void setSecondaryDetails(Boolean secondaryDetails) {
			this.secondaryDetails = secondaryDetails;
		}

		//Generating getObject Function which will set values for ModelEntity and returns ModelEntity.  This itself will do process for Distribute Data 
	    private Organization getObject() throws Exception {
	    	
	    	FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elCtx = ctx.getELContext();
			ExpressionFactory exF = ctx.getApplication().getExpressionFactory();
			ValueExpression ve = exF.createValueExpression(elCtx, "#{listMBean270}", ListMBean_270.class);
			ListMBean_270 res = (ListMBean_270) ve.getValue(elCtx);
			if(res.getParentAccount_Type() == 'N') {
				this.account_Type = 'S';
			} else if(res.getParentAccount_Type() == 'S'){
				this.account_Type = 'A';
			} else {
				throw new Exception("You cannot create any Organization please update your account to Service provider");
			}
			
	    	Organization returnEntity = new Organization ();
				returnEntity.setCustomerId (customerId);
				returnEntity.setName (name);
				returnEntity.setDisplayName (displayName);
				returnEntity.setDomainName (domainName);
				returnEntity.setDescription (description);
					//Added this Extra
				returnEntity.setAccount_Type(this.account_Type);
			if(activationDate != null)
				returnEntity.setActivationDate (new java.sql.Date(activationDate.getTime ()));
			if(expirationDate != null)
				returnEntity.setExpirationDate (new java.sql.Date(expirationDate.getTime ()));
			//if(lastUpdated != null)
			returnEntity.setLastUpdated (new java.sql.Timestamp(System.currentTimeMillis()));
			returnEntity.setTimezone (timezone);
			returnEntity.setStatus (status);
			this.selectedStatus = this.getIndex(this.status.toString(), this.bstatusTV);	
			if(this.secondaryDetails) {
			OrganizationAddress address2OrganizationAddress = new OrganizationAddress ();
			address2OrganizationAddress.setOrganizationAddressId (this.address2PKValue);
			address2OrganizationAddress.setStreetAddress1 (address2StreetAddress1);
			address2OrganizationAddress.setStreetAddress2 (address2StreetAddress2);
			address2OrganizationAddress.setCity (address2City);
			address2OrganizationAddress.setState (address2State);
			if (this.address2country != -1) {
				Country countryTemp = new Country();
				countryTemp.setCountryId(country);
				address2OrganizationAddress.setCountry(countryTemp);
				this.selectedCountry2 = this.getIndex(this.address2country, this.address2CountryIsoCode);
			} else {
				this.selectedCountry2 = -1;
			}
			address2OrganizationAddress.setZip (address2Zip);
			returnEntity.setAddress2 (address2OrganizationAddress);
			}
			if(this.secondaryDetails) {

				OrganizationContactInfo contactInfo2OrganizationContactInfo = new OrganizationContactInfo ();
				contactInfo2OrganizationContactInfo.setContactId (this.contactInfo2PKValue);
				contactInfo2OrganizationContactInfo.setContactPerson (contactInfo2ContactPerson);
				contactInfo2OrganizationContactInfo.setEmailId1 (contactInfo2EmailId1);
				contactInfo2OrganizationContactInfo.setEmailId2 (contactInfo2EmailId2);
				contactInfo2OrganizationContactInfo.setPhoneNumber (contactInfo2PhoneNumber);
				contactInfo2OrganizationContactInfo.setAlternatePhoneNumber (contactInfo2AlternatePhoneNumber);
				contactInfo2OrganizationContactInfo.setMobileNumber (contactInfo2MobileNumber);
				contactInfo2OrganizationContactInfo.setFaxNumber (contactInfo2FaxNumber);
				returnEntity.setContactInfo2 (contactInfo2OrganizationContactInfo);
			}
			OrganizationContactInfo contactInfo1OrganizationContactInfo = new OrganizationContactInfo ();
			contactInfo1OrganizationContactInfo.setContactId (this.contactInfo1PKValue);
			contactInfo1OrganizationContactInfo.setContactPerson (contactInfo1ContactPerson);
			contactInfo1OrganizationContactInfo.setEmailId1 (contactInfo1EmailId1);
			contactInfo1OrganizationContactInfo.setEmailId2 (contactInfo1EmailId2);
			contactInfo1OrganizationContactInfo.setPhoneNumber (contactInfo1PhoneNumber);
			contactInfo1OrganizationContactInfo.setAlternatePhoneNumber (contactInfo1AlternatePhoneNumber);
			contactInfo1OrganizationContactInfo.setMobileNumber (contactInfo1MobileNumber);
			contactInfo1OrganizationContactInfo.setFaxNumber (contactInfo1FaxNumber);
				contactInfo1OrganizationContactInfo.setMerchantAccount(returnEntity);
			returnEntity.setContactInfo1 (contactInfo1OrganizationContactInfo);
			
			OrganizationAddress address1OrganizationAddress = new OrganizationAddress ();
			address1OrganizationAddress.setOrganizationAddressId (this.address1PKValue);
			address1OrganizationAddress.setStreetAddress1 (address1StreetAddress1);
			address1OrganizationAddress.setStreetAddress2 (address1StreetAddress2);
			address1OrganizationAddress.setCity (address1City);
			address1OrganizationAddress.setState (address1State);
				if(this.country != -1) {
				Country countryTemp = new Country ();
				countryTemp.setCountryId (country);
				address1OrganizationAddress.setCountry (countryTemp);
				this.selectedCountry = this.getIndex(this.country, this.address1CountryIsoCode);		
				} else {
					this.selectedCountry = -1;
				}
			address1OrganizationAddress.setZip (address1Zip);
			returnEntity.setAddress1 (address1OrganizationAddress);
			return returnEntity;
	    }
	    
	    private Organization getBaseObject () {
			Organization baseObject = new Organization ();
			return baseObject;
		}
		
//For getting OperationId for a particular Operation depending upon the user's Role	
		private int getOperationId (CPFConstants.OperationType opType) {
			int operationId = new Integer (-1);
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext exContext = context.getExternalContext();
			if(opType.equals(CPFConstants.OperationType.CREATE)) {
				if (exContext.isUserInRole ("SPA") ||exContext.isUserInRole ("NPM")) {
					operationId = 273;
				}
			}
			if(opType.equals(CPFConstants.OperationType.MODIFY)) {
				if (exContext.isUserInRole ("SPA") ||exContext.isUserInRole ("NPM")) {
					operationId = 273;
				}
			}
			if(opType.equals(CPFConstants.OperationType.VIEW)) {
				if (exContext.isUserInRole ("SPA") ||exContext.isUserInRole ("NPM")) {
					operationId = 273;
				}
			}
			return operationId;
			//return 270;
			//return -1;
		}
		
		public String nextPage() {
			return "np" + this.pageNo++;
		}
		
		public String previousPage() {
			return "pp" + this.pageNo--;
		}
		
		public String cancelAction() {
			this.pageNo = 1;
			return "list";
		}
		
		public String saveAction (ActionEvent e) throws Exception {
			this.pageNo = 1;
			String returnValue = null;
			Organization organization = getObject();
			Organization merchantAc = _getMerchantAccount();		//Getting merchant Account from Session Object 
			organization.setMerchantAccount(merchantAc);
			if(this.mode == 0) {
				this.operationId = getOperationId(CPFConstants.OperationType.CREATE);
				returnValue = MgmtManager.save(organization, this.operationId);
				
				System.out.println("createUser() calling ");
				userModule = (UserModule)new InitialContext().lookup("java:portal/UserModule");
				User user = userModule.createUser("admin@" + organization.getDomainName(), "1234", organization);
				//Set<HibernateRoleImpl> roles = new HashSet<HibernateRoleImpl>();
				//HibernateRoleImpl spa = new HibernateRoleImpl();
				//spa.setName("SPA");
				//roles.add(spa);
				//membershipModule.assignRoles(user, roles);
				System.out.println("createUser() exited ");
			}
			else {
				this.operationId = getOperationId(CPFConstants.OperationType.MODIFY);
				this.fillCriteria(CPFConstants.OperationType.MODIFY);
				returnValue = MgmtManager.save(organization, this.criteria, this.operationId);
			}
			this.mode = 2;
			if(!this.secondaryDetails) {
				this.resetSecondaryAddressDetails();
			}
			FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elCtx = ctx.getELContext();
			ExpressionFactory exF = ctx.getApplication().getExpressionFactory();
			ValueExpression ve = exF.createValueExpression(elCtx, "#{listMBean270}", ListMBean_270.class);
			ListMBean_270 res = (ListMBean_270) ve.getValue(elCtx);
			res.setMode(0);			
			return returnValue;
		}
		
		public String modifyAction (ActionEvent e) throws Exception {
			System.out.println("mod");
			Object val = e.getComponent().getAttributes().get("pkValue");
			this.primarykeyValue = new Long(val.toString());
			String returnString = viewAction(null);
			setMode(1);
			
			SelectItem def = null;
			/*def = new SelectItem();
			def.setLabel("No-Selection");
			def.setValue("-1");
			this.address2CountryIsoCode.add(0, def);
			def = new SelectItem();
			def.setLabel("No-Selection");
			def.setValue("-1");
			this.address1CountryIsoCode.add(0, def);	*/	
			FacesContext context = FacesContext.getCurrentInstance();
			this.userIsInRole(context);
			this.getDetailsVisibility(this.userRole);
			
			System.out.println("In modifyAction: " + this);
			return "modify";
		}
		
		public String addAction (ActionEvent e) throws CPFException {
			this.mode = 0;
			this.customerId = null;
			this.name = null;
			this.displayName = null;
			this.domainName = null;
			this.description = null;
			this.activationDate = null;
			this.expirationDate = null;
			this.lastUpdated = null;
			this.timezone = null;
			this.status = null;
			this.address2StreetAddress1 = null;
			this.address2StreetAddress2 = null;
			this.address2City = null;
			this.address2State = null;
			this.address2country = new Long(-1);
			this.address2Zip = null;
			this.contactInfo1ContactPerson = null;
			this.contactInfo1EmailId1 = null;
			this.contactInfo1EmailId2 = null;
			this.contactInfo1PhoneNumber = null;
			this.contactInfo1AlternatePhoneNumber = null;
			this.contactInfo1MobileNumber = null;
			this.contactInfo1FaxNumber = null;
			this.contactInfo2ContactPerson = null;
			this.contactInfo2EmailId1 = null;
			this.contactInfo2EmailId2 = null;
			this.contactInfo2PhoneNumber = null;
			this.contactInfo2AlternatePhoneNumber = null;
			this.contactInfo2MobileNumber = null;
			this.contactInfo2FaxNumber = null;
			this.address1StreetAddress1 = null;
			this.address1StreetAddress2 = null;
			this.address1City = null;
			this.address1State = null;
			this.country = new Long(-1);
			this.address1Zip = null;
			this.address2PKValue = null;
			this.contactInfo1PKValue = null;
			this.contactInfo2PKValue = null;
			this.address1PKValue = null;
			FacesContext context = FacesContext.getCurrentInstance();
			this.userIsInRole(context);
			this.getDetailsVisibility(this.userRole);
			SelectItem def = null;
			MgmtPortletUtil portletUtil = new MgmtPortletUtil();
			this.address2CountryIsoCode = portletUtil.getData("Country", "countryId", "isoCode", true,"");
			/*def = new SelectItem();
			def.setLabel("No-Selection");
			def.setValue("-1");
			this.address2CountryIsoCode.add(0, def);*/
			this.address1CountryIsoCode = portletUtil.getData("Country", "countryId", "isoCode", true,"");
			/*def = new SelectItem();
			def.setLabel("No-Selection");
			def.setValue("-1");
			this.address1CountryIsoCode.add(0, def);*/			
			
			return SS_Constants.ReturnMessage.SUCCESS.toString();
		}
		
		public String viewAction (ActionEvent e) throws Exception {
			System.out.println("View");
			if(e != null) {
				if(e.getComponent().getId().equals("delete")) {
					FacesContext ctx = FacesContext.getCurrentInstance();
					ELContext elCtx = ctx.getELContext();
					ExpressionFactory exF = ctx.getApplication().getExpressionFactory();
					ValueExpression ve = exF.createValueExpression(elCtx, "#{listMBean270}", ListMBean_270.class);
					ListMBean_270 res = (ListMBean_270) ve.getValue(elCtx);
					this.primarykeyValue = res.getPrimaryKeyValue();
				} else {
					Object val = e.getComponent().getAttributes().get("pkValue");
					this.primarykeyValue = new Long(val.toString());
				}
			}
			Object object = null;
			if(e == null) {
				this.operationId = getOperationId(CPFConstants.OperationType.MODIFY);
				this.fillCriteria(CPFConstants.OperationType.MODIFY);
			}
			else {
				this.operationId = getOperationId(CPFConstants.OperationType.VIEW);
				this.fillCriteria(CPFConstants.OperationType.VIEW);
				
				FacesContext context = FacesContext.getCurrentInstance();
				this.userIsInRole(context);
				this.getDetailsVisibility(this.userRole);
			}
			try {
				object = MgmtManager.getDetails(this.operationId, this.getBaseObject(), this.criteria);
			} catch (CPFException e1) {
				e1.printStackTrace();
				return SS_Constants.ReturnMessage.PROVERROR.toString();
			}
			this.mode = 2;
			MgmtPortletUtil portletUtil = new MgmtPortletUtil();
			this.address2CountryIsoCode = portletUtil.getData("Country", "countryId", "isoCode", true,"");
			this.address1CountryIsoCode = portletUtil.getData("Country", "countryId", "isoCode", true,"");		
			System.out.println("View : " );		
			distributeData(object);			//Setting all member class variables here....
			System.out.println("View end: ");
			return SS_Constants.ReturnMessage.SUCCESS.toString();
		}
		
		public String delete (ActionEvent e) throws Exception {
			this.viewAction(e);
			this.status = new Integer(-999);
			//this.operationId = this.getOperationId(CPFConstants.OperationType.MODIFY);	//added this line on 25th Jan
			this.saveAction(e);
			return "SUCCESS";
		}
	
//This will return user's Role depending upon context	
	private void userIsInRole (FacesContext context) throws CPFException{
		ExternalContext exContext = context.getExternalContext();
		if (exContext.isUserInRole("SPA")) {
			this.setUserRole ("SPA");
		} else
		if (exContext.isUserInRole("NPM")) {
			this.setUserRole ("NPM");
		} else
		{
			throw new CPFException("Not Authenticated please contact provider for necessary  privileges", 4046);
		}
	}
			
		
//Start of getting visibility for Details columns	
	private void getDetailsVisibility(String userRole) {
		this.listVisibility = new ArrayList<Boolean>();
		if(userRole.equals("SPA")) {
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
		}
		if(userRole.equals("NPM")) {
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
			this.listVisibility.add(true);
		}
	}
//End of getting visibility for listing columns
		
		
	//This will returns MerchantAccount Object of Current User
	private Organization _getMerchantAccount() throws Exception {
		//commenting out security ...
		/*FacesContext ctx = FacesContext.getCurrentInstance();
		PortletRequest request = PortletFacesUtils.getPortletRequest(ctx);
		PortletSession session = request.getPortletSession();
		Object obj = session.getAttribute ("User");
		User user = (User)obj;
		return user.getMerchantAccount (); */

    	Set<Principal> s = SecurityAssociation.getSubject().getPrincipals();
    	
    	for (Principal principal : s) {
    		LOG.debug ("sub principal: " + principal.getClass().getName());
			if (principal instanceof GBUserPrincipal) {
				LOG.debug ("p: " + principal);
				
				Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();
				long enterpriseId = enterprise.getOrganizationId();
				LOG.debug("enterpriseId = " + enterpriseId);
				return enterprise;
			}

		}
    	
	
	//Organization o = new Organization ();
	//o.setOrganizationId (new Long (0));
	return null;
	}
	
	//This will set all the member class values from Object returned by CPFSessionFacade
	private void distributeData(Object o) {
		this.resetValues();
		Organization dataObject = (Organization)o;
		this.customerId = dataObject.getCustomerId();
		this.name = dataObject.getName();
		this.displayName = dataObject.getDisplayName();
		this.domainName = dataObject.getDomainName();
		this.description = dataObject.getDescription();
		this.activationDate = dataObject.getActivationDate();
		this.expirationDate = dataObject.getExpirationDate();
		this.lastUpdated = dataObject.getLastUpdated();
		this.timezone = dataObject.getTimezone();
		this.status = dataObject.getStatus();
		this.selectedStatus = this.getIndex(this.status.toString(), this.bstatusTV);
		OrganizationAddress address2OrganizationAddress = dataObject.getAddress2();
		if (address2OrganizationAddress != null) {
			this.address2PKValue = address2OrganizationAddress.getOrganizationAddressId();
			this.address2StreetAddress1 = address2OrganizationAddress.getStreetAddress1();
			this.address2StreetAddress2 = address2OrganizationAddress.getStreetAddress2();
			this.address2City = address2OrganizationAddress.getCity();
			this.address2State = address2OrganizationAddress.getState();
			Country inner = address2OrganizationAddress.getCountry();
			if (inner != null) {
				this.address2country = inner.getCountryId();
				this.selectedCountry2 = this.getIndex(this.address2country, this.address2CountryIsoCode);
			} else {
				this.selectedCountry2 = -1;
			}
			this.address2Zip = address2OrganizationAddress.getZip();
		} else {
			this.selectedCountry2 = -1;
		}
		OrganizationContactInfo contactInfo1OrganizationContactInfo = dataObject.getContactInfo1();
		if (contactInfo1OrganizationContactInfo != null) {
		this.contactInfo1PKValue = contactInfo1OrganizationContactInfo.getContactId();
		this.contactInfo1ContactPerson = contactInfo1OrganizationContactInfo.getContactPerson();
		this.contactInfo1EmailId1 = contactInfo1OrganizationContactInfo.getEmailId1();
		this.contactInfo1EmailId2 = contactInfo1OrganizationContactInfo.getEmailId2();
		this.contactInfo1PhoneNumber = contactInfo1OrganizationContactInfo.getPhoneNumber();
		this.contactInfo1AlternatePhoneNumber = contactInfo1OrganizationContactInfo.getAlternatePhoneNumber();
		this.contactInfo1MobileNumber = contactInfo1OrganizationContactInfo.getMobileNumber();
		this.contactInfo1FaxNumber = contactInfo1OrganizationContactInfo.getFaxNumber();
		}
		OrganizationContactInfo contactInfo2OrganizationContactInfo = dataObject.getContactInfo2();
		if (contactInfo2OrganizationContactInfo != null) {
		this.contactInfo2PKValue = contactInfo2OrganizationContactInfo.getContactId();
		this.contactInfo2ContactPerson = contactInfo2OrganizationContactInfo.getContactPerson();
		this.contactInfo2EmailId1 = contactInfo2OrganizationContactInfo.getEmailId1();
		this.contactInfo2EmailId2 = contactInfo2OrganizationContactInfo.getEmailId2();
		this.contactInfo2PhoneNumber = contactInfo2OrganizationContactInfo.getPhoneNumber();
		this.contactInfo2AlternatePhoneNumber = contactInfo2OrganizationContactInfo.getAlternatePhoneNumber();
		this.contactInfo2MobileNumber = contactInfo2OrganizationContactInfo.getMobileNumber();
		this.contactInfo2FaxNumber = contactInfo2OrganizationContactInfo.getFaxNumber();
		}
		OrganizationAddress address1OrganizationAddress = dataObject.getAddress1();
		if (address1OrganizationAddress != null) {
		this.address1PKValue = address1OrganizationAddress.getOrganizationAddressId();
		this.address1StreetAddress1 = address1OrganizationAddress.getStreetAddress1();
		this.address1StreetAddress2 = address1OrganizationAddress.getStreetAddress2();
		this.address1City = address1OrganizationAddress.getCity();
		this.address1State = address1OrganizationAddress.getState();
		Country inner = address1OrganizationAddress.getCountry();
		if(inner != null) {
		this.country = inner.getCountryId();
		this.selectedCountry = this.getIndex(this.country, this.address1CountryIsoCode);
		} else {
		this.selectedCountry = -1;
		}
		this.address1Zip = address1OrganizationAddress.getZip();
		}
		
		if(address2OrganizationAddress == null && contactInfo2OrganizationContactInfo == null) {
			this.secondaryDetails = false;
		} else {
			this.secondaryDetails = true;
		}
	}
	
	private void resetSecondaryAddressDetails() {
		this.address2StreetAddress1 = null;
		this.address2StreetAddress2 = null;
		this.address2City = null;
		this.address2State = null;
		this.address2country = new Long(-1);
		this.address2Zip = null;
		this.selectedCountry2 = -1;
		
		this.contactInfo2ContactPerson = null;
		this.contactInfo2EmailId1 = null;
		this.contactInfo2EmailId2 = null;
		this.contactInfo2PhoneNumber = null;
		this.contactInfo2AlternatePhoneNumber = null;
		this.contactInfo2MobileNumber = null;
		this.contactInfo2FaxNumber = null;
	}
	
	private void resetValues() {
		this.pageNo = 1;
		this.customerId = null;
		this.name = null;
		this.displayName = null;
		this.domainName = null;
		this.description = null;
		this.activationDate = null;
		this.expirationDate = null;
		this.lastUpdated = null;
		this.timezone = null;
		this.status = null;
		this.address2StreetAddress1 = null;
		this.address2StreetAddress2 = null;
		this.address2City = null;
		this.address2State = null;
		this.address2country = new Long(-1);
		this.address2Zip = null;
		this.contactInfo1ContactPerson = null;
		this.contactInfo1EmailId1 = null;
		this.contactInfo1EmailId2 = null;
		this.contactInfo1PhoneNumber = null;
		this.contactInfo1AlternatePhoneNumber = null;
		this.contactInfo1MobileNumber = null;
		this.contactInfo1FaxNumber = null;
		this.contactInfo2ContactPerson = null;
		this.contactInfo2EmailId1 = null;
		this.contactInfo2EmailId2 = null;
		this.contactInfo2PhoneNumber = null;
		this.contactInfo2AlternatePhoneNumber = null;
		this.contactInfo2MobileNumber = null;
		this.contactInfo2FaxNumber = null;
		this.address1StreetAddress1 = null;
		this.address1StreetAddress2 = null;
		this.address1City = null;
		this.address1State = null;
		this.country = new Long(-1);
		this.address1Zip = null;
	}
	
	private int getIndex(Object selected, List<SelectItem> available) {
	 	int i = 0;
	    for(Iterator<SelectItem> itr = available.iterator(); itr.hasNext();) {
	    	SelectItem temp = itr.next();
	    	if(temp.getValue().equals(selected)) {
	    		break;
	    	}
	    	i++;	
	    }
	    return i;
	}	
	 
	private void fillCriteria(CPFConstants.OperationType o) throws Exception {
		Criteria c = new Criteria();
		c.setBaseEntityName("Organization");
		c.setBasePrimaryKey("organizationId");
		c.setBasePrimaryKeyValue(this.getPrimarykeyValue());
		Long merchantId = this._getMerchantAccount().getOrganizationId();
		c.setWhere("Organization.merchantAccount= (select merchantAccount from Organization merchantAccount where merchantAccount.organizationId=" + merchantId + ")");
		if(o.equals(CPFConstants.OperationType.MODIFY)) {
			c.setFields("customerId, name, displayName, domainName, description, activationDate, expirationDate, lastUpdated, timezone, status, address2.organizationAddressId,address2.streetAddress1,address2.streetAddress2,address2.city,address2.state,address2.country, address2.zip,contactInfo1.contactId,contactInfo1.contactPerson,contactInfo1.emailId1,contactInfo1.emailId2,contactInfo1.phoneNumber,contactInfo1.alternatePhoneNumber,contactInfo1.mobileNumber,contactInfo1.faxNumber,contactInfo2.contactId,contactInfo2.contactPerson,contactInfo2.emailId1,contactInfo2.emailId2,contactInfo2.phoneNumber,contactInfo2.alternatePhoneNumber,contactInfo2.mobileNumber,contactInfo2.faxNumber,address1.organizationAddressId,address1.streetAddress1,address1.streetAddress2,address1.city,address1.state,address1.country, address1.zip");
		} else if(o.equals(CPFConstants.OperationType.VIEW)) {
			c.setFields("customerId, name, displayName, domainName, description, activationDate, expirationDate, lastUpdated, timezone, status, address2.organizationAddressId, address2.streetAddress1, address2.streetAddress2, address2.city, address2.state, address2.country.isoCode, address2.zip, contactInfo1.contactId, contactInfo1.contactPerson, contactInfo1.emailId1, contactInfo1.emailId2, contactInfo1.phoneNumber, contactInfo1.alternatePhoneNumber, contactInfo1.mobileNumber, contactInfo1.faxNumber, contactInfo2.contactId, contactInfo2.contactPerson, contactInfo2.emailId1, contactInfo2.emailId2, contactInfo2.phoneNumber, contactInfo2.alternatePhoneNumber, contactInfo2.mobileNumber, contactInfo2.faxNumber, address1.organizationAddressId, address1.streetAddress1, address1.streetAddress2, address1.city, address1.state, address1.country.isoCode, address1.zip");
		}
		this.criteria = c;
	}
}
