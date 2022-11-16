 
package com.genband.m5.maps.sp.mbeans;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.log4j.Logger;
import org.jboss.security.SecurityAssociation;

import com.genband.m5.maps.common.AuxiliaryDetails;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.common.SS_Constants;
import com.genband.m5.maps.common.AuxiliaryDetails.SearchInfo;
import com.genband.m5.maps.common.entity.Organization;
import com.genband.m5.maps.identity.GBUserPrincipal;

public class ListMBean_270 {

//Variables Declaration

	private static final Logger LOG = Logger.getLogger(ListMBean_270.class);
	
	private boolean canCreate = false;

	private boolean canModify = false;
	
	private boolean canView = false;

	private boolean canDelete = false;
		
	private String searchKey;

	private String searchOperator;

	private Object searchValue;
	
	private boolean searechCaseSensitive;
	
	private Map<String, SelectItemGroup> searchFields = new HashMap<String, SelectItemGroup>();  //Map of Attribute and their supportable search Operators depending upon its DataType
	
	private List<SelectItem> attbs;
	
	private SelectItemGroup operators;

	private String primaryKeyName;
	
	private String orderBy;
	
	private String orderByState = null;		//added this line on 28th jan
	
	private boolean ascending;
	
	private int pageSize = 10;
	
	private int rowNumber;
	
	private Criteria criteria;
	
	private Long primaryKeyValue;
	
	private int operationId;
	
	private int numberOfRecords;
	
	private boolean initialCall = true;
	
	private boolean nextDisabled;
	
	private boolean previousDisabled;
	
	private String userRole;
	
	private List<Boolean> listVisibility;
	
	private List<SelectItem> radio;
	
	private int mode;
	
	private List<Object[]> coll;
	
	private Character parentAccount_Type;
//End of variables Declaration

	//Default Constructor
	public ListMBean_270 () {
	}

//Start Of Setters and Getters

	public boolean isCanCreate() {
		return canCreate;
	}
	
	public void setCanCreate(boolean canCreate) {
		this.canCreate = canCreate;
	}

	public boolean isCanModify() {
		return canModify;
	}
	
	public void setCanModify(boolean canModify) {
		this.canModify = canModify;
	}
		
	public boolean isCanView() {
		return canView;
	}
	
	public void setCanView(boolean canView) {
		this.canView = canView;
	}
	
