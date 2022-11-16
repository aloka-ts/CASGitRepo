package com.genband.jain.protocol.ss7.tcap;

import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.tcap.JainTcapListener;
import jain.protocol.ss7.tcap.JainTcapProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.deployer.TcapSessionCount;
import com.baypackets.ase.sipconnector.AseSipConnector;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.genband.tcap.provider.TcapFactory;
import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapSession;

public class TcapSessionReplicator implements CommandHandler {

	private static Logger logger = Logger.getLogger(TcapSessionReplicator.class);
	private static String CMD_TCAP_INFO = "print-tcap-info".intern();
	private static String USG_TCAP_INFO = "print-tcap-info [-short|-all]".intern();
	private static int REPL_CTXT_COUNTER = 1;
	
	private boolean activeSas;
	private transient JainTcapProvider provider;
	private List<JainTcapListener> listeners = new CopyOnWriteArrayList<JainTcapListener>();
	private static Map<ClassLoader, TcapReplicationContext> ctxtMap =
				new ConcurrentHashMap<ClassLoader, TcapReplicationContext>();
	private Map<String, TcapSessionImpl> dialogueMap =
				new ConcurrentHashMap<String, TcapSessionImpl>();

	public TcapSessionReplicator(JainTcapProvider provider){
		this.provider = provider;
	}

	public JainTcapProvider getProvider() {
		return provider;
	}
	
	public void init(){
		TelnetServer telnetServer = (TelnetServer)
						Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(CMD_TCAP_INFO, this);
		activeSas = isSASActive();
	}

	public void cleanup(){
		TcapReplicationContext[] ctxts = new TcapReplicationContext[ctxtMap.size()];
		ctxtMap.values().toArray(ctxts);
		for(int i=0;i<ctxts.length;i++){
			ctxts[i].cleanup();
		}
		ctxtMap.clear();
		dialogueMap.clear();
		listeners.clear();

		TelnetServer telnetServer = (TelnetServer)
						Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.unregisterHandler(CMD_TCAP_INFO, this);
	}

