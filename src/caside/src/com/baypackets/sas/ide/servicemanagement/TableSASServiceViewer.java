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
package com.baypackets.sas.ide.servicemanagement;

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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import com.baypackets.sas.ide.util.IdeUtils;
import com.baypackets.sas.ide.util.SASServices;
import com.baypackets.sas.ide.util.StatusASE;
import com.baypackets.sas.ide.mgmt.SASInstance;

import java.lang.reflect.Constructor;

/**
 * This class shows the status of the all the services deployed on SIP 
 * Application Server in the form of a table.
 * @author eclipse
 *
 */

public class TableSASServiceViewer {

	private Table tableOfServices = null;

	private SASServices services = null;

	private Hashtable ASEServices = null;

	private StatusASE statusSAS = null;

	private GetStatusSAS getSASStatus = null;

	private int port = 14000;

	private int MAXSIZE = 100000;

	private int JMXURL = 1;

	private TableColumn serviceNameColumn = null;

	private TableColumn serviceDeployingAgent = null;

	private TableColumn serviceStatusColumn = null;

	private String SASAddress = null;

	private boolean isEmbedded = true;

	private boolean autoLoadFlag = false;

	private int infoActive = 0;

	private int deployActive = 0;

	private boolean flag = true;

	private int RowIndex = 0;

	private String ServiceName = null;

	private Button DeployButton = null;

	private Button StartService = null;

	private Button ActivateService = null;

	private Button DeactivateService = null;

	private Button UndeployService = null;

	private Button StopService = null;

	private String ProjectName = null;

	private Button StopServer = null;

	private Button ServiceInfo = null;

	private int RefreshInterval = 0;

	private int counterDeploy = 0;

	String serviceName = null;

	String serviceVersion = null;

	String servicePriority = null;

	String pathSAR = null;

	private ServiceManagementView view = null;

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

