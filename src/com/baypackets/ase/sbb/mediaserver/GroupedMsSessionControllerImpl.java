package com.baypackets.ase.sbb.mediaserver;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.HashMap;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.mediaserver.MediaServerManager;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsAdaptorFactory;
import com.baypackets.ase.sbb.GroupedMsEvent;
import com.baypackets.ase.sbb.GroupedMsSessionController;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MediaServerSelector;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.b2b.ConnectHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipFactory;

/**
 * This is SBB to support geographically closer media server functionality.
 * This class extends MsSessionController class and uses its methods.
 *
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class GroupedMsSessionControllerImpl extends MsSessionControllerImpl implements GroupedMsSessionController{

	private static Logger _logger = Logger.getLogger(GroupedMsSessionControllerImpl.class);
	private static final long serialVersionUID = -674054328848884324L;
	private int retryCounter = 0;	
	private boolean isSelectRemoteMS = false;	
	private int allowRemoteOnBusy = 0;	
	private MediaServer connectedMS = null;	
	private int capabilities = 0;	
	private transient SBBEventListener sbbEventListener = null;	
	private GroupedMSEventListener groupedMSEventListener = null;	
	private MediaServerManager mediaServerManager = null;	
	MediaServerSelector msSelector = null;
	private SipServletRequest request = null;
	private Address addrA = null;
	private Address from = null;
	private SipServletRequest voiceXMLRequest = null;
	private URL resource = null;
	private URL dialoutResource = null;
	private transient Object prevHandler =null; 
	
	private int OPRATION_TYPE = 0 ;
	private int usePrivateMsOnPvtIf=0;
	private static final int CONNECT = 1 ;
	private static final int DIALOUT = 2 ;
	private static final int DIALOUT_TO_A = 3 ;
	private static final int DIALOUT_FROM = 4 ;
	private static final int PLAY_VOICEXML_CONNECT = 5 ;
	private static final int PLAY_VOICEXML_DIALOUT = 6 ;
	
	
	public GroupedMsSessionControllerImpl() {
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl() called...");
		}
		groupedMSEventListener = new GroupedMSEventListener();
		
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		this.allowRemoteOnBusy = (Integer.parseInt(config.getValue(Constants.ALLOW_REMOTE_MS_ON_BUSY)) == 1 ? 1 : 0);
		
		if(config.getValue(Constants.USE_PRIVATE_MS_ON_PRIVATE_IF)!=null){
		  this.usePrivateMsOnPvtIf= (Integer.parseInt(config.getValue(Constants.USE_PRIVATE_MS_ON_PRIVATE_IF)) == 1 ? 1 : 0);
		}
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("MsSessionController(): Setting allowremoteonbusy property to : " + this.allowRemoteOnBusy +" useprivatems "+usePrivateMsOnPvtIf);
		}
	}
	
	
	/**
	 * Used for connecting to Media Server for particular capability.
	 * 
	 * This is the function that will be called by the application.
	 */
	public void connect(SipServletRequest request, int capabilities) throws MediaServerException {
		
		/**The connect as well as dialout return the event CONNECTED or CONNECT_FAILED. 
		 * So to know which one of the operation was failed, we save it in a local variable*/
		this.OPRATION_TYPE = this.CONNECT ;
		
		/**Whenever the operation is triggered from the application, the retry counter must be 
		 * reset to 0, so that the counter from the previous sbb operation is not retained here. */
		this.retryCounter=0;
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : connect() enter");
		}
		this.request = request;
		this.capabilities = capabilities;
		
		/**Call a private connect function, which will be called repeatedly unless the operation
		 * completes successfully, or we run out of media servers to try connecting to.
		 * This is needed since the retry counter needs to be properly updated.*/
		this.connect();			
	}
	void connect()throws MediaServerException {
		// get the ms selector from the servlet context.
		if(msSelector == null)
			msSelector = (MediaServerSelector) this.getServletContext().getAttribute(MediaServerSelector.class.getName());
		//select MS		
		this.selectMediaSever();
		// increment the counter by 1, so that if the operation fails, it's value can be checked against.
		retryCounter++;
		super.setEventListener(groupedMSEventListener);
		super.connect(request, this.connectedMS);
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : connect() exit ... connectedMS is: " + this.connectedMS);
		}
	}
	
	/**
	 * This is the function that will be called by the application.
	 */
	public void dialOut( int capabilities) throws MediaServerException, IllegalStateException {
		
		/**The connect as well as dialout return the event CONNECTED or CONNECT_FAILED. 
		 * So to know which one of the operation was failed, we save it in a local variable*/
		this.OPRATION_TYPE = this.DIALOUT ;
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : dialOut() enter");
		}
		
		this.capabilities = capabilities;
		this.retryCounter=0;
		
		/**Call a private function, which will be called repeatedly unless the operation
		 * completes successfully, or we run out of media servers to try connecting to.
		 * This is needed since the retry counter needs to be properly updated.*/
		this.dialOut();
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : dialout() exit ... connectedMS is: " + this.connectedMS);
		}
	}
	void dialOut() throws MediaServerException, IllegalStateException{
		if(msSelector == null)
			msSelector = (MediaServerSelector) this.getServletContext().getAttribute(MediaServerSelector.class.getName());
		this.selectMediaSever();
		retryCounter++;
		super.setEventListener(groupedMSEventListener);
		super.dialOut(this.connectedMS);
	}
	
	/**
	 * This is the function that will be called by the application.
	 */	
	public void dialOut(Address addrA,int capabilities) throws MediaServerException, IllegalStateException {
		
		/**The connect as well as dialout return the event CONNECTED or CONNECT_FAILED. 
		 * So to know which one of the operation was failed, we save it in a local variable*/
		this.OPRATION_TYPE = this.DIALOUT_TO_A ;
		
		this.retryCounter=0;
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : dialOut(addr) enter");
		}
		this.addrA=addrA;
		this.capabilities = capabilities;
		
		/**Call a private function, which will be called repeatedly unless the operation
		 * completes successfully, or we run out of media servers to try connecting to.
		 * This is needed since the retry counter needs to be properly updated.*/
		this.dialOutToA();
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : dialout() exit ... connectedMS is: " + this.connectedMS);
		}
	}
	void dialOutToA() throws MediaServerException, IllegalStateException{
		if(msSelector == null)
			msSelector = (MediaServerSelector) this.getServletContext().getAttribute(MediaServerSelector.class.getName());
		this.selectMediaSever();
		retryCounter++;
		super.setEventListener(groupedMSEventListener);
		super.dialOut(this.addrA,this.connectedMS);
	}
	
