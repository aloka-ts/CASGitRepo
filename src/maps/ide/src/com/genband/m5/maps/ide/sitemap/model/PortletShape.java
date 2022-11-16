package com.genband.m5.maps.ide.sitemap.model;

import org.eclipse.swt.graphics.Image;

/**
 * An elliptical shape.
 * @author Genband
 */
public class PortletShape extends Shape {

	private String name;
	private String toolTip;
	private int icon_type;

/** A 16x16 pictogram of an elliptical shape. */
private static final Image PORTLET_ICON = createImage("portlet16_0.bmp");

private static final long serialVersionUID = 1;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public Image getIcon() {
	return PORTLET_ICON;
}

public String toString() {
	return "Ellipse " + hashCode();
}

public String getToolTip() {
	return toolTip;
}

public void setToolTip(String toolTip) {
	this.toolTip = toolTip;
}

public int getIcon_type() {
	return icon_type;
}

public void setIcon_type(int icon_type) {
	this.icon_type = icon_type;
}
}
