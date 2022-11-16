package com.baypackets.ase.resource;


/**
 * Factory interface used for creating the resource objects.
 * The factory objects fecilitate the applications to make use of the resource adaptor.
 * As part of the contract between the resource adaptor and the application,
 * the resource adaptor could sub-class this interface and provide more
 * resource specific application programming interfaces (APIs). 
 * 
 * In case the resource adaptor does not define any resource factory,
 * the container will provide an implementation of the 
 * <code>DefaultResourceFactory</code> and make it available to the applications.
 */
public interface ResourceFactory {
	
}
