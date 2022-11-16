package com.genband.m5.maps.ide.sitemap.model.commands;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;

import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;


/**
 * A command to remove a shape from its parent.
 * The command can be undone or redone.
 * @author Genband
 */
public class ShapeDeleteCommand extends Command {
/** Shape to remove. */
private final Shape child;

/** ShapeDiagram to remove from. */
private final SiteMap parent;
/** True, if child was removed from its parent. */
private boolean wasRemoved;

/**
 * Create a command that will remove the shape from its parent.
 * @param parent the SiteMap containing the child
 * @param child    the Shape to remove
 * @throws IllegalArgumentException if any parameter is null
 */
public ShapeDeleteCommand(SiteMap parent, Shape child) {
	if (parent == null || child == null) {
		throw new IllegalArgumentException();
	}
	setLabel("shape deletion");
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
	wasRemoved = parent.removeChild(child);
}


/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	// add the child and reconnect its connections
	parent.addChild(child);
}
}