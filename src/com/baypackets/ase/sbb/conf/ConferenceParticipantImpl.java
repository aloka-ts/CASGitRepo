package com.baypackets.ase.sbb.conf;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.ConferenceParticipant;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.mediaserver.MsSessionControllerImpl;

public class ConferenceParticipantImpl extends MsSessionControllerImpl
		implements ConferenceParticipant {
	
	private static final Logger logger = Logger.getLogger(ConferenceParticipantImpl.class);
	private static final long serialVersionUID = 243547432450798285L;
	private String displayRegionId;
	public String getId() {
		return super.getId();
	}
	
	public void activate(SipSession session) {
		if(logger.isDebugEnabled()){
			logger.debug("activate() IN");
		}

		super.activate(session);
	}
	public String getDisplayRegionId()
	{
		return this.displayRegionId;
	}
	public void setDisplayRegionId(String displayId)
	{
		this.displayRegionId=displayId;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("writeExternal() called on SBB with name: " + this.getName());
		}
		try{
			boolean isdisplayRegionIdValid = this.displayRegionId != null;
			out.writeBoolean(isdisplayRegionIdValid);
			if(isdisplayRegionIdValid)
				out.writeUTF(displayRegionId);
		super.writeExternal0(out);
		}catch(Exception e){
			logger.error("Exception in writeExternal()....." +e,e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("writeExternal() completed on SBB with name: " + this.getName());
		}
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug("readExternal() called on SBB object...");
		}
		boolean isdisplayRegionIdValid=in.readBoolean();
		if(isdisplayRegionIdValid)
			this.displayRegionId=in.readUTF();
		super.readExternal0(in);
		if (logger.isDebugEnabled()) {
			logger.debug("readExternal(): Completed de-serialization of SBB with name: " + this.getName());
		}
	}
	
	
}
