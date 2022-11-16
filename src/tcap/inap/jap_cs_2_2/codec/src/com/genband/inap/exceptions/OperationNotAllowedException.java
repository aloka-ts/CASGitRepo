package com.genband.inap.exceptions;

/**
 * This exception will be thrown when operation not allowed.
 * @author vgoel
 *
 */
public class OperationNotAllowedException extends Exception {

	/**
	 * default serial version Id
	 */
	private static final long serialVersionUID = 1L;

	public OperationNotAllowedException(){
		
	}
	
	public OperationNotAllowedException(String msg){
		super(msg);
	}
}
