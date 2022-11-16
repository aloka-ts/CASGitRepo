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
package com.baypackets.sas.ide.soa.views;

import java.net.InetAddress;
import java.util.ArrayList;
import org.eclipse.swt.widgets.Shell;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.io.InputStream;
import org.eclipse.swt.widgets.Display;
import java.io.FileInputStream;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import java.net.URL;
import org.eclipse.core.runtime.Path;

import com.baypackets.ase.jmxmanagement.ServiceManagementMBean;
import com.baypackets.ase.jmxmanagement.SarFileByteArray;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.BPProjectINFO;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.SASProvisionedSOAServicesUtil;
import com.baypackets.sas.ide.util.StatusASE;
import com.baypackets.sas.ide.mgmt.SASInstance;

import java.lang.reflect.Constructor;

/**
 * This class shows the status of the all the services deployed on SIP 
 * Application Server in the form of a table.
 * @author eclipse
 *
 */

public class TableProvisionSOAServiceViewer {

	private Table tableOfServices = null;

	private SASProvisionedSOAServicesUtil services = null;

	private Hashtable ASEServices = null;

	private StatusASE statusSAS = null;

	private GetStatusSAS getSASStatus = null;

	private int port = 14000;

	private int MAXSIZE = 100000;

	private int JMXURL = 1;

	private TableColumn serviceNameColumn = null;
	private TableColumn serviceVersionColumn = null;
    private TableColumn locationOfWSDL = null;

	private TableColumn serviceStatusColumn = null;

	private String SASAddress = null;

	private boolean isEmbedded = true;

	private boolean autoLoadFlag = false;

	private int infoActive = 0;

	private int deployActive = 0;

	private boolean flag = true;

	private int RowIndex = 0;

	private String ServiceName = null;

	private Button ProvisionButton = null;

	private Button UpdateService = null;

	private Button RemoveService = null;


	private int RefreshInterval = 0;

	private int counterDeploy = 0;

	String serviceName = null;
    String serviceWSDLLocation = null;
	String serviceVersion=null;

	private ProvisionSOAServiceView view = null;

	private Composite frame = null;

	private int counter = 0;

	ImageDescriptor descriptorred = null;

	ImageDescriptor descriptorgreen = null;

	private static Class jmxmpConnectorClass = null;

	static {
		try {
			jmxmpConnectorClass = Class
					.forName("javax.management.remote.jmxmp.JMXMPConnector");
			SasPlugin.getDefault()
					.log(
							"The Jmxmpconnector class loaded is "
									+ jmxmpConnectorClass);
		} catch (ClassNotFoundException e) {
			SasPlugin.getDefault().log("The JMXMPConnector class not found");
		}
	}

	public TableProvisionSOAServiceViewer(Composite parent) {
		this.addChildControls(parent);

	}

