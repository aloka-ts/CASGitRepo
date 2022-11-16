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
*     Package:  com.genband.m5.maps.siteMap.model
*
*     File:     PageTreeEditPart.java
*
*     Desc:   	Tree EditPart used in outline.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.graphics.Image;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentDeleteEditPolicy;
import com.genband.m5.maps.ide.sitemap.model.HeaderFooter;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.Shape;

/**
 * TreeEditPart used for Page instances.
 * This is used in the Outline View of the SiteMapEditor.
 * This edit part must implement the PropertyChangeListener interface, 
 * so that it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */
class PageTreeEditPart extends AbstractTreeEditPart implements
		PropertyChangeListener {

/**
 * Create a new instance of this edit part using the given model element.
 * @param model a non-null page instance
 */
PageTreeEditPart(Page model) {
	super(model);
}

/**
 * Upon activation, attach to the model element as a property change listener.
 */
public void activate() {
	if (!isActive()) {
		super.activate();
		((ModelElement) getModel()).addPropertyChangeListener(this);
	}
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// allow removal of the associated model element
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentDeleteEditPolicy());
}

/**
 * Upon deactivation, detach from the model element as a property change listener.
 */
public void deactivate() {
	if (isActive()) {
		super.deactivate();
		((ModelElement) getModel()).removePropertyChangeListener(this);
	}
}

private Page getCastedModel() {
	return (Page) getModel();
}

/** (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
 */
protected Image getImage() {
	return getCastedModel().getIcon();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
 */
protected String getText() {
	return getCastedModel().toString();
}

/* (non-Javadoc)
 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
 */
public void propertyChange(PropertyChangeEvent evt) {
	refreshVisuals(); // this will cause an invocation of getImage() and getText(), see below
}
}