package com.genband.m5.maps.ide.model;

import java.util.List;
import java.util.Locale;
import org.eclipse.core.resources.IFile;

import com.genband.m5.maps.common.CPFConstants;


public class CPFProject {

	/**
	 * @uml.property  name="dataModel"
	 */
	private List<IFile> dataModel;

	/**
	 * @uml.property  name="defaultLocale"
	 */
	private Locale defaultLocale;

	/**
	 * @uml.property  name="locales"
	 */
	private List<Locale> locales = new java.util.ArrayList<Locale>();

	/**
	 * @uml.property  name="name"
	 */
	private String name = "";

	/**
	 * @uml.property  name="navigationType"
	 */
	private CPFConstants.NavigationType navigationType = CPFConstants.NavigationType.NAVIGATION_TYPE_I;

	/**
	 * Getter of the property <tt>dataModel</tt>
	 * @return  Returns the dataModel.
	 * @uml.property  name="dataModel"
	 */
	public List<IFile> getDataModel() {
		return dataModel;
	}

	/**
	 * Getter of the property <tt>defaultLocale</tt>
	 * @return  Returns the defaultLocale.
	 * @uml.property  name="defaultLocale"
	 */
	public Locale getDefaultLocale() {
		return defaultLocale;
	}

	/**
	 * Getter of the property <tt>locales</tt>
	 * @return  Returns the locales.
	 * @uml.property  name="locales"
	 */
	public List<Locale> getLocales() {
		return locales;
	}

	/**
	 * Getter of the property <tt>name</tt>
	 * @return  Returns the name.
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter of the property <tt>navigationType</tt>
	 * @return  Returns the navigationType.
	 * @uml.property  name="navigationType"
	 */
	public CPFConstants.NavigationType getNavigationType() {
		return navigationType;
	}

	/**
	 * Setter of the property <tt>dataModel</tt>
	 * @param dataModel  The dataModel to set.
	 * @uml.property  name="dataModel"
	 */
	public void setDataModel(List<IFile> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * Setter of the property <tt>defaultLocale</tt>
	 * @param defaultLocale  The defaultLocale to set.
	 * @uml.property  name="defaultLocale"
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Setter of the property <tt>locales</tt>
	 * @param locales  The locales to set.
	 * @uml.property  name="locales"
	 */
	public void setLocales(List<Locale> locales) {
		this.locales = locales;
	}

	/**
	 * Setter of the property <tt>name</tt>
	 * @param name  The name to set.
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Setter of the property <tt>navigationType</tt>
	 * @param navigationType  The navigationType to set.
	 * @uml.property  name="navigationType"
	 */
	public void setNavigationType(CPFConstants.NavigationType navigationType) {
		this.navigationType = navigationType;
	}

}
