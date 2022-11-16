package com.genband.m5.maps.ide.sitemap.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.AbstractTreeEditPart;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.swt.graphics.Image;

import com.genband.m5.maps.ide.sitemap.editpolicy.SiteMapComponentEditPolicy;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;

/**
 * TreeEditPart for a SiteMap instance.
 * This is used in the Outline View of the SiteMapEditor.
 * <p>This edit part must implement the PropertyChangeListener interface, 
 * so it can be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Genband
 */
class DiagramTreeEditPart extends AbstractTreeEditPart
	implements PropertyChangeListener {

/** 
 * Create a new instance of this edit part using the given model element.
 * @param model a non-null SiteMap instance
 */
DiagramTreeEditPart(SiteMap model) {
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
 * @see com.genband.m5.maps.ide.sitemap.editpart.ShapeTreeEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// If this editpart is the root content of the viewer, then disallow removal
	if (getParent() instanceof RootEditPart) {
		//installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new SiteMapComponentEditPolicy());
	}
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

private SiteMap getCastedModel() {
	return (SiteMap) getModel();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getImage()
 */
protected Image getImage() {
	return null;//getCastedModel().getIcon();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractTreeEditPart#getText()
 */
protected String getText() {
	return getCastedModel().toString();
}

/**
 * Convenience method that returns the EditPart corresponding to a given child.
 * @param child a model element instance
 * @return the corresponding EditPart or null
 */
private EditPart getEditPartForChild(Object child) {
	return (EditPart) getViewer().getEditPartRegistry().get(child);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
 */
protected List getModelChildren() {
	return getCastedModel().getChildren(); // a list of shapes
}

/* (non-Javadoc)
* @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
*/
public void propertyChange(PropertyChangeEvent evt) {
	String prop = evt.getPropertyName();
	if (SiteMap.CHILD_ADDED_PROP.equals(prop)) {
		// add a child to this edit part
		// causes an additional entry to appear in the tree of the outline view
		addChild(createChild(evt.getNewValue()), -1);
	} else if (SiteMap.PAGE_ADDED_PROP.equals(prop)) {
		// add a child to this edit part
		// causes an additional entry to appear in the tree of the outline view
		addChild(createChild(evt.getNewValue()), -1);
	}else if (SiteMap.CHILD_REMOVED_PROP.equals(prop)) {
		// remove a child from this edit part
		// causes the corresponding edit part to disappear from the tree in the outline view
		removeChild(getEditPartForChild(evt.getNewValue()));
	}else if (SiteMap.PAGE_REMOVED_PROP.equals(prop)) {
		// remove a child from this edit part
		// causes the corresponding edit part to disappear from the tree in the outline view
		removeChild(getEditPartForChild(evt.getNewValue()));
	} else if(SiteMap.SITEMAP_LAYOUT_PROP.equals(prop)){
		System.out.println("DiagramTReeEditPart: layout changed ");
	}else{
		refreshVisuals();
	}
}
}
