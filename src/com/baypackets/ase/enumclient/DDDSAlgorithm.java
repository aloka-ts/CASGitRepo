/**

 * DDDSAlgorithm.java
 *
 *Created on March 19,2007
 */
package com.baypackets.ase.enumclient;

import org.xbill.DNS.Cache;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.RRset;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Record;
import org.xbill.DNS.Message;
import org.xbill.DNS.Section;
import org.xbill.DNS.ResolverListener;
import org.xbill.DNS.Resolver;

import java.lang.String;
import java.lang.StringBuffer;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.StringTokenizer;
import java.net.UnknownHostException;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.IOException;
//import com.genband.threadpool.*;
import com.baypackets.ase.spi.util.*;
import com.baypackets.ase.util.AseStrings;

/**
 * This class defines DDDSAlgorithm as per defined in RFC-3402
 * @author Ashish kabra
 */
public class DDDSAlgorithm {

	private static final String UDP = "UDP";
	private static final String TCP = "TCP";
	private String m_AUS;
	private String m_key;
	private EnumContext m_ctx;
	private EnumListener m_lsnr;
	private ExtendedResolver  m_resolver;
	private List uriList=new ArrayList() ;
	private int jj = 0;  //Ashish for testing non term NAPTR
	private Tree tree ;
    transient private static Logger m_logger =
          Logger.getLogger(DDDSAlgorithm.class);
	
	public DDDSAlgorithm (String aus, String key) {
		m_AUS = aus ; 
		m_key = key;
	}	
	
	public DDDSAlgorithm (EnumContext ctx , String aus, String key) {
		this(aus, key);
		m_ctx = ctx;
    }

	public DDDSAlgorithm (EnumContext ctx , EnumListener lsnr , String aus, String key) {
		this(ctx, aus, key );
		m_lsnr = lsnr;
    }

	/** Takes array of unsorted Records as input and sort  them in increasing 
	  * order of order field . If the order field is same then sorting is done 
	  * in increasing order of preference field of NAPTRs.
		@param Record[] record array to be sort
		@param n number of records in array
		@return sorted record array
	 */	
	private Record[] sortNAPTR(Record[] rec ) {
		boolean SWITCHED=true;
		int n = rec.length ;
		if(m_logger.isDebugEnabled() ) 
			m_logger.debug(" Entered :sortNAPTR " ) ;
		for (int pass =0; pass<n-1 && SWITCHED ; pass++ ) {
			SWITCHED = false ; 
			for(int j=0 ; j<n-pass-1 ; j++ ) {
				if(((NAPTRRecord)rec[j]).getOrder() > ((NAPTRRecord)rec[j+1]).getOrder() ) {
					SWITCHED = true;
					Record hold = rec[j] ; 
					rec[j] = rec[j+1];
					rec[j+1] = hold;
				} else if (((NAPTRRecord)rec[j]).getOrder() == ((NAPTRRecord)rec[j+1]).getOrder() ) {
					if( ((NAPTRRecord)rec[j]).getPreference() > ((NAPTRRecord)rec[j+1]).getPreference() ) {
						SWITCHED = true;
                   		Record hold = rec[j] ;
                    	rec[j] = rec[j+1];
                    	rec[j+1] = hold;
					}
				}
			}
		}
		 if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting :sortNAPTR " ) ;	
		return rec;	
	}


	/** Checks wether flag is true or not. For Enum Application only "u" and "" 
		flags are allowed. All other flags are invalid.
		@param record Record for which flag field is to be checked
		@return true if flag is valid else false
	 */ 
	private boolean isValidFlag(Record record) {
		NAPTRRecord rec = (NAPTRRecord)record ;
		String flag = rec.getFlags();
		if( flag.compareToIgnoreCase("u")==0 || flag.compareToIgnoreCase("")==0)
			return true;
		return false;
	}
	/** Checks wether service is valid or not. For ENUM application only "E2U"
		service is allowed.
		 @param record Record for which service field is to be checked
		@return true if service field is "E2U" else false	
	  */ 
	private boolean isValidService(Record record) {
		NAPTRRecord rec = (NAPTRRecord)record ;
		String service = rec.getService();
		service = service.substring(0, service.indexOf(AseStrings.PLUS)).trim();
		if(service.compareTo("E2U") ==0)	
			return true;
		return false;
	}

    private String changeRegex ( String str) {
        StringBuffer sb = new StringBuffer(str ) ;
        int index=0;
        String returnStr = str;
        while ( index >=0 ) {
            index = -1;
            if ( sb.indexOf(AseStrings.SLASH_ONE) != -1 ) {
                index = sb.indexOf(AseStrings.SLASH_ONE);
                sb = sb.replace (index,index+1,AseStrings.DOLLAR_ONE );
            }

            if ( sb.indexOf(AseStrings.SLASH_TWO) != -1 ) {
                index = sb.indexOf(AseStrings.SLASH_TWO);
                 sb = (sb.replace (index,index+1,AseStrings.DOLLAR_TWO ));
            }
            if ( sb.indexOf(AseStrings.SLASH_THREE) != -1 ) {
                index = sb.indexOf(AseStrings.SLASH_THREE);
                returnStr = new String(sb.replace (index,index+1,AseStrings.DOLLAR_THREE));
            }

            if ( sb.indexOf(AseStrings.SLASH_FOUR) != -1 ) {
                index = sb.indexOf(AseStrings.SLASH_FOUR);
                 returnStr = new String(sb.replace (index,index+1,AseStrings.DOLLAR_FOUR));
            }
            if ( sb.indexOf(AseStrings.SLASH_FIVE) != -1 ) {
                index = sb.indexOf(AseStrings.SLASH_FIVE);
                returnStr = new String(sb.replace (index,index+1,AseStrings.DOLLAR_FIVE));
            }

            if ( sb.indexOf(AseStrings.SLASH_SIX) != -1 ) {
                index = sb.indexOf(AseStrings.SLASH_SIX);
                returnStr = new String(sb.replace (index,index+1,AseStrings.DOLLAR_SIX));
            }
            if ( sb.indexOf(AseStrings.SLASH_SEVEN) != -1 ) {
                index = sb.indexOf(AseStrings.SLASH_SEVEN);
                returnStr = new String(sb.replace (index,index+1,AseStrings.DOLLAR_SEVEN));
            }
        }
        returnStr = new String(sb);
        return returnStr ;
    }

	
	private int setResolverParameters( Resolver[] resolvers) {
		//getList of server available
        List servers = m_ctx.getServerList();
	
		int retries = -1 ;
		int j=0;
		for ( int i =0 ; i<resolvers.length ; i++ ) { 
			EnumServer server = (EnumServer)servers.get(i);
			String[] str = new String[] {server.getIpAddr() } ;
			if(m_logger.isDebugEnabled()) {

			m_logger.debug("Resolver" + i + " is : " + server.getIpAddr() );
			}
			Resolver res = resolvers[i] ;

			if ( server.getPort() != 0 ) 
				res.setPort( server.getPort() ) ;
			else 
				res.setPort( 53 ) ;

			if ( server.getRetries() >0  && j==0 ) {
				retries = server.getRetries();
				j++;
			}
			
			if ( server.getProtocol().compareToIgnoreCase(UDP) == 0 ) 
				res.setTCP(false ) ;
			else if ( server.getProtocol().compareToIgnoreCase(TCP) == 0 ) {
				res.setTCP( true ) ;
			} else {
				m_logger.error( " not entered \"UDP/TCP\" , setting protocol as a default protocol TCP " ) ;	
				res.setTCP( true ) ; 
			}

			if ( server.getTimeout() != 0 ) 
				res.setTimeout( server.getTimeout() ) ;
			else 
				res.setTimeout( 60 ) ;

		}
		
		if ( retries < 0 )
			retries=3 ;
		return retries;
	}


