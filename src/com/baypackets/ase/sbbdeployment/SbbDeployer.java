package com.baypackets.ase.sbbdeployment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.Servlet;
import org.apache.log4j.Logger;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseWrapper;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.deployer.DeployerImpl;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.Constants;

public class SbbDeployer extends DeployerImpl {

	private static Logger m_logger = Logger.getLogger(SbbDeployer.class);
	private ArrayList appList = new ArrayList();

	public SbbDeployer() {
		super();
	}

	public AbstractDeployableObject createDeployableObject() {
		SbbContext context = new SbbContext();
		context.setType(this.getType());
		context.setObjectName(Constants.SBB_NAME);
		context.setDeploymentName(Constants.SBB_NAME);
		return context;
	}

	public String getDAOClassName() {
		return null;
	}

	public String[] getDDNames() {
		return null;
	}

	public File getDeployDirectory() {
		return null;
	}

	public short getType() {
		return DeployableObject.TYPE_SBB;
	}

	public synchronized DeployableObject upgrade(String appName, String version,
			int priority, InputStream stream) throws UpgradeFailedException {

		AbstractDeployableObject deployable = null;
		try {
			byte[] binary = this.getByteArray(stream);
			
			// upgrade the deployable object
			deployable = (AbstractDeployableObject) super.upgrade(appName, (new Date()).toString(), priority, new ByteArrayInputStream(binary));
			
			// save the latest class loader for SBB
			this.host.setLatestSbbCL(deployable.getClassLoader());
			
			// update all the registered applications
			Iterator itr = ((SbbDeployer) deployable.getDeployer()).getAllRegisteredApps();
			while(itr.hasNext()) {
				AseContext ctx = (AseContext) itr.next();
				ctx.removeChild(Constants.SBB_SERVLET_NAME);
				AseWrapper wrapper = new AseWrapper(Constants.SBB_SERVLET_NAME);
				ctx.addChild(wrapper);
				Class servletClazz = deployable.getClassLoader().loadClass(Constants.SBB_SERVLET_CLASS);
				if (!Servlet.class.isAssignableFrom(servletClazz)) {
					throw new InvalidClassException("Invalid Servlet Class : " + servletClazz.getName());
				}

				Servlet servlet = (Servlet) servletClazz.newInstance();
				wrapper.setServlet(servlet);                    
				wrapper.setLoadOnStartup(1);

				wrapper.initServlet();        		
			}

			// save the SBB JAR on setup
			File archive = new File(Constants.ASE_HOME.concat("/sbb/"), "sbb-impl.jar");
			OutputStream out = new BufferedOutputStream(new FileOutputStream(archive));
			InputStream in = new BufferedInputStream(new ByteArrayInputStream(binary));

			byte[] bytes = new byte[1000];
			int bytesRead = in.read(bytes, 0, bytes.length);

			while (bytesRead > 0) {
				out.write(bytes, 0, bytesRead);
				bytesRead = in.read(bytes, 0, bytes.length);
			}
			out.close();
		} catch(Exception ex) {
			m_logger.error("An exception occured while trying to upgrade SBB");
			throw new UpgradeFailedException(ex.getMessage(), ex);    		
		}
		return deployable;
	}

	/**
	 *	This method removes an <code>AseContext</code> from the list 
	 *	of AseContext which use this sbb. This method is called
	 *	when an application is deactivated.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public void unregisterApp(AseContext ctx){
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Inside unregisterApp with context "+ctx);
		}
		this.appList.remove(ctx);
	}

	/**
	 *	This method adds an <code>AseContext</code> to the list which 
	 *	uses this sbb. This method is called when an application
	 *	is activated.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public void registerApp(AseContext ctx){
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Inside registerApp() with context "+ctx);
		}
		this.appList.add(ctx);
	}

	/**
	 *	This method returns the Iterator over all of the 
	 *	<code>AseContext</code> which uses this sbb.
	 *
	 *	@param ctx-<code>AseContext</code> to be added to the list.
	 */
	public Iterator getAllRegisteredApps(){
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Inside getAllRegisteredApps()");
		}
		return this.appList.iterator();
	}
}
