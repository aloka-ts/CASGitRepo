/*
 * AuditDialog.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb.audit;
import java.io.Serializable;
/**
 * The AuditDialog class will be used to store result of an msml dialog related audit request 
 * for conference/connection.
 * This class provides getters and setters for dialog related properties.
 */
public class AuditDialog implements Serializable{
	
	private String src;
	private String type;
	private String name;
	private String duration;
	private String primitive;
	private String controller;
	/**
	 * This method sets src attribute of dialog.
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	/**
	 * This method returns src attribute of dialog.
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * This method sets name attribute of dialog.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * This method returns name attribute of dialog.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * This method sets type attribute of dialog.
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * This method returns type attribute of dialog.
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * This method sets value of duration element of dialog.
	 * @param duration the duration to set
	 */
	public void setDuration(String duration) {
		this.duration = duration;
	}
	/**
	 * This method returns value of duration element of dialog.
	 * @return the duration
	 */
	public String getDuration() {
		return duration;
	}
	/**
	 * This method sets value of primitive element of dialog.
	 * @param primitive the primitive to set
	 */
	public void setPrimitive(String primitive) {
		this.primitive = primitive;
	}
	/**
	 * This method returns value of primitive element of dialog.
	 * @return the primitive
	 */
	public String getPrimitive() {
		return primitive;
	}
	/**
	 * This method sets value of controller element of dialog.
	 * @param controller the controller to set
	 */
	public void setController(String controller) {
		this.controller = controller;
	}
	/**
	 * This method returns value of controller element of dialog.
	 * @return the controller
	 */
	public String getController() {
		return controller;
	}
	
}