/*
 * AppRuleSet.java
 *
 * Created on August 7, 2004, 4:22 PM
 */
package com.baypackets.ase.container;

import java.io.InvalidClassException;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.Servlet;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSetBase;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.security.ResourceCollection;
import com.baypackets.ase.security.SasSecurityManager;
import com.baypackets.ase.security.SecurityConstraint;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.deployer.AbstractDeployableObject;


/**
 * An instance of this class encapsulates the set of Rules that the Digester
 * utility will execute when parsing a Servlet application's "sip.xml" 
 * deployment descriptor file during application install.
 * 
 * @see org.apache.commons.digester.Digester
 * @see org.apache.commons.digester.Rule
 */
public class AppRuleSet extends RuleSetBase {
    
    private static Logger _logger = Logger.getLogger(AppRuleSet.class);
    private static StringManager _strings = StringManager.getInstance(AppRuleSet.class.getPackage());
    
    private AbstractDeployableObject _context;
    //private Map _listeners = new HashMap();
    //private Map _servletListeners = new HashMap();
    
    
    /**
     * @param context  The AseContext object to configure during parsing.
     */
    public AppRuleSet(DeployableObject context) {
        _context = (AbstractDeployableObject)context;
    }
     
    
    /**
     * Adds the parsing Rules to the given Digester instance.
     *
     * @param digester  A utility for parsing XML.
     */
    public void addRuleInstances(Digester digester) {
        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AppRuleSet.addRuleInstances", _context.getName()));
        }
        
        SetAttributeRule setAttributeRule = new SetAttributeRule(this._context);
        digester.addRule("sip-app/app-name", setAttributeRule);
      /**  digester.addRule("sip-app/main-servlet", setAttributeRule);
        digester.addRule("sip-app/servlet-selection/main-servlet", setAttributeRule);   **/   
        digester.addRule("sip-app/display-name", setAttributeRule);
        digester.addRule("sip-app/description", setAttributeRule);
        digester.addRule("sip-app/distributable", setAttributeRule);
        digester.addRule("sip-app/icon/large-icon", setAttributeRule);        
        digester.addRule("sip-app/icon/small-icon", setAttributeRule);
        digester.addRule("sip-app/context-param/param-name", setAttributeRule);
        digester.addRule("sip-app/context-param/param-value", setAttributeRule);
        digester.addRule("sip-app/proxy-config/sequential-search-timeout", setAttributeRule);
        digester.addRule("sip-app/session-config/session-timeout", setAttributeRule);
        
        
        ServletRule servletRule = new ServletRule(this._context); 
        digester.addRule("sip-app/servlet/servlet-name", servletRule);
        digester.addRule("sip-app/servlet/servlet-class", servletRule);
        digester.addRule("sip-app/servlet-selection/main-servlet", servletRule);      //Bug 6268
        digester.addRule("sip-app/servlet-mapping", servletRule);
        
        digester.addRule("sip-app/servlet/load-on-startup", servletRule);
        digester.addRule("sip-app/servlet/security-role-ref/role-name", servletRule);
        digester.addRule("sip-app/servlet/security-role-ref/role-link", servletRule);
        digester.addRule("sip-app/servlet/init-param/param-name", servletRule);
        digester.addRule("sip-app/servlet/init-param/param-value", servletRule);        
        
        ListenerRule listenerRule = new ListenerRule(this._context);
        digester.addRule("sip-app/listener/listener-class", listenerRule); 
        
        SecurityRule securityRule = new SecurityRule(this._context,false);
        securityRule.deployable = this._context;
        digester.addRule("sip-app/security-constraint", securityRule);
        digester.addRule("sip-app/security-constraint/proxy-authentication", securityRule);
        digester.addRule("sip-app/security-constraint/resource-collection", securityRule);        
        digester.addRule("sip-app/security-constraint/resource-collection/servlet-name", securityRule);
        digester.addRule("sip-app/security-constraint/resource-collection/sip-method", securityRule);        
        digester.addRule("sip-app/security-constraint/auth-constraint/role-name", securityRule);
        digester.addRule("sip-app/security-constraint/user-data-constraint/transport-guarantee", securityRule);       
        digester.addRule("sip-app/login-config/auth-method", securityRule);
        digester.addRule("sip-app/login-config/realm-name", securityRule);
        digester.addRule("sip-app/login-config/identity-assertion/identity-assertion-scheme", securityRule);
        digester.addRule("sip-app/login-config/identity-assertion/identity-assertion-support", securityRule);
        
