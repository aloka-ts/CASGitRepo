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
*     File:     PageChildGroup.java
*
*     Desc:   	Model for the PageChildGroup.
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
 *Model for the PageChildGroup
 * @author Genband
 */
public class PageChildGroup extends PageInnerGroup {
/** A 16x16 pictogram of a PageChildGroup.*/
private static final Image PAGE_CHILD_GROUP_ICON = createImage("pageChildGroup16.bmp");

public Image getIcon() {
	return PAGE_CHILD_GROUP_ICON;
}

public String toString() {
	return "SubPages " + hashCode();
}
}
