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
package com.baypackets.sas.ide.mgmt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.ase.jmxmanagement.ServiceManagementMBean;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.IdeUtils;
import com.baypackets.sas.ide.util.SASServices;
import com.baypackets.sas.ide.util.StatusASE;


/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class StartSAS implements IWorkbenchWindowActionDelegate, IJavaLaunchConfigurationConstants {
	
	private static final String LOCAL_HOST = "127.0.0.1".intern();
	private IWorkbenchWindow window;
	IProgressMonitor monitor = null;	//Added by Neeraj
	private int port = 14000;
	
	private int debugPort = 8000;

	private int MAXSIZE = 100000;

	private int JMXURL = 1;
	
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

	/**
	 * The constructor.
	 */
	public StartSAS() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {

		//Run the validation routine first....
		if(!validate()){
			return;
		}
		
	
		//Get the ASE_HOME classpath variable and check whether it is defined or not.
		IPath pathASEHOME = JavaCore.getClasspathVariable("ASE_HOME");
				
		//Get the previously stored launch configuration and remove it.....
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(ID_JAVA_APPLICATION);
		try{
		
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals("CAS")) {
					configuration.delete();
					break;
				}
			}
		}catch(Exception ex){
			SasPlugin.getDefault().log(ex.getMessage(), ex);
		}
		
		
		//Create a new launch configuration and launch the process.... 
		ILaunchConfigurationWorkingCopy workingCopy;
		String mode = this.getMode();
		try{
			workingCopy = type.newInstance(null,"CAS");
			
			ISelection selection = window.getSelectionService().getSelection();
//			IProject project = IdeUtils.getProject(selection);
//			String projectName = project != null ? project.getName() : "";
//			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
			
			IProject[] projects = IdeUtils.getProject(selection);//added by reeta
			IProject project=null;
			String projectName="";
			
			if(projects!=null){
			for(int i=0;i<projects.length;i++){
				project = projects[i];
				projectName = project != null ? project.getName() : "";
			}
		}
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName); //need to resole later
			
			IVMInstall jre= JavaRuntime.getDefaultVMInstall(); 
			File jdkHome = jre.getInstallLocation(); //usr/java
			
			SasPlugin.getDefault().log("VMINSTALl======>"+jre);
			SasPlugin.getDefault().log("JDK HOME=====>"+jdkHome);
						 
			workingCopy.setAttribute(ATTR_MAIN_TYPE_NAME,"com.baypackets.ase.startup.Bootstrap");
			IPath toolsjar = new Path(jdkHome.getAbsolutePath()).append("lib").append("tools.jar");
			
			IPath sasclasspath = new Path("ASE_HOME").append("bpjars").append("bootstrap.jar");
			SasPlugin.getDefault().log("CASCLASSPATH====>  "+sasclasspath);
			
			//added by reeta
			IPath sasclasspath1 = new Path("ASE_HOME").append("otherjars").append("fscontext.jar");
			SasPlugin.getDefault().log("CASCLASSPATH fscontext====>  "+sasclasspath1);
			
			IPath sasclasspath2 = new Path("ASE_HOME").append("otherjars").append("providerutil.jar");
			SasPlugin.getDefault().log("CASCLASSPATH fscontext====>  "+sasclasspath2);
			
			IPath sasclasspath3 = new Path("ASE_HOME").append("otherjars").append("jmxremote_optional.jar");
			SasPlugin.getDefault().log("CASCLASSPATH fscontext====>  "+sasclasspath3);

			IPath sasclasspath4 = new Path("ASE_HOME").append("httpjars").append("ase-http.jar");
		
			
			SasPlugin.getDefault().log("CASCLASSPATH httpjars====>  "+sasclasspath4);
			
			IPath httpjavaUrlFactory4 = pathASEHOME.append("Common").append("thirdParty").append("jakarta-tomcat").append("common").append("lib").append("naming-java.jar");
			//
			
			IPath systemLibsPath = new Path(JavaRuntime.JRE_CONTAINER);
			SasPlugin.getDefault().log("SYSTEM LIBS PATH======> "+systemLibsPath);
					
			IRuntimeClasspathEntry toolsEntry = JavaRuntime.newArchiveRuntimeClasspathEntry(toolsjar);
			toolsEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			 
			IRuntimeClasspathEntry sasClassPathEntry = JavaRuntime.newVariableRuntimeClasspathEntry(sasclasspath);
			sasClassPathEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			//added by reeta
			IRuntimeClasspathEntry sasClassPathEntry1 = JavaRuntime.newVariableRuntimeClasspathEntry(sasclasspath1);
			sasClassPathEntry1.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IRuntimeClasspathEntry sasClassPathEntry2 = JavaRuntime.newVariableRuntimeClasspathEntry(sasclasspath2);
			sasClassPathEntry2.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IRuntimeClasspathEntry sasClassPathEntryhttp = JavaRuntime.newVariableRuntimeClasspathEntry(sasclasspath4);
			sasClassPathEntryhttp.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IRuntimeClasspathEntry sasClassPathEntry3 = JavaRuntime.newVariableRuntimeClasspathEntry(sasclasspath3);
			sasClassPathEntry3.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IRuntimeClasspathEntry sasClassPathEntry4 = JavaRuntime.newVariableRuntimeClasspathEntry(httpjavaUrlFactory4);
			sasClassPathEntry4.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IPath soaJibBinder = new Path("ASE_HOME").append("tools/jibx/jibx-bind.jar");
			IRuntimeClasspathEntry soaClassPathEntry = JavaRuntime.newVariableRuntimeClasspathEntry(soaJibBinder);
			soaClassPathEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IPath soaJibBinder1 = new Path("ASE_HOME").append("tools/jibx/jibx-javatools.jar");
			IRuntimeClasspathEntry soaClassPathEntry1 = JavaRuntime.newVariableRuntimeClasspathEntry(soaJibBinder1);
			soaClassPathEntry1.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IPath soaJibBinder2 = new Path("ASE_HOME").append("tools/jibx/jibx-extras.jar");
			IRuntimeClasspathEntry soaClassPathEntry2 = JavaRuntime.newVariableRuntimeClasspathEntry(soaJibBinder2);
			soaClassPathEntry2.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IPath soaJibBinder3 = new Path("ASE_HOME").append("tools/jibx/commons-lang-2.5.jar");
		//	IPath soaJibBinder3 = new Path("ASE_HOME").append("tools/jibx/*.jar");
			IRuntimeClasspathEntry soaClassPathEntry3 = JavaRuntime.newVariableRuntimeClasspathEntry(soaJibBinder3);
			soaClassPathEntry3.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
