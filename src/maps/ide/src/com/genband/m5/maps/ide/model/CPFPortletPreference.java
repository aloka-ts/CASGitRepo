package com.genband.m5.maps.ide.model;

import java.util.List;
import org.eclipse.core.resources.IFile;

import com.genband.m5.maps.common.CPFConstants;


public class CPFPortletPreference implements java.io.Serializable{
	
	public CPFPortletPreference(){
		
	}

	/**
	 * @uml.property  name="title"
	 */
	private String title = "";
	
	/**
	 * @uml.property  name="helpJsp"
	 */
	private String helpJsp;
	
    /**
     * @uml.property  name="portletModes"
     */
	private List<CPFConstants.PortletMode> portletModes;
	
   /**
	 * @uml.property  name="pagination"
	 */
	private int pagination;
	
	/**
	 * @uml.property  name="windowModes"
	 */
	private List<CPFConstants.WindowMode> windowModes;
	
	/**
	 * @uml.property  name="defaultWindowMode"
	 */
	private CPFConstants.WindowMode defaultWindowMode = CPFConstants.WindowMode.NORMAL;
	
	/**
	 * @uml.property  name="helpSupported"
	 */
	private boolean helpSupported;
	
	/**
	 * Getter of the property <tt>title</tt>
	 * @return  Returns the title.
	 * @uml.property  name="title"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter of the property <tt>title</tt>
	 * @param title  The title to set.
	 * @uml.property  name="title"
	 */
	public void setTitle(String title) {
		this.title = title;
	}


	/**
	 * Getter of the property <tt>pagination</tt>
	 * @return  Returns the pagination.
	 * @uml.property  name="pagination"
	 */
	public int getPagination() {
		return pagination;
	}

	/**
	 * Setter of the property <tt>pagination</tt>
	 * @param pagination  The pagination to set.
	 * @uml.property  name="pagination"
	 */
	public void setPagination(int pagination) {
		this.pagination = pagination;
	}


	/**
	 * Getter of the property <tt>windowModes</tt>
	 * @return  Returns the windowModes.
	 * @uml.property  name="windowModes"
	 */
	public List<CPFConstants.WindowMode> getWindowModes() {
		return windowModes;
	}

	/**
	 * Setter of the property <tt>windowModes</tt>
	 * @param windowModes  The windowModes to set.
	 * @uml.property  name="windowModes"
	 */
	public void setWindowModes(List<CPFConstants.WindowMode> windowModes) {
		this.windowModes = windowModes;
	}

	/**
	 * Getter of the property <tt>defaultWindowMode</tt>
	 * @return  Returns the defaultWindowMode.
	 * @uml.property  name="defaultWindowMode"
	 */
	public CPFConstants.WindowMode getDefaultWindowMode() {
		return defaultWindowMode;
	}

	/**
	 * Setter of the property <tt>defaultWindowMode</tt>
	 * @param defaultWindowMode  The defaultWindowMode to set.
	 * @uml.property  name="defaultWindowMode"
	 */
	public void setDefaultWindowMode(CPFConstants.WindowMode defaultWindowMode) {
		this.defaultWindowMode = defaultWindowMode;
	}


	/**
	 * Getter of the property <tt>helpSupported</tt>
	 * @return  Returns the helpSupported.
	 * @uml.property  name="helpSupported"
	 */
	public boolean getHelpSupported() {
		return helpSupported;
	}

	/**
	 * Setter of the property <tt>helpSupported</tt>
	 * @param helpSupported  The helpSupported to set.
	 * @uml.property  name="helpSupported"
	 */
	public void setHelpSupported(boolean helpSupported) {
		this.helpSupported = helpSupported;
	}

	/** Getter of the property <tt>helpJsp</tt>
	 * @return  Returns the helpJsp.
	 * @uml.property  name="helpJsp"
	 */
	public String getHelpJsp() {
		if (null !=helpJsp){
			return helpJsp;
		}else{
			return null;
		}
	}

	/**
	 * Setter of the property <tt>helpJsp</tt>
	 * @param helpJsp  The helpJsp to set.
	 * @uml.property  name="helpJsp"
	 */
	public void setHelpJsp(String helpJsp) {
		this.helpJsp = helpJsp;
	}

	public List<CPFConstants.PortletMode> getPortletModes() {
		return portletModes;
	}

	public void setPortletModes(List<CPFConstants.PortletMode> portletModes) {
		this.portletModes = portletModes;
	}

}
