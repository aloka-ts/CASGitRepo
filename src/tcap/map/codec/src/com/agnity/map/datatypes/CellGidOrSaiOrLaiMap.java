/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/

package com.agnity.map.datatypes;

public class CellGidOrSaiOrLaiMap {

	private CellGlobalIdOrServiceAreaIdFixedLengthMap cellGidOrSaiFixedLen;
	private LAIFixedLenDataType laiFixedLen;
	
	public CellGidOrSaiOrLaiMap(
			CellGlobalIdOrServiceAreaIdFixedLengthMap cgidOrSaiFixedLen) {
		this.cellGidOrSaiFixedLen = cgidOrSaiFixedLen;
		this.laiFixedLen = null;
	}
	
	public CellGidOrSaiOrLaiMap(LAIFixedLenDataType laiFixedLen) {
		this.laiFixedLen = laiFixedLen;
		this.cellGidOrSaiFixedLen = null;
	}
	/**
	 * Method to return CellGlobalIdOrServiceAreaIdFixedLengthMap object
	 * @return CellGlobalIdOrServiceAreaIdFixedLengthMap
	 */
	
	public CellGlobalIdOrServiceAreaIdFixedLengthMap getCgidOrSaiFixedLen() {
		return this.cellGidOrSaiFixedLen;
	}
	
	/**
	 *  
	 * @return LAIFixedLenDataType
	 */

	public LAIFixedLenDataType getLaiFixedLen() {
		return this.laiFixedLen;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CellGidOrSaiOrLaiMap [cellGidOrSaiFixedLen="
				+ cellGidOrSaiFixedLen + ", laiFixedLen=" + laiFixedLen + "]";
	}

}
