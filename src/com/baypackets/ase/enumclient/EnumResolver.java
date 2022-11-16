/**
 * EnumResolver.java
 *
 *Created on March 16,2007
 */
package com.baypackets.ase.enumclient;

import com.baypackets.ase.enumclient.EnumServer;
import com.baypackets.ase.spi.util.Work;
import com.baypackets.ase.spi.util.WorkListener;
import com.baypackets.ase.spi.util.WorkManager;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;

import java.io.File;
import java.lang.String;
import java.lang.Class;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

import org.apache.log4j.Logger;
import org.apache.commons.digester.Digester;
import org.xbill.DNS.Cache;
import org.xbill.DNS.NAPTRRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.RRset;

//ashish 
//File imported only for testing pupose . delete it later on 
import java.io.File;

/**
 * This class is invoked in EnumClient class. 
 * @author Ashish kabra
 */
public class EnumResolver implements EnumClient , MComponent { 
	
    transient private static Logger m_logger =
          Logger.getLogger(EnumResolver.class);
	//private DDDSAlgotithm ddds = new DDDSAlgorithm();

	private static final int THREADPOOL_SIZE=10;

	private static EnumContext m_defaultContext;
    private static String configfilelocation  =
        com.baypackets.ase.util.Constants.ASE_HOME + File.separator +
        "conf" + File.separator + "enum-config.xml";
	
	protected static ThreadPool m_threadPool;
	protected static Cache m_cache ;
	protected static boolean m_isStarted;
	
	public EnumResolver() {
	}

	public static Cache getCache() {
		return m_cache;
	}

	public static ThreadPool getThreadPool() { 
		return m_threadPool;
	}

	/** This method takes telephone number in E.164 as input and convert telephone number
	 * in application unique string (AUS).
	 * @param number A Telephone Number
     * @return Application Unique String 
     */
	private String convertToAUS(String number) { 
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entering :convertToAUS " ) ;
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Number to be resolved :  " + number ) ;
		StringTokenizer st = new StringTokenizer(number, "-");
		StringBuffer buffer= new StringBuffer("");
		while (st.hasMoreTokens()) {
			buffer.append(st.nextToken());
		}
		String str = new String(buffer);
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Application Unique String  :  " + str ) ;
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting :convertToAUS " ) ;
		return str ; 
	}
	
	/** This method takes AUS as input and convert this to a unique key in DNS.
	 * @param m_AUS Application Unique String
	 * @param _zone 
	 *	@return key to search in DNS 
	 */
    private String convertToKey( String m_AUS, String zone) {
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Entering :convertToKey " ) ;
        StringBuffer buffer = new StringBuffer();
        for (int i=m_AUS.length()-1;i>0 ; i--) {
            buffer.append(m_AUS.charAt(i));
            buffer.append(AseStrings.CHAR_DOT);
        }
      //  buffer.append("e164.arpa.");
        if(zone!=null && !zone.isEmpty()){
           buffer.append(zone);
        } else{
        	if(m_logger.isDebugEnabled() )
                m_logger.debug(" convertToKey : use default zone e164.arpa. as no valid zone provided") ;
        	buffer.append("e164.arpa.");
        }
        String str=buffer.toString();
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Key formed : " + str ) ;
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting :convertToKey " ) ;
          return str;
    }
	
	/** This method is identity method in case of ENUM application
	 * @param str AUS 
	 * @return input string
	 */
	private String firstWellKnownRule ( String str) {
		return str;
	}

	/** This method takes telephone number as input. It checks wether number is E.164 format 
	 * number or not. 
	 * @param number A telephone number
	 * @return true if input is in E.164 format otherwise false.
	 */
	private boolean isE164Number (String number) {
		StringTokenizer st = new StringTokenizer(number, AseStrings.MINUS);		
		if (!number.startsWith(AseStrings.PLUS) ) {
			m_logger.error( "number doesnot start with + "); 
			return false;
		}
		String str; 
		// Permitted digit length is 15 .But in code + is also included in digit length so length set to 16
		int length = 16;
		int j=1;
		while (st.hasMoreTokens()) {
			str = st.nextToken();
			length = length-str.length();
                if (j==1 ) {
                    if (str.length() >4 ) {
						m_logger.error( "Country code length >3 " ) ;
						return false;
                    }	
				}
			for(int i=0 ; i<str.length(); i++ ) { 
				if ( j==1 ) {
					j++; continue ;
				}
				char ch= str.charAt(i);
				//System.out.println(ch);
				if(ch <48 || ch>57 ) {
					m_logger.error( " non digit entry");
					return false;
				}
			}
		}

        if( length < 0 ) {
            m_logger.error (" Number length greater than 15  " ) ;
            return false;
        }
			
		return true;
	}

			
    /** Updates the configuration parameters of the component as
    specified in the Pair array **/
    public void updateConfiguration(Pair[] configData, OperationType optype)
            throws UnableToUpdateConfigException {
	}

