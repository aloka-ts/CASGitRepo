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

public class SiteMapFigure2 extends Figure {
	Rectangle bounds ;
	Rectangle siteMapBounds ;
	Color FGColor;
	Color BGColor;
	
	public SiteMapFigure2(Rectangle bounds) {
	System.out.print("in constructor of SitemapFigure");
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
	
	@Override
	public void paintFigure(Graphics graphics) {
		System.out.println("paintFigure of SitemapFigure entered ");
		//super.paintFigure(graphics);
		//graphics.setXORMode(true);
		
		int lineWidth = 2;
		graphics.setLineWidth(lineWidth );
		System.out.print("FGColor = " + FGColor);
		
		graphics.setForegroundColor(FGColor);
		//graphics.setBackgroundColor(ColorConstants.red);
		Rectangle b1 = new Rectangle(getBounds().x ,getBounds().y + 30,getBounds().width/2,getBounds().height/2);
		Rectangle b2 = new Rectangle(getBounds().x,getBounds().y,20,20);
		
		graphics.drawRoundRectangle(b1,4,4);
		graphics.drawRoundRectangle(b2,4,4);
		//graphics.drawOval(b2);
		//paintChildren(graphics);
		//Font font;
		//graphics.setFont(FontConstants.);
		graphics.setForegroundColor(ColorConstants.titleGradient);
		
		//graphics.drawText(getFilename(), siteMapBounds.x + 10, siteMapBounds.y - 6 );
		graphics.setForegroundColor(FGColor);
		
		System.out.println("paintFigure of SitemapFigure exit \n\n\n");
		
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
