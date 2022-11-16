package com.baypackets.ase.ra.smpp.server.receiver.util;

import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;


public class TableUtils {

	
	private static Logger logger = Logger.getLogger(TableUtils.class);
	private static Table users;
	
	public static Table getUsers() {
		return users;
	}

	public static void setUsers(Table users) {
		TableUtils.users = users;
	}

	/**
	 * Returns a record whose one of the attributes matches to
	 * the provided attribute. If none found, returns null.
	 * @param key the attribute used for matching
	 * @return the found record
	 */
	public static synchronized Record find(Attribute key) {
		logger.debug("TableUtils: inside find wioth "+key); 
		if (key != null) {
			return find(key.getName(), key.getValue());
		} else {
			return null;
		}
	}

	/**
	 * Returns record which contains an attribute with the same name
	 * as provided equal to the value as provided. If none found, returns null.
	 * The comparison of the value is case sensitive.
	 * @param name the name of attribute to check
	 * @param value the required value of the attribute
	 * @return the found record
	 */
	public static synchronized Record find(String name, String value) {
		logger.debug("TableUtils: inside find with name and value"+ name + " " +value); 
		Record current;
		String currKeyValue;
		ListIterator<Record> iter = users.getRecords().listIterator(0);
		while (iter.hasNext()) {
			current = (Record) iter.next();
			logger.debug("TableUtils: Current Record:- "+current); 
			currKeyValue = getValue(current ,name);
			logger.debug("TableUtils: CurrentKey value:- "+currKeyValue); 
			if ((currKeyValue != null) && (currKeyValue.equals(value))) {
				logger.debug("TableUtils: CurrentKey value retrieved:- "+currKeyValue);
				return current;
			}
		}
		return null;
	}
	
	public static synchronized String getValue(Record current,String name) {
		logger.debug("TableUtils: Inside getValue:- "+current);
		logger.debug("TableUtils: Name:- "+name);
		Attribute attr = get(current,name);
		if (attr != null) {
			logger.debug("TableUtils: Inside getValue value returned:- "+attr.getValue());
			return attr.getValue();
		} else {
			return null;
		}
	}

	public static synchronized Attribute get(Record current,String name) {
		logger.debug("TableUtils: Inside get:- "+current);
		logger.debug("TableUtils: Name:- "+name);
		Attribute attr;
		ListIterator<Attribute> iter = current.getAttributes().listIterator();
				
		while (iter.hasNext()) {
			attr = (Attribute) iter.next();
			if (nameEquals(attr,name)) {
				logger.debug("TableUtils: returned attribute"+ attr);
				return attr;
			}
		}
		
		return null;
	}

	public static boolean nameEquals(Attribute attr,String name) {
		logger.debug("Inside name Equals:-" +attr + "name"+ name);
		if (attr.getName() != null) {
			logger.debug("Comparing value "+attr.getName()+ "name"+ name);
	   return StringUtils.equalsIgnoreCase(attr.getName(),name);
				
		} else {
			return name == null; // nulls are equal
		}
	}
}
