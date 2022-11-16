package com.genband.m5.maps.sp.mbeans;

import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.apache.log4j.Logger;

import com.genband.m5.maps.common.CPFException;
import com.genband.m5.maps.common.Criteria;
import com.genband.m5.maps.common.PortletFacesUtils;
import com.genband.m5.maps.common.SS_Constants;
import com.genband.m5.maps.interfaces.ICPFSessionFacade;

public class MgmtManager {
	
	public MgmtManager () {
		Object o = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("cpf-config");
		System.out.println("cpfmgr constr: " + o);
				
	}
	
	private static Logger LOG = Logger.getLogger("com.genband.m5.maps.common");

	private static ICPFSessionFacade cpfSessionFacade;
	
	static{
		try {
			LOG.info ("Looking up ejb in the container context ...");
			Context ctx = new InitialContext();
			LOG.debug("Got initial context: " + ctx);
			
			Object o = ctx.lookup("maps/LocalMgmtSessionFacade");
			LOG.debug("Got a successful lookup: " + o);
			cpfSessionFacade = (ICPFSessionFacade) o;
			//cpfSessionFacade.initialize();
		} catch (Exception e) {
			LOG.info("Got a Exception while lookup: ", e );
		}
		LOG.info("Got a handle to CPFSessionFacade: " + cpfSessionFacade);
	}
	
	private static List<String> dataFor;
	
	//Getting Handle to CPFSessionFacade
/*	static {		
		try {
			Context ctx = new InitialContext();
			cpfSessionFacade = (CPFSessionFacade)ctx.lookup("ejb/CPFSessionFacade");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}*/
//TODO ServiceLocator for CPFSessionFacade

	public List<String> getDataFor() {
		/*FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext exCtx = ctx.getExternalContext();
		List<String> dataFor = (List<String>) exCtx.getRequestMap().get("dataFor");*/
		System.out.println("data: " + dataFor);
		return dataFor;
	}

	public void setDataFor(List<String> dataFor) {
		MgmtManager.dataFor = dataFor;
	}
	
	public static void set (List<String> data){
		dataFor = data;
	}
	
	//Operation to be performed after click on Save button and returns success or error message 
	//Has been called from Managed Bean for each portal
	public static String save(Object rootEntity, int opId) {
		
		System.out.println("Save Called");
		try {
			cpfSessionFacade.create(opId, rootEntity);
		} catch (CPFException e) {
			e.printStackTrace();
			return SS_Constants.ReturnMessage.PROVERROR.toString();
		}
		return SS_Constants.ReturnMessage.SUCCESS.toString();
	}
	
	public static String save(Object rootEntity, Criteria c, int opId){
		
		System.out.println("Save Called");
		try {
			cpfSessionFacade.modify(opId, rootEntity, c);
		} catch (CPFException e) {
			e.printStackTrace();
			return SS_Constants.ReturnMessage.PROVERROR.toString();
		}
		return SS_Constants.ReturnMessage.SUCCESS.toString();
	}
	
	//Operation to be performed after click on delete and return success or failure message
	public static String delete (Object rootEntity, Long primaryKeyValue, int opId) {
		
		try {
			cpfSessionFacade.delete(opId, rootEntity, primaryKeyValue, false);
		} catch (CPFException e) {
			e.printStackTrace();
			return SS_Constants.ReturnMessage.PROVERROR.toString();
		}
		return SS_Constants.ReturnMessage.SUCCESS.toString();
	}
	
	/**
	 * For static read or data read in the context of create/modify/view
	 * We would need to generate unique op id for such nested listing
	 * as well.
	 */
	public static Collection<Object[]> getResult (Criteria c) {
		Collection<Object[]> collection = null;
		try {
			collection = cpfSessionFacade.list(-1, c); //TODO
		} catch (CPFException e) {
			e.printStackTrace();
			return null;
		}
		LOG.info("CPFManager Length got here is: " + collection.size());
		return collection;
	}
	

	public static Collection<Object[]> getResult (int opId, Criteria c) {

		LOG.debug ("invoking session facade list op. user id: ");
		if (LOG.isDebugEnabled ())
			userctx ();

		Collection<Object[]> collection = null;
		try {
			collection = cpfSessionFacade.list(opId, c);
		} catch (CPFException e) {
			e.printStackTrace();
			return null;
		}
		LOG.info("CPFManager Length got here is: " + collection.size());
		return collection;
	}

	private static void userctx () {

		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			PortletRequest request = PortletFacesUtils.getPortletRequest(ctx);
			PortletSession session = request.getPortletSession();
			LOG.debug("Session tree: " + session.getAttributeNames());
			LOG.debug("Remote User: " + request.getRemoteUser());
			LOG.debug("Principal: " + request.getUserPrincipal().getName());
			LOG.debug("Principal class: "
					+ request.getUserPrincipal().getClass());
			LOG.debug("Admin? - " + request.isUserInRole("admin"));

		} catch (Exception e) {
			LOG.debug ("got exception", e);
		}

	}

	public static Object getDetails (int opId, Object rootEntity, Criteria c) throws CPFException {
		//Criteria criteria = CPFConfig.getCriteria(SS_Constants.OperationType.VIEW, opId);
		Object object1 = cpfSessionFacade.viewObject(opId, rootEntity, c);
		/*Object[] object = new Object[6];
		object[0] = new Long(90);
		object[1] = new String("Damu");
		java.util.Date date = new Date(System.currentTimeMillis());
		object[2] = date;
		object[3] = new String("street");
		object[4] = new String("Noida");
		object[5] = new String("India"); */
		//object = cpfSessionFacade.view(opId, criteria);
		return object1;
	}
}