    /** Changes the Component State to the state indicated by the argument
    passed. The states are changed according to the priority values. **/
	public void changeState(MComponentState state)
                    throws UnableToChangeStateException {
		try {
            if (state.getValue() == MComponentState.LOADED) {
                //this.initialize();
            }
            if (state.getValue() == MComponentState.RUNNING){
                //this.start();
            }
            if(state.getValue() == MComponentState.STOPPED){
							if ( m_isStarted == true ) {
                this.stop();
							}
            }
        } catch(Exception e){
            m_logger.error("changeState: ", e);
            throw new UnableToChangeStateException(e.getMessage());
        }

	}

    /**
     * Performs initialization.
     */
	public void initialize() throws Exception {
		if (m_logger.isDebugEnabled()) {
    	m_logger.info("initialize(): Initializing EnumResolver: " );
    }
		File configFile = new File(configfilelocation);
		m_defaultContext = loadConfigParam(configFile);
		//loadConfigParam(configFile);
		if ( m_defaultContext == null ) {
			m_logger.error( "Default context is null " ) ;
		}
        m_threadPool = new ThreadPool( THREADPOOL_SIZE , true ,"ENUM" , null , null , 50);
		ThreadMonitor tm = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
		this.m_threadPool.setThreadMonitor(tm);
		m_cache = new Cache();
	}

	
    /** Starts ThreadPool
     *
     */
    public void start() {
			if ( m_isStarted == false ) {
				if(m_logger.isDebugEnabled() )
					m_logger.debug("Going to start ENUM ThreadPool " );

				this.m_threadPool.start();
				m_isStarted=true;
				m_cache = new Cache();
			} else {
				if(m_logger.isDebugEnabled() )
					m_logger.debug("ENUM threadPool already started");
			}
    }

    /** Stops ThreadPool
     *
     */
    public void stop() {
        this.m_threadPool.shutdown();
				m_cache.clearCache();
		}

    /** Reads configuration from enum-config.xml
        @param file File to be read for configuration parameter
        @return EnumContext
     */
    public EnumContext loadConfigParam (File file) {
	if(m_logger.isDebugEnabled() ) {
        m_logger.debug("Entering loadConfigParam " ) ;
	}
        String[] integer = new String [] {"java.lang.Integer"} ;
        String[] bool = new String [] {"java.lang.Boolean"} ;
        try {
        	Digester digester = new Digester();
        	digester.setValidating(false);
        	digester.addObjectCreate("enum-context", EnumContext.class);
        	digester.addObjectCreate("enum-context/servers/server", "com.baypackets.ase.enumclient.EnumServer");

        	// server configuration
        	digester.addCallMethod("enum-context/servers/server/ip","setIpAddr",0);
        	digester.addCallMethod("enum-context/servers/server/port","setPort",0 , integer);
        	digester.addCallMethod("enum-context/servers/server/timeout","setTimeout",0, integer );
        	digester.addCallMethod("enum-context/servers/server/retries","setRetries",0, integer );
        	digester.addCallMethod("enum-context/servers/server/protocol","setProtocol",0);
        	digester.addSetNext("enum-context/servers/server" ,"addServer");

        	// other ENUM context Parameters
        	digester.addCallMethod("enum-context/cache" ,"setCacheOption" ,0 ,bool);
        	digester.addCallMethod("enum-context/recursion" ,"setRecursionOption" ,0 , bool);
        	digester.addCallMethod("enum-context/max-recursion" ,"setMaxRecursion" ,0 , integer);
        	digester.addCallMethod("enum-context/parallel-query" ,"setParallelQueryOption" ,0, bool);
        	digester.addCallMethod("enum-context/uri-scheme" ,"setURIScheme" ,0);
					
					digester.addCallMethod("enum-context/async" ,"setAsyncOption" ,0 , bool);

					m_defaultContext = (EnumContext)digester.parse(file);
					if(m_logger.isInfoEnabled() ) {
						m_logger.info ( "Successfully parsed the enum configuration file " ) ;
					}
        } catch (Exception ex) {
				m_logger.error( "Error occured during Parsing "  + ex.toString() ) ;
                ex.printStackTrace();
        }
	if(m_logger.isDebugEnabled() ) {
        m_logger.debug("Exiting loadConfigParam " ) ;
	}
        return m_defaultContext;
    }
	
