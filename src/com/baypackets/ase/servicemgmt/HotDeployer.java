/*
 * Created on Oct 25, 2004
 *
 */
package com.baypackets.ase.servicemgmt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;


/**
 * <p> If hot deployment is enabled, the object of this class will monitor the apps
 * directory for any changes and perform auto-deploy as necessary. There are two
 * ways to enable/disable hot deployment, one is using EMS and another is using 
 * ase.properties. By default, hot deployment is enabled. 
 * </p>
 * 
 * <p> If auto-deploy is enabled, SAS will deploy and redeploy applications 
 * dynamically, including:
 * <ul>
 * <li>Deployment of SARs which are newly copied to the apps directory.</li>
 * <li>Redeployment of SARs which have been deployed previously and newly updated.</li>
 * </ul>
 * </p>
 * 
 * <p>Due to current apps directory structure and lack of application identification, 
 * SAS has the following limitations:
 * <ul>
 * <li>SAS assumes all the existing SARs in apps directory are already deployed. 
 * when the system starts up. If an existing SAR is never deployed before system 
 * starts, SAS cannot re-deploy it.</li>
 * <li>If an application is un-deployed using telnet, SAS will not re-deploy it.</li>
 * </ul>
 * </P>
 * 
 * <p> Hot Deployment Comparison Of SAS and Tomcat
 * <ul>
 * <li>Hot deploy enable/disable property: Both of SAS and Tomcat support. 
 * SAS also supports run time configuration of this property.</li>
 * <li>Deployment of application archives: Tomcat deploys WARs which are copied 
 * to host application doc base. SAS deploys both WARs and SARs which are copied 
 * to application doc base. Currently, SAS does not support multi-host.</li>
 * <li>Deployment of untared applications: Tomcat deploys untared applications 
 * which are copied to host doc base. SAS can support this feature if application 
 * working directories are separated for their doc base directories.</li>
 * <li>Redeployment of archived applications: Both of Tomcat and SAS re-deploy 
 * applications which have been deployed from their archives when the archives 
 * are updated.</li>
 * <li>Redeployment of untared applications: Tomcat re-deploys applications if 
 * the /WEB-INF/web.xml file is updated. SAS can support this feature if application 
 * working directories are separated from their doc base directories.</li>
 * <li>Undeployment of applications: Tomcat undeploys an application if its doc base 
 * is removed. SAS can support this feature if desired to. (Code is available.)</li>
 * <li>Other features that Tomcat supports but SAS not:</li>
 * <ul>
 * <li>Redeployment of applications if their context XML file is updated.</li>
 * <li>Redeployment of applications if their context XML file is copied to  
 * $CATALINA_HOME/conf/[enginename]/[hostname]/ foleder.</li>
 * </ul>
 * </ul>
 * </p>
 * @author Dana
 */
public class HotDeployer implements ThreadOwner {
	public static final int PRIORITY_NORMAL = 5;
        public static final String DEFAULT_VERSION = "1.0";
        
	private static Logger logger = Logger.getLogger(HotDeployer.class);
	private static final String SIP_XML = "WEB-INF" + File.separator + "sip.xml";
    private static final String WEB_XML = "WEB-INF" + File.separator + "web.xml";
    private static final String EXT_WAR =".war";
    private static final String EXT_SAR =".sar";
    private static final String EXT_JAR =".jar";
    private static final String EXT_ZIP =".zip";

	private ThreadMonitor threadMonitor = null;
	private boolean enabled = true;
	private Map dirMap; // Two dimensional Map. Host path is used as first dimension
	                    // key and file name is used as the second dimension key
	private long interval = 60000;
	private ArrayList deployers = new ArrayList();
	
	public void start() {			
		DeployerFactory factory = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
		this.deployers.add(factory.getDeployer(DeployableObject.TYPE_SAS_APPLICATION));
		this.deployers.add(factory.getDeployer(DeployableObject.TYPE_RESOURCE));
		
		dirMap = new HashMap(this.deployers.size());
		for (int i = 0; i < this.deployers.size(); i++) {
			Deployer deployer = (Deployer)this.deployers.get(i);
			File hostPath = deployer.getDeployDirectory();
			dirMap.put(hostPath, getDeployDescriptors(hostPath));
		}

		threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);

