/*
 * Copyright (c) 1996-2001
 * Logica Mobile Networks Limited
 * All rights reserved.
 *
 * This software is distributed under Logica Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package com.baypackets.ase.ra.smpp.server.receiver.util;

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * The class <code>Record</code> represents a set of <code>Attribute</code>s.
 * It's used in <code>Table</code> class. It can represent various types of
 * data, e.g. user settings, config parameters etc. When used in
 * <code>Table</code>, different records might have different attributes
 * -- attributes with different names.
 *
 * @author Bahul Malik
 * @see Table
 * @see Attribute
 */
public class Record {
	/**
	 * The list of the attributes of this record.
	 */
	private List<Attribute> attributes;

	@Override
	public String toString() {
		return "Record [attributes=" + attributes + "]";
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

}