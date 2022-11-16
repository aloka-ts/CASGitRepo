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
*     Package:  com.genband.m5.maps.ide.sitemap.model.commands
*
*     File:     PageDeleteCommand.java
*
*     Desc:   	Command to delete a Page from Site Map.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/
package  com.genband.m5.maps.ide.sitemap.model.commands;

import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * A command to remove a page from its parent.
 * The command can be undone or redone.
 * @author Genband
 */
public class PageDeleteCommand extends Command {
/** Page to remove. */
private final MainPage page;

/** SiteMap to remove from. */
private final SiteMap parent;
/** True, if page was removed from its parent. */
private boolean wasRemoved;

private int pageRemoved = -2;
/**
 * Create a command that will remove the page from its parent.
 * @param parent the SiteMap containing the child
 * @param child    the Page to remove
 * @throws IllegalArgumentException if any parameter is null
 */
public PageDeleteCommand(SiteMap parent, MainPage page) {
	if (parent == null || page == null) {
		throw new IllegalArgumentException();
	}
	setLabel("Page deletion");
	this.parent = parent;
	this.page = page;
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
	CPFPlugin.getDefault().log("Page delete Command execution started");
	CPFPlugin.getDefault().log("page no of page to be deleted: "+ page.getPageNo());
	Shell shell = CPFPlugin.getDefault().getWorkbench()
	.getActiveWorkbenchWindow().getShell();
	
	boolean status = MessageDialog.openConfirm(shell, "Confim Delete", "Do you want to delete " + page.getName() + " page?");
	
	if ( true == status ) {
		pageRemoved = page.getPageNo();
		parent.setPageRemoved(pageRemoved);
		//page.setLastpageDeleted(pageRemoved);
		wasRemoved = parent.removePage(page);
		CPFPlugin.getDefault().log("Page delete Command execution completed");
	}else{
		CPFPlugin.getDefault().log("Page delete Command execution completed \n but page is not deleted because user didn't confirm it.");
	}
}

/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#redo()
 */
public void redo() {
	// remove the page
	pageRemoved = page.getPageNo();
	parent.setPageRemoved(pageRemoved);
	//page.setLastpageDeleted(pageRemoved);
	wasRemoved = parent.removePage(page);

}


/* (non-Javadoc)
 * @see org.eclipse.gef.commands.Command#undo()
 */
public void undo() {
	// add a page
	pageRemoved = Constants.INVALID;
	parent.addPage(page);

}
}