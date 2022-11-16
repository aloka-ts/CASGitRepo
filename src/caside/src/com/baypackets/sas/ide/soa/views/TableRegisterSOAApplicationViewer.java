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
import com.baypackets.sas.ide.util.SASDeployementSOAServicesUtil;
import com.baypackets.sas.ide.util.SASRegisteredSOAApplicationsUtil;
import com.baypackets.sas.ide.util.StatusASE;
import com.baypackets.sas.ide.mgmt.SASInstance;

import java.lang.reflect.Constructor;

/**
 * This class shows the status of the all the services deployed on SIP 
 * Application Server in the form of a table.
 * @author eclipse
 *
 */

public class TableRegisterSOAApplicationViewer {

	private Table tableOfServices = null;

	private SASRegisteredSOAApplicationsUtil services = null;
	private SASDeployementSOAServicesUtil deployedSoaServices=null; //service list to which this app can be registered
	
	java.util.List<String> servicesList=null;

	private Hashtable ASEServices = null;

	private StatusASE statusSAS = null;

	private GetStatusSAS getSASStatus = null;

	private int port = 14000;

	private int MAXSIZE = 100000;

	private int JMXURL = 1;

	private TableColumn serviceNameColumn = null;
	private TableColumn appNameColumn = null;
	private TableColumn applicationURLColumn = null;

	private TableColumn serviceStatusColumn = null;

	private String SASAddress = null;

	private boolean isEmbedded = true;

	private boolean autoLoadFlag = false;

	private int infoActive = 0;

	private int deployActive = 0;

	private boolean flag = true;

	private int RowIndex = 0;

	private String ServiceName = null;

	private Button RegisterService = null;

	private Button UpdateService = null;

	private Button RemoveService = null;


	private Button ServiceInfo = null;

	private int RefreshInterval = 0;

	private int counterDeploy = 0;

	String serviceName = null;
	String applicationName = null;

	String appURL = null;

	String pathSAR = null;

	private RegisterSOAApplicationView view = null;

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

	public TableRegisterSOAApplicationViewer(Composite parent) {
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
		
		port = SasPlugin.getPORT();

		statusSAS = StatusASE.getInstance();
		int portSAS = statusSAS.getPORT();

		if (portSAS != 0)
			port = portSAS;

		tableOfServices.removeAll();
		
		
		if (getSASStatus == null)
			getSASStatus = new GetStatusSAS();

		//Neeraj 19th	ASEServices = new Hashtable();
		services = SASRegisteredSOAApplicationsUtil.getInstance(getSASStatus,this.getControl().getShell());
		statusSAS = StatusASE.getInstance();

		

		MAXSIZE = SasPlugin.getFileSIZE();
		JMXURL = SasPlugin.getJMXURL();

		try {
			
			this.SASAddress = statusSAS.getAddress();

			if (statusSAS.getAttach() == 0)
				isEmbedded = true;
			else
				isEmbedded = false;
			//reeta modified it to not showtable when CAS is niether embedded not attached
			if((statusSAS.isEmbeddedRunning()||statusSAS.getAttach()!=0) && getSASStatus.getStatus(SASAddress)){
				services.setAddress(SASAddress);
				services.setAllRegisteredSOAApps();
			}else {
				return;
			}
			
			//To fill the combo box of the Services Name in register Application dialog we need to get all the 
			//Soa services deployed on SAS
			if((statusSAS.isEmbeddedRunning()||statusSAS.getAttach()!=0) && getSASStatus.getStatus(SASAddress)){
				deployedSoaServices=SASDeployementSOAServicesUtil.getInstance();
				deployedSoaServices.setAddress(SASAddress);
				deployedSoaServices.setAllDeployedSOAServices();
			}else {
				return;
			}
		}

		catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			return;
		}

