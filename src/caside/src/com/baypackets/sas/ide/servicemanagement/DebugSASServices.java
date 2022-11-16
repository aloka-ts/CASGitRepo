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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.baypackets.ase.jmxmanagement.SarFileByteArray;
import com.baypackets.ase.jmxmanagement.ServiceManagementMBean;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.mgmt.SASInstance;
import com.baypackets.sas.ide.util.BPProjectINFO;
import com.baypackets.sas.ide.util.IdeUtils;
import com.baypackets.sas.ide.util.SASServices;
import com.baypackets.sas.ide.util.StatusASE;

public class DebugSASServices implements IObjectActionDelegate,
		IJavaLaunchConfigurationConstants {
	private IWorkbenchWindow window;
	IWorkbenchPart activePart;
	protected IVMInstall jre;
	private IProject contextProject = null;
	private String ServiceName = null;
	private String ProjectName = null;
	private IProject[] contextProjectsSelected = null;
	String SASAddress = null;
	private String pathSAR;
	private boolean isEmbeddedSAS;

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

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

		activePart = targetPart;
		this.window = activePart.getSite().getWorkbenchWindow();
	}

	public void run(IAction action) {

		ISelection currentSelection = SasPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService()
				.getSelection();

		contextProjectsSelected = IdeUtils.getProject(currentSelection);

		SasPlugin.getDefault().log(
				"Selected Debuging Project !!!!!!!!!!!!!!"
						+ contextProjectsSelected);

		if (contextProjectsSelected == null) {
			contextProjectsSelected = getWorkspace().getRoot().getProjects();
		}

		if (contextProjectsSelected == null) {
			String st[] = new String[] { "OK" };
			MessageDialog dia = new MessageDialog(window.getShell(),
					"AGNITY CAS Service Debug", null,
					"Please select a Project ", MessageDialog.WARNING, st, 0);

			dia.open();
			return;
		}

		int len = contextProjectsSelected.length;
		if (len > 0) {
			int indx = 0;
			if (contextProjectsSelected[indx].isOpen()) // debug only the first
														// open project selected
			{
				contextProject = contextProjectsSelected[indx];
				ProjectName = contextProject.getName();
				// buildSAR();
				debugProject();
			}

		}

	}

	private void debugProject() {
		SasPlugin.getDefault().log("Starting Debuging Project !!!!!!!!!!!!!!");
		statusASE = StatusASE.getInstance();

		int debugPort = SasPlugin.getDefault().getDebugPort();

		SASAddress = statusASE.getAddress();
		// JMX Port
		port = SasPlugin.getPORT();
		int portSAS = statusASE.getPORT();

		if (portSAS != 0)
			port = portSAS;

		boolean running = SASInstance.getInstance().isRunning();
		boolean connected = SASInstance.getInstance().isConnected();
		if (!running && !connected) {

			String strMessage = "The IDE is neither attached with CAS nor Embedded CAS is running to Debug this Service !!!";

			String[] tx = new String[] { "OK" };
			MessageDialog ms = new MessageDialog(window.getShell(),
					"AGNITY CAS Service Debug", null, strMessage,
					MessageDialog.ERROR, tx, 0);
			ms.open();
			return;
		}

		this.buildSAR();
		this.fillServiceParameters();
		if (!this.checkIfDeployed()) {
			String strMessage = "Exiting from AGNITY CAS Service Debug as Service Should be in ACTIVE State to Debug !!!";

			String[] tx = new String[] { "OK" };
			MessageDialog ms = new MessageDialog(window.getShell(),
					"AGNITY CAS Service Debug", null, strMessage,
					MessageDialog.INFORMATION, tx, 0);
			ms.open();
			return;
		}

		// Get the previously stored launch configuration and remove it.....
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager
				.getLaunchConfigurationType(ID_REMOTE_JAVA_APPLICATION);
		try {

			ILaunchConfiguration[] configurations = manager
					.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals(ProjectName)) {
					configuration.delete();
					break;
				}
			}
		} catch (Exception ex) {
			SasPlugin.getDefault().log(ex.getMessage(), ex);
		}

		// Create a new launch configuration and launch the process....
		ILaunchConfigurationWorkingCopy workingCopy;

		try {
			SasPlugin.getDefault().log("Project Name is======> " + ProjectName);
			workingCopy = type.newInstance(null, ProjectName); // /launch name
			workingCopy.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					ProjectName); // project name

			workingCopy
					.setAttribute(
							IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,
							IJavaLaunchConfigurationConstants.ID_SOCKET_ATTACH_VM_CONNECTOR);

			String srcepath = getWorkspace().getRoot().getProject(ProjectName)
					.getFullPath().toOSString();
			SasPlugin.getDefault().log("Source Path is======> " + srcepath);
			java.util.List<String> srclist = new java.util.ArrayList<String>();
			srclist.add(srcepath);
			workingCopy.setAttribute(ATTR_SOURCE_PATH, srclist);
			// added by reeta

			if (statusASE.isEmbeddedRunning()) {
				SASAddress = InetAddress.getLocalHost().getHostAddress();
			}
			SasPlugin.getDefault().log(
					"The host and port are==============>" + SASAddress
							+ " and port " + debugPort);
			HashMap attrMap = new HashMap();
			attrMap.put("hostname", SASAddress);
			attrMap.put("port", "" + debugPort);

			workingCopy
					.setAttribute(
							IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP,
							attrMap);
			ILaunchConfiguration configuration = workingCopy.doSave();
			ILaunch launch = configuration.launch(ILaunchManager.DEBUG_MODE,
					null, false);
			SasPlugin.getDefault().log("Open Debug Prespective");
			window.getWorkbench().showPerspective(
					"org.eclipse.debug.ui.DebugPerspective", window);

			if (!launch.isTerminated()) {
				String strMessage = ProjectName
						+ " source has been attached successfully with Embedded CAS for debugging!!!";

				String[] tx = new String[] { "OK" };
				MessageDialog ms = new MessageDialog(window.getShell(),
						"AGNITY CAS Service Debug", null, strMessage,
						MessageDialog.ERROR, tx, 0);
				ms.open();
				return;
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			String[] tx = new String[] { "OK" };
			MessageDialog ms = new MessageDialog(activePart.getSite()
					.getShell(), "AGNITY CAS Service Debug", null, e
					.getMessage(), MessageDialog.ERROR, tx, 0);
			ms.open();
			return;
		}

	}

	public void buildSAR() {
		SasPlugin.getDefault().log("Building SAR ==== >");
		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			contextProject.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

		IFolder rootfolder = contextProject.getFolder("WEB-INF");

		try {
			IFolder binFolder = contextProject.getFolder("bin");

			if (binFolder.exists()) {
				IFolder classesFolder = rootfolder.getFolder("classes");

				if (classesFolder.exists())
					classesFolder.delete(true, null);

				binFolder.copy(classesFolder.getFullPath(), true, null);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IVMInstall jre;
		SasPlugin.getDefault().log("Root Folder ==== >" + rootfolder);

		IPath folderPath = rootfolder.getFullPath();
		SasPlugin.getDefault().log("Folder path ====> " + folderPath);
		IPath projectPath = contextProject.getFullPath();

		SasPlugin.getDefault().log("PROJECT RELATED PATH====>" + projectPath);

		jre = JavaRuntime.getDefaultVMInstall();

		SasPlugin.getDefault().log("THE JRE is ====>" + jre);
		File jdkHome = jre.getInstallLocation();
		String jdkPath = jdkHome.getPath();

		SasPlugin.getDefault().log("JDK HOME=====>" + jdkPath);

		IPath jdkPATH = new Path(jdkPath);

		String commands[] = new String[4];

		commands[0] = jdkPATH.append("bin").append("jar").toString();

		SasPlugin.getDefault().log(commands[0]);
		commands[1] = "cvf";

		// commands[2] =ServiceName+".sar";
		commands[2] = contextProject.getName() + ".sar";
		String rootFolder = "";

		String envp[] = new String[1];

		envp[0] = contextProject.getLocation().toOSString();
		File builtservice = null;
		try {
			IPath projectpath = contextProject.getLocation();

			builtservice = projectpath.toFile();

			SasPlugin.getDefault().log("SAR filepath ==== >" + builtservice);

			commands[3] = "WEB-INF";
			SasPlugin.getDefault().log("execute Commands====> " + commands);

			SasPlugin.getDefault().log("ROOTFOLDER====>" + rootFolder);
			SasPlugin.getDefault().log("Env prop====>" + envp);
		} catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
		}

		try {
			Process process = Runtime.getRuntime().exec(commands, envp,
					builtservice);

			InputStream stream = process.getInputStream();

			SasPlugin.getDefault().log(
					"waiting for exec sar thread to compplete====>");

			// process.waitFor();

			SasPlugin.getDefault().log(
					"bulding Sar command has been executed ==== >");

			stream.close();

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void fillServiceParameters() {
		SasPlugin.getDefault().log("Fill Service parameters");
		BPProjectINFO projectInfo = BPProjectINFO.getInstance();

		projectInfo.initialize(ProjectName);
		ServiceName = projectInfo.getApplicationName(ProjectName);
		serviceVersion = projectInfo.getApplicationVersion(ProjectName);
		servicePriority = projectInfo.getApplicationPriority(ProjectName);
		pathSAR = projectInfo.getApplicationPath(ProjectName);
		SasPlugin.getDefault()
				.log("The Service version is..." + serviceVersion);
		SasPlugin.getDefault().log(
				"The Service Priority is..." + servicePriority);
		SasPlugin.getDefault().log("The Path to SAR is..." + pathSAR);

	}

	private boolean checkIfDeployed() {
		boolean deployed = false;
		boolean exitDebug = false;
		SASServices services = SASServices.getInstance();
		services.setAddress(SASAddress);
		services.setAllServices();

		Hashtable ASEServices = services.getServices();
		Set serv = ASEServices.keySet();
		Iterator itr = serv.iterator();
		String str = null;
		String state = null;
		while (itr.hasNext()) {
			str = (String) itr.next();
			Hashtable entries = (Hashtable) ASEServices.get(str);
			SasPlugin.getDefault().log(
					"The Service from map is.." + str
							+ "Current Service to debug with version is"
							+ this.getId());
			if (str.startsWith(this.getId())) {
				deployed = true;
				state = (String) entries.get("STATUS");
				break;
			}

		}
		if (deployed) {

			SasPlugin.getDefault().log(
					"The Service is already deployed..." + pathSAR);

			SasPlugin.getDefault().log(
					"Service State ....for " + str + " is " + state);
			if (state.equals("ACTIVE")) {
				exitDebug = true;
			} else if (state.equals("INSTALLED")) {
				if (MessageDialog
						.openQuestion(window.getShell(),
								"AGNITY CAS Service Debug",
								"This Service is Deployed on CAS do you want to Activate it ?")) {
					startServiceAction(str);
					SasPlugin.getDefault().log("Service is started....." + str);
					activateServiceAction(str);
					SasPlugin.getDefault().log(
							"Service is activated....." + str);
					exitDebug = true;
				} else {
					SasPlugin.getDefault().log("Exiting from debug.." + str);
					exitDebug = false;
				}
			} else if (state.equals("READY")) {
				if (MessageDialog.openQuestion(window.getShell(),
						"AGNITY CAS Service Debug",
						"This Service is Ready.Do you want to Activate it ?")) {
					activateServiceAction(str);
					SasPlugin.getDefault().log(
							"Service is activated....." + str);
					exitDebug = true;
				} else {
					SasPlugin.getDefault().log("Exiting from debug.." + str);
					exitDebug = false;

				}
			}
			return exitDebug;
		} else {
			SasPlugin.getDefault().log(
					"The Service is not deployed...lets deploy it");
			String strMessage = "This Service is not Deployed.Do you want to Deploy and Activate it ?";

			if (MessageDialog.openQuestion(window.getShell(),
					"AGNITY CAS Service Debug", strMessage)) {
				this.deployService();

				// get id of this deployed service and activate it
				services.setAllServices();
				ASEServices = services.getServices();
				serv = ASEServices.keySet();
				itr = serv.iterator();

				while (itr.hasNext()) {
					str = (String) itr.next();
					Hashtable entries = (Hashtable) ASEServices.get(str);
					SasPlugin
							.getDefault()
							.log(
									"The Service from map is.."
											+ str
											+ "Current Service to debug with version is"
											+ this.getId());

					if (str.startsWith(this.getId())) {
						SasPlugin
								.getDefault()
								.log(
										"The Service has got deployed lets start and activate it");
						this.startServiceAction(str);
						this.activateServiceAction(str);
						break;
					}

				}
				exitDebug = true;

			} else {
				SasPlugin
						.getDefault()
						.log(
								"Returning as service is not deployed and also user donot want to deploy it!!!");
				exitDebug = false;
			}

			return exitDebug;

		}

	}

	private void startServiceAction(String ServiceName) {
		JMXConnector jmxc = null;
		try {
			String signs[] = new String[] { "java.lang.String" };

			Object params[] = { ServiceName };

			JMXServiceURL url = null;
			MBeanServerConnection mbsc = null;
			String domain = null;
			ObjectName stdMBeanName = null;
			ServiceManagementMBean proxy = null;
			String deploystatus = "";
			try {

				// jmxc = JMXConnectorFactory.connect(url, null); reeta
				// commented it

				// Check if the JMXMP connector is available reeta adding it
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
				// reeta modified connection as per connector
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
					MessageDialog messageBox = new MessageDialog(window
							.getShell(), "Service Starting", null, ServiceName
							+ " Starting Failed on CAS running at "
							+ SASAddress, MessageDialog.INFORMATION, buttontxt,
							0);
					messageBox.open();
				}

			} catch (Exception exe) {
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(window.getShell(),
						"Service Starting", null, ServiceName
								+ " Starting Failed on CAS running at "
								+ SASAddress, MessageDialog.INFORMATION,
						buttontxt, 0);
				messageBox.open();
				SasPlugin.getDefault().log(exe.getMessage(), exe);

			} finally {
				if (jmxc != null)
					jmxc.close();
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void activateServiceAction(String ServiceName) {
		try {

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

				// reeta added following code
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
					MessageDialog messageBox = new MessageDialog(window
							.getShell(), "Service Activation", null,
							ServiceName
									+ " activation Failed on CAS running at "
									+ SASAddress, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
				}

			} catch (Exception exe) {
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(window.getShell(),
						"Service Activation", null, ServiceName
								+ " activation Failed on CAS running at "
								+ SASAddress, MessageDialog.INFORMATION,
						buttontxt, 0);
				messageBox.open();
				SasPlugin.getDefault().log(exe.getMessage(), exe);
			} finally {
				if (jmxc != null)
					jmxc.close();
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	public void deployService() {
		String[] buttontxt = new String[] { "OK" };
		JMXServiceURL url = null;
		JMXConnector jmxc = null;
		MBeanServerConnection mbsc = null;
		String domain = null;
		ObjectName stdMBeanName = null;
		String signs[] = null;
		Object params[] = null;
		try {

			SasPlugin.getDefault().log("PATH SAR ===== " + pathSAR);

			if (isLocal()) {
				signs = new String[] { "java.lang.String", "java.lang.String",
						"java.lang.String", "java.lang.String" };
				params = new Object[] { ServiceName, serviceVersion,
						servicePriority, pathSAR };
			} else {
				InputStream stream = new FileInputStream(pathSAR);
				byte[] bytes = new byte[MAXSIZE];
				stream.read(bytes);
				SarFileByteArray byteArray = new SarFileByteArray();
				byteArray.setByteArray(bytes);
				HashMap hash = new HashMap();
				hash.put("sar", byteArray);
				signs = new String[] { "java.lang.String", "java.lang.String",
						"java.lang.String", "java.lang.String",
						"java.util.HashMap" };
				params = new Object[] { ServiceName, serviceVersion,
						servicePriority, pathSAR, hash };
			}

			SasPlugin.getDefault().log("CAS ADDRESSS ====>" + SASAddress);
			// jmxc = JMXConnectorFactory.connect(url, null); //reeta commented
			// it and added following code in place of it
			// Check if the JMXMP connector is available reeta adding it
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
			// reeta modified connection as per connector
			SasPlugin.getDefault().log("JMXServiceURL===== >" + url);

			mbsc = jmxc.getMBeanServerConnection();
			domain = mbsc.getDefaultDomain();
			stdMBeanName = new ObjectName(
					domain
							+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

			String deploystatus = mbsc.invoke(stdMBeanName, "redeploy", params,
					signs).toString();
			if (deploystatus.equals("true")) {
				if ((this.isEmbeddedSAS) && (statusASE.isEmbeddedRunning())) {
					MessageDialog messageBox = new MessageDialog(window
							.getShell(), "Service Deployment", null,
							ServiceName
									+ " deployed Successfully on Embedded CAS",
							MessageDialog.INFORMATION, buttontxt, 0);
					messageBox.open();
				} else {
					if (this.isEmbeddedSAS) {
						MessageDialog messageBox = new MessageDialog(
								window.getShell(),
								"Service Deployment",
								null,
								ServiceName
										+ " deployed Successfully on Local CAS",
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					} else {
						MessageDialog messageBox = new MessageDialog(
								window.getShell(),
								"Service Deployment",
								null,
								ServiceName
										+ " deployed Successfully on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}
				}

			} else {
				if ((this.isEmbeddedSAS) && (statusASE.isEmbeddedRunning())) {
					MessageDialog messageBox = new MessageDialog(window
							.getShell(), "Service Deployment", null,
							ServiceName + " deployment Failed on Embedded CAS",
							MessageDialog.INFORMATION, buttontxt, 0);
					messageBox.open();
				} else {
					if (this.isEmbeddedSAS) {
						MessageDialog messageBox = new MessageDialog(window
								.getShell(), "Service Deployment", null,
								ServiceName
										+ " deployement Failed on Local CAS",
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					} else {
						MessageDialog messageBox = new MessageDialog(
								window.getShell(),
								"Service Deployment",
								null,
								ServiceName
										+ " deployement Failed on CAS running at "
										+ SASAddress,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}
				}
			}
		} catch (Exception exe) {
			SasPlugin.getDefault().log(exe.getMessage(), exe);
			MessageDialog messageBox = new MessageDialog(window.getShell(),
					"Service Deployment", null, ServiceName
							+ "deployement Failed " + exe.getMessage(),
					MessageDialog.INFORMATION, buttontxt, 0);
			messageBox.open();
		} finally {
			try {
				if (jmxc != null)
					jmxc.close();
			} catch (IOException e) {
				SasPlugin
						.getDefault()
						.log(
								"getStatus() of DeployServiceAction has thrown expection");
			}
		}

	}

	public String getId() {
		String id = null;

		if (this.ServiceName != null && this.serviceVersion != null) {
			id = this.ServiceName + "_" + this.serviceVersion;
		}
		return id;
	}

	private boolean isLocal() {
		try {
			String localHost = InetAddress.getLocalHost().getHostAddress()
					.toString();

			SasPlugin.getDefault().log("LOCAL HOST ====>" + localHost);

			SasPlugin.getDefault().log("CAS ADDRESSS ==== >" + SASAddress);
			if (SASAddress.trim().equals("127.0.0.1")) {
				this.isEmbeddedSAS = true;
				return true;
			}

			if (SASAddress.trim().equals("localhost")) {
				this.isEmbeddedSAS = true;
				return true;
			}

			if (SASAddress.trim().equals(localHost)) {
				this.isEmbeddedSAS = true;
				return true;
			} else {
				this.isEmbeddedSAS = false;
				return false;
			}
		} catch (Exception e) {
			this.isEmbeddedSAS = false;
			return false;
		}
	}

	public static IWorkspace getWorkspace() {

		return ResourcesPlugin.getWorkspace();
	}

	public IProject[] getSelectedProject() {
		return contextProjectsSelected;

	}

	// changed by reeta
	public void selectionChanged(IAction action, ISelection currentSelection) {
	}

	public void dispose() {

	}

	String serviceVersion;
	String servicePriority;
	private int JMXURL = 1;
	private int port = 14000;
	StatusASE statusASE;
	private int MAXSIZE = 100000;
}