    /** Takes URI scheme and a list of URI scheme as input and 
        returns list which contains URI as specified by input scheme.
        @param scheme Name of scheme 
        @param list list which is to be filtered
        @return Filtered list
      */
	private synchronized List filterByScheme(String scheme, List list) {
		if (m_logger.isDebugEnabled()) {

			m_logger.debug("in filterByScheme() ");
			m_logger.debug(" Scheme is : " + scheme);
		}
		if (list == null)
			return null;
		ArrayList newList = new ArrayList();
		Iterator iter = list.iterator();
		if (iter != null) {
			while (iter.hasNext()) {
				String uri = (String) iter.next();
				if (m_logger.isDebugEnabled()) {

					m_logger.debug("uri is  " + uri);
				}
				if (scheme.equals("cic") && uri.indexOf("cic") != -1) {
					if (m_logger.isDebugEnabled()) {

						m_logger.debug("add uri to list   " + uri);
					}
					newList.add(uri);
				} else {
					StringTokenizer st = new StringTokenizer(uri, ":");
					String uriScheme = st.nextToken();
					if (uriScheme.compareToIgnoreCase(scheme) == 0)
						newList.add(uri);
				}
			}
		}

		return newList;
	}


    /** This method resolves key to URIs found in Cache
     * @param aus URI scheme
     * @param key DNS key
     *@return list of URIs found in cache
     */
    private List<RRset> resolveFromCache(String key) {
        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entering :resolveFromCache " ) ;
        Name name = null;
        try{
            name = new Name(key);
        }catch(TextParseException e) {
            m_logger.error(" not a right key " + e.getMessage() , e) ;
        }
        if( key ==null ) {
            m_logger.error("key is null , no further processing returning from here Exiting resolveFromCache" );
            return null;
         }

        //RRset[] recordSet = EnumResolver.getCache().findAnyRecords(name , Type.NAPTR);
		List<RRset> recordSet = null ;
		synchronized ( EnumResolver.getCache() ) {
        	recordSet = EnumResolver.getCache().lookupRecords(name , Type.NAPTR,1).answers();
		}

		if ( recordSet != null ) { 
			if(m_logger.isDebugEnabled()) {

			m_logger.debug( "found entry in cache" ) ;
			}
		}

        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting :resolveFromCache() " ) ;

        return recordSet ;
    
    }

    /** This method synchronously resolves data base key to list of URIs
        @param key FQDN
        @return list of resolved URIs
     */
    public List resolveSync( String key) throws EnumException{
        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entered : resolveSync()" ) ;

        ExtendedResolver resolver = null ;
        List servers = m_ctx.getServerList();
        int numberOfServer = servers.size();
        String[] str= new String[numberOfServer];

        if ( numberOfServer <= 0) {
            m_logger.error(" No host to resolver ; throwing enum exception " ) ;
            throw new EnumException( "No Host for Resolution " ) ;
            //return ;
        }
        //getting IPAddresses in string array
        for ( int i=0; i< numberOfServer ; i++ )
            str[i] = ((EnumServer)servers.get(i)).getIpAddr();

        try {
            resolver = new ExtendedResolver(str ) ;
            Resolver[] res = resolver.getResolvers() ;
            int retries = setResolverParameters(res);
            resolver.setRetries(retries);
        } catch(UnknownHostException e ) {
            m_logger.error("Hosts name not correct : " + e.toString() );
            throw  new EnumException( "Hosts name not correct  " ) ;
        }

        if ( resolver == null ) {
            m_logger.error(" ExtendedResolver is null , try again with proper EnumServer/s " ) ;
            throw  new EnumException( " ExtendedResolver is null  " );
            //return;
        }

        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Extended resolver created for DNS" ) ;
		List list = null;
		list =  resolveSync( key , 0, resolver );

        String scheme = m_ctx.getURIScheme();
        if (scheme !=null && scheme.compareToIgnoreCase("") != 0 )
            list = this.filterByScheme(scheme,list);
        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting :resolveSync " ) ;
        return list ;		
	}	
    
    
    /** This method synchronously resolves data base key to list of URIs
    @param key FQDN
    @return list of resolved URIs
 */
public List<RRset> resolveSyncRRSet( String key) throws EnumException{
    if(m_logger.isDebugEnabled() )
        m_logger.debug(" Entered : resolveSync()" ) ;

    ExtendedResolver resolver = null ;
    List servers = m_ctx.getServerList();
    int numberOfServer = servers.size();
    String[] str= new String[numberOfServer];

    if ( numberOfServer <= 0) {
        m_logger.error(" No host to resolver ; throwing enum exception " ) ;
        throw new EnumException( "No Host for Resolution " ) ;
        //return ;
    }
    //getting IPAddresses in string array
    for ( int i=0; i< numberOfServer ; i++ )
        str[i] = ((EnumServer)servers.get(i)).getIpAddr();

    try {
        resolver = new ExtendedResolver(str ) ;
        Resolver[] res = resolver.getResolvers() ;
        int retries = setResolverParameters(res);
        resolver.setRetries(retries);
    } catch(UnknownHostException e ) {
        m_logger.error("Hosts name not correct : " + e.toString() );
        throw  new EnumException( "Hosts name not correct  " ) ;
    }

    if ( resolver == null ) {
        m_logger.error(" ExtendedResolver is null , try again with proper EnumServer/s " ) ;
        throw  new EnumException( " ExtendedResolver is null  " );
        //return;
    }

    if(m_logger.isDebugEnabled() )
        m_logger.debug(" Extended resolver created for DNS" ) ;
	List<RRset> list = null;
	list =  resolveSyncRRSet( key , 0, resolver );

    if(m_logger.isDebugEnabled() )
        m_logger.debug(" Exiting :resolveSync " ) ;
    return list ;		
}	
    
