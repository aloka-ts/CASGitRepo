package com.genband.ase.alcx.MediaService.MediaServiceImpl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.net.URLEncoder;
import java.lang.IllegalStateException;

import javax.servlet.sip.URI;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Logger;

import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsAdaptorFactory;
import com.baypackets.ase.msadaptor.MsDialogSpec;
import com.baypackets.ase.sbb.mediaserver.*;
import com.baypackets.ase.sbb.EarlyMediaCallback;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MsCollectSpec;
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
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.sbb.impl.SBBOperationContext;
import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.sip.SipServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class _MsSessionControllerImpl extends SBBImpl implements MsSessionController
{
    protected String id;
    protected transient MediaServer mediaServer;
    protected transient MsAdaptor msAdaptor;

    private int timeout;
    private int dialogId;
    private transient MsOperationResult result;
    private boolean connected = false;

    public int getNoAnswerTimeout() { return 10000;/* nice */ }
    public void setNoAnswerTimeout(int timeout) { /* nice */ }
    
    private static Logger _logger = Logger.getLogger(_MsSessionControllerImpl.class);

    public _MsSessionControllerImpl()
    {
            this.setTimeout(10000);
    }

    public void SetCurrentServiceContext(ServiceContext sContext)
    {
        this.sContext = sContext;
        /*
		 * Set the SBB attribute in Service context to know whihc SBB is currently used in this service context
		 */
       this.sContext.setAttribute(SBBOperationContext.ATTRIBUTE_SBB,  "MediaService");
       this.sContext.setAttribute("MediaService",  this);
       
    }

    public void connect(SipServletRequest request, MediaServer mediaServer) throws MediaServerException, IllegalStateException
    {
        if (mediaServer == null)
                throw new IllegalArgumentException("Media Server object cannot be NULL");
        try {
        	
        	 if(_logger.isDebugEnabled())
  				 _logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] inside connect()");
            this.mediaServer = mediaServer;
            String uri = this.getMsAdaptor().getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
            SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
            Address addrB = factory.createAddress(uri);
            _MsConnectHandler handler = new _MsConnectHandler(request, addrB);
            this.addSBBOperation(handler);
            handler.start();
        } catch(ProcessMessageException e){
                throw new MediaServerException(e.getMessage(), e);
        } catch(ServletParseException e){
                throw new MediaServerException(e.getMessage(), e);
        }
    }

    public void dialOut(MediaServer mediaServer)  throws MediaServerException, IllegalStateException
    {
        if(mediaServer == null)
                throw new IllegalArgumentException("Media Server object cannot be NULL");
        try{
                this.mediaServer = mediaServer;
                String uri = this.getMsAdaptor().getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
                SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
                Address addrB = factory.createAddress(uri);
                _MsOneWayDialoutHandler handler = new _MsOneWayDialoutHandler(addrB);
                this.addSBBOperation(handler);

                handler.start();
        }catch(ProcessMessageException e){
                throw new MediaServerException(e.getMessage(), e);
        }catch(ServletParseException e){
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

			String uri = this.getMsAdaptor().getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_MS_DIALOG, null);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			Address addrB = factory.createAddress(uri);

			_MsDialoutHandler handler = new _MsDialoutHandler(from, addrA, addrB);
			handler.setNoAnswerTimeout(this.getNoAnswerTimeout());
			this.addSBBOperation(handler);
			handler.start();
		} catch (ProcessMessageException e) {
			throw new MediaServerException(e.getMessage(), e);
		} catch (ServletParseException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
	}


    public void dialOut(Address addrA, MediaServer mediaServer) { }

    public void play(MsPlaySpec playSpec) { }

    public void playCollect(MsPlaySpec playSpec, MsCollectSpec collectSpec) { }

    public void playRecord(MsPlaySpec playSpec, MsRecordSpec recordSpec) { }

    public void playVoiceXmlOnDialout(MediaServer mediaServer, URL resource) throws MediaServerException, IllegalStateException{
       if(mediaServer == null)
			throw new IllegalArgumentException("Media Server object cannot be NULL");
       if(resource == null)
           throw new IllegalArgumentException("VXML URL cannot be NULL");
		try{
			this.mediaServer = mediaServer;
			//this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);
			
			String uri =this.getMsAdaptor().getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_VOICEXML, resource);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
	               
               
                        String VPath= resource.toString();
                        String appSessionid =this.getApplicationSession().getId();
                        
                        if(_logger.isDebugEnabled())
           				 _logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<SBB> playVoiceXmlOnDialout voice Xml is " +VPath + " Decoded AppSession id is "+appSessionid);
           			 
           			 
                        if(VPath.indexOf(";")!=-1) {  //sip paramters has been addded to it
           			       
                           String pathonly =VPath.substring(0 ,VPath.indexOf(";"));
                           String params =VPath.substring(VPath.indexOf(";") ,VPath.length());
                        
                           if(VPath.startsWith("http"))
                        		   VPath = pathonly+URLEncoder.encode("?aai="+appSessionid,"UTF-8") +params;
                           
                        }else {
                        	
                            if(VPath.startsWith("http"))
                        	       VPath =VPath+URLEncoder.encode("?aai="+appSessionid,"UTF-8");
                        	
                        }
                        
                        if(_logger.isDebugEnabled())
              				 _logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<SBB> playVoiceXmlOnDialout voice Xml path after Encoding  " +VPath);

//                        if(VPath.indexOf("?")!=-1)
//                      	  VPath =VPath+"&aai="+this.getApplicationSession().getId(); 	
//              			else
//              				VPath =VPath+"?aai="+this.getApplicationSession().getId(); 
//			
			
			

		                URI uri1= factory.createURI(uri);
                        SipURI uri2= (SipURI)uri1;

                       uri2.setParameter("voicexml",  VPath);
                       uri2.setParameter("aai",URLEncoder.encode(appSessionid,"UTF-8")); 
                       Address addrB = factory.createAddress(uri2);
                       addrB.setDisplayName(uri2.getUser());
	
             _MsOneWayDialoutHandler handler = new _MsOneWayDialoutHandler(addrB);
			this.addSBBOperation(handler);
			
			this.getServletContext().setAttribute(this.getApplicationSession().getId(), this.getApplicationSession());
		        this.getApplicationSession().setAttribute(SBBOperationContext.ATTRIBUTE_SBB, this.getName());	
			handler.start();
		}catch(ServletParseException e){
			throw new MediaServerException(e.getMessage(), e);
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		} catch (UnsupportedEncodingException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
 }

    public void playVoiceXmlOnConnect(SipServletRequest request, MediaServer mediaServer, URL resource)throws MediaServerException, IllegalStateException { 
 if(mediaServer == null)
                        throw new IllegalArgumentException("Media Server object cannot be NULL");
 if(resource == null)
	                   throw new IllegalArgumentException("VXML URL cannot be NULL");
                try{
                        this.mediaServer = mediaServer;
                        //this.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);

                       String uri = this.getMsAdaptor().getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_VOICEXML, resource);
                        SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
                       
                        String VPath= resource.toString();
                        String appSessionid =this.getApplicationSession().getId();
                        
                        if(_logger.isDebugEnabled())
           				 _logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<SBB> playVoiceXmlOnConnect voice Xml is " +VPath + " Decoded AppSession id is "+appSessionid);
           			 
           			 
                        if(VPath.indexOf(";")!=-1) {  //sip paramters has been addded to it
           			       
                           String pathonly =VPath.substring(0 ,VPath.indexOf(";"));
                           String params =VPath.substring(VPath.indexOf(";") ,VPath.length());
                        
                           if(VPath.startsWith("http"))
                                  VPath = pathonly+URLEncoder.encode("?aai="+appSessionid,"UTF-8") +params;
                           
                        }else {
                        	
                        	 if(VPath.startsWith("http"))
                        	       VPath =VPath+URLEncoder.encode("?aai="+appSessionid,"UTF-8");
                        	
                        }
                        
                        if(_logger.isDebugEnabled())
              				 _logger.debug("[CALL-ID]"+sContext.getAttribute(SipServiceContextProvider.ORIG_CALL_ID) +"[CALL-ID] "+"<SBB> playVoiceXmlOnConnect voice Xml path after Encoding  " +VPath);
              			 
                     
//                        if(VPath.indexOf("?")!=-1) //if http params in voice xml tag
//                    	    VPath =VPath+"&aai="+this.getApplicationSession().getId(); 	
//            			else
//            				VPath =VPath+"?aai="+this.getApplicationSession().getId(); 
                      
                     
                       URI uri1= factory.createURI(uri);
                       SipURI uri2= (SipURI)uri1;

                       uri2.setParameter("voicexml",VPath );
                       uri2.setParameter("aai",URLEncoder.encode(appSessionid,"UTF-8")); 
                       Address addrB = factory.createAddress(uri2);
                       addrB.setDisplayName(uri2.getUser());
                        
                        this.getServletContext().setAttribute(this.getApplicationSession().getId(), this.getApplicationSession());
			            this.getApplicationSession().setAttribute(SBBOperationContext.ATTRIBUTE_SBB, this.getName());

                        _MsConnectHandler handler = new _MsConnectHandler(request, addrB);
                        this.addSBBOperation(handler);

                        handler.start();
                }catch(ProcessMessageException e){
                        throw new MediaServerException(e.getMessage(), e);
                }catch(ServletParseException e){
                        throw new MediaServerException(e.getMessage(), e);
                } catch (UnsupportedEncodingException e) {
        			throw new MediaServerException(e.getMessage(), e);
        		}


}
    
    public void playSubseqVoiceXmlWithINFO(URL resource) throws MediaServerException, IllegalStateException, IllegalArgumentException {}

    void parseSDP(SipServletMessage message) {
		
			if(_logger.isDebugEnabled()){
    		_logger.debug("parseSDP for making  target for dialogstart");
    	}
    	  String regex_resultant = "notfound";
          	regex_resultant= "conn:"+ message.getTo().getParameter("tag");

          sContext.setAttribute("Target", regex_resultant);
          sContext.setAttribute("DialogId", regex_resultant+"/dialog:abc"); 
          
      	if(_logger.isDebugEnabled()){
    		_logger.debug("parseSDP Target has been set to "+regex_resultant);
    	}
		
		/*
        Object sdp = null;
        try
        {
             sdp = message.getContent();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        if (sdp == null) {
//              throw new MediaServerException("No SDP to parse in given SIP message.");
        }

        String bytes = null;

        if (sdp instanceof byte[]) {
                bytes = new String((byte[])sdp);
        } else if (sdp instanceof String) {
                bytes = (String)sdp;
        } else {

        }

        String regex_resultant = "notfound";
        {
            String regex = "c=IN IP4 ([0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+)";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(bytes);

            if (m.find())
                regex_resultant = m.group(1);
        }

        {
            String regex = "m=audio ([0-9]+)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(bytes);

            if (m.find())
                regex_resultant = regex_resultant + ":" + m.group(1);
        }

        sContext.setAttribute("Target", regex_resultant);
        */
    }

    public void disconnect() throws MediaServerException, IllegalStateException
    {
        try
        {
            this.connected = false;
            DisconnectHandler handler = new DisconnectHandler();
            this.addSBBOperation(handler);
            handler.start();
        }
        catch(ProcessMessageException e)
        {
            throw new MediaServerException(e.getMessage(), e);
        }
        catch (IllegalStateException ise)
        {

		}
    }

    public void disconnectMediaServer() throws MediaServerException, IllegalStateException
    {
        try
        {
            this.connected = false;
            OneWayDisconnectHandler handler = new OneWayDisconnectHandler();
            handler.setDisconnectParty(OneWayDisconnectHandler.PARTY_B);
            this.addSBBOperation(handler);
            handler.start();
        }
        catch(ProcessMessageException e)
        {
            throw new MediaServerException(e.getMessage(), e);
        }
    }

    public MediaServer getMediaServer()
    {
            return this.mediaServer;
    }

    public int getTimeout()
    {
            return this.timeout;
    }

    public void play() throws MediaServerException, IllegalStateException
    {
        try
        {
            MsDialogSpec dialog = new MsDialogSpec();
            dialog.setConnectionId(this.getId());
            dialog.setId(String.valueOf(this.dialogId++));
            _MediaServerInfoHandler handler = new _MediaServerInfoHandler(sContext, this, _MediaServerInfoHandler.MS_OPERATION_PLAY, dialog);
            this.addSBBOperation(handler);
            handler.start();
        }
        catch(ProcessMessageException e)
        {
            throw new MediaServerException(e.getMessage(), e);
        }
    }

    public void playCollect() throws MediaServerException, IllegalStateException
    {
        try
        {
            MsDialogSpec dialog = new MsDialogSpec();
            dialog.setConnectionId(this.getId());
            dialog.setId(String.valueOf(this.dialogId++));
            _MediaServerInfoHandler handler = new _MediaServerInfoHandler(sContext, this, _MediaServerInfoHandler.MS_OPERATION_PLAY_COLLECT, dialog);
            this.addSBBOperation(handler);
            handler.start();
        }
        catch(ProcessMessageException e)
        {
            throw new MediaServerException(e.getMessage(), e);
        }
    }

    public void playRecord() throws MediaServerException, IllegalStateException
    {
        try
        {
            MsDialogSpec dialog = new MsDialogSpec();
            dialog.setConnectionId(this.getId());
            dialog.setId(String.valueOf(this.dialogId++));
            _MediaServerInfoHandler handler = new _MediaServerInfoHandler(sContext, this, _MediaServerInfoHandler.MS_OPERATION_PLAY_RECORD, dialog);
            this.addSBBOperation(handler);
            handler.start();
        }
        catch (ProcessMessageException e)
        {
            throw new MediaServerException(e.getMessage(), e);
        }
    }

    public void endPlay() throws MediaServerException, IllegalStateException
    {
        try
        {
            MsDialogSpec dialog = new MsDialogSpec();
            dialog.setConnectionId(this.getId());
            dialog.setId(String.valueOf(this.dialogId++));
            _MediaServerInfoHandler handler = new _MediaServerInfoHandler(sContext, this, _MediaServerInfoHandler.MS_OPERATION_END_PLAY, dialog);
            this.addSBBOperation(handler);
            handler.start();
        }
        catch (ProcessMessageException e)
        {
            throw new MediaServerException(e.getMessage(), e);
        }
    }

    public void setResult(MsOperationResult result)
    {
            this.result = result;
    }

    public MsOperationResult getResult()
    {
            return this.result;
    }

    public void setTimeout(int timeout)
    {
            this.timeout = timeout;
    }

    public MsAdaptor getMsAdaptor()
    {
        if (this.msAdaptor == null)
            this.msAdaptor = new _MLAdaptor(sContext);
        return this.msAdaptor;
    }

    public String getId()
    {
            return this.id;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
            super.writeExternal(out);
            out.writeBoolean(this.connected);
         //   out.writeObject(this.mediaServer);
            out.writeObject(this.id);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            super.readExternal(in);
            this.connected = in.readBoolean();
         //   this.mediaServer = (MediaServer)in.readObject();
            this.id= (String)in.readObject();
    }

    public void activate(SipSession session) {
    	
    	  super.activate(session);
    	  if(_logger.isDebugEnabled()){
    		  _logger.debug(" Activating _MsSessionControllerImpl SBB..."+this);
    		  
    	  }
    	  
		if (sContext == null) {

			if (this.getApplicationSessin() != null) {

				Object sCtx = this.getApplicationSessin().getAttribute(
						SipServiceContextProvider.SERVICE_CONTEXT);
				if (sCtx != null)
					sContext = (ServiceContext) sCtx;

			}

			if (_logger.isDebugEnabled()) {
				_logger
						.debug(" activating _MsSessionControllerImpl ServiceContext has been set to..."
								+ sContext);

			}
		}
           

            EarlyMediaCallback callback =  (EarlyMediaCallback)this.getCallback(EarlyMediaCallback.class.getName());
            if(callback instanceof MsConnectHandler){
                    ((MsConnectHandler)callback).sessionActivated(session);
            }
    }

    public int fireEvent(SBBEvent event){
            if(event != null && event.getEventId().equals(SBBEvent.EVENT_CONNECTED)){
                    this.connected = true;
            }
            return super.fireEvent(event);
    }

    ServiceContext sContext = null;
    
    public void stopMediaOperations() throws MediaServerException, IllegalStateException{
    	//TODO: Need Implementation if in case required for SCE
    }
	@Override
	public void record(MsRecordSpec recordSpec) throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void playCollect(MsPlaySpec playSpec, MsCollectSpec collectSpec,
			String groupTopology) throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void playRecord(MsPlaySpec playSpec, MsRecordSpec recordSpec,
			String groupTopology) throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endPlayDialog() throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endRecordDialog() throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endPlayCollectDialog() throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void endPlayRecordDialog() throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void audit(MsAuditSpec auditSpec) throws MediaServerException,
			IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ArrayList<String> getSupportedMediaTypes(SipServletMessage message)
			throws MediaServerException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public ArrayList<String> getSupportedMediaTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
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

