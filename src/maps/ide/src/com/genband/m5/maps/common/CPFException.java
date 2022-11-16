package com.genband.m5.maps.common;

import java.sql.SQLException;
//import org.hibernate.HibernateException;
//import org.hibernate.JDBCException;

public class CPFException extends Exception {

	private static final long serialVersionUID = -8599116444622523343L;
	private int categoryCode;
	private String simpleMessage = null ;
	public CPFException (String msg, int e) {
		super(msg);
		categoryCode = e;
	}
	public CPFException (String msg, Throwable t, int e) {
		super(msg, t);
		categoryCode = e;		
	}
	public CPFException (String msg, String simpleMessage , int e) {
		super(msg);
		categoryCode = e;
		this.simpleMessage = simpleMessage ;
	}
	public CPFException (String msg, Throwable t, String simpleMessage , int e) {
		super(msg, t);
		categoryCode = e;
		this.simpleMessage = simpleMessage ;
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
	 * 204 - Not able to delete due to Constraint Violation
	 * 301 - Not able to create
	 * 302 - Exception caught in CPFSessionFacade during create (Most probably Transaction rollback during create)
	 * 600 - Query Syntax error
	 * 601 - Not able to list due to some unknown reason
	 * 602 - could not resolve property (field passed is not found in database) -- QueryException
	 * 603 - QueryException due to some unknown error in query
	 * 604 - EJBTransactionRollbackException //Transaction rollback
	 * 605 - Some of the preconditions for listing were not fulfilled
	 * 700 - Query Syntax Error
	 * 701 - Not able to getResult to view
	 * 702 - More than one tuple available to view
	 * 703 - No tuple to view
	 * 704 - PrimaryKey not properly set //PK = null
	 * 800 - Not able to Modify
	 * 801 - Tuple to be modified not found in database
	 * 802 - Problem while processing for some related entity
	 * 803 - Problem while processing for some weak entity
	 * 804 - Could not get PK
	 * 901 - Not able to create and then list
	 * 1001 - Not able to modify and then list
	 * 1101 - Not able to delete and then list
	 * 4041 - Not authorized to List
	 * 4042 - Not authorized to View
	 * 4043 - Not authorized to Create
	 * 4044 - Not authorized to Modify
	 * 4045 - Not authorized to delete
	 */
	
	public int getCategoryCode () {		
		return categoryCode;
	}
	
	/**
	 * 
	 * @return vendor specific database error code or -1 if none available
	 */
	public int getVendorCode () {
		Throwable t = getCause();
		if (null != t)
			t = t.getCause();
		if (t != null && t instanceof SQLException)
			return ((SQLException)t).getErrorCode();
		
		return -1;
	}
	public String getSimpleMessage() {
		return simpleMessage;
	}
	
}
