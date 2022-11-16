/**
 * EnumException.java
 *
 *Created on March 19,2007
 */
package com.baypackets.ase.enumclient;

import java.lang.Exception;

/**
 * This is Exception class for ENUM.
 * @author Ashish kabra
 */
public class EnumException extends Exception {
	//private String description;
	private int m_errorCode ;
	public EnumException(String desc) {
		super(desc);
	}

    public EnumException(String desc , int errorCode) {
        super(desc);
		m_errorCode = errorCode;
    }	

}
