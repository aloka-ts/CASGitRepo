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
*     Package:  com.genband.m5.maps.ide.sitemap
*
*     File:     ShapesCreateCommand.java
*
*     Desc:   	Command to add a Shape to Site Map.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model.commands;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.commands.Command;

import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;

/**
 * A command to add a Shape to Site Map.
 * The command can be undone or redone.
 * @author Genband
 */
public class ShapeCreateCommand 
	extends Command 
{
	
/** The new shape. */ 
private Shape newShape;
/** ShapeDiagram to add to. */
private final SiteMap parent;
/** The bounds of the new Shape. */
private Rectangle bounds;

/**
 * Create a command that will add a new Shape to a SiteMap.
 * @param newShape the new Shape that is to be added
 * @param parent the SiteMap that will hold the new element
 * @param bounds the bounds of the new shape; the size can be (-1, -1) if not known
 * @throws IllegalArgumentException if any parameter is null, or the request
 * 						  does not provide a new Shape instance
 */
public ShapeCreateCommand(Shape newShape, SiteMap parent, Rectangle bounds) {
	this.newShape = newShape;
	this.parent = parent;
	this.bounds = bounds;
	setLabel("shape creation");
}

/**
 * Can execute if all the necessary information has been provided. 
 * @see org.eclipse.gef.commands.Command#canExecute()
 */
public boolean canExecute() {
	return newShape != null && parent != null && bounds != null;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#execute()
 */
public void execute() {
	newShape.setLocation(bounds.getLocation());
	Dimension size = bounds.getSize();
	if (size.width > 0 && size.height > 0)
		newShape.setSize(size);
	redo();
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	System.out.println(" parent is : " + parent);
	parent.addChild(newShape);
	//PageShape p = new PageShape();
	
	//parent.addChild(p);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	parent.removeChild(newShape);
}
	
}