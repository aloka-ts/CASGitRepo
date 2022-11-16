/**********************************************************************
*	 GENBAND,  Inc. Confidential and Proprietary
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
*     Package:  com.genband.m5.maps.ide.sitemap.editpolicy
*
*     File:     ComponentSelectionEditPolicy.java
*
*     Desc:   	This is the edit policy which helps in changing the figures 
*     			on mouse events(Mouse hover ).
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.editpolicy;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;

import com.genband.m5.maps.ide.sitemap.figure.BasicFigure;
import com.genband.m5.maps.ide.sitemap.figure.FooterFigure;
import com.genband.m5.maps.ide.sitemap.figure.HeaderFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure3;
import com.genband.m5.maps.ide.sitemap.util.Constants;

public class ComponentSelectionEditPolicy extends SelectionEditPolicy{

@Override
public void showSourceFeedback(Request request) {
	// TODO Auto-generated method stub
	//super.showSourceFeedback(request);
	if(getHostFigure() instanceof SiteMapFigure3){
		SiteMapFigure3 figure = (SiteMapFigure3)getHostFigure();
		figure.setFGColor(ColorConstants.green);
		figure.repaint();
	}else if(getHostFigure() instanceof BasicFigure){
		BasicFigure figure = (BasicFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState() && Constants.CHILD_SELECTED != figure.getState()){
			figure.setState(Constants.HOVER);
			figure.repaint();
		}
	}else if(getHostFigure() instanceof SiteMapFigure){
		SiteMapFigure figure = (SiteMapFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState()){
			figure.setState(Constants.HOVER);
			figure.repaint();
		}
	}
	
	
}

@Override
public void eraseSourceFeedback(Request request) {
	// TODO Auto-generated method stub
	//super.eraseSourceFeedback(request);
	if(getHostFigure() instanceof SiteMapFigure3){
		SiteMapFigure3 figure = (SiteMapFigure3)getHostFigure();
		figure.setFGColor(ColorConstants.black);
		figure.repaint();
	}else if(getHostFigure() instanceof BasicFigure){
		BasicFigure figure = (BasicFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState() && Constants.CHILD_SELECTED != figure.getState()){
			figure.setState(Constants.NORMAL);
			figure.repaint();
		}
	}else if(getHostFigure() instanceof SiteMapFigure){
		SiteMapFigure figure = (SiteMapFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState()){
			figure.setState(Constants.NORMAL);
			figure.repaint();
		}
	}
	
}
@Override
public void showTargetFeedback(Request request) {
	// TODO Auto-generated method stub
	//super.showTargetFeedback(request);
	if(getHostFigure() instanceof SiteMapFigure3){
		SiteMapFigure3 figure = (SiteMapFigure3)getHostFigure();
		figure.setFGColor(ColorConstants.green);
		figure.repaint();
	}else if(getHostFigure() instanceof BasicFigure){
		BasicFigure figure = (BasicFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState() && Constants.CHILD_SELECTED != figure.getState()){
			figure.setState(Constants.HOVER);
			figure.repaint();
		}
	}else if(getHostFigure() instanceof SiteMapFigure){
		SiteMapFigure figure = (SiteMapFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState()){
			figure.setState(Constants.HOVER);
			figure.repaint();
		}
	}

}
@Override
public void eraseTargetFeedback(Request request) {
	// TODO Auto-generated method stub
	//super.eraseTargetFeedback(request);
	if(getHostFigure() instanceof SiteMapFigure3){
		SiteMapFigure3 figure = (SiteMapFigure3)getHostFigure();
		figure.setFGColor(ColorConstants.black);
		figure.repaint();
	}else if(getHostFigure() instanceof BasicFigure){
		BasicFigure figure = (BasicFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState() && Constants.CHILD_SELECTED != figure.getState()){
			figure.setState(Constants.NORMAL);
			figure.repaint();
		}
	}else if(getHostFigure() instanceof SiteMapFigure){
		SiteMapFigure figure = (SiteMapFigure)getHostFigure();
		if(Constants.SELECTED != figure.getState()){
			figure.setState(Constants.NORMAL);
			figure.repaint();
		}
	}
	
}
	@Override
	protected void hideSelection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void showSelection() {
		// TODO Auto-generated method stub
		
	}

}
