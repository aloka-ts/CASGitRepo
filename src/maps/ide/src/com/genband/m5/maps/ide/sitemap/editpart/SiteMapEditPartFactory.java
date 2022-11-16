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
* "Copyright 2008 GENBAND, Inc.  All rights reserved."
**********************************************************************
**/


/**********************************************************************
*
*     Project:  MAPS
*
*     Package:  com.genband.m5.maps.ide.sitemap.editpart
*
*     File:     SiteMapEditPartFactory.java
*
*     Desc:   	Factory that maps model elements to edit parts.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   January 8, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.genband.m5.maps.ide.sitemap.model.HeaderFooter;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PageInnerGroup;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;

/**
 * Factory that maps model elements to edit parts.
 * @author Genband
 */
public class SiteMapEditPartFactory implements EditPartFactory {

/**
 * (non-Javadoc)
 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
 */
public EditPart createEditPart(EditPart context, Object modelElement) {
	// get EditPart for model element
	EditPart part = getPartForElement(modelElement);
	// store model element in EditPart
	part.setModel(modelElement);
	return part;
}

/**
 * Maps an object to an EditPart. 
 * @throws RuntimeException if no match was found (programming error)
 */
private EditPart getPartForElement(Object modelElement) {
	if (modelElement instanceof SiteMap) {
		return new SiteMapEditPart();
	}
	if (modelElement instanceof HeaderFooter) {
		return new HeaderFooterEditPart();
	}
	if (modelElement instanceof MainPage) {
		return new PageEditPart();
	}if (modelElement instanceof SubPage) {
		System.out.println("In factory: Created the SubPageEditPart instance");
		return new SubPageEditPart();
	}if (modelElement instanceof PageInnerGroup) {
		return new PageInnerGroupEditPart();
	}if (modelElement instanceof PlaceHolder) {
		PlaceHolderEditPart placeHolderEditPart = new PlaceHolderEditPart();
		//placeHolderEditPart.setParent(context);
		System.out.println("In factory: Created the placeHolderEditPart instance");
		return placeHolderEditPart;
	}if (modelElement instanceof Portlet) {
		return new PortletEditPart();
	}if (modelElement instanceof Shape) {
		return new ShapesEditPart();
	}
	
	throw new RuntimeException(
			"Can't create part for model element: "
			+ ((modelElement != null) ? modelElement.getClass().getName() : "null"));
}

}