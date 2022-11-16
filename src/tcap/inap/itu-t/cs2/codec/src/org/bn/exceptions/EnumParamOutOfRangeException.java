package org.bn.exceptions;

/**
 * This exception will be thrown when input is invalid.
 * @author saneja
 *
 */

public class EnumParamOutOfRangeException extends Exception{
	


	/**
	 * default serial version
	 */
	private static final long	serialVersionUID	= 1L;

	
	public EnumParamOutOfRangeException(String msg) {
		super(msg);
	}
	
	public EnumParamOutOfRangeException(Throwable e) {
		super(e);
	}

}
