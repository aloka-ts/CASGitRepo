/*
 * AseLockException.java
 *
 * Created on August 23, 2004
 */

package com.baypackets.ase.common.exceptions;

import java.lang.Exception;

public class AseLockException extends Exception {
	 public AseLockException() {
		  super();
	 }
	 
	 public AseLockException(String message) {
		  super(message);
	 }
}

