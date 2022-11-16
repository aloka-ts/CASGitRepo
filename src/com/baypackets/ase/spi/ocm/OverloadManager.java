package com.baypackets.ase.spi.ocm;

/**
OverloadManager interface allows access to the Overload Control Manager in SAS. Using this interface, the applications can query the status of a Overload Parameter, add or remove Overload Listeners that are invoked when the overload condition occurs or clears.
<br>
The reference to the OverloadManager is available to the applications as a Servlet Context attribute using the name "com.baypackets.ase.spi.ocm.OverloadManager". The applications have to implement the "com.baypackets.ase.spi.ocm.OverloadListener" interface to get notified of the overload conditions.
<br>
The applications can register the listeners in the following two ways. 
<ul>
<li>The application can define the implementation class name using the listener element in the sip.xml and/or the sas.xml. In this case, the SAS would instantiate an object of the implementation class and register it with the OCM during the application startup and remove the registration when the application is stopped. </li>
<li>The application can programatically register/unregister the listener objects by using this interface. </li>
</ul>
*/
public interface OverloadManager {

	/**
	Registers the OverloadListener with the Overload Control Manager.
	*/	
	public void addOverloadListener(OverloadListener listener);
	
	/**
	Registers the OverloadListener with the Overload Control Manager.
	*/	
	public void addOverloadListener(OverloadListener listener, float threshod);

	/**
	Removes the OverloadListener from the Overload Control Manager.
	*/	
	public void removeOverloadListener(OverloadListener listener);
	
	/**
	Removes the OverloadListener from the Overload Control Manager.
	*/	
	public void removeOverloadListener(OverloadListener listener, float threshod);

	/**
	Returns whether the Overload Control Manager is enabled or not.
        */
	public boolean isEnabled();

	/**
	Returns the current value of the OverloadParameter for the specified type.
	*/
	public OverloadParameter getParameter(OverloadParameter.Type type);
}
