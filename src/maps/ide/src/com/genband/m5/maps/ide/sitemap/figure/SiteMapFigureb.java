package com.genband.m5.maps.ide.sitemap.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;

public class SiteMapFigureb extends FreeformLayer {
	
	Label label;
	
	public SiteMapFigureb() {
	//Figure f = new FreeformLayer();
		//setBorder(new MarginBorder(3));
		setBounds(new Rectangle(10,10,100,100));
		setLayoutManager(new FreeformLayout());
		
		label = new Label("" + getFileName());
		//System.out.print("getBounds.... " + getBounds());
		add(label);
	}
	
	/*protected void paintChildren(Graphics graphics) {
		IFigure child;
		Rectangle clip = Rectangle.SINGLETON;
		for (int i = 0; i < getChildren().size(); i++) {
			//System.out.println("111in paintchildren");
				child = (IFigure)getChildren().get(i);
				//System.out.println("child.isVisible() "+ child.isVisible() + "child.intersects(graphics.getClip(clip))" + child.intersects(graphics.getClip(clip)));
				//myTabFolder.setBounds(graphics.getClip(clip));
				//button1.setBounds(graphics.getClip(clip));
				label.setBounds(graphics.getClip(clip));
				//System.out.println("getLocation() " + getLocation());
				//label.setLocation(getBounds().getLocation());
				System.out.println("in sitemapFigure graphics.getClip(clip) " + graphics.getClip(clip));
				//System.out.println("child.getBounds()  " + child.getBounds());
				//if (child.isVisible() && child.intersects(graphics.getClip(clip))) {
				//System.out.println("in if of in paintchildren");
					graphics.clipRect(child.getBounds());
				child.paint(graphics);
				graphics.restoreState();
			//}
		}
	}*/
	
	private String getFileName() {
		// TODO Auto-generated method stub
		return "NP SiteMap.sitemap";
	}

	protected void paintFigure(Graphics graphics) {
		//super.paintFigure(graphics);
		graphics.drawRectangle(getBounds());
		paintChildren(graphics);
	}
}
