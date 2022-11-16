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
*     File:     Footer.java
*
*     Desc:   	Model for the footer.
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
 *Model for the footer
 * @author Genband 
 */
public class Footer extends HeaderFooter {
/** A 16x16 pictogram of footer. */
private static final Image FOOTER_ICON = createImage("footer16.bmp");


public Image getIcon() {
	return FOOTER_ICON;
}

public String toString() {
	return "Footer " + hashCode();
}
}
