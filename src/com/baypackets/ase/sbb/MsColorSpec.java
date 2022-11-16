/*
 * MsColorSpec.java
 * 
 * @author Amit Baxi 
 */
package com.baypackets.ase.sbb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The MsRegionSpec class defines the specification for a msml color related
 * attributes. This class allows to set color attributes as defined in RFC 5707.
 */
public class MsColorSpec implements Serializable {
	public static final String FORMAT_KEYWORD = "KEYWORD";
	public static final String FROMAT_HEXADECIMAL_RGB = "#RGB";
	public static final String FROMAT_HEXADECIMAL_RRGGBB = "#RRGGBB";
	public static final String FROMAT_RGB_PERCENT = "RGB_PERCENT";
	public static final String FROMAT_RGB_255 = "RGB_255";
	public static final String COLOR_AQUA = "aqua";
	public static final String COLOR_BLACK = "black";
	public static final String COLOR_BLUE = "blue";
	public static final String COLOR_FUCHSIA = "fuchsia";
	public static final String COLOR_GREY = "gray";
	public static final String COLOR_GREEN = "green";
	public static final String COLOR_LIME = "lime";
	public static final String COLOR_MAROON = "maroon";
	public static final String COLOR_NAVY = "navy";
	public static final String COLOR_OLIVE = "olive";
	public static final String COLOR_ORANGE = "orange";
	public static final String COLOR_PURPLE = "purple";
	public static final String COLOR_RED = "red";
	public static final String COLOR_SILVER = "silver";
	public static final String COLOR_TEAL = "teal";
	public static final String COLOR_WHITE = "white";
	public static final String COLOR_YELLOW = "yellow";

	String color_keywords[] = new String[] { COLOR_AQUA, COLOR_BLACK,
			COLOR_BLUE, COLOR_FUCHSIA, COLOR_GREY, COLOR_GREEN, COLOR_LIME,
			COLOR_MAROON, COLOR_NAVY, COLOR_OLIVE, COLOR_ORANGE, COLOR_PURPLE,
			COLOR_RED, COLOR_SILVER, COLOR_TEAL, COLOR_WHITE, COLOR_YELLOW };

	private String color;
	private String format;

	public MsColorSpec(String color) {
		this.format = FORMAT_KEYWORD;
		this.color = color;
	}

	public MsColorSpec(String color, String format) {
		this.format = format;
		this.color = color;
	}

	/*
	 * This method validates color defined by this object. This method validate
	 * color using a regular expressions i.e. selected by color's format.
	 */
	public boolean isValidColor() {
		Pattern pattern = null;
		if (this.color == null || this.format == null)
			return false;

		if (this.format.equals(FORMAT_KEYWORD)
				&& Arrays.asList(color_keywords).contains(this.color))
			return true;

		else if (this.format.equals(FROMAT_HEXADECIMAL_RGB))
			pattern = Pattern.compile("#[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]");

		else if (this.format.equals(FROMAT_HEXADECIMAL_RRGGBB))
			pattern = Pattern
					.compile("#[0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F][0-9a-fA-F]");

		else if (this.format.equals(FROMAT_RGB_PERCENT))
			pattern = Pattern
					.compile("^rgb\\(\\s*([0]?\\d\\d?|100)%\\s*,\\s*([0]?\\d\\d?|100)%\\s*,\\s*([0]?\\d\\d?|100)%\\s*\\)");

		else if (this.format.equals(FROMAT_RGB_255))
			pattern = Pattern
					.compile("^rgb\\(\\s*([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s*,\\s*([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s*,\\s*([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\s*\\)");
		else
			return false;
		Matcher m = pattern.matcher(this.color);
		if (m.matches())
			return true;
		else
			return false;
	}
	/**
	 * This method returns color specified by this object.
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	
}
