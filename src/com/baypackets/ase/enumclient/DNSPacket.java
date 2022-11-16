/**
 * DNSPacket.java
 *
 *Created on March 16,2007
 */
package com.baypackets.ase.enumclient;

import org.xbill.DNS.*;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Flags;

import org.apache.log4j.Logger;

/**
 * This class defines the message that is queried to DNS server.
 * @author Ashish kabra
 */

public class DNSPacket {

	private String m_key;
	transient private static Logger m_logger =
          Logger.getLogger(DNSPacket.class);

	public DNSPacket(String key) {
		m_key= key;
	}

/*	public Message makePacket(String key ,  boolean recursionFlag) {
		Name name = null;
        int type = Type.NAPTR; 
        int dclass = DClass.IN; 

		try{
			 name = new Name(key) ;
		} catch (TextParseException e ) {
			m_logger.error("Key is not in standard format");	
		}
		
		Record question = Record.newRecord(name , type , dclass );
		Message query = Message.newQuery(question);
				
		//set recursion option in message
		if( recursionFlag ) {
			query.getHeader().setFlag(Flags.RD) ; 
		}
		return query ;
	} 
*/

	public Message makePacket(boolean recursionFlag) {
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entering : makePacket()" ) ;
        Name name = null;
        int type = Type.NAPTR;
        int dclass = DClass.IN;

        try{
             name = new Name(m_key) ;
        } catch (TextParseException e ) {
            m_logger.error("Key is not in standard format" + e.toString() );
        }

        Record question = Record.newRecord(name , type , dclass );
        Message query = Message.newQuery(question);
		if(m_logger.isDebugEnabled() )
			m_logger.debug(" Query to DNS server created : \n" + query.toString() ) ;
   
        //set recursion option in message
        if( recursionFlag ) {
			if(m_logger.isDebugEnabled() )
            	m_logger.debug(" Recursion flag being set " ) ;
            query.getHeader().setFlag(Flags.RD) ;
        }
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting : makePacket()" ) ;
        return query ;
    }

} 	
		