	public EnumContext getDefaultContext() {
		return m_defaultContext;
	}

	public List resolveSync(String number,String zone ) throws EnumException {

		return resolveSync(m_defaultContext , number , zone);
	}
	
	
	public List<RRset> resolveSynch(String key,String aus) throws EnumException{
		DDDSAlgorithm ddds = new DDDSAlgorithm(m_defaultContext ,aus,key);
		return ddds.resolveSyncRRSet(key);

	}

	/** This method is used for Synchronous resolution.
	 * @param ctx EnumContext
	 * @param number A Telephone number
	 * @return List of resolved URIs corrresponding to number
  	 */
		
	public List resolveSync(EnumContext ctx, String number,String zone ) throws EnumException {
		if(m_logger.isDebugEnabled() ) {
			m_logger.debug(" Entering :EnumResolver.resolveSync  for "+number +" and Zone "+zone ) ;
		}
		try {
			if(m_threadPool == null ) {
				this.initialize();
				this.start();
			}
		} catch (Exception exp ) {
			m_logger.error( " Error in initializing ENUM threadpool " , exp );
		}

		if ( ctx == null ) {
    	ctx = m_defaultContext;
		}

		if ( !this.isE164Number(number) )
			throw new EnumException("not an ENUM number" ) ; 
        String aus = this.convertToAUS(number);
        if ( aus.length()>16 ) {
			m_logger.error("Enum number length > 15 ; Throwing EnumException " ) ;
			throw new EnumException("not an ENUM number, length > 16" ) ;
		}
        String key = this.convertToKey(aus,zone);
        boolean cacheOption = ctx.getCacheOption();
		if(m_logger.isDebugEnabled() ) {
           	m_logger.debug(" Resolving from DNS server " ) ;
		}
		DDDSAlgorithm ddds = new DDDSAlgorithm(ctx ,aus,key);
		return ddds.resolveSync(key);

	}

	public void resolveAsync(String number ,String zone,EnumListener lsnr) throws EnumException {
		resolveAsync(m_defaultContext , number ,zone,lsnr);
	}   
	
	/** This mehod is used for Asynchronous resolution 
	 * @param ctx EnumContext
	 * @param number A Telephone Number
	 * @param lsnr EnumListener registered for call back purpose
	 */		
	public void resolveAsync(EnumContext ctx, String number ,String zone,EnumListener lsnr) throws EnumException {
		if(m_logger.isDebugEnabled() ) {
    	m_logger.debug(" Entering EnumResolver:resolveAsync for Number "+number +" and Zone "+zone ) ;
		}
		try {
			if(m_threadPool == null ) {
				this.initialize();
				this.start();
			}
		} catch (Exception exp ) {
			m_logger.error( " Error in initializing ENUM threadpool " , exp );
		}
		if ( ctx == null ) { 
			ctx = m_defaultContext;
		}

		if ( m_defaultContext.getAsyncOption() == false ) {
			 EnumException e = new EnumException("Asynchronous option not set in enum config file" ) ;
			 m_logger.error( "Asynchronous option not set in enum config file , so can't handle asynchronous calls" + e.getMessage() , e) ;
			throw e;
		 }
			
		AsyncData data = new AsyncData( ctx, number, zone,lsnr);
        if(m_logger.isDebugEnabled() )
            m_logger.debug(" Submitting Number to threadPool for resolution" ) ;
		m_threadPool.doWork(data);
		if(m_logger.isDebugEnabled() )
            m_logger.debug(" Exiting :resolveAsync " ) ;
    }

	private class AsyncData implements Work {
    	private EnumContext _ctx;
    	private EnumListener _lsnr;
    	private String  _number;
    	private String _AUS=null;
    	private String _zone=null;

    	public AsyncData (EnumContext ctx, String number ,String zone,EnumListener lsnr) {
        	_ctx = ctx;
        	_lsnr= lsnr;
        	_number=number;
        	_zone=zone;
    	}

    	public AsyncData (EnumContext ctx, String number,String zone,EnumListener lsnr , String aus) {
        	_ctx = ctx;
       		_lsnr= lsnr;
     	    _number=number;
        	_AUS = aus;
        	_zone=zone;
    	}
    	public String getNumber() {
        	return _number;
    	} 
          
    	public EnumContext getContext () {
        	return _ctx;
    	}    
        
    	public EnumListener getListener() {
        	return _lsnr;
    	}

    	public String getAUS () {
        	return _AUS;
    	}

    	public String getZone() {
        	return _zone;
    	}
        public WorkListener getWorkListener() {return null ; } ;
        public int getTimeout(){ return 0; } ;

