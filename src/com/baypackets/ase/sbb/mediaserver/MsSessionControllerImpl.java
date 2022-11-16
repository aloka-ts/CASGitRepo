
package com.baypackets.ase.sbb.mediaserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsAdaptorFactory;
import com.baypackets.ase.msadaptor.MsDialogSpec;
import com.baypackets.ase.msadaptor.msml.MsmlAdaptor;
import com.baypackets.ase.sbb.EarlyMediaCallback;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsCollectSpec;
import com.baypackets.ase.sbb.dialog.group.MsGroupSpec;
import com.baypackets.ase.sbb.MsOperationResult;
import com.baypackets.ase.sbb.MsPlaySpec;
import com.baypackets.ase.sbb.MsRecordSpec;
import com.baypackets.ase.sbb.MsSessionController;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.audit.MsAuditSpec;
import com.baypackets.ase.sbb.b2b.DisconnectHandler;
import com.baypackets.ase.sbb.b2b.OneWayDialoutHandler;
import com.baypackets.ase.sbb.b2b.OneWayDisconnectHandler;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMediaDescription;
import com.dynamicsoft.DsLibs.DsSdpObject.DsSdpMsg;
@DefaultSerializer(ExternalizableSerializer.class)
public class MsSessionControllerImpl extends SBBImpl implements MsSessionController {

	private static final long serialVersionUID = -388488843243974114L;
	private static Logger _logger = Logger.getLogger(MsSessionControllerImpl.class);

	protected String id;
	protected MediaServer mediaServer;
	protected MsAdaptor msAdaptor;
	
	private int timeout;
	private int dialogId;
	private MsOperationResult result;
	private boolean connected = false;
	private int noAnswerTimeout = 60;
	
	private ArrayList<String> supportedMediaTypesList ;
	
	/* This hashTable will store all attributes */	
	private Hashtable msAttributes =  new Hashtable();

