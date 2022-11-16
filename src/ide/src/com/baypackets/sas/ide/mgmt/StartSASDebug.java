package com.baypackets.sas.ide.mgmt;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class StartSASDebug extends StartSAS {

	public String getMode() {
		return ILaunchManager.DEBUG_MODE;
	}
}