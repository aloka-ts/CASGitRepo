package com.baypackets.ase.ari;

import java.util.Map;

import javax.servlet.sip.ar.SipApplicationRoutingRegion;
import javax.servlet.sip.ar.SipRouteModifier;

public class AseSipApplicationRouterInfo {

	public AseSipApplicationRouterInfo(String nextApplicationName,
			String subscriberURI, SipApplicationRoutingRegion sipAppRegion, String[] routes,
			SipRouteModifier sipRouteModifier, String order,Map<String,String> optionalParameters) {
		this.nextApplicationName = nextApplicationName;
		this.subscriberURI = subscriberURI;
		this.route = routes;
		this.routeModifier = sipRouteModifier;
		this.region = sipAppRegion;
		this.order = order;
		this.optionalParameters=optionalParameters;
	}

	public void setNextApplicationName(String name) {
		this.nextApplicationName=name;
	}
	
	public String getNextApplicationName() {
		return this.nextApplicationName;
	}

	public String[] getRoute() {
		return this.route;
	}

	public SipRouteModifier getRouteModifier() {
		return this.routeModifier;
	}

	public SipApplicationRoutingRegion getRoutingRegion() {
		return this.region;
	}

	public String getSubscriberURI() {
		return this.subscriberURI;
	}

	public String toString() {
		return new String("SipApplicationRouterInfo:" + nextApplicationName
				+ ":" + route + ":" + routeModifier.toString() + ":"
				+ region.toString() + ":" + order + ":" + subscriberURI +" Optional Paranmeters: "+optionalParameters);
	}

	// TODO: Where does region come from?

	private String nextApplicationName;
	private String[] route;
	private SipRouteModifier routeModifier;
	private SipApplicationRoutingRegion region;
	private String order;
	private String subscriberURI;
	
	private Map<String,String> optionalParameters;

	public String getOrder() {
		return order;
	}

	public SipApplicationRoutingRegion getRegion() {
		return region;
	}

	public Map<String, String> getOptionalParameters() {
		return optionalParameters;
	}

}
