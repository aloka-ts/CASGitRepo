package com.genband.sip.ServiceDefinitionApp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionActivationListener;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionEvent;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.b2b.DialoutHandler;
import com.baypackets.ase.sbb.b2b.OneWayDialoutHandler;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.internalservices.TraceService;
import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceDefinition;
import com.genband.ase.alc.common.ALCBaseContext;
import com.genband.ase.alc.config.ALCConfigurator;
import com.genband.ase.alc.config.Constants;
import com.genband.ase.alc.sip.SipServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
@DefaultSerializer(ExternalizableSerializer.class)
public class ApplicationLogicControlSipService extends
		javax.servlet.sip.SipServlet implements javax.servlet.Servlet,
		SipApplicationSessionListener, Constants, SipApplicationSessionActivationListener {
	
	private static final long serialVersionUID = -4629937833502893124L;
	private static final String ALCServiceContext = SipServiceContextProvider.SERVICE_CONTEXT;

	private static final String ATT_CPA_CHECK = "ATT_CPA_CHECK";
	private static final String ATT_CANCEL_RECIEVED = "ATT_CANCEL_RECIEVED";
	private Logger logger = null;
	transient private static TraceService traceService;
	transient private final static String TRACE_LEVEL = "TRACE_LEVEL";
	private ServiceContext initContext = null;
	public static ServletContext servletContext = null;

	public void init() throws ServletException {
		logger = Logger.getLogger(getALCNameSpace());
		try {

			if (logger.isDebugEnabled())
				logger.debug("App loader is >>> "
						+ this.getServletContext().getAttribute(
								"CONTEXT_CLASS_LOADER"));

			servletContext = getServletContext();

			ALCBaseContext.setTraceService(BaseContext.getTraceService());

			ALCConfigurator configReps = new ALCConfigurator();
			ALCBaseContext.setConfigRepository(configReps);

			if (ServiceDefinition.getAlcMapping("__" + getALCNameSpace()) == null) {

				log("CreateALCMLDefinition as it may be SAS restart.\n SAS donot parse the ALCML Service on deploy as we can do it only by deploy-alcml command..");
				ServiceDefinition.CreateALCMLDefinition("__"
						+ getALCNameSpace(), getServiceURL(), false, true);

			} else {
				log("CreateALCMLDefinition bindings should be available here  ..");
			}

			ServiceDefinition initService = ServiceDefinition
					.getServiceDefinition("__" + getALCNameSpace(),
							"initialize");
			if (initService != null) {
				logger.log(Level.DEBUG,
						"Calling initialize service on namspace "
								+ getALCNameSpace());
				initContext = new ServiceContext();

				SipServiceContextProvider sscp = new SipServiceContextProvider(
						getServletContext(), initContext);
				initContext.addServiceContextProvider(sscp);

				initContext.setAttribute(SipServiceContextProvider.Context,
						getServletContext());
				initService.execute(initContext);
			} else {
				log("No initialize service in namspace ..");
			}

			String traceLevel = configReps.getValue(TRACE_LEVEL);

			int traceL = 0;

			if (traceLevel != null) {
				traceL = Integer.parseInt(traceLevel);

				// traceService.setTraceLevel(traceL);
				log("TRACE level is set to .." + traceL);

				// ALCBaseContext.setTraceService(traceService);
			}
			// hpahuja | SCE external configuration file changes | start
			String useConfigFile = configReps.getValue(USE_CONFIGURATION_FILE);
			// check flag if config file needs to be used then fetch the path
			// and set the
			// properties
			logger.debug("value of useConfigFile>>>>>  " + useConfigFile);
			if (useConfigFile != null && useConfigFile.equalsIgnoreCase("true")) {
				String ConfigFilePath = configReps
						.getValue(CONFIGURATION_FILE_PATH);
				if (ConfigFilePath != null) {

					logger.debug("loading app_configuration.properties ConfigFilePath >>>>"
							+ ConfigFilePath);
					Properties props = new Properties();

					try {
						props.load(new FileInputStream(ConfigFilePath));
					} catch (FileNotFoundException e) {
						logger.debug("The app_configuration.properties file not found");
						throw new ServletException(
								"The app_configuration.properties file not found");
					} catch (IOException e) {
						logger.debug("Unable to open app_configuration.properties file");
						throw new ServletException(
								"Unable to open app_configuration.properties file");
					}

					// if property file is to be used then set all the values
					// from file in service context and also in config rep
					logger.debug("Iterating app_configuration.properties file");
					for (String key : props.stringPropertyNames()) {
						String value = props.getProperty(key);
						if (null != value)
							value = value.trim();

						initContext.setAttribute(key, value);
						configReps.setValue(key, props.getProperty(key));
						logger.debug("setting "
								+ key
								+ " => "
								+ value
								+ " in context and config repo from properties file");
					}

				} else {
					logger.debug("The app_configuration.properties file PATH is not specified");
					throw new ServletException(
							"The app_configuration.properties file PATH is not specified");

				}

			}

			// hpahuja | SCE external configuration file changes | END
			String initDBAS = configReps.getValue(USE_DBACCESS_SERVICE);

			if (initDBAS != null && initDBAS.equalsIgnoreCase("true")) {
				// use db access service
				ALCBaseContext.setDbAccessService(BaseContext
						.getDbAccessService());
				logger.error("DBAccessService has been set to ..."
						+ BaseContext.getDbAccessService());
			}
			logger.error(" ApplicationLogicControlSipService has been intialized...");

			// ALCBaseContext.setDbAccessService(dbaccess);
			// }

		} catch (Exception e) {
			logger.log(Level.ERROR, "Service creation failure", e);
		}
	}

	public void destroy() {
		try {
			ServiceDefinition dtorService = ServiceDefinition
					.getServiceDefinition("__" + getALCNameSpace(), "destruct");
			if (dtorService != null) {
				logger.log(Level.DEBUG, "Calling destruct service on namspace "
						+ getALCNameSpace());
				ServiceContext sdContext = new ServiceContext();
				dtorService.execute(sdContext);
			}
			ServiceDefinition.removeNameSpace("__" + getALCNameSpace());
		} catch (Exception e) {
			logger.log(Level.ERROR, "Service creation failure", e);
		}
	}

	protected void doRequest(SipServletRequest req) {
		try {

				logger.log(Level.DEBUG, "doRequest() received request"+ req.getMethod());
			
			ServiceDefinition sd = ServiceDefinition.getServiceDefinition("__"
					+ getALCNameSpace(), "do-" + req.getMethod().toLowerCase());
			String attCPACheck = "false";
			if (req.getMethod().equals("INVITE")) {
				req.createResponse(100).send();
			}
			// THis change is done for CPA call flow for ATT project.
			// IN this on basis of CANCel recived we set a attribute in service
			// context to check which announcemnet need to play on this
			// atrribute basis
			// IN this case we will by pass the execution of service definition
			// by the do-cancel service because in SCE there is no switch back
			// to previous service.
			// IN CPX call flows if write do-cancel then service execution will
			// not come back on CPX call and call fails
			// So in CPX call flows do-cancel in SCE will not work because of
			// the check applied in service definition execution below to
			// prevent any executuion of do-cancel in this call scenario.

			if (req.getMethod().equals("CANCEL")) {
				ServiceContext sdContext = (ServiceContext) req
						.getApplicationSession()
						.getAttribute(ALCServiceContext);

				if (sdContext != null) {
					String cpacheck=((String) sdContext
							.getAttribute(ATT_CPA_CHECK));
					attCPACheck = cpacheck!=null?cpacheck.toLowerCase():null;
				}
				logger.log(Level.DEBUG,
						"Recievced CANCEL and setting atrribute ATT_CANCEL_RECIEVED:::"
								+ attCPACheck);
				if (attCPACheck != null && attCPACheck.equalsIgnoreCase("true")) {
					sdContext.setAttribute(ATT_CANCEL_RECIEVED, "true");
				} else if (attCPACheck == null) {
					attCPACheck = "false";
				}
				// 8721 bug
				// This has been done because in CPX call flows when B is
				// ringing and A CANCELS then we need to cancel the
				// ongoing dialout operation between B and IVR.
				String routeStatus = (String) sdContext
						.getAttribute("ROUTE_STATUS");
				if (logger.isDebugEnabled()) {
					logger.debug("[CALL-ID]"
							+ sdContext
									.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
							+ "[CALL-ID] "
							+ "Now checking whether cancel is recived before B party answered call or after");
					logger.debug("[CALL-ID]"
							+ sdContext
									.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
							+ "[CALL-ID] " + "Route Status is :::"
							+ routeStatus);
				}
				if (sd==null && routeStatus == null) {
					if (logger.isDebugEnabled()) {
						logger.debug("[CALL-ID]"
								+ sdContext
										.getAttribute(SipServiceContextProvider.ORIG_CALL_ID)
								+ "[CALL-ID] "
								+ "Executing  cancel dialout  in this case");
					}
					String sbbname = (String) sdContext
							.getAttribute(SBBOperationContext.ATTRIBUTE_SBB);
					SBBImpl sbb = (SBBImpl) sdContext.getAttribute(sbbname);
					// settinng this attribute in App session so to avoid
					// signalling to this party during cancel dialout operation.
					req.getApplicationSession().setAttribute(
							ATT_CANCEL_RECIEVED, "true");

					// cancelling dialout . it is equalivalent to
					// b2b.canceldialout or _groupedMsSessioncontroller
					// .canceldialout just to avoid
					// instance of check on sbb i have written following
					// directly cancel on sbb object
					sbb.cancel(OneWayDialoutHandler.class);
					sbb.cancel(DialoutHandler.class);
				}

			}

			if (sd == null) {
				logger.log(Level.DEBUG, "No service found for "
						+ getALCNameSpace() + "::" + req.getMethod());
				super.doRequest(req);
			} else {
				// to prevent execution of do-cancel if any in CPX call flows
				// only for ATT Govt Project.
				if (attCPACheck.equalsIgnoreCase("false")) {
					if (logger.isDebugEnabled()) {
						logger.debug("Excecuting do request method");
					}
					ServiceContext sdContext = (ServiceContext) req
							.getApplicationSession().getAttribute(
									ALCServiceContext);
					if (sdContext == null) {
						sdContext = new ServiceContext();
						req.getApplicationSession().setAttribute(
								ALCServiceContext, sdContext);
					}
					
					if(req.getMethod().equals("CANCEL")){
						sdContext.setAttribute(ATT_CANCEL_RECIEVED, "true");
					}

					logger.log(Level.DEBUG, "Received Request " + req);
					SipServiceContextProvider sscp = new SipServiceContextProvider(
							getServletContext(), req.getApplicationSession(),
							req, sdContext);
					sdContext.addServiceContextProvider(sscp);
					sd.execute(sdContext);
				}else{
					
					if (logger.isDebugEnabled()) {
						logger.debug("attCPACheck is not false!!!");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(
					Level.ERROR,
					"Exception while processing request for namespace "
							+ e.getMessage() + " " + getALCNameSpace() + "::"
							+ req.getMethod());
		}
	}

	protected void doProvisionalResponse(SipServletResponse res) {
		try {
			ServiceDefinition sd = ServiceDefinition.getServiceDefinition("__"
					+ getALCNameSpace(), "do-provisional-response");

			if (sd == null) {
				logger.log(Level.DEBUG, "No service found for "
						+ getALCNameSpace() + ":: do-provisional-response");
				super.doProvisionalResponse(res);
			} else {
				ServiceContext sdContext = (ServiceContext) res
						.getApplicationSession()
						.getAttribute(ALCServiceContext);
				if (sdContext == null) {
					sdContext = new ServiceContext();
					res.getApplicationSession().setAttribute(ALCServiceContext,
							sdContext);
				}
				SipServiceContextProvider sscp = new SipServiceContextProvider(
						getServletContext(), res.getApplicationSession(), res,
						sdContext);
				sdContext.addServiceContextProvider(sscp);
				sd.execute(sdContext);
			}
		} catch (Exception e) {
			logger.log(Level.ERROR,
					"Exception while processing response for namespace "
							+ getALCNameSpace() + ":: do-provisional-response");
		}
	}

	protected void doSuccessResponse(SipServletResponse res) {
		try {
			ServiceDefinition sd = ServiceDefinition.getServiceDefinition("__"
					+ getALCNameSpace(), "do-success-response");

			if (sd == null) {
				logger.log(Level.DEBUG, "No service found for "
						+ getALCNameSpace() + ":: do-success-response");
				super.doSuccessResponse(res);
			} else {
				ServiceContext sdContext = (ServiceContext) res
						.getApplicationSession()
						.getAttribute(ALCServiceContext);
				if (sdContext == null) {
					sdContext = new ServiceContext();
					res.getApplicationSession().setAttribute(ALCServiceContext,
							sdContext);
				}
				SipServiceContextProvider sscp = new SipServiceContextProvider(
						getServletContext(), res.getApplicationSession(), res,
						sdContext);
				sdContext.addServiceContextProvider(sscp);
				sd.execute(sdContext);
			}
		} catch (Exception e) {
			logger.log(Level.ERROR,
					"Exception while processing response for namespace "
							+ getALCNameSpace() + ":: do-success-response");
		}
	}

	protected void doRedirectResponse(SipServletResponse res) {
		try {
			ServiceDefinition sd = ServiceDefinition.getServiceDefinition("__"
					+ getALCNameSpace(), "do-redirect-response");

			if (sd == null) {
				logger.log(Level.DEBUG, "No service found for "
						+ getALCNameSpace() + ":: do-redirect-response");
				super.doRedirectResponse(res);
			} else {
				ServiceContext sdContext = (ServiceContext) res
						.getApplicationSession()
						.getAttribute(ALCServiceContext);
				if (sdContext == null) {
					sdContext = new ServiceContext();
					res.getApplicationSession().setAttribute(ALCServiceContext,
							sdContext);
				}
				SipServiceContextProvider sscp = new SipServiceContextProvider(
						getServletContext(), res.getApplicationSession(), res,
						sdContext);
				sdContext.addServiceContextProvider(sscp);
				sd.execute(sdContext);
			}
		} catch (Exception e) {
			logger.log(Level.ERROR,
					"Exception while processing response for namespace "
							+ getALCNameSpace() + ":: do-redirect-response");
		}
	}

	protected void doErrorResponse(SipServletResponse res) {
		try {
			ServiceDefinition sd = ServiceDefinition.getServiceDefinition("__"
					+ getALCNameSpace(), "do-error-response");

			if (sd == null) {
				logger.log(Level.DEBUG, "No service found for "
						+ getALCNameSpace() + ":: do-error-response");
				super.doErrorResponse(res);
			} else {
				ServiceContext sdContext = (ServiceContext) res
						.getApplicationSession()
						.getAttribute(ALCServiceContext);
				if (sdContext == null) {
					sdContext = new ServiceContext();
					res.getApplicationSession().setAttribute(ALCServiceContext,
							sdContext);
				}
				SipServiceContextProvider sscp = new SipServiceContextProvider(
						getServletContext(), res.getApplicationSession(), res,
						sdContext);
				sdContext.addServiceContextProvider(sscp);
				sd.execute(sdContext);
			}
		} catch (Exception e) {
			logger.log(Level.ERROR,
					"Exception while processing response for namespace "
							+ getALCNameSpace() + ":: do-error-response");
		}
	}

	public URL getServiceURL() {
		try {
			return new URL(getServletContext().getInitParameter("ServiceURL"));
		} catch (Exception e) {
			logger.log(Level.ERROR, "Exception while processing ServiceURL", e);
		}
		return null;
	}

	public String getALCNameSpace() {
		if (namespace == null)
			namespace = getServletContext().getInitParameter("ALCNameSpace");
		return namespace;
	}

	private String namespace = null;

	@Override
	public void sessionCreated(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub

		logger.log(Level.DEBUG,
				"ApplicationLogicControlSipService :sessionCreated  "
						+ getALCNameSpace() + "ID: "
						+ arg0.getApplicationSession().getId());

	}

	@Override
	public void sessionDestroyed(SipApplicationSessionEvent arg0) {
		logger.log(Level.DEBUG,
				"ApplicationLogicControlSipService :sessionDestroyed  "
						+ getALCNameSpace() + "ID: "
						+ arg0.getApplicationSession().getId());

	}

	@Override
	public void sessionExpired(SipApplicationSessionEvent arg0) {

		if (logger.isDebugEnabled()) {
			logger.log(Level.DEBUG,
					"ApplicationLogicControlSipService :sessionExpired for "
							+ getALCNameSpace());
		}

		if (logger.isDebugEnabled()) {
			logger.log(
					Level.DEBUG,
					"ApplicationLogicControlSipService :sessionExpired check if time need to be increased For"
							+ getALCNameSpace()
							+ " ID: "
							+ arg0.getApplicationSession().getId());
		}

		Iterator<SipSession> it = (Iterator<SipSession>) arg0.getApplicationSession().getSessions("SIP");
		boolean extendTime = false;

		while (it.hasNext()) {

			SipSession sipSession = (SipSession) it.next();
			
			SipSession.State state=sipSession.getState();
			
			if (logger.isDebugEnabled()) {
				logger.log(
						Level.DEBUG,
						"ApplicationLogicControlSipService :sessionExpired check sip session state "
								+ getALCNameSpace() + " Sip Session state  : "
								+ state+" SipSession ID :"+sipSession);
			}
			
			
			if((state==SipSession.State.CONFIRMED)||state==SipSession.State.EARLY){
				extendTime = true;
			}

		}

		if (extendTime) {
			if (logger.isDebugEnabled()) {
				logger.log(Level.DEBUG,
						"ApplicationLogicControlSipService :sessionExpired increase app session time  "
								+ getALCNameSpace() + " ID: "
								+ arg0.getApplicationSession().getId());
			}
			arg0.getApplicationSession().setExpires(5);
		} else {
			if (logger.isDebugEnabled()) {
				logger.log(
						Level.DEBUG,
						"ApplicationLogicControlSipService :sessionExpired donot increase app session time  "
								+ getALCNameSpace()
								+ " ID: "
								+ arg0.getApplicationSession().getId());
			}
		}

	}

	public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0) {
		logger.log(Level.DEBUG,
				"ApplicationLogicControlSipService :sessionReadyToInvalidate "
						+ getALCNameSpace() + "ID: "
						+ arg0.getApplicationSession().getId());

	}
	

	
	/**
	 * This method is called when a session gets action after FT
	 * @param appSessionEvent
	 */
	@Override
	public void sessionDidActivate(SipApplicationSessionEvent appSessionEvent) {
		
		if (logger.isDebugEnabled())
			logger.debug("<SBB> Entering sessionDidActivate for service context");

		SipApplicationSession appSession = appSessionEvent.getApplicationSession();
		
		if (logger.isDebugEnabled()){
			logger.debug("<SBB> session activation for sesion = "
					+ appSession.getId());
		}

		ServiceContext sContext = (ServiceContext) appSession
				.getAttribute(SipServiceContextProvider.SERVICE_CONTEXT);

		if (sContext != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("service context found in app session = ");
			}
			if (sContext.getAttribute(SipServiceContextProvider.Session) == null) {
				sContext.setAttribute(SipServiceContextProvider.Session,
						appSession);
			}

			if (sContext.getAttribute(SipServiceContextProvider.Context) == null) {
				sContext.setAttribute(SipServiceContextProvider.Context,
						getServletContext());
			}
		} else {

			if (logger.isDebugEnabled()) {
				logger.debug("service context not found in app session could not update ");
			}
		}

	}

	@Override
	public void sessionWillPassivate(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
