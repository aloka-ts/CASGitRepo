/*
 * Created on 23 March, 2006
 */
package com.baypackets.sas.ide.servicemanagement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.ase.jmxmanagement.AlcmlFileByteArray;
import com.baypackets.ase.jmxmanagement.SarFileByteArray;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.descriptors.BPSASDeploymentDescriptor;
import com.baypackets.sas.ide.mgmt.StartSAS;
import com.baypackets.sas.ide.util.ALCMLUtils;
import com.baypackets.sas.ide.util.BPProjectINFO;
import com.baypackets.sas.ide.util.BPSASSOAServicesNature;
import com.baypackets.sas.ide.util.BPSASServicesNature;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.IdeUtils;
import com.baypackets.sas.ide.util.PreDeploy;
import com.baypackets.sas.ide.util.StatusASE;

/**
 * This class deploys the application on the SIP Application Server. The only
 * condition is that it should be in running state. This class takes application
 * name, its priority, its version and the targeted address of SIP Application
 * Server from the user.
 * 
 * @author eclipse
 * 
 */

public class DeployServiceAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	private IProject contextProject = null;

	private IProject[] contextProjectsSelected = null;

	private String serviceName = null;

	private String servicePriority = null;

	private String serviceVersion = null;

	private int port = 14000;

	private int MAXSIZE = 100000;

	private int JMXURL = 1;

	private boolean isSoaService = false;

	private String SASAddress = null; // Ip Address or Host name on whcih SAS is
										// to be deployed

	private String pathSAR = null;
	
	private boolean isAlcml = false;

	private String alcmlFolder = "";
	
	private String remoteAlcmlFilePath = "";
	
	private BPProjectINFO projectInfo = null;

	private StartSAS startSAS = null;

	private IProgressMonitor monitor = null;

	private StatusASE statusASE = null;

	private boolean sasStartedSuccessfully = false;

	private boolean isEmbeddedSAS = false;

	private static Class jmxmpConnectorClass = null;

	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {

		this.window = window;
		servicePriority = "1";
		serviceVersion = "1.0";
		statusASE = StatusASE.getInstance();

	}

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

	// major modification by reeta
	public void run(IAction action) {

		int portSAS = statusASE.getPORT();

		port = SasPlugin.getPORT();
		if (portSAS != 0)
			port = portSAS;
		MAXSIZE = SasPlugin.getFileSIZE();
		JMXURL = SasPlugin.getJMXURL();
		try {
			String projectName = null;
			PreDeploy deploy = null;

			if (IdeUtils.getAllProjects().isEmpty()) {
				String[] buttontxt = new String[] { "OK" };

				MessageDialog messageBox = new MessageDialog(window.getShell(),
						"Service Deployment", null,
						"There is no CAS Service in Workspace to Deploy!!!",
						MessageDialog.INFORMATION, buttontxt, 0);
				messageBox.open();
				return;
			}
			deploy = new PreDeploy(window.getShell());

			BPSASDeploymentDescriptor sasDescriptor = null;

			if (deploy.isCancelled()) {
				return;
			}

			projectName = deploy.getProjectName();
			contextProject = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);

			// check if service is soa or not
			if (contextProject.hasNature(BPSASSOAServicesNature.NATURE_ID)) {
				isSoaService = true;
			} else if (contextProject.getDescription().hasNature(BPSASServicesNature.VTP_NATURE_ID)) {
				isSoaService = false;
				isAlcml = true;
				deployAlcmlService(deploy);
				return;
			} else {
				isSoaService = false;
				isAlcml = false;
			}
			projectInfo = BPProjectINFO.getInstance();

			SasPlugin.getDefault().log(
					"The project while  Deploying Service  is...."
							+ projectName);

			serviceName = deploy.getName();
			serviceVersion = deploy.getVersion();
			servicePriority = deploy.getPriority();
			SasPlugin.getDefault().log("The Service Name === >" + serviceName);
			SasPlugin.getDefault().log(
					"The Service Version === > " + serviceVersion);
			SasPlugin.getDefault().log(
					"The Service Priority ==== > " + servicePriority);
			sasDescriptor = new BPSASDeploymentDescriptor(projectName);

			sasDescriptor.modifyDescriptor(serviceName, serviceVersion,
					servicePriority);
			buildSAR();

			projectInfo.addProjectInfo(projectName, serviceName,
					servicePriority, serviceVersion);

			SASAddress = deploy.getAddressOfSAS();
			port = deploy.getPORT();

			if (port == 0)
				port = SasPlugin.getPORT();
			deploy.setDispose();
			pathSAR = projectInfo.getApplicationPath(projectName);
			SasPlugin.getDefault().log("SARFILE ==== >" + pathSAR);
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

//		checkServerAndCallDeploy();

	}

	public void deployAlcmlService(PreDeploy deploy) {

		IProject iProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(deploy.getProjectName());
		File projectFolder = iProject.getLocation().toFile();
		FilenameFilter filter = new FilenameFilter() {

			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.startsWith("alcml");
			}
		};

		final String[] folders = projectFolder.list(filter);
		String alcmlFolder = "";
		if (folders.length == 0) {

			String buttontxt[] = new String[] { "OK" };

			MessageDialog messageBox = new MessageDialog(window.getShell(),
					"Service Deployment", null,
					" Service Deployment Failed: Unable to find alcml folder",
					MessageDialog.ERROR, buttontxt, 0);
			messageBox.open();
			return;

		} else if (folders.length > 1) {

			alcmlFolder = deploy.displayAndSelectAlcmlFolders(folders);

		} else {
			alcmlFolder = folders[0];
		}
		if(alcmlFolder == null)
			return;

	
		SASAddress = deploy.getAddressOfSAS();
		port = deploy.getPORT();	
		if(!isLocal()){
			this.remoteAlcmlFilePath = deploy.getRemoteAlcmlFilePath();
			if(remoteAlcmlFilePath == null)
				return;
			this.remoteAlcmlFilePath = this.remoteAlcmlFilePath + "/" + deploy.getName();
		}
		
		
		if (port == 0)
			port = SasPlugin.getPORT();

		deployAlcmlService(deploy.getProjectName(), deploy.getName(), deploy
				.getVersion(), deploy.getPriority(), alcmlFolder, iProject
				.getLocation().toString());
		deploy.setDispose();
	}

	public void deployAlcmlService(String projectName, String serviceName,
			String version, String priority, String alcmlFolder,
			String projectLocation) {

		String alcmlFilePath = "file:\\" +  projectLocation + "/" + alcmlFolder + "/"
				+ "CallDesign_alcml_out.xml";

		BPSASDeploymentDescriptor sasDescriptor = null;
		projectInfo = BPProjectINFO.getInstance();

		SasPlugin.getDefault().log(
				"The project while  Deploying Service  is...." + projectName);

		this.serviceName = serviceName;
		serviceVersion = version;
		servicePriority = priority;
		SasPlugin.getDefault().log("The Service Name === >" + this.serviceName);
		SasPlugin.getDefault().log(
				"The Service Version === > " + serviceVersion);
		SasPlugin.getDefault().log(
				"The Service Priority ==== > " + servicePriority);
		if(!isLocal()){
			String tempRemoteAlcmlFilePath;
			if(!this.remoteAlcmlFilePath.contains("file:")){
				tempRemoteAlcmlFilePath = "file:\\" + this.remoteAlcmlFilePath + "/CallDesign_alcml_out.xml";
			} else {
				tempRemoteAlcmlFilePath = this.remoteAlcmlFilePath + "/CallDesign_alcml_out.xml";
			}
			
			pathSAR = ALCMLUtils.createSarAndGetPath(this.serviceName,
					serviceVersion, servicePriority, alcmlFilePath, projectLocation
							+ "/" + alcmlFolder , tempRemoteAlcmlFilePath);
		} else{
		pathSAR = ALCMLUtils.createSarAndGetPath(this.serviceName,
				serviceVersion, servicePriority, alcmlFilePath, projectLocation
						+ "/" + alcmlFolder);
		}
		
		this.alcmlFolder= projectLocation + "/" + alcmlFolder + "/";
		
		sasDescriptor = new BPSASDeploymentDescriptor(projectName,
		 this.serviceName, serviceVersion, servicePriority);
		
		 sasDescriptor.modifyDescriptor(this.serviceName, serviceVersion,
		 servicePriority);

		projectInfo.addProjectInfo(projectName, this.serviceName,
				servicePriority, serviceVersion);

		checkServerAndCallDeploy();
		
		
	}
	
	private void checkServerAndCallDeploy(){
		try {
			// Deploying the Service
			// First check whether Sip Application Server is running on the at
			// the host

			// If SASADDERESS IS LOCAl //check whether it is running or not.
			String[] buttontxt = new String[] { "OK" };
			GetStatusSAS getStatusSAS = new GetStatusSAS();
			boolean running = getStatusSAS.getStatus(SASAddress);
			String message = "";
			if (running) {
				if (statusASE.isEmbeddedRunning()) {
					deployService();
					return;
				} else if (statusASE.getAttach() > 0) {
					deployService();
					return;
				} else {
					message = "The CAS instance is running at host "
							+ SASAddress
							+ " and is neither embedded nor attached with the IDE.";
					MessageDialog messageBox = new MessageDialog(window
							.getShell(), "Service Deployment", null, message,
							MessageDialog.ERROR, buttontxt, 0);
					messageBox.open();
					return;
				}
			} else {
				message = "The CAS instance is currently not running at host :"
						+ SASAddress;
				MessageDialog messageBox = new MessageDialog(window.getShell(),
						"Service Deployment", null, message,
						MessageDialog.ERROR, buttontxt, 0);
				messageBox.open();
				return;
			}
		} catch (Exception e) {
			String buttontxt[] = new String[] { "OK" };

			MessageDialog messageBox = new MessageDialog(window.getShell(),
					"Service Deployment", null, " Service Deployment Failed",
					MessageDialog.ERROR, buttontxt, 0);
			messageBox.open();
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}
	
	
	private AlcmlFileByteArray[] getALCMLFilesAsStreams(String folderPath){
		
		File folderFile = new File(folderPath);
		String[] fileListArr = folderFile.list(new FilenameFilter(){
		
			public boolean accept(File dir, String name) {
				return name.contains(".xml");
			}
		});
		
		AlcmlFileByteArray[] byteArr = new AlcmlFileByteArray[fileListArr.length];
		int i=0;
		for(String fileStr: fileListArr){
			try {
				byte[] by  = new byte[MAXSIZE];
				FileInputStream in = new FileInputStream(folderPath + "/" + fileStr);
				in.read(by);
				int length =0;
				for(int j=0;j<by.length;j++){
					if(by[j] == 0)
						break;
					length ++;
				}
				byteArr[i] = new AlcmlFileByteArray();
				byteArr[i].setFileName(fileStr);
				byteArr[i].setByteArray(Arrays.copyOf(by, length));
				i++;
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return byteArr;
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
				params = new Object[] { serviceName, serviceVersion,
						servicePriority, pathSAR };
			} else {
				InputStream stream = new FileInputStream(pathSAR);
				byte[] bytes = new byte[MAXSIZE];
				stream.read(bytes);
				SarFileByteArray byteArray = new SarFileByteArray();
				byteArray.setByteArray(bytes);
				HashMap hash = new HashMap();
				hash.put("sar", byteArray);
				if(isAlcml){
					hash.put("alcmlFiles", getALCMLFilesAsStreams(this.alcmlFolder));
					hash.put("alcmlPathOnserver", this.remoteAlcmlFilePath);
				}
				
				signs = new String[] { "java.lang.String", "java.lang.String",
						"java.lang.String", "java.lang.String",
						"java.util.HashMap" };
				params = new Object[] { serviceName, serviceVersion,
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

			if (isSoaService) {
				stdMBeanName = new ObjectName(
						domain
								+ ":type=com.baypackets.ase.jmxmanagement.SOAServiceManagement,index=1");
			} else {
				stdMBeanName = new ObjectName(
						domain
								+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");
			}

			String deploystatus = mbsc.invoke(stdMBeanName, "redeploy", params,
					signs).toString();
			if (deploystatus.equals("true")) {
				if ((this.isEmbeddedSAS) && (statusASE.isEmbeddedRunning())) {
					MessageDialog messageBox = new MessageDialog(window
							.getShell(), "Service Deployment", null,
							serviceName
									+ " deployed Successfully on Embedded CAS",
							MessageDialog.INFORMATION, buttontxt, 0);
					messageBox.open();
				} else {
					if (this.isEmbeddedSAS) {
						MessageDialog messageBox = new MessageDialog(
								window.getShell(),
								"Service Deployment",
								null,
								serviceName
										+ " deployed Successfully on Local CAS",
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					} else {
						MessageDialog messageBox = new MessageDialog(
								window.getShell(),
								"Service Deployment",
								null,
								serviceName
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
							serviceName + " deployment Failed on Embedded CAS",
							MessageDialog.INFORMATION, buttontxt, 0);
					messageBox.open();
				} else {
					if (this.isEmbeddedSAS) {
						MessageDialog messageBox = new MessageDialog(window
								.getShell(), "Service Deployment", null,
								serviceName
										+ " deployement Failed on Local CAS",
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					} else {
						MessageDialog messageBox = new MessageDialog(
								window.getShell(),
								"Service Deployment",
								null,
								serviceName
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
					"Service Deployment", null, serviceName
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

	public void selectionChanged(IAction action, ISelection currentSelection) {

		contextProjectsSelected = IdeUtils.getProject(currentSelection);
		if (contextProjectsSelected != null) {

			int len = contextProjectsSelected.length;
			if (len > 0) {
				int indx = 0;
				for (indx = 0; indx < len; indx++) {
					contextProject = contextProjectsSelected[indx];
					break;

				}
			}

		}
	}

	public static IWorkspace getWorkspace() {

		return ResourcesPlugin.getWorkspace();
	}

	public void buildSAR() {
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

		String ServiceName = new String();
		ServiceName = serviceName;

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
			SasPlugin.getDefault().log("Commands====> " + commands[3]);

			SasPlugin.getDefault().log("ROOTFOLDER====>" + rootFolder);
		} catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
		}

		try {
			Process process = Runtime.getRuntime().exec(commands, envp,
					builtservice);

			InputStream stream = process.getInputStream();

			process.waitFor();

			stream.close();

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	public boolean getStatus() {
		JMXServiceURL url = null;
		JMXConnector jmxc = null;
		MBeanServerConnection mbsc = null;
		String domain = null;
		ObjectName stdMBeanName = null;
		Integer status = null;
		try {

			if (JMXURL == 1) {
				url = new JMXServiceURL("jmxmp", SASAddress, port);
			} else {
				url = new JMXServiceURL(
						"service:jmx:rmi:///jndi/rmi://SASAddress:" + port
								+ "/jmxsasserver");
			}
			SasPlugin.getDefault().log("JMXServiceURL===== >" + url);

			// jmxc = JMXConnectorFactory.connect(url, null); //reeta commented
			// above code

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
			status = (Integer) mbsc.invoke(stdMBeanName, "status", null, null);

			if (status == null)
				return false;

			if (status.intValue() == 1) {

				return true;
			} else
				return false;

		} catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
			return false;
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

	protected Action getSASStartupCompleteAction() {
		return new Action("SAS Startup Status") {
			public void run() {
				if (getStatus()) {
					MessageDialog.openInformation(window.getShell(),
							"CAS Startup", "CAS is Successfully started");

					sasStartedSuccessfully = true;
					deployService();
				} else {
					sasStartedSuccessfully = false;
					String tx[] = new String[] { "OK" };
					MessageDialog ms = new MessageDialog(window.getShell(),
							"CAS Startup", null, "CAS failed to start",
							MessageDialog.ERROR, tx, 0);
					ms.open();

				}

			}
		};
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

}

