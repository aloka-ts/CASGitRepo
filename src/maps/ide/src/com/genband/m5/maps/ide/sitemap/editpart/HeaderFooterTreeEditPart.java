package com.genband.m5.maps.ide.sitemap.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.graphics.Image;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractTreeEditPart;

import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentDeleteEditPolicy;
import com.genband.m5.maps.ide.sitemap.model.HeaderFooter;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.Shape;

/**
 * TreeEditPart used for HeaderFooter instances (more specific for Header and
 * Footer instances). This is used in the Outline View of the SiteMapEditor.
 * This edit part must implement the PropertyChangeListener interface, 
 * so that it can be notified of property changes in the corresponding model element.
 * 
 * @author Genband
 */
class HeaderFooterTreeEditPart extends AbstractTreeEditPart implements
		PropertyChangeListener {

/**
 * Create a new instance of this edit part using the given model element.
 * @param model a non-null header instance
 */
HeaderFooterTreeEditPart(HeaderFooter model) {
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

private HeaderFooter getCastedModel() {
	return (HeaderFooter) getModel();
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