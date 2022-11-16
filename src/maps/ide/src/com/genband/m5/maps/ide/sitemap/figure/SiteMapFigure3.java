package com.genband.m5.maps.ide.sitemap.figure;


import javax.swing.text.StyleConstants.FontConstants;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Shell;

public class SiteMapFigure3 extends Figure {
	Rectangle bounds ;
	Rectangle siteMapBounds ;
	Color FGColor;
	Color BGColor;
	
	public SiteMapFigure3(Rectangle bounds) {
	System.out.print("in constructor of SitemapFigure3");
	//super();
	FGColor = ColorConstants.lightGray;
	Ellipse ellipse = new Ellipse();
	RectangleFigure rectFigure = new RectangleFigure();
	//Rectangle bounds = getBounds();
	this.bounds = new Rectangle(bounds);
	siteMapBounds = new Rectangle(bounds.x + 3 , bounds.y + 3 , bounds.width - 9 , bounds.height - 9);
	ellipse.setBounds(siteMapBounds);
	rectFigure.setBounds(siteMapBounds);
	System.out.print("");
	//add(ellipse);
	//add(rectFigure);
	}
	
	public SiteMapFigure3() {
		System.out.print("in constructor of SitemapFigure3");
		//super();
		//this.bounds= new Rectangle(100,100,100,100);
		FGColor = ColorConstants.lightGray;
		//Ellipse ellipse = new Ellipse();
		//RectangleFigure rectFigure = new RectangleFigure();
		//Rectangle bounds = getBounds();
		//this.bounds = new Rectangle(bounds);
		//siteMapBounds = new Rectangle(bounds.x + 3 , bounds.y + 3 , bounds.width - 9 , bounds.height - 9);
		//ellipse.setBounds(siteMapBounds);
		//rectFigure.setBounds(siteMapBounds);
		//System.out.print("");
		
		//add(ellipse);
		//add(rectFigure);
		//this.bounds= new Rectangle(100,100,100,100);
		}
	@Override
	public void paintFigure(Graphics graphics) {
		System.out.println("paintFigure of SitemapFigure3 entered ");
		//super.paintFigure(graphics);
		System.out.println("vandana: parent is : " + getParent());
		int lineWidth = 2;
		//graphics.setXORMode(true);

		graphics.setLineWidth(lineWidth );
		System.out.print("FGColor = " + FGColor);
		
		graphics.setForegroundColor(FGColor);
		graphics.setBackgroundColor(ColorConstants.red);
		
		Rectangle b1 = new Rectangle(getBounds().x ,getBounds().y + 30,getBounds().width/2,getBounds().height/2);
		Rectangle b2 = new Rectangle(getBounds().x + 30,getBounds().y,20,20);
		graphics.fillRectangle(getBounds().x ,getBounds().y,getBounds().width,getBounds().height);
		//graphics.drawRoundRectangle(b1,4,4);
		//graphics.drawRoundRectangle(b2,4,4);

		//paintChildren(graphics);
		//Font font;
		//graphics.setFont(FontConstants.);
		graphics.setForegroundColor(ColorConstants.titleGradient);
		
		//graphics.drawText(getFilename(), siteMapBounds.x + 10, siteMapBounds.y - 6 );
		graphics.setForegroundColor(FGColor);
		
		System.out.println("paintFigure of SitemapFigure3 exit \n\n\n");
		
	}

	@Override
	public void setBounds(Rectangle rect) {
		// TODO Auto-generated method stub
		super.setBounds(rect);
	}
	/*@Override
	public void addMouseMotionListener(MouseMotionListener listener) {
		super.addMouseMotionListener(listener);
		System.out.println("in addMouseMotionListener");
	}
	@Override
	public void addMouseListener(MouseListener listener) {
		// TODO Auto-generated method stub
		super.addMouseListener(listener);
		System.out.println("in addMouseListener");
	}*/
	private String getFilename() {
		return "NP Sitemap.sitemap";
	}

	public void setFGColor(Color forgroundColor) {
		this.FGColor = forgroundColor;
	}

	public void setBGColor(Color backgroundColor) {
		this.BGColor = backgroundColor;
	}
}