		new PollingThread(this).start();
	}
	
	// Create a deployment map for a directory
	private Map getDeployDescriptors(File path) {
		String[] fileNames = path.list();
		Map discriptors = new HashMap(fileNames.length);
		for (int i = 0; i < fileNames.length; i++) {
			// Currently, only .sar and .war can be hot deployed
			if (fileNames[i].endsWith(EXT_SAR) || 
					fileNames[i].endsWith(EXT_WAR) ||
					fileNames[i].endsWith(EXT_JAR)|| fileNames[i].endsWith(EXT_ZIP)) {
				File file = new File(path, fileNames[i]);
				if (file.exists() && file.isFile()) {
					
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("add DD for application found  " + fileNames[i]);
					}
					discriptors.put(fileNames[i], 
							new DeployDescriptor(fileNames[i], file.lastModified()));
				}
			}
			
			/* This directory structure is not currently supported
			if (fileNames[i].equals(".") || fileNames[i].equals("..") ||
					fileNames[i].equals("db") || fileNames[i].equals("archives")) {
				continue;
			}
			File file = new File(path, fileNames[i]);
			if (file.exists()) {
				discriptors.put(fileNames[i], new DeployDescriptor(path, fileNames[i]));
			}
			*/
		}
		return discriptors;
	}
	
	/**
	 * Update each host if there are any application deployment changes.
	 *
	 */
	private void update() {
		if (this.deployers == null || this.deployers.size() == 0) {
			logger.error("No Deployers found");
			return;
		}
		for (int i = 0; i < this.deployers.size(); i++) {
			Deployer deployer = (Deployer) this.deployers.get(i);
			updateDeployer(deployer);
		}
	}
	
	private void updateDeployer(Deployer deployer) {
		File hostPath = deployer.getDeployDirectory();
		Map currentHostMap = getDeployDescriptors(hostPath);
		Set current = currentHostMap.keySet();
		Map previousHostMap = (Map)dirMap.get(hostPath);
		Set previous = null;
		if (previousHostMap == null) {
			// A new host
			previousHostMap = new HashMap(0);
			//previous = new HashSet(0);
		} 
		else {
			previous = previousHostMap.keySet();
		}
		
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("Current Applictaion map " + current);
		}
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("Previous Applictaion map " + previous);
		}
				
		/* Not used for now
		// If an application is previously deployed but not found 
		// from current application list, it is undeployed.
		Set toBeUndeployed = new HashSet(previous);
		toBeUndeployed.removeAll(current);
		for (Iterator i = toBeUndeployed.iterator(); i.hasNext();) {
			String name = (String)i.next();
			String[] splitedName = null;
			try {
				splitedName = name.split("\\.");
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("HotDeployer is going to undeploy " + splitedName[0]);
				}
				host.stop(splitedName[0]);
				host.undeploy(splitedName[0]);
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info(splitedName[0] + " is undeployed");
				}
			} catch (ShutdownFailedException ex) {
				logger.error("Fail to stop " + splitedName[0]);
				continue;
			} catch (Exception ex) {
				logger.error("Fail to undeploy "+ splitedName[0]);
				logger.error(ex);
				continue;
			}
		}
		*/
		
		// If a name from the current application list finds no match from
		// previously deployed applications. The application with this name
		//is deployed.
		Set currentMap = new HashSet(current);
		
		Set toBeDeployed =new HashSet();
		Set updated= new HashSet();
		
		Iterator iterator=currentMap.iterator();
		
		while (iterator.hasNext()) {

			DeployableObject app = null;
			String appName = (String) iterator.next();
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("appName --> " + appName);
			}
			try {
				Iterator it = deployer.findByName(appName);
				app = it.hasNext() ? (DeployableObject) it.next() : null;
			} catch (Exception e) {
			}
				
			if (app != null) {
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("add application to tobe updated map " + appName);
				}
				updated.add(appName);
				continue;
			}else {
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("add application to toBeDeployed map "
							+ appName);
				}
			   toBeDeployed.add(appName);
			   continue;
			}
		}
		
		//toBeDeployed.removeAll(previous);
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("To be deployed host map " + toBeDeployed);
		}
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("To be updated host map " + updated);
		}
		for (Iterator i = toBeDeployed.iterator(); i.hasNext();) {
			String name = (String)i.next();
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("HotDeployer is going to deploy " + name);
			}
			
			String appName = null;
			File sarFile=new File(hostPath, name);
			URL url = null;
			try {
				url =sarFile.toURL();
			} catch (MalformedURLException ex) {
				logger.error("Fail to deploy " + name, ex);
				continue;
			}
			
			DeployableObject app = null;
			InputStream stream = null;
			try {
				stream = new BufferedInputStream(url.openStream());
				app = deployer.deploy(stream, Deployer.CLIENT_HOTDEPLOY);
				appName = app.getObjectName();
			} catch (DeploymentFailedException ex) {
				try {
					stream = new BufferedInputStream(url.openStream());
					appName = name.split("\\.")[0];
					app =deployer.deploy(appName, 
							DEFAULT_VERSION, 
							PRIORITY_NORMAL, 
			                null,
							stream, Deployer.CLIENT_HOTDEPLOY);
					appName = app.getObjectName();
				} catch (Exception e) {
					logger.error("Failed to deploy " + name, e);
					continue;					
				}
			} 
			catch( Exception ex )
			{
				logger.error(ex);
			}
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(appName + " is deployed");
			}
			
			try {
				deployer.start(app.getId());
				deployer.activate(app.getId());
			}catch (StartupFailedException ex) {
				logger.error("Fail to start " + appName + " : " + ex);
				continue;
			}catch (ActivationFailedException ex) {
				logger.error("Fail to activate " + appName + " : " + ex);
				continue;
			} finally{
				
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("Delete the sar file which has got deployed "+name);
				}
				sarFile.delete();
			}
		}
		
		// Update the remains