	private void addChildControls(Composite composite) {
		Composite frame = new Composite(composite, 0);
		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		GridData gd = new GridData(768);
		gd.horizontalSpan = 4;
		frame.setLayout(gl);
		frame.setLayoutData(gd);
		try {
			descriptorred = ImageDescriptor.createFromURL(new URL(null, "file:"
					+ new Path(SasPlugin.fullPath("icons")).append("red.gif")
							.toOSString()));
			descriptorgreen = ImageDescriptor.createFromURL(new URL(null,
					"file:"
							+ new Path(SasPlugin.fullPath("icons")).append(
									"green.gif").toOSString()));
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
		this.frame = frame;
		counter = 0;
		createServiceTable(frame);
		createButtons(frame);
		RefreshInterval = SasPlugin.getDelay();
		showTable();
		//getSASStatus = new GetStatusSAS();
		ThreadMonitorSAS monitorSAS = new ThreadMonitorSAS();
	}

	
	protected void showTable() {
		if (  StatusASE.getInstance().isEmbeddedRunning() || ( StatusASE.getInstance().getAttach() > 0)){	
		port = SasPlugin.getPORT();

		statusSAS = StatusASE.getInstance();
		int portSAS = statusSAS.getPORT();

		if (portSAS != 0)
			port = portSAS;

		tableOfServices.removeAll();
		
		if (getSASStatus == null)
			getSASStatus = new GetStatusSAS();

		//Neeraj 19th	ASEServices = new Hashtable();
		services = SASProvisionedSOAServicesUtil.getInstance(getSASStatus,this.getControl().getShell());
		statusSAS = StatusASE.getInstance();

		MAXSIZE = SasPlugin.getFileSIZE();
		JMXURL = SasPlugin.getJMXURL();

		try {
			
			this.SASAddress = statusSAS.getAddress();

			if (statusSAS.getAttach() == 0)
				isEmbedded = true;
			else
				isEmbedded = false;
			
			if((statusSAS.isEmbeddedRunning()||statusSAS.getAttach()!=0) && getSASStatus.getStatus(SASAddress)){
				services.setAddress(SASAddress);
				services.setAllProvisionedSOAServices();
			}else {
				return;
			}
			
		}

		catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			return;
		}

		try {
		
			ASEServices = services.getProvisioinedSOAServices();
			Set serv = ASEServices.keySet();
			Iterator itr = serv.iterator();
			while (itr.hasNext()) {
				String str = (String) itr.next();
				Hashtable entries = (Hashtable) ASEServices.get(str);

				String servicename = str;
				String version = (String) entries.get("VERSION");
				String wsdlLocation = (String) entries.get("WSDLLOCATION");

				String numbers[] = null;
				
				numbers = new String[] { servicename,version,wsdlLocation};
				
				TableItem firstItem = new TableItem(tableOfServices, SWT.NONE);
				firstItem.setText(numbers);
		}
			if(tableOfServices.getItemCount()==0){
			    ProvisionButton.setEnabled(true);
				UpdateService.setEnabled(false);
				RemoveService.setEnabled(false);
			}
			
			if(tableOfServices.getSelectionIndex()==-1)
				ProvisionButton.setEnabled(true);
			
			if (RowIndex > 0)
				tableOfServices.select(this.RowIndex);
			else
				tableOfServices.select(0);
			return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			SasPlugin.getDefault().log(
					"The exception in show table is " + e.toString());

		}
		}	

	}

	public void createButtons(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);
		ProvisionButton = new Button(composite, SWT.PUSH | SWT.CENTER);
		ProvisionButton.setText("Provision Service");
		GridData gridDeploy = new GridData();
		gridDeploy.widthHint = 110;
		gridDeploy.horizontalSpan=1;
		ProvisionButton.setLayoutData(gridDeploy);
	    ProvisionButton.setEnabled(true); 
		ProvisionButton.addSelectionListener(new SelectionAdapter() {

			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {

				//deployService();
				createProvisionPage();
	//			if(createProvisionPage()) {
//					String[] buttontxt = new String[] { "OK" };
//					MessageDialog messageBox = new MessageDialog(ProvisionButton.getShell(), "Service Provisioing", null,
//							" Service Provisioned Successfully on CAS running at "
//									+ SASAddress, MessageDialog.INFORMATION,
//							buttontxt, 0);
//				}
		

			}
		});

