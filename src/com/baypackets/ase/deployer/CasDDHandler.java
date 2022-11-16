package com.baypackets.ase.deployer;

import java.io.InputStream;
import java.io.InvalidClassException;
import java.util.EventListener;
import java.util.Iterator;

import javax.servlet.Servlet;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AppRuleSet;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.AseWrapper;
import com.baypackets.ase.container.ResourceFactoryHolder;
import com.baypackets.ase.container.ResourceMappingRule;
import com.baypackets.ase.container.SipXmlEntityResolver;
import com.baypackets.ase.measurement.AseAppCounterManager;
import com.baypackets.ase.measurement.AseMeasurementManager;
import com.baypackets.ase.ocm.TimeMeasurementRule;
import com.baypackets.ase.replication.Policy;
import com.baypackets.ase.replication.PolicyManager;
import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.util.Constants;

public class CasDDHandler extends DefaultDDHandler {
	
	private static Logger logger = Logger.getLogger(CasDDHandler.class);

	public CasDDHandler() {
		super();
	}

	public void parse(DeploymentDescriptor dd, DeployableObject deployable) 
								throws Exception{
		logger.debug("CasDDHandler...parse");
		Digester digester = new Digester();
        //digester.setValidating(true);
        digester.setEntityResolver(new SipXmlEntityResolver());
        digester.setErrorHandler(new DefaultHandler() {
            public void error(SAXParseException e) throws SAXException {
                throw e;
            }
        });
        
        //Set the properties into the deployable Object 
        digester.push(deployable);
        if(deployable.getDeploymentName() == null) {
        	digester.addBeanPropertySetter("cas-app/name", "deploymentName");
        }
        // Bug 6815
        if(deployable.getObjectName() == null) { // <app-name> from sip.xml must have been already read
        	digester.addBeanPropertySetter("cas-app/name", "objectName");
        }
        digester.addBeanPropertySetter("cas-app/version", "version");
        digester.addBeanPropertySetter("cas-app/priority", "priority");
	    digester.addBeanPropertySetter("cas-app/main-servlet", "mainServlet");
        
        //Check for the SBB tag and process it.
        SbbRule sbbRule = new SbbRule(this.getDeployer() , deployable);
        sbbRule.deployer = this.getDeployer();
        digester.addRule("cas-app/sbb", sbbRule);

        //Check for the sysutil tag and process it.
        SysUtilRule sysUtilRule = new SysUtilRule(deployable);
        digester.addRule("cas-app/sysutil", sysUtilRule);
        
        //check for the process annotations tag
        AnnotationRule annotationRule=new AnnotationRule((AbstractDeployableObject)deployable);
        //JSR 289.42
        digester.addRule("cas-app/enable-annotation", annotationRule);
        digester.addRule("cas-app/enable-lib-annotation", annotationRule);
        
        //Check for the application specific measurement config tag
        AppMeasurementRule appMeasurementRule = new AppMeasurementRule((AbstractDeployableObject)deployable);
        digester.addRule("cas-app/measurement-config-file",appMeasurementRule);
        
        //Check for the listener classes.
        AppRuleSet.ListenerRule listenerRule = new AppRuleSet.ListenerRule((AbstractDeployableObject)deployable);
        digester.addRule("cas-app/listener/listener-class", listenerRule);
        
        //Check for the Message Handler definitions.
        MessageHandlerRule msgHandlerRule = new MessageHandlerRule((AbstractDeployableObject)deployable);
        digester.addRule("cas-app/message-handler/handler-name", msgHandlerRule);
        digester.addRule("cas-app/message-handler/handler-class", msgHandlerRule);

        //Add the Resource Factory mapping as attributes in the Servlet Context.
		/*
		digester.addCallMethod("cas-app/resource-factory-mapping", "setAttribute", 2);
        digester.addObjectCreate("cas-app/resource-factory-mapping", ResourceFactoryHolder.class.getName());
        digester.addBeanPropertySetter("cas-app/resource-factory-mapping/resource-name", "resourceName");
        digester.addCallParam("cas-app/resource-factory-mapping/factory-name", 0);
        digester.addCallParam("cas-app/resource-factory-mapping", 1, true);
		*/

		if(deployable instanceof AseContext){
			ResourceFactoryMappingRule rfRule = new ResourceFactoryMappingRule(
												(AseContext)deployable);
			digester.addRule("cas-app/resource-factory-mapping/factory-name",rfRule);
			digester.addRule("cas-app/resource-factory-mapping/resource-name",rfRule);
		}

        //Make the Message Handler mappings
        digester.addCallMethod("cas-app/message-handler-mapping", "addDefaultHandler", 2);
        digester.addCallParam("cas-app/message-handler-mapping/resource-name", 0);
        digester.addCallParam("cas-app/message-handler-mapping/handler-name", 1);
        digester.addObjectCreate("cas-app/message-handler-mapping", ResourceMappingRule.class.getName());
        digester.addBeanPropertySetter("cas-app/message-handler-mapping/resource-name", "resourceName");
        digester.addBeanPropertySetter("cas-app/message-handler-mapping/handler-name", "servletName");
        digester.addSetNext("cas-app/message-handler-mapping", "addTriggeringRule", "com.baypackets.ase.dispatcher.Rule");
        
        //Process the login-config tag.
		//BpInd 17903 
        AppRuleSet.SecurityRule securityRule = new AppRuleSet.SecurityRule((AbstractDeployableObject)deployable,false);
        digester.addRule("cas-app/login-config/auth-method", securityRule);
        digester.addRule("cas-app/login-config/realm-name", securityRule);
        
        //Process the replication-config tag
        ReplicationConfigRule replConfigRule = new ReplicationConfigRule((AbstractDeployableObject)deployable);
        digester.addRule("cas-app/replication-config/replication-policy", replConfigRule);
        digester.addBeanPropertySetter("cas-app/replication-config/replication-policy/event", "replicationEventId");
        digester.addRule("cas-app/replication-config/replication-policy/type", replConfigRule);
        
        //Process the response-time tag 
        digester.addObjectCreate("cas-app/response-time", TimeMeasurementRule.class.getName());
        digester.addSetProperty("cas-app/response-time", "target", "targetTime");
        digester.addSetProperty("cas-app/response-time", "weight", "weight");
        digester.addSetProperty("cas-app/response-time/start-time", "session-index", "beginSessionIndex");
        digester.addSetProperty("cas-app/response-time/start-time", "message-index", "beginMessageIndex");
        digester.addSetProperty("cas-app/response-time/end-time", "session-index", "endSessionIndex");
        digester.addSetProperty("cas-app/response-time/end-time", "message-index", "endMessageIndex");
        digester.addSetNext("cas-app/response-time", "addTimeMeasurementRule", TimeMeasurementRule.class.getName());

        digester.parse(new InputSource(dd.getStream()));
	}

