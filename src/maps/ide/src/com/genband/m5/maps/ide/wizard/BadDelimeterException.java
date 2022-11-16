package com.genband.m5.maps.ide.wizard;
/**
 * An Illegal delimiter was specified.
 * <p>
 * This class has been replaced by BadDelimiterException.  It is not deprecated,
 * and it may be used, however the name of this class contains a spelling error.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.02.08
 * @see BadDelimiterException
 */
public class BadDelimeterException extends IllegalArgumentException {

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 7603007141975623144L;

	/**
	 * Constructs an exception with null as its error detail message.
	 *
	 * @since ostermillerutils 1.02.08
	 */
	public BadDelimeterException(){
		super();
	}

	/**
	 * Constructs an exception with the specified detail message.
	 * The error message string s can later be retrieved by the
	 * Throwable.getMessage()  method of class java.lang.Throwable.
	 *
	 * @param s the detail message.
	 *
	 * @since ostermillerutils 1.02.08
	 */
	public BadDelimeterException(String s){
		super(s);
	}
}
