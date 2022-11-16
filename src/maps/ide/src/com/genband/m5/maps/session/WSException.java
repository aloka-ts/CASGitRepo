package com.genband.m5.maps.session;

import java.sql.SQLException;
//import org.hibernate.HibernateException;
//import org.hibernate.JDBCException;

public class WSException extends Exception {

	private static final long serialVersionUID = -8599116444622523343L;
	public WSException (String msg) {
		super(msg);
	}
	public WSException (String msg, Throwable t) {
		super( t);
	}
	
	/**
	 * 
	 * @return the category code of the error such as - 
	 * 100 - Constraint violation
	 * 101 - Mismatched type
	 * 102 - Syntax error
	 * 103 - Could not get lock on shared resource
	 * 104 - Connection exception
	 * 105 - Generic database exception
	 * 200 - Logical Delete not supported
	 * 201 - Tuple to be deleted not found due to some error
	 * 202 - Tuple to be deleted not present in the database
	 * 203 - Not able to Delete
	 * 204 - Not able to delete due to Constrait Violation
	 * 301 - Not able to create
	 * 600 - Query Syntax error
	 * 601 - Not able to list due to some unknown reason
	 * 700 - Query Syntax Error
	 * 701 - Not able to getResult to view
	 * 702 - More than one tuple available to view
	 * 703 - No tuple to view
	 * 704 - PrimaryKey not properly set //PK = null
	 * 800 - Not able to Modify
	 * 4041 - Not authorized to List
	 * 4042 - Not authorized to View
	 * 4043 - Not authorized to Create
	 * 4044 - Not authorized to Modify
	 * 4045 - Not authorized to delete
	 */
	
}