	public MsSessionControllerImpl() {
		if (_logger.isDebugEnabled()) {
			_logger.debug("MsSessionController() called...");
		}

		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		this.setTimeout(Integer.parseInt(config.getValue(Constants.OID_MS_OP_TIMEOUT)));
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("MsSessionController(): Setting Media Server operation timeout to: " + this.getTimeout() + " seconds.");
		}
	}

	public void connect(SipServletRequest request, MediaServer mediaServer) throws MediaServerException, IllegalStateException {
		if(mediaServer == null)
			throw new IllegalArgumentException("Media Server object cannot be NULL");
		try{
			this.mediaServer = mediaServer;
			this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);
			setSupportedMediaTypes(request);
			String uri = this.msAdaptor.getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			Address addrB = factory.createAddress(uri);
			
			MsConnectHandler handler = new MsConnectHandler(request, addrB);
			this.addSBBOperation(handler);
			
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(ServletParseException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(MediaServerException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void dialOut(MediaServer mediaServer) throws MediaServerException, IllegalStateException {
	
		if(mediaServer == null)
			throw new IllegalArgumentException("Media Server object cannot be NULL");
		try{
			this.mediaServer = mediaServer;
			this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);
			
			String uri = this.msAdaptor.getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			Address addrB = factory.createAddress(uri);
			
			MsOneWayDialoutHandler handler = new MsOneWayDialoutHandler(addrB);
			handler.setNoAnswerTimeout(this.noAnswerTimeout);
			this.addSBBOperation(handler);
			
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(ServletParseException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(MediaServerException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void dialOut(Address addrA, MediaServer mediaServer) throws MediaServerException, IllegalStateException {
		if (mediaServer == null) {
			throw new IllegalArgumentException("MediaServer object cannot be NULL.");
		}
		if (addrA == null) {
			throw new IllegalArgumentException("Address object cannot be NULL.");
		}

		try {
			this.mediaServer = mediaServer;
			this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);

			String uri = this.msAdaptor.getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			Address addrB = factory.createAddress(uri);

			MsDialoutHandler handler = new MsDialoutHandler(null, addrA, addrB);
			handler.setNoAnswerTimeout(this.noAnswerTimeout);
			this.addSBBOperation(handler);
			handler.start();
		} catch (ProcessMessageException e) {
			throw new MediaServerException(e.getMessage(), e);
		} catch (ServletParseException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
	}
	
	public void dialOut(Address from, Address addrA, MediaServer mediaServer) throws MediaServerException, IllegalStateException {
		if (mediaServer == null) {
			throw new IllegalArgumentException("MediaServer object cannot be NULL.");
		}
		if (from == null) {
			throw new IllegalArgumentException("From Address object cannot be NULL.");
		}
		if (addrA == null) {
			throw new IllegalArgumentException("Address object cannot be NULL.");
		}

		try {
			this.mediaServer = mediaServer;
			this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);

			String uri = this.msAdaptor.getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			Address addrB = factory.createAddress(uri);

			MsDialoutHandler handler = new MsDialoutHandler(from, addrA, addrB);
			handler.setNoAnswerTimeout(this.noAnswerTimeout);
			this.addSBBOperation(handler);
			handler.start();
		} catch (ProcessMessageException e) {
			throw new MediaServerException(e.getMessage(), e);
		} catch (ServletParseException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
	}


	public void disconnect() throws MediaServerException, IllegalStateException {
	
		try{
			this.connected = false;
			DisconnectHandler handler = new DisconnectHandler();
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void disconnectMediaServer() throws MediaServerException, IllegalStateException {
		try{
			this.connected = false;
			OneWayDisconnectHandler handler = new OneWayDisconnectHandler();
			handler.setDisconnectParty(OneWayDisconnectHandler.PARTY_B);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			_logger.error(e.getMessage(), e);
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public MediaServer getMediaServer() {
		return this.mediaServer;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void setNoAnswerTimeout(int timeout) {
		this.noAnswerTimeout = timeout;
	}

	public int getNoAnswerTimeout() {
		return this.noAnswerTimeout;
	}

	public void play(MsPlaySpec playSpec) throws MediaServerException, IllegalStateException {
		try{
			MsDialogSpec dialog = new MsDialogSpec();
			if(_logger.isDebugEnabled()) 
			_logger.debug("<SBB> Connection id =" +this.getId());
			dialog.setConnectionId(this.getId());
			if(_logger.isDebugEnabled()) 
			_logger.debug("<APP> dialog "+dialog);
			this.dialogId=MediaServerInfoHandler.MS_OPERATION_PLAY;
			dialog.setId(Integer.toString(dialogId));
			dialog.addMediaServerSpec(playSpec);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_PLAY, dialog);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			_logger.error(e.getMessage(), e);
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void record(MsRecordSpec recordSpec) throws MediaServerException, IllegalStateException {
		try{
			MsDialogSpec dialog = new MsDialogSpec();
			dialog.setConnectionId(this.getId());
			this.dialogId=MediaServerInfoHandler.MS_OPERATION_RECORD;
			dialog.setId(Integer.toString(dialogId));
			dialog.addMediaServerSpec(recordSpec);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_RECORD, dialog);
			handler.setRecordSpec(recordSpec);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}
	
	public void playCollect(MsPlaySpec playSpec, MsCollectSpec collectSpec) throws MediaServerException, IllegalStateException {
		try{
	
			MsDialogSpec dialog = new MsDialogSpec();
			dialog.setConnectionId(this.getId());
			this.dialogId=MediaServerInfoHandler.MS_OPERATION_PLAY_COLLECT;
			dialog.setId(Integer.toString(dialogId));			
			dialog.addMediaServerSpec(playSpec);
			dialog.addMediaServerSpec(collectSpec);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_PLAY_COLLECT, dialog);
			handler.setCollectSpec(collectSpec);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void playRecord(MsPlaySpec playSpec, MsRecordSpec recordSpec) throws MediaServerException, IllegalStateException {
		try{
			MsDialogSpec dialog = new MsDialogSpec();
			dialog.setConnectionId(this.getId());
			this.dialogId=MediaServerInfoHandler.MS_OPERATION_PLAY_RECORD;
			dialog.setId(Integer.toString(dialogId));
			dialog.addMediaServerSpec(playSpec);
			dialog.addMediaServerSpec(recordSpec);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_PLAY_RECORD, dialog);
			handler.setRecordSpec(recordSpec);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}


	@Override
	public void playCollect(MsPlaySpec playSpec, MsCollectSpec collectSpec,
			String groupTopology) throws MediaServerException,
			IllegalStateException {
		try{
			
			MsDialogSpec dialog = new MsDialogSpec();
			dialog.setConnectionId(this.getId());
			this.dialogId=MediaServerInfoHandler.MS_OPERATION_PLAY_COLLECT;
			dialog.setId(Integer.toString(dialogId));			
			MsGroupSpec groupSpec=new MsGroupSpec();
			groupSpec.setGroupTopology(groupTopology);
			groupSpec.addMediaServerSpec(playSpec);
			groupSpec.addMediaServerSpec(collectSpec);
			dialog.setGroup(groupSpec);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_PLAY_COLLECT, dialog);
			handler.setCollectSpec(collectSpec);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
		
	}

	@Override
	public void playRecord(MsPlaySpec playSpec, MsRecordSpec recordSpec,
			String groupTopology) throws MediaServerException,
			IllegalStateException {
		try{
			MsDialogSpec dialog = new MsDialogSpec();
			dialog.setConnectionId(this.getId());
			this.dialogId=MediaServerInfoHandler.MS_OPERATION_PLAY_RECORD;
			dialog.setId(Integer.toString(dialogId));
			MsGroupSpec groupSpec=new MsGroupSpec();
			groupSpec.setGroupTopology(groupTopology);
			groupSpec.addMediaServerSpec(playSpec);
			groupSpec.addMediaServerSpec(recordSpec);
			dialog.setGroup(groupSpec);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_PLAY_RECORD, dialog);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
		
	}
	
	public void playVoiceXmlOnDialout(MediaServer mediaServer, URL resource) throws MediaServerException, IllegalStateException, IllegalArgumentException {
		if(mediaServer == null)
			throw new IllegalArgumentException("Media Server object cannot be NULL");
		if(resource == null)
            throw new IllegalArgumentException("VXML URL cannot be NULL");
		try{
			this.mediaServer = mediaServer;
			this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);
			
			String uri = this.msAdaptor.getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_VOICEXML, resource);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);

			 String VPath= resource.toString();
			 
			 String appSessionid =this.getApplicationSession().getId();
             
             if(_logger.isDebugEnabled())
				 _logger.debug("<SBB> playVoiceXmlOnDialout voice Xml is " +VPath + " Decoded AppSession id is "+appSessionid);
			 
			 
             if(VPath.indexOf(AseStrings.SEMI_COLON)!=-1) {  //sip paramters has been addded to it
			       
                String pathonly =VPath.substring(0 ,VPath.indexOf(AseStrings.SEMI_COLON));
                String params =VPath.substring(VPath.indexOf(AseStrings.SEMI_COLON) ,VPath.length());
             
                if(VPath.startsWith(AseStrings.PROTOCOL_HTTP))
                  VPath = pathonly+URLEncoder.encode("?aai="+appSessionid,AseStrings.XML_ENCODING_UTF8) +params;
                
             }else {
            	 
            	 if(VPath.startsWith(AseStrings.PROTOCOL_HTTP))
             	   VPath =VPath+URLEncoder.encode("?aai="+appSessionid,AseStrings.XML_ENCODING_UTF8);
             	
             }
             
             if(_logger.isDebugEnabled())
   				 _logger.debug("<SBB> playVoiceXmlOnDialout voice Xml path after Encoding  " +VPath);
             
//             if(VPath.indexOf("?")!=-1)
//         	    VPath =VPath+"&aai="+this.getApplicationSession().getId(); 	
// 			else
// 				VPath =VPath+"?aai="+this.getApplicationSession().getId(); 

		 
			 
                       URI uri1= factory.createURI(uri);
	               SipURI uri2= (SipURI)uri1;
                        uri2.setParameter("aai",URLEncoder.encode("\"" + this.getApplicationSession().getId()+ "\"",AseStrings.XML_ENCODING_UTF8)); 
	                   uri2.setParameter("voicexml",  VPath);
                       Address addrB = factory.createAddress(uri2);
	               addrB.setDisplayName(uri2.getUser()); 	
		      
                        //System.out.println("<SBB> uri is "+addrB);	
                        this.getServletContext().setAttribute(this.getApplicationSession().getId(), this.getApplicationSession());
		         this.getApplicationSession().setAttribute(SBBOperationContext.ATTRIBUTE_SBB, this.getName());        
                       
                        OneWayDialoutHandler handler = new OneWayDialoutHandler(null, addrB);
			this.addSBBOperation(handler);
			
			handler.start();
		}catch(ServletParseException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
	}

 public void playVoiceXmlOnConnect(SipServletRequest request,MediaServer mediaServer, URL resource) throws MediaServerException, IllegalStateException, IllegalArgumentException {

                if(mediaServer == null)
                        throw new IllegalArgumentException("Media Server object cannot be NULL");
                if(resource == null)
	                   throw new IllegalArgumentException("VXML URL cannot be NULL");
                try{
                        this.mediaServer = mediaServer;
                        this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);
                       
                         String uri = this.msAdaptor.getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_VOICEXML, resource);
                        SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);

                        String VPath= resource.toString();
                        
                         String appSessionid =this.getApplicationSession().getId();
                        
                        if(_logger.isDebugEnabled())
           				 _logger.debug("<SBB> playVoiceXmlOnConnect voice Xml is " +VPath + " Decoded AppSession id is "+appSessionid);
           			 
           			 
                        if(VPath.indexOf(AseStrings.SEMI_COLON)!=-1) {  //sip paramters has been addded to it
           			       
                           String pathonly =VPath.substring(0 ,VPath.indexOf(AseStrings.SEMI_COLON));
                           String params =VPath.substring(VPath.indexOf(AseStrings.SEMI_COLON) ,VPath.length());
                        
                           if(VPath.startsWith(AseStrings.PROTOCOL_HTTP))
                        	   VPath = pathonly+URLEncoder.encode("?aai="+appSessionid,AseStrings.XML_ENCODING_UTF8) +params;
                           
                        }else {
                        	 if(VPath.startsWith(AseStrings.PROTOCOL_HTTP))
                        	   VPath =VPath+URLEncoder.encode("?aai="+appSessionid,AseStrings.XML_ENCODING_UTF8);
                        	
                        }
                        
                        if(_logger.isDebugEnabled())
              				 _logger.debug("<SBB> playVoiceXmlOnConnect voice Xml path after Encoding  " +VPath);
                        
//                        if(VPath.indexOf("?")!=-1)
//                    	    VPath =VPath+"&aai="+this.getApplicationSession().getId(); 	
//            			else
//            				VPath =VPath+"?aai="+this.getApplicationSession().getId(); 

                       URI uri1= factory.createURI(uri);
                       SipURI uri2= (SipURI)uri1;
                       uri2.setParameter("aai",URLEncoder.encode("\"" + appSessionid+ "\"",AseStrings.XML_ENCODING_UTF8)); 
                       uri2.setParameter("voicexml",  VPath);
                       Address addrB = factory.createAddress(uri2);
                       addrB.setDisplayName(uri2.getUser());

                 //       System.out.println("<SBB> uri is "+addrB+ " Appid "+this.getApplicationSession().getId()+ " SC is "+this.getServletContext() +" App//              Session is " + this.getApplicationSession());
                        this.getServletContext().setAttribute(this.getApplicationSession().getId(), this.getApplicationSession());
                        this.getApplicationSession().setAttribute(SBBOperationContext.ATTRIBUTE_SBB, this.getName());
 
                        MsConnectHandler handler = new MsConnectHandler(request, addrB);
                        this.addSBBOperation(handler);

                        handler.start();
                }catch(ProcessMessageException e){
                        throw new MediaServerException(e.getMessage(), e);
                }catch(ServletParseException e){
                        throw new MediaServerException(e.getMessage(), e);
                }catch(MediaServerException e){
                        throw new MediaServerException(e.getMessage(), e);
                } catch (UnsupportedEncodingException e) {
        			throw new MediaServerException(e.getMessage(), e);
        		}
        }



       public void playSubseqVoiceXmlWithINFO(URL resource) throws MediaServerException, IllegalStateException, IllegalArgumentException { 
             if(mediaServer == null) throw new IllegalArgumentException("Media Server object cannot be NULL");
		try{
		//System.out.println("<SBB> playSubseqVoiceXmlWithINFO "+resource);	
		MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_PLAY_VXML, resource); 
                    this.addSBBOperation(handler);
                    handler.start();
			
			
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}
       
       public void stopMediaOperations() throws MediaServerException, IllegalStateException {
           try{
        	   
        		if (_logger.isDebugEnabled()) {
					_logger.debug("stopMediaOperations..Entering ");
				}
                  boolean needStop= super.stopMediaOperation( msAdaptor.getClass().getName().contains(com.baypackets.ase.sbb.util.Constants.MSML_TYPE));
                  
				if (needStop) {
					
					MsDialogSpec dialog = new MsDialogSpec();
					dialog.setConnectionId(this.getId());
					dialog.setOperation(MsDialogSpec.OP_CODE_DIALOG_END);
					dialog.setConnectionId(this.getId());
					
					this.setAttribute(
							com.baypackets.ase.sbb.util.Constants.ATTRIBUTE_OPERATION_STOPPED,AseStrings.TRUE_CAPS);
					
					MediaServerInfoHandler handler=null;
					if (msAdaptor.getClass().getName().contains(com.baypackets.ase.sbb.util.Constants.MSML_TYPE)) {
						
						if (_logger.isDebugEnabled()) {
							_logger.debug("stopMediaOperations..end on going MSML dialog ");
						}
						dialog.setId(Integer.toString(dialogId));
						handler = new MediaServerInfoHandler(
								this, MediaServerInfoHandler.MS_OPERATION_STOP_RECORD,
								dialog);
						
						((BasicSBBOperation)handler).setOperationContext(this);
					}else {
						if (_logger.isDebugEnabled()) {
							_logger.debug("stopMediaOperations..end on going MSCML Operation ");
						}
						this.dialogId = MediaServerInfoHandler.MS_OPERATION_STOP_RECORD;
						dialog.setId(Integer.toString(dialogId));
						
						handler = new MediaServerInfoHandler(
								this, MediaServerInfoHandler.MS_OPERATION_STOP_RECORD,
								dialog);
						this.addSBBOperation(handler);	
					}
					handler.start();
					
				}else{
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("stopMediaOperations.. stop can not be performed ");
					}
				}
                   
           }catch(ProcessMessageException e){
                   throw new MediaServerException(e.getMessage(), e);
           }

   }
       
	public void setResult(MsOperationResult result) {
		this.result = result;
	}

	public MsOperationResult getResult() {
		return this.result;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public MsAdaptor getMsAdaptor(){
		if (this.msAdaptor == null) {
			try {
				this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(this.mediaServer);
			} catch (Exception e) {
				String msg = "getMsAdaptor(): Error in obtaining adaptor from factory: " + e.getMessage();
				_logger.error(msg, e);
				throw new RuntimeException(msg);
			}
		}	
		return this.msAdaptor;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * This method parses the SDP in the given SIP message and sets the host and
	 * port attributes of this SBB using the values specified in the connection 
	 * and media description fields of the SDP. 
	 */
	void parseSDP(SipServletMessage message) {
		try{
			this.id = this.msAdaptor.getConnectionId(message);
			if(_logger.isDebugEnabled()) 
				_logger.debug("<SBB> Connection id is "+this.id);
		}catch(MediaServerException e){
			String msg = "Error occurred while parsing SDP of SIP message: " + e.getMessage();
			_logger.error(msg, e);
		}
	}

	public ArrayList<String> getSupportedMediaTypes(){
		return this.supportedMediaTypesList;
	}
	
	public ArrayList<String> getSupportedMediaTypes(SipServletMessage message)throws MediaServerException{
		ArrayList<String> supportedMediaTypesListLocal = new ArrayList<String>();
		try{			
			_logger.debug("parseSDP(): Parsing SDP for connection and media description fields...");	
			Object sdp = message.getContent();
			if (sdp == null) {
				throw new MediaServerException(
						"No SDP to parse in given SIP message.");
			}
			byte[] bytes = null;
			if (sdp instanceof byte[]) {
				_logger.debug("SDP is a byte array");
				bytes = (byte[]) sdp;
			} else if (sdp instanceof String) {
				_logger.debug("SDP is a String");
				bytes = sdp.toString().getBytes();
			} else {
				throw new MediaServerException("Unable to parse content of SIP message.  Content is of an unknown type: "
								+ sdp.getClass());
			}

			DsSdpMsg msg = new DsSdpMsg(bytes);
			_logger.debug("Getting the Media Description of the SDP");

			DsSdpMediaDescription[] mediaFields = msg.getMediaDescriptionList();
			if (mediaFields == null || mediaFields.length == 0) {
				throw new MediaServerException("No media fields specified in SDP message.");
			}
			
			for (int i = 0; i < mediaFields.length; i++) {
				String mediaType = mediaFields[i].getMediaField().getMediaType();
				if (mediaType != null)
					supportedMediaTypesListLocal.add(mediaType);
			}
			_logger.debug("Supported media found in SDP:"+supportedMediaTypesListLocal);
			
		}catch(MediaServerException e){
			throw e;
		} catch (Exception e) {
			throw new MediaServerException(e.getMessage(), e);
		}
		return supportedMediaTypesListLocal;
	}

	public void setSupportedMediaTypes(SipServletMessage message)throws MediaServerException{
		try{			
			supportedMediaTypesList = new ArrayList<String>();
			if(_logger.isDebugEnabled()){
				_logger.debug("parseSDP(): Parsing SDP for connection and media description fields...");
			}
			Object content = message.getContent();
			if (content == null) {
				if(_logger.isDebugEnabled()){
					_logger.debug("No SDP to parse in given SIP message.");			
				}
				return;
			}
			String contentType = message.getContentType();
			
			byte[] bytesContent = null;
			// Added handling for multipart content 
			if(contentType.startsWith(com.baypackets.ase.sbb.util.Constants.SDP_MULTIPART)){
				int sdpBpIndx=-1;
				MimeMultipart multipart = null ;
				multipart=(MimeMultipart)content;
				int count=multipart.getCount();
				for(int i=0;i<count;i++){
					if(multipart.getBodyPart(i).getContentType().startsWith(com.baypackets.ase.sbb.util.Constants.SDP_CONTENT_TYPE)){
						sdpBpIndx=i;
						break;
					}
				}
				if(sdpBpIndx!=-1){
				    ByteArrayInputStream bis=(ByteArrayInputStream) multipart.getBodyPart(sdpBpIndx).getContent(); 
				    int bytes=bis.available();
				    bytesContent=new byte[bytes];
				    bis.read(bytesContent,0,bytes);
				    bis.close();
				}else{
					throw new MediaServerException("No SDP found in Mesage Content of type:"+contentType+" class:"+ content.getClass());
				}
			}else{
				if (content instanceof byte[]) {
					if(_logger.isDebugEnabled()){
						_logger.debug("SDP is a byte array");
					}
					bytesContent = (byte[]) content;
				} else if (content instanceof String) {
					if(_logger.isDebugEnabled()){
						_logger.debug("SDP is a String");
					}
					bytesContent = content.toString().getBytes();
				} else {
					throw new MediaServerException("Unable to parse content of SIP message.  Content is of an unknown type: "
							+ content.getClass());
				}
			}
			DsSdpMsg msg = new DsSdpMsg(bytesContent);
			if(_logger.isDebugEnabled()){
				_logger.debug("Getting the Media Description of the SDP");
			}
			DsSdpMediaDescription[] mediaFields = msg.getMediaDescriptionList();
			if (mediaFields == null || mediaFields.length == 0) {
				throw new MediaServerException("No media fields specified in SDP message.");
			}
			
			for (int i = 0; i < mediaFields.length; i++) {
				String mediaType = mediaFields[i].getMediaField().getMediaType();
				if (mediaType != null)
					supportedMediaTypesList.add(mediaType);
			}
			if(_logger.isDebugEnabled()){
				_logger.debug("Supported media found in SDP:"+supportedMediaTypesList);
			}
			
		}catch(MediaServerException e){
			throw e;
		} catch (Exception e) {
			throw new MediaServerException(e.getMessage(), e);
		}		
	}

	public void writeExternal0(ObjectOutput out) throws IOException {
		super.writeExternal0(out);
		out.writeInt(dialogId);
		out.writeBoolean(this.connected);
		out.writeObject(this.mediaServer);
		out.writeObject(this.id);
		out.writeObject(this.supportedMediaTypesList);
		
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		//out.writeInt(dialogId);
		out.writeBoolean(this.connected);
		out.writeObject(this.mediaServer);
		out.writeObject(this.id);
		//out.writeObject(this.supportedMediaTypesList);
		
	}
	
	public void readExternal0(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal0(in);
		this.dialogId=in.readInt();
		this.connected = in.readBoolean();
		this.mediaServer = (MediaServer)in.readObject();
		this.id= (String)in.readObject();
		this.supportedMediaTypesList = (ArrayList<String>)in.readObject();
	
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		//this.dialogId=in.readInt();
		this.connected = in.readBoolean();
		this.mediaServer = (MediaServer)in.readObject();
		this.id= (String)in.readObject();
		//this.supportedMediaTypesList = (ArrayList<String>)in.readObject();
		
		
	}
	
	public void activate(SipSession session) {
		super.activate(session);
	
		EarlyMediaCallback callback =  (EarlyMediaCallback)this.getCallback(EarlyMediaCallback.class.getName());
		if(callback instanceof MsConnectHandler){
			((MsConnectHandler)callback).sessionActivated(session);
		}
	}

	public int fireEvent(SBBEvent event){
		if(event != null && SBBEvent.EVENT_CONNECTED.equals(event.getEventId())){
			this.connected = true;
		}
		return super.fireEvent(event);
	}
	
	//change for GroupedMSSBB for adding ISUP messages as attributes
	public void setAttribute(String name, Object value) throws IllegalArgumentException, IllegalStateException  {
		if (name == null) {
			_logger.error("<SBB> Either name is illegal");
			throw new IllegalArgumentException();
		}
	
		if (name.equalsIgnoreCase(IAM_ISUP) ||
						name.equalsIgnoreCase(ACM_ISUP) ||
						name.equalsIgnoreCase(ANM_ISUP) ||
						name.equalsIgnoreCase(CPG_ISUP) ||
						name.equalsIgnoreCase(EXTRA_183_CPG) ||
						name.equalsIgnoreCase(REL_ISUP) ||
						name.equalsIgnoreCase(RLC_ISUP) || 
						name.equalsIgnoreCase(MS_INVITE_WITHOUT_SDP) || 
						name.equalsIgnoreCase(STOP_ANNOUNCEMENT) || 
						name.equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.TIMEOUT_REQUIRED) || 
						name.equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.UPDATE_NEEDED) || 
						name.equalsIgnoreCase(TERM_SDP) || name.equalsIgnoreCase(EARLY_DIALOUT) ||
						name.equalsIgnoreCase(NO_ACK) || name.equalsIgnoreCase(SEND_1XX)||
						name.equalsIgnoreCase(B_TAKEOVER)||
		                name.equalsIgnoreCase(RE_INVITE_WITH_NEW_SDP)||
		                name.equalsIgnoreCase(CDR_INFO_HEADER)||
		                name.equalsIgnoreCase(LEG_ID)||
		                name.equalsIgnoreCase(SBBOperationContext.ATTRIBUTE_SDP_PARTY_B) ||
		                name.equalsIgnoreCase(SBBOperationContext.ATTRIBUTE_SDP_PARTY_B_CONTENT_TYPE)||
		                name.equalsIgnoreCase(PARTY_A_EARLY_RESP_CODE)||
		                name.equalsIgnoreCase(PARTY_A_REASON_HDR) ||
		                name.equalsIgnoreCase(ATTRIBUTE_OPERATION_STOPPED)){

			if(value == null)
				msAttributes.remove(name);			
			else
				msAttributes.put(name,value);	
		}
		else
			super.setAttribute(name,value);				
	}
	
	//change for GroupedMSSBB for adding ISUP messages as attributes
	public Object getAttribute(String name) throws IllegalStateException {

		if (name.equalsIgnoreCase(IAM_ISUP) ||
				name.equalsIgnoreCase(ACM_ISUP) ||
				name.equalsIgnoreCase(ANM_ISUP) ||
				name.equalsIgnoreCase(CPG_ISUP) ||
				name.equalsIgnoreCase(REL_ISUP) ||
				name.equalsIgnoreCase(RLC_ISUP) ||
				name.equalsIgnoreCase(EXTRA_183_CPG)||
				name.equalsIgnoreCase(MS_INVITE_WITHOUT_SDP) || 
				name.equalsIgnoreCase(STOP_ANNOUNCEMENT) || 
				name.equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.TIMEOUT_REQUIRED) || 
				name.equalsIgnoreCase(com.baypackets.ase.sbb.util.Constants.UPDATE_NEEDED) || 
				name.equalsIgnoreCase(TERM_SDP) || name.equalsIgnoreCase(EARLY_DIALOUT) ||
				name.equalsIgnoreCase(NO_ACK) || name.equalsIgnoreCase(SEND_1XX)||
				name.equalsIgnoreCase(B_TAKEOVER)||
				name.equalsIgnoreCase(RE_INVITE_WITH_NEW_SDP)||
                name.equalsIgnoreCase(CDR_INFO_HEADER) ||
                name.equalsIgnoreCase(LEG_ID) ||
                name.equalsIgnoreCase(SBBOperationContext.ATTRIBUTE_SDP_PARTY_B) ||
                name.equalsIgnoreCase(SBBOperationContext.ATTRIBUTE_SDP_PARTY_B_CONTENT_TYPE) ||
                name.equalsIgnoreCase(PARTY_A_EARLY_RESP_CODE)||
                name.equalsIgnoreCase(PARTY_A_REASON_HDR)||
                name.equalsIgnoreCase(ATTRIBUTE_OPERATION_STOPPED)){

			return msAttributes.get(name);	
		}
		else
			return super.getAttribute(name);		
	}

	@Override
	public void endPlayCollectDialog() throws MediaServerException, IllegalStateException{
		try{
			MsDialogSpec dialog = new MsDialogSpec();
			_logger.debug("<SBB> Connection id =" +this.getId());
			dialog.setConnectionId(this.getId());
			_logger.debug("<APP> dialog "+dialog);
			dialog.setId(Integer.toString(MediaServerInfoHandler.MS_OPERATION_PLAY_COLLECT));
			dialog.setOperation(MsDialogSpec.OP_CODE_DIALOG_END);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this,MediaServerInfoHandler.MS_OPERATION_DIALOG_END_PLAY_COLLECT, dialog);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			_logger.error(e.getMessage(), e);
			throw new MediaServerException(e.getMessage(), e);
		}		
	}

	@Override
	public void endPlayDialog() throws MediaServerException, IllegalStateException {
			try{
				MsDialogSpec dialog = new MsDialogSpec();
				_logger.debug("<SBB> Connection id =" +this.getId());
				dialog.setConnectionId(this.getId());
				_logger.debug("<APP> dialog "+dialog);
				dialog.setId(Integer.toString(MediaServerInfoHandler.MS_OPERATION_PLAY));
				dialog.setOperation(MsDialogSpec.OP_CODE_DIALOG_END);
				MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_DIALOG_END_PLAY, dialog);
				this.addSBBOperation(handler);
				handler.start();
			}catch(ProcessMessageException e){
				_logger.error(e.getMessage(), e);
				throw new MediaServerException(e.getMessage(), e);
			}
		}
	@Override
	public void endRecordDialog() throws MediaServerException, IllegalStateException {
		try{
			MsDialogSpec dialog = new MsDialogSpec();
			_logger.debug("<SBB> Connection id =" +this.getId());
			dialog.setConnectionId(this.getId());
			_logger.debug("<APP> dialog "+dialog);
			dialog.setId(Integer.toString(MediaServerInfoHandler.MS_OPERATION_RECORD));
			dialog.setOperation(MsDialogSpec.OP_CODE_DIALOG_END);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_DIALOG_END_RECORD, dialog);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			_logger.error(e.getMessage(), e);
			throw new MediaServerException(e.getMessage(), e);
		}
	}
	@Override
	public void endPlayRecordDialog()throws MediaServerException, IllegalStateException {
		try{
			MsDialogSpec dialog = new MsDialogSpec();
			_logger.debug("<SBB> Connection id =" +this.getId());
			dialog.setConnectionId(this.getId());
			_logger.debug("<APP> dialog "+dialog);
			dialog.setId(Integer.toString(MediaServerInfoHandler.MS_OPERATION_PLAY_RECORD));
			dialog.setOperation(MsDialogSpec.OP_CODE_DIALOG_END);
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_DIALOG_END_PLAY_RECORD, dialog);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			_logger.error(e.getMessage(), e);
			throw new MediaServerException(e.getMessage(), e);
		}
		
	}

	@Override
	public void audit(MsAuditSpec auditSpec) throws MediaServerException,
			IllegalStateException {
		if(auditSpec!=null){
		try{		
			_logger.debug("<SBB> Connection id =" +this.getId());
			
			com.baypackets.ase.msadaptor.MsAuditSpec spec=new com.baypackets.ase.msadaptor.MsAuditSpec();
			spec.setMark(auditSpec.getMark());
			spec.setQueryId(auditSpec.getQueryId());
			spec.setStateList(auditSpec.getStateList());
			
			MediaServerInfoHandler handler = new MediaServerInfoHandler(this, MediaServerInfoHandler.MS_OPERATION_AUDIT, spec);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			_logger.error(e.getMessage(), e);
			throw new MediaServerException(e.getMessage(), e);
		}
		}else{
			throw new IllegalStateException("AuditSpec can not be passed as NULL:");
		}
		
	}
	
	public boolean isMediaServerConnected(){
		if(_logger.isDebugEnabled()){
			_logger.debug("Inside isMediaServerConnected() ...");
		}
		boolean isConnected=false;
		if(this.getB() != null){
			int state = ((Integer)this.getB().getAttribute(
					Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
			if(state == com.baypackets.ase.sbb.util.Constants.STATE_CONFIRMED){
				isConnected = true;
			}
		}
		if(_logger.isDebugEnabled()){
			_logger.debug("Exitting isMediaServerConnected() with value:"+isConnected);
		}
		return isConnected;
	}

}