	public String execute(String command, String[] args, InputStream in,
			OutputStream out) throws CommandFailedException {
		StringBuffer buffer = new StringBuffer();
		boolean all = args.length > 0 && args[0] != null && args[0].equals("-all");
		boolean checkSS7Info = args.length > 0 && args[0] != null && args[0].equals("SS7SigInfo");
		
		if(checkSS7Info){
			if(args.length > 1 && args[1] != null){
				if(args[1].equals("1")){
					JainTcapProviderImpl.getImpl().enableSS7MsgInfo(true);
					buffer.append("SS7 Signalling Info feature in print-tcap-info is enabled");
				}else if(args[1].equals("0")){
					JainTcapProviderImpl.getImpl().enableSS7MsgInfo(false);
					buffer.append("SS7 Signalling Info feature in print-tcap-info is disabled");
				}else{
					buffer.append("Invalid Argument : " + args[1] + " Use <help print-tcap-info> command to check correct usage");
				}

			}else if (args.length == 1){
				if(JainTcapProviderImpl.getImpl().isSS7MsgInfoEnabled()){
					buffer.append("SS7 Signalling Info feature in print-tcap-info is on");
				} else {
					buffer.append("SS7 Signalling Info feature in print-tcap-info is off");
				}
			}
		}
		
		if(!checkSS7Info){
			TcapProviderGateway.printDebugInfo(buffer, all);
			Iterator<TcapReplicationContext> ctxts = this.ctxtMap.values().iterator();
			for(;ctxts.hasNext();){
				buffer.append(ctxts.next().printDebugInfo(buffer, all));
			}
		}
		
		if(all){
			List<TcapSessionImpl> tcapSessionList = getAllTcapSessions();
			logger.error("AT triggerd : Total number of tcap sessions : " + tcapSessionList.size());
			buffer.append("\nTcapSessions Are::\n");
			for(TcapSessionImpl tcapsession: tcapSessionList){
				buffer.append(tcapsession);
				buffer.append("\n");
			}
		}
		
		String fileName = "";
		FileOutputStream fStream = null;
		PrintStream pstream = null;
		try{
			if(args.length >= 2 && !checkSS7Info){
				fileName = args[1];
				File file = new File(fileName);

				if(!file.getParentFile().exists()){
					file.getParentFile().mkdir();
				}else{
					if(!file.getParentFile().isDirectory()){
						throw new Exception(file.getParent() + " is not a directory");
					}
				}
				fStream = new FileOutputStream(fileName);
				out.write(("\r\nRedirecting output to file :"+fileName).getBytes());
			}
			pstream = new PrintStream(fStream != null ? fStream : out);
			pstream.println(buffer.toString());
		}catch(Exception e){	
			logger.error(e.toString(), e);
			return e.getMessage();
		}finally{
			if(fStream != null){
				try{
					fStream.close();
					pstream.close();
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}
		return AseStrings.NEWLINE_WITH_CR + command + " Completed successfully.";
	} 

	public String getUsage(String command) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Usage :");
		buffer.append(command);
		buffer.append(AseStrings.SPACE);
		buffer.append("<Type>");
		buffer.append(AseStrings.SPACE);
		buffer.append("<file-name>");
		buffer.append("\r\nWhere\r\n");
		buffer.append("<Type>");
		buffer.append(" include:");
		buffer.append("\r\n\t-short    = Print the Tcap Provider Gateways and Tcap Relication Context");
		buffer.append("\r\n\t-all     =  Print the Tcap Provider Gateways, Tcap Relication Context and Active Tcap Sessions");
		buffer.append(AseStrings.NEWLINE_WITH_CR);
		buffer.append("<file-name>");
		buffer.append(" specifies the absolute file name to redirect the output.");
		buffer.append("\r\n\t If the file name is missing, the output will be redirected to the console.");
		buffer.append(AseStrings.NEWLINE_WITH_CR);
		buffer.append("\r\nUsage :");
		buffer.append(command);
		buffer.append(AseStrings.SPACE);
		buffer.append("SS7SigInfo <value>");
		buffer.append("\r\nWhere\r\n");
		buffer.append("<Value> include:");
		buffer.append("\r\n\t 0   = SS7 Signalling Information in print-tcap-info is not shown");
		buffer.append("\r\n\t 1   = SS7 Signalling Information in print-tcap-info is shown");
		buffer.append("\r\n\t If no value is set then current status of SS7 Signalling Info feature in print-tcap-info is given");
		return buffer.toString();
	}

	public void addListener(JainTcapListener listener){
		if(this.listeners.contains(listener)){
			logger.warn("Listener already registered.");
			return;
		}
		listeners.add(listener);

		ClassLoader loader = listener.getClass().getClassLoader();
		logger.warn("addListener in TcapSessionreplicator classLoader:" + loader);
		TcapReplicationContext ctxt = ctxtMap.get(loader);
		if(ctxt == null){
			boolean active = isSASActive();

			AseContext appContext = getApplicationContext(loader);
		//	String id = appContext != null ? appContext.getId() :
			//		"TcapReplicationContext:"+REPL_CTXT_COUNTER++;
			
			String id = appContext != null ? appContext.getId() :
				"tcap-provider_1.0_2";
			if(logger.isDebugEnabled()){
				logger.debug("Application Context ID :" + id);
			}

			if(logger.isDebugEnabled()){
				logger.debug("Is Container Active:" + active);
			}

			ctxt = new TcapReplicationContext(id, loader, this);
			ctxt.setActive(active);

			ctxtMap.put(loader, ctxt);
		}
	}

	public void removeListener(JainTcapListener listener){
		if(!this.listeners.contains(listener)){
			logger.warn("Listener already removed.");
			return;
		}
		listeners.remove(listener);

		ClassLoader loader = listener.getClass().getClassLoader();
		for(int i=0; i<listeners.size();i++){
			JainTcapListener temp = listeners.get(i);
			if(temp.getClass().getClassLoader() == loader){
				return;
			}
		}

		TcapReplicationContext ctxt = ctxtMap.remove(loader);
		if(ctxt !=null){
			ctxt.clear();
		}
	}

	List<TcapSessionImpl> getAllTcapSessions(){
		return new ArrayList<TcapSessionImpl>(dialogueMap.values());
	}

	public TcapFactory getTcapFactory(JainTcapListener listener){
		ClassLoader loader = listener.getClass().getClassLoader();
		return ctxtMap.get(loader);
	}

	public TcapSession getTcapSession(int dialogueId){
		TcapSession session =dialogueMap.get(""+dialogueId);
		if(activeSas || isSASActive()){
			activeSas = true;
			if(session!=null && !session.isActive()){
				session.activate();
			}
		}
		return session;
	}

	public TcapSession getTcapSession(int dialogueId, JainTcapListener listener){
		
		Integer tcCorr=provider.getTCCorrelationId(dialogueId);
		
		TcapSession session =null; 
		if(tcCorr==null)
			session = dialogueMap.get(""+dialogueId);
		else
			session = dialogueMap.get(tcCorr.toString());
		
		if(session != null || listener == null)
			return session;

		ClassLoader loader = listener.getClass().getClassLoader();
		TcapReplicationContext ctxt = ctxtMap.get(loader);

		return createTcapSession(ctxt, dialogueId);

	}

	public void replicate(int id, String eventId){
		if(logger.isDebugEnabled()){
			logger.debug("replicate called in TcapSessionReplicator @@@");
		}
		try{
			TcapSession session = getTcapSession(id);
			if(session == null || !(session instanceof TcapSessionImpl)){
				if(logger.isDebugEnabled()){
					logger.debug("Tcap sesison is invalidated or null or not a tcapsesison::"+session);
				}
				return;
			}

			TcapReplicationContext ctxt = ((TcapSessionImpl)session).getReplicationContext();
			ctxt.replicate(""+session.getDialogueId(), eventId);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	public TcapSession createTcapSession(TcapReplicationContext ctxt) throws IdNotAvailableException{
		if(ctxt == null)
			return null;

		int id = provider.getNewDialogueId();
		return createTcapSession(ctxt, id);
	}

	public TcapSession createTcapSession(TcapReplicationContext ctxt, int id){
		if(ctxt == null)
			return null;

		TcapSessionImpl session = new TcapSessionImpl(id);
		Integer tcCorr=provider.getTCCorrelationId(id);
		
		if(tcCorr!=null)
			session.setTcCorrelationId(tcCorr.intValue());		
		if(logger.isDebugEnabled()){
			logger.debug("createTcapSession setReplicable @@" + id);
		}
		ctxt.setReplicable(session);
		return session;
	}

	public void addTcapSession(TcapSessionImpl session){
		int id = session.getDialogueId();
		if(session.isClosed()){
			logger.error(id+"Trying tp add cosed session");
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("insert tcapsession in dialogueMap"+session.getDialogueId());
		}
		int tcCorrId=session.getTcCorrelationId();
		if(tcCorrId>0)
		 dialogueMap.put(""+tcCorrId, session);
		else
	     dialogueMap.put(""+id, session);
	}
	
	//added by nitin
	public void removeTcapSession(TcapSessionImpl session){
		if(logger.isDebugEnabled()){
			logger.debug("removeTcapSession remove from dialogueMap::"+session.getDialogueId());
		}
		int id = session.getDialogueId();
		
		int tcCorrId=session.getTcCorrelationId();
		  if(tcCorrId>0)
		    dialogueMap.remove(""+tcCorrId);
		else
			dialogueMap.remove(""+id);
	}

	public void closeTcapSession(int dialogueId) throws IdNotAvailableException {
		/* todo: shouldn't down cast, but need to change TcapSession interface to get this to work */
		TcapSessionImpl ts = (TcapSessionImpl)getTcapSession(dialogueId);
		if (ts != null)
		{
			closeTcapSession(ts);
		}
	}

	public void closeTcapSession(TcapSessionImpl session) throws IdNotAvailableException {
		int id = session.getDialogueId();
		TcapListener listener = (TcapListener)session.getAttribute("ListenerApp");
		TcapSessionCount.getInstance().removeTcapDialog(listener.getName(), listener.getVersion(), String.valueOf(id));
		provider.releaseDialogueId(id);
		//session.setAttribute("CLOSED", "true");
		//session.replicate();
		if(logger.isDebugEnabled()){
			logger.debug("closeTcapSession removeReplicable @@" + id);
		}
		session.getReplicationContext().removeReplicable(session.getReplicableId());
		if(logger.isDebugEnabled()){
			logger.debug("closeTcapSession remove from dialogueMap" + id);
		}
		dialogueMap.remove(""+id);
	}

	public static AseContext getApplicationContext(ClassLoader loader){
		AseContext ctx = null;
		AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
		AseContainer[] children = host.findChildren();
		for(int i=0; children != null && i<children.length;i++){
			if(logger.isDebugEnabled()){
				logger.debug("getApplicationContext children[i]:" + children[i].getName());
			}
			if(children[i] == null)
				continue;

			if(!(children[i] instanceof AseContext))
				continue;

			AseContext temp = (AseContext)children[i];
			if(temp.getClassLoader() == loader){
				if(logger.isDebugEnabled()){
					logger.debug("getApplicationContext loader matched :" + temp.getClassLoader() );
				}
				ctx = temp;
				break;
			}
		}
		return ctx;
	}
	
	public static TcapReplicationContext getTcapReplicationContext(JainTcapListener listener){
		ClassLoader loader = listener.getClass().getClassLoader();
		TcapReplicationContext ctxt = ctxtMap.get(loader);
		return ctxt;
	}

	public static boolean isSASActive(){
		AseSipConnector connector = (AseSipConnector)Registry.lookup("SIP.Connector");
		return connector.getRole() == 1;
	}
}
