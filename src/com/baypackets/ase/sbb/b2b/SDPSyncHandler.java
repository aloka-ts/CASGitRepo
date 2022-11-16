/*
 * Created on Oct 30, 2004
 *
 */
package com.baypackets.ase.sbb.b2b;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.Address;

import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;


/**
 */
@DefaultSerializer(ExternalizableSerializer.class)
public class SDPSyncHandler extends BasicSBBOperation {
	private static final long serialVersionUID = 83584327033147L;
	public static final int OPERATION_MUTE = 1;
	public static final int OPERATION_RESYNC = 2;

	private int operation;
	
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public SDPSyncHandler() {
		super();
	}

	/**
	 * 
	 */
	public SDPSyncHandler(int operation) {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.operation = in.readInt();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.operation);
	}
}