    /**
     * This method is used by EnumReceiver( server) to resolve incoming enum request
     * @param key
     * @param recursionCounter
     * @param resolver
     * @return
     * @throws EnumException
     */
    private List<RRset> resolveSyncRRSet( String key, int recursionCounter, ExtendedResolver resolver) throws EnumException{
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entered : resolveSync()" ) ;

        List<RRset> recordSet = null;
        //RRset[] recordSet = null;
		
        int maxRecursion = m_ctx.getMaxRecursion();
        //get Recursion option
        boolean recursion = m_ctx.getRecursionOption();

		boolean cacheOption = m_ctx.getCacheOption();

		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Cache option : " + cacheOption ) ;

		if ( cacheOption ) {
			recordSet = resolveFromCache(key);
			if (recordSet != null ) {
				//recordSet = recordSet1;
				if(m_logger.isDebugEnabled() )
            		m_logger.debug(" Entry found in cache" ) ;
			}
		}
		
		if ( recordSet == null ) {
		
			//makes packet to query DNS servers
			DNSPacket packet = new DNSPacket( key );		
			Message query = packet.makePacket(recursion);
			Message response = null;

			try {
				if(m_logger.isDebugEnabled() )
   	         		m_logger.debug(" Sending query to DNS server/s" ) ;
					response = EnumSender.getInstance().sendSynch(query,m_ctx.getServerList());//resolver.send(query);
				if(response ==null) {
					m_logger.error(" Response received by DNS server is null  , returning from here " ) ;
					return null;
				}
			}catch(Exception e ) {//IOException
				m_logger.error(" problem in sending query to resolver " + e.getMessage() , e) ;	
				throw new EnumException( " problem in sending query to resolver " );
				//return null;
			}
		 	
			int responseCode ;
			if ( response != null )
				responseCode = response.getHeader().getRcode();
			else {
				m_logger.error(" Response received by DNS server is null  , returning from here " ) ;
                return null;
            } 

			if(m_logger.isDebugEnabled() )
				m_logger.debug(" response code of message returned : " + responseCode ) ;	
	
			if( responseCode ==1 )
   		          throw new EnumException("Format error - The name server was unable to interpret the query."
									+	"Response Code " + responseCode, responseCode);
			if( responseCode ==2 )
				throw new EnumException(" Server failure : Response Code : " + responseCode, responseCode); 
       
			if( responseCode ==4 )
   		         throw new EnumException(" Name Server does not support this kind of query  : Response Code : " 
									+ responseCode, responseCode);
 
			if( responseCode ==5 )
   		         throw new EnumException(" Refused by NameServer for Policy Reasons :  Response Code : " 
									 + responseCode, responseCode);

            if( cacheOption ) {
                //may add code for authoritative answer and may set credibility according to th
            	if(m_logger.isInfoEnabled() )
            		m_logger.info("Adding Message to Cache " ) ;
                 synchronized ( EnumResolver.getCache() ) {
                    EnumResolver.getCache().addMessage(response);
                 }
            }

			 recordSet = response.getSectionRRsets(Section.ANSWER);
		}
		return recordSet;
    }
	
