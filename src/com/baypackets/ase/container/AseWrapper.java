/*
 * AseWrapper.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.ase.container;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletContextEvent;
import javax.servlet.sip.SipServletListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSessionsUtil;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.deployer.DeployerImpl;
import com.baypackets.ase.latency.AseLatencyData;
//import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;
//import com.baypackets.ase.ra.diameter.gy.GyMessageHandler;
//import com.baypackets.ase.ra.diameter.gy.enums.CCRequestTypeEnum;
//import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
//import com.baypackets.ase.ra.diameter.rf.RfMessageHandler;
//import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.ra.diameter.ro.RoMessageHandler;
//import com.baypackets.ase.ra.diameter.ro.enums.CCRequestTypeEnum;
//import com.baypackets.ase.ra.diameter.sh.ShMessageHandler;
//import com.baypackets.ase.ra.diameter.sh.ShProfileUpdateRequest;
//import com.baypackets.ase.ra.diameter.sh.ShRequest;
//import com.baypackets.ase.ra.diameter.sh.ShSubscribeNotificationRequest;
//import com.baypackets.ase.ra.diameter.sh.ShUserDataRequest;
import com.baypackets.ase.ra.enumserver.message.EnumMessage;
import com.baypackets.ase.ra.enumserver.message.EnumMessageHandler;
import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.radius.RadiusAccessRequest;
import com.baypackets.ase.ra.radius.RadiusAccountingRequest;
import com.baypackets.ase.ra.radius.RadiusMessageHandler;
import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.sipconnector.AseSipDiagnosticsLogger;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.StringManager;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

/**
 * An instance of this class manages the lifecycle of a Servlet.
 * 
 * @see javax.servlet.Servlet
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class AseWrapper extends AseBaseContainer implements ServletConfig {
	private static final long serialVersionUID = -3814634264647849725L;
	private static Logger _logger = Logger.getLogger(AseWrapper.class);
	private static StringManager _strings = StringManager
	.getInstance(AseWrapper.class.getPackage());

	private Servlet _servlet;
	private MessageHandler messageHandler;
	private Integer _loadOnStartup;
	private Map _initParams = new ConcurrentHashMap();
	private boolean _isInitialized;

	private AseSipDiagnosticsLogger _diagLogger;
	private String _reqPrefix;
	private String _resPrefix;

	private boolean isServletResourcesInjected = false;

    /**
     * Default Constructor.
     *
     */
	public AseWrapper() {
		super();
	}

	/**
	 * Assigns the specified name to this AseWrapper instance.
	 * 
	 * @param name
	 *            The unique name to associate with this wrapper
	 * @throws IllegalArgumentException
	 *             if the given name is null
	 */
	public AseWrapper(String name) {
		super(name);
	}

	/**
	 * Sets this AseWrapper's parent.
	 * 
	 * @param parent
	 *            Must be an instance of AseContext
	 * @throws IllegalArgumentException
	 *             if the given argument is not an instance of AseContext.
	 * @see com.baypackets.ase.container.AseContext
	 */
	public void setParent(AseContainer parent) {
		if (!(parent instanceof AseContext || parent == null)) {
			throw new IllegalArgumentException(_strings
					.getString("AseWrapper.invalidParent"));
		}
		super.setParent(parent);
	}

	/**
	 * An AseWrapper object has no children, therefore, this method will throw
	 * an IllegalArgumentException.
	 */
	public void addChild(AseContainer child) {
		throw new IllegalArgumentException(_strings.getString(
				"AseWrapper.invalidChild", getName()));
	}

	/**
	 * Processes the given Servlet request and response objects.
	 * 
	 * @param request
	 *            An object encapsulating a Servlet request.
	 * @param response
	 *            An object encapsulating a Servlet response.
	 */
	public void processMessage(SasMessage message) throws ServletException,
	AseInvocationFailedException {
		try {
			
			if(_logger.isDebugEnabled()) {

				_logger.debug("processMessage");
				}
			if (message instanceof SipServletRequest) {
				this.invokeServlet((SipServletRequest) message, null);
			} else if (message instanceof SipServletResponse) {
				this.invokeServlet(null, (SipServletResponse) message);
			} 
//				else if(message instanceof ShUserDataRequest) {
//				if(_logger.isDebugEnabled()) {
//
//				_logger.debug("Passing UDR to applicaiton");
//				}
//				ShMessageHandler shHandler = (ShMessageHandler)messageHandler;
//				shHandler.doUDR((ShRequest)message);
//				return;
//			} else if(message instanceof ShProfileUpdateRequest) {
//				if(_logger.isDebugEnabled()) {
//
//				_logger.debug("Passing PUR to applicaiton");
//				}
//				ShMessageHandler shHandler = (ShMessageHandler)messageHandler;
//				shHandler.doPUR((ShRequest)message);
//				return;
//			} else if(message instanceof ShSubscribeNotificationRequest) {
//				if(_logger.isDebugEnabled()) {
//
//				_logger.debug("Passing SNR to applicaiton");
//				}
//				ShMessageHandler shHandler = (ShMessageHandler)messageHandler;
//				shHandler.doSNR((ShRequest)message);
//				return;
//			} 
             else if(message instanceof com.baypackets.ase.ra.diameter.ro.CreditControlRequest) {
				this.invokeRoMessageHandler((com.baypackets.ase.ra.diameter.ro.CreditControlRequest) message);
			}
//             else if(message instanceof CreditControlRequest) {
//				this.invokeGyMessageHandler((CreditControlRequest) message);
//			}
			else if(message instanceof RadiusRequest) {
				this.invokeRadiusMessageHandler((RadiusRequest) message);
			} else if (message instanceof EnumRequest) {
				if (_logger.isDebugEnabled()) {

					_logger.debug("Passing Enum Request to EnumMessageHandler applicaiton");
				}
				EnumMessageHandler enumHandler=(EnumMessageHandler)messageHandler;
				enumHandler.receiveEnumMessage((EnumMessage)message);
			}else if (message instanceof Message) {
				this.invokeMessageHandler((Message) message);
			}
		} catch (ServletException e1) {
			throw e1;
		} catch (Exception e2) {
			_logger.error(e2.toString(), e2);
			throw new AseInvocationFailedException(e2.toString());
		}
	}


	public void invokeRadiusMessageHandler(RadiusRequest message) {
		try
		{
			RadiusMessageHandler radiusHandler = (RadiusMessageHandler)messageHandler;
		
			if(message instanceof RadiusAccessRequest )
				radiusHandler.handleRadiusAccessRequest((RadiusAccessRequest) message);
			else if(message instanceof RadiusAccountingRequest )
				radiusHandler.handleRadiusAccountingRequest((RadiusAccountingRequest) message);
			else if (message instanceof Message) {
				this.invokeMessageHandler((Message) message);
			}
		}
		catch(Exception ex)
		{
			_logger.error("invokeRadiusMessageHandler() failed: Exception :",ex);
		} 
	}
