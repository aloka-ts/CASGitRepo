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
*     Package:  com.genband.m5.maps.ide.sitemap.figure
*
*     File:     BasicFigure.java
*
*     Desc:   	This a figure which is the superclass of all figures
*     			except SiteMapFigure.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.util.Constants;

public class BasicFigure extends Figure{
	Rectangle bounds ;
	int state;
	public BasicFigure(){
		state = Constants.NORMAL;
		bounds = new Rectangle(10,10,10,10);
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	protected static Image createImage(String name) {
		return CPFPlugin.getDefault().getImageRegistry().get(name);
	}

}
