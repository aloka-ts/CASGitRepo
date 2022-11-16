package com.genband.m5.maps.ide.sitemap.model.commands;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;

import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;

/**
 * A command to remove a portlet
 * The command can be undone or redone.
 * @author Genband 
 */
public class PortletDeleteCommand extends Command {
/** Portlet to remove. */
private final Portlet child;

/** PlaceHolder to remove from. */
private final PlaceHolder parent;
/** True, if Portlet was removed from its PlaceHolder. */
private boolean wasRemoved;

/**
 * Create a command that will remove the Portlet .
 * @param parent the PlaceHolder containing the Portlet
 * @param child    the Portlet to remove
 * @throws IllegalArgumentException if any parameter is null
 */
public PortletDeleteCommand(PlaceHolder parent, Portlet child) {
	if (parent == null || child == null) {
		throw new IllegalArgumentException();
	}
	setLabel("Portlet deletion");
	this.parent = parent;
	this.child = child;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#canUndo()
 */
public boolean canUndo() {
	return wasRemoved;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	redo();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	// remove the child and disconnect its connections
	wasRemoved = parent.removePortlet(child);
}


/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	// add the child and reconnect its connections
	parent.addPortlet(child);
}
}