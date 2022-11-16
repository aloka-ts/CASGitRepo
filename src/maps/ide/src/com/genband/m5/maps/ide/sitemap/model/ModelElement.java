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
*     File:     ModelElement.java
*
*     Desc:   	Abstract prototype of a model element.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
 
/**
 * Abstract prototype of a model element.
 * This class provides features necessary for all model elements, like:
 * 
 * property-change support (used to notify edit parts of model changes),
 * property-source support (used to display property values in the Property View) and
 * serialization support (the model hierarchy must be serializable, so that the editor can
 * save and restore a binary representation).
 */
public abstract class ModelElement implements IPropertySource, Serializable {
/** An empty property descriptor. */
private static final IPropertyDescriptor[] EMPTY_ARRAY = new IPropertyDescriptor[0];

private static final long serialVersionUID = 1;
/** Delegate used to implemenent property-change-support. */
private transient PropertyChangeSupport pcsDelegate = new PropertyChangeSupport(this);

protected String roles = "Role1";

/** 
 * Attach a non-null PropertyChangeListener to this object.
 * @param l a non-null PropertyChangeListener instance
 * @throws IllegalArgumentException if the parameter is null
 */
public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
	if (l == null) {
		throw new IllegalArgumentException();
	}
	pcsDelegate.addPropertyChangeListener(l);
}

/** 
 * Report a property change to registered listeners (for example edit parts).
 * @param property the programmatic name of the property that changed
 * @param oldValue the old value of this property
 * @param newValue the new value of this property
 */
protected void firePropertyChange(String property, Object oldValue, Object newValue) {
	if (pcsDelegate.hasListeners(property)) {
		pcsDelegate.firePropertyChange(property, oldValue, newValue);
	}
}

/**
 * Returns a value for this property source that can be edited in a property sheet.
 * model elements should return themselves and 
 * custom IPropertySource implementations should return an editable value.</li>
 * Override only if necessary.
 * @return this instance
 */
public Object getEditableValue() {
	return this;
}

/**
 * Children should override this. The default implementation returns an empty array.
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	return EMPTY_ARRAY;
}

/**
 * Children should override this. The default implementation returns null.
 */
public Object getPropertyValue(Object id) {
	return null;
}

/**
 * Children should override this. The default implementation returns false.
 */
public boolean isPropertySet(Object id) {
	return false;
}

/** 
 * Deserialization constructor. Initializes transient fields.
 * @see java.io.Serializable
 */
private void readObject(ObjectInputStream in) 
throws IOException, ClassNotFoundException {
	in.defaultReadObject();
	pcsDelegate = new PropertyChangeSupport(this);
}

/** 
 * Remove a PropertyChangeListener from this component.
 * @param l a PropertyChangeListener instance
 */
public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
	if (l != null) {
		pcsDelegate.removePropertyChangeListener(l);
	}
}

/**
 * Children should override this. The default implementation does nothing.
 */
public void resetPropertyValue(Object id) {
	// do nothing
}

/**
 * Children should override this. The default implementation does nothing.
 */
public void setPropertyValue(Object id, Object value) {
	// do nothing
}

public String getRoles() {
	return roles;
}

public void setRoles(String roles) {
	this.roles = roles;
}
}
