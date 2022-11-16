/*
 * MsRegionSpec.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb;
import java.io.Serializable;
import java.net.URI;
import java.text.DecimalFormat;
/**
 * The MsRegionSpec class defines the specification for a msml region element as per RFC 5707.
 * This class provides methods for defining id, top, left, priority, relative size and 
 * other attributes of region element of msml. 
 * region element can by child element of videolayout selector or stream (as visual).
 */
public class MsRegionSpec implements Serializable{
private String id;
private Double left=0.0000D;
private Double top=0.0000D;
private String relativeSize;
private Double regionPriority;
private String title;
private MsColorSpec titleTextColor;
private MsColorSpec titleBackgroundColor;
private MsColorSpec borderColor;
private int borderWidth;
private URI logo;
private boolean freeze;
private boolean blank;
private DecimalFormat decimalformat = new DecimalFormat("####.####");
/**
 * This method sets id attribute that can be used as a name to refer the region.
 * @param id the id to set
 */
public void setId(String id) {
	this.id = id;
}
/**
 *  This method returns id attribute that can be used as a name to refer the region.
 * @return the id
 */
public String getId() {
	return id;
}
/**
 *  This method sets left attribute of region element.
 * @param left the left to set
 */
public void setLeft(Double left) {
	this.left =Double.valueOf(decimalformat.format(left));;
}
/**
 * This method returns left attribute of region element.
 * @return the left
 */
public Double getLeft() {
	return left;
}
/**
 * This method sets top attribute of region element.
 * @param top the top to set
 */
public void setTop(Double top) {
	this.top = Double.valueOf(decimalformat.format(top));;
}
/**
 * This method returns top attribute of region element.
 * @return the top
 */
public Double getTop() {
	return top;
}
/**
 * This method sets relativesize attribute of region element.
 * @param relativesize the relativesize to set
 */
public void setRelativesize(String relativesize) {
	this.relativeSize=relativesize;
}
/**
 * This method returns relativesize attribute of region element. 
 * @return the relativesize
 */
public String getRelativesize() {
	return relativeSize;
}
/**
 * This method sets title attribute of region element.
 * @param title the title to set
 */
public void setTitle(String title) {
	this.title = title;
}
/**
 * This method returns title attribute of region element.
 * @return the title
 */
public String getTitle() {
	return title;
}
/**
 * This method sets titletextcolor attribute of region element.
 * @param titletextcolor the titletextcolor to set
 */
public void setTitleTextColor(MsColorSpec titletextcolor) {
	this.titleTextColor = titletextcolor;
}
/**
 * This method returns titletextcolor attribute of region element.
 * @return the titletextcolor
 */
public MsColorSpec getTitleTextColor() {
	return titleTextColor;
}
/**
 * This method sets titlebackgroundcolor attribute of region element.
 * @param titlebackgroundcolor the titlebackgroundcolor to set
 */
public void setTitleBackgroundColor(MsColorSpec titlebackgroundcolor) {
	this.titleBackgroundColor = titlebackgroundcolor;
}
/**
 * This method returns titlebackgroundcolor attribute of region element.
 * @return the titlebackgroundcolor
 */
public MsColorSpec getTitleBackgroundColor() {
	return titleBackgroundColor;
}
/**
 * This method sets bordercolor attribute of region element.
 * @param bordercolor the bordercolor to set
 */
public void setBorderColor(MsColorSpec bordercolor) {
	this.borderColor = bordercolor;
}
/**
 * This method returns bordercolor attribute of region element.
 * @return the bordercolor
 */
public MsColorSpec getBorderColor() {
	return borderColor;
}
/**
 * This method sets borderwidth attribute of region element.
 * @param borderwidth the borderwidth to set
 */
public void setBorderWidth(int borderwidth) {
	if(borderwidth>0)
	this.borderWidth = borderwidth;
}
/**
 * This method returns borderwidth attribute of region element.
 * @return the borderwidth
 */
public int getBorderWidth() {
	return borderWidth;
}
/**
 * This method sets logo attribute of region element.
 * @param logo the logo to set
 */
public void setLogo(URI logo) {
	this.logo = logo;
}
/**
 * This method returns logo attribute of region element.
 * @return the logo
 */
public URI getLogo() {
	return logo;
}
/**
 * This method sets freeze attribute of region element.
 * @param freeze the freeze to set
 */
public void setFreeze(boolean freeze) {
	this.freeze = freeze;
}
/**
 * This method returns freeze attribute of region element.
 * @return the freeze
 */
public boolean isFreeze() {
	return freeze;
}
/**
 * This method sets blank attribute of region element.
 * @param blank the blank to set
 */
public void setBlank(boolean blank) {
	this.blank = blank;
}
/**
 * This method checks blank attribute of region element.
 * @return the blank
 */
public boolean isBlank() {
	return blank;
}
/**
 * This method sets priority attribute for region element.
 * Minimum inclusive value=0.0, Maximum exclusive value=1.0.
 * Incorrect values will ignored by Adaptor while generating msml request. 
 * @param priority the region_priority to set
 */
public void setPriority(Double priority) {
	this.regionPriority = priority;
}
/**
 * This method returns priority attribute for region element.
 * Minimum inclusive value=0.0, Maximum exclusive value=1.0.
 * Incorrect values will ignored by Adaptor while generating msml request. 
 * @return the region_priority
 */
public Double getPriority() {
	return regionPriority;
}

}