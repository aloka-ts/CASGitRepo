package com.genband.m5.maps.ide.wizard;

/**
 * An illegal quote was specified.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.02.16
 */
public class BadQuoteException extends IllegalArgumentException {

	/**
	 * serial version id
	 */
	private static final long serialVersionUID = -5926914821468886713L;

	/**
	 * Constructs an exception with null as its error detail message.
	 *
	 * @since ostermillerutils 1.02.16
	 */
	public BadQuoteException(){
		super();
	}

	/**
	 * Constructs an exception with the specified detail message.
	 * The error message string s can later be retrieved by the
	 * Throwable.getMessage()  method of class java.lang.Throwable.
	 *
	 * @param s the detail message.
	 *
	 * @since ostermillerutils 1.02.16
	 */
	public BadQuoteException(String s){
		super(s);
	}
}