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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents table of <code>Record</code>s. Users can add,
 * search, replace and remove records as well as read table from
 * file and write it to a file. Different records in the table can have
 * different attributes, however if the search for record with certain
 * value of given attribute is required, then the attribute must be present
 * in all the records. Single attribute search is supported, i.e. if
 * the key is naturally represented by more than one attribute,
 * there must be an attribute which contains bothe the attributes in some way.
 * <p>
 * The table can be read and written from and to input and output stream using
 * an implementation of <code>TableParser</code> class.
 *
 * @author Bahul Malik
 * @see Record
 */
public class Table {
	/**
	 * Holds all records currently present in the table.
	 */
	private List<Record> records;


	@Override
	public String toString() {
		return "Table [records=" + records + "]";
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

}
