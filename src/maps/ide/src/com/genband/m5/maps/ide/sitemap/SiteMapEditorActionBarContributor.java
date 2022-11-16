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
*     File:     SiteMapEditorActionBarContributor.java
*
*     Desc:   	Contributes actions to a toolbar.
*
*     Author         Date                    Description
*    ---------------------------------------------------------
*	  Genband        December 28, 2007       Initial Creation
*
**********************************************************************
*/

package com.genband.m5.maps.ide.sitemap;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionFactory;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;

/**
 * Contributes actions to a toolbar.
 * This class is tied to the editor in the definition of editor-extension (see plugin.xml).
 * @author Genband
 */
public class SiteMapEditorActionBarContributor extends ActionBarContributor {

/**
 * Create actions managed by this contributor.
 * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
 */
protected void buildActions() {
	addRetargetAction(new DeleteRetargetAction());
	addRetargetAction(new UndoRetargetAction());
	addRetargetAction(new RedoRetargetAction());
}

/**
 * Add actions to the given toolbar.
 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
 */
public void contributeToToolBar(IToolBarManager toolBarManager) {
	toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
	toolBarManager.add(getAction(ActionFactory.REDO.getId()));
}

/*
 * (non-Javadoc)
 * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
 */
protected void declareGlobalActionKeys() {
	// currently none
}

}