	/** This method synchronously resolves data base key to list of URIs
		@param key FQDN
		@param recursionCounter number of times recursed
		@param resolver Extended Resolver to be used
		@return list of resolved URIs
	 */
	private List resolveSync( String key, int recursionCounter, ExtendedResolver resolver) throws EnumException{
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entered : resolveSync()" ) ;

		List<RRset> recordSet = null;
        //RRset[] recordSet = null;
		
        int maxRecursion = m_ctx.getMaxRecursion();
        //get Recursion option
        boolean recursion = m_ctx.getRecursionOption();

		boolean cacheOption = m_ctx.getCacheOption();

		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Cache option : " + cacheOption ) ;

		if ( cacheOption ) {
			recordSet = resolveFromCache(key);
			if (recordSet != null ) {
				//recordSet = recordSet1;
				if(m_logger.isDebugEnabled() )
            		m_logger.debug(" Entry found in cache" ) ;
			}
		}
		
		if ( recordSet == null ) {
		
			//makes packet to query DNS servers
			DNSPacket packet = new DNSPacket( key );		
			Message query = packet.makePacket(recursion);
			Message response = null;

			try {
				if(m_logger.isDebugEnabled() )
   	         		m_logger.debug(" Sending query to DNS server/s" ) ;
					response =  EnumSender.getInstance().sendSynch(query,m_ctx.getServerList());//resolver.send(query);
				if(response ==null) {
					m_logger.error(" Response received by DNS server is null  , returning from here " ) ;
					return null;
				}
			}catch(Exception e ) {
				m_logger.error(" problem in sending query to resolver " + e.getMessage() , e) ;	
				throw new EnumException( " problem in sending query to resolver " );
				//return null;
			}
		 	
			int responseCode ;
			if ( response != null )
				responseCode = response.getHeader().getRcode();
			else {
				m_logger.error(" Response received by DNS server is null  , returning from here " ) ;
                return null;
            } 

			if(m_logger.isDebugEnabled() )
				m_logger.debug(" response code of message returned : " + responseCode ) ;	
	
			if( responseCode ==1 )
   		          throw new EnumException("Format error - The name server was unable to interpret the query."
									+	"Response Code " + responseCode, responseCode);
			if( responseCode ==2 )
				throw new EnumException(" Server failure : Response Code : " + responseCode, responseCode); 
       
			if( responseCode ==4 )
   		         throw new EnumException(" Name Server does not support this kind of query  : Response Code : " 
									+ responseCode, responseCode);
 
			if( responseCode ==5 )
   		         throw new EnumException(" Refused by NameServer for Policy Reasons :  Response Code : " 
									 + responseCode, responseCode);

            if( cacheOption ) {
                //may add code for authoritative answer and may set credibility according to th
            	if(m_logger.isInfoEnabled() )
            		m_logger.info("Adding Message to Cache " ) ;
                 synchronized ( EnumResolver.getCache() ) {
                    EnumResolver.getCache().addMessage(response);
                 }
            }

			recordSet = response.getSectionRRsets(Section.ANSWER);
		}

		 /* THIS UT CODE NOT DELETED BECAUSE IT WILL BE USED FOR IT PURPOSE ALSO
            
            // Ashish Change recordSet1 to recordSet above  Testing non-Terminal NAPTR  *****************start****************
             Name name = null; 
             recordSet = new RRset[1];
             Name repl1 = null;
             Name repl2=null;
             try {      
                 name = new Name("8.7.3.4.e164.arpa.");
                 repl1 = new Name("7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
                 repl2= new Name(".");

            } catch (TextParseException e)
            {}
            //int jj =1 ; declare jj as static int at top
            //if ( jj>1 )
            NAPTRRecord reco1 = new NAPTRRecord(repl1, 1, 12121 , 100 , 7, "u" , "E2U+sip" , "!^.*$!sip:57@aarnet.edu.au!",repl2 );
            NAPTRRecord reco2 = new NAPTRRecord(repl1, 1, 12121 , 100 , 7, "" , "E2U+sip" , "",repl1 );
            RRset rrset = new RRset ();
            rrset.addRR( (Record)reco2 ) ;
            rrset.addRR( (Record)reco1 ) ;
			if ( jj==0 ) {
				response.removeAllRecords(1);
				response.addRecord(reco1 , 1);
				response.addRecord(reco2 , 1);
			}
			EnumResolver.getCache().addMessage(response);
            //RRset rrset1 = new RRset ( (Record)reco1 ) ;
            recordSet[0]= rrset ;
            //recordSet[1]= rrset1 ;
            if ( jj > 0 ) {
                recordSet[0].clear();
                recordSet =  response.getSectionRRsets(Section.ANSWER);
                //recordSet = recordSet1;
            }
            //System.out.println("  value of jj " + jj + "\n" ) ;
            jj++;
            // ************ testing non Terminal NAPTR ends ********************

             */

		//}
		
		ArrayList recordArray = new ArrayList();
	//	Iterator<RRset> iter;
	//	for( int n=0 ; n<recordSet.size() ; n++) {
//			iter = recordSet.iterator();//[n].rrs();	
//			if( iter != null ) {
//				while (iter.hasNext() ) {
//					recordArray.add(iter.next());
//				}
		Iterator<RRset> iter = recordSet.iterator();// .[n].rrs();
		if (iter != null) {
			while (iter.hasNext()) {
				RRset rset = iter.next();
				List<Record> records = rset.rrs();
				Iterator<Record> recordItr = records.iterator();
				if (recordItr != null) {
					while (recordItr.hasNext()) {
						recordArray.add(recordItr.next());
					}
				}
				// recordList[size++] = (NAPTRRecord)iter.next();
			}
		}
				
	//	}
		
		//sort the record array 
		int numberOfRecords = recordArray.size();
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Number of records returned in response : " + numberOfRecords ) ;
		Record[] sortedRecord =new Record[numberOfRecords];
		Record[] recordList = new Record[numberOfRecords];
		if (numberOfRecords > 0 ) {
			for (int i=0; i<numberOfRecords ; i++) {
				recordList[i] = (Record)recordArray.get(i);
				if(m_logger.isDebugEnabled() ) {
            		m_logger.debug(" " + recordArray.get(i).toString() ) ; }
			}
			 sortedRecord = sortNAPTR(recordList);
		} else {
			m_logger.error("No record is returned in response " );
			return null;
		}

		//printing Sorted Records 
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Sorted records are : " ) ;
		for( int n=0 ; n<sortedRecord.length ; n++) {
			if(m_logger.isInfoEnabled() )
            	m_logger.info(" " + sortedRecord[n].toString() ) ;
		}
		
		
		//boolean cacheOption = m_ctx.getCacheOption();
		for( int n=0 ; n<sortedRecord.length ; n++) {
			if(m_logger.isDebugEnabled() )
            	m_logger.debug(" \n Processing following NAPTR : " + sortedRecord[n].toString() ) ;
			if ( !isValidFlag(sortedRecord[n]) ) {
				if(m_logger.isDebugEnabled() )
            		m_logger.debug(" Flag Not Valid , SO rejecting this NAPTR" ) ;
				continue;
			}

			// if NAPTR is terminal one
			if ( (((NAPTRRecord)sortedRecord[n]).getFlags()).compareToIgnoreCase("u") ==0 ) {

			    // in case of non terminal NAPTR just ignore this field
            	if(!isValidService(sortedRecord[n])) {
                	if(m_logger.isDebugEnabled() )
                    	m_logger.debug(" Not valid service , SO rejecting this NAPTR " ) ;
                	continue;
            	}
				/*
				if( cacheOption ) {
					//may add code for authoritative answer and may set credibility according to th
					if(m_logger.isInfoEnabled()) {

					m_logger.info("Adding Record to Cache " ) ;
					}
					synchronized ( EnumResolver.getCache() ) { 
						EnumResolver.getCache().addRecord(sortedRecord[n] ,5,response);
					}
				}
				*/
				if(m_logger.isInfoEnabled()) {
				m_logger.info ( " This is  a Terminal NAPTR " ) ;
				}
          	 	String regexp = ((NAPTRRecord)sortedRecord[n]).getRegexp();
                //if regexp field is null string then go to replacement field
                if (regexp.compareTo("") == 0) {
					if(m_logger.isInfoEnabled()) {
					m_logger.info ( " regex field is empty , so replacing with Replacement field " ) ;
					}
                	String repl = ((NAPTRRecord)sortedRecord[n]).getReplacement().toString();
					try { 
						URI uri = new URI(repl);
                        uriList.add(uri);
					} catch ( URISyntaxException e) { 
						m_logger.error( " Not a valid URI to add , continuing with next NAPTR : " + e.getMessage() , e ) ;
					}
                  	continue;
              	}

         		 //  here code for appying AUS to regexp field 
                 StringTokenizer st = new StringTokenizer(regexp, "!");
                 String reg = st.nextToken();
                 String replValue = st.nextToken(); 
				 replValue = changeRegex( replValue ) ;
                 uriList.add(m_AUS.replaceAll(reg,replValue) );
				
          	} else {
				if ( !recursion ) {
					if(m_logger.isDebugEnabled()) {
					m_logger.debug( "Non Terminal NAPTR , but recursion not supported "+
								" so continuing with next NAPTR	" ) ;
					}
					continue;
				}
				if(m_logger.isDebugEnabled() )
            		m_logger.debug(" non-Terminal NAPTR Processing " ) ;

				//incase of non-Terminal NAPTR
				//substitution field is null , then go for replacement field 
				String newKey = null;
				String regexp = ((NAPTRRecord)sortedRecord[n]).getRegexp();
				if (regexp.compareTo("") == 0) {
          	   	   newKey = ((NAPTRRecord)sortedRecord[n]).getReplacement().toString();
                   // apply DDDS algorithm again to find other key 
                 } else {
					
					//apply AUS to regexp field to find newkey
                    StringTokenizer st = new StringTokenizer(regexp, "!");
                    String reg = st.nextToken();
                    String replValue = st.nextToken();
                    newKey = m_AUS.replaceAll(reg,replValue) ;
					
				}
	
				// apply DDDS algorithm again to find other key. 
				if ( recursionCounter <= maxRecursion ) {
                	resolveSync(newKey,++recursionCounter , resolver);
				}
            	else { 
					if(m_logger.isDebugEnabled()) {
					m_logger.debug( "Maximum nuber of recursion exceeded : so ignoring this NAPTR and continuing with other NAPTR " ) ;
					}
					continue;
					//return null;
				}
			}// non terminal NAPTR procession over
			
		} //end for //record array processing over	
		return uriList;
	}

	
    /** This method Asynchronously resolves data base key to list of URIs
        @param key FQDN
      */
 	public void resolveAsync(String key) {
        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entered : resolveAsync()" ) ;

        ExtendedResolver resolver = null ;
        List servers = m_ctx.getServerList();
        int numberOfServer = servers.size();
        String[] str= new String[numberOfServer];

        if ( numberOfServer <= 0) {
            m_logger.error(" No host to resolver ; throwing enum exception " ) ;
            m_lsnr.handleException( new EnumException( "No Host for Resolution " ) ) ;
            return ;
        }
		//getting IPAddresses in string array 
        for ( int i=0; i< numberOfServer ; i++ )
            str[i] = ((EnumServer)servers.get(i)).getIpAddr();

        try {
            resolver = new ExtendedResolver(str ) ;
            Resolver[] res = resolver.getResolvers() ;
            int retries = setResolverParameters(res);
            resolver.setRetries(retries);
        } catch(UnknownHostException e ) {
            m_logger.error("Hosts name not correct : " + e.toString() );
			m_lsnr.handleException( new EnumException( "Hosts name not correct  " ) ) ;
        }

		if ( resolver == null ) {
			m_logger.error(" ExtendedResolver is null , try again with proper EnumServer/s " ) ;
			m_lsnr.handleException( new EnumException( " ExtendedResolver is null  " ) ) ;
			return;
		}

		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Extended resolver created for DNS" ) ;
	
		boolean recursiveFlag = false;
		if(m_logger.isInfoEnabled() )
            m_logger.info(" Initializing tree " ) ;
		tree = new Tree(key);
		dddsAsync(key,resolver,recursiveFlag, tree.getRoot() , 0);
	}

