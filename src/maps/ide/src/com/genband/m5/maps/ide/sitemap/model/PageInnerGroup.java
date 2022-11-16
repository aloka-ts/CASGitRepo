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
*     File:     PageInnerGroup.java
*
*     Desc:   	Abstract prototype of PageChildGroup and PageContentGroup.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model;

import org.eclipse.swt.graphics.Image;
/**
 * Abstract prototype of PageChildGroup or PageContentGroup.
 * Use subclasses(PageChildGroup.java or PageContentGroup.java) to instantiate.
 * @author Genband
 */
public abstract class PageInnerGroup extends ModelElement {

protected static Image createImage(String name) {
	return null;//CPFPlugin.getDefault().getImageRegistry().get(name);
}

/**
 * Return a pictogram (small icon) describing this model element.
 * Children should override this method and return an appropriate Image.
 * @return a 16x16 Image or null
 */
public abstract Image getIcon();

}