		try {
		
			ASEServices = services.getRegsiteredSOAApps();
			Set serv = ASEServices.keySet();
			Iterator itr = serv.iterator();
			while (itr.hasNext()) {
				String str = (String) itr.next();
				Hashtable entries = (Hashtable) ASEServices.get(str);

				String appName = str;
				String serviceName = (String) entries.get("ServiceName");
				String applicationURL = (String) entries.get("ApplicationURL");

				String numbers[] = null;
				
				numbers = new String[] { appName,serviceName,applicationURL};
				
				TableItem firstItem = new TableItem(tableOfServices, SWT.NONE);
				firstItem.setText(numbers);
		}
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
		
		
		try {

			Hashtable ASEServices = deployedSoaServices.getDeployedSOAServices();
			Set serv = ASEServices.keySet();
			Iterator itr = serv.iterator();
			servicesList=new ArrayList<String>();
			while (itr.hasNext()) {
				String servicename= (String) itr.next();
                servicesList.add(servicename);
			}
		}catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			SasPlugin.getDefault().log(
					"The exception in show table is " + e.toString());

		}

	}

	public void createButtons(Composite composite) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		composite.setLayout(gridLayout);
		RegisterService = new Button(composite, SWT.PUSH | SWT.CENTER);
		RegisterService.setText("Register Application");
		GridData gridDeploy = new GridData();
		gridDeploy.widthHint = 110;
		gridDeploy.horizontalSpan=1;
		RegisterService.setLayoutData(gridDeploy);
	    RegisterService.setEnabled(true); 
		RegisterService.addSelectionListener(new SelectionAdapter() {

			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {

				//deployService();
				if(createRegisterPage()){
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(RegisterService.getShell(), "Application Registeration", null,
							" Application Registered Successfully on CAS running at "
									+ SASAddress, MessageDialog.INFORMATION,
							buttontxt, 0);
				}
		

			}
		});

		UpdateService = new Button(composite, SWT.PUSH | SWT.CENTER);
		UpdateService.setText("Update Application");
		GridData gridDataStart = new GridData();
		gridDataStart.widthHint = 110;
		gridDataStart.horizontalSpan=1;
		UpdateService.setLayoutData(gridDataStart);
		UpdateService.setEnabled(false);
		UpdateService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				  updateApplication();
				  showTable();
				  displayButtons();

			}
		});

		RemoveService = new Button(composite, SWT.PUSH | SWT.CENTER);
		RemoveService.setText("Remove Application");
		GridData gridActivate = new GridData();
		gridActivate.widthHint = 110;
		gridActivate.horizontalSpan=1;

		RemoveService.setLayoutData(gridActivate);
		RemoveService.setEnabled(false);
		RemoveService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			    removeApplication();
				showTable();
				displayButtons();

			
		}	
		});


		ServiceInfo = new Button(composite, SWT.PUSH | SWT.CENTER);

		ServiceInfo.setText("Application Info");
		GridData gridDataStopServer = new GridData();
		gridDataStopServer.widthHint = 110;
		gridDataStopServer.horizontalSpan=1;
		ServiceInfo.setLayoutData(gridDataStopServer);
		ServiceInfo.setEnabled(false);
		ServiceInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				serviceInfo();

			}
		});

	}
	
	
	private void updateApplication(){
		int RowIndex = tableOfServices.getSelectionIndex();
		if (RowIndex > -1) {
	      applicationName = (tableOfServices.getItem(RowIndex)).getText(0);
	      services.updateApplication(applicationName);
		}else {String[] buttontxt = new String[] { "OK" };
		  MessageDialog messageBox = new MessageDialog(this.getControl()
				.getShell(), "Updating Application", null,
				" Please select a application from the table",
				MessageDialog.ERROR, buttontxt, 0);
		  messageBox.open();
		}
	}
	
	
	private void removeApplication(){
		int RowIndex = tableOfServices.getSelectionIndex();
		if (RowIndex > -1) {
	      applicationName = (tableOfServices.getItem(RowIndex)).getText(0);
	      services.removeApplication(applicationName);
		}else {String[] buttontxt = new String[] { "OK" };
		  MessageDialog messageBox = new MessageDialog(this.getControl()
				.getShell(), "Application Removal", null,
				" Please select a application from the table",
				MessageDialog.ERROR, buttontxt, 0);
		  messageBox.open();
		}
	}
	
	
	private void serviceInfo() {
		try {

			if ((tableOfServices.getItemCount()) == 0) {
				return;
			}

			if (ServiceName == null) {
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(this.getControl()
						.getShell(), "Service INFORMATION", null,
						" Please select a Service from the table",
						MessageDialog.ERROR, buttontxt, 0);
				messageBox.open();
				return;
			}
			if (infoActive > 0)
				return;
			infoActive = 1;
			Shell serviceInfoPageShell = this.getControl().getShell();

			ServiceInfoDialog infoDialog = new ServiceInfoDialog(
					serviceInfoPageShell);

			infoDialog.setServices(ASEServices, ServiceName);
			infoDialog.open();

			serviceInfoPageShell.setText("AGNITY CAS Services Deployment");

			if (infoDialog.getPageComplete())
				infoDialog.setDispose();
			infoActive = 0;
			return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
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

		tableOfServices.addMouseListener(new ServiceListener());
		
		appNameColumn = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 0);
		appNameColumn.setText("Application Name");
		appNameColumn.setWidth(210);
		

		serviceNameColumn = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 1);
		serviceNameColumn.setText("Service Name");
		serviceNameColumn.setWidth(210);

		applicationURLColumn = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 2);
		applicationURLColumn.setText("Application Interface URL");
		applicationURLColumn.setWidth(210);
		
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
				this.ServiceInfo.setEnabled(true);

			
		} else
			return;

	}

	//***************************************************************************
	private boolean createRegisterPage() {
		
		Shell shell=this.getControl().getShell();
        RegisterServiceDialog dialog = new RegisterServiceDialog(shell);
        dialog.setServiceNameList(servicesList);
        dialog.open();
        shell.setText("AGNITY CAS SOA Application Registeration");

		if (dialog.isCancelled()) {
			return false;
		} else {
			this.ServiceName = dialog.getServiceName();
			this.appURL = dialog.getApplicationURL();
			this.applicationName=dialog.getApplicationName();
			try{
			services.registerApplication(applicationName, ServiceName, appURL);
			return true;
			}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
			SasPlugin.getDefault().log(
					"The exception in  reistering App" + e.toString());
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
			super("SAS MOnitor Thread");
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
				showTable();
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

	public void setViewPart(RegisterSOAApplicationView view) {
		this.view = view;
	}

	private void updateSASINFO() {
		try {
			Action statusButtonAction = view.statusButtonAction;
			//ImageDescriptor descriptorred = ImageDescriptor.createFromURL(new URL(null, "file:"+new Path(SasPlugin.fullPath("icons")).append("red.gif").toOSString()));
			//ImageDescriptor descriptorgreen = ImageDescriptor.createFromURL(new URL(null, "file:"+new Path(SasPlugin.fullPath("icons")).append("green.gif").toOSString()));

			if (getSASStatus.getStatus(SASAddress)) {
				if ((isEmbedded) && (SASInstance.getInstance().isRunning())) {
					statusButtonAction.setImageDescriptor(descriptorgreen);

					statusButtonAction
							.setToolTipText("Embedded CAS is running");
					//statusButtonAction.setToolTipText("Embedded CAS is running. click to Shutdown SAS"); //reeta added and commented this to folowing in all the setToolTipText
				} else {
					statusButtonAction.setImageDescriptor(descriptorgreen);

					statusButtonAction.setToolTipText("CAS is running at "
							+ SASAddress);
				}

			} else {
				if (isEmbedded) {
					statusButtonAction.setImageDescriptor(descriptorred);
					statusButtonAction
							.setToolTipText("Embedded CAS is not running");
					statusButtonAction.setEnabled(true);
				} else {
					statusButtonAction.setImageDescriptor(descriptorred);
					statusButtonAction.setToolTipText("SAS is not running at "
							+ SASAddress);
					statusButtonAction.setEnabled(true);
				}

			}
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private class ServiceListener implements MouseListener {
		public void mouseDown(MouseEvent e) {
		}

		public void mouseUp(MouseEvent e) {
		}

		public void mouseDoubleClick(MouseEvent e) {
			serviceInfo();
		}

		ServiceListener() {
		}
	}

	public void setDispose() {
		try {
			RegisterService.dispose();
			UpdateService.dispose();
			RemoveService.dispose();
			ServiceInfo.dispose();
			tableOfServices.dispose();
			serviceNameColumn.dispose();
			applicationURLColumn.dispose();
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