	public static class SbbRule extends Rule{
		private Deployer deployer;
		private DeployableObject deployable;
		
		public SbbRule(Deployer deployer, DeployableObject deployable){
			this.deployer = deployer;
			this.deployable = deployable;
		} 
		
		public void begin(String nameSpace, String name, Attributes attributes) throws SAXException {
			try{
				if(deployer != null && deployer instanceof DeployerImpl){

					if(!(deployable instanceof AseContext)) {
						logger.debug("deployable is not instanceof AseContext: returning:"); // check if exception reqd
						return;
					}

					AseContext context = (AseContext)deployable;				
					AseWrapper wrapper = new AseWrapper(Constants.SBB_SERVLET_NAME);	
					context.addChild(wrapper);

					AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
					ClassLoader cl = host.getLatestSbbCL();
					Class servletClazz = cl.loadClass(Constants.SBB_SERVLET_CLASS);

					if (!Servlet.class.isAssignableFrom(servletClazz)) {
						throw new InvalidClassException("Invalid Servlet Class : " + servletClazz.getName());
					}

					Servlet servlet = (Servlet) servletClazz.newInstance();

					//Iterator iterator = context.getListeners(servletClazz).iterator();
					//Servlet servlet = iterator.hasNext() ? (Servlet) iterator.next() : null;                
					//if (servlet == null) {
					//servlet = (Servlet)servletClazz.newInstance();                        
					//}

					wrapper.setServlet(servlet);                
					wrapper.setLoadOnStartup(1);
					
					//context.addListener((EventListener)servletClazz.newInstance());
					context.addListener(servletClazz);
					
					Class listenerClazz = cl.loadClass(Constants.SBB_LISTENER_CLASS);
					//context.addListener((EventListener)listenerClazz.newInstance());
					context.addListener(listenerClazz);

					context.setUsesSBB(true);                
				}
			}catch(Exception e){
				throw new SAXException(e);
			}
		}
	}

