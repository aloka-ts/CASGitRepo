/*------------------------------------------
 * SIP Dispatcher implementation
 * Nasir
 * Version 1.0   08/19/04
 * BayPackets Inc.
 * Revisions:
 * BugID : Date : Info
 *------------------------------------------*/

package com.baypackets.ase.dispatcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.sip.SipURI;
import javax.servlet.sip.ar.SipApplicationRouter;
import javax.servlet.sip.ar.SipTargetedRequestInfo;
import javax.servlet.sip.ar.SipTargetedRequestType;
import javax.servlet.sip.ar.SipApplicationRouterInfo;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.router.AseSipApplicationRouterManager;
import com.baypackets.ase.sipconnector.AseAddressImpl;
import com.baypackets.ase.sipconnector.AseSipConnector;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.sipconnector.AseSipURIImpl;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddressHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsUtil.DsException;


public class DispatcherImpl implements Dispatcher {

	public static RulesRepository rulesRepository;
	private static boolean initialized = false;
	private static Logger _logger = Logger.getLogger(DispatcherImpl.class);
	private static StringManager _strings = StringManager
			.getInstance(DispatcherImpl.class.getPackage());

	public DispatcherImpl() {
		if (_logger.isEnabledFor(Level.INFO)) {
			_logger.info("Entering the constructor of SIP dispatcher");
		}
		if (!initialized) {
			rulesRepository = ((RulesRepository) (Registry
					.lookup(Constants.RULES_REPOSITORY)));

			if (AseSipApplicationRouterManager.getSysAppRouter() != null) {
				AseSipApplicationRouterManager.getSysAppRouter().init();

			}

			initialized = true;
			if (_logger.isEnabledFor(Level.INFO)) {
				_logger.info("Initializing the SIP dispatcher");
			}
		}
	}

	public Destination getDestination(SasMessage request,
			Destination destination, AseHost host) {

		if (request instanceof AseSipServletRequest) {
			return getDestinationFromAppRouter(request, destination, host);
		}
		return null;
	}

	public Destination getDestinationFromRules(SasMessage request,
			Destination destination) {
		ArrayList exclusionRules = null; /* the entire rules matched so far */
		ArrayList exclusionComposite = null; /*
											 * rules matched for this
											 * destn,invocationID
											 */
		boolean freshDestination = (exclusionRules = destination
				.getAccumulatedRuleComposites()).size() == 0;

		if (_logger.isEnabledFor(Level.INFO)) {
			_logger.info("getDestination(): The destination object is new ? "
					+ freshDestination);
		}

		if (!freshDestination) {
			if (_logger.isEnabledFor(Level.INFO)) {
				_logger
						.info("getDestination(): Destination object is recycled");
			}

			exclusionComposite = new ArrayList();
			Object o = null;
			for (int p = 0; p < exclusionRules.size(); p++) {
				if (((RuleDataComposite) (o = exclusionRules.get(p)))
						.getInvocationId() == destination.getInvocationId()) {
					exclusionComposite.add(o);
					if (_logger.isEnabledFor(Level.INFO)) {
						_logger
								.info("getDestination(): Previous rule belongs to this invocation Id so adding to list "
										+ ((RuleDataComposite) o)
										+ " invocation Id="
										+ destination.getInvocationId());
					}
				}
			}

			if (_logger.isEnabledFor(Level.INFO)) {
				_logger.info("getDestination(): Exclusion composite size is "
						+ exclusionComposite.size());
			}
		}
		RuleDataComposite rule = rulesRepository.findMatchingRule(request,
				exclusionComposite, null);

		if (rule == null) {
			if (freshDestination) {
				if (_logger.isEnabledFor(Level.INFO)) {
					_logger
							.info("getDestination(): It was a blank destination and no destination was found");
				}
				destination.setValid(false);
				destination.setStatus(Dispatcher.NO_DESTINATION_FOUND);
				return destination;
			} else {
				if (_logger.isEnabledFor(Level.INFO)) {
					_logger
							.info("getDestination(): It was a reused destination and no further destination was found");
				}

				destination.setStatus(Dispatcher.PROCESSING_OVER);
				return destination;
			}
		} else {
			if (freshDestination) {
				if (_logger.isEnabledFor(Level.INFO)) {
					_logger.info("getDestination(): Found first destination.");
				}
				destination.setValid(true);
				destination.setStatus(Dispatcher.DESTINATION_FOUND);
				destination.addRuleDataComposite(rule);
				destination.setServletName((rule.getRule()).getServletName());
				destination.setAppName((rule.getRule()).getAppName());
				return destination;
			} else {
				for (int i = 0; i < exclusionRules.size(); i++) {
					if ((((RuleDataComposite) (exclusionRules.get(i)))
							.getInvocationId() != destination.getInvocationId())
							&& (rule.equals((RuleDataComposite) exclusionRules
									.get(i)))) {
						destination.setValid(false);
						destination.setStatus(Dispatcher.LOOP_DETECTED);
						if (_logger.isEnabledFor(Level.INFO)) {
							_logger.info("getDestination(): Loop detected");
						}
						return destination;
					}
				}
				destination.setValid(true);
				destination.addRuleDataComposite(rule);
				destination.setStatus(Dispatcher.DESTINATION_FOUND);
				destination.setServletName((rule.getRule()).getServletName());
				destination.setAppName((rule.getRule()).getAppName());
				if (_logger.isEnabledFor(Level.INFO)) {
					_logger.info("getDestination():Found further destination.");
				}
				return destination;
			}
		}
	}

