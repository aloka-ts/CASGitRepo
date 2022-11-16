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
*     Package:  com.genband.m5.maps.ide.sitemap.editpart
*
*     File:     PageInnerGroupEditPart.java
*
*     Desc:   	EditPart (Controller) for PageContentGroup and PageChildGroup both.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.gmf.runtime.diagram.ui.figures.ResizableCompartmentFigure;

import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentDeleteEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.ComponentSelectionEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.HeaderFooterXYLayoutEditPolicy;
import com.genband.m5.maps.ide.sitemap.editpolicy.PageInnerGroupXYLayoutEditPolicy;
import com.genband.m5.maps.ide.sitemap.figure.BasicFigure;
import com.genband.m5.maps.ide.sitemap.figure.BasicPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.DisplayPageFigure;
import com.genband.m5.maps.ide.sitemap.figure.FooterFigure;
import com.genband.m5.maps.ide.sitemap.figure.HeaderFigure;
import com.genband.m5.maps.ide.sitemap.figure.PageChildGroupFigure;
import com.genband.m5.maps.ide.sitemap.figure.PageContentGroupFigure;
import com.genband.m5.maps.ide.sitemap.figure.PageFigure;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure2;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure3;
import com.genband.m5.maps.ide.sitemap.figure.SiteMapFigure5;
import com.genband.m5.maps.ide.sitemap.figure.TrailFigure;
import com.genband.m5.maps.ide.sitemap.figure.TrailTabFigure;
import com.genband.m5.maps.ide.sitemap.model.Footer;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.model.HeaderFooter;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.ModelElement;
import com.genband.m5.maps.ide.sitemap.model.PageChildGroup;
import com.genband.m5.maps.ide.sitemap.model.PageContentGroup;
import com.genband.m5.maps.ide.sitemap.model.PageInnerGroup;
import com.genband.m5.maps.ide.sitemap.model.PageShape;
import com.genband.m5.maps.ide.sitemap.model.PortletShape;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.util.Constants;

/**
 * EditPart used for PageContentGroup or PageChildGroup instances. 
 * @author Genband
 */
public class PageInnerGroupEditPart extends AbstractGraphicalEditPart {
	
/**
 * Upon activation, attach to the model element as a property change listener.
 */
public void activate() {
	if (!isActive()) {
		super.activate();
		//((ModelElement) getModel()).addPropertyChangeListener(this);
	}
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
	// allow removal of the associated model element
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentDeleteEditPolicy());
	installEditPolicy(EditPolicy.CONTAINER_ROLE, new PageInnerGroupXYLayoutEditPolicy());
	
	//installEditPolicy(EditPolicy.LAYOUT_ROLE,  new Shapes1XYLayoutEditPolicy());
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,  new ComponentSelectionEditPolicy());

	}
	
/*(non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
protected IFigure createFigure() {
	IFigure f = createFigureForModel();
	f.setLayoutManager(new FreeformLayout());
	f.setOpaque(true); // non-transparent figure
	return f;
}

/**
 * Return an IFigure depending on the instance of the current model element.
 * This allows this EditPart to be used for both subclasses of HeaderFooter. 
 */
private IFigure createFigureForModel() {
	if (getModel() instanceof PageContentGroup) {
		Figure figure = new PageContentGroupFigure();
		return figure;
	} else if (getModel() instanceof PageChildGroup) {
		Figure figure = new PageChildGroupFigure();
		return figure;
	} else {
		throw new IllegalArgumentException();
	}
}

/**
 * Upon deactivation, detach from the model element as a property change listener.
 */
public void deactivate() {
	if (isActive()) {
		super.deactivate();
		//((ModelElement) getModel()).removePropertyChangeListener(this);
	}
}

public PageInnerGroup getCastedModel() {
	return (PageInnerGroup) getModel();
}


protected void refreshVisuals() {
	int xmargin = 10 ;
	int ymargin = 20;
	int height = 30;
	System.out.println("PageInnerGroupEditPart: in refreshVisuals");
	//Rectangle bounds = new Rectangle(getCastedModel().getLocation(),getCastedModel().getSize());
	Rectangle bounds = new Rectangle(50,50,50,50);
	Rectangle displayPageBounds = ((GraphicalEditPart)getParent()).getFigure().getBounds();
	if(getCastedModel() instanceof PageChildGroup){
		//bounds = new Rectangle (x + xmargin,y+ ymargin,lx-2*xmargin,height);	
		bounds = new Rectangle (displayPageBounds.x + xmargin , displayPageBounds.y + ymargin ,
				displayPageBounds.width - 2*xmargin , displayPageBounds.height/20 ) ;	
	}else if (getCastedModel() instanceof PageContentGroup){
		//bounds = new Rectangle (20,500-30-30 ,580,30);
		//bounds = new Rectangle(x + xmargin, y+ ly -ymargin - height, lx-2*xmargin, height) ;	
		bounds = new Rectangle(displayPageBounds.x + xmargin , displayPageBounds.y + displayPageBounds.height - 2*ymargin ,
				displayPageBounds.width-2*xmargin , displayPageBounds.height/20 ) ;	

	}
	System.out.println("HeaderFooterEditPart: displayPageBounds: " + displayPageBounds);
	System.out.println("HeaderFooterEditPart: bounds: " + bounds);
	if ( displayPageBounds.height == 0 && displayPageBounds.width == 0 ){
		bounds = new Rectangle(20,20,0,0);
	}
	// notify parent container of changed position & location
	// if this line is removed, the XYLayoutManager used by the parent container 
	// (the Figure of the ShapesDiagramEditPart), will not know the bounds of this figure
	// and will not draw it correctly.
	System.out.println("getparent in refreshVisuals: " + getParent());
	((GraphicalEditPart) getParent()).setLayoutConstraint(this, getFigure(), bounds);
	
	
}
@Override
public void setSelected(int value) {
//	super.setSelected(value);
	if(SELECTED == value || SELECTED_PRIMARY == value){
		((BasicFigure)getFigure()).setState(Constants.SELECTED);
		getFigure().repaint();
	}else{
		((BasicFigure)getFigure()).setState(Constants.NORMAL);
		getFigure().repaint();
	}
}