	private void dddsAsync(String key, ExtendedResolver resolver , 
							 boolean recursiveFlag , TreeNode node , int recursionCounter) {
        boolean recursion = m_ctx.getRecursionOption();
        List<RRset> recordSet = null;
		if ( m_ctx.getCacheOption() ) {
			recordSet =  resolveFromCache(key);
		}

		if ( recordSet != null ) {
			if(m_logger.isInfoEnabled() )
            	m_logger.info(" found record set length in cache " + recordSet.size() ) ;

			Message msg = new Message();
			msg.getHeader().setRcode(0);
			//Iterator<RRset> iter;
        	//for( int n=0 ; n<recordSet.length ; n++) {
//	         	iter = recordSet.iterator();//[n].rrs();
//           		 	if( iter != null ) {
//           	     	while (iter.hasNext() ) {
//           	         	msg.addRecord((Record)iter.next().first(), 1);
//           	     	}
//           	 	}

			Iterator<RRset> iter = recordSet.iterator();// .[n].rrs();
			if (iter != null) {
				while (iter.hasNext()) {
					RRset rset = iter.next();
					List<Record> records = rset.rrs();
					Iterator<Record> recordItr = records.iterator();
					if (recordItr != null) {
						while (recordItr.hasNext()) {
							msg.addRecord(recordItr.next(), 1);
						}
					}
					// recordList[size++] = (NAPTRRecord)iter.next();
				}
			}
   	     //	}
			
			boolean resolvedFromCacheFlag = false ;
			if ( recursionCounter > 0 ) 
				resolvedFromCacheFlag = true ;

			AsyncData async = new AsyncData( msg , resolver , recursiveFlag , node , recursionCounter, resolvedFromCacheFlag);
				//async.setMessage(msg);
				async.execute();
		} else {
		
	        //make packet to query DNS servers
	        DNSPacket packet = new DNSPacket( key );
   		     Message query = packet.makePacket(recursion);

	        if(m_logger.isDebugEnabled() )
   		         m_logger.debug(" Sending query to DNS server/s aynschrounously" ) ;
	        AsyncResolution listener = new AsyncResolution(resolver,recursiveFlag, node , recursionCounter);
	        Object id = EnumSender.getInstance().sendAsynch(query,listener,m_ctx.getServerList());//resolver.sendAsync(query, listener);
	        listener.setId(id);
		}
	}
		