	public TableSASServiceViewer(Composite parent) {
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

		//Neeraj 19th	ASEServices = new Hashtable();
		services = SASServices.getInstance();
		statusSAS = StatusASE.getInstance();

		if (getSASStatus == null)
			getSASStatus = new GetStatusSAS();

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
				services.setAllServices();
			}else {
				return;
			}
			
		}

		catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			return;
		}

		try {

			ASEServices = services.getServices();
			Set serv = ASEServices.keySet();
			Iterator itr = serv.iterator();
			while (itr.hasNext()) {
				String str = (String) itr.next();
				Hashtable entries = (Hashtable) ASEServices.get(str);

				String servicename = str;
				/*int i = str.lastIndexOf("_");
				 int len = str.length();

				 String serviceNamewithVersion = str.substring(0,i);

				 i = serviceNamewithVersion.lastIndexOf("_");


				 len = serviceNamewithVersion.length();

				 String servicename = serviceNamewithVersion.substring(0,i);*/
				String deployed = (String) entries.get("DEPLOYEDBY");

				String state = (String) entries.get("STATUS");

				String numbers[] = null;
				if (deployed.trim().equals("CLIENT_IDE")) {
					numbers = new String[] { servicename, "CAS IDE", state };
				} else {
					numbers = new String[] { servicename, deployed, state };
				}
				TableItem firstItem = new TableItem(tableOfServices, SWT.NONE);
				firstItem.setText(numbers);
			}
			if(tableOfServices.getItemCount()==0){
				DeployButton.setEnabled(true);
				StartService.setEnabled(false);
				DeactivateService.setEnabled(false);
				ActivateService.setEnabled(false);
				StopService.setEnabled(false);
				UndeployService.setEnabled(false);
				ServiceInfo.setEnabled(false);
			}
			if(tableOfServices.getSelectionIndex()==-1)
				DeployButton.setEnabled(true);
			
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
		gridLayout.numColumns = 7;
		composite.setLayout(gridLayout);
		DeployButton = new Button(composite, SWT.PUSH | SWT.CENTER);
		DeployButton.setText("Deploy Service");
		GridData gridDeploy = new GridData();
		gridDeploy.widthHint = 110;
		DeployButton.setLayoutData(gridDeploy);
		//	DeployButton.setEnabled(true); //added n removed by reeta
		DeployButton.addSelectionListener(new SelectionAdapter() {

			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {

				deployService();

			}
		});

		StartService = new Button(composite, SWT.PUSH | SWT.CENTER);
		StartService.setText("Start Service");
		GridData gridDataStart = new GridData();
		gridDataStart.widthHint = 110;
		StartService.setLayoutData(gridDataStart);
		StartService.setEnabled(false);
		StartService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				startServiceAction();
				showTable();
				displayButtons();

			}
		});

		ActivateService = new Button(composite, SWT.PUSH | SWT.CENTER);
		ActivateService.setText("Activate Service");
		GridData gridActivate = new GridData();
		gridActivate.widthHint = 110;

		ActivateService.setLayoutData(gridActivate);
		ActivateService.setEnabled(false);
		ActivateService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				activateServiceAction();
				showTable();
				displayButtons();

			}
		});

		DeactivateService = new Button(composite, SWT.PUSH | SWT.CENTER);
		DeactivateService.setText("Deactivate Service");
		GridData gridDeActivate = new GridData();
		gridDeActivate.widthHint = 120;
		DeactivateService.setLayoutData(gridDeActivate);
		DeactivateService.setEnabled(false);
		DeactivateService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deactivateServiceAction();
				showTable();
				displayButtons();

			}
		});

		StopService = new Button(composite, SWT.PUSH | SWT.CENTER);
		StopService.setText("Stop Service");
		GridData gridDatastop = new GridData();
		gridDatastop.widthHint = 100;
		StopService.setLayoutData(gridDatastop);
		StopService.setEnabled(false);

		StopService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stopServiceAction();
				showTable();
				displayButtons();

			}
		});

		UndeployService = new Button(composite, SWT.PUSH | SWT.CENTER);
		UndeployService.setText("Undeploy Service");
		GridData gridDataUndeploy = new GridData();
		gridDataUndeploy.widthHint = 110;
		UndeployService.setLayoutData(gridDataUndeploy);

		UndeployService.setEnabled(false);
		UndeployService.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				undeployServiceAction();
				showTable();
				displayButtons();

			}
		});

		/*StopServer = new Button(composite, SWT.PUSH | SWT.CENTER);
		 
		 StopServer.setText("Stop SAS");		
		 GridData gridDataStopServer = new GridData ();
		 gridDataStopServer.widthHint = 110;
		 StopServer.setLayoutData(gridDataStopServer);
		 
		 StopServer.setEnabled(true);
		 StopServer.addSelectionListener(new SelectionAdapter()
		 {
		 public void widgetSelected(SelectionEvent e)
		 {
		 stopServerAction();
		 showTable();
		 displayButtons();
		 
		 
		 }
		 });	*/

		ServiceInfo = new Button(composite, SWT.PUSH | SWT.CENTER);

		ServiceInfo.setText("Service Info");
		GridData gridDataStopServer = new GridData();
		gridDataStopServer.widthHint = 110;
		ServiceInfo.setLayoutData(gridDataStopServer);
		ServiceInfo.setEnabled(true);
		ServiceInfo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				serviceInfo();

			}
		});

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

	private void deployService() {
		try {
			if (counterDeploy > 0)
				return;

			counterDeploy = 1;
			Shell deployPageShell = this.getControl().getShell();
			
			if(IdeUtils.getNonSOAProjects().isEmpty()){
				String[] buttontxt = new String[] { "OK" };

				MessageDialog messageBox = new MessageDialog(
						deployPageShell,
						"Service Deployment",
						null,
						"There is no CAS Service in Workspace to Deploy!!!",
						MessageDialog.INFORMATION, buttontxt, 0);
				messageBox.open();
				return;
			}

			if (!createDeployPage(deployPageShell)) {
				counterDeploy = 0;
				return;
			}

			//counterDeploy=0;

			//********************************************************************************
			String[] buttontxt = new String[] { "OK" };
			boolean running =getSASStatus.getStatus(SASAddress);
			StatusASE statusASE = StatusASE.getInstance();
			String message="";
			if(running){
				if (statusASE.isEmbeddedRunning()){
	
				}else if (statusASE.getAttach() > 0){
					
			   }else {
					message = "The CAS instance is running at host " + SASAddress +" and is neither embedded nor attached with the IDE.";
					MessageDialog messageBox = new MessageDialog(
							deployPageShell,
							"Service Deployment",
							null,
							message,
							MessageDialog.ERROR, buttontxt, 0);
					messageBox.open();
					counterDeploy = 0;
					return;
			   }
			} else{
				message = "The CAS instance is currently not running at host :" + SASAddress;
				MessageDialog messageBox = new MessageDialog(
						deployPageShell,
						"Service Deployment",
						null,
						message,
						MessageDialog.ERROR, buttontxt, 0);
				messageBox.open();
				counterDeploy = 0;
				return;
			}

//			if (!getSASStatus.getStatus(SASAddress)) {
//				String[] buttontxt = new String[] { "OK" };
//
//				if ((SASAddress.equals("127.0.0.1"))
//						|| (SASAddress.equals("localhost"))) {
//					MessageDialog messageBox = new MessageDialog(
//							this.getControl().getShell(),
//							"Service Deployment",
//							null,
//							" Service Deployment Failed as CAS is not running at LocalHost",
//							MessageDialog.INFORMATION, buttontxt, 0);
//					messageBox.open();
//
//					counterDeploy = 0;
//					return;
//				}
//
//				MessageDialog messageBox = new MessageDialog(this.getControl()
//						.getShell(), "Service Deployment", null,
//						" Service Deployment Failed as CAS is not running at "
//								+ SASAddress, MessageDialog.INFORMATION,
//						buttontxt, 0);
//
//				messageBox.open();
//				counterDeploy = 0;
//				return;
//
//			}

			buttontxt = new String[] { "OK" };
			JMXConnector jmxc = null;
			try {
				SasPlugin.getDefault().log(
						"The port and CAS address inside deploy Service from Service "
								+ "managemnet view is" + port + " ,"
								+ SASAddress);
				InputStream stream = new FileInputStream(pathSAR);
				byte[] bytes = new byte[MAXSIZE];
				stream.read(bytes);
				SarFileByteArray byteArray = new SarFileByteArray();
				byteArray.setByteArray(bytes);
				HashMap hash = new HashMap();
				hash.put("sar", byteArray);
				String signs[] = new String[] { "java.lang.String",
						"java.lang.String", "java.lang.String",
						"java.lang.String", "java.util.HashMap" };
				Object params[] = { serviceName, serviceVersion,
						servicePriority, pathSAR, hash };
				
				
				SasPlugin.getDefault().log("Service version is ===== >" +serviceVersion +" Service Name "+serviceName+
						" Priority "+servicePriority +" Path Sar "+ pathSAR + "HashTable is.."+hash);

				JMXServiceURL url = null;
				jmxc = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;

				//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it and added code below
				//				 Check if the JMXMP connector is available reeta adding it
				if (JMXURL == 1) {
					url = new JMXServiceURL("jmxmp", SASAddress, port);
					Class[] paramTypes = { JMXServiceURL.class };
					Constructor cons = jmxmpConnectorClass
							.getConstructor(paramTypes);

					Object[] args = { url };
					Object theObject = cons.newInstance(args);
					jmxc = (JMXConnector) theObject;
					jmxc.connect();
				} else {
					url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
							+ SASAddress + ":" + port + "/jmxsasserver");
					jmxc = JMXConnectorFactory.connect(url, null);

				}
				//reeta modified connection as per connector
				SasPlugin.getDefault().log("JMXServiceURL===== >" + url);

				mbsc = jmxc.getMBeanServerConnection();
				domain = mbsc.getDefaultDomain();

				stdMBeanName = new ObjectName(
						domain
								+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

				String deploystatus = mbsc.invoke(stdMBeanName, "redeploy",
						params, signs).toString();
				SasPlugin.getDefault().log("Deploymet status is ===== >" +deploystatus );
				if (deploystatus.equals("true")) {

//					statusASE=StatusASE.getInstance();
					//reeta modified it
		//			if(statusASE.getAttach()!=0){
					 MessageDialog messageBox = new MessageDialog(SasPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),"Service Deployment", null,serviceName +" deployed Successfully on CAS running at "+SASAddress, MessageDialog.INFORMATION, buttontxt,0);
					 messageBox.open();
			//		 showTable();
			//		}
				} else {
					MessageDialog messageBox = new MessageDialog(this
							.getControl().getShell(), "Service Deployment",
							null, serviceName
									+ "  deployement Failed on CAS running at "
									+ SASAddress, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();

				}

			} catch (Exception exe) {
				SasPlugin.getDefault().log(exe.getMessage(), exe);
				MessageDialog messageBox = new MessageDialog(
						this.getControl().getShell(),
						"Service Deployment",
						null,
						serviceName
								+ " deployement Failed. [Connection is refused or CAS is not running at "
								+ SASAddress + " ]", MessageDialog.INFORMATION,
						buttontxt, 0);
				messageBox.open();
			}

			finally //added by reeta
			{
				if (jmxc != null)
					jmxc.close();
				counterDeploy = 0;
			}
			//
		} catch (Exception e) {
			counterDeploy = 0;
			SasPlugin.getDefault().log(e.getMessage(), e);
			return;
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
		gridData.horizontalSpan = 7;
		tableOfServices.setLayoutData(gridData);
		tableOfServices.setLinesVisible(true);
		tableOfServices.setHeaderVisible(true);

		tableOfServices.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				displayButtons();
			}
		});

		tableOfServices.addMouseListener(new ServiceListener());

		serviceNameColumn = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 0);
		serviceNameColumn.setText("Service Id");
		serviceNameColumn.setWidth(260);
		//serviceNameColumn.addSelectionListener(new ColumnListener());

		serviceDeployingAgent = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 1);
		serviceDeployingAgent.setText("Service Deployer");
		serviceDeployingAgent.setWidth(260);
		//serviceDeployingAgent.addSelectionListener(new ColumnListener());

		serviceStatusColumn = new TableColumn(tableOfServices, SWT.CENTER
				| SWT.BUTTON_MASK, 2);
		serviceStatusColumn.setText("Service Status");
		serviceStatusColumn.setWidth(260);

		//serviceStatusColumn.addSelectionListener(new ColumnListener());
		// Add listener to column so tasks are sorted by owner when clicked

	}

	public Control getControl() {
		return tableOfServices.getParent();
	}

	public void displayButtons() {
		
		this.RowIndex = tableOfServices.getSelectionIndex();
		if (this.RowIndex > -1) {
			ServiceName = (tableOfServices.getItem(this.RowIndex)).getText(0);
            ServiceInfo.setEnabled(true);
			if ((tableOfServices.getItem(this.RowIndex)).getText(2).trim()
					.equals("READY")) {
				//	DeployButton.setEnabled(false);
				DeployButton.setEnabled(false);
				StartService.setEnabled(false);
				DeactivateService.setEnabled(false);
				ActivateService.setEnabled(true);
				StopService.setEnabled(true);
				UndeployService.setEnabled(false);
			}

			if ((tableOfServices.getItem(this.RowIndex)).getText(2).trim()
					.equals("ACTIVE")) {

				DeployButton.setEnabled(false); //added n removed by reeta
				StartService.setEnabled(false);
				DeactivateService.setEnabled(true);
				ActivateService.setEnabled(false);
				StopService.setEnabled(false);
				UndeployService.setEnabled(false);

			}

			if ((tableOfServices.getItem(this.RowIndex)).getText(2).trim()
					.equals("INSTALLED")) {

				DeployButton.setEnabled(true);//added n removed by reeta
				StartService.setEnabled(true);
				DeactivateService.setEnabled(false);
				ActivateService.setEnabled(false);
				StopService.setEnabled(false);
				UndeployService.setEnabled(true);

			}
			
		} else {
			DeployButton.setEnabled(true);
			return;
		}

	}

	private void startServiceAction() {
		JMXConnector jmxc = null;
		try {
			if (getSASStatus.getStatus(SASAddress)) {
				String signs[] = new String[] { "java.lang.String" };

				Object params[] = { ServiceName };

				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String deploystatus = "";
				try {

					//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it

					//					 Check if the JMXMP connector is available reeta adding it
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName, "startservice",
							params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(this
								.getControl().getShell(), "Service Starting",
								null, ServiceName
										+ " Starting Failed on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(this
							.getControl().getShell(), "Service Starting", null,
							ServiceName + " Starting Failed on CAS running at "
									+ SASAddress, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}

			}

			else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void stopServiceAction() {
		try {

			if (getSASStatus.getStatus(SASAddress)) {
				String signs[] = new String[] { "java.lang.String" };

				Object params[] = { ServiceName };

				JMXServiceURL url = null;
				JMXConnector jmxc = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;

				String deploystatus = "";
				try {
					//	 jmxc = JMXConnectorFactory.connect(url, null); reeta commented it

					//					 Check if the JMXMP connector is available reeta adding it
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//reeta modified connection as per connector

					SasPlugin.getDefault().log(
							"JMXConnector ========== > " + jmxc);

					mbsc = jmxc.getMBeanServerConnection();

					SasPlugin.getDefault().log(
							"MBeanServerConnection========== > " + mbsc);

					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName, "stopservice",
							params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(this
								.getControl().getShell(), "Service Stopping",
								null, ServiceName
										+ " stopping Failed on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(this
								.getControl().getShell(), "Service Stopping",
								null, ServiceName
										+ " stopping Failed on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}
					SasPlugin.getDefault().log(exe.getMessage(), exe);
				} finally {
					if (jmxc != null)
						jmxc.close();
				}

			} else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void activateServiceAction() {
		try {

			if (getSASStatus.getStatus(SASAddress)) {
				String signs[] = new String[] { "java.lang.String" };

				Object params[] = { ServiceName };

				JMXServiceURL url = null;
				JMXConnector jmxc = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;

				String deploystatus = "";
				try {

					//reeta added following code
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//
					mbsc = jmxc.getMBeanServerConnection();

					SasPlugin.getDefault().log(
							"MBeanServerConnection========== > " + mbsc);

					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName, "activateservice",
							params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(
								this.getControl().getShell(),
								"Service Activation",
								null,
								ServiceName
										+ " activation Failed on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(this
							.getControl().getShell(), "Service Activation",
							null, ServiceName
									+ " activation Failed on CAS running at "
									+ SASAddress, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
					SasPlugin.getDefault().log(exe.getMessage(), exe);
				} finally {
					if (jmxc != null)
						jmxc.close();
				}

			} else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void deactivateServiceAction() {
		try {

			if (getSASStatus.getStatus(SASAddress)) {
				String signs[] = new String[] { "java.lang.String" };

				Object params[] = { ServiceName };

				JMXServiceURL url = null;
				JMXConnector jmxc = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;

				String deploystatus = "";
				try {

					//	 jmxc = JMXConnectorFactory.connect(url, null); reeta commented it
					//reeta added following code as per connector
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//
					mbsc = jmxc.getMBeanServerConnection();

					SasPlugin.getDefault().log(
							"MBeanServerConnection========== > " + mbsc);

					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName,
							"deactivateservice", params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(
								this.getControl().getShell(),
								"Service De-activation",
								null,
								ServiceName
										+ " deactivation Failed on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(this
							.getControl().getShell(), "Service De-activation",
							null, ServiceName
									+ " deactivation Failed on CAS running at "
									+ SASAddress, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
					SasPlugin.getDefault().log(exe.getMessage(), exe);
				} finally {
					if (jmxc != null)
						jmxc.close();

				}
			} else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void undeployServiceAction() {
		try {

			if (getSASStatus.getStatus(SASAddress)) {
				String signs[] = new String[] { "java.lang.String" };
				Object params[] = { ServiceName };
				JMXServiceURL url = null;
				JMXConnector jmxc = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String deploystatus = "";
				try {

					//	jmxc = JMXConnectorFactory.connect(url, null);

					//reeta added following code as per connector
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//

					mbsc = jmxc.getMBeanServerConnection();

					SasPlugin.getDefault().log(
							"MBeanServerConnection========== > " + mbsc);

					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName, "undeployservice",
							params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(
								this.getControl().getShell(),
								"Service Undeployment",
								null,
								ServiceName
										+ " undeployment Failed on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(this
							.getControl().getShell(), "Service Undeployment",
							null, ServiceName
									+ " undeployment Failed on CAS running at "
									+ SASAddress, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
					SasPlugin.getDefault().log(exe.getMessage(), exe);
				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			} else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	//***************************************************************************
	private boolean createDeployPage(Shell shell) {

		DeployServiceDialog dialog = new DeployServiceDialog(shell);

		if(dialog==null){
			return false;
		}
		dialog.open();

		shell.setText("AGNITY CAS Services Deployment");

		if (dialog.isCancelled()) {
			return false;
		} else {
			this.serviceName = dialog.getServiceName();
			this.servicePriority = dialog.getServicePriority();
			this.serviceVersion = dialog.getServiceVersion();
			this.pathSAR = dialog.getServicePath();
			this.port = dialog.getPORT();
			this.SASAddress = dialog.getAddressOfSAS();
			return true;
		}

	}

	private boolean getPageComplete() {
		return false;
	}

	private void stopServerAction() {
		try {

			if (getSASStatus.getStatus(SASAddress)) {

				JMXServiceURL url = null;
				JMXConnector jmxc = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;

				String deploystatus = "";
				try {

					// jmxc = JMXConnectorFactory.connect(url, null);

					//reeta added following code as per connector
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// 

					mbsc = jmxc.getMBeanServerConnection();

					SasPlugin.getDefault().log(
							"MBeanServerConnection========== > " + mbsc);

					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					mbsc.invoke(stdMBeanName, "stopserver", null, null);
					showResultDialog();
				} catch (Exception e) {
					SasPlugin.getDefault().log(e.getMessage(), e);
					showResultDialog();
				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			} else {
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(this.getControl()
						.getShell(), "CAS Shutdown", null,
						"CAS is not running", MessageDialog.INFORMATION,
						buttontxt, 0);
				messageBox.open();

			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void showResultDialog() {
		try {
			if (getSASStatus.getStatus(SASAddress)) {
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(this.getControl()
						.getShell(), "CAS Shutdown", null,
						"CAS Shutdown Failed", MessageDialog.ERROR, buttontxt,
						0);
				messageBox.open();
			} else {
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(this.getControl()
						.getShell(), "CAS Shutdown", null,
						"CAS Successfully stopped", MessageDialog.INFORMATION,
						buttontxt, 0);
				messageBox.open();
			}
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			String[] buttontxt = new String[] { "OK" };
			MessageDialog messageBox = new MessageDialog(this.getControl()
					.getShell(), "CAS Shutdown", null, "CAS Shutdown Failed",
					MessageDialog.ERROR, buttontxt, 0);
			messageBox.open();
		}
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
				
					if ( statusASE.isEmbeddedRunning() || (statusASE.getAttach() > 0)){
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

	public void setViewPart(ServiceManagementView view) {
		this.view = view;
	}

	private void updateSASINFO() {
		try {
			Action statusButtonAction = view.statusButtonAction;
			//ImageDescriptor descriptorred = ImageDescriptor.createFromURL(new URL(null, "file:"+new Path(SasPlugin.fullPath("icons")).append("red.gif").toOSString()));
			//ImageDescriptor descriptorgreen = ImageDescriptor.createFromURL(new URL(null, "file:"+new Path(SasPlugin.fullPath("icons")).append("green.gif").toOSString()));
			   StatusASE statusASE = StatusASE.getInstance();
				
			if (getSASStatus!=null && getSASStatus.getStatus(SASAddress)) {
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
			DeployButton.dispose();
			StartService.dispose();
			ActivateService.dispose();
			DeactivateService.dispose();
			UndeployService.dispose();
			StopService.dispose();
			ServiceInfo.dispose();
			tableOfServices.dispose();
			serviceNameColumn.dispose();
			serviceDeployingAgent.dispose();
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
