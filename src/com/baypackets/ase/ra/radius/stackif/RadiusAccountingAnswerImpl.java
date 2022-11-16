package com.baypackets.ase.ra.radius.stackif;

import java.util.List;
import org.apache.log4j.Logger;
import org.tinyradius.packet.RadiusPacket;
import com.baypackets.ase.ra.radius.RadiusAccountingRequest;
import com.baypackets.ase.ra.radius.RadiusAccountingAnswer;
import com.baypackets.ase.resource.Request;

public class RadiusAccountingAnswerImpl extends RadiusAbstractResponse
implements RadiusAccountingAnswer{
	private static final long serialVersionUID = 1L;
	private RadiusAccountingRequest request;
	private static Logger logger = Logger.getLogger(RadiusAccountingAnswerImpl.class);

	protected RadiusAccountingAnswerImpl(int type,final int identifier) {
		super(type);
		RadiusPacket radiusPacket=new RadiusPacket(type, identifier);
		super.setRadiusPacket(radiusPacket);
	}

	@SuppressWarnings("unchecked")
	protected RadiusAccountingAnswerImpl(int type,int identifier, List attributes) {
		super(type);
		RadiusPacket radiusPacket=new RadiusPacket(type, identifier,attributes);
		super.setRadiusPacket(radiusPacket);
	}
	protected RadiusAccountingAnswerImpl(RadiusPacket radiusPacket){
		super(radiusPacket.getPacketType());
		super.setRadiusPacket(radiusPacket);
	}

	protected void setRequest(RadiusAccountingRequest request)
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
