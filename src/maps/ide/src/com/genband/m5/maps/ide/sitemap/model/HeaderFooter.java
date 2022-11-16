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
*     File:     HeaderFooter.java
*
*     Desc:   	Abstract prototype of Header or Footer.
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
/**
 * Abstract prototype of Header or Footer.
 * Has a size (width and height), a location (x and y position) and
 * jsp file location as properties
 * Use subclasses(Header.java or Footer.java) to instantiate.
 * @author Genband  
 */
public abstract class HeaderFooter extends ModelElement {

/** 
 * A static array of property descriptors.
 * There is one IPropertyDescriptor entry per editable property.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
private static IPropertyDescriptor[] descriptors;
/** ID for the Height property value  */
private static final String HEIGHT_PROP = "HeaderFooter.Height";
/** Property ID to use when the location of this HeaderFooter is modified. */
public static final String LOCATION_PROP = "HeaderFooter.Location";
/** Property ID to use when the size of this HeaderFooter is modified. */
public static final String SIZE_PROP = "HeaderFooter.Size";
/** ID for the Width property value  */
private static final String WIDTH_PROP = "HeaderFooter.Width";

/** ID for the X property value   */
private static final String XPOS_PROP = "HeaderFooter.xPos";
/** ID for the Y property value   */
private static final String YPOS_PROP = "HeaderFooter.yPos";
/** ID for the JSP Fragment Location property value (used for by the corresponding property descriptor).  */
private static final String FILE_PATH_PROP = "HeaderFooter.filePath";

/*
 * Initializes the property descriptors array.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
static {
	descriptors = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(FILE_PATH_PROP, "JSP Fragmant Location"), // id and description pair
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

protected static Image createImage(String name) {
	return null;//CPFPlugin.getDefault().getImageRegistry().get(name);
	
}

/** Location of this Header or Footer. */
private Point location = new Point(0, 0);
/** Size of this Header or Footer. */
private Dimension size = new Dimension(300,30 );
/** File path for jsp fragment*/
private String filePath = "Specify the absolute path here";

/**
 * Return a pictogram (small icon) describing this model element.
 * Children should override this method and return an appropriate Image.
 * @return a 16x16 Image or null
 */
public abstract Image getIcon();

/**
 * Return the Location of this Header or Footer.
 * @return a non-null location instance
 */
public Point getLocation() {
	return location.getCopy();
}

/**
 * Returns an array of IPropertyDescriptors for this HeaderFooter.
 * The returned array is used to fill the property view, when the edit-part corresponding
 * to this model element is selected.
 * @see #descriptors
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	return descriptors;
}

/**
 * Return the property value for the given propertyId, or null.
 * The property view uses the IDs from the IPropertyDescriptors array 
 * to obtain the value of the corresponding properties.
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public Object getPropertyValue(Object propertyId) {
	if (FILE_PATH_PROP.equals(propertyId)) {
		return filePath;
	}
	return super.getPropertyValue(propertyId);
}

/**
 * Return the Size of this HeaderFooter.
 * @return a non-null Dimension instance
 */
public Dimension getSize() {
	return size.getCopy();
}

/**
 * Set the Location of this HeaderFooter.
 * @param newLocation a non-null Point instance
 * @throws I
 * llegalArgumentException if the parameter is null
 */
public void setLocation(Point newLocation) {
	if (newLocation == null) {
		throw new IllegalArgumentException();
	}
	location.setLocation(newLocation);
	firePropertyChange(LOCATION_PROP, null, location);
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
	if (FILE_PATH_PROP.equals(propertyId)) {
		filePath = (String)value;
	} else {
		super.setPropertyValue(propertyId, value);
	}
}

/**
 * Set the Size of this HeaderFooter.
 * Will not modify the size if newSize is null.
 * @param newSize a non-null Dimension instance or null
 */
public void setSize(Dimension newSize) {
	if (newSize != null) {
		size.setSize(newSize);
		firePropertyChange(SIZE_PROP, null, size);
	}
}

public String getFilePath() {
	return filePath;
}

public void setFilePath(String filePath) {
	this.filePath = filePath;
}
}