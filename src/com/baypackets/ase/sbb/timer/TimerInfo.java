/**
 *Info to be passed in timer.
 *@author Sumit 
 */
package com.baypackets.ase.sbb.timer;

import java.io.Serializable;

import com.baypackets.ase.sbb.b2b.ConnectHandler;

public class TimerInfo implements Serializable {

	private static final long serialVersionUID = 4046336346631832900L;
	private String msg;
	private ConnectHandler connectHandler = null;
	private Object obj = null;
	public TimerInfo(String msg, Object object){
		this.msg=msg;
		//this.connectHandler = (ConnectHandler)object;
		this.obj = object;
	}
	
	public Object getObject(){
		return this.obj;
	}
	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}
	
	/**
	 * @param msg the msg to set
	 */
	public void setConnectHandler(ConnectHandler connectHandler) {
		this.connectHandler = connectHandler;
	}
	/**
	 * @return the msg
	 */
	public ConnectHandler getConnectHandler() {
		return connectHandler;
	}
	
}