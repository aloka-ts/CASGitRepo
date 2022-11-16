package com.baypackets.ase.router.customize.servicenode;

import java.util.Set;

import javax.naming.NamingException;

public interface SnApplicationRouterDao {

    Set<String> findInterestedApplicationNames(String terminatingNumber,
                                               String originatingNumber,
                                               String routeInformation,
                                               String serviceTriggerMapping) throws Throwable;
    
    Set<String> findInterestedApplicationNames(String terminatingNumber,
            String originatingNumber,
            String divesionHdr,
            String diversionReason,
            String routeInformation,
            String serviceTriggerMapping) throws Throwable;

    void init(String procedureName) throws NamingException;

    boolean isInitialized();

    void warmUp();

	Set<String> findInterestedApplicationNamesWithTC(String terminatingNumber,
			String originatingNumber, String routeInformation,
			String serviceTriggerMapping, String triggerCriteria)
			throws Throwable;


}