	// Internal class which implements ResolverListener interface

	private class AsyncResolution implements ResolverListener {
		private Object _id;
		private boolean _recursiveFlag;
		private ExtendedResolver _resolver;
		private TreeNode _node;
		private int _recursionCounter;
		public AsyncResolution(ExtendedResolver resolver, 
								boolean recursiveFlag , TreeNode node, int recursionCounter) {
			_resolver =  resolver;
			_recursiveFlag = recursiveFlag;
			_node = node;
			_recursionCounter = recursionCounter ;	
		}

		public void handleException(Object id, Exception e) {
			
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("handleException id : " + e.toString() ) ;
			}
			if (id == _id) {
				if( !_recursiveFlag ) 
					m_lsnr.handleException(e);	
				else  {
					synchronized(tree) {
                        synchronized(_node ) {
							if (!_node.isTerminal() && _node.getChildren() == null)
								_node.setDone(true);
							if (  tree.isTreeProcessingOver() ) {
								tree.makeList(uriList);
            					//filter list according to URI scheme
            					String scheme = m_ctx.getURIScheme();
            					if(m_logger.isDebugEnabled()) {
            						m_logger.debug("scheme is : " +scheme ) ;
            					}
            					if (scheme !=null && scheme.compareToIgnoreCase("") != 0 )
                					uriList = filterByScheme(scheme,uriList);
            					m_lsnr.receiveUriList(uriList) ;
        					}
							m_logger.error( "For non terminal NAPTR , returned following exception from DNS : " + e.toString() ) ;
						}
					}
				}
			}
		}

		public void receiveMessage(Object id, Message response) {
			
			if(m_logger.isDebugEnabled()) {
				m_logger.debug(" DDDS :inside receiveMessage for id " +id) ;
			}
			
			if ( response != null ){//&& id == _id) {

			/*
			// ********* Testing  ***************
	             Name name = null;
   		          RRset[] recordSet = new RRset[1];
   		          Name repl1 = null;
	             Name repl2=null;
   		          try {
   		              name = new Name("8.7.3.4.e164.arpa.");
   		              repl1 = new Name("7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
   		              repl2= new Name(".");
		
   		         } catch (TextParseException e)
   		         {}
   		         NAPTRRecord reco1 = new NAPTRRecord(repl1, 1, 12121 , 100 , 7, "u" , "E2U+sip" , "!^.*$!sip:57@aarnet.edu.au!",repl2 );
   		         NAPTRRecord reco2 = new NAPTRRecord(repl1, 1, 12121 , 100 , 7, "" , "E2U+sip" , "",repl1 );
				if ( jj == 0 ) {
					response.removeAllRecords(1);
					response.addRecord(reco1 , 1 ) ;
					response.addRecord(reco2 , 1 ) ;
				}
				jj++;
			*/

		        if ( m_ctx.getCacheOption() ) {
					synchronized ( EnumResolver.getCache() ) {
						if(m_logger.isInfoEnabled()) {
						m_logger.debug ( " Adding response to the cache " + response ) ;
						}
   	                 	EnumResolver.getCache().addMessage(response);
  	              	}
				}else{
					if(m_logger.isInfoEnabled()) {
						m_logger.debug ( " cache not enabled submit "+ response ) ;
						}
				}
				AsyncData data = new AsyncData( response,  _resolver, _recursiveFlag, _node, _recursionCounter, false);
				EnumResolver.getThreadPool().submit(data);

			} else {
				m_logger.error ( " receiveMessage  : response is null " ) ;
				m_lsnr.handleException(new EnumException("Response returned from DNS server is null " )) ;
			}
		}

        public void setId(Object id) {
            _id = id ;
        }