		ResourceRefRule resourceRefRule = new ResourceRefRule(this._context); 
        digester.addRule("sip-app/resource-ref", resourceRefRule);
        digester.addRule("sip-app/resource-ref/res-ref-name", resourceRefRule);
        digester.addRule("sip-app/resource-ref/res-type", resourceRefRule);
        digester.addRule("sip-app/resource-ref/res-auth", resourceRefRule);
        digester.addRule("sip-app/resource-ref/resource-sharing-scope", resourceRefRule);
    }    
    
    
    /**
     * This Rule is executed to process all "servlet" elements defined in a 
     * Servlet application's deployment descriptor.
     */
    public static class ServletRule extends Rule {
        
        private AseWrapper wrapper;
        private String paramName;
        private String roleName;
        private AbstractDeployableObject deployable;
        
        
        public ServletRule(AbstractDeployableObject deployable){
        	this.deployable = deployable;
        }
        
        /**
         * @param nameSpace  The name space of the element encountered
         * @param tagName  The local name of the element encountered
         * @param body  The body content of the element encountered
         */
        public void body(String nameSpace, String name, String text) throws SAXException {
			String body = text.trim();            
            try {
				if(!(deployable instanceof AseContext)) {
					if (_logger.isDebugEnabled()) {

						_logger.debug("deployable is not instanceof AseContext: returning:");
					}
					return;
				}
				AseContext context = (AseContext)deployable;

			//	System.out.println("Reeta The name of the Element is .."+name +" NameSpace is "+nameSpace +" Text is: "+text);
				
                if (name.equals("servlet-name")) {
                    wrapper = new AseWrapper(body);
                    
                    // If this is the first Servlet in the descriptor, set
                    // it as the default handler for the application.
                    AseContainer[] children = context.findChildren();
                    if (children == null || children.length == 0) {
                        context.setDefaultHandlerName(body);
                    }
                    
                    context.addChild(wrapper);
                } else if (name.equals("servlet-class")) {
                    Class servletClass = Class.forName(body, true, context.getClassLoader());
                    
                    if (!Servlet.class.isAssignableFrom(servletClass)) {
                        throw new InvalidClassException(_strings.getString("AppRuleSet.invalidServletClass", servletClass.getName()));
                    }
                    
                    Iterator iterator = context.getListeners(servletClass).iterator();
                    Servlet servlet = iterator.hasNext() ? (Servlet) iterator.next() : null;                    
                    
                    if (servlet == null) {
                        servlet = (Servlet)servletClass.newInstance();                        
                    }
                    
                    wrapper.setServlet(servlet);
                } else if (name.equals("param-name")) {
                    paramName = body;
                } else if (name.equals("param-value")) {
                    wrapper.addInitParam(paramName, body);
                } else if (name.equals("role-name")) {
                    roleName = name;
                } else if (name.equals("role-link")) {
                    if (context.getSecurityManager() == null) {
                        context.setSecurityManager(new SasSecurityManager());
                    }
                    context.getSecurityManager().addRoleMapping(wrapper.getName(), roleName, body);
                } else if (name.equals("load-on-startup")) {
                    if (body == null || body.trim().equals("")) {
                        wrapper.setLoadOnStartup(new Integer(Integer.MIN_VALUE));
                    } else {
                        wrapper.setLoadOnStartup(new Integer(body));
                    }
                }else if (name.equals("main-servlet")){
                	context.setMainServlet(body);
                } else if(name.equals("servlet-mapping")) {
                	context.setServletMapPresent(true);
                }
            } catch (Exception e) {
                String msg = "Error occurred while processing the " + name + " element: " + e.toString();
                _logger.error(msg, e);
                throw new SAXException(msg);
            }
        }
    }
    
    
    /**
     * This Rule is executed to process the "display-name", "description", and
     * "context-param" elements defined in an application's deployment 
     * descriptor.
     */
    public static class SetAttributeRule extends Rule {
        
        private String paramName;
        private AbstractDeployableObject deployable;
        
        public SetAttributeRule(AbstractDeployableObject deployable){
        	this.deployable = deployable;
        }
        
        public void body(String nameSpace, String name, String text) {            
			String body = text.trim();
			if(! (deployable instanceof AseContext)) {
				if (_logger.isInfoEnabled()) {

					_logger.debug("deployable is not instanceof AseContext: returning:");
				}
				return;
			}
			AseContext context = (AseContext)deployable;
            if (name.equals("display-name")) {
                context.setDisplayName(body);
            } else if (name.equals("large-icon")) {
                context.setLargeIcon(body);
            } else if (name.equals("small-icon")) {
                context.setSmallIcon(body);
            } else if (name.equals("description")) {
                context.setDescription(body);
            } else if (name.equals("distributable")) {
                context.setDistributable(true);
            } else if (name.equals("sequential-search-timeout")) {
                context.setSequentialSearchTimeout(Integer.parseInt(body));
            } else if (name.equals("session-timeout")) {
                context.setAppSessionTimeout(Integer.parseInt(body));
            } else if (name.equals("param-name")) {
                paramName = body;                
            } else if (name.equals("param-value")) {
                context.setInitParam(paramName, body);
            }else if (name.equals("app-name")){
            	context.setObjectName(body);
            }
        }        
    }
   
    public static class ResourceRefRule extends Rule {
		
		private AbstractDeployableObject deployable; 
		private ResourceReference resRef;
		
		public ResourceRefRule(AbstractDeployableObject deployable) {
			this.deployable = deployable;
		}

		public void body(String nameSpace, String name, String text) {
			String body = text.trim();
			if(!(deployable instanceof AseContext)) {
				if (_logger.isDebugEnabled()) {

					_logger.debug("deployable is not instanceof AseContext: returning:");
				}
				return;
			}
			AseContext context = (AseContext)deployable;
			if (name.equals("res-ref-name")) {
				resRef = new ResourceReference();
				context.addResourceReferenceList(resRef);
				resRef.setResourceRefName(body);
			} else if (name.equals("res-type")) {
				resRef.setResourceType(body);
			} else if (name.equals("res-auth")) {
				resRef.setResourceAuth(body);
			} else if (name.equals("res-sharing-scope")) {
				resRef.setResourceSharingScope(body);
			}
		
		}
	}
    
    /**
     * This Rule is executed to process all "listener-class" elements defined 
     * in an application's deployment descriptor.
     */
    public static class ListenerRule extends Rule {
    	
    	AbstractDeployableObject context = null;
        
    	public ListenerRule(AbstractDeployableObject context){
        	this.context = context;
        }
    	
        public void body(String nameSpace, String name, String text) throws SAXException {
			String body = text.trim();            
            try {
				if(context instanceof AseContext) {
                	Class listenerClass = Class.forName(body, true, context.getClassLoader());
                	((AseContext)context).addListener(listenerClass);                
				}
            } catch (Exception e) {
                String msg = "Error occurred while processing the " + name + " element: " + e.toString();
                _logger.error(msg, e);
                throw new SAXException(msg);
            }
        }        
    }
    
    
    /**
     * This Rule is executed to process all security related elements defined
     * in an application's deployment descriptor.
     */
    public static class SecurityRule extends Rule {
        
    	private AbstractDeployableObject deployable;
        private SecurityConstraint constraint;

		private boolean sasLoginFlag=false;
       
        public SecurityRule(AbstractDeployableObject deployable,boolean flag){
        	this.deployable = deployable;
			this.sasLoginFlag=flag;
        }
        
        public void begin(String nameSpace, String name, Attributes attributes) throws SAXException {
            try {
				if(! (deployable instanceof AseContext)) {
					if (_logger.isDebugEnabled()) {
						_logger.debug("deployable is not instance of AseContext: returning");
					}
					return;
				}
				AseContext context = (AseContext)deployable;
                if (name.equals("security-constraint")) {
                    constraint = new SecurityConstraint();
                    constraint.setRoles(new HashSet());
                    ResourceCollection resources = new ResourceCollection();
                    resources.setMethods(new HashSet());
                    resources.setServletNames(new HashSet());
                    constraint.setResourceCollection(resources);
                    context.getSecurityManager().addConstraint(constraint);
                }
            } catch (Exception e) {
                String msg = "Error occurred while processing the " + name + " element: " + e.toString();
                _logger.error(msg, e);
                throw new SAXException(msg);                
            }
        }
        
        public void body(String nameSpace, String name, String text) throws SAXException {
			String body = text.trim();            
            try {
				if(! (deployable instanceof AseContext)) {
					if (_logger.isDebugEnabled()) {
						_logger.debug("deployable is not instance of AseContext: returning");
					}
					return;
				}
				AseContext context = (AseContext)deployable;
                if (name.equals("proxy-authentication")) {
                    constraint.setProxyAuth(true);
                } else if (name.equals("servlet-name")) {
                    constraint.getResourceCollection().getServletNames().add(body);
                } else if (name.equals("sip-method")) {
                    constraint.getResourceCollection().getMethods().add(body);                    
                } else if (name.equals("role-name")) {
                    constraint.getRoles().add(body);
                } else if (name.equals("transport-guarantee")) {
                    constraint.setTransport(body);
                } else if (name.equals("auth-method")) {
					//BpInd 17903
					if(context.getSecurityManager().getAuthMethod()==null)
						{
							if((sasLoginFlag==true && body.equals(context.getSecurityManager().ASSERTED_IDENTITY))||(sasLoginFlag==false && (body.equals(context.getSecurityManager().DIGEST) || body.equals(context.getSecurityManager().BASIC) || body.equals(context.getSecurityManager().CLIENT_CERT))))
                    		{
								if (_logger.isDebugEnabled()) {
									_logger.debug("body="+body+"flag="+sasLoginFlag);
								}
								context.getSecurityManager().setAuthMethod(body);
							}
							else
							{
								throw new SAXException(body+" auth method is not defined in this file");
							}
								
								
						}
					else
						throw new SAXException("More then one authorization method is specified");
                } else if (name.equals("realm-name")) {
                    context.getSecurityManager().setRealmName(body);
                } else if (name.equals("identity-assertion-scheme")) {
                	context.getSecurityManager().setIdAssertScheme(body);
                } else if (name.equals("identity-assertion-support")) {
                	context.getSecurityManager().setIdAssertSupport(body);
                }
            } catch (Exception e) {
                String msg = "Error occurred while processing the body content of the " + name + " element: " + e.toString();
                _logger.error(msg, e);
                throw new SAXException(msg);
            }
        }                
    }
    
}
