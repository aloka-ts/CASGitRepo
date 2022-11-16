package com.genband.ase.alcx.MediaService.MediaServiceImpl;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletResponse;

import com.baypackets.ase.sbb.b2b.DialoutHandler;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class _MsDialoutHandler extends DialoutHandler{

	private transient _MsSessionControllerImpl msSession = null;

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public _MsDialoutHandler() {
		super();
	}
	
	public _MsDialoutHandler(Address from, Address addressA, Address addressB) {
		super(from, addressA, addressB);
	}
	public void handleResponse(SipServletResponse response) {
		
		int responseCode = response.getStatus();
		if(response.getSession() == this.getMsSBB().getB() &&
			responseCode >=200 && responseCode < 299 && 
			response.getMethod().equals("INVITE")){
			this.getMsSBB().parseSDP(response);
		}

		super.handleResponse(response);
	}
	
	private _MsSessionControllerImpl getMsSBB(){
		if(msSession == null){
			this.msSession = (_MsSessionControllerImpl)
						this.getOperationContext().getSBB();
		}
		return this.msSession;
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
	}
}