        public Object getId () {
            return _id;
        }

    }

    private class AsyncData implements Work {
		private ExtendedResolver _resolver;
        private Message  _message;
        private String _AUS=null;
		private boolean _recursiveFlag;
		private TreeNode _node;
		private int _recursionCounter;
		private boolean _resolvedFromCache;

		public AsyncData () {
        }
        public AsyncData (Message obj ,ExtendedResolver resolver, 
							boolean recursiveFlag, TreeNode node, int recursionCounter, boolean resolvedFromCache) {
            _message=obj;
			_resolver = resolver;
			_recursiveFlag = recursiveFlag;
			_node = node;
			_recursionCounter = recursionCounter;
			 _resolvedFromCache = resolvedFromCache;
        }

		public Message getMessage() { 
            return _message;
        }

		public void setMessage(Message msg ) {
            _message = msg ;
        }


        public String getAUS () {
            return _AUS;
		}
        public WorkListener getWorkListener() {return null ; } ;
        public int getTimeout(){ return 0; } ;
		
		public void execute( ) {
			if(m_logger.isInfoEnabled()) {	
			m_logger.info(" inside execute method ,  tree id is :"  + tree ) ;
			}
			int responseCode = _message.getHeader().getRcode();
	        int maxRecursion = m_ctx.getMaxRecursion();

            if( responseCode >0 && responseCode != 3) {
				if ( !_recursiveFlag ) {
                	m_lsnr.handleError(responseCode);
				} else {
					synchronized(tree) {
						synchronized(_node ) { 
							m_logger.error( " response code greater than 0 " ) ; 
							if (!_node.isTerminal() && _node.getChildren() == null) {
								_node.setDone(true);
							}
						}
						if (  tree.isTreeProcessingOver() && !_resolvedFromCache )
							returnOnCompletion();
					}
				}
				return;
            }

			List<RRset> recordSet = _message.getSectionRRsets(Section.ANSWER);

			ArrayList recordArray = new ArrayList();
			// Record[] recordList =null;
			// for( int n=0 ; n<recordSet.length ; n++) {
			Iterator<RRset> iter = recordSet.iterator();// .[n].rrs();
			if (iter != null) {
				while (iter.hasNext()) {
					RRset rset = iter.next();
					List<Record> records = rset.rrs();
					Iterator<Record> recordItr = records.iterator();
					if (recordItr != null) {
						while (recordItr.hasNext()) {
							recordArray.add(recordItr.next());
						}
					}
					// recordList[size++] = (NAPTRRecord)iter.next();
				}
			}
        //	}
			
        	//sort the record array
        	int numberOfRecords = recordArray.size();

			if(m_logger.isDebugEnabled() )
	            m_logger.debug(" Number of records returned in response : " + numberOfRecords ) ;

       		Record[] sortedRecord =new Record[numberOfRecords];
        	Record[] recordList = new Record[numberOfRecords];
        	if (numberOfRecords > 0 ) {
             	for (int i=0; i<numberOfRecords ; i++) {
                	recordList[i] = (Record)recordArray.get(i);
					if(m_logger.isDebugEnabled() )
                    	m_logger.debug(" " + recordArray.get(i).toString() ) ;
				}
             	sortedRecord = sortNAPTR(recordList);
        	} else {
				if ( !_recursiveFlag ) {
            		m_lsnr.receiveUriList(null) ;
				} else {
					synchronized(tree) {
					synchronized(_node ) {
                    	if (!_node.isTerminal() && _node.getChildren() == null) {
                        	_node.setDone(true);
                    	}
                	} 
					if (  tree.isTreeProcessingOver() && !_resolvedFromCache )
						returnOnCompletion();
					}
				}	
				return;
			}

        	//printing Sorted Records 
        	if(m_logger.isDebugEnabled() )
            	m_logger.debug(" Sorted records are : " ) ;
       	 	for( int n=0 ; n<sortedRecord.length ; n++) {
            	if(m_logger.isInfoEnabled() )
                	m_logger.info(" " + sortedRecord[n].toString() ) ;
        	}
	

        	boolean cacheOption = m_ctx.getCacheOption();
			if(m_logger.isDebugEnabled() )
                  m_logger.debug(" Cache option : " + cacheOption ) ;
			//NAPTR processing 
			synchronized (tree ) {
				synchronized (_node ) {
        			for( int n=0 ; n<sortedRecord.length ; n++) {
						if(m_logger.isDebugEnabled() )
	           			    m_logger.debug(" \n Processing following NAPTR : " + sortedRecord[n].toString() ) ;

           				if ( !isValidFlag(sortedRecord[n]) ) {
							if(m_logger.isDebugEnabled() )
               	    			m_logger.debug(" Flag Not Valid , SO rejecting this NAPTR" ) ;
           	   	 			continue;
						}
           				// if NAPTR is terminal one
           				if ( (((NAPTRRecord)sortedRecord[n]).getFlags()).compareToIgnoreCase("u") ==0 ) {
               	    		if(!isValidService(sortedRecord[n])) {
               	       		 	if(m_logger.isDebugEnabled() )
               	       		     	m_logger.debug(" Not valid service , SO rejecting this NAPTR " ) ;
               		         	continue;
               	    		}
							/*
               				if( cacheOption ) {
                   			//EnumResolver.m_cache.addRecord(sortedRecord[n] ,1,_message);
								if(m_logger.isInfoEnabled()) {
								m_logger.info("Adding Record to Cache " ) ;
								}
								synchronized ( EnumResolver.getCache() ) {
                   					EnumResolver.getCache().addRecord(sortedRecord[n] ,1,_message);
								}
               				} 
							*/  
							if(m_logger.isInfoEnabled()) {
							m_logger.info ( " This is  a Terminal NAPTR " ) ;
							}
           					String regexp = ((NAPTRRecord)sortedRecord[n]).getRegexp();
               				//if regexp field is null string then go to replacement field
               				if (regexp.compareTo("") == 0) {
								if(m_logger.isInfoEnabled()) {
								m_logger.info ( " regex field is empty , so replacing with Replacement field " ) ;
								}
                   				String repl = ((NAPTRRecord)sortedRecord[n]).getReplacement().toString();
                    			try {
                       			 	URI uri = new URI(repl);
                       		         if(m_logger.isDebugEnabled() ) {
                       		             m_logger.debug(" node has " + _node.getChildren().size()  + "  children " ) ;
                       		             m_logger.debug(" Adding " + uri.toString() + " as Terminal Child to node"   ) ;
                       		         }

                       		 		_node.addChild(uri.toString(), true);
                    			} catch ( Exception e) {
                       			 	m_logger.error( " Not a valid URI to add , continuing with next NAPTR : " + e.toString() ) ;
                    			}
                   				continue;
               				}   
               				//  here code for appying AUS to regexp field 
               				StringTokenizer st = new StringTokenizer(regexp, "!");
               				String reg = st.nextToken();
               				String replValue = st.nextToken();
							replValue = changeRegex( replValue ) ;
							if(m_logger.isDebugEnabled()) {
							m_logger.debug("   reg exp " + reg + " replacement " + replValue ) ;
							}
							try {
								URI uri = new URI(m_AUS.replaceAll(reg,replValue) ) ;
									if(m_logger.isDebugEnabled() ) {
                               		     m_logger.debug(" Adding " + uri.toString() + " as Terminal Child to node " + _node  ) ;   
                               		 }
								_node.addChild(uri.toString(), true);			
							} catch ( Exception e) {
                           		 m_logger.error( " Not a valid URI to add , continuing with next NAPTR : " + e.toString() ) ;
							
                        	}
            			} else {
							if ( !m_ctx.getRecursionOption() ) {
							if(m_logger.isDebugEnabled()) {
               					m_logger.debug( "Non Terminal NAPTR recursion not supported " ) ;
							}
								continue;
               				}

							if(m_logger.isDebugEnabled() )
                   			m_logger.debug(" non-Terminal NAPTR Processing " ) ;
               				//incase of non-Terminal NAPTR
               				//substitution field is null , then go for replacement field 
               				String newKey = null;
               				String regexp = ((NAPTRRecord)sortedRecord[n]).getRegexp();
               				if (regexp.compareTo("") == 0) {
               					newKey = ((NAPTRRecord)sortedRecord[n]).getReplacement().toString();
                  				// apply DDDS algorithm again to find other key 
               				} else {

                   				//apply AUS to regexp field to find newkey
								StringTokenizer st = new StringTokenizer(regexp, "!");
                       			String reg = st.nextToken();
                       			String replValue = st.nextToken();
                   				newKey = m_AUS.replaceAll(reg,replValue) ;
               				}

               				// Apply DDDS algorithm again to find other key.
               				if ( _recursionCounter <= maxRecursion ) {	
								TreeNode node = new TreeNode(newKey);
								node.setTerminal(false);
								if(m_logger.isDebugEnabled() )
									m_logger.debug(" Adding " + newKey + " node " + node +  " as Terminal Child to node " + _node  ) ;
								_node.addChild(node); 
								
								dddsAsync( newKey , _resolver , true , node , ++_recursionCounter ) ;
							}
                			else {
                   				if(m_logger.isDebugEnabled()) {
								m_logger.debug( "Maximum nuber of recursion exceeded : so contunuing with next NAPTR " ) ;
								}
								continue;
                			}
					
            			}// non terminal NAPTR procession over

        			} //end for //record array processing over 
					_node.setDone(true);
					if (  tree.isTreeProcessingOver() && !_resolvedFromCache ) {
						returnOnCompletion();		
					}
				}//synchronize (_node)
			}//synchronize (tree)
		}//end execute()
	
		private void returnOnCompletion() {
            //filter list according to URI scheme
            String scheme = m_ctx.getURIScheme();
			synchronized (uriList) {
            	tree.makeList(uriList);
            	if (scheme !=null && scheme.compareToIgnoreCase("") != 0 )
                	uriList = filterByScheme(scheme,uriList);
				
            	m_lsnr.receiveUriList(uriList) ;
			}
		}	
		
	} // end of class AsyncData

 

	// Unit Testing Code
    public static void main(String args[] ) {
		DDDSAlgorithm ddds = new DDDSAlgorithm("+4378" , "8.7.3.4.e164.arpa.");
		//String str = ddds.convertToKey();
		Name name = null;
		Name repl = null;
		try {
			name = new Name("8.7.3.4.e164.arpa.");
			repl = new Name(".");
		} catch (TextParseException e)
		{}
		
		 
			//isValidFlag n isValidService UNIT TESTING 
//		NAPTRRecord rec = new NAPTRRecord(name, 1, 12121 , 100 , 5, "u" , "E2U+sip" , "!^.*$!sip:61262223557@aarnet.edu.au!", repl);
//			boolean flag = ddds.isValidService(rec);
//			boolean flag1 = ddds.isValidFlag(rec);
//			System.out.println( flag + "   " + flag1);
//		

		

			/*
			//SORTING UNIT TESTING 
			NAPTRRecord reco = new NAPTRRecord(name, 1, 12121 , 100 , 7, "u" , "E2U+sip" , "!^.*$!sip:61262223557@aarnet.edu.au!", repl);
			//rec[0] = reco;
			NAPTRRecord reco1 = new NAPTRRecord(name, 1, 121211 , 100 , 5, "u" , "E2U+sip" , "!^.*$!sip:61262223557@aarnet.edu.au!", repl);
			NAPTRRecord reco2 = new NAPTRRecord(name, 1, 12121 , 101 , 5, "u" , "E2U+sip" , "!^.*$!sip:61262223557@aarnet.edu.au!", repl);
			NAPTRRecord reco3 = new NAPTRRecord(name, 1, 12121 , 10 , 5, "u" , "E2U+sip" , "!^.*$!sip:61262223557@aarnet.edu.au!", repl);
			Record[] rec = {reco , reco1 , reco2, reco3};
			rec = ddds.sortNAPTR(rec );
			for ( int i =0 ; i<rec.length ; i++ ) {
				System.out.println(rec[i]);
			}
			*/

	/*
		String uri = "sip:ad@lfd.com" ; 
		StringTokenizer st = new StringTokenizer(uri , ":");
		System.out.println(st.nextToken());

		
		EnumServer server = new EnumServer("192.168.9.13" , 53 , "TCP" , 3 , 12222 );
		ArrayList array = new ArrayList();
		//List list;
		array.add(server);
		List list= array;
		EnumContext ctx = new EnumContext();
		ctx.setServerList(list);
		//list = ddds.resolveSync(ctx , "7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
		
		try {
			//list = ddds.resolveSync(ctx , "7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
			list = ddds.resolveSync(ctx , "7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
		} catch (EnumException e ) {
		}	
		Iterator iter = list.iterator();
		if ( iter!= null ) {
			while ( iter.hasNext() ) {
				System.out.println( iter.next());
			}
		}
		//String str1 = new String;
		String[] str = new String[1];
	*/
        /*
        // Ashish Change recordSet1 to recordSet above  Testing non-Terminal NAPTR  *****************start****************
        Name name = null;
        RRset[] recordSet = new RRset[1];
        Name repl1 = null;
        Name repl2=null;
        try {      
            name = new Name("8.7.3.4.e164.arpa.");
            repl1 = new Name("7.7.7.7.7.0.0.9.5.1.6.e164.arpa.");
            repl2= new Name(".");

        } catch (TextParseException e)
        {}
        //int jj =1 ; declare jj as static int at top
        //if ( jj>1 )
        NAPTRRecord reco1 = new NAPTRRecord(repl1, 1, 12121 , 100 , 7, "u" , "E2U+sip" , "!^.*$!sip:57@aarnet.edu.au!",repl2 );
        NAPTRRecord reco2 = new NAPTRRecord(repl1, 1, 12121 , 10 , 7, "" , "E2U+sip" , "",repl1 );
        RRset rrset = new RRset ();
        rrset.addRR( (Record)reco2 ) ;
        rrset.addRR( (Record)reco1 ) ;
        //RRset rrset1 = new RRset ( (Record)reco1 ) ;
        recordSet[0]= rrset ;
        //recordSet[1]= rrset1 ;
        if ( jj > 0 ) {
            recordSet[0].clear();
            recordSet =  _message.getSectionRRsets(Section.ANSWER);
            //recordSet = recordSet1;
        }
        System.out.println("  value of jj " + jj + "\n" ) ;
        jj++;
        // ************ testing non Terminal NAPTR ends ********************

        */
	}


}//end class 



