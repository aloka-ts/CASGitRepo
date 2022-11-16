package com.genband.m5.maps.ide.wizard;


/**
 * An illegal line ending was specified.
 *
 * @author Stephen Ostermiller http://ostermiller.org/contact.pl?regarding=Java+Utilities
 * @since ostermillerutils 1.06.01
 */
public class BadLineEndingException extends IllegalArgumentException {

	/**
	 * Serial version id
	 */
	private static final long serialVersionUID = -3300235286182152695L;

	/**
	 * Constructs an exception with null as its error detail message.
	 *
	 * @since ostermillerutils 1.06.01
	 */
	public BadLineEndingException(){
		super();
	}

	/**
	 * Constructs an exception with the specified detail message.
	 * The error message string s can later be retrieved by the
	 * Throwable.getMessage()  method of class java.lang.Throwable.
	 *
	 * @param s the detail message.
	 *
	 * @since ostermillerutils 1.06.01
	 */
	public BadLineEndingException(String s){
		super(s);
	}
}
