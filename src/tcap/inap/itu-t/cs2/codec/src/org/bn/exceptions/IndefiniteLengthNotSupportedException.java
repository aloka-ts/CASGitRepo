package org.bn.exceptions;

/**
 * This exception will be thrown when there is a message or parameter is of indefinite length.
 * @author pgandhi
 *
 */

public class IndefiniteLengthNotSupportedException extends Exception{
	
	/**
	 * default serial version id
	 */
	private static final long serialVersionUID = 1L;

	public IndefiniteLengthNotSupportedException(String msg) {
		super(msg);
	}

}
