package com.genband.m5.maps.security;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.genband.m5.maps.common.CPFConstants.OperationType;


public class SecurityHelper {

	private static Logger logger = Logger.getLogger(SecurityHelper.class);

	
	public CPFMethodPermission lookupPermission (EntityManager em, String role, int operationId, OperationType oT) {
		
		CPFMethodPermission permission = null;
		
		if ( operationId == -1) {
			//return an All OK
			permission = new CPFMethodPermission ("ALL", OperationType.ALL, null);
			
			return permission;			
		}

		logger.debug ("Entity Manager is around? - " + (em != null));
		
		Query q = em.createNamedQuery("permissionLookupQuery");		
		
		logger.debug("Query object is alive? - " + (q != null));		
		
		q.setParameter("opID", operationId)
						.setParameter("opType", oT.name().toLowerCase())
						.setParameter("role", role);
		
		logger.debug ("Invoking query ...");
		
		MethodPermission p = (MethodPermission) q.getSingleResult();
		
		if (p != null) {
			
			permission = new CPFMethodPermission (p.getRootEntity(), oT, p.getAttributes() == null ? null : p.getAttributes().split(","));
		}
		
		return permission;
	}
	
}


