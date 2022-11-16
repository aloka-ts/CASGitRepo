/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
//clicks on tool bar is handled by this class
package com.baypackets.sas.ide.soa.views;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.jface.resource.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.mgmt.SASInstance;
import java.net.URL;
import org.eclipse.core.runtime.*;
import com.baypackets.sas.ide.util.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import javax.management.remote.*;
import javax.management.*;
import com.baypackets.ase.jmxmanagement.*;
import com.baypackets.sas.ide.util.StatusASE;

import java.lang.reflect.Constructor;

/**
 * This sample class demonstrates how to use the TableViewerExample 
 * inside a workbench view. The view is essentially a wrapper for
 * the TableViewerExample. It handles the Selection event for the close 
 * button.
 */

public class RegisterSOAApplicationView extends ViewPart 
{
		
	private TableRegisterSOAApplicationViewer viewer =null;	
	private Action LoadServicesAction = null;
	private Action StatusSASAction = null;
	private String SASAddress = null;
	private boolean isEmbedded = true;
	public Action statusButtonAction = null;
	private StatusASE statusASE = null;

	

	/**
	 * The constructor.
	 */
	public RegisterSOAApplicationView() 
	{
		
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	
	public void createPartControl(Composite parent) 
	{
		viewer = new TableRegisterSOAApplicationViewer(parent);

		createActions();
		createMenu();
		createToolbar();

		viewer.setViewPart(this);
		
	}
	
	public void createActions()
	{
		LoadServicesAction = new Action("Load Services ")
		{
			public void run()
			{
				LoadServices();
			}
		};
		
		StatusSASAction = new Action("CAS INFO")
		{
			public void run()
			{
				getSASInfo();
			}
		};

		statusButtonAction = new Action("CAS STATUS") //changed by reeta from CAS INFO to CAS STATUS
		{
			public void run()
			{
			//	showStatus();	//commented by reeta .when this button is pressed we are not doing anything 
				                //we should not shutdown CAS so commenting
				showSASStatus();
			}
		};
		
		
		
		try
		{
			ImageDescriptor descriptor = ImageDescriptor.createFromURL(new URL(null, "file:"+new Path(SasPlugin.fullPath("icons")).append("bp.gif").toOSString()));
			LoadServicesAction.setImageDescriptor(descriptor);
			ImageDescriptor descriptorsas = ImageDescriptor.createFromURL(new URL(null, "file:"+new Path(SasPlugin.fullPath("icons")).append("status.gif").toOSString()));
			LoadServicesAction.setToolTipText("Auto Load Services Off");
			StatusSASAction.setImageDescriptor(descriptorsas);
			StatusSASAction.setToolTipText("SIP Application Server Info");

			ImageDescriptor descriptoryellow = ImageDescriptor.createFromURL(new URL(null, "file:"+new Path(SasPlugin.fullPath("icons")).append("yellow.gif").toOSString()));

			statusButtonAction.setImageDescriptor(descriptoryellow);
			statusButtonAction.setToolTipText("Embedded CAS is not running.");



		}
		catch(Exception e)
		{
			String str = "Exception in Image Descriptor: ProvisionSOAServiceView:createActions";

                	SasPlugin.getDefault().log(str,e);
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
		
		//LoadServicesAction.setImageDescriptor();
	};
	
	private void createMenu()
	{
		IMenuManager mgr = getViewSite().getActionBars().getMenuManager();
		mgr.add(LoadServicesAction);
		mgr.add(StatusSASAction);
		mgr.add(statusButtonAction);
		
	}
	
	private void createToolbar()
	{
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(LoadServicesAction);
		mgr.add(StatusSASAction);
		mgr.add(statusButtonAction);
	}

	
	private void LoadServices()
	{
		String str = "LOADING SERVICES IN THE SERVICE MANAGEMENT VIEW";

		SasPlugin.getDefault().log(str);

		viewer.showTable();
		if(viewer.getAutoLoad())
		{
			viewer.setAutoLoad(false);
			LoadServicesAction.setToolTipText("Auto Load Services Off");
		}
		else
		{
			viewer.setAutoLoad(true);
			LoadServicesAction.setToolTipText("Auto Load Services On");
		}
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	
	public void setFocus() 
	{
		viewer.getControl().setFocus();
	}
	
	/**
	 * Handle a 'close' event by disposing of the view
	 */

	public void handleDispose() 
	{	
		//viewer.setStopThread();
		this.getSite().getPage().hideView(this);
	}
	
	private void getSASInfo()
	{
		SASAddress = viewer.getAddress();
		isEmbedded = viewer.getEmbeddedSAS();
		GetStatusSAS getSASStatus = new GetStatusSAS();
		if(getSASStatus.getStatus(SASAddress))
		{
			if((isEmbedded)&&(SASInstance.getInstance().isRunning()))
			{
				String[] buttontxt = new String[]{"OK"};
				MessageDialog messageBox = new MessageDialog(viewer.getControl().getShell(),"CAS Information", null,"Embedded CAS is running. Services Status are shown in the Table" , MessageDialog.INFORMATION, buttontxt,0);
			 
				messageBox.open();
			}
			else
			{
				String[] buttontxt = new String[]{"OK"};
				MessageDialog messageBox = new MessageDialog(viewer.getControl().getShell(),"CAS Information", null,"CAS is running on "+SASAddress+". Services Status are shown in the Table" , MessageDialog.INFORMATION, buttontxt,0);
			 
				messageBox.open();
				
			}
			
		}
		else
		{
			MessageDialog messageBox =null;
			String[] buttontxt = new String[]{"OK"};
			if(isEmbedded)
			{
				messageBox = new MessageDialog(viewer.getControl().getShell(),"CAS Information", null,"Embedded CAS is not running " , MessageDialog.INFORMATION, buttontxt,0);
			}
			else
			{
				messageBox = new MessageDialog(viewer.getControl().getShell(),"CAS Information", null,"CAS is not running " , MessageDialog.INFORMATION, buttontxt,0);
			}
		 
			messageBox.open();
			
		}
		
	}
	
	
	    
	 
	/*protected void finalize()
	{
		viewer.setStopThread();
	}*/
	public void dispose()
	{
		//super.dispose();
		viewer.setStopThread();
		//viewer.dispose();
		viewer.setDispose();
        	//LoadServicesAction = null;
        	//StatusSASAction = null;
		
	}
	
	//addded by reeta
	private void showSASStatus(){
		//doing nothing here
	}
	


		
}