public void performRequest(Request req) {
	System.out.print("action perform");
	super.performRequest(req);
	System.out.println("PageInnerGroupEditPart: req: type : " + req.getType());
	System.out.println("PageInnerGroupEditPart : req: class : " + req.getClass().getName());
	System.out.println("PageInnerGroupEditPart : req: extendedData : " + req.getExtendedData());
	if(req.getType().equals(REQ_DIRECT_EDIT)){
		System.out.println("PageInnerGroupEditPart: REQ_DIRECT_EDIT entered " );
		PageEditPart parent = (PageEditPart)getParent();
		if(parent.getCastedModel().isDummy()){
			//PageEditPart associatedPagePart = getDataPagePart(parent.getCastedModel().getPageNo());
			//MainPage associatedPage = associatedPagePart.getCastedModel();
			//PageFigure associatedPageFigure = (PageFigure)associatedPagePart.getFigure();
			MainPage displayPage = parent.getCastedModel();
			DisplayPageFigure displayPageFigure = (DisplayPageFigure)parent.getCastedFigure();
			int previousDisplayState = displayPageFigure.getDisplayState();
			if(getModel() instanceof PageChildGroup){
				if(previousDisplayState == Constants.BOTH_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CHILD_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CONTENT_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				} 			
			}else if (getModel() instanceof PageContentGroup){
				if(previousDisplayState == Constants.BOTH_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CHILD_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CONTENT_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				} 
			}
		}else{
			System.out.println("Error: PageInnerEditPart's paret is not displayPage.");
		}
		BasicFigure figure = (BasicFigure) getFigure();
		figure.setState(Constants.SELECTED);
		figure.repaint();
		
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("PageInnerGroupEditPart: REQ_DIRECT_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
	if(req.getType().equals(REQ_OPEN)){
		System.out.println("PageInnerGroupEditPart: REQ_OPEN_EDIT entered " );
		PageEditPart parent = (PageEditPart)getParent();
		if(parent.getCastedModel().isDummy()){
			//PageEditPart associatedPagePart = getDataPagePart(parent.getCastedModel().getPageNo());
			//MainPage associatedPage = associatedPagePart.getCastedModel();
			//PageFigure associatedPageFigure = (PageFigure)associatedPagePart.getFigure();
			MainPage displayPage = parent.getCastedModel();
			DisplayPageFigure displayPageFigure = (DisplayPageFigure)parent.getCastedFigure();
			int previousDisplayState = displayPageFigure.getDisplayState();
			if(getModel() instanceof PageChildGroup){
				if(previousDisplayState == Constants.BOTH_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CHILD_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CONTENT_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				} 			
			}else if (getModel() instanceof PageContentGroup){
				if(previousDisplayState == Constants.BOTH_VISIBLE){
					displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CHILD_VISIBLE){
					
					displayPage.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
					displayPageFigure.setDisplayState(Constants.PAGE_CONTENT_VISIBLE);
				}else if(previousDisplayState == Constants.PAGE_CONTENT_VISIBLE){
					List siblings = getParent().getChildren();
					for( int i = 0 ; i < siblings.size() ; i++ ){
						if(siblings.get(i) instanceof PageInnerGroupEditPart){
							if ( ((PageInnerGroupEditPart)siblings.get(i)).getModel() instanceof PageChildGroup ) {
								displayPage.setDisplayState(Constants.PAGE_CHILD_VISIBLE);
								displayPageFigure.setDisplayState(Constants.PAGE_CHILD_VISIBLE);	
							}
						}
					}
					
				} 
			}
		}else{
			System.out.println("Error: PageInnerEditPart's paret is not displayPage.");
		}
		
		//Color fgColor = ColorConstants.red;
		
		//figure.paint(f.graphics);
		//f.setFGColor(fgColor);
		//f.paintFigure(new SWTGraphics(new GC(new Shell())));
		System.out.println("PageInnerGroupEditPart: REQ_OPEN_EDIT exit \n\n" );
		//fireSelectionChanged();
	}
}
//helper function to get edit part of MainPage(data page and not displayPage) having the given pageNo.
private PageEditPart getDataPagePart(int pageNo){
	 PageEditPart dataPagePart = null;
	 List displayPageSiblingParts = getParent().getParent().getChildren();
	 for ( int i = 0 ; i < displayPageSiblingParts.size() ; i++ ) {
			if (displayPageSiblingParts.get(i) instanceof PageEditPart 
					&&( false == ((PageEditPart)displayPageSiblingParts.get(i)).getCastedModel().isDummy()) 
					&& pageNo == ((PageEditPart)displayPageSiblingParts.get(i)).getCastedModel().getPageNo()){
				dataPagePart = (PageEditPart) displayPageSiblingParts.get(i);
			}
		}
		return dataPagePart;
}

}