//		Set updated = new HashSet(current);
//		updated.removeAll(toBeDeployed);
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("Update Remaining " + updated);
		}
		
		for (Iterator i = updated.iterator(); i.hasNext();) {
			String name = (String)i.next();
			DeployDescriptor updateDesc = (DeployDescriptor)currentHostMap.get(name);
			DeployDescriptor preDesc = (DeployDescriptor)previousHostMap.get(name);
//			if (updateDesc == null || preDesc == null) {
//				// Can't update. This, however, shouldn't happen in normal case.
//				continue;
//			}
			if (updateDesc != null && preDesc != null
					&& updateDesc.getDate().after(preDesc.getDate())) {
				// Update is needed.				
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("HotDeployer is going to redeploy/upgrade " + name);
				}
				
				String appName = null;
				URL url = null;
				DeployableObject app = null;
				try {
					url = new File(hostPath, name).toURL();
				} catch (MalformedURLException ex) {
					logger.error("Fail to deploy " + name + " : " + ex);
					continue;
				}
				InputStream stream = null;	
				try {
					stream = new BufferedInputStream(url.openStream());
					app = deployer.upgrade(stream);
					appName = app.getObjectName();
				} catch (UpgradeFailedException ex) {
					try {
						stream = new BufferedInputStream(url.openStream());
						appName = name.split("\\.")[0];
						app = deployer.upgrade(appName, 
								DEFAULT_VERSION, 
								PRIORITY_NORMAL, 
								stream);
						appName = app.getObjectName();
					} catch (Exception e) {
						logger.error("Fail to redeploy " + name + " : " + e);
						continue;					
					}
				} 
				catch ( Exception ex)
				{
					logger.error(ex);
				}
				
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info(appName + " is redeployed");
				}
			}else{
				
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("check next application");
				}
				continue;
			}
		}
		
		// Replace previous DeployDescriptors with current
		dirMap.put(hostPath, currentHostMap);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info(displayDeployMap());
		}
	}
	
	public synchronized void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public synchronized void setInterval(long interval) {
		this.interval = interval*1000;
	}
    
    public String displayDeployMap() {
    	StringBuffer buf = new StringBuffer("SIP Application deployment map: \n");
		for (Iterator i = dirMap.entrySet().iterator(); i.hasNext();) {
			Map.Entry hostEntry = (Map.Entry)i.next();
			buf.append(((File)hostEntry.getKey()).getPath() + ": \n");
			Map files = (Map)hostEntry.getValue();
			for (Iterator j = files.entrySet().iterator(); j.hasNext();) {
				Map.Entry fileEntry = (Map.Entry)j.next();
				DeployDescriptor desc = (DeployDescriptor)fileEntry.getValue();
				buf.append(AseStrings.TAB + (String)fileEntry.getKey());
				buf.append(": " + desc.getDate() + AseStrings.NEWLINE);
			}
		}
    	return buf.toString();
    }
    
    /**
     * @author Dana
     *
     * The instance of this class stores file information necessary for 
     * hot deployment
     */
    private class DeployDescriptor {
    	private String name;
    	private Date date;
    	
    	// This constructor is not used for current directory structure
    	public DeployDescriptor(File path, String name) {
    		this.name = name;
    		
    		// If the file is a directory, last modification time of 
    		// sip.xml or web.xml is used
    		File file = new File(path, name);
 			if (!file.isFile()) {
				file = new File(path, name + File.separator + SIP_XML);
				if (!file.exists()) {
					file = new File(path, name + File.separator + WEB_XML);
				}
			}
	   		if (file.exists()) {
				date = new Date(file.lastModified()); 
	   		}
    	}
    	
    	public DeployDescriptor(String name, long timestamp) {
    		this.name = name;
    		this.date = new Date(timestamp);
    	}
    	
    	public String getName() {
    		return name;
    	}
    	
    	public Date getDate() {
    		return date;
    	}
    	
    	public boolean equals(Object obj) {
    		if (!(obj instanceof DeployDescriptor)) return false;
    		DeployDescriptor descriptor = (DeployDescriptor)obj;
    		if (name.equals(descriptor.getName()) && 
    				date.equals(descriptor.getDate())) {
    			return true;
    		}
    		return false;
    	}
    	
    	public int hashCode() {
    		String str= name + date;
    		return str.hashCode();
    	}
    }

	/**
	 * @return
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @return
	 */
	public long getInterval() {
		return interval;
	}

	// As ThreadOwner
	public int threadExpired(MonitoredThread thread) {
		logger.error(thread.getName() + " expired");

		// Print the stack trace
		StackDumpLogger.logStackTraces();

		return ThreadOwner.SYSTEM_RESTART; 
	}

	private class PollingThread extends MonitoredThread {
		ThreadOwner threadOwner = null;

		PollingThread(ThreadOwner thOwner) {
			super("HotDeployer", AseThreadMonitor.getThreadTimeoutTime(),
											BaseContext.getTraceService());
			threadOwner = thOwner;
		}

		public void run() {
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("HotDeployer started.");
				logger.info(displayDeployMap());
			}

			// Register thread with thread monitor
			try {
				// Set thread state to idle before registering
				this.setThreadState(MonitoredThreadState.Idle);

				threadMonitor.registerThread(this);
			} catch(ThreadAlreadyRegisteredException exp) {
				logger.error("This thread is already registered with Thread Monitor", exp);
			}

			try {
				while (enabled) {
					try {
						Thread.sleep(interval);

						this.updateTimeStamp();
						this.setThreadState(MonitoredThreadState.Running);
						update();
					} catch (InterruptedException e) {
						// TODO handle interrupt
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
					}

					this.setThreadState(MonitoredThreadState.Idle);
				}// while
			} finally {
				// Unregister thread with thread monitor
				try {
					threadMonitor.unregisterThread(this);
				} catch(ThreadNotRegisteredException exp) {
					logger.error("This thread is not registered with Thread Monitor", exp);
				}
			}
		}

		public ThreadOwner getThreadOwner() {
			return threadOwner;
		}
	} // PollingThread ends
}
