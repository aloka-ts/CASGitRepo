/**
 * EnumContext.java
 *
 *Created on March 15,2007 
 */
package com.baypackets.ase.enumclient;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.File;
import org.apache.log4j.Logger;
import java.lang.Exception;
/**
 * This class contains configuration parameter for ENUM client.
 * @author Ashish kabra
 */

public class EnumContext {
	transient private static Logger m_logger =
          Logger.getLogger(EnumContext.class);
	/** Context parameters */
	private boolean m_cache ;
	private boolean m_async= false ;
	private boolean m_recursion ; 
	private int m_maxRecursion ;
	private boolean m_parallelQuery ;
	private String m_uriScheme  ;
	private List m_servers ; 

	//Ashish need to change
	private static final String ENUM_CONFIG_FILE_PATH="/user/akabra/dns/dnsjava-2.0.3/enum-config.xml" ;

	
	public EnumContext() {
		//loadConfigParam(ENUM_CONFIG_FILE_PATH);
		m_cache = true ;
 		m_recursion = true ;
		m_maxRecursion = 5 ;
		m_parallelQuery = false;
		m_uriScheme = null;
		m_servers = new ArrayList();
	}


	/** Returns cache option.If returns true means cache option is enabled
     * if returns false means cache option is disabled.
	 *	@return cache option 
	 */
	public boolean getCacheOption () {
		return m_cache ; 
	}

    /** if input is true then enables cache option , otherwise disables cache option
     * @param flag Cache Option 
     */
    public void setCacheOption (boolean flag) {
        m_cache = flag ;
    }
	
	/** Returns recursion option. If returns true means recursion option is enabled
	 * if returns false means recursion option is disabled.
	 * @return recursion option
	 */
	public boolean getRecursionOption() {
		return m_recursion ;
	}

	/** Returns the maximum number of recursion allowed to DNS server. 
	 * @return number of recursions allowed
	 */
	public int getMaxRecursion() {
		return m_maxRecursion ;
	}
	
	/** Returns the list of preferred ENUM server which will be queried at first.
	 * @return  server list
	 */
	public List getServerList() {
		 return m_servers ;
	}
	
	 /** Returns the parallel query option. If returned value is true means that parallel query
	   * will be done , if returned value is false means that query to DNS server will be done 
	   * in sequential manner.
	   * @return parallel query option
	   */	
	public boolean getParallelQueryOption() {
		return m_parallelQuery ;
	}
 
	/** Return the URI scheme set by application else returns null.
	 * @return URI scheme required in response 
	 */
	public String getURIScheme() {
		return m_uriScheme ; 
	}

    /** Return the Synchronousity option.
     * @return Synchronousity option 
     */
    public boolean getAsyncOption() {
        return m_async ;
    }

    /** if input is true then allows asynchronous calls.
     * @param flag Synchronousity Option
     */
    public void setAsyncOption( boolean flag ) {
        m_async = flag ;
    }
	

	/** if input is true then sets recursion option , otherwise unsets recursion option
	 * @param flag Recursion Option
	 */
	public void setRecursionOption( boolean flag ) {
		m_recursion = flag ;
	}

	/** Sets the maximum number of recursion allowed to DNS server.
	 * @param recursion number of recursion allowed before detecting loop
	 */ 
	public void setMaxRecursion(int recursion) {
		m_maxRecursion = recursion;
	}
	
	/** Sets the list to the preferred ENUM server list.
	 * @param list list of preferred ENUM server
	 */
	public void setServerList( List list) {
		m_servers = list ; 
	}

	/** Adds the server to the preferred ENUM server list.
     * @param server list of preferred ENUM server
     */
	public void addServer(EnumServer server ) {
		for ( int i=0;i<m_servers.size() ; i++ ) { 
			if (((EnumServer)m_servers.get(i)).getIpAddr().compareToIgnoreCase(server.getIpAddr()) == 0 ) {
				m_logger.error ( " Server Already exist in List ") ;
				return ;
			}
		}
		if ( server.getIpAddr() != null )
			m_servers.add(server);
		else 
			m_logger.error("IP address is null , So rejecting it  " ) ;	
	}

	/** if input is true then enables parallel query option , otherwise disables parallel query option
	 * @param flag parallel query option
	 */
	public void setParallelQueryOption( boolean flag ) {
		m_parallelQuery = flag ; 
	}
 
	/** Set the particular URI scheme. Application may want particular type of URIs 
	 * in reply to resolveSync/resolveAsync method.
	 * @param uriScheme Desired URI scheme by Application
	 */ 
	public void setURIScheme( String uriScheme) {
		m_uriScheme = uriScheme ;
	}

	/*
	// Unit Testing Code

	public String toString() {
      String newline = System.getProperty( "line.separator" );
      StringBuffer buf = new StringBuffer();

      buf.append( "--- Servers ---" ).append( newline );
		buf.append( "Size " + m_servers.size() ).append( newline );
      for( int i=0; i<m_servers.size(); i++ ){
		buf.append( (EnumServer)m_servers.get(i)) .append( newline );
         buf.append( ((EnumServer)m_servers.get(i)).getIpAddr() ).append( newline );
      }

      return buf.toString();
   	}
	
	public static void main( String args[] ) {
		System.out.println(" in main " );
		File input = new File( "/user/akabra/dns/dnsjava-2.0.3/enum-config.xml" );
		EnumContext ctx = new EnumContext();
		ctx = ctx.loadConfigParam(input);
		//System.out.println (ctx.toString());
		List servers = ctx.getServerList();
		
		Iterator iter = servers.iterator();
		while ( iter.hasNext() ) {		
			EnumServer ser = (EnumServer)iter.next();
			//System.out.println("server " + ser ) ;
			System.out.println("ip " + ser.getIpAddr() + " ConfigOption " +  ser.getConfigFlag() + "cacheOption " + ctx.getCacheOption());
		}
	}
	*/	

}
