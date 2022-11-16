package com.genband.m5.maps.sp.mbeans;

import java.util.*;
import com.genband.m5.maps.sp.mbeans.MgmtManager;
import com.genband.m5.maps.common.PortletFacesUtils;
import javax.faces.event.ActionEvent;
import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.common.User;
import com.genband.m5.maps.common.SS_Constants;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import java.security.Principal;
import com.genband.m5.maps.identity.GBUserPrincipal;
import com.genband.m5.maps.security.ICPFDataLoader;

import java.util.Set;

import org.jboss.logging.Logger;
import org.jboss.security.SecurityAssociation;
import com.genband.m5.maps.common.entity.DeployedApp;
import com.genband.m5.maps.common.entity.Organization;

/**
		This is the managed Bean class for DeployedApp 
		@Genband.com
*/

public class  CreateMBean_109 {

		public static Logger logger = Logger.getLogger(CreateMBean_109.class);
	
		private java.lang.String appId;
		
		private java.lang.String appDescription;
		
		private java.util.Date deployDate;
		
		private int mode;
		
		private Long primarykeyValue;
		
		private int operationId;
		
		private String userRole;
		
		private List<Boolean> listVisibility;
		
		private Criteria criteria;
		
		//Added
		private java.lang.String url;
//Varibles declaration End...........................................

		public CreateMBean_109 () {
		}
		
		public java.lang.String getAppId () {
			return this.appId;
		}
		 
		public java.lang.String getAppDescription () {
			return this.appDescription;
		}
		 
