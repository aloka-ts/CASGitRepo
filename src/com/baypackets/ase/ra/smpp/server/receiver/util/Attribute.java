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

/**
 * Represents an attribute (field) of a <code>Record</code>. Each attribute
 * has name and a value. Values are textual, i.e. if you want to use
 * this class to work with integral values, you have to cast the values
 * explicitly.
 *
 * @author Bahul Malik
 * @version $Revision: 1.1 $
 * @see Record
 * @see Table
 */
public class Attribute {
	@Override
	public String toString() {
		return "Attribute [name=" + name + ", value=" + value + "]";
	}

	/**
	 * The name of the attribute
	 */
	private String name = null;

	/**
	 * The value of the attribute.
	 */
	private String value = null;

	/**
	 * Default constructor initialises <code>name</code> and <code>value</code>
	 * of the attribute to empty (null) values.
	 * @see #setName(String)
	 * @see #setValue(String)
	 */
	public Attribute() {
	}

	/**
	 * If you know the name but not the value yet, use this constructor.
	 * @see #setValue(String)
	 */
	public Attribute(String name) {
		this.name = name;
	}

	/**
	 * Initialises the attribute's both <code>name</code> and <code>value</code>
	 */
	public Attribute(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Sets the name of the attribute.
	 * @param name the new value for the name of the attribute.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the value of the attribute.
	 * @param value the new vlaue of the attribute.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Returns the name of the attribute.
	 * @return the name of the attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the current value of the attribute.
	 * @return the current value of the attribute
	 */
	public String getValue() {
		return value;
	}

}