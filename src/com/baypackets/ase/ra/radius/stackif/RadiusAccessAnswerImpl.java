package com.baypackets.ase.ra.radius.stackif;

import java.util.List;
import org.apache.log4j.Logger;
import org.tinyradius.packet.RadiusPacket;
import com.baypackets.ase.ra.radius.RadiusAccessRequest;
import com.baypackets.ase.ra.radius.RadiusAccessAnswer;
import com.baypackets.ase.resource.Request;

public class RadiusAccessAnswerImpl extends RadiusAbstractResponse implements RadiusAccessAnswer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 111222L;
	private RadiusAccessRequest request;
	private static Logger logger = Logger.getLogger(RadiusAccessAnswerImpl.class);
	protected RadiusAccessAnswerImpl(int type,final int identifier) {
		super(type);
		RadiusPacket radiusPacket=new RadiusPacket(type, identifier);
		super.setRadiusPacket(radiusPacket);
	}

	@SuppressWarnings({ "unchecked" })
	protected RadiusAccessAnswerImpl(int type,int identifier, List attributes) {
		super(type);
		RadiusPacket radiusPacket=new RadiusPacket(type, identifier,attributes);
		super.setRadiusPacket(radiusPacket);
	}

	protected RadiusAccessAnswerImpl(RadiusPacket radiusPacket){
		super(radiusPacket.getPacketType());
		super.setRadiusPacket(radiusPacket);
	}

	protected void setRequest(RadiusAccessRequest request)
	{
		if(logger.isDebugEnabled()){
			logger.debug("Inside setRequest()");
		}
		this.request=request;
	}
	@Override
	public Request getRequest() {
		if(logger.isDebugEnabled()){
			logger.debug("Inside getRequest()");
		}
		return this.request;
	}

	@Override
	public String toString() {
		if(getRadiusPacket()!=null)
			return getRadiusPacket().toString();
		return null;
	}
}