		public void execute() {
			if(m_logger.isInfoEnabled() ) {
				m_logger.info("execute EnumResolver " ) ;
			}
			if ( ! isE164Number(_number) ) {
				Exception exp = new EnumException("not an ENUM number" ) ;
				m_logger.error("Number is not an ENUM number " + exp ); 
				_lsnr.handleException(exp);
			}
        	String aus = convertToAUS(_number);
        	if ( aus.length()>16 ) {
				m_logger.error( "Enum number length > 15 ; Throwing EnumException " ) ;
                Exception exp = new EnumException("not an ENUM number, length > 16" ) ;
                _lsnr.handleException(exp);
            }
            String key = convertToKey(aus,_zone);
            boolean cacheOption = _ctx.getCacheOption();
            DDDSAlgorithm ddds = new DDDSAlgorithm(_ctx, _lsnr , aus,key);
            ddds.resolveAsync(key);

        }

    }


//Ashish
    //UNIT TESTING CODE
		/*
    public static void main(String args[] )
    {  
        EnumResolver res = new EnumResolver();
        String str = res.convertToAUS("+1212-1212");
        boolean f = res.isE164Number("+1a-12121-1");
        //System.out.println(f);

        //EnumServer server = new EnumServer("192.168.9.13" , 53 , "UDP" , 3 , 12222 );
        ArrayList array = new ArrayList();
        //List list;
        //array.add(server);
        List list= array;
        EnumContext ctx = new EnumContext();
        File input = new File( "/user/akabra/dns/dnsjava-2.0.3/enum-config.xml" );
        res.loadConfigParam(input);
        List servers = ctx.getServerList();
        Iterator ite = servers.iterator();
        while( ite.hasNext() ) {
            EnumServer server = (EnumServer)ite.next();
            System.out.println( "  port " + server.getPort()) ;
        }
        //ctx.setURIScheme("mailto");
        //ctx.addServer(server);


        //CACHE TESTING CODE    SUCCESSFUL
        //System.out.println(ctx.toString() + "cache Option " + ctx.getCacheOption());
        Name name = null;
        Name name1 = null;
        Name repl = null;
        try {
            name = new Name("8.2.2.5.2.3.0.8.7.3.4.e164.arpa.");
            name1 = new Name("2.2.5.2.3.0.8.7.3.4.e164.arpa.");
            repl = new Name(".");
        } catch (TextParseException e)
        {}
        NAPTRRecord reco1 = new NAPTRRecord(name, 1, 11 , 100 , 5, "u" , "E2U+sip" , "!^.*$!sip:61262223557@aarnet.edu.au!", repl);
        NAPTRRecord reco = new NAPTRRecord(name1, 1, 10000 , 100 , 5, "u" , "E2U+sip" , "!^.*$!sip:61262223557@aarnet.edu.au!", repl);
        RRset rr1 = new RRset() ;
        rr1.addRR( reco ) ;
        //rr1.addRR( reco1 ) ;

        //m_cache.addRecord(reco , 2 , "" ) ;
        //m_cache.addRecord(reco1 , 2 , "");
        //m_cache.addRRset(rr1, 2 ) ;

        int size = m_cache.getSize();
        //m_cache.flushName(name);

        //System.out.println ( "entries in cache " + size + "    " + m_cache.toString() ) ;
        try {
            Thread.sleep(1225);
        } catch ( Exception e )
        {}
        size = m_cache.getSize();
        //System.out.println ( "entries in cache " + size +  "   " +  m_cache.toString() ) ;
        //System.out.println ( " cache class" + m_cache.getDClass() + "     class of RRset" + rr1.getDClass()) ;
        //RRset[] rr = m_cache.lookup(name , DClass.IN ,2).answers();
        RRset[] rr = m_cache.findAnyRecords(name , Type.NAPTR);
        for ( int i=0; rr!=null && i<rr.length ; i++ ) {
            Iterator it = rr[i].rrs() ;
            while( it.hasNext() ) {
                System.out.println( it.next() ) ;
            }
        }

        try {
            list = res.resolveSync(ctx , "+43-780-325-228");
            //list = res.resolveSync(ctx , "+43-664-420-4100");

        } catch (EnumException e ) {
            System.out.println("enum xception " + e );
        }
        Iterator iter = list.iterator();
        if (iter != null ) {
            while (iter.hasNext() ) {
                System.out.println( iter.next());
            }
        }
        try {
        list = res.resolveSync(ctx , "+43-780-325-228");

        } catch (EnumException e ) {
            System.out.println("enum xception " + e );
        }
        iter = list.iterator();
        if (iter != null ) {
            while (iter.hasNext() ) {
                System.out.println( iter.next());
            }
        }
    } */
}
