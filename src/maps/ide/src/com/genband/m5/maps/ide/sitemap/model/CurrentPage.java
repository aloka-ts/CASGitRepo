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
*     File:     CurrentPage.java
*
*     Desc:   	Model class for a dummy page which actually represents 
*     			the current page selected.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.genband.m5.maps.ide.CPFPlugin;

/**
 * Model class for Current page Info Holder.
 * @author Genband 
 */
public class CurrentPage extends ModelElement {

/** 
 * A static array of property descriptors.
 * There is one IPropertyDescriptor entry per editable property.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
private static IPropertyDescriptor[] descriptors;
/** ID for the name property value (used for by the corresponding property descriptor).  */
public static final String PAGE_NAME = "CurrentPage.name";
/** ID for the layout property value (used for by the corresponding property descriptor).  */
public static final String PAGE_LAYOUT = "CurrentPage.layout";
/** ID for the theme property value (used for by the corresponding property descriptor).  */
public static final String PAGE_THEME = "CurrentPage.theme";

/** A 16x16 pictogram of Page */
private static final Image PAGE_ICON = createImage("page16.bmp");
/** No. of sub pages in this page*/
private int noOfSubPages = 1;
/** Index of this Page */
private int pageNo = 1;

/*
 * Initializes the property descriptors array.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
static {
	descriptors = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(PAGE_NAME, "New Page"), // id and description pair
			new TextPropertyDescriptor(PAGE_LAYOUT, "Specify layout"), // id and description pair
			new TextPropertyDescriptor(PAGE_THEME, "Specify theme"), // id and description pair
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

public CurrentPage(){
}

protected static Image createImage(String name) {
	return CPFPlugin.getDefault().getImageRegistry().get(name);
}

/** Location of this Page */
private Point location = new Point(0, 0);
/** Size of this Page */
private Dimension size = new Dimension(30,30 );
/** Name of this Page */
private String name = "New Page";
/** Layout of this page*/
private String layout = "layout";
/** Theme of this page*/
private String theme = "theme";

public Image getIcon() {
	return PAGE_ICON;
}

/**
 * Return the Location of this Page.
 * @return a non-null location instance
 */
public Point getLocation() {
	return location.getCopy();
}

/**
 * Returns an array of IPropertyDescriptors for this Page.
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
	if (PAGE_NAME.equals(propertyId)) {
		return name;
	}else if (PAGE_LAYOUT.equals(propertyId)) {
		return layout;
	}else if (PAGE_THEME.equals(propertyId)) {
		return theme;
	}
	return super.getPropertyValue(propertyId);
}

/**
 * Return the Size of this Page.
 * @return a non-null Dimension instance
 */
public Dimension getSize() {
	return size.getCopy();
}

public String getLayout() {
	return layout;
}

public void setLayout(String layout) {
	this.layout = layout;
}

public String getTheme() {
	return theme;
}

public void setTheme(String theme) {
	this.theme = theme;
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
	if (PAGE_NAME.equals(propertyId)) {
		name = (String)value;
		firePropertyChange(PAGE_NAME, null, name);
	} else if (PAGE_LAYOUT.equals(propertyId)) {
		layout = (String)value;
		firePropertyChange(PAGE_LAYOUT, null, layout);
	} else if (PAGE_THEME.equals(propertyId)) {
		theme = (String)value;
		firePropertyChange(PAGE_THEME, null, theme);
	} else {
		super.setPropertyValue(propertyId, value);
	}
}

public String toString() {
	return "Page " + hashCode();
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public int getPageNo() {
	return pageNo;
}

public void setPageNo(int pageNo) {
	this.pageNo = pageNo;
}

public int getNoOfSubPages() {
	return noOfSubPages;
}

public void setNoOfSubPages(int noOfSubPages) {
	noOfSubPages = noOfSubPages;
}


}