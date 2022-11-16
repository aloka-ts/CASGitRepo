package com.genband.m5.maps.common;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;
import org.jboss.security.SecurityAssociation;
import com.genband.m5.maps.common.entity.Organization;
import com.genband.m5.maps.identity.GBUserPrincipal;

public class PortletUtil {
	private static final Logger LOG = Logger.getLogger(PortletUtil.class);
	
	/*private List<String> dataFor;
	
	private static Map<String, List<SelectItem>> dataValuesMap = new HashMap<String,  List<SelectItem>>();

	private List<SelectItem> data;

	public List<String> getDataFor() {
		return dataFor;
	}


	public void setDataFor(List<String> dataFor) {
		this.dataFor = dataFor;
		System.out.println("getting: " + dataFor);
	}
	
	public static void loadData (List<String> entities) {
		System.out.println("Make sure data is loaded for: " + entities);
		
		for (String key : entities) {

			if (! dataValuesMap.containsKey(key)) {
				synchronized (dataValuesMap) {
					if (dataValuesMap.containsKey(key))
						return; //data loaded by another thread
					System.out.println("Data for key: " + key + " is not loaded. Loading ...");
					Criteria criteria = new Criteria();
					String[] temp = key.split(" ");
					criteria.setBaseEntityName(temp[0]);
					criteria.setBasePrimaryKey(temp[1]);
					criteria.setFields(temp[0] + "." + temp[1] + ","
										+ temp[0] + "." + temp[2]);
					criteria.setFrom(temp[0] + " " + temp[0]);
					FacesContext ctx = FacesContext.getCurrentInstance();
					criteria.setWhere("");
					List<SelectItem> value = new ArrayList<SelectItem>();
					value.add(new SelectItem(key + "-Data_1", key + "-1"));
					value.add(new SelectItem(key + "-Data_2", key + "-2"));
					dataValuesMap.put(key, value);
				}
			}
			else {
				System.out.println("Loaded data is present for key: " + key);
			}
		}
	}

	public Map<String, List<SelectItem>> getDataValues () {
		return dataValuesMap;
	} 
	
	public List<SelectItem> getData(String object) {
		return dataValuesMap.get(object);
	}*/
	
	public List<SelectItem> getData(String baseEntityName, String primaryKeyName
										, String displayColumnName, boolean isStatic ,String extraPredicate) {
		List<SelectItem> returnData = new ArrayList<SelectItem>();
		Criteria criteria = new Criteria();
		criteria.setBaseEntityName(baseEntityName);
		criteria.setBasePrimaryKey(primaryKeyName);
		criteria.setFields(primaryKeyName + ", " + displayColumnName);
		criteria.setFrom(baseEntityName + " " + baseEntityName);
		String where = null;
		if(!isStatic) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest request = null;
			try {
				request = PortletFacesUtils.getPortletRequest(ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PortletSession session = request.getPortletSession();
			/*Object obj = session.getAttribute ("User");
			User user = (User)obj;
			Organization merchantAccount = user.getMerchantAccount();*/
			//Long merchantId = merchantAccount.getOrganizationId();
			Long merchantId = -100L;
			try {
				merchantId = _getMerchantAccount().getOrganizationId();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("The Extra Predicate is........."+ extraPredicate);
			if(!extraPredicate.equals("null") && !extraPredicate.equals("")){
				System.out.println("The Extra Predicate is not null........."+ extraPredicate);
				where = new String(baseEntityName + ".merchantAccount= (select merchantAccount from Organization " +
						"merchantAccount where merchantAccount.organizationId=" + merchantId + ") AND " +extraPredicate);
			}else{
				where = new String(baseEntityName + ".merchantAccount= (select merchantAccount from Organization " +
						"merchantAccount where merchantAccount.organizationId=" + merchantId + ")");
			}
			
		}
		criteria.setWhere(where);
		//CPFManager cpfManager = new CPFManager();
		Collection<Object[]> result = CPFManager.getResult(criteria);
		for (Object[] objects : result) {
			SelectItem selectitem = new SelectItem();
			if(objects[1] != null)
				selectitem.setLabel(objects[1].toString());
			else {
				selectitem.setLabel("");
			}
			selectitem.setValue(objects[0]);
			returnData.add(selectitem);
		} 
		return returnData;
	}
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
}
