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
*     File:     DisplaySubPageFigure.java
*
*     Desc:   	Figure  which is basically nothing but a tab's upper portion
*     			(similar to button).
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

import java.util.List;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class DisplaySubPageFigure extends BasicPageFigure {
	
	int parentPageNo = Constants.INVALID;
	
	public DisplaySubPageFigure() {
		super();
		System.out.print("in constructor of DisplaySubPageFigure");
		setBounds(new Rectangle(9,9,100,100));
		}
		
	@Override
	public void paintFigure(Graphics graphics) {
	
		System.out.println("\n DisplaySubPageFigure: paintFigure() entered ");
		/*
		//get parent's(displayPage's) bounds.So that we can adjust it accordingly.
		Rectangle displayPageBounds = getParent().getBounds();
		System.out.println("parent is : " + getParent());
		Rectangle pageChildGroupBounds = null;
		int x = 0;
		int y = 0 ;
		
		int width = 0;
		int height = 0;
		int fontHeight = graphics.getFontMetrics().getHeight();
		int fontWidth = graphics.getFontMetrics().getAverageCharWidth();
		int textAdjustment = 2* fontWidth;
		*/int tabHeight = 0;
		int tabWidth = 0;
		int tabX = 0;
		int tabY = 0;
		int firstTabX = 0;
		int xMargin = 0;
		//int lineWidth = displayPageBounds.height/150;
		List<IFigure> siblingFigures = getParent().getChildren() ;
		System.out.println("pageNo is : " + getPageNo() + " name is : " + getPageName());
		int flag = 0;
		
		for (int i = 0; i< siblingFigures.size(); i++){
			//if ( siblingFigures.get(i) instanceof PageChildGroupFigure ) {
			//	pageChildGroupBounds = ((PageChildGroupFigure)siblingFigures.get(i)).getBounds();
			//}else 
			if ( siblingFigures.get(i) instanceof SubPageFigure 
					&&  ((SubPageFigure)siblingFigures.get(i)).getPageNo() == getPageNo()) {
				tabHeight =  ((SubPageFigure)siblingFigures.get(i)).getBounds().height ;
				tabWidth =  ((SubPageFigure)siblingFigures.get(i)).getBounds().width ;
				tabY =  ((SubPageFigure)siblingFigures.get(i)).getBounds().y ;
				tabX =	((SubPageFigure)siblingFigures.get(i)).getBounds().x;
				
				flag = 1;
			}if ( siblingFigures.get(i) instanceof SubPageFigure 
					&&  ( 1 == ((SubPageFigure)siblingFigures.get(i)).getPageNo()) ) {
				firstTabX =  ((SubPageFigure)siblingFigures.get(i)).getBounds().x ;
			}
		}
		/*if ( 0 == flag ) {
			System.out.println("DisplaySubPageFigure: setting bounds to 10 and exiting");
			setBounds(new Rectangle(10,10,10,10));
			
			return;
		}
		x = firstTabX;
		//x = tabX;
		if ( 1 != getPageNo() ){
			x = x - 2 ;
		}
		xMargin = displayPageBounds.width/Constants.DISPLAYPAGE_SUBPAGE_X_MARGIN_RATIO;
		
		width = pageChildGroupBounds.width - 2*xMargin;
		y =  tabY +tabHeight + 1;
		height = (int)((double)displayPageBounds.height/Constants.DISPLAYPAGE_DISPLAYSUBPAGE_HEIGHT_RATIO);
		//height = displayPageBounds.height ;
		bounds = new Rectangle(x,y,width,height);
		*/
		bounds = ((DisplayPageFigure)getParent()).getDisplaySubPageBounds();
		int x = bounds.x;
		int y = bounds.y;
		int width = bounds.width;
		int height = bounds.height;
		setBounds(bounds);
		width = width - 2 ;
		//graphics.setBackgroundColor(ColorConstants.black);
		//graphics.fillRectangle(bounds);
		
		//if(Constants.NORMAL == state){
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x, y, x+width+1 , y);
			
			int startR = 252;
			int startG = 252;
			int startB = 254;
			int endR = 244;
			int endG = 243;
			int endB = 238;
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			if ( 6 == Constants.DISPLAY_PAGE_SHADING ) {
				startB = 225;
				endB = endB - 30;
			}
			//dark gray
			if(2== Constants.DISPLAY_PAGE_SHADING){
				startR = 230;
				startG = 230;
				startB = 230;
				endR = 215;
				endG = 215;
				endB = 210;
					
			}
			
			//light gray
			if ( 1 == Constants.DISPLAY_PAGE_SHADING ) {
				startR = 250;
				startG = 250;
				startB = 250;
				endR = 235;
				endG = 235;
				endB = 230;
					
			}
			/*int startR = 244;
			int startG = 243;
			int startB = 238;
			int endR = 220;
			int endG = 220;
			int endB = 220;
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			*/
			//startB = 225;
			//endB = endB - 30;
			double gradientR = (double)(startR - endR)/height;
			double gradientG = (double)(startG - endG)/height;
			double gradientB = (double)(startB - endB)/height;
			//int gradientG = startG - endG;
			//int gradientB = startB - endB;
			int adjustment = 2;
			height = height - 1 - 2 - adjustment;
			int i = 0 ;
			for ( i = 0 ; i < height ; i++ ){
				currentR = startR - (int)(gradientR*i);
				currentG = startG - (int)(gradientG*i);
				currentB = startB - (int)(gradientB*i);
				graphics.setForegroundColor(new Color(null,currentR,currentG,currentB));
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
			}
			//draw 3 white(almost) lines(boundries)
			graphics.setForegroundColor(new Color(null,252,252,254));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x+1, y+1, x+1 , y+i+1);// left vertical
			graphics.drawLine(x+width-2, y+1, x+width-2 , y+i+1);//right vertical
			
			//draw actual(Visible) boundary of tab
			i++;
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x, y, x , y+i+1);// left vertical
			graphics.drawLine(x+width-1, y, x+width-1 , y+i+1);//right vertical
			
			//draw shadow
			i++;
			graphics.setForegroundColor(new Color(null,208,206,191));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x+width, y+1, x+width , y+i+1);//right vertical
			
			//draw light shadow
			i++;
			graphics.setForegroundColor(new Color(null,227,224,208));
			graphics.drawLine(x, y+i+1, x + width , y+i+1);//bottom horizontal
			graphics.drawLine(x+width+1, y, x+width+1 , y+i+1);//right vertical
			//2 dots of light shadow color at top right
			graphics.setForegroundColor(new Color(null,227,224,208));
			graphics.drawLine(x+width+2 , y,x+width+3,y);
			
			//draw white line on the tab selected
			graphics.setForegroundColor(new Color(null,252,252,254));
			//graphics.setForegroundColor(new Color(null,230,230,230));
			if ( 2 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setForegroundColor(new Color(null,230,230,230));
			} else if ( 1 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setForegroundColor(new Color(null,250,250,250));
			} else if ( 6 == Constants.DISPLAY_PAGE_SHADING ) {
				graphics.setForegroundColor(new Color(null,252,252,254-30));
			}
			
			//TODO draw white line
			graphics.drawLine(tabX+1 , y,tabX+tabWidth-3,y);
			
			
			//draw vertical boundaries
			//graphics.setForegroundColor(new Color(null,145,167,180));
			//graphics.drawLine(x, y, x, y + height+1+2+adjustment);
			//graphics.drawLine(x+width-1, y, x+width-1, y + height+1+2+adjustment);
			//graphics.drawText(pageName, x, y + height/2-fontHeight/2);
		/*}else if ( Constants.SELECTED == state || Constants.HOVER == state) {
			graphics.setForegroundColor(new Color(null,230,139,44));
			graphics.drawLine(x, y, x+width, y);
			graphics.setForegroundColor(new Color(null,255,199,60));
			graphics.drawLine(x, y+1, x+width, y+1);
			//graphics.setBackgroundColor(new Color(null,255,255,225));
			graphics.setBackgroundColor(ColorConstants.white);
			graphics.fillRectangle(x,y+2,width-1,height-2);
			if ( Constants.HOVER == state ){
			//	graphics.fillRectangle(x,y+2,width-1,height-2);
			//}else{
				graphics.setForegroundColor(new Color(null,236,235,230));
				int i = height-3;
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
				i++;
				graphics.setForegroundColor(new Color(null,145,155,156));
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
				
			}
			//Vertical Lines
			graphics.setForegroundColor(new Color(null,145,155,156));
			graphics.drawLine(x,y,x,y + height);
			graphics.drawLine(x+width-1,y,x+width-1,y+height);
			//graphics.drawText(pageName, x+2, y + height/2-fontHeight/2);
			graphics.drawText(pageName, x , y + height/2-fontHeight/2);
	
		}*/
		System.out.println("\n DisplaySubPageFigure: paintFigure() exitiing ");
	}
	
	@Override
	protected void paintChildren(Graphics graphics) {
		System.out.println("in paintChildern of DisplaySubPageFigure");
		IFigure child;

		Rectangle clip = Rectangle.SINGLETON;
		for (int i = 0; i < getChildren().size(); i++) {
			child = (IFigure)getChildren().get(i);
			//System.out.println("child.intersects(graphics.getClip(clip)) : " + child.intersects(graphics.getClip(clip)));
			//System.out.println("graphics.getClip(clip)) : " + graphics.getClip(clip));
			//System.out.println("child.getBounds() : " + child.getBounds());
			child.setBounds(graphics.getClip(clip));
			//System.out.println("child.getBounds() : " + child.getBounds());
			if (child.isVisible() && child.intersects(graphics.getClip(clip))) {
				graphics.clipRect(child.getBounds());
				child.paint(graphics);
				graphics.restoreState();
			}
		}
	}

	
	public int getParentPageNo() {
		return parentPageNo;
	}
	public void setParentPageNo(int parentPageNo) {
		this.parentPageNo = parentPageNo;
	}
	
}
