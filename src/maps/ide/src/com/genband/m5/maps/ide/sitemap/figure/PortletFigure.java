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
*     File:     PortletFigure.java
*
*     Desc:   	Figure for portlet.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.figure;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.util.Constants;

public class PortletFigure extends BasicFigure {
	Rectangle placeHolderBounds = new Rectangle(0,0,500,500);
	Color FGColor;
	Color BGColor;
	private int portletNo = 1;
	private String toolTip ;
	private int iconType;
	private String portletName = "Portlet Name";
	public PortletFigure() {
		System.out.print("in constructor of PortletFigure");
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
		
		System.out.println("paintFigure of PortletFigure entered ");
		Image portletIcon = createImage("portlet16_"+iconType+".bmp");
		//get parent's(Display page's) bounds.So that we can adjust it accordingly.
		placeHolderBounds = getParent().getBounds();
		Rectangle siteMapBounds = getParent().getParent().getParent().getBounds();
		int iconWidth = 16;
		int iconHeight = 16;
		int xMargin = siteMapBounds.width/Constants.PLACEHOLDER_PORTLET_X_MARGIN_RATIO;
		int yMargin = siteMapBounds.height/Constants.PLACEHOLDER_PORTLET_Y_MARGIN_RATIO;
		//Setting y and height of portlet...it is same for every portlet
		int y = placeHolderBounds.y + yMargin;
		int height = placeHolderBounds.height - 2*yMargin;
		int x = placeHolderBounds.x + xMargin;
		int width = placeHolderBounds.width - 2* xMargin;
		
		height = (placeHolderBounds.height - yMargin)/Constants.MAX_NO_OF_PORTLETS;
		if ( getNoOfPortlets() > 2) {
			height = (placeHolderBounds.height - yMargin)/getNoOfPortlets();
				
		}
		height = height - yMargin;
		y = placeHolderBounds.y + (yMargin + height)*(portletNo - 1) + yMargin;
		
		int noOfShadedLines = height/Constants.PORTLET_HEIGHT_SHADOW_RATIO;
		
		/*int totalWidth = displayPageBounds.width - 2*xMargin;
		if(layout.equals(Constants.LAYOUT_GENERIC_2_COLUMN) && (1 == placeHolderNo)){
			x = displayPageBounds.x + xMargin;
			width = (int)((double)totalWidth*Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1_TO_PLACEHOLDER2_RATIO);
		} else if(layout.equals(Constants.LAYOUT_GENERIC_2_COLUMN) && (2 == placeHolderNo)){
			int placeHolder1_width = (int)((double)totalWidth*Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1_TO_PLACEHOLDER2_RATIO);
			x = displayPageBounds.x + xMargin + placeHolder1_width;
			width = (int)((double)totalWidth*(1.0 - Constants.LAYOUT_GENERIC_2_COLUMN_PLACEHOLDER1_TO_PLACEHOLDER2_RATIO));
		} else if(layout.equals(Constants.LAYOUT_3_COLUMN) && (1 == placeHolderNo)){
			x = displayPageBounds.x + xMargin;
			width = (int)((double)totalWidth*.33);
		} else if(layout.equals(Constants.LAYOUT_3_COLUMN) && (2 == placeHolderNo)){
			width = (int)((double)totalWidth*.33);
			x = displayPageBounds.x + xMargin + width;
		}else if(layout.equals(Constants.LAYOUT_3_COLUMN) && (3 == placeHolderNo)){
			width = (int)((double)totalWidth*.33);
			x = displayPageBounds.x + xMargin + 2*width;
		}
		*/
		int widthWithoutShadow = width - noOfShadedLines;
		int heightWithoutShadow = height - noOfShadedLines;
		int titleHeight = heightWithoutShadow/Constants.PORTLET_TITLE_CONTENT_RATIO;
		
		bounds = new Rectangle(x,y,width,height);
		setBounds(bounds);
		//graphics.drawRectangle(x,y,width-1,height-2);
		if(state == Constants.NORMAL){
			graphics.setForegroundColor(Constants.SELECTABLE_COLOR);
			//graphics.drawRectangle(bounds);
			}else if(state == Constants.HOVER){
			graphics.setForegroundColor(Constants.HOVER_COLOR);
			//graphics.drawRectangle(bounds);
		}else if(state == Constants.SELECTED){
			graphics.setForegroundColor(Constants.SELECTION_COLOR);
			//graphics.drawRectangle(bounds);
		}
		//draw boundary of portlet
		graphics.setForegroundColor(ColorConstants.white);
		if(Constants.SELECTED == getState()){
			graphics.setForegroundColor(new Color(null,0,0,0));
			graphics.drawLine(x, y, x + widthWithoutShadow, y);
			graphics.drawLine(x, y, x, y + heightWithoutShadow+1);
		}else{
			graphics.drawLine(x, y, x + widthWithoutShadow, y);
			graphics.drawLine(x, y, x, y + heightWithoutShadow);
		}
		/*if(Constants.SELECTED == getState()){
			graphics.setForegroundColor(new Color(null,70,70,70));
			graphics.drawLine(x, y-1, x + widthWithoutShadow, y-1);
			graphics.drawLine(x+1, y, x+1, y + heightWithoutShadow);
		}*/
		
		graphics.setForegroundColor(new Color(null,128,128,128));
		if(Constants.SELECTED == getState()){
			graphics.setForegroundColor(ColorConstants.white);
			graphics.drawLine(x+widthWithoutShadow,y+1,x+widthWithoutShadow,y+heightWithoutShadow);
			graphics.drawLine(x+1, y + heightWithoutShadow, x+widthWithoutShadow, y+heightWithoutShadow);
		}
		else{
			graphics.drawLine(x+widthWithoutShadow,y+1,x+widthWithoutShadow,y+heightWithoutShadow);
			graphics.drawLine(x+1, y + heightWithoutShadow, x+widthWithoutShadow, y+heightWithoutShadow);
		}
		if(Constants.SELECTED == getState()){
			//noOfShadedLines = 1;
			graphics.setForegroundColor(new Color(null,79,79,79));
			graphics.drawLine(x+widthWithoutShadow+1, y, x + widthWithoutShadow+1 , y+heightWithoutShadow+1);
			graphics.drawLine(x, y+heightWithoutShadow+1, x + widthWithoutShadow+1 , y+heightWithoutShadow+1);
			
		}else{
			int startR = 79;
			int startG = 79;
			int startB = 79;
			int endR = 238;
			int endG = 238;
			int endB = 238;
			int currentR = 0;
			int currentG = 0;
			int currentB = 0;
			
			double gradientR = (double)(startR - endR)/noOfShadedLines;
			double gradientG = (double)(startG - endG)/noOfShadedLines;
			double gradientB = (double)(startB - endB)/noOfShadedLines;
			//int gradientG = startG - endG;
			//int gradientB = startB - endB;
			int adjustment = 2;
			height = height - 1 - 2 - adjustment;
			int i = 0 ;
			for ( i = 0 ; i < noOfShadedLines ; i++ ){
				currentR = startR - (int)(gradientR*i);
				currentG = startG - (int)(gradientG*i);
				currentB = startB - (int)(gradientB*i);
				graphics.setForegroundColor(new Color(null,currentR,currentG,currentB));
				graphics.drawLine(x+widthWithoutShadow+1+i, y+2+i, x + widthWithoutShadow+1+i , y+heightWithoutShadow+1+i);
				graphics.drawLine(x+1+i, y+heightWithoutShadow+i+1, x + widthWithoutShadow+i+1 , y+heightWithoutShadow+i+1);
				//graphics.drawLine(x, y+i+1, x + width , y+i+1);
			}
	}
		
		
		//graphics.drawRectangle(bounds);
		graphics.setBackgroundColor(Constants.PORTLET_TITLE_FILL_COLOR);
		if(Constants.HOVER == getState()){
			graphics.setBackgroundColor(Constants.PORTLET_TITLE_FILL_COLOR_HOVER);
		}
		if(Constants.SELECTED == getState()){
			graphics.setBackgroundColor(Constants.PORTLET_TITLE_FILL_COLOR_SELECTED);
		}
		graphics.fillRectangle(x+1,y+1,widthWithoutShadow-1,titleHeight);
		graphics.drawImage(portletIcon, x + Constants.PORTLET_ICON_MARGIN, y + titleHeight/2 - iconHeight/2);
		
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.fillRectangle(x+1,y+1+titleHeight,widthWithoutShadow-1,heightWithoutShadow - titleHeight -2);
		
		graphics.setForegroundColor(Constants.TEXT_COLOR);
		graphics.drawText(getPortletName(), x + iconWidth + 2*Constants.PORTLET_ICON_MARGIN, y + titleHeight/2 - iconHeight/2);
		
		graphics.setForegroundColor(Constants.PORTLET_CONTENT_LINE_COLOR);
		for ( int lineY = y + titleHeight+yMargin ; lineY < y+heightWithoutShadow-yMargin;lineY+=6 ) {
			graphics.drawLine(x+xMargin, lineY, x+xMargin + width/3 -xMargin/2, lineY);
			graphics.drawLine(x+xMargin + width/3 +xMargin/2, lineY, x+width-xMargin, lineY);
			//Font font = new Font()
			//graphics.setFont(f);
		}
		
		/*graphics.drawRectangle(bounds);
		graphics.setBackgroundColor(Constants.PORTLET_TITLE_FILL_COLOR);
		graphics.fillRectangle(x+1,y+1,width-1,titleHeight);
		graphics.drawImage(portletIcon, x + Constants.PORTLET_ICON_MARGIN, y + titleHeight/2 - iconHeight/2);
		
		graphics.setForegroundColor(Constants.TEXT_COLOR);
		graphics.drawText(getPortletName(), x + iconWidth + 2*Constants.PORTLET_ICON_MARGIN, y + titleHeight/2 - iconHeight/2);
		graphics.setForegroundColor(Constants.PORTLET_CONTENT_LINE_COLOR);
		for ( int lineY = y + titleHeight+yMargin ; lineY < y+height-yMargin;lineY+=6 ) {
			graphics.drawLine(x+xMargin, lineY, x+xMargin + width/3 -xMargin/2, lineY);
			graphics.drawLine(x+xMargin + width/3 +xMargin/2, lineY, x+width-xMargin, lineY);
			//Font font = new Font()
			//graphics.setFont(f);
		}*/
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
	private int getNoOfPortlets(){
		int noOfPortlets = 0;
		List siblings = getParent().getChildren();
		for ( int i = 0 ; i < siblings.size() ; i++ ) {
			if(siblings.get(i) instanceof PortletFigure){
				noOfPortlets++;
			}
		}
		return noOfPortlets;
	}
	protected static Image createImage(String name) {
		return CPFPlugin.getDefault().getImageRegistry().get(name);
	}
	private String getPortletText() {
		return "Portlet";
	}
	
	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}

	public int getPortletNo() {
		return portletNo;
	}

	public void setPortletNo(int portletNo) {
		this.portletNo = portletNo;
	}

	public String getPortletName() {
		return portletName;
	}

	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}

	/*public String getToolTip() {
		return toolTip;
	}*/

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public int getIconType() {
		return iconType;
	}

	public void setIconType(int iconType) {
		this.iconType = iconType;
	}
}
