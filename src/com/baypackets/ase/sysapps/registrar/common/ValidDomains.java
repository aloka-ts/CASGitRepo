/**
 * File:ValidDomains.java
 * @author Kameswara Rao
 *
 */

package com.baypackets.ase.sysapps.registrar.common;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.SQLException;

import org.apache.log4j.*;

import com.baypackets.ase.sysapps.registrar.dao.BindingsDAO;


/**
 * This class contains a table of all the valid domains and provides methods to validate the domain names in the various requests the registrar receives
 *
 */
public class ValidDomains
{
	//Data members
	/** The valid domain names for which registration can take place */
	private Hashtable<String, String> m_hashTable = null;

	private BindingsDAO m_bindingsDAO = null;

	private Logger log = Logger.getLogger(ValidDomains.class);

	//Constructors
	public ValidDomains(BindingsDAO bindingsDAO)
	{
		m_hashTable = new Hashtable<String, String>();

		this.m_bindingsDAO = bindingsDAO;

	}

	//Methods
	/**
	 * This method queries the data base using the bindingsDAO interface to fill the table with valid domains
	 * @throws NullPointerException
	 */
	public synchronized void fillTable()
	{
		ArrayList<String> domainList= null;
		try
		{	
			domainList= m_bindingsDAO.getValidDomains();

			if(domainList==null)
				return;

		}
		catch(SQLException e)
		{
			log.error(e.toString());
			return;
		}
		catch(Exception e)
		{
			log.error(e.toString());
			return;
		}
		
		for(String tempDomain : domainList){
			if(log.isDebugEnabled()){
				log.info("Adding" +tempDomain+"tpo the list");
			}
			m_hashTable.put(tempDomain, tempDomain);
		}
		
	}

	/**
	 * This method compares the string with the request uri's in the hashtable 
	 * @param String, the uri whose validity has to be checked
	 * @return boolean
	 *
	 */
	public boolean compare(String checkURI)
	{
		String[] temp = new String[2];
		if(checkURI.startsWith("sip:"))
		{
			temp = checkURI.split(":");
			checkURI = temp[1];	
			log.info(checkURI+"another log");
		}

		boolean contains=m_hashTable.containsKey(checkURI);

		return contains;
	}
	
	/**
	 * This method queries the data base using the bindingsDAO interface to fill the table with updated valid domains
	 * @throws NullPointerException
	 */
	
	public  synchronized void updateTable() {
		
		if(log.isDebugEnabled()){
			log.info("Inside updateTable method");
		}
		ArrayList<String> domainList = new ArrayList<String>();
		
		try {
			domainList = m_bindingsDAO.getValidDomains();
			
			if(domainList == null){
				log.error("Domain List is NULL");
				return;
			}
			
		} catch (SQLException e) {
			log.error(e.toString(),e);
			return;
		} catch (Exception e) {
			log.error(e.toString(),e);
			return;
		}
		
		Iterator<String> iterator = m_hashTable.keySet().iterator();
		
		while(iterator.hasNext()){
			String key = iterator.next();
			if(!domainList.contains(key)){
				m_hashTable.remove(key);
			}
		}
        //adding the new domain entries
        for(String tempDomain: domainList){
        	m_hashTable.put(tempDomain, tempDomain);
        }		
        
        if(log.isDebugEnabled()){
        	log.info("leaving updateTable method");
        }
	}
}
