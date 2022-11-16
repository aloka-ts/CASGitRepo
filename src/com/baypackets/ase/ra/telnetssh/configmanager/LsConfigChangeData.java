/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.configmanager;

import oracle.jdbc.dcn.RowChangeDescription.RowOperation;

/**
 * The Class LSConfigChangeData.
 *
 * @author saneja
 */
public class LsConfigChangeData implements Comparable<LsConfigChangeData> {
	
	/** The table name. */
	private String tableName;
	
	/** The row operation. */
	private RowOperation rowOperation;
	
	/** The row id. */
	private String rowId;
	
	/**
	 * Gets the table name.
	 *
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Sets the table name.
	 *
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Gets the row operation.
	 *
	 * @return the rowOperation
	 */
	public RowOperation getRowOperation() {
		return rowOperation;
	}
	
	/**
	 * Sets the row operation.
	 *
	 * @param rowOperation the rowOperation to set
	 */
	public void setRowOperation(RowOperation rowOperation) {
		this.rowOperation = rowOperation;
	}
	
	/**
	 * Gets the row id.
	 *
	 * @return the rowId
	 */
	public String getRowId() {
		return rowId;
	}
	
	/**
	 * Sets the row id.
	 *
	 * @param rowId the rowId to set
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return "TableName: "+tableName+"  Operation: "+rowOperation.toString()+"  rowID: "+rowId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(LsConfigChangeData lsConfigChangeData) {
		
		RowOperation thisOper= this.getRowOperation();
		RowOperation objectOper= lsConfigChangeData.getRowOperation();
		int retValue =-1;
		if(thisOper == objectOper){
			retValue=0;
		}else{
			retValue*= ( thisOper.name().compareToIgnoreCase(objectOper.name()) ); 
				
		}
		
		return retValue;
	}

}
