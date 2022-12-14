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
*     Project:  CPFSupport
*
*     Package:  com.genband.m5.maps.ide.sitemap.model
*
*     File:     Shape.java
*
*     Desc:   	Editor for the SiteMap.
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
 * Abstract prototype of a shape.
 * Has a size (width and height), a location (x and y position) and a list of incoming
 * and outgoing connections. Use subclasses to instantiate a specific shape.
 * @author Genband 
 */
public abstract class Shape extends ModelElement {

/** 
 * A static array of property descriptors.
 * There is one IPropertyDescriptor entry per editable property.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
private static IPropertyDescriptor[] descriptors;
/** ID for the Height property value (used for by the corresponding property descriptor). */
private static final String HEIGHT_PROP = "Shape.Height";
/** Property ID to use when the location of this shape is modified. */
public static final String LOCATION_PROP = "Shape.Location";
private static final long serialVersionUID = 1;
/** Property ID to use when the size of this shape is modified. */
public static final String SIZE_PROP = "Shape.Size";
/** Property ID to use when the list of outgoing connections is modified. */
public static final String SOURCE_CONNECTIONS_PROP = "Shape.SourceConn";
/** Property ID to use when the list of incoming connections is modified. */
public static final String TARGET_CONNECTIONS_PROP = "Shape.TargetConn";
/** ID for the Width property value (used for by the corresponding property descriptor). */
private static final String WIDTH_PROP = "Shape.Width";

/** ID for the X property value (used for by the corresponding property descriptor).  */
private static final String XPOS_PROP = "Shape.xPos";
/** ID for the Y property value (used for by the corresponding property descriptor).  */
private static final String YPOS_PROP = "Shape.yPos";

/*
 * Initializes the property descriptors array.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
static {
	descriptors = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(XPOS_PROP, "X"), // id and description pair
			new TextPropertyDescriptor(YPOS_PROP, "Y"),
			new TextPropertyDescriptor(WIDTH_PROP, "Width"),
			new TextPropertyDescriptor(HEIGHT_PROP, "Height"),
	};
	// use a custom cell editor validator for all four array entries
	for (int i = 0; i < descriptors.length; i++) {
		((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
				int intValue = -1;
				try {
					intValue = Integer.parseInt((String) value);
				} catch (NumberFormatException exc) {
					return "Not a number";
				}
				return (intValue >= 0) ? null : "Value must be >=  0";
			}
		});
	}
} // static

protected static Image createImage(String name) {
	return CPFPlugin.getDefault().getImageRegistry().get(name);
	
}

/** Location of this shape. */
private Point location = new Point(0, 0);
/** Size of this shape. */
private Dimension size = new Dimension(50, 50);

/**
 * Return a pictogram (small icon) describing this model element.
 * Children should override this method and return an appropriate Image.
 * @return a 16x16 Image or null
 */
public abstract Image getIcon();

/**
 * Return the Location of this shape.
 * @return a non-null location instance
 */
public Point getLocation() {
	return location.getCopy();
}

/**
 * Returns an array of IPropertyDescriptors for this shape.
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
	if (XPOS_PROP.equals(propertyId)) {
		return Integer.toString(location.x);
	}
	if (YPOS_PROP.equals(propertyId)) {
		return Integer.toString(location.y);
	}
	if (HEIGHT_PROP.equals(propertyId)) {
		return Integer.toString(size.height);
	}
	if (WIDTH_PROP.equals(propertyId)) {
		return Integer.toString(size.width);
	}
	return super.getPropertyValue(propertyId);
}

/**
 * Return the Size of this shape.
 * @return a non-null Dimension instance
 */
public Dimension getSize() {
	return size.getCopy();
}

/**
 * Set the Location of this shape.
 * @param newLocation a non-null Point instance
 * @throws IllegalArgumentException if the parameter is null
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
 * <p>The property view uses the IDs from the IPropertyDescriptors array to set the values
 * of the corresponding properties.</p>
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public void setPropertyValue(Object propertyId, Object value) {
	if (XPOS_PROP.equals(propertyId)) {
		int x = Integer.parseInt((String) value);
		setLocation(new Point(x, location.y));
	} else if (YPOS_PROP.equals(propertyId)) {
		int y = Integer.parseInt((String) value);
		setLocation(new Point(location.x, y));
	} else if (HEIGHT_PROP.equals(propertyId)) {
		int height = Integer.parseInt((String) value);
		setSize(new Dimension(size.width, height));
	} else if (WIDTH_PROP.equals(propertyId)) {
		int width = Integer.parseInt((String) value);
		setSize(new Dimension(width, size.height));
	} else {
		super.setPropertyValue(propertyId, value);
	}
}

/**
 * Set the Size of this shape.
 * Will not modify the size if newSize is null.
 * @param newSize a non-null Dimension instance or null
 */
public void setSize(Dimension newSize) {
	if (newSize != null) {
		size.setSize(newSize);
		firePropertyChange(SIZE_PROP, null, size);
	}
}
}