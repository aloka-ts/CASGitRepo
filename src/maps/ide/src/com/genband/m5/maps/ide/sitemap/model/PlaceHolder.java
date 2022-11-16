/**********************************************************************
*	 GENBAND, Inc. Confidential and Proprietary
*
* This work contains valuable confidential and proprietary 
* information.
* Disclosure, use or reproduction without the written authorization of
* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
* is protected by the laws of the United States and other countries.
* If publication of the work should occur the following notice shall 
* apply:
* 
* "Copyright 2007 GENBAND, Inc.  All rights reserved."
**********************************************************************
**/


/**********************************************************************
*
*     Project:  MAPS
*
*     Package:  com.genband.m5.maps.ide.sitemap.model
*
*     File:     PlaceHolder.java
*
*     Desc:   	Model class for PlaceHolder.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * Model class for PlaceHolder.
 * @author Genband
 */
public class PlaceHolder extends ModelElement {

/** 
 * A static array of property descriptors.
 * There is one IPropertyDescriptor entry per editable property.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
private static IPropertyDescriptor[] descriptors;
/** ID for the Height property value  */
public static final String PLACEHOLDER_HEIGHT_PROP = "PlaceHolder.Height";
/** Property ID to use when the location of this PlaceHolder is modified. */
public static final String PLACEHOLDER_LOCATION_PROP = "PlaceHolder.Location";
/** Property ID to use when the size of this PlaceHolder is modified. */
public static final String PLACEHOLDER_SIZE_PROP = "PlaceHolder.Size";
/** ID for the Width property value  */
public static final String PLACEHOLDER_WIDTH_PROP = "PlaceHolder.Width";

/** ID for the name property value (used for by the corresponding property descriptor).  */
public static final String PLACEHOLDER_NAME = "PlaceHolder.name";
/** ID for the roles property value */
public static final String PLACEHOLDER_ROLES = "PlaceHolder.roles";
/** ID for the layout property value */
public static final String PLACEHOLDER_LAYOUT = "PlaceHolder.layout";
/** ID for the theme property value */
public static final String PLACEHOLDER_THEME = "PlaceHolder.theme";

/** Property ID to use when a portlet is added to this placeholder. */
public static final String PORTLET_ADDED_PROP = "PlaceHolder.PortletAdded";
/** Property ID to use when a portlet is removed from this placeholder. */
public static final String PORTLET_REMOVED_PROP = "PlaceHolder.PortletRemoved";
/** Property ID to use when a child is added to this placeholder. */
public static final String CHILD_ADDED_PROP = "PlaceHolder.ChildAdded";
/** Property ID to use when a child is removed from this placeholder. */
public static final String CHILD_REMOVED_PROP = "PlaceHolder.ChildRemoved";

/** A 16x16 pictogram of PlaceHolder */
private static final Image PLACEHOLDER_ICON = createImage("placeholder16.bmp");
/** Index of this PlaceHolder */
private int placeHolderNo = 1;
/**Layout of page */
private String layout = null;
private int noOfPortlets = 0;

private List child = new ArrayList();
/*
 * Initializes the property descriptors array.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
static {
	descriptors = new IPropertyDescriptor[] { 
			new PropertyDescriptor(PLACEHOLDER_NAME, "Region"), // id and description pair
			//new TextPropertyDescriptor(PLACEHOLDER_LAYOUT, "Specify layout"), // id and description pair
			//new TextPropertyDescriptor(PLACEHOLDER_THEME, "Specify theme"), // id and description pair
	};
	// use a custom cell editor validator for FILE_PATH_PROP
	for (int i = 0; i < descriptors.length; i++) {
		((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
				return null;
			}
		});
	}
} // static

public PlaceHolder(){
}

protected static Image createImage(String name) {
	return null;//CPFPlugin.getDefault().getImageRegistry().get(name);
}

/** Location of this PlaceHolder */
private Point location = new Point(0, 0);
/** Size of this PlaceHolder*/
private Dimension size = new Dimension(30,30 );
/** Name of this PlaceHolder */
private String name = "Left";

public Image getIcon() {
	return PLACEHOLDER_ICON;
}

/**
 * Return the Location of this PlaceHolder.
 * @return a non-null location instance
 */
public Point getLocation() {
	return location.getCopy();
}

/**
 * Returns an array of IPropertyDescriptors for this Placeholder.
 * <p>The returned array is used to fill the property view, when the edit-part corresponding
 * to this model element is selected.</p>
 * @see #descriptors
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	return descriptors;
}

/**
 * Return the property value for the given propertyId, or null.
 * <p>The property view uses the IDs from the IPropertyDescriptors array 
 * to obtain the value of the corresponding properties.</p>
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public Object getPropertyValue(Object propertyId) {
	if (PLACEHOLDER_NAME.equals(propertyId)) {
		return name;
	}
	return super.getPropertyValue(propertyId);
}

/**
 * Return the Size of this .
 * @return a non-null Dimension instance
 */
public Dimension getSize() {
	return size.getCopy();
}

/**
 * Set the Location of this .
 * @param newLocation a non-null Point instance
 * @throws I
 * llegalArgumentException if the parameter is null
 */
public void setLocation(Point newLocation) {
	if (newLocation == null) {
		throw new IllegalArgumentException();
	}
	location.setLocation(newLocation);
	firePropertyChange(PLACEHOLDER_LOCATION_PROP, null, location);
}


/**
 * Set the property value for the given property id.
 * If no matching id is found, the call is forwarded to the superclass.
 * The property view uses the IDs from the IPropertyDescriptors array to set the values
 * of the corresponding properties.
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public void setPropertyValue(Object propertyId, Object value) {
	if (PLACEHOLDER_NAME.equals(propertyId)) {
		name = (String)value;
		firePropertyChange(PLACEHOLDER_NAME, null, name);
	}else if(PLACEHOLDER_LAYOUT.equals(propertyId)){
		layout = (String)value;
		firePropertyChange(PLACEHOLDER_LAYOUT, null, layout);
	}
	else {
		super.setPropertyValue(propertyId, value);
	}
}

@Override
public void setRoles(String roles) {
	String previousRoles = getRoles();
	super.setRoles(roles);
	firePropertyChange(PlaceHolder.PLACEHOLDER_ROLES, previousRoles , roles);
}

/** Return a List of child components in this placeholder. The returned List should not be modified. */
public List getChildren() {
	return child;
}
/** 
 * Add a portlet to this placeHolder.
 * @param s a non-null portlet instance
 * @return true, if the portlet was added, false otherwise
 */
public boolean addPortlet(Portlet portlet) {
	if (portlet != null && child.add(portlet)) {
		System.out.println("Placeholder: in addPortlet");
		noOfPortlets++;
		portlet.setPortletNo(noOfPortlets);
		firePropertyChange(PORTLET_ADDED_PROP, null, portlet);
		//firePropertyChange(PAGE_NAME, null, placeHolder);
		return true;
	}
	return false;
}
/**
 * Remove a Portlet from this placeHolder.
 * @param s a non-null Portlet instance;
 * @return true, if the Portlet was removed, false otherwise
 */
public boolean removePortlet(Portlet portlet) {
	if (portlet != null && child.remove(portlet)) {
		noOfPortlets--;
		firePropertyChange(PORTLET_REMOVED_PROP, null, portlet);
		return true;
	}
	return false;
}


/**
 * Set the Size of this placeholder.
 * Will not modify the size if newSize is null.
 * @param newSize a non-null Dimension instance or null
 */
public void setSize(Dimension newSize) {
	if (newSize != null) {
		size.setSize(newSize);
		firePropertyChange(PLACEHOLDER_SIZE_PROP, null, size);
	}
}
public String toString() {
	return "PlaceHolder " + hashCode();
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public int getPlaceHolderNo() {
	return placeHolderNo;
}

public void setPlaceHolderNo(int placeHolderNo) {
	this.placeHolderNo = placeHolderNo;
}

public String getLayout() {
	return layout;
}

public void setLayout(String layout) {
	this.layout = layout;
}
}