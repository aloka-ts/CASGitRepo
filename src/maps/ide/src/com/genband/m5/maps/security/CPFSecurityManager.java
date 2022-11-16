package com.genband.m5.maps.security;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import com.genband.m5.maps.common.CPFConstants.OperationType;

/**
 * Does custom security check on all call to CPFSessionFacade
 * The user is checked for privilege based on the operation id passed
 * Note: operation id is assumed to be secure from tampering.
 *
 */
public class CPFSecurityManager {

	private static Logger logger = Logger.getLogger(CPFSecurityManager.class);
	
	@Resource
	private EJBContext ejbCtx;

	//Get list of roles from environment
	@Resource (name="maps/allowedRoles")
	private String rolesStr = "Authenticated"; //if no entry defined

	@PersistenceContext(unitName="data")
	private EntityManager m_entityManager;
	
	@AroundInvoke
	public Object checkCall (InvocationContext ctx) throws Exception {
		
		String mName = ctx.getMethod().getName();
		logger.info("Entered into security later...");
	    logger.debug("Security manager invoked for " + ctx.getTarget().getClass().getName() + "." + mName + "()");
	    logger.debug ("EntityManger is around? - " + (m_entityManager != null));
	    
	    try {
	    	logger.debug( "caller principal is :" + ejbCtx.getCallerPrincipal());
	    	logger.info( "caller principal is :" + ejbCtx.getCallerPrincipal());
	    	logger.error( "caller principal is :" + ejbCtx.getCallerPrincipal());
	    	logger.trace( "caller principal is :" + ejbCtx.getCallerPrincipal());


	    	logger.debug( "caller principal class is :" + ejbCtx.getCallerPrincipal().getClass().getName());
	    	logger.info( "caller principal class is :" + ejbCtx.getCallerPrincipal().getClass().getName());
	    	logger.error( "caller principal class is :" + ejbCtx.getCallerPrincipal().getClass().getName());
	    	logger.trace( "caller principal class is :" + ejbCtx.getCallerPrincipal().getClass().getName());
	    	//user may have multiple roles, in case for a given operation multiple roles are allowed
	    	//and the user has more than one such role; she is deemed to have the first role selected
	    	//from the static list in a configuration file.

		    logger.debug ("The configured roles are - " + rolesStr);
	    	String[] roles = rolesStr.split(",");
	    	
	    	if (ctx.getParameters().length < 2 
	    			|| ! (ctx.getParameters()[0] instanceof Integer)) {
	    		throw new SecurityException ("Insufficient or wrong type of parameters in method call");
	    	}
	    	int operationId = ( (Integer) ctx.getParameters()[0]).intValue();
	    	
	    	CPFMethodPermission permission = null;
	    	
    		if (ejbCtx == null)
    			throw new SecurityException ("Not running in EJB Context ...");
    		
	    	//testing...
    		/*logger.debug (ejbCtx.getEnvironment());
    		logger.debug (ejbCtx.getEnvironment().getProperty("maps/allowedRoles"));
    		logger.debug (ejbCtx.getEnvironment().getProperty("java:/comp/env/maps/allowedRoles"));*/
	    	
    		mName = mName.toUpperCase();
    		OperationType oT = null;
			if (mName.startsWith("LIST")) {
				oT = OperationType.LIST;
			}
			else if (mName.startsWith("VIEW")) {
				oT = OperationType.VIEW;
			}
			else if (mName.startsWith("CREATE")) {
				oT = OperationType.CREATE;
			}
			else if (mName.startsWith("MODIFY")) {
				oT = OperationType.MODIFY;
			}
			else if (mName.startsWith("DELETE")) {
				oT = OperationType.DELETE;
			}
			else {
				logger.error ("Incorrect operation type - " + mName);
				throw new SecurityException ("Incorrect operation type - " + mName);
			}
    		
	    	for (String role : roles) {
	    		logger.debug ("For role of " + role + " => " + ejbCtx.isCallerInRole(role));
				if (ejbCtx.isCallerInRole(role)) {
					try {
						permission = lookupMethodPermission(role, operationId, oT);
						//we break on the  first non-null lookup.
						//TODO: as per req, it should be a superset of priv. This is todo.
						if (permission != null)
							break;
					} catch (Exception e) {
						//okay
						logger.error ("Got an error in lookup - " + e.getMessage(), e);
					}
				}
			}
			if (permission == null) {
				throw new SecurityException ("User: " + ejbCtx.getCallerPrincipal().getName() + " has no permission for OP ID: " + operationId);
			}
			

	    	if (permission.getOperationType().equals(OperationType.ALL) ) {

    			logger.info("Security OK for User: " + ejbCtx.getCallerPrincipal().getName() + " has ALL permission for " + ctx.getMethod().getName());
		    	
	    	}
	    	
	 //TODO commenting out the attribtues visibility as not needed in drop-2..Uncomment this when needed..
	    	
//	    	if (permission.getOperationType().equals(OperationType.CREATE)
//	    			|| permission.getOperationType().equals(OperationType.DELETE)) {

/*	    		if (! ctx.getMethod().getName().toLowerCase().startsWith("create")
	    				&& ! ctx.getMethod().getName().toLowerCase().startsWith("delete")) {
	    			
					throw new SecurityException ("User: " + ejbCtx.getCallerPrincipal().getName() + " has no permission for " + ctx.getMethod().getName());
	    		}*/
    			
    			/*logger.info("create/delete passed security OK.");
		    	
	    	}
	    	else if (permission.getOperationType().equals(OperationType.VIEW)
	    			|| permission.getOperationType().equals(OperationType.LIST)) {*/
	    		

/*	    		if (! ctx.getMethod().getName().toLowerCase().startsWith("view")
	    				&& ! ctx.getMethod().getName().toLowerCase().startsWith("list")) {
	    			
					throw new SecurityException ("User: " + ejbCtx.getCallerPrincipal().getName() + " has no permission for " + ctx.getMethod().getName());
	    		}*/
 	    		
	    		/*if (! (ctx.getParameters()[1] instanceof Criteria)) {
		    		throw new SecurityException ("Insufficient or wrong type of parameters in method call");
		    	}
	    		logger.debug ("validating view/list call");
	    		Criteria c = (Criteria) ctx.getParameters()[1];
	    		logger.debug ("The criteria in call is: " + c);
	    		
	    		processCriteriaForView (c, permission); //correct c for permission
	    		
	    		logger.debug ("validated criteria as: " + c);
	    		
	    		ctx.setParameters(new Object[] { ctx.getParameters()[0], c}); //set new criteria object in invocation context
	    	}
	    	else if (permission.getOperationType().equals(OperationType.MODIFY)) {*/
	    		
/*	    		if (! ctx.getMethod().getName().toLowerCase().startsWith("modify") ) {
	    			
					throw new SecurityException ("User: " + ejbCtx.getCallerPrincipal().getName() + " has no permission for " + ctx.getMethod().getName());
	    		}*/
 	    		
	    		/*if (ctx.getParameters().length != 3
	    				|| ! (ctx.getParameters()[2] instanceof Criteria)) {
		    		throw new SecurityException ("Insufficient or wrong type of parameters in method call");
		    	}
	    		logger.debug ("validating modify call");
	    		Criteria c = (Criteria) ctx.getParameters()[1];
	    		logger.debug ("The criteria in call is: " + c);
	    		
	    		processCriteriaForModify (c, permission); //correct c for permission

	    		logger.debug ("validated criteria as: " + c);
	    		
	    		ctx.setParameters(new Object[] { ctx.getParameters()[0], c}); //set new criteria object in invocation context
	    	}*/
/*	    	else if (permission.getOperationType().equals(OperationType.CREATE_LIST)) {
	    		
	    		logger.info ("No impl yet. Security OK ;)");
	    	}
	    	else if (permission.getOperationType().equals(OperationType.DELETE_LIST)) {

	    		logger.info ("No impl yet. Security OK ;)");
	    	}
	    	else if (permission.getOperationType().equals(OperationType.MODIFY_LIST)) {

	    		logger.info ("No impl yet. Security OK ;)");
	    	}*/
	    			
	    	
	    	logger.info ("Invoking target with parameters: " + ctx.getParameters());
	    	return ctx.proceed();
	      
	    } catch(Throwable t) {
	    	
	    	logger.error("Got exception in security check ...", t);
	    	throw new SecurityException (t);
	    	
	    } finally {
	    	
	    	logger.debug("Exiting target.");
	      
	    }
	}
	protected CPFMethodPermission lookupMethodPermission (String role, int operationId, OperationType oT) throws Exception {
		
		CPFMethodPermission permission = null;
		
		logger.debug("lookup for role: " + role + ", & opId: " + operationId + ", & opType: " + oT);
		
		permission = new SecurityHelper().lookupPermission (m_entityManager, role, operationId, oT);
		
		logger.debug ("returning permission of - " + permission);
		return permission;
	}
	
