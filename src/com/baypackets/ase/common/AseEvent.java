/*
 * Created on Aug 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.common;

import java.util.EventObject;
import com.baypackets.ase.util.AseStrings;

/**
 */
public class AseEvent extends EventObject {
	
	private int type;
	private Object data;
	
	/**
	 * 
	 */
	public AseEvent(Object source, int type, Object data) {
		super(source);
		this.type = type;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public int getType() {
		return type;
	}

	public void setData(Object object) {
		data = object;
	}

	public void setType(int i) {
		type = i;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("(event_type=");
		buffer.append(this.type);
		buffer.append(", data=");
		buffer.append(this.data);
		buffer.append(", source=");
		buffer.append(this.getSource());
		buffer.append(AseStrings.PARENTHESES_CLOSE);
		return buffer.toString();
	}
}
