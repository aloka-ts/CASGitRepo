
package com.baypackets.ase.jndi_jdbc.ds;
import java.sql.*;
import javax.sql.*;
import java.io.*;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Calendar;
import org.apache.log4j.*;


/** This class wraps the physical prepared statement obejct.
* This class delegates all the method to the underlying physical statement object except the close method.
*/

public class LogicalPrepareStatement implements PreparedStatement 
{
	private PreparedStatement preparedstatement=null;
	private String query=null;
	private Connection connection=null;
	private PooledConnectionImpl poolconn=null;
	
	private static Logger logger=Logger.getLogger(LogicalPrepareStatement.class);

	public LogicalPrepareStatement(String sqlquery,Connection conn,PooledConnectionImpl pool) throws SQLException
	{
        	try
		{
		
			query=sqlquery;
			connection =conn;
			preparedstatement=connection.prepareStatement(sqlquery);
			poolconn=pool;
		}
		catch(SQLException e)
		{
			logger.error(e.toString(),e);
			throw e;
		}
		catch(Exception ee)
		{
			if (logger.isDebugEnabled()) {
				logger.debug(ee.toString(),ee);
			}
		}
	}
  
 	public ResultSet executeQuery() throws SQLException
  	{
  		return preparedstatement.executeQuery();
  	
  	}

  
  	public int executeUpdate() throws SQLException
  	{
		if (logger.isDebugEnabled()) {
			logger.debug("I am in execute update");
		}
		return preparedstatement.executeUpdate();
  	//return 1;
  	}

 
  	public void setNull(int parameterIndex, int sqlType) throws SQLException
  	{
  		preparedstatement.setNull(parameterIndex,sqlType);
  	}