	/*void processCriteriaForView (Criteria c, CPFMethodPermission permission) {
		String[] fields = c.fields.split(",");
		int[] failedIndex = new int[fields.length];
		int result = validate (fields, permission.getAttributes(), failedIndex);
		
		if (result == 0) { 		
			logger.info("view passed security OK.");
		}
		else {
			logger.warn("view: failed in validation of fields ... error count " + result);
			for (int i = 0; i < result; i++) {
				fields[failedIndex[i]] = "null"; //set the column field to string null
			}
		}
		StringBuilder f = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			f.append (fields[i]).append (",");
		}
		f.deleteCharAt(f.length() - 1); //delete dangling comma
		
		c.setFields(f.toString()); //set the criteria fields
		
		return;
	}*/
	
	/*private void processCriteriaForModify (Criteria c, CPFMethodPermission permission) {
		String[] fields = c.fields.split(",");
		int[] failedIndex = new int[fields.length];
		int result = validate (fields, permission.getAttributes(), failedIndex);
		
		if (result == 0) {	    		
			logger.info("view passed security OK.");
		}
		else {
			logger.warn("view: failed in validation of fields ... error count " + result);
			
			for (int i = 0; i < result; i++) {
				fields[failedIndex[i]] = null; //set the column field to null object
			}
		}
		StringBuilder f = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] == null)
				continue; //skipping
			f.append (fields[i]).append (",");
		}
		f.deleteCharAt(f.length() - 1); //delete dangling comma
		
		c.setFields(f.toString()); //set the criteria fields
		
		return;
	}*/
	/*int validate (String[] inputFields, String[] permittedFields, int[] failedIndex)  {
		
		logger.debug("validating: " + Arrays.toString (inputFields) + ", against: " + Arrays.toString (permittedFields));
		int count = 0;
		
		//N**2 complex algo ...
		for (int i = 0; i < inputFields.length; i++) {
			boolean passed = false; //if input field is validated
			logger.info(i + "th(i) " + inputFields[i]);
			for (int j = 0; j < permittedFields.length; j++) {
				logger.info(j + "th(j) " + permittedFields[j]);
				if (inputFields[i].trim().equals (permittedFields[j].trim())) {
					passed = true;
					break;
				}
			}
			if (! passed) {
				failedIndex[count++] = i;
			}
		}
		logger.debug("Finished validating: " + (count==0) + ", failedIndex is: " + Arrays.toString(failedIndex));
		
		return count;
	}*/
}