/**
	 * This is the function that will be called by the application.
	 */
	public void dialOut(MediaServer mediaServer)
    throws MediaServerException, IllegalStateException
  {
    if (_logger.isDebugEnabled()) {
      _logger.debug("GroupedMsSessionControllerImpl : dialOut(mediaServer) enter");
    }
    super.setEventListener(this.groupedMSEventListener);
    super.dialOut(mediaServer);
  }
  
	/**
	 * This is the function that will be called by the application.
	 */
	public void dialOut(Address from, Address addrA,int capabilities) throws MediaServerException, IllegalStateException {
		
		/**The connect as well as dialout return the event CONNECTED or CONNECT_FAILED. 
		 * So to know which one of the operation was failed, we save it in a local variable*/
		this.OPRATION_TYPE = this.DIALOUT_FROM ;
		
		this.retryCounter=0;
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : dialOut(addr,addr) enter");
		}
		this.addrA=addrA;
		this.from=from ;
		this.capabilities = capabilities;
		
		/**Call a private function, which will be called repeatedly unless the operation
		 * completes successfully, or we run out of media servers to try connecting to.
		 * This is needed since the retry counter needs to be properly updated.*/
		this.dialOutFrom();
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : dialout() exit ... connectedMS is: " + this.connectedMS);
		}
	}
	void dialOutFrom() throws MediaServerException, IllegalStateException{
		if(msSelector == null)
			msSelector = (MediaServerSelector) this.getServletContext().getAttribute(MediaServerSelector.class.getName());
		this.selectMediaSever();
		retryCounter++;
		super.setEventListener(groupedMSEventListener);
		super.dialOut(this.from,this.addrA,this.connectedMS);
	}
	
	/**
	 * This is the function that will be called by the application.
	 */
	public void playVoiceXmlOnConnect(SipServletRequest request, URL resource) throws MediaServerException, IllegalStateException {
		
		/**The connect as well as dialout return the event CONNECTED or CONNECT_FAILED. 
		 * So to know which one of the operation was failed, we save it in a local variable*/
		this.OPRATION_TYPE = this.PLAY_VOICEXML_CONNECT ;
		
		//This is done to route the VXML calls to only XMS media servers
		//In addition to this change we need to configure the XMS servers with VXML capability only and 
		//IPMS servers with other capabilities
		this.capabilities = MediaServer.CAPABILITY_VOICE_XML; 
				
		this.retryCounter=0;
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : playVoiceXmlOnConnect(SipServletRequest,URL) enter");
		}
		this.voiceXMLRequest=request;
		this.resource=resource;
		
		/**Call a private function, which will be called repeatedly unless the operation
		 * completes successfully, or we run out of media servers to try connecting to.
		 * This is needed since the retry counter needs to be properly updated.*/
		this.voiceXMLOnConnect();
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : playVoiceXmlOnConnect(SipServletRequest,URL) exit ... connectedMS is: " + this.connectedMS);
		}
	}
	void voiceXMLOnConnect() throws MediaServerException, IllegalStateException{
		if(msSelector == null)
			msSelector = (MediaServerSelector) this.getServletContext().getAttribute(MediaServerSelector.class.getName());
		this.selectMediaSever();
		retryCounter++;
		super.setEventListener(groupedMSEventListener);
		super.playVoiceXmlOnConnect(this.voiceXMLRequest, this.connectedMS, this.resource);
	}
	
	/**
	 * This is the function that will be called by the application.
	 */
	public void playVoiceXmlOnDialout(URL resource) throws MediaServerException, IllegalStateException {
		
		/**The connect as well as dialout return the event CONNECTED or CONNECT_FAILED. 
		 * So to know which one of the operation was failed, we save it in a local variable*/
		this.OPRATION_TYPE = this.PLAY_VOICEXML_DIALOUT ;
		//This is done to route the VXML calls to only XMS media servers
		//In addition to this change we need to configure the XMS servers with VXML capability only and 
		//IPMS servers with other capabilities
		this.capabilities = MediaServer.CAPABILITY_VOICE_XML; 

		
		this.retryCounter=0;
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : playVoiceXmlOnDialout(URL) enter");
		}
		this.dialoutResource=resource;
		
		/**Call a private function, which will be called repeatedly unless the operation
		 * completes successfully, or we run out of media servers to try connecting to.
		 * This is needed since the retry counter needs to be properly updated.*/
		this.voiceXMLOnDialout();
		if (_logger.isDebugEnabled()) {
			_logger.debug("GroupedMsSessionControllerImpl : playVoiceXmlOnDialout(URL) exit ... connectedMS is: " + this.connectedMS);
		}
	}
	void voiceXMLOnDialout() throws MediaServerException, IllegalStateException{
		if(msSelector == null)
			msSelector = (MediaServerSelector) this.getServletContext().getAttribute(MediaServerSelector.class.getName());
		this.selectMediaSever();
		retryCounter++;
		super.setEventListener(groupedMSEventListener);
		super.playVoiceXmlOnDialout(this.connectedMS, this.dialoutResource);
	}
	
	/*
	 * media-server selection according to ase property allowRemoteOnBusy
	 * first try to get local media server, if all local MS are down and allowRemoteBusy=1
	 * then try to get remote MS.
	 */
	private void selectMediaSever()throws MediaServerException{
		
		int privateMS = 0;
		if (usePrivateMsOnPvtIf == 1) {
			
			if(_logger.isDebugEnabled()){
				_logger.debug("selectMediaSever:: privatems is enabled when call reeceived on private interface "+usePrivateMsOnPvtIf);
			}
			Boolean isPrivate = (Boolean) getApplicationSession().getAttribute(
					Constants.RECEIVED_PRIVATE_IF);

			privateMS = isPrivate != null && isPrivate.booleanValue() ? 1 : 0;
		}else{
			if(_logger.isDebugEnabled()){
				_logger.debug("selectMediaSever:: use only public media servers as using private is disabled ");
			}
		}
		
		if(_logger.isDebugEnabled()){
			_logger.debug("selectMediaSever:: isPrivate  is "+privateMS);
		}
		
		if(allowRemoteOnBusy==0){
			this.connectedMS = msSelector.selectByCapabilities(capabilities, LOCAL_MS,privateMS);
			if(_logger.isDebugEnabled()){
				_logger.debug("Selected local media server::"+this.connectedMS);
			}
			if(retryCounter == 0 && this.connectedMS == null) {
				throw new MediaServerException("no local Media Server found for given capabilities");
			}
			return;
		}
		
		if(allowRemoteOnBusy==1){
			//non 2xx received try connect different media server but retryCounter becomes 1 now
			//safety check to select remote media server, when received event for 2xx for invite then
			//isSelectRemoteMS=false
			if(isSelectRemoteMS){
				this.connectedMS = msSelector.selectByCapabilities(capabilities, REMOTE_MS,privateMS);
				if(this.connectedMS == null) {		//if no remote ms found too, throw exception
					throw new MediaServerException("no local/remote Media Server found for given capabilities");
				}
				return;
			}
			this.connectedMS = msSelector.selectByCapabilities(capabilities, LOCAL_MS, privateMS);
			if(retryCounter == 0 && this.connectedMS == null) {
				this.connectedMS = msSelector.selectByCapabilities(capabilities, REMOTE_MS,privateMS);
				if(this.connectedMS == null) {		//if no remote ms found too, throw exception
					throw new MediaServerException("no local/remote Media Server found for given capabilities");
				}
				isSelectRemoteMS = true;
			}
			if(_logger.isDebugEnabled()){
				_logger.debug("Selected local/remote media server::"+this.connectedMS);
			}
			return;
		}
		
		/*if(isSelectRemoteMS) {	//remote ms
			this.connectedMS = msSelector.selectByCapabilities(capabilities, REMOTE_MS);
		}
		else {
			this.connectedMS = msSelector.selectByCapabilities(capabilities, LOCAL_MS);
			//if no local MS found, select remote ms (this will happen only first time)
			if(retryCounter == 0 && this.connectedMS == null) {
				this.connectedMS = msSelector.selectByCapabilities(capabilities, REMOTE_MS);
				if(this.connectedMS == null) {		//if no remote ms found too, throw exception
					throw new MediaServerException("no local/remote Media Server found for given capabilities");
				}
				isSelectRemoteMS = true;
			}
		}*/
	}
	
	/*----------------------------------------------------------------------------------------------------*/
	
	/**
     * Associates the event listener with this SBB.
     * @param listener - Listener to be associated.
     * @throws IllegalStateException if this object is already invalidated.
     */
	public void setEventListener(SBBEventListener listener) throws IllegalStateException {
		this.sbbEventListener = listener;

		if (this.sbbEventListener != null) {
			// Store the name of the listener class in the app session so that it
			// may be reconstructed during session activation on the cluster peer.
			this.appSession.setAttribute(this.getName() + com.baypackets.ase.sbb.util.Constants.SBB_LISTENER_CLASS, 
						this.sbbEventListener.getClass().getName());
		}
	}
	
	//Written for FT scenario but not required as of now.
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeInt(this.retryCounter);
		out.writeBoolean(isSelectRemoteMS);
		//out.writeObject(connectedMS);
		out.writeInt(capabilities);
		//out.writeObject(sbbEventListener);
	}

	//Written for FT scenario but not required as of now.
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.retryCounter = in.readInt();
		this.isSelectRemoteMS = in.readBoolean();
		//this.connectedMS = (MediaServer)in.readObject();
		this.capabilities = in.readInt();
		//this.sbbEventListener = (SBBEventListener)in.readObject();
	}

	
	/**
	 * SBBEventListener class that listen to MSSessionController
	 * SBB and reports back to Service
	 *
	 */
	private class GroupedMSEventListener implements SBBEventListener {
		private static final long serialVersionUID = -296466986103703719L;
	
		public void activate(SBB sbb) {
		}
	
		public int handleEvent(SBB sbb, SBBEvent event) {
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("GroupedMSEventListener : handleEvent() enter");
			}
			
			GroupedMsEvent groupedMsEvent = new GroupedMsEvent();
			groupedMsEvent.setEventId(event.getEventId());
			groupedMsEvent.setReasonCode(event.getReasonCode());
			groupedMsEvent.setMessage(event.getMessage());
			
			if(event.getMessage() instanceof SipServletRequest) 
			{
				if(event.getEventId().equals(SBBEvent.EVENT_CONNECTED)) {	//connected to MS
					groupedMsEvent.setConnectedMediaServer(connectedMS);
					isSelectRemoteMS=false;
					if (_logger.isDebugEnabled()) {
						_logger.debug("GroupedMSEventListener : MS connected: " + connectedMS);
					}								
				}
				return sbbEventListener.handleEvent(sbb, groupedMsEvent);
			}
			else
			{
				SipServletResponse response = (SipServletResponse)event.getMessage();
				
				if(event.getEventId().equals(SBBEvent.EVENT_CONNECT_FAILED) && response != null && response.getRequest() != null 
						&& response.getRequest().getMethod().equalsIgnoreCase("INVITE") && (response.getStatus()>299)) {	//MS busy or Timeout
					
					if (_logger.isDebugEnabled()) {
						_logger.debug("GroupedMSEventListener.handleEvent(): Media server busy / timeout ... retrying : Cause - "+response.getStatus());
					}
									
					if(mediaServerManager == null)
						mediaServerManager = (MediaServerManager) sbb.getServletContext().getAttribute(MediaServerSelector.class.getName());		
					
					int localMSCount = mediaServerManager.getActiveMSCount(capabilities, LOCAL_MS);
					int remoteMSCount = mediaServerManager.getActiveMSCount(capabilities, REMOTE_MS);
					
					try{
						//retry (SR) logic
						if(!isSelectRemoteMS && retryCounter < localMSCount) {
							
							if(OPRATION_TYPE == CONNECT){
								removeA();
								removeB();
								connect();
							}else if(OPRATION_TYPE == DIALOUT){
								removeB();
								dialOut();
							}else if(OPRATION_TYPE == DIALOUT_TO_A){
								removeA();
								removeB();
								dialOutToA();
							}else if(OPRATION_TYPE == DIALOUT_FROM){
								removeA();
								removeB();
								dialOutFrom();
							}else if (OPRATION_TYPE == PLAY_VOICEXML_CONNECT){
								removeA();
								removeB();
								voiceXMLOnConnect();
							}else if (OPRATION_TYPE == PLAY_VOICEXML_DIALOUT){
								removeB();
								voiceXMLOnDialout();
							}
							
							return SBBEventListener.NOOP;
						}
						else if(!isSelectRemoteMS && retryCounter >= localMSCount && allowRemoteOnBusy == 0) {
							return sbbEventListener.handleEvent(sbb, groupedMsEvent);
						}
						else if(!isSelectRemoteMS && retryCounter >= localMSCount && allowRemoteOnBusy == 1) {	//allow to go on remote
							if(remoteMSCount != 0) {		//switching from local ms to remote ms
								_logger.error("GroupedMSEventListener : Trying for Remote MS ");
								isSelectRemoteMS = true;
								if(OPRATION_TYPE == CONNECT){
									removeA();
									removeB();
									connect();
								}else if(OPRATION_TYPE == DIALOUT){
									removeB();
									dialOut();
								}else if(OPRATION_TYPE == DIALOUT_TO_A){
									removeA();
									removeB();
									dialOutToA();
								}else if(OPRATION_TYPE == DIALOUT_FROM){
									removeA();
									removeB();
									dialOutFrom();
								}else if (OPRATION_TYPE == PLAY_VOICEXML_CONNECT){
									removeA();
									removeB();
									voiceXMLOnConnect();
								}else if (OPRATION_TYPE == PLAY_VOICEXML_DIALOUT){
									removeB();
									voiceXMLOnDialout();
								}
								
								return SBBEventListener.NOOP;
							}
							else {
								return sbbEventListener.handleEvent(sbb, groupedMsEvent);
							}
						}
						else if(isSelectRemoteMS && retryCounter < localMSCount+remoteMSCount){
							if(_logger.isDebugEnabled()) 
								_logger.debug("GroupedMSEventListener : Trying for Remote MS.. ");
							if(OPRATION_TYPE == CONNECT){
								removeA();
								removeB();
								connect();
							}else if(OPRATION_TYPE == DIALOUT){
								removeB();
								dialOut();
							}else if(OPRATION_TYPE == DIALOUT_TO_A){
								removeA();
								removeB();
								dialOutToA();
							}else if(OPRATION_TYPE == DIALOUT_FROM){
								removeA();
								removeB();
								dialOutFrom();
							}else if (OPRATION_TYPE == PLAY_VOICEXML_CONNECT){
								removeA();
								removeB();
								voiceXMLOnConnect();
							}else if (OPRATION_TYPE == PLAY_VOICEXML_DIALOUT){
								removeB();
								voiceXMLOnDialout();
							}
							return SBBEventListener.NOOP;
						}
						else {
							return sbbEventListener.handleEvent(sbb, groupedMsEvent);
						}
															
					}
					catch(MediaServerException mse ) {
						return sbbEventListener.handleEvent(sbb, groupedMsEvent);
					}
					catch(IllegalStateException ise ) {
						return sbbEventListener.handleEvent(sbb, groupedMsEvent);
					}
				}else {
					 return sbbEventListener.handleEvent(sbb, groupedMsEvent);						
				}
			}
		}
		
	}
	
	 public void stopMediaOperations(Object obj) throws MediaServerException, IllegalStateException{
		 this.prevHandler=obj;
		 this.stopMediaOperations();
	 }
	 
	 public Object getPrevHandler(){
		 return this.prevHandler;
	 }
	 
	 public void removePrevHandler(){
		 this.prevHandler=null;
	 }
	
}
