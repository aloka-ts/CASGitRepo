package com.genband.m5.maps.ide.sitemap.model;

import org.eclipse.swt.graphics.Image;

/**
 * A rectangular shape.
 * @author Genband
 */
public class PageShape extends Shape {
/** A 16x16 pictogram of a rectangular shape. */
private static final Image PAGE_ICON = createImage("page16.bmp");

private static final long serialVersionUID = 1;

public Image getIcon() {
	return PAGE_ICON;
}

public String toString() {
	return "Rectangle " + hashCode();
}
}
