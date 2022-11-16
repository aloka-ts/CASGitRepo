package com.genband.m5.maps.ide.wizard;

/**
 * An Illegal delimiter was specified.
 * <p>
 * This class exists to fix a spelling error in BadDelimeterException.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.02.20
 * @see BadDelimeterException
 */
public class BadDelimiterException extends BadDelimeterException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = -3250803278822032684L;

	/**
	 * Constructs an exception with null as its error detail message.
	 *
	 * @since ostermillerutils 1.02.20
	 */
	public BadDelimiterException(){
		super();
	}

	/**
	 * Constructs an exception with the specified detail message.
	 * The error message string s can later be retrieved by the
	 * Throwable.getMessage()  method of class java.lang.Throwable.
	 *
	 * @param s the detail message.
	 * @since ostermillerutils 1.02.20
	 */
	public BadDelimiterException(String s){
		super(s);
	}
}