//
//	public void invokeGyMessageHandler(CreditControlRequest message)
//	throws ServletException, IOException
//	{
//		try
//		{
//			GyMessageHandler gyHandler = (GyMessageHandler)messageHandler;
//			CCRequestTypeEnum recordType = message.getEnumCCRequestType();
//
//			switch (recordType){
//
//			case EVENT_REQUEST:
//				gyHandler.handleEventCCRRequest(message);
//				break;
//			case INITIAL_REQUEST:
//				gyHandler.handleInitialCCRRequest(message);
//				break;
//			case UPDATE_REQUEST:
//				gyHandler.handleInterimCCRRequest(message);
//				break;
//			case TERMINATION_REQUEST:
//				gyHandler.handleTerminationCCRRequest(message);
//			}
//
//		}
//		catch(Exception ex)
//		{
//			_logger.error("invokeGyMessageHandler() failed: Exception :",ex);
//		} 
//	}
//
//	public void invokeRfMessageHandler(RfAccountingRequest message)
//	throws ServletException, IOException
//	{
//		try
//		{
//			RfMessageHandler rfHandler = (RfMessageHandler)messageHandler;
//			AccountingRecordTypeEnum recordType = message.getEnumAccountingRecordType();
//
//			switch (recordType){
//
//			case EVENT_RECORD:
//				rfHandler.handleEventRecordRequest(message);
//				break;
//			case START_RECORD:
//				rfHandler.handleStartRecordRequest(message);
//				break;
//			case INTERIM_RECORD:
//				rfHandler.handleInterimRecordRequest(message);
//				break;
//			case STOP_RECORD:
//				rfHandler.handleStopRecordRequest(message);
//			}
//
//		}
//		catch(Exception ex)
//		{
//			_logger.error("handleIncomingUserDataRequest() failed: Exception :",ex);
//		} 
//	}

	public void invokeRoMessageHandler(com.baypackets.ase.ra.diameter.ro.CreditControlRequest message)
	throws ServletException, IOException
	{
		try
		{
			RoMessageHandler roHandler = (RoMessageHandler)messageHandler;
			int recordType = (int)message.getCCRequestType();

			switch (recordType){

			case com.baypackets.ase.ra.diameter.ro.utils.Constants.EVENT_REQUEST:
				roHandler.handleEventCCRRequest(message);
				break;
			case com.baypackets.ase.ra.diameter.ro.utils.Constants.INITIAL_REQUEST:
				roHandler.handleInitialCCRRequest(message);
				break;
			case com.baypackets.ase.ra.diameter.ro.utils.Constants.UPDATE_REQUEST:
				roHandler.handleInterimCCRRequest(message);
				break;
			case com.baypackets.ase.ra.diameter.ro.utils.Constants.TERMINATION_REQUEST:
				roHandler.handleTerminationCCRRequest(message);
			}

		}
		catch(Exception ex)
		{
			_logger.error("handleIncomingUserDataRequest() failed: Exception :",ex);
		} 
	}
	//    public void invokeShMessageHandler(ShMessage message)
	//        throws ServletException, IOException
	//    {
	//        try
	//        {
	//            _logger.debug("Inside invokeShMessageHandler");
	//            ShMessageHandler shHandler = (ShMessageHandler)messageHandler;
	//            if(message instanceof ShUserDataRequest)
	//            {
	//                _logger.debug("Passing UDR to applicaiton");
	//                shHandler.doUDR((ShRequest)message);
	//            } else if(message instanceof ShProfileUpdateRequest) {
	//                _logger.debug("Passing PUR to applicaiton");
	//                shHandler.doPUR((ShRequest)message);
	//            } else if(message instanceof ShSubscribeNotificationRequest) {
	//                _logger.debug("Passing SNR to applicaiton");
	//                shHandler.doSNR((ShRequest)message);
	//            }
	//        }
	//        catch(ResourceException e)
	//        {
	//            throw new ServletException(e.getMessage(), e);
	//        }
	//    }




	/**
	 * Dispatches the given request and response objects to the Servlet.
	 */
	public void invokeMessageHandler(Message message) throws ServletException,
	IOException {
		try {
			
			if(_logger.isDebugEnabled()) {

			_logger.debug("Inside invokeMessageHandler call handleMessage");
			}
			messageHandler.handleMessage(message);
			
			if(_logger.isDebugEnabled()) {

				_logger.debug("Inside invokeMessageHandler handleMessage called ");
			}
			
		} catch (ResourceException e) {
			throw new ServletException(e.getMessage(), e);
		}
	}


	/**
	 * Dispatches the given request and response objects to the Servlet.
	 */
	public void invokeServlet(ServletRequest request, ServletResponse response)
	throws ServletException, IOException {

		if (_servlet == null) {
			throw new ServletException("Servlet Object is NULL");
		}
		// Initialize the servlet if not already initialized...
		try {
			checkInitialized();
		} catch (ServletException e) {
			_logger.error(e.getMessage(), e);
			this.undeployApp();
		}

		final boolean isRequest = (request!=null);

		//JSR 289.42
		injectResourcesInServlet(request,response);
		
		//
		// Dump diagnostic logs if enabled
		//
		if (_diagLogger.isAppMsgLoggingEnabled()) {
			if (request != null && (request instanceof SipServletRequest)) {
				SipServletRequest ssreq = (SipServletRequest) request;
				if (_diagLogger.dumpRequest(ssreq.getMethod())) {
					_diagLogger.log(_reqPrefix + ssreq.getMethod() + ":"
							+ ssreq.getCallId() + ":"
							+ ssreq.getSession().getId());
				}
			}

			if (response != null && (response instanceof SipServletResponse)) {
				SipServletResponse ssres = (SipServletResponse) response;
				if (_diagLogger.dumpResponse(ssres.getMethod(), ssres
						.getStatus())) {
					_diagLogger.log(_resPrefix + ssres.getMethod() + "/"
							+ ssres.getStatus() + ":" + ssres.getCallId() + ":"
							+ ssres.getSession().getId());
				}
			}
		}

		if((isRequest?request:response) instanceof AseSipServletMessage ){
			AseLatencyData.noteLatencyData( (AseSipServletMessage) (isRequest?request:response),
					AseLatencyData.ComponentTimes.CONTAINER, false );
		}

		_servlet.service(request, response);

		if((isRequest?request:response) instanceof AseSipServletMessage ){
			AseLatencyData.noteLatencyData( (AseSipServletMessage) (isRequest?request:response),
					AseLatencyData.ComponentTimes.APPLICATION, false );
		}

	}

	
	/**
	 * overloaded inject resource to inject resources on activation.
	 * This will reuce allocation pressure on FT
	 */
	public void injectResourcesInServlet(){
		injectResourcesInServlet(null,null);
		
	}
		
	/**
	 * 
	 * synchronus wrapper with double check on  
	 * injectResourcesInServlet(Servlet servlet,ServletRequest request, ServletResponse response)
	 * @param request
	 * @param response
	 */
	private void injectResourcesInServlet(
			ServletRequest request, ServletResponse response) {
				
		if (!isServletResourcesInjected) {
			synchronized (this) {
				if (!isServletResourcesInjected) {
					injectResourcesInServlet(_servlet, request, response);
					isServletResourcesInjected = true;
				}//@end inner if
			}//@ned synchronized
		}//@end outer if
	}

	/**
	 * JSR 289.42
	 * This method injects resources in servlet to be invoked.
	 * Resources cab be SipFactory,SipSessionsUtil and timerService
	 * @param servlet
	 * @param request
	 * @param response
	 */
	private void injectResourcesInServlet(Servlet servlet,
			ServletRequest request, ServletResponse response) {

		Field[] fields = servlet.getClass().getDeclaredFields();
		if(_logger.isInfoEnabled()) {

		_logger.info("In method injectResourcesInServlet for servlet:"+servlet.getClass().getCanonicalName());
		}
		// for resource injection
		for (Field field : fields) {
			for (Annotation annotation : field.getAnnotations()) {
				if (annotation.annotationType().equals(Resource.class)) {
					try {
						if (field.getType().equals(SipFactory.class)) {

							if(_logger.isInfoEnabled()) {

							_logger.info("Resource annotation found on field:"+field.getName());
							}
							SipFactory sipFactory = (SipFactory) getServletContext()
							.getAttribute(
							"javax.servlet.sip.SipFactory");
							field.setAccessible(true);
							field.set(servlet, sipFactory);
							field.setAccessible(false);
							if(_logger.isInfoEnabled()) {

							_logger.info("Injecting SipFactory in field:"+field.getName());
							}

						} else if (field.getType().equals(TimerService.class)) {
							if(_logger.isInfoEnabled()) {

							_logger.info("Resource annotation found on field:"+field.getName());
							}
							field.setAccessible(true);
							TimerService t = (TimerService) getServletContext()
							.getAttribute(
							"javax.servlet.sip.TimerService");
							field.set(servlet, t);
							field.setAccessible(false);
							if(_logger.isInfoEnabled()) {

							_logger.info("Injecting TimerService in field:"+field.getName());
							}

						} else if (field.getType()
								.equals(SipSessionsUtil.class)) {
							if(_logger.isInfoEnabled()) {

							_logger.info("Resource annotation found on field:"+field.getName());
							}
							SipSessionsUtil s = (SipSessionsUtil) getServletContext()
							.getAttribute(
							"javax.servlet.sip.SipSessionsUtil");
							field.setAccessible(true);
							field.set(servlet, s);
							if(_logger.isInfoEnabled()) {

							_logger.info("Injecting SipSessionsUtil in field:"+field.getName());
							}
							field.setAccessible(false);

						} else {
							_logger.error("Resource annotation on field "
									+ field.getName() + "of class "
									+ servlet.getClass().getCanonicalName()
									+ "  is not valid");
						}
					} catch (IllegalArgumentException e) {
						_logger.error("IllegalArgument Exception in injectResourcesInServlet method ",e);
					} catch (IllegalAccessException e) {
						_logger.error("IllegalAccessException in injectResourcesInServlet method ",e);
					}
				}
			}

		}

	}

	/**
	 * Called by "invokeServlet" to initialize the Servlet if it has not yet
	 * been initialized.
	 */
	public void checkInitialized() throws ServletException {
		if (!_isInitialized) {
			try{
				_logger.error("context Not intialized yet wait for some time");
				Thread.sleep(200);
				}catch(Exception e){
					_logger.error(" exception cause while sleep");
				}
			synchronized(this){
		if (!_isInitialized) {
			initServlet();
				}//end inner if
			}//end synchronized
		}//end outer if
	}

	/**
	 * Returns the unique name of the Servlet that this AseWrapper object is
	 * encapsulating.
	 */
	public String getServletName() {
		return getName();
	}

	/**
	 * Returns the Servlet instance encapsulated by this AseWrapper.
	 */
	public Servlet getServlet() {
		return _servlet;
	}

	/**
	 * Sets the Servlet instance to be encapsulated by this AseWrapper.
	 */
	public void setServlet(Servlet servlet) {
		_servlet = servlet;
	}

	/**
	 * Adds an initialization parameter to this AseWrapper object that will be
	 * made available to the Servlet instance when it's "init()" method is
	 * invoked.
	 */
	public void addInitParam(String name, String value) {
		_initParams.put(name, value);
	}

	/**
	 * Returns an Integer value indicating the priority level in which to call
	 * the Servlet's "init()" method upon application startup. If this value is
	 * null, the Servlet will only have it's "init()" method invoked when it
	 * processes it's first request.
	 */
	public Integer getLoadOnStartup() {
		return _loadOnStartup;
	}

	/**
	 * Sets the priority level of the Servlet which determines when it's
	 * "init()" method is called upon application startup.
	 */
	public void setLoadOnStartup(Integer priority) {
		_loadOnStartup = priority;
	}

	/**
	 * Initializes the Servlet instance encapsulated by this AseWrapper object
	 * by invoking it's "init()" method.
	 */
	public void initServlet() throws ServletException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("initServlet() called...");
		}

		if (!_isInitialized) {
			if (_servlet != null) {
				_servlet.init(getServletConfig());
			}

			if (this.messageHandler != null) {
				this.messageHandler.init(this.getServletContext());
			}

			_diagLogger = AseSipDiagnosticsLogger.getInstance();
			_reqPrefix = new String("REQ2" + getServletName() + ":");
			_resPrefix = new String("RES2" + getServletName() + ":");

			_isInitialized = true;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug(_strings.getString("AseWrapper.initSuccessful",
					getName()));
		}
	}

	/**
	 * 
	 * @return
	 */
	public void destroyServlet() throws ServletException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("destroyServlet() called for :" + this.getName());
		}

		// Destroy the servlet if it is initialized
		if (this._isInitialized) {
			if (_servlet != null) {
				this._servlet.destroy();
			}
			if (this.messageHandler != null) {
				this.messageHandler.destroy();
			}
		} else {
			if (_logger.isDebugEnabled()) {
				_logger
				.debug("Servlet never initialized, so not calling destroy");
			}
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("destroyServlet() completed for :" + this.getName());
		}
	}

	public Object getWrappedObject() {
		if (this._servlet != null)
			return this._servlet;

		if (this.messageHandler != null)
			return this.messageHandler;

		return null;
	}

	/**
	 * Returns the ServletConfig object that is dispatched to the Servlet
	 * instance upon it's initialization. The implementation returned by this
	 * method is a facade for this AseWrapper object.
	 */
	public ServletConfig getServletConfig() {
		try {
			final AseWrapper wrapper = this;

			return (ServletConfig) Proxy.newProxyInstance(getClass()
					.getClassLoader(), new Class[] { ServletConfig.class },
					new InvocationHandler() {
				public Object invoke(Object proxy, Method method,
						Object[] args) throws Throwable {
					return method.invoke(wrapper, args);
				}
			});
		} catch (Exception e) {
			_logger.error(e.toString(), e);
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * Implemented from ServletConfig.
	 */
	public String getInitParameter(String name) {
		return (String) _initParams.get(name);
	}

	/**
	 * Implemented from ServletConfig.
	 */
	public java.util.Enumeration getInitParameterNames() {
		return Collections.enumeration(_initParams.keySet());
	}

	/**
	 * Implemented from ServletConfig.
	 */
	public javax.servlet.ServletContext getServletContext() {
		AseContainer parent = getParent();

		if (parent instanceof AseContext) {
			return ((AseContext) parent).getServletContext();
		}
		return null;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(MessageHandler listener) {
		this.messageHandler = listener;
		if(_logger.isDebugEnabled()) {

			_logger.debug("setMessageHandler .. "+listener);
			}
	}

	public boolean isInitialized() {
		return _isInitialized;
	}

	public void invokeSipServletListener() {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Invoking the SipServletListener ::"
					+ (_servlet == null ? "NULL" : getName())
					+ ":: initialized==" + _isInitialized);
		}

		if (_servlet == null || !(_servlet instanceof SipServlet)
				|| !_isInitialized)
			return;

		AseContainer parent = getParent();
		if (!(parent instanceof AseContext))
			return;

		AseContext context = (AseContext) parent;
		List listeners = context.getListeners(SipServletListener.class);
		if (listeners == null || listeners.isEmpty()) {
			if (_logger.isDebugEnabled()) {
				_logger
				.debug("No lifecycle listeners specified. So not invoking lifecycle listener");
			}
			return;
		}

		SipServletContextEvent event = new SipServletContextEvent(context
				.getServletContext(), (SipServlet) _servlet);
		for (Object listener : listeners) {
			try {
				((SipServletListener) listener).servletInitialized(event);
			} catch (Throwable t) {
				_logger.error(t.getMessage(), t);
			}
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("Completed the invocation of SipServletListener");
		}
	}

	protected void undeployApp() {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Undeploying the application on Servlet Failure");
		}
		try {
			AseContext context = (AseContext) getParent();
			context.setExpectedState(DeployableObject.STATE_READY);
			((DeployerImpl) context.getDeployer()).checkExpectedState(context
					.getId(), true);
			context.setState(DeployableObject.STATE_ERROR);
		} catch (Exception e) {
			_logger.error(e.getMessage(), e);
		}
	}
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
		if (_logger.isDebugEnabled()) {
			_logger.debug("Entering AseWrapper writeExternal()");
		}

		this._servlet = (Servlet) in.readObject();
		this._loadOnStartup = in.readInt();
		this._initParams = (ConcurrentHashMap) in.readObject();
		this._isInitialized = in.readBoolean();
		this._resPrefix = in.readUTF();
		this._reqPrefix = in.readUTF();
		this._diagLogger = AseSipDiagnosticsLogger.getInstance();
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("Leaving AseWrapper writeExternal()");
		}
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Entering AseWrapper writeExternal()");
		}

		out.writeObject(this._servlet);
		out.writeInt(this._loadOnStartup);
		out.writeObject(this._initParams);
		out.writeBoolean(this._isInitialized);
		out.writeUTF(this._resPrefix);
		out.writeUTF(this._reqPrefix);

		if (_logger.isDebugEnabled()) {
			_logger.debug("Leaving AseWrapper writeExternal()");
		}
    }
}