//			IPath soaJibBinder4 = new Path("ASE_HOME").append("tools/jibx/commons-lang-2.0.jar");
//			IRuntimeClasspathEntry soaClassPathEntry4 = JavaRuntime.newVariableRuntimeClasspathEntry(soaJibBinder4);
//			soaClassPathEntry4.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			

			IPath soaJibBinder5 = new Path("ASE_HOME").append("tools/jibx/bcel.jar");
			IRuntimeClasspathEntry soaClassPathEntry5 = JavaRuntime.newVariableRuntimeClasspathEntry(soaJibBinder5);
			soaClassPathEntry5.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			IPath soaJibBinder6 = new Path("ASE_HOME").append("tools/jibx/jaxme-js-0.3.jar");
			IRuntimeClasspathEntry soaClassPathEntry6 = JavaRuntime.newVariableRuntimeClasspathEntry(soaJibBinder6);
			soaClassPathEntry6.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			
			
			
			//
			IRuntimeClasspathEntry systemLibsPathEntry = JavaRuntime.newRuntimeContainerClasspathEntry(systemLibsPath, IRuntimeClasspathEntry.STANDARD_CLASSES);
			
			List classpaths = new ArrayList();
			classpaths.add(toolsEntry.getMemento());
			classpaths.add(sasClassPathEntry.getMemento());
			//added by reeta
			classpaths.add(sasClassPathEntry1.getMemento());
			classpaths.add(sasClassPathEntry2.getMemento());
			classpaths.add(sasClassPathEntry3.getMemento());
			classpaths.add(sasClassPathEntry4.getMemento());
			classpaths.add(soaClassPathEntry.getMemento());
			classpaths.add(soaClassPathEntry1.getMemento());
			classpaths.add(soaClassPathEntry2.getMemento());
			classpaths.add(soaClassPathEntry3.getMemento());
		//	classpaths.add(soaClassPathEntry4.getMemento());
			classpaths.add(soaClassPathEntry5.getMemento());
			classpaths.add(soaClassPathEntry6.getMemento());
			//
			classpaths.add(systemLibsPathEntry.getMemento());
			 
			workingCopy.setAttribute(ATTR_CLASSPATH,classpaths);
			workingCopy.setAttribute(ATTR_DEFAULT_CLASSPATH,false);
			SasPlugin.getDefault().log("ASE_HOME=========>"+pathASEHOME);
			 
			IPath pathHTTPHOME = pathASEHOME.append("Common").append("thirdParty").append("jakarta-tomcat");
			SasPlugin.getDefault().log("HTTPHOME=========>"+pathHTTPHOME);
			
			IPath pathJACORBHOME = pathASEHOME.append("Common").append("thirdParty").append("TAO").append("JacOrb").append("JacORB");
			IPath pathLD_LIBRARY_PATH = pathASEHOME.append("lib").append("lib");
			 
			JavaCore.setClasspathVariable("JACORB_HOME",pathJACORBHOME,null);
			JavaCore.setClasspathVariable("HTTP_CONTAINER_HOME",pathHTTPHOME,null);
			JavaCore.setClasspathVariable("LD_LIBRARY_PATH", pathLD_LIBRARY_PATH,null);
			
			SasPlugin.getDefault().log(JavaCore.getClasspathVariable("HTTP_CONTAINER_HOME").toOSString());
			String aseOptions = "-Djava.security.auth.login.config="+pathASEHOME.toString()+"/conf/jaas.config -Dase.home="+pathASEHOME.toString()+" -DREXEC_LOG_FILE_NAME=ASE_"+System.currentTimeMillis()+".rexec_out"+" -Dhttp.container.home="+pathHTTPHOME.toString()+" -Djava.net.preferIPv4Stack=true";
			SasPlugin.getDefault().log("ASEOPTIONS=====>"+aseOptions);
			
			String TOMCAT_OPTS="-Dcatalina.home="+pathHTTPHOME.toString()+" -Dcatalina.base="+pathHTTPHOME.toString();
			SasPlugin.getDefault().log("TOMCAT OPTIONS =====>"+TOMCAT_OPTS);
				 
			String JACORB_OPTS = "-Djacorb.security.support_ssl=off -Djacorb.security.enforce_ssl=off -Djacorb.security.ssl.client.required_options=40 -Djacorb.security.ssl.client.supported_options=40 -DOAPort=5030";
			SasPlugin.getDefault().log("JACORB OPTIONS=====>"+JACORB_OPTS);
			
			//added by reeta
			String DEBUG_OPTS="";
		 	if(window != null && mode.equals(ILaunchManager.DEBUG_MODE)){
		 		DEBUG_OPTS=	"-Xrunjdwp:transport=dt_socket,address="
                + this.debugPort
                + ",server=y,suspend=n -Xdebug -Xnoagent -Djava.compiler=NONE";
			}
			SasPlugin.getDefault().log("DEBUG OPTIONS=====>"+DEBUG_OPTS);
			
			String arguments =DEBUG_OPTS+aseOptions+" "+TOMCAT_OPTS+" "+JACORB_OPTS;
			SasPlugin.getDefault().log("COMPLETE ARGUMENTS ==============>"+arguments);
			 
			workingCopy.setAttribute(ATTR_VM_ARGUMENTS,arguments);	 
			File workingDir = JavaCore.getClasspathVariable("ASE_HOME").append("bpjars").toFile();
				 
			workingCopy.setAttribute(ATTR_WORKING_DIRECTORY,workingDir.getAbsolutePath());
			
			ILaunchConfiguration configuration = workingCopy.doSave();
			ILaunch launch = configuration.launch(ILaunchManager.RUN_MODE, null, false);
			SASInstance.getInstance().setLaunch(launch);

			startJob();
			if (window != null && mode.equals(ILaunchManager.DEBUG_MODE)){
				SasPlugin.getDefault().log("Open Debug Prespective");
				window.getWorkbench().showPerspective("org.eclipse.debug.ui.DebugPerspective", window);
			}
		}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
			String[] tx = new String[]{"OK"};
			MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Startup", null, "Not able to start AGNITY CAS", MessageDialog.ERROR, tx,0);
			ms.open();
			return;
		}
	}

	private boolean validate(){
		//Check whether IDE is already running a CAS instance or attached to a CAS instance or starting a CAS instance 
		boolean running = SASInstance.getInstance().isRunning();
		boolean connected = SASInstance.getInstance().isConnected();
		if(running || connected){
			StatusASE statusASE = StatusASE.getInstance();
			String strMessage = "The IDE is already running or attached to a CAS instance. So cannot start a new CAS instance...";
			
			if(connected){
				strMessage = "Embedded CAS cannot be started as IDE is attached to CAS running at "+statusASE.getAddress();
			}else if (running){
				strMessage = "There is already an embedded CAS instance running. So cannot start one more instance";
			}
				
			String[] tx = new String[]{"OK"};
            MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Startup", null, strMessage, MessageDialog.ERROR, tx,0);
            ms.open();
            return false;
		}
		
		//Get the IP Address of the local host...
		String localHost = null;
		try{
			localHost = InetAddress.getLocalHost().getHostAddress().toString();
			SasPlugin.getDefault().log("LOCAL HOST ====>"+localHost);
		}catch(Exception e){
			
			SasPlugin.getDefault().log(e.getMessage(), e);
			String[] tx = new String[]{"OK"};
			MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Startup", null, "Not able to find the IP address of the system", MessageDialog.ERROR, tx,0);
			ms.open();
			return false;
		}
		
		//Check the CAS stared outside of this IDE....
		GetStatusSAS statusSAS =new GetStatusSAS();
		if(statusSAS.getStatus(localHost)){
			MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Startup", null, "There is already a CAS instance running outside this IDE. So not starting SAS", MessageDialog.ERROR, new String[]{"OK"},0);
			ms.open();
			return false;
		}
		
		//IPath pathASEHOME = JavaCore.getClasspathVariable("ASE_HOME");
		IPath pathASEHOME =getASEHOMEVar();
		if(pathASEHOME == null){
			MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Startup", null, "Not able to get the ASE_HOME class path or environment variable", MessageDialog.ERROR, new String[]{"OK"},0);
			ms.open();
			return false;
		}

		return true;
	}

	
	private void startJob()
	{
		String strMessage = "Starting Embedded CAS";
		String mode = this.getMode();
		if(mode != null && mode.equals(ILaunchManager.DEBUG_MODE)){
			strMessage = "Starting Embedded CAS in Debug Mode";
		}
		
		// Added by Neeraj Jadaun
		Job job = new Job(strMessage){
			protected IStatus run (IProgressMonitor monit){
				int sleepTime = SasPlugin.getSASStartupTime();
				try{
					Thread.sleep(sleepTime);
					GetStatusSAS statusSAS =new GetStatusSAS();
					StatusASE statusASE = StatusASE.getInstance();
					for(;SASInstance.getInstance().isRunning();){
						if(statusSAS.getStatus(LOCAL_HOST)){
							statusASE.setEmbeddedRunning(true);
							statusASE.setPORT(0);
							statusASE.setAttach(0);
							 startDeployedServices();
							break;
						}
						
						if(monit.isCanceled()){
							SASInstance.getInstance().stop();
							return Status.CANCEL_STATUS;
						}
					}
				}catch(Exception e){
					SasPlugin.getDefault().log(e.getMessage(), e);
				}
				
				showResults();
				return Status.OK_STATUS;
			}
		};
	

		job.setUser(true);
		job.schedule();
	}
	
	private void startDeployedServices(){
		port = SasPlugin.getPORT();
		JMXURL = SasPlugin.getJMXURL();
		SASServices services = SASServices.getInstance();
		services.setAddress(LOCAL_HOST);
		services.setAllServices();
		
		try {

			Hashtable ASEServices = services.getServices();
			Set serv = ASEServices.keySet();
			Iterator itr = serv.iterator();
			while (itr.hasNext()) {
				String serviceName = (String) itr.next();
				Hashtable entries = (Hashtable) ASEServices.get(serviceName);
				String state = (String) entries.get("STATUS");
				String Serviceinfo = (String)entries.get("INFO");
				
				 SasPlugin.getDefault().log("Service State ....for "+serviceName+" is " +state);
                  //if we want to active all the deployed services when the server starts then uncomment the below code
//				if(state.equals("INSTALLED")){
//				    startServiceAction(serviceName);
//				    state = "READY";
//				    SasPlugin.getDefault().log("Service is started....."+serviceName);
//				}
//				if(state.equals("READY")){
//					activateServiceAction(serviceName);
//					SasPlugin.getDefault().log("Service is activated....."+serviceName);
//		         }
				
				
				
			}
		}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
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

							//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it

							//					 Check if the JMXMP connector is available reeta adding it
							if (JMXURL == 1) {
								url = new JMXServiceURL("jmxmp", LOCAL_HOST, port);
								Class[] paramTypes = { JMXServiceURL.class };
								Constructor cons = jmxmpConnectorClass
										.getConstructor(paramTypes);

								Object[] args = { url };
								Object theObject = cons.newInstance(args);
								jmxc = (JMXConnector) theObject;
								jmxc.connect();
							} else {
								url = new JMXServiceURL(
										"service:jmx:rmi:///jndi/rmi://" + LOCAL_HOST
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
								SasPlugin.getDefault().log("Starting service "+ServiceName + "Failed");
							}

						} catch (Exception exe) {
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

							//reeta added following code
							if (JMXURL == 1) {
								url = new JMXServiceURL("jmxmp", LOCAL_HOST, port);
								Class[] paramTypes = { JMXServiceURL.class };
								Constructor cons = jmxmpConnectorClass
										.getConstructor(paramTypes);

								Object[] args = { url };
								Object theObject = cons.newInstance(args);
								jmxc = (JMXConnector) theObject;
								jmxc.connect();
							} else {
								url = new JMXServiceURL(
										"service:jmx:rmi:///jndi/rmi://" + LOCAL_HOST
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
								if (deploystatus.equals("false")) {
									SasPlugin.getDefault().log("Activating service "+ServiceName + "Failed");
								}

							}

						} catch (Exception exe) {
							SasPlugin.getDefault().log(exe.getMessage(), exe);
						} finally {
							if (jmxc != null)
								jmxc.close();
						}

				} catch (Exception e) {
					SasPlugin.getDefault().log(e.getMessage(), e);
				}

			}
			
			
			
			private IPath getASEHOMEVar(){
				
				 IPath pathASEHOME = JavaCore.getClasspathVariable("ASE_HOME");	
					
				 if(pathASEHOME==null){
					   
					    String envVariable=System.getenv("ASE_HOME");
						SasPlugin.getDefault().log("The ASE_HOME path..env variable is "+envVariable);
						
						if(envVariable!=null) {
						
							pathASEHOME=new Path(envVariable);
						
						try {
							SasPlugin.getDefault().log("The ASE_HOME Classpath Variable as it was not set "+pathASEHOME);
							JavaCore.setClasspathVariable("ASE_HOME",pathASEHOME,null);
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				 }		
					return pathASEHOME;
			}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
		monitor = new NullProgressMonitor();
	}
	
	public String getMode(){
		return ILaunchManager.RUN_MODE;
	}
	
	protected void showResults(){
		Display.getDefault().asyncExec(new Runnable(){
			public void run()
			{
				if(SASInstance.getInstance().isRunning()){
					String tx[] = new String[]{"OK"};
					MessageDialog ms = new MessageDialog(window.getShell(), "Embedded CAS Startup",null,"Embedded CAS Started Successfully", MessageDialog.INFORMATION, tx,0);
			 		ms.open();
				}else{
					String tx[] = new String[]{"OK"};
			 		MessageDialog ms = new MessageDialog(window.getShell(), "Embedded CAS Startup",null,"Embedded CAS Startup Failed...", MessageDialog.INFORMATION, tx,0);
			 		ms.open();
				}
			}
		});
	 }
}