		public java.util.Date getDeployDate () {
			return this.deployDate;
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

		public void setAppId (java.lang.String appId) {
			this.appId = appId;
		}
		 
		public void setAppDescription (java.lang.String appDescription) {
			this.appDescription = appDescription;
		}
		 
		public void setDeployDate (java.util.Date deployDate) {
			this.deployDate = deployDate;
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
		
		//Generating getObject Function which will set values for ModelEntity and returns ModelEntity.  This itself will do process for Distribute Data 
	    private DeployedApp getObject() throws Exception {
	    	DeployedApp returnEntity = new DeployedApp ();
				returnEntity.setAppId (appId);
				returnEntity.setAppDescription (appDescription);
			if(deployDate != null)
				returnEntity.setDeployDate (new java.sql.Date(deployDate.getTime ()));	
			return returnEntity;
	    }
	    
	    private DeployedApp getBaseObject () {
			DeployedApp baseObject = new DeployedApp ();
			return baseObject;
		}
		
//For getting OperationId for a particular Operation depending upon the user's Role	
		private int getOperationId (CPFConstants.OperationType opType) {
			int operationId = new Integer (-99);
			FacesContext context = FacesContext.getCurrentInstance();
			ExternalContext exContext = context.getExternalContext();
			if(opType.equals(CPFConstants.OperationType.VIEW)) {
				if (exContext.isUserInRole ("NPA")) {
					operationId = 110;
				}
			}
			if(opType.equals(CPFConstants.OperationType.CREATE)) {
				if (exContext.isUserInRole ("NPA")) {
					operationId = 110;
				}
			}
			if(opType.equals(CPFConstants.OperationType.MODIFY)) {
				if (exContext.isUserInRole ("NPA")) {
					operationId = 110;
				}
			}
			return operationId;
			//return -1;
		}
		
		
		public String saveAction (ActionEvent e) throws Exception {
			System.out.println("Inside DeployedApp");
			String returnValue = null;
			DeployedApp deployedApp = getObject();
			Organization merchantAc = _getMerchantAccount();		//Getting merchant Account from Session Object 
			deployedApp.setMerchantAccount(merchantAc);
			//Changes for User
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest request = PortletFacesUtils.getPortletRequest(ctx);
			deployedApp.setAppDeployer(request.getRemoteUser());
			
			if(this.mode == 0) {
				this.operationId = getOperationId(CPFConstants.OperationType.CREATE);
				returnValue = MgmtManager.save(deployedApp, this.operationId);
			}
			else {
				this.operationId = getOperationId(CPFConstants.OperationType.MODIFY);
				this.fillCriteria(CPFConstants.OperationType.MODIFY);
				returnValue = MgmtManager.save(deployedApp, this.criteria, this.operationId);
			}
			
			//this.mode = 2;		
			
			//FacesContext ctx = FacesContext.getCurrentInstance();
			ELContext elCtx = ctx.getELContext();
			ExpressionFactory exF = ctx.getApplication().getExpressionFactory();
			ValueExpression ve = exF.createValueExpression(elCtx, "#{listMBean109}", ListMBean_109.class);
			ListMBean_109 res = (ListMBean_109) ve.getValue(elCtx);
			res.setMode(0);	
		
			if(this.mode == 0) {
				Criteria cr = res.getCriteria();
				// call search to get pk of deployedApp
				res.setSearchKey("DeployedApp.appId");
				res.setSearechCaseSensitive(true);
				res.setSearchOperator(CPFConstants.Operators.EQUAL.toString());
				res.setSearchValue(this.appId);
				
				res.search();
				
				//read search result
				Collection<Object[]> data = res.getCollec();
				
				if (data.size() != 1) {
					logger.error ("SHould not happen...");
				}
				Object pk = data.iterator().next()[0];
				deployedApp.setId((Long) pk);
				
				logger.debug("Got an identity for deployed app : " + deployedApp);
	
				// upload data now
	
				Context ctxt = new InitialContext();
				logger.info("Context inside listener is : " + ctxt.toString());
				Object o = ctxt.lookup("maps/LocalMgmtDataLoader");
				logger.info("Lookup successful? - " + (o != null));
				logger.debug("Class of o is - " + o.getClass().getName());
				ICPFDataLoader loader = (ICPFDataLoader) o;
				loader.uploadSecurityData(url, deployedApp);
				res.setCriteria(cr);
				res.setMode(0);
			}
			this.mode = 2;
			return returnValue;
		}
		
		public String modifyAction (ActionEvent e) throws Exception {
			System.out.println("mod");
			Object val = e.getComponent().getAttributes().get("pkValue");
			this.primarykeyValue = new Long(val.toString());
			String returnString = viewAction(null);
			setMode(1);
					
			FacesContext context = FacesContext.getCurrentInstance();
			this.userIsInRole(context);
			this.getDetailsVisibility(this.userRole);
			
			System.out.println("In modifyAction: " + this);
			return "modify";
		}
		
		public String addAction (ActionEvent e) throws CPFException {
			this.mode = 0;
			this.appId = null;
			this.appDescription = null;
			this.deployDate = null;
			this.url = null;
			FacesContext context = FacesContext.getCurrentInstance();
			this.userIsInRole(context);
			this.getDetailsVisibility(this.userRole);			
			
			return SS_Constants.ReturnMessage.SUCCESS.toString();
		}
		
		public String viewAction (ActionEvent e) throws Exception {
			System.out.println("View");
			if(e != null) {
				Object val = e.getComponent().getAttributes().get("pkValue");
				this.primarykeyValue = new Long(val.toString());
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
			System.out.println("View : " );		
			distributeData(object);			//Setting all member class variables here....
			System.out.println("View end: ");
			return SS_Constants.ReturnMessage.SUCCESS.toString();
		}
	
//This will return user's Role depending upon context	
	private void userIsInRole (FacesContext context) throws CPFException{
		ExternalContext exContext = context.getExternalContext();
		if (exContext.isUserInRole("NPA")) {
			this.setUserRole ("NPA");
		} else
		{
			throw new CPFException("Not Authenticated please contact provider for necessary  privileges", 4046);
		}
	}
			
		
//Start of getting visibility for Details columns	
	private void getDetailsVisibility(String userRole) {
		this.listVisibility = new ArrayList<Boolean>();
		if(userRole.equals("NPA")) {
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

                  //LOG.debug ("sub principal: " + principal.getClass().getName());

                        if (principal instanceof GBUserPrincipal) {

                              //LOG.debug ("p: " + principal);

                              

                              Organization enterprise = ((GBUserPrincipal) principal).getMerchantAccount();

                              long enterpriseId = enterprise.getOrganizationId();

                              //LOG.debug("enterpriseId = " + enterpriseId);

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
		DeployedApp dataObject = (DeployedApp)o;
		this.appId = dataObject.getAppId();
		this.appDescription = dataObject.getAppDescription();
		this.deployDate = dataObject.getDeployDate();
	}
	
	private void resetValues() {
		this.appId = null;
		this.appDescription = null;
		this.deployDate = null;
		this.url = null;
	}	
	private void fillCriteria(CPFConstants.OperationType o) throws Exception {
		Criteria c = new Criteria();
		c.setBaseEntityName("DeployedApp");
		c.setBasePrimaryKey("id");
		c.setBasePrimaryKeyValue(this.getPrimarykeyValue());
		Long merchantId = this._getMerchantAccount().getOrganizationId();
		c.setWhere("DeployedApp.merchantAccount= (select merchantAccount from Organization merchantAccount where merchantAccount.organizationId=" + merchantId + ")");
		if(o.equals(CPFConstants.OperationType.MODIFY)) {
			c.setFields("appId, appDescription, deployDate");
		} else if(o.equals(CPFConstants.OperationType.VIEW)) {
			c.setFields("appId, appDescription, deployDate");
		}
		this.criteria = c;
	}

	/**
	 * @return the url
	 */
	public java.lang.String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(java.lang.String url) {
		this.url = url;
	}
}