  	public void setBoolean(int parameterIndex, boolean x) throws SQLException
  	{
  		preparedstatement.setBoolean(parameterIndex,x);
  	}

  
  	public void setByte(int parameterIndex, byte x) throws SQLException
  	{
  		preparedstatement.setByte(parameterIndex,x);
  	}

 
  	public void setShort(int parameterIndex, short x) throws SQLException
  	{
  		preparedstatement.setShort(parameterIndex,x);
  	}

 
  	public void setInt(int parameterIndex, int x) throws SQLException
  	{
  		preparedstatement.setInt(parameterIndex,x);
  	}

 
  	public void setLong(int parameterIndex, long x) throws SQLException
  	{
  		preparedstatement.setLong(parameterIndex,x);
  	}

  
  	public void setFloat(int parameterIndex, float x) throws SQLException
  	{
  		preparedstatement.setFloat(parameterIndex,x);
  	}

 
  	public void setDouble(int parameterIndex, double x) throws SQLException
  	{
  		preparedstatement.setDouble(parameterIndex,x);
  	}

  
  	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
  	{
  		preparedstatement.setBigDecimal(parameterIndex,x);
  	}

  
  	public void setString(int parameterIndex, String x) throws SQLException
  	{
  		preparedstatement.setString(parameterIndex,x);
  	}

  
  	public void setBytes(int parameterIndex, byte[] x) throws SQLException
  	{
  		preparedstatement.setBytes(parameterIndex,x);
  	}

  
  	public void setDate(int parameterIndex, Date x) throws SQLException
  	{
  		preparedstatement.setDate(parameterIndex,x);
  	}

  
  	public void setTime(int parameterIndex, Time x) throws SQLException
  	{
  		preparedstatement.setTime(parameterIndex,x);
  	}

  
  	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
  	{
  		preparedstatement.setTimestamp(parameterIndex,x);
  	}

 
  	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
  	{
  		preparedstatement.setAsciiStream(parameterIndex,x,length);
  	}

  
  	public void setUnicodeStream(int parameterIndex, InputStream x, int length)throws SQLException
  	{
  		preparedstatement.setUnicodeStream(parameterIndex,x,length);
  	}

  
  	public void setBinaryStream(int parameterIndex, InputStream x, int length)throws SQLException
  	{
  		preparedstatement.setBinaryStream(parameterIndex,x,length);
  	}

  
  	public void clearParameters() throws SQLException
  	{
  		preparedstatement.clearParameters();
  	}

  
  	public void setObject(int parameterIndex, Object x, int targetSqlType,int scale) throws SQLException
  	{
  		preparedstatement.setObject(parameterIndex,x,targetSqlType,scale);
  	}

 
  	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException
  	{
  		preparedstatement.setObject(parameterIndex,x,targetSqlType);
  	}

 
  	public void setObject(int parameterIndex, Object x) throws SQLException
  	{
  		preparedstatement.setObject(parameterIndex,x);
  	}

  
  	public boolean execute() throws SQLException
  	{
  		return preparedstatement.execute();
  	}

  
  	public void addBatch() throws SQLException
  	{
  		preparedstatement.addBatch();
  	}

 
  	public void setCharacterStream(int parameterIndex, Reader reader,int length) throws SQLException
  	{
  		preparedstatement.setCharacterStream(parameterIndex,reader,length);
  	}

  
  	public void setRef(int i, Ref x) throws SQLException
  	{
  		preparedstatement.setRef(i,x);
  	}

 
  	public void setBlob(int i, Blob x) throws SQLException
  	{
  		preparedstatement.setBlob(i,x);
  	}

  
  	public void setClob(int i, Clob x) throws SQLException
  	{
  		preparedstatement.setClob(i,x);
  	}

 
  	public void setArray(int i, Array x) throws SQLException
  	{
  		preparedstatement.setArray(i,x);
  	}

 
  	public ResultSetMetaData getMetaData() throws SQLException
  	{
  		return preparedstatement.getMetaData();
  	}

  
  	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException
  	{
  		preparedstatement.setDate(parameterIndex,x,cal);
  	}

 
  	public void setTime(int parameterIndex, Time x, Calendar cal)throws SQLException
  	{
  		preparedstatement.setTime(parameterIndex,x,cal);
  	}

  
  	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)throws SQLException
  	{
  		preparedstatement.setTimestamp(parameterIndex,x,cal);
  	}

 
  	public void setNull(int paramIndex, int sqlType, String typeName)throws SQLException
  	{
  		preparedstatement.setNull(paramIndex,sqlType,typeName);
  	}

  	public void setURL(int parameterIndex, URL x) throws SQLException
  	{
  		preparedstatement.setURL(parameterIndex,x);
  	}

  
  	public ParameterMetaData getParameterMetaData() throws SQLException
  	{
  		return preparedstatement.getParameterMetaData();
  	}
  
  
  //***********************************************************************//
  
  	public ResultSet executeQuery(String sql) throws SQLException
  	{
  		return preparedstatement.executeQuery(sql);
  	}

 
  	public int executeUpdate(String sql) throws SQLException
  	{
  		return preparedstatement.executeUpdate(sql);
	
  	}

  
  	public void close() 
  	{
		if (logger.isInfoEnabled()) {
			logger.info("I am closing the Logical Prepared Statement");
		}
		try
		{
			poolconn.logicalPrepareStatementClosed(this,query);
		}
		catch(Exception e)
		{
			logger.error(e.toString(),e);	
		
		}
  	
  	}

 
  	public int getMaxFieldSize() throws SQLException
  	{
  		return preparedstatement.getMaxFieldSize();
  	}

  
  	public void setMaxFieldSize(int max) throws SQLException
  	{
  		preparedstatement.setMaxFieldSize(max);
  	}

 
  	public int getMaxRows() throws SQLException
  	{
  		return preparedstatement.getMaxRows();
  	}

  
  	public void setMaxRows(int max) throws SQLException
  	{
  		preparedstatement.setMaxRows(max);
  	}

 
  	public void setEscapeProcessing(boolean enable) throws SQLException
  	{
  		preparedstatement.setEscapeProcessing(enable);
  	}

  
  	public int getQueryTimeout() throws SQLException
  	{
  		return preparedstatement.getQueryTimeout();
  	}

  
  	public void setQueryTimeout(int seconds) throws SQLException
  	{
  		preparedstatement.setQueryTimeout(seconds);
  	}

 
  	public void cancel() throws SQLException
  	{
  		preparedstatement.cancel();
  	}

 
  	public SQLWarning getWarnings() throws SQLException
  	{
  		return preparedstatement.getWarnings();
  	}

  
  	public void clearWarnings() throws SQLException
  	{
  		preparedstatement.clearWarnings();
  	}

 
  	public void setCursorName(String name) throws SQLException
  	{
  		preparedstatement.setCursorName(name);
  	}

  
  	public boolean execute(String sql) throws SQLException
  	{
  		return preparedstatement.execute(sql);
  	}

 
  	public ResultSet getResultSet() throws SQLException
  	{
  		return preparedstatement.getResultSet();
  	}

  
  	public int getUpdateCount() throws SQLException
  	{
  		return preparedstatement.getUpdateCount();
  	}

 
  	public boolean getMoreResults() throws SQLException
  	{
  		return preparedstatement.getMoreResults();
  	}

 
  	public void setFetchDirection(int direction) throws SQLException
  	{
  		preparedstatement.setFetchDirection(direction);
  	}

  
  	public int getFetchDirection() throws SQLException
  	{
  		return preparedstatement.getFetchDirection();
  	}

  
  	public void setFetchSize(int rows) throws SQLException
  	{
  		preparedstatement.setFetchSize(rows);
  	}

 
  	public int getFetchSize() throws SQLException
  	{
  		return preparedstatement.getFetchSize();
  	}

  
  	public int getResultSetConcurrency() throws SQLException
  	{
  		return preparedstatement.getResultSetConcurrency();
  	}

 
  	public int getResultSetType() throws SQLException
  	{
  		return preparedstatement.getResultSetType();
  	}

  
  	public void addBatch(String sql) throws SQLException
  	{
  		preparedstatement.addBatch(sql);
  	}

 
  	public void clearBatch() throws SQLException
  	{
  		preparedstatement.clearBatch();
  	}

 
  	public int[] executeBatch() throws SQLException
  	{
  		return preparedstatement.executeBatch();	
  	
  	}

 
  	public Connection getConnection() throws SQLException
  	{
  		return preparedstatement.getConnection();
  	}

  
  	public boolean getMoreResults(int current) throws SQLException
  	{
  		return preparedstatement.getMoreResults(current);
  	}

  
  	public ResultSet getGeneratedKeys() throws SQLException
  	{
  		return preparedstatement.getGeneratedKeys();
  	}

  
  	public int executeUpdate(String sql, int autoGeneratedKeys)throws SQLException
  	{
  		return preparedstatement.executeUpdate(sql,autoGeneratedKeys);
  	}

 
  	public int executeUpdate(String sql, int[] columnIndexes)throws SQLException
  	{
  		return preparedstatement.executeUpdate(sql,columnIndexes);
  	}

  
  	public int executeUpdate(String sql, String[] columnNames)throws SQLException
  	{
  		return preparedstatement.executeUpdate(sql,columnNames);
  	}

  
  	public boolean execute(String sql, int autoGeneratedKeys)throws SQLException
  	{
  		return preparedstatement.execute(sql,autoGeneratedKeys);
  	}

  
  	public boolean execute(String sql, int[] columnIndexes) throws SQLException
  	{
  		return preparedstatement.execute(sql,columnIndexes);
  	}

 
  	public boolean execute(String sql, String[] columnNames)throws SQLException
  	{
  		return preparedstatement.execute(sql,columnNames);
  	}

  
  	public int getResultSetHoldability() throws SQLException
  	{
  		return preparedstatement.getResultSetHoldability();
  	}
/** This method closes the physical prepared statement permanently.
*/
  
  	public void finalclose() throws SQLException
  	{
		preparedstatement.close();
  	}
  
}