	public static class SysUtilRule extends Rule{
		private DeployableObject deployable;
		
		public SysUtilRule(DeployableObject deployable){
			this.deployable = deployable;
		} 
		
		public void begin(String nameSpace, String name, Attributes attributes) throws SAXException {
			try{
					if(!(deployable instanceof AseContext)) {
						logger.debug("deployable is not instanceof AseContext: returning:"); 
						return;
					}

					AseContext context = (AseContext)deployable;				
					context.setSysUtil(true);                
			}catch(Exception e){
				throw new SAXException(e);
			}
		}
	}
	
	public static class ReplicationConfigRule extends Rule {
        
		AbstractDeployableObject context;
		PolicyManager manager = (PolicyManager)Registry.lookup(Constants.NAME_POLICY_MANAGER);
		
		public ReplicationConfigRule(AbstractDeployableObject context){
			this.context = context;
        }
        
		public void begin(String nameSpace, String name, Attributes attributes) throws SAXException {
            try {
                if (name.equals("replication-policy")) {
                	Policy policy = new Policy();
                	policy.setCreatorId(context.getId());
                	policy.setType(Policy.APPLICATION_POLICY);
                	digester.push(policy);
                }
            } catch (Exception e) {
                String msg = "Error occurred while processing the " + name + " element: " + e.toString();
                logger.error(msg, e);
                throw new SAXException(msg);                
            }
        }
        
        public void body(String nameSpace, String name, String body) throws SAXException {
            try {
                if (name.equals("replication-type")) {
                	Policy policy = (Policy)digester.peek();
                	short replType = manager.getReplicationType(body);
                	policy.setReplicationType(replType);
                } 
            } catch (Exception e) {
                String msg = "Error occurred while processing the body content of the " + name + " element: " + e.toString();
                logger.error(msg, e);
                throw new SAXException(msg);
            }
        }

		public void end(String namespace, String name) throws Exception {
			if (name.equals("replication-policy")) {
				Policy policy = (Policy)digester.pop();
				manager.addPolicy(policy);
			}
		}
    }
	
	public static class MessageHandlerRule extends Rule {
        
        private AseWrapper wrapper;
        private AbstractDeployableObject deployable;
        
        
        public MessageHandlerRule(AbstractDeployableObject deployable){
        	this.deployable = deployable;
        }
        
        /**
         * @param nameSpace  The name space of the element encountered
         * @param tagName  The local name of the element encountered
         * @param body  The body content of the element encountered
         */
        public void body(String nameSpace, String name, String body) throws SAXException {            
            try {
				if(! (deployable instanceof AseContext)) {
					logger.debug("deployable is not AseContext: returning:");
					return;
				}
				AseContext	context = (AseContext)deployable;	
                if (name.equals("handler-name")) {
                    wrapper = new AseWrapper(body);
                    context.addChild(wrapper);
                } else if (name.equals("handler-class")) {
                    Class servletClass = Class.forName(body, true, deployable.getClassLoader());
                    
                    if (!MessageHandler.class.isAssignableFrom(servletClass)) {
                        throw new InvalidClassException("The Class ", servletClass.getName() + " Should implement interface :" + MessageHandler.class.getName());
                    }
                    
                    Iterator iterator = context.getListeners(servletClass).iterator();
                    MessageHandler handler = iterator.hasNext() ? (MessageHandler) iterator.next() : null;                    
                    
                    if (handler == null) {
                    	handler = (MessageHandler)servletClass.newInstance();                        
                    }
                    
                    wrapper.setMessageHandler(handler);
                }
            } catch (Exception e) {
                String msg = "Error occurred while processing the " + name + " element: " + e.toString();
                logger.error(msg, e);
                throw new SAXException(msg);
            }
        }
    }
	