		UpdateService = new Button(composite, SWT.PUSH | SWT.CENTER);
		UpdateService.setText("Update Service");
		GridData gridDataStart = new GridData();
		gridDataStart.widthHint = 110;
		gridDataStart.horizontalSpan=1;
		UpdateService.setLayoutData(gridDataStart);
		UpdateService.setEnabled(false);
		UpdateService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateService();
				showTable();
				displayButtons();

			}
		});

		RemoveService = new Button(composite, SWT.PUSH | SWT.CENTER);
		RemoveService.setText("Remove Service");
		GridData gridActivate = new GridData();
		gridActivate.widthHint = 110;
		gridActivate.horizontalSpan=1;

		RemoveService.setLayoutData(gridActivate);
		RemoveService.setEnabled(false);
		RemoveService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    removeService();
				showTable();
				displayButtons();

			
		}	
		});

	}
	
	
	private void updateService(){
		int RowIndex = tableOfServices.getSelectionIndex();
		if (RowIndex > -1) {
	      this.ServiceName = (tableOfServices.getItem(RowIndex)).getText(0);
	      this.serviceVersion=(tableOfServices.getItem(RowIndex)).getText(1);
	      this.serviceWSDLLocation=(tableOfServices.getItem(RowIndex)).getText(2);

			Shell shell=this.getControl().getShell();
	        ProvisionServiceDialog dialog = new ProvisionServiceDialog(shell);
	        
            dialog.setServiceName(ServiceName);
            dialog.setServiceVer(serviceVersion);
            dialog.setServiceWSDLLocation(serviceWSDLLocation);
            dialog.isupdate();
            
			dialog.open();

			shell.setText("AGNITY CAS SOA Remote Provisioned Service upgradation");

			if (dialog.isCancelled()) {
			} else {
				this.serviceName = dialog.getServiceName();
				this.serviceWSDLLocation = dialog.getServiceWSDLLocation();
				this.serviceVersion=dialog.getServiceVersion();
				SasPlugin.getDefault().log("updateService() with..."+this.serviceName +" Ver: "+this.serviceVersion+" loc "+ dialog.getServiceWSDLLocation());
	            services.updateService(ServiceName,this.serviceVersion,this.serviceWSDLLocation);
			}
		}else {String[] buttontxt = new String[] { "OK" };
		  MessageDialog messageBox = new MessageDialog(this.getControl()
				.getShell(), "Updating Service", null,
				" Please select a Service from the table",
				MessageDialog.ERROR, buttontxt, 0);
		  messageBox.open();
		}
	}
	
	
	
	private void removeService(){
		int RowIndex = tableOfServices.getSelectionIndex();
		if (RowIndex > -1) {
	      ServiceName = (tableOfServices.getItem(RowIndex)).getText(0);
	      services.removeService(ServiceName);
		}else {String[] buttontxt = new String[] { "OK" };
		  MessageDialog messageBox = new MessageDialog(this.getControl()
				.getShell(), "Removing Service", null,
				" Please select a Service from the table",
				MessageDialog.ERROR, buttontxt, 0);
		  messageBox.open();
		}
	}



	public boolean validateAction() {
		return true;
	}

	public void createServiceTable(Composite composite) {
		counter = 1;
		GridData gd = new GridData();
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BUTTON_MASK | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		gd = new GridData(768);

		tableOfServices = new Table(composite, style);
		GridData gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 4;
		tableOfServices.setLayoutData(gridData);
		tableOfServices.setLinesVisible(true);
		tableOfServices.setHeaderVisible(true);

		tableOfServices.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				displayButtons();
			}
		});

		serviceNameColumn = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 0);
		serviceNameColumn.setText("Service Name");
		serviceNameColumn.setWidth(210);
		
		serviceVersionColumn = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 1);
		serviceVersionColumn.setText("Service Version");
		serviceVersionColumn.setWidth(210);


		locationOfWSDL = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 2);
		locationOfWSDL.setText("Location Of WSDL(file or http)");
		locationOfWSDL.setWidth(350);
		
	}

	public Control getControl() {
		return tableOfServices.getParent();
	}

	public void displayButtons() {
		
		this.RowIndex = tableOfServices.getSelectionIndex();
		if (this.RowIndex > -1) {
			ServiceName = (tableOfServices.getItem(this.RowIndex)).getText(0);

				UpdateService.setEnabled(true);
				RemoveService.setEnabled(true);
				ProvisionButton.setEnabled(false);
				

			
		} else {
			ProvisionButton.setEnabled(true);
			return;
		}
		
		

	}

	
	//***************************************************************************
	private boolean createProvisionPage() {
		
		Shell shell=this.getControl().getShell();
        ProvisionServiceDialog dialog = new ProvisionServiceDialog(shell);

		dialog.open();

		shell.setText("AGNITY CAS SOA Remote Service Provisioning ");

		if (dialog.isCancelled()) {
			return false;
		} else {
			this.serviceName = dialog.getServiceName();
			this.serviceWSDLLocation = dialog.getServiceWSDLLocation();
			this.serviceVersion=dialog.getServiceVersion();
			try{
				services.provisionService(serviceName,serviceVersion,serviceWSDLLocation);
				return true;
				}catch(Exception e){
				SasPlugin.getDefault().log(e.getMessage(), e);
				SasPlugin.getDefault().log(
						"The exception in provisioing Service" + e.toString());
				return false;
					
				}
			
		}

	}

	private boolean getPageComplete() {
		return false;
	}

	

	
	public String getAddress() {
		return this.SASAddress;
	}

	public boolean getEmbeddedSAS() {
		return this.isEmbedded;
	}

	private class ThreadMonitorSAS extends Thread {
		ThreadMonitorSAS() {
			super("CAS MOnitor Thread");
			flag = true;
			start();
		}

		public void run() {
			while (flag) {
				try {
					Thread.sleep(RefreshInterval);
					//	if(autoLoadFlag)/ reeta
					//	{
					showResults();
					//	}

				} catch (Exception e) {
					SasPlugin.getDefault().log(e.getMessage(), e);
					flag = false;
					return;
				}
			}
		}
	}

	protected void showResults() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (flag == false)
					return;
				//disposeAndCreateTable();
				  StatusASE statusASE = StatusASE.getInstance();
				if (statusASE.isEmbeddedRunning() || (statusASE.getAttach() > 0)){
					showTable();
				}
				updateSASINFO();
			}
		});
	}

	public void setStopThread() {
		flag = false;
	}

	protected void finalize() {
		flag = false;
	}

	public void setViewPart(ProvisionSOAServiceView view) {
		this.view = view;
	}

	private void updateSASINFO() {
		try {
			Action statusButtonAction = view.statusButtonAction;
			
			StatusASE statusASE = StatusASE.getInstance();
			
			if (getSASStatus.getStatus(SASAddress)) {
				if ((isEmbedded) && (SASInstance.getInstance().isRunning())) {
					statusButtonAction.setImageDescriptor(descriptorgreen);

					statusButtonAction
							.setToolTipText("Embedded CAS is running");
					//statusButtonAction.setToolTipText("Embedded CAS is running. click to Shutdown SAS"); //reeta added and commented this to folowing in all the setToolTipText
				} else if(statusASE.getAttach() > 0){
					statusButtonAction.setImageDescriptor(descriptorgreen);

					statusButtonAction.setToolTipText("CAS is running at "
							+ SASAddress);
				} else {
					String message = "The CAS instance is running at host " + SASAddress +" and is neither embedded nor attached with the IDE.";
					statusButtonAction.setImageDescriptor(descriptorred);
                    statusButtonAction.setToolTipText(message);
				}
			} else {
				if (isEmbedded) {
					statusButtonAction.setImageDescriptor(descriptorred);
					statusButtonAction
							.setToolTipText("Embedded CAS is not running");
					statusButtonAction.setEnabled(true);
				} else {
					statusButtonAction.setImageDescriptor(descriptorred);
					statusButtonAction.setToolTipText("CAS is not running at "
							+ SASAddress);
					statusButtonAction.setEnabled(true);
				}

			}
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	public void setDispose() {
		try {
			ProvisionButton.dispose();
			UpdateService.dispose();
			RemoveService.dispose();
			tableOfServices.dispose();
			serviceNameColumn.dispose();
			serviceVersionColumn.dispose();
			locationOfWSDL.dispose();
			serviceStatusColumn.dispose();
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}

	public boolean getAutoLoad() {
		return autoLoadFlag;
	}

	public void setAutoLoad(boolean fl) {
		this.autoLoadFlag = fl;
	}

}
