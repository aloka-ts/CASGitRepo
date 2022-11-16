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
*     File:     PlaceHolderFigure.java
*
*     Desc:   	Figure  for placeHolder.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.genband.m5.maps.ide.sitemap.util.Constants;

public class PlaceHolderFigure extends BasicFigure {
	Rectangle displayPageBounds = new Rectangle(0,0,500,500);
	Color FGColor;
	Color BGColor;
	private int placeHolderNo = 1;
	private String placeHolderName = "Left";
	String layout = "Generic 2 Column";
	public PlaceHolderFigure() {
		System.out.print("in constructor of PlaceHolderFigure");
		FGColor = Constants.SELECTABLE_COLOR;
		state = Constants.NORMAL;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	@Override
	public void paintFigure(Graphics graphics) {
		
		System.out.println("paintFigure of PlaceHolderFigure entered ");
		
		//get parent's(Display page's) bounds.So that we can adjust it accordingly.
		System.out.println("\nIn paintFigure() of PlaceHolderFigure");
		if(getParent() instanceof SubPageFigure){
			setBounds(new Rectangle(0,0,0,0));
			return;
		}
		displayPageBounds = getParent().getBounds();
		Rectangle siteMapBounds = getParent().getParent().getBounds();
		int iconWidth = 16;
		int xMargin = siteMapBounds.width/Constants.PAGE_PLACEHOLDER_X_MARGIN_RATIO;
		int yMargin = siteMapBounds.height/Constants.PAGE_PLACEHOLDER_Y_MARGIN_RATIO;
		//Setting y and height of placeholder...it is same for every placeholder
		int y = displayPageBounds.y + yMargin;
		int height = displayPageBounds.height - 2*yMargin;
		//set x and width of placeholder.it will depend on layout and placeholder no. 
		int x = displayPageBounds.x;
		int width = 0;
		int totalWidth = displayPageBounds.width - 2*xMargin;
		Rectangle pageContentGroupBounds = null;
		boolean childExists = false;
		if(getParent() instanceof DisplayPageFigure){
			if ( Constants.PAGE_CHILD_VISIBLE == ((DisplayPageFigure)getParent()).getDisplayState() ) {
			
				List siblings = getParent().getChildren();
				for(int i = 0 ; i < siblings.size() ; i++ ){
					if (siblings.get(i) instanceof PageChildGroupFigure){
						childExists = true;
					}
				}
				if ( true == childExists ) {
					setBounds(new Rectangle(0,0,0,0));
					return;
				}
			}
			
			List siblings = getParent().getChildren();
			for(int i = 0 ; i < siblings.size() ; i++ ){
				if(siblings.get(i) instanceof PageContentGroupFigure){
					pageContentGroupBounds = ((BasicFigure)siblings.get(i)).getBounds();
					x = pageContentGroupBounds.x;
					y = pageContentGroupBounds.y + pageContentGroupBounds.height + yMargin;
					totalWidth = pageContentGroupBounds.width - 2*xMargin;
					height = ((DisplayPageFigure)getParent()).getPageContentGroupLastY() - y - 2*yMargin;
				}else if (siblings.get(i) instanceof PageChildGroupFigure){
					childExists = true;
				}
			}
		}
		System.out.println("IT layout of placeholder is: " + layout);
		//if ( false == childExists)
		if(layout.equals(Constants.LAYOUT_GENERIC_2_COLUMN) && (1 == placeHolderNo)){
			x = x + xMargin;
			width = (int)((double)totalWidth*Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1_TO_PLACEHOLDER2_RATIO);
		} else if(layout.equals(Constants.LAYOUT_GENERIC_2_COLUMN) && (2 == placeHolderNo)){
			int placeHolder1_width = (int)((double)totalWidth*Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1_TO_PLACEHOLDER2_RATIO);
			x = x + xMargin + placeHolder1_width;
			width = (int)((double)totalWidth*(1.0 - Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1_TO_PLACEHOLDER2_RATIO));
		} else if(layout.equals(Constants.LAYOUT_3_COLUMN) && (1 == placeHolderNo)){
			x = x + xMargin;
			width = (int)((double)totalWidth*.33);
		} else if(layout.equals(Constants.LAYOUT_3_COLUMN) && (2 == placeHolderNo)){
			width = (int)((double)totalWidth*.33);
			x = x + xMargin + width;
		}else if(layout.equals(Constants.LAYOUT_3_COLUMN) && (3 == placeHolderNo)){
			width = (int)((double)totalWidth*.33);
			x = x + xMargin + 2*width;
		}
		bounds = new Rectangle(x,y,width,height);
		setBounds(bounds);
		graphics.setLineStyle(SWT.LINE_DOT);
		if(Constants.NORMAL == state){
			graphics.setForegroundColor(Constants.SELECTABLE_COLOR);
		}else if(Constants.HOVER == state){
			graphics.setForegroundColor(Constants.HOVER_COLOR);	
		}else if(Constants.SELECTED == state){
			graphics.setForegroundColor(Constants.SELECTION_COLOR);	
		}
			
		graphics.drawRectangle(x,y,width-1,height-2);
	
				
		/*if(state == Constants.NORMAL){
			graphics.setForegroundColor(Constants.SELECTABLE_COLOR);
			graphics.drawRectangle(bounds);
		
		}
		graphics.setForegroundColor(FGColor);
		graphics.setBackgroundColor(ColorConstants.red);
		//bounds = new Rectangle(getBounds().x ,getBounds().y ,getBounds().width*2,getBounds().height*2);
		bounds = getParent().getBounds();
		setBounds(bounds);
		
		Rectangle b1 = new Rectangle(getBounds().x ,getBounds().y + 30,getBounds().width/2,getBounds().height/2);
		Rectangle b2 = new Rectangle(getBounds().x + 30,getBounds().y,20,20);
		graphics.fillRectangle(bounds);
		//graphics.fillRectangle(getBounds().x ,getBounds().y,getBounds().width,getBounds().height);
		//graphics.drawRoundRectangle(b1,4,4);
		//graphics.drawRoundRectangle(b2,4,4);
		
		//paintChildren(graphics);
		//Font font;
		//graphics.setFont(FontConstants.);
		graphics.setForegroundColor(ColorConstants.titleGradient);
		
		//graphics.drawText(getFilename(), siteMapBounds.x + 10, siteMapBounds.y - 6 );
		graphics.setForegroundColor(FGColor);
		
		System.out.println("paintFigure of PlaceHolderFigure exit \n\n\n");
		*/

		/*//get parent's(pages's) bounds.So that we can adjust it accordingly.
		System.out.println("\nIn paintFigure() of PlaceHolderFigure");
		siteMapBounds = getParent().getParent().getBounds();
		
		Rectangle  contentPaneBounds = null ;
		int iconWidth = 16;
		int x = 0;
		int y = 0;
		
		int width = 2*Constants.PAGE_TAB_ICON_MARGIN + iconWidth + Constants.PAGE_TEXT_TAB_MARGIN;
		int height = 0;
		int fontHeight = graphics.getFontMetrics().getHeight();
		int fontWidth = graphics.getFontMetrics().getAverageCharWidth();
		System.out.println("fontHeight = "+ fontHeight + "fontWidth = " + fontWidth);
		int lineWidth = siteMapBounds.height/150;
		List<IFigure> siblingFigures = getParent().getChildren() ;
		System.out.println("pageNo is : " + placeHolderNo + " name is : " + placeHolderName);
		for (int i = 0; i< siblingFigures.size(); i++){
			if (siblingFigures.get(i) instanceof DisplayPageFigure){
				contentPaneBounds = siblingFigures.get(i).getBounds();
			}
		}
		System.out.println("x = " + x);
		y = contentPaneBounds.y ;
		x = x + contentPaneBounds.x + 2;
		width = contentPaneBounds.width/2 -2 ;
		height = contentPaneBounds.height;
		bounds = new Rectangle(x,y,width+1,height);
		graphics.setBackgroundColor(ColorConstants.black);
		graphics.setForegroundColor(ColorConstants.blue);
		graphics.fillRectangle(getBounds().x ,getBounds().y,getBounds().width,getBounds().height);
		//setBounds(bounds);
		//graphics.setBackgroundColor(ColorConstants.black);
		//graphics.fillRectangle(bounds);
		
		/*if(Constants.NORMAL == state){
			y = y + 2 ;
			graphics.setForegroundColor(new Color(null,145,167,180));
			graphics.drawLine(x, y, x + width-1 , y);
			int startR = 255;
			int startG = 255;
			int startB = 255;
			int endR = 240;
			int endG = 240;
			int endB = 234;
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			//startB = 225;
			//endB = endB - 30;
			double gradientR = (double)(startR - endR)/height;
			double gradientG = (double)(startG - endG)/height;
			double gradientB = (double)(startB - endB)/height;
			//int gradientG = startG - endG;
			//int gradientB = startB - endB;
			int adjustment = 0;
			
			height = height - 1 - 2 - adjustment;
			int i = 0 ;
			for ( i = 0 ; i < height ; i++ ){
				currentR = startR - (int)(gradientR*i);
				currentG = startG - (int)(gradientG*i);
				currentB = startB - (int)(gradientB*i);
				graphics.setForegroundColor(new Color(null,currentR,currentG,currentB));
				graphics.drawLine(x, y+i+1, x + width , y+i+1);
			}
			graphics.setForegroundColor(new Color(null,236,235,230));
			//graphics.drawLine(x, y+i+1, x + width , y+i+1);
			i++;
			graphics.setForegroundColor(new Color(null,145,155,156));
			//graphics.drawLine(x, y+i+1, x + width , y+i+1);
			//draw vertical boundaries
			graphics.setForegroundColor(new Color(null,145,167,180));
			graphics.drawLine(x, y, x, y + height+1+2+adjustment);
			graphics.drawLine(x+width-1, y, x+width-1, y + height+1+2+adjustment);
			graphics.setForegroundColor(ColorConstants.black);
			graphics.drawText(placeHolderName, x+2*Constants.PAGE_TAB_ICON_MARGIN+iconWidth , y + height/2-fontHeight/2);
		}else if ( Constants.SELECTED == state || Constants.HOVER == state) {
			if ( Constants.HOVER == state ) {
				y = y+ 2 ;
			}
			if ( Constants.SELECTED == state ) {
				width = width + 4 ;
				x = x - 2 ;
				bounds = new Rectangle(x,y,width+1,height);
				setBounds(bounds);
					
			}
			//draw 2 orange shaded lines
			graphics.setForegroundColor(new Color(null,230,139,44));
			graphics.drawLine(x, y, x+width-1, y);
			graphics.setForegroundColor(new Color(null,255,199,60));
			graphics.drawLine(x, y+1, x+width-1, y+1);
			//graphics.setBackgroundColor(new Color(null,255,255,225));
			graphics.setBackgroundColor(new Color(null,252,252,254));
			graphics.fillRectangle(x,y+2,width-1,height-2);
		
			if ( Constants.HOVER == state ){
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
			//graphics.drawImage(pageIcon, x + Constants.PAGE_TAB_ICON_MARGIN, y + height/2-iconHeight/2 -2);
			graphics.setForegroundColor(ColorConstants.black);
			graphics.drawText(placeHolderName, x+2*Constants.PAGE_TAB_ICON_MARGIN+iconWidth , y + height/2-fontHeight/2 -2);
		}*/
	}
	
	@Override
	protected void paintChildren(Graphics graphics) {
		System.out.println("in paintChildern of placeholderFigure");
		IFigure child;

		Rectangle clip = Rectangle.SINGLETON;
		for (int i = 0; i < getChildren().size(); i++) {
			child = (IFigure)getChildren().get(i);
			System.out.println("vvvvvvvv : child.intersects(graphics.getClip(clip)) : " + child.intersects(graphics.getClip(clip)));
			System.out.println("graphics.getClip(clip)) : " + graphics.getClip(clip));
			System.out.println("child.getBounds() : " + child.getBounds());
			child.setBounds(graphics.getClip(clip));
			System.out.println("child.getBounds() : " + child.getBounds());
			if (child.isVisible() && child.intersects(graphics.getClip(clip))) {
				graphics.clipRect(child.getBounds());
				child.paint(graphics);
				graphics.restoreState();
			}
		}
	}
	private String getPageText() {
		return "Page";
	}
	
	public int getPlaceHolderNo(){
		return placeHolderNo;
	}
	
	public String getPlaceHolderName(){
		return placeHolderName;
	}
	
	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}
	public void setPlaceHolderName(String name) {
		placeHolderName  = name ;	
	}
	public void setPlaceHolderNo(int placeHolderNo) {
		this.placeHolderNo = placeHolderNo;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}
	
}