	public Destination getDestinationFromAppRouter(SasMessage request,
			Destination destination, AseHost host) {
		boolean freshDestination = true;
		AseSipServletRequest req = (AseSipServletRequest) request;
		SipApplicationRoutingDirective dir = req.getRoutingDirective();
		SipApplicationRoutingRegion region;
		Serializable stateInfo;
		SipTargetedRequestInfo targetedInfo = null; //JSR289.36
		if (dir == SipApplicationRoutingDirective.NEW) {
			stateInfo = null;
			region = SipApplicationRoutingRegion.ORIGINATING_REGION;
		} else {
			AseSipSession sess = (AseSipSession) req.getSession();
			stateInfo = sess.getRouterStateInfo();
			region = sess.getRegion();
		}

		if (stateInfo != null) {
			freshDestination = false;
		}

		//JSR289.36
		if(req.isTargeted() && destination.getApplicationName() != null) {
			if (_logger.isEnabledFor(Level.INFO))
				_logger.info("Application Name already provided. SipTargetedRequestInfo to be passed to App router.");			
			targetedInfo = new SipTargetedRequestInfo(SipTargetedRequestType.ENCODED_URI, destination.getApplicationName());
		}

		String lastAppName = "";
		while (true) {
			SipApplicationRouterInfo ri = null;
			SipApplicationRouter ar = AseSipApplicationRouterManager
					.getSysAppRouter();
			if (ar != null) {
				ri = ar.getNextApplication(req, region, dir, targetedInfo, stateInfo);
			}

			// Null return means request is unhandled
			if (ri == null) {
				if (_logger.isEnabledFor(Level.INFO)) {
					_logger
							.info("getDestinationFromAppRouter(): App router returned null, setting it for External Route");
				}
				// return null ;//getDestinationFromRules(request, destination);
				// reeta

				destination.setStatus(Dispatcher.EXTERNAL_ROUTE);
				return destination;
			} else {

				switch (ri.getRouteModifier()) {
				case ROUTE:
					if ((ri.getRoutes() != null)
							&& (ri.getRoutes().length != 0)) {
						try {
							DsURI dsUri = DsURI
									.constructFrom(ri.getRoutes()[0]);
							if (dsUri instanceof DsSipURL) {
								String uriHost = ((DsSipURL) dsUri).getHost()
										.toString();
								if (AseSipConnector.isMatchingAddress(uriHost)) {
									// If local route than set it into popped
									// route header.
									DsSipNameAddressHeader dsNAHeader = new DsSipContactHeader(
											dsUri);
									req.setPoppedRoute(new AseAddressImpl(
											dsNAHeader));

								} else {
									if (_logger.isEnabledFor(Level.INFO)) {
										_logger
												.info("getDestinationFromAppRouter(): Remote route returned from app router");
									}

									// push all routes ahead of existing routes
									for (String route : ri.getRoutes()) {
										dsUri = DsURI
												.constructFrom(route);
										SipURI uri = new AseSipURIImpl(
												(DsSipURL) dsUri);
										uri.setLrParam(true);
										req.pushRouteInternal(uri);
									}
									if (freshDestination) {
										destination
												.setStatus(Dispatcher.EXTERNAL_ROUTE);
									} else {
										destination
												.setStatus(Dispatcher.PROCESSING_OVER);
									}
									return destination;
								}
							} else {
								_logger
										.warn("Non-SIP Route URI returned by Application Router");
				     			if(req.getHeader("P-Asserted-Service") != null){
				     				_logger
									.info("Removing P-Asserted-Service");
				     				req.removeHeader("P-Asserted-Service");
								}
								if(req.getHeader("Accept-Contact") !=null){
				     				_logger
									.info("Removing Accept-Contact");
									req.removeHeader("Accept-Contact");
								}
							}
						} catch (DsSipParserException e) {
							_logger
									.warn(
											"Invalid Route URI returned by Application Router",
											e);
						}catch (DsException e) {
							_logger
							.warn(
									"Invalid Route URI returned by Application Router",
									e);
				}
					}
					break;

				case ROUTE_BACK:

					if ((ri.getRoutes() != null)
							&& (ri.getRoutes().length != 0)) {
						SipURI uri=null;
						// push containers route header first
						try {
							ConfigRepository cr = (ConfigRepository) Registry
							.lookup(Constants.NAME_CONFIG_REPOSITORY);

			         		String containerIpAddress = AseUtils.getIPAddressList( cr
							.getValue(Constants.OID_SIP_CONNECTOR_IP_ADDRESS), true);
			         		
			         		String containerSipPort = (String) cr
							.getValue(Constants.OID_SIP_CONNECTOR_PORT);
							
							 String containerRoute="sip:"+ containerIpAddress+ ":"+containerSipPort;
                        	DsURI dsUri=DsURI.constructFrom(containerRoute);
                        	uri = new AseSipURIImpl((DsSipURL) dsUri);
                        	req.pushRouteInternal(uri);
                        	
                            
							//push other routes afterwards
							for (String route : ri.getRoutes()) {
								dsUri = DsURI.constructFrom(route);
								uri = new AseSipURIImpl((DsSipURL) dsUri);
								uri.setLrParam(true);
								req.pushRouteInternal(uri);
							}
						} catch (DsSipParserException e) {
							_logger
									.warn(
											"Invalid Route URI returned by Application Router",
											e);
						}
					}
				case NO_ROUTE:
					// No difference here as we do not have popped route in SIP
					// request.
					break;
				}

				String applicationName = ri.getNextApplicationName();

				// An application name of null or blank indicates processing
				// complete.
				if ((applicationName == null)
						|| (applicationName.length() == 0)) {
					if (freshDestination) {
						destination.setValid(false);
						if (_logger.isEnabledFor(Level.INFO)) {
							_logger
									.info("getDestinationFromAppRouter(): It was a blank destination and no application was returned");
						}
						destination.setStatus(Dispatcher.NO_DESTINATION_FOUND);
						return destination;
					} else {
						if (_logger.isEnabledFor(Level.INFO)) {
							_logger
									.info("getDestinationFromAppRouter(): It was a reused destination and no further application was returned");
						}
						destination.setStatus(Dispatcher.PROCESSING_OVER);
						return destination;
					}
				} else {

					Iterator it = host.findByNamePrefix(applicationName);
					if (it.hasNext()) {
						AseContainer cont = (AseContainer) it.next();
						if (_logger.isEnabledFor(Level.DEBUG)) {
							_logger
									.debug("getDestinationFromAppRouter(): Checking for main servlet");
						}
						// cont should not be null
						String ms = ((AseContext) cont).getMainServlet();
						if (ms != null) {
							destination.setValid(true);
							destination.setServletName(ms);
							// destination.setAppName(((AseContext)cont).getObjectName());
							// // set into AddRuleSet.java app-name as
							// objectName from sip.xml //cont.getName());

							destination.setAppName(cont.getName());
							AseSipSession sess = (AseSipSession) req
									.getSession();
							sess.setSubscriberURI(ri.getSubscriberURI());
							sess.setRegion(ri.getRoutingRegion());
							sess.setRouterStateInfo(ri.getStateInfo());

							if (_logger.isEnabledFor(Level.INFO)) {
								_logger
										.info("getDestinationFromAppRouter(): Main servlet "
												+ ms
												+ " for app "
												+ cont.getName()
												+ " used as destination ");
							}
							destination.setStatus(Dispatcher.DESTINATION_FOUND);
							return destination;
						} else { // if no main servlet found find if servlet
							// specified in rules

							RuleDataComposite rule = rulesRepository
									.findMatchingRule(request, null,
											applicationName);

							if (rule == null) { // if no rule specified invoke
								// default handler
								_logger
										.debug("getDestinationFromAppRouter(): Rule Not found so invoking default servlet for "
												+ applicationName);

								destination.setValid(true);
								destination.setServletName(((AseContext) cont)
										.getDefaultHandlerName());
								destination.setAppName(cont.getName());
								AseSipSession sess = (AseSipSession) req
										.getSession();
								sess.setSubscriberURI(ri.getSubscriberURI());
								sess.setRegion(ri.getRoutingRegion());
								sess.setRouterStateInfo(ri.getStateInfo());

								destination
										.setStatus(Dispatcher.DESTINATION_FOUND);
								return destination;

							} else {

								// if no rule specified invoke default handler
								_logger
										.debug("getDestinationFromAppRouter(): Rule Mathcing for finding servlet as No Main Servlet defined for  "
												+ applicationName);
								destination.setValid(true);
								destination.setServletName((rule.getRule())
										.getServletName());
								destination.setAppName((rule.getRule())
										.getAppName());
								AseSipSession sess = (AseSipSession) req
										.getSession();
								sess.setSubscriberURI(ri.getSubscriberURI());
								sess.setRegion(ri.getRoutingRegion());
								sess.setRouterStateInfo(ri.getStateInfo());

								destination
										.setStatus(Dispatcher.DESTINATION_FOUND);
								return destination;
							}

						}

					} else {
						_logger
								.warn("Unable to get container for Application : "
										+ applicationName);
					}

					RuleDataComposite rule = rulesRepository.findMatchingRule(
							request, null, applicationName);
					if (rule == null) {
						if(_logger.isDebugEnabled()) {

						_logger
								.debug("getDestinationFromAppRouter(): Rule matching failed for "
										+ applicationName);
						}
						if (lastAppName.equals(applicationName)) {
							// In order to prevent looping.
							_logger
									.error("Detected looping on getting destination for : "
											+ applicationName);
							if (freshDestination) {
								destination.setValid(false);
								if (_logger.isEnabledFor(Level.INFO)) {
									_logger
											.info("getDestinationFromAppRouter(): It was a blank destination and no application was returned");
								}
								destination
										.setStatus(Dispatcher.NO_DESTINATION_FOUND);
								return destination;
							} else {
								if (_logger.isEnabledFor(Level.INFO)) {
									_logger
											.info("getDestinationFromAppRouter(): It was a reused destination and no further application was returned");
								}
								destination
										.setStatus(Dispatcher.PROCESSING_OVER);
								return destination;
							}
						}
						lastAppName = applicationName;
						// Will loop back here,
					} else {
						destination.setValid(true);
						destination.setServletName((rule.getRule())
								.getServletName());
						destination.setAppName((rule.getRule()).getAppName());
						AseSipSession sess = (AseSipSession) req.getSession();
						sess.setSubscriberURI(ri.getSubscriberURI());
						sess.setRegion(ri.getRoutingRegion());
						sess.setRouterStateInfo(ri.getStateInfo());

						destination.setStatus(Dispatcher.DESTINATION_FOUND);
						return destination;
					}
				}
			}
		}
	}
}