	/**
	 * This class parses the process-annotation rule and sets the value in context
	 * JSR 289.42
	 * @author averma
	 *
	 */
	public static class AnnotationRule extends Rule{
	        
	        private AbstractDeployableObject deployable;
	        
	        
	        
	        public AnnotationRule(AbstractDeployableObject deployable){
	        	this.deployable = deployable;
	        }

		/**
		 * @param nameSpace
		 *            The name space of the element encountered
		 * @param tagName
		 *            The local name of the element encountered
		 * @param body
		 *            The body content of the element encountered
		 */
		public void body(String nameSpace, String name, String body)
				throws SAXException {
			try {

				AseContext context = (AseContext) deployable;
				// default value
				if (name.equals("enable-annotation")) {
					context.setEnableAnnotation(Boolean.parseBoolean(body));

					logger.debug("Value in EnableAnnotation tag is:"
							+ context.isEnableAnnotation());
				}else if (name.equals("enable-lib-annotation")) {
					context.setEnableLibAnnotation(Boolean.parseBoolean(body));

					logger.debug("Value in EnablelibAnnotation tag is:"
							+ context.isEnableAnnotation());
				}
			} catch (Exception e) {
				String msg = "Error occurred while processing the " + name
						+ " element: " + e.toString();
				logger.error(msg, e);
				throw new SAXException(msg);
			}
		}

	}
	
	/**
	 * This class creates a rule for the <measurement-config> tag
	 * specified in sas.xml 
	 * This indicates an application has defined its own measurement
	 * counters.
	 * Name of the service and the path of the measuremet-config file
	 * is getting stored a list of services.
	 */
	public static class AppMeasurementRule extends Rule{

		private AbstractDeployableObject deployable;
       
		public AppMeasurementRule(AbstractDeployableObject deployable){
        	this.deployable = deployable;
        }

		public void body(String nameSpace, String name, String body)
		throws SAXException {
			try {
				if(name.equals("measurement-config-file")) {
					AseMeasurementManager.instance().addServiceName(deployable.getId(), body, AseMeasurementManager.STATUS_INACTIVE);
				}
				
				AseContext context = (AseContext) deployable;
				
				/*
				 * Instance of AseAppCounterManager and the deploy name of
				 * the service is stored as an attribute in Asecontext so that
				 * application is able to retreive them through ServletContext
				 */
				//context.setAttribute(AseAppCounterManager.INSTANCE, AseAppCounterManager.instance());
				//This is done to avoid the coupling of having servlet context object in the service.
				//Service doesn't want to pass servlet context object while playing with app counters
				context.setAttribute(AseAppCounterManager.INSTANCE, new AseAppCounterManager(context));
				context.setAttribute(AseAppCounterManager.DEPLOY_NAME, deployable.getId());
			} catch (Exception e) {
				String msg = "Error occurred while processing the " + name
				+ " element: " + e.toString();
				logger.error(msg, e);
				throw new SAXException(msg);
			}
		}
	}
	
	public static class ResourceFactoryMappingRule extends Rule {
		 private AseContext context;
		 private ResourceFactoryHolder rfHolder;
		 private String factoryName;
		 private String resourceName;
	        
	        
        public ResourceFactoryMappingRule(AseContext context){
        	this.context = context;
        }
	        
        /**
         * @param nameSpace  The name space of the element encountered
         * @param tagName  The local name of the element encountered
         * @param body  The body content of the element encountered
         */
        public void body(String nameSpace, String name, String text) throws SAXException {            
			String body=text.trim();
            try {
                if (name.equals("factory-name")) {
            		this.rfHolder = new ResourceFactoryHolder();   
					this.factoryName = body;
                }else if(name.equals("resource-name")) {
					this.resourceName = body;
            		this.rfHolder = new ResourceFactoryHolder();   
					rfHolder.setResourceName(resourceName);
					context.setAttribute(factoryName,rfHolder);
					context.addResourceName(resourceName);
				}
            } catch (Exception e) {
                String msg = "Error occurred while processing the " + name + " element: " + e.toString();
                logger.error(msg, e);
                throw new SAXException(msg);
            }
        }
    }
}