	public boolean isCanDelete() {
		return canDelete;
	}
	
	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}
		
	
	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getSearchOperator() {
		return searchOperator;
	}

	public void setSearchOperator(String searchOperator) {
		this.searchOperator = searchOperator;
	}

	public Object getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(Object searchValue) {
		this.searchValue = searchValue;
	}

	public boolean isSearechCaseSensitive() {
		return searechCaseSensitive;
	}

	public void setSearechCaseSensitive(boolean searechCaseSensitive) {
		this.searechCaseSensitive = searechCaseSensitive;
	}
	
	public List<SelectItem> getAttbs() {
		return this.attbs;
	}
	
	public void getAttbs(List<SelectItem> attbs) {
		this.attbs = attbs;
	}
	
	public SelectItemGroup getOperators() {
		this.operators = getSearchFields().get(this.getSearchKey());
		return operators;
	}

	public void setOperators(SelectItemGroup operators) {
		this.operators = operators;
	}

	public Map<String, SelectItemGroup> getSearchFields() {
		return searchFields;
	}

	public void setSearchFields(Map<String, SelectItemGroup> searchFields) {
		this.searchFields = searchFields;
	}

	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	public void setPrimaryKeyName(String primaryKeyName) {
		this.primaryKeyName = primaryKeyName;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public String getOrderByState() {
		return orderByState;
	}

	public void setOrderByState(String orderByState) {
		this.orderByState = orderByState;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public int getOperationId() {
		return operationId;
	}

	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}
	
	public Long getPrimaryKeyValue() {
		return primaryKeyValue;
	}

	public void setPrimaryKeyValue(Long primaryKeyValue) {
		this.primaryKeyValue = primaryKeyValue;
	}
	
	public int getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(int numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

	public boolean isNextDisabled() {
		return nextDisabled;
	}

	public void setNextDisabled(boolean nextDisabled) {
		this.nextDisabled = nextDisabled;
	}

	public boolean isPreviousDisabled() {
		this.mode = 0;
		return previousDisabled;
	}

	public void setPreviousDisabled(boolean previousDisabled) {
		this.previousDisabled = previousDisabled;
	}
	
	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	
	public List<Boolean> getListVisibility() {
		return listVisibility;
	}

	public void setListVisibility(List<Boolean> listVisibility) {
		this.listVisibility = listVisibility;
	}
	
	public List<SelectItem> getRadio() {
		return radio;
	}

	public void setRadio(List<SelectItem> radio) {
		this.radio = radio;
	}
	
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public Character getParentAccount_Type() {
		return parentAccount_Type;
	}

	public void setParentAccount_Type(Character parentAccount_Type) {
		this.parentAccount_Type = parentAccount_Type;
	}

	public List<Object[]> getColl() {
		return coll;
	}

	public void setColl(List<Object[]> coll) {
		this.coll = coll;
	}
//End of Setters and Getters

	//For deleting a record (If User does not have permission then this will throw Exception or failure Message)
	public String delete (ActionEvent e) {
		this.mode = 0;
		this.operationId = this.getOperationId(CPFConstants.OperationType.DELETE);
		String returnValue = MgmtManager.delete (this.getBaseObject(), this.primaryKeyValue, this.operationId);
		this.operationId = this.getOperationId(CPFConstants.OperationType.LIST);	//added this line on 25th Jan
		return returnValue;
	}
	//End of Deleting a record
	
	//Getting result as Collection of Array Objects to display the result on listing page 
	public Collection<Object[]> getCollec () throws Exception {
		List<Object[]> coll = new ArrayList<Object[]>();
		this.searechCaseSensitive = false;
		this.searchValue = null;
		this.primaryKeyValue = null;
		if(this.initialCall) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			this.userIsInRole(ctx);
			this.getListVisibility(this.userRole);
			this.setRowNumber(0);		//Added this line on 24th Jan
			this.criteria = this.getDefaultCriteria();
			this.initialCall = false;		//added this line on 24th Jan	
			this.loadAttbsOperators(this.userRole);
		}
		this.setOperationId(this.getOperationId(CPFConstants.OperationType.LIST));
		if(this.mode == 0) {
			if(this.orderByState == null) {
				this.ascending = true;
				this.criteria.getSearchDetails().setAscending(this.ascending);
				this.orderBy = new String("childOrgnizationAccounts.customerId");
				this.criteria.getSearchDetails().setOrderBy(this.orderBy);
				this.orderByState = this.orderBy;
			}
			coll = (List<Object[]>)MgmtManager.getResult(this.operationId, this.criteria);
			this.mode = 1;
			this.coll = coll;
		//}
			
		if(this.coll.size() != this.pageSize + 1) {
			this.setNextDisabled(true);
		} else {
			this.setNextDisabled(false);
		}
		
		if(this.coll.size() < this.pageSize + 1) {		//Added this If statement on 24th Jan
			this.setNextDisabled(true);
		}
		
		if(this.rowNumber == 0) {
			this.setPreviousDisabled(true);
		} else {
			this.setPreviousDisabled(false);
		}
		
		if(this.coll.size() == this.pageSize + 1) {
			this.coll.remove(this.pageSize);
		}
		
		radio = new ArrayList<SelectItem>();
		for (Object[] objects : this.coll) {
				SelectItem item = new SelectItem();
				item.setValue(objects[0]);
				item.setLabel("");
				radio.add(item);
				if(objects[6].equals(new Integer(1))) {
					objects[6] = new String("Active");
				} else if(objects[6].equals(new Integer(2))) {
					objects[6] = new String("In-Active");
				} else {
					objects[6] = new String("Suspended");
				}
		}
		}
		return (Collection<Object[]>) this.coll;
	}
	//End of Listing	
	//Funtion for Searching details
	public String search () throws Exception  {
		this.criteria = this.getDefaultCriteria();
		if(this.searchValue != null && this.searchValue.toString().trim().length() > 0) {	//Added this if condition on 29th Jan
			AuxiliaryDetails searchDetails = this.criteria.getSearchDetails();
		
			SearchInfo searchInfo = new SearchInfo ();
			CPFConstants.Operators operator = CPFConstants.Operators.valueOf(searchOperator);
			searchInfo.setOperator(operator);
			searchInfo.setSearchKey(this.searchKey);
			if(searchKey.equals("childOrgnizationAccounts.status")) {
				if(searchValue.equals("Active")) {
					this.searchValue = new Integer(1);
				} else if(searchValue.equals("In-Active")) {
					this.searchValue = new Integer(2);
				} else if(searchValue.equals("Suspended")) {
					this.searchValue = new Integer(3);
				} else {

					this.searchValue = new Integer(3);
				}
			}
			searchInfo.setSearchValue(searchValue);
			searchInfo.setSearechCaseSensitive(searechCaseSensitive);
		
			searchDetails.setSearchInfo(searchInfo);
			searchDetails.setSearch(true);
			this.rowNumber = 0;
			searchDetails.setRowNumber(rowNumber);
			this.criteria.setSearchDetails(searchDetails);
			this.mode = 0;
		}
		
		this.initialCall = false;
		return SS_Constants.ReturnMessage.SUCCESS.toString();
	}
	//End of searching details
	
	public String reset () {
		this.mode = 0;
		this.initialCall = true;
		this.searchValue = null;
		this.orderByState = null;
		this.criteria.reset(this.pageSize);
		return "SUCCESS";
	}
	
	//Function to Sort on one Column
	public String sort () throws Exception {
		this.mode = 0;
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext exCtx = ctx.getExternalContext();
		Map<String, String> params = exCtx.getRequestParameterMap();
		String val = (String)params.get("sortBy");
		System.out.println("Order By: " + val);
		this.criteria = this.getDefaultCriteria();
		AuxiliaryDetails searchDetails = this.criteria.getSearchDetails();
		//this.ascending = true;
		this.orderBy = val;
		if(orderBy.equals(this.orderByState)) {
			this.ascending = !(this.ascending);	
		} else {
			this.ascending = true;
			this.orderByState = this.orderBy;
		}
		searchDetails.setAscending(this.ascending);
		this.rowNumber = 0;
		searchDetails.setRowNumber(rowNumber);
		searchDetails.setOrderBy(orderBy);
		searchDetails.setSearch(false);
		
		this.criteria.setSearchDetails(searchDetails);
		this.setCriteria(criteria);
		String returnString = "SUCCESS";
		this.initialCall = false;
		return returnString;
	}
	//End of Sorting
	
	//For viewing list of next records 
	public String pageChange (ActionEvent e) throws Exception {
		this.mode = 0;
		String componentId = e.getComponent().getId();
		//this.criteria = this.getDefaultCriteria();
		AuxiliaryDetails auxiliaryDetails = this.criteria.getSearchDetails();
		if(componentId.equals(SS_Constants.PAGEFLOW_NEXT)) {
			this.rowNumber = this.rowNumber + this.pageSize;
		} else if(componentId.equals(SS_Constants.PAGEFLOW_PREVIOUS)) {
			this.rowNumber = this.rowNumber - this.pageSize;
		} 
		auxiliaryDetails.setRowNumber(rowNumber);
		criteria.setSearchDetails(auxiliaryDetails);
		this.setCriteria(criteria);
		this.initialCall = false;
		return "SUCCESS";
	}
	
	private Organization getBaseObject () {
		Organization baseObject = new Organization ();
		return baseObject;
	}
	
	
	//This will return default Criteria Object 
	private Criteria getDefaultCriteria () throws Exception {
		Criteria c = new Criteria();
		String fields = "childOrgnizationAccounts.organizationId, childOrgnizationAccounts.customerId, childOrgnizationAccounts.name, childOrgnizationAccounts.domainName, childOrgnizationAccounts.activationDate, childOrgnizationAccounts.expirationDate, childOrgnizationAccounts.status, childOrgnizationAccounts.lastUpdated";
		c.setFields(fields);
		c.setFrom("Organization Organization left join Organization.childOrgnizationAccounts childOrgnizationAccounts");
		Organization merchantAccount = _getMerchantAccount ();
		Long merchantId = merchantAccount.getOrganizationId();
		c.setWhere("childOrgnizationAccounts.status != -999 and childOrgnizationAccounts.merchantAccount= (select merchantAccount from Organization merchantAccount where merchantAccount.organizationId=" + merchantId + ")");	
		c.setBaseEntityName("Organization");
		c.setBasePrimaryKey("organizationId");
		AuxiliaryDetails auxiliaryDetails = new AuxiliaryDetails();
		auxiliaryDetails.setPageSize(pageSize);
		auxiliaryDetails.setRowNumber(rowNumber);
		auxiliaryDetails.setSearch(false);
		c.setSearchDetails(auxiliaryDetails);
		return c;
	}
	
	//This will returns MerchantAccount Object of Current User
	private Organization _getMerchantAccount() throws Exception {

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
	
	//This will return Operation Id depending upon user's Role and OperationType
	private int getOperationId (CPFConstants.OperationType opType){
		int opId = -1;
			if(opType.equals(CPFConstants.OperationType.LIST)) {
				if(this.userRole.equals("SPA") || this.userRole.equals("NPM")) {
					opId = 269;
				}
			} 
			if(opType.equals(CPFConstants.OperationType.DELETE)) {
				if(this.userRole.equals("SPA") || this.userRole.equals("NPM")) {
					opId = 269;
				}
			} 
		return opId;
		//return 270;
		//return -1;
	}
	
	private void userIsInRole (FacesContext context) throws CPFException {
		ExternalContext exContext = context.getExternalContext();
		FacesContext ctx = FacesContext.getCurrentInstance();
		ELContext elCtx = ctx.getELContext();
		ExpressionFactory exF = ctx.getApplication().getExpressionFactory();
		ValueExpression ve = exF.createValueExpression(elCtx, "#{createMBean277}", CreateMBean_277.class);
		CreateMBean_277 res = (CreateMBean_277) ve.getValue(elCtx);
		try {
			res.viewAction(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.parentAccount_Type = res.getAccount_Type();
		
		if (exContext.isUserInRole("SPA")) {
			this.setUserRole ("SPA");
			System.out.println("Inside Bean Account Type is : " + this.parentAccount_Type);
			if(this.parentAccount_Type == 'N' || this.parentAccount_Type == 'S') {
				this.canCreate = true;
				this.canModify = true;
				this.canView = true;
				this.canDelete = true;
			}
		} else 
		if (exContext.isUserInRole("NPM")) {
			this.setUserRole ("NPM");
			if(this.parentAccount_Type == 'N' || this.parentAccount_Type == 'S') {
				this.canCreate = true;
				this.canModify = true;
				this.canView = true;
				this.canDelete = true;
			}
		} else 
		{
			throw new CPFException("Not Authenticated please contact provider for necessary  privileges", 4046);
		}
	}

//Start of getting visibility for listing columns	
	private void getListVisibility(String userRole) {
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
		}
	}
//End of getting visibility for listing columns	
	private void loadAttbsOperators(String userRole) {
			//Independent of User Role		
		searchFields = new HashMap<String, SelectItemGroup>(); 
		Map<String, SelectItemGroup> temp = new HashMap<String, SelectItemGroup>();
		SelectItemGroup selectItemGrp = null;
		SelectItem[] supportedOperators = null;
		
		selectItemGrp = new SelectItemGroup();
		supportedOperators = new SelectItem[6];	
		supportedOperators[0] = new SelectItem();
		supportedOperators[0].setLabel(CPFConstants.Operators.EQUAL.toString());
		supportedOperators[0].setValue(CPFConstants.Operators.EQUAL.toString());
		supportedOperators[1] = new SelectItem();
		supportedOperators[1].setLabel(CPFConstants.Operators.GREATER_THAN.toString());
		supportedOperators[1].setValue(CPFConstants.Operators.GREATER_THAN.toString());
		supportedOperators[2] = new SelectItem();
		supportedOperators[2].setLabel(CPFConstants.Operators.GREATER_THAN_EQUAL.toString());
		supportedOperators[2].setValue(CPFConstants.Operators.GREATER_THAN_EQUAL.toString());
		supportedOperators[3] = new SelectItem();
		supportedOperators[3].setLabel(CPFConstants.Operators.LESS_THAN.toString());
		supportedOperators[3].setValue(CPFConstants.Operators.LESS_THAN.toString());
		supportedOperators[4] = new SelectItem();
		supportedOperators[4].setLabel(CPFConstants.Operators.LESS_THAN_EQUAL.toString());
		supportedOperators[4].setValue(CPFConstants.Operators.LESS_THAN_EQUAL.toString());
		supportedOperators[5] = new SelectItem();
		supportedOperators[5].setLabel(CPFConstants.Operators.NOT_EQUAL.toString());
		supportedOperators[5].setValue(CPFConstants.Operators.NOT_EQUAL.toString());
		selectItemGrp.setSelectItems(supportedOperators);
		temp.put("NUMERIC", selectItemGrp);
		
		selectItemGrp = new SelectItemGroup();
		supportedOperators = new SelectItem[3];
		supportedOperators[0] = new SelectItem();
		supportedOperators[0].setLabel(CPFConstants.Operators.CONTAINS.toString());
		supportedOperators[0].setValue(CPFConstants.Operators.CONTAINS.toString());
		supportedOperators[1] = new SelectItem();
		supportedOperators[1].setLabel(CPFConstants.Operators.NOT_CONTAINS.toString());
		supportedOperators[1].setValue(CPFConstants.Operators.NOT_CONTAINS.toString());
		supportedOperators[2] = new SelectItem();
		supportedOperators[2].setLabel(CPFConstants.Operators.EQUAL.toString());
		supportedOperators[2].setValue(CPFConstants.Operators.EQUAL.toString());
		selectItemGrp.setSelectItems(supportedOperators);
		temp.put("TEXT", selectItemGrp);
				
		attbs = new ArrayList<SelectItem>();
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		Locale currentLocal=ctx.getViewRoot().getLocale();
		
		String code=currentLocal.getLanguage() + "_"+ currentLocal.getCountry();
		
		System.out.println("ListMBean...loading Resource Bundle bundle/resources_" + code + ".properties");
		
		InputStream stream = this.getClass().getClassLoader().getResourceAsStream("bundle/resources_" + code + ".properties");
		
		if(stream==null){
			stream=this.getClass().getClassLoader().getResourceAsStream("bundle/resources.properties");
		}
		Properties bundle=new Properties();
	    try {
			bundle.load(stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		
		SelectItem s0 = new SelectItem();
		s0.setValue("childOrgnizationAccounts.customerId");
		s0.setLabel(bundle.getProperty("L270_Organization_CustomerId"));
		attbs.add(s0); 
		searchFields.put("childOrgnizationAccounts.customerId", temp.get("TEXT"));
		
		SelectItem s1 = new SelectItem();
		s1.setValue("childOrgnizationAccounts.name");
		s1.setLabel(bundle.getProperty("L270_Organization_Child_Org_Name"));
		attbs.add(s1); 
		searchFields.put("childOrgnizationAccounts.name", temp.get("TEXT"));
		
		SelectItem s2 = new SelectItem();
		s2.setValue("childOrgnizationAccounts.domainName");
		s2.setLabel(bundle.getProperty("L270_Organization_Child_Org_DomainName"));
		attbs.add(s2); 
		searchFields.put("childOrgnizationAccounts.domainName", temp.get("TEXT"));
		
		SelectItem s3 = new SelectItem();
		s3.setValue("childOrgnizationAccounts.activationDate");
		s3.setLabel(bundle.getProperty("L270_Organization_ActivationDate"));
		attbs.add(s3); 
		searchFields.put("childOrgnizationAccounts.activationDate", temp.get("NUMERIC"));
		
		SelectItem s4 = new SelectItem();
		s4.setValue("childOrgnizationAccounts.expirationDate");
		s4.setLabel(bundle.getProperty("L270_Organization_ExpirationDate"));
		attbs.add(s4); 
		searchFields.put("childOrgnizationAccounts.expirationDate", temp.get("NUMERIC"));
		
		SelectItem s5 = new SelectItem();
		s5.setValue("childOrgnizationAccounts.status");
		s5.setLabel(bundle.getProperty("L270_Organization_Child_Org_Status"));
		attbs.add(s5); 
		searchFields.put("childOrgnizationAccounts.status", temp.get("NUMERIC"));
		
		SelectItem s6 = new SelectItem();
		s6.setValue("childOrgnizationAccounts.lastUpdated");
		s6.setLabel(bundle.getProperty("L270_Organization_LastUpdated"));
		attbs.add(s6); 
		searchFields.put("childOrgnizationAccounts.lastUpdated", temp.get("NUMERIC"));
		
		//depends upon User Role
		if(userRole.equals("SPA")) {
					this.searchKey = "childOrgnizationAccounts.customerId";
		}
		if(userRole.equals("NPM")) {
					this.searchKey = "childOrgnizationAccounts.customerId";
		}
	}	
}