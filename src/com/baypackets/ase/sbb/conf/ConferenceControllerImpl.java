package com.baypackets.ase.sbb.conf;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.mediaserver.MediaServerManager;
import com.baypackets.ase.msadaptor.MsAdaptor;
import com.baypackets.ase.msadaptor.MsAdaptorFactory;
import com.baypackets.ase.msadaptor.MsConfSpec;
import com.baypackets.ase.sbb.ConferenceController;
import com.baypackets.ase.sbb.ConferenceInfo;
import com.baypackets.ase.sbb.ConferenceParticipant;
import com.baypackets.ase.sbb.ConferenceRegistry;
import com.baypackets.ase.sbb.ConnectException;
import com.baypackets.ase.sbb.DisconnectException;
import com.baypackets.ase.sbb.GroupedMsEvent;
import com.baypackets.ase.sbb.MediaServer;
import com.baypackets.ase.sbb.MediaServerException;
import com.baypackets.ase.sbb.MediaServerSelector;
import com.baypackets.ase.sbb.MsConferenceSpec;
import com.baypackets.ase.sbb.MsConferenceStream;
import com.baypackets.ase.sbb.MsMonitorSpec;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.mediaserver.MsSessionControllerImpl;
import com.baypackets.ase.sbb.util.Constants;

public class ConferenceControllerImpl extends MsSessionControllerImpl 
										implements ConferenceController {
	
	private static final Logger logger = Logger.getLogger(ConferenceControllerImpl.class);
	private static final long serialVersionUID = -243547432424397411L;
	private String conferenceId = null;
	private ArrayList participantIds = new ArrayList();
	private transient ArrayList participants = new ArrayList();
	private ArrayList activeSpeakers = new ArrayList();
	private ConferenceInfoImpl confInfo = null;
	MediaServerSelector msSelector = null;
	private GroupedMSEventListener groupedMSEventListener = new GroupedMSEventListener();
	private transient SBBEventListener sbbEventListener = null;	
	private int capabilities=0;
	private int retryCounter = 0;	
	private boolean isSelectRemoteMS = false;
	private MsConferenceSpec confSpec=null;
	private MediaServerManager mediaServerManager = null;	
	private MediaServer connectedMS=null;
	// boolean flag that will be used to determine weather groupedMSEventListener will be used or not.
	private boolean connectWithCapabilities=false;	
	
	public void participantUnregistered(ConferenceParticipant participant) {
		//NO OP
	}

	public void connect(MsConferenceSpec conf, MediaServer mediaServer) throws IllegalStateException, IllegalArgumentException, ConnectException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside connect() IN");
			logger.debug("Return value of isValid(): " +isValid());
		}
		if(!isValid())	{
			throw new IllegalStateException("SBB is not Valid");
		}
		if(mediaServer == null)
			throw new IllegalArgumentException("Media Server object cannot be NULL");
		try{

			ServletContext context = this.getServletContext();
			ConferenceRegistry mgr =  (ConferenceRegistry)
						context.getAttribute(ConferenceRegistry.class.getName());
			ConferenceInfo info = mgr.findByConferenceID(conf.getConferenceId());
			if(info != null){
				throw new IllegalArgumentException("Conference Identifier is already in use.");	
			}
			
			this.conferenceId = conf.getConferenceId();
			super.mediaServer = mediaServer;
			super.msAdaptor = MsAdaptorFactory.getInstance().getMsAdaptor(mediaServer);
			
			//Check connectWith capabilities if true Controller's connect method will use groupedMSEventListener that will pass event to application's
			//SBBEventListener.
			if(!connectWithCapabilities)
			super.setEventListener(sbbEventListener);
			
			connectWithCapabilities=false;
			
			this.setId();

			if(logger.isDebugEnabled()){
				logger.debug("Creating the URI for the Media Server");
			}
			String uri = super.msAdaptor.getMediaServerURI(mediaServer, MsAdaptor.CONNECTION_TYPE_CONFERENCE, this.conferenceId);
			SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			Address addrB = factory.createAddress(uri);
			
			//Start the connect handler.
			if(logger.isDebugEnabled()){
				logger.debug("Registering and starting the Connect Handler.");
			}
			ConferenceConnectHandler handler = new ConferenceConnectHandler(addrB, conf);
			this.addSBBOperation(handler);
			handler.setMediaServer(super.mediaServer);
			handler.start();
			
			//Also create the Active speaker Notification handler.
			if(logger.isDebugEnabled()){
				logger.debug("Registering and starting the ASN Handler.");
			}
			ConferenceASNHandler asnHandler = new ConferenceASNHandler();
			this.addSBBOperation(asnHandler);
			asnHandler.start();
			//Also create the NoMediaInfo handler.
			if(logger.isDebugEnabled()){
				logger.debug("Registering and starting the NoMedia Handler.");
			}
			ConferenceNoMediaHandler noMediaHandler = new ConferenceNoMediaHandler();
			this.addSBBOperation(noMediaHandler);
			noMediaHandler.start();	
						
		}catch(ProcessMessageException e){
			throw new ConnectException(e.getMessage(), e);
		}catch(ServletParseException e){
			throw new ConnectException(e.getMessage(), e);
		}catch(MediaServerException e){
			throw new ConnectException(e.getMessage(), e);
		}
	}

	public void connect(MsConferenceSpec conf, int capabilities) throws IllegalStateException, 
	IllegalArgumentException, ConnectException, MediaServerException{
		if(logger.isDebugEnabled())
			logger.debug("Inside connect(spec,capability)......");
		if(msSelector == null)
			msSelector = (MediaServerSelector) this.getServletContext().getAttribute(MediaServerSelector.class.getName());
		this.capabilities=capabilities;
		this.confSpec=conf;
		retryCounter=0;
		//setting connect with capabilities as true so Controller's connect method will use groupedMSEventListener 
		//that will pass event to application's SBBEventListener.
		super.setEventListener(groupedMSEventListener);
		connect();
		if(logger.isDebugEnabled())
			logger.debug("Exitting connect(spec,capability)......");
	}
	
	private void connect() throws IllegalStateException, IllegalArgumentException,MediaServerException,ConnectException{
		this.selectMediaserver();
		retryCounter++;			
		connectWithCapabilities=true;
		this.connect(confSpec, connectedMS);
	}
	
	private void selectMediaserver() throws MediaServerException{
		
		Object privateObj = getApplicationSession().getAttribute("RECEIVED_PRIVATE_IF");
	    boolean isPrivate = false;
	    if (privateObj != null) {
	      isPrivate = (Boolean)getApplicationSession().getAttribute("RECEIVED_PRIVATE_IF");
	    }
		int privateip=isPrivate?1:0;
		
		if(isSelectRemoteMS) {	//remote ms
			this.connectedMS = msSelector.selectByCapabilities(capabilities, REMOTE_MS, privateip);
		}
		else {
			this.connectedMS  = msSelector.selectByCapabilities(capabilities, LOCAL_MS,privateip);
			//if no local MS found, select remote ms (this will happen only first time)
			if(retryCounter == 0 && this.connectedMS == null) {
				this.connectedMS  = msSelector.selectByCapabilities(capabilities, REMOTE_MS,privateip);
				if(	this.connectedMS  == null) {		//if no remote ms found too, throw exception
					logger.error("no local/remote Media Server found for given capabilities..");
					throw new MediaServerException("no local/remote Media Server found for given capabilities");
				}
				isSelectRemoteMS = true;
			}
		}
	}

	public void disconect() throws IllegalStateException, DisconnectException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside disconnect()....");
		}
		//Added to cleanup conference id from conference Registry	
		this.unregisterConference();
		try{
			super.disconnectMediaServer();
		}catch(MediaServerException e){
			throw new DisconnectException(e.getMessage(), e);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Exitting disconnect()....");
		}
	}

	public Iterator getParticipants() {
		return this.participants.iterator();
	}

	public boolean isActiveSpeaker(ConferenceParticipant participant) {
		String participantId = participant.getId();
		if(participantId == null)
				return false;
		return this.activeSpeakers != null && this.activeSpeakers.contains(participantId);
	}

	public void join(ConferenceParticipant[] joiningParticipants, String[] modes) throws IllegalArgumentException, MediaServerException {
		if(logger.isDebugEnabled())
				logger.debug("<SBB> get id called "+this.getId());
		if(logger.isDebugEnabled()){
			logger.debug("Validating the input for JOIN");
		}
		for(int i=0; i<joiningParticipants.length;i++){
			if(joiningParticipants[i].getId() == null){
				throw new IllegalArgumentException("Participant ID cannot be NULL");
			}
			this.checkMediaServer(joiningParticipants[i].getMediaServer());
		}
		
		try{

			if(logger.isDebugEnabled()){
				logger.debug("Creating the operation spec for JOIN");
			}
			MsConfSpec confSpec = new MsConfSpec();
			confSpec.setId(this.getConferenceId());
			logger.debug("<SBB> setting connection is in MsConfSpec "+this.getId());
			confSpec.setConnectionId(this.getId());
			confSpec.setOperation(MsConfSpec.OP_CODE_JOIN_PARTICIPANT);
			
			for(int i=0; i<joiningParticipants.length;i++){
				String mode = (modes != null && modes.length > i) ? modes[i] : null;
				confSpec.joinParticipant(joiningParticipants[i].getId(), mode,joiningParticipants[i].getDisplayRegionId());
				confSpec.addConferenceParticipantSBB(joiningParticipants[i]);
			}

			if(logger.isDebugEnabled()){
				logger.debug("Creating and registering the JOIN command handler.");
			}
			ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
			this.addSBBOperation(handler);
			handler.start();
			
			//Add these participants to the list
			if(logger.isDebugEnabled()){
				logger.debug("Adding the participants to the local list");
			}
			for(int i=0; i<joiningParticipants.length;i++){
				this.addParticipant(joiningParticipants[i]);
			}
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public void unjoin(ConferenceParticipant[] leavingParticipants) throws MediaServerException {
		this.unjoin(leavingParticipants, null);
	}
	
	@Override
	public void unjoin(MsConferenceStream[] streams)throws MediaServerException {
		this.unjoin(null, streams);		
	}

	@Override
	public void unjoin(ConferenceParticipant[] leavingParticipants,MsConferenceStream[] streams) throws MediaServerException {
		if(logger.isDebugEnabled()){
			logger.debug("Validating the inputs for UNJOIN");
		}
		if (leavingParticipants != null) {
			for (int i = 0; i < leavingParticipants.length; i++) {
				if (leavingParticipants[i].getId() == null) {
					throw new IllegalArgumentException(
							"Participant ID cannot be NULL");
				}

				if (!this.participantIds.contains(leavingParticipants[i]
						.getId())) {
					throw new IllegalArgumentException(
							"Participant is not a member in the conference");
				}
			}
		}
		if(streams!=null){
		for (int i = 0; i < streams.length; i++) {
			
			if (streams[i].getMedia() != MsConferenceStream.AUDIO_MEDIA	&& streams[i].getMedia() != MsConferenceStream.VIDEO_MEDIA) {
				throw new IllegalArgumentException("Incorrect value for media attribute of modify stream");
			}
			if (streams[i].getId1() == null) {
				throw new IllegalArgumentException("ID1 cannot be NULL");
			}
			if (streams[i].getId2() == null) {
				throw new IllegalArgumentException("ID2 cannot be NULL");
			}
			if (streams[i].getDirection() != MsConferenceStream.TO_ID1 && streams[i].getDirection() != MsConferenceStream.FROM_ID1) {
				throw new IllegalArgumentException("Incorrect value for direction attribute of unjoin stream");
			}
		}
		}
		
		try{
			if(logger.isDebugEnabled()){
				logger.debug("Creating the Media Server OP Spec for UNJOIN");
			}
			MsConfSpec confSpec = new MsConfSpec();
			confSpec.setId(this.getConferenceId());
			confSpec.setConnectionId(this.getId());
			//Check for leaving Participants
			
			if (leavingParticipants != null) {
				confSpec.setOperation(MsConfSpec.OP_CODE_UNJOIN_PARTICIPANT);

				for (int i = 0; i < leavingParticipants.length; i++) {
					confSpec.leaveParticipant(leavingParticipants[i].getId());
					confSpec.addConferenceParticipantSBB(leavingParticipants[i]);
				}
			}
			// Check for streams to be unjoined
			if (streams != null) {
				confSpec.setOperation(confSpec.getOperation() | MsConfSpec.OP_CODE_UNJOIN_STREAM); // Keep previous operation also valid
				for (int i = 0; i < streams.length; i++) {
					confSpec.addUnjoinStream(streams[i]);
				}
			}	
			if(logger.isDebugEnabled()){
				logger.debug("Registering and joining the command handler for UNJOIN");
			}
			ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
			this.addSBBOperation(handler);
			handler.start();
			
			//If handle.start() executed successfully without any exception then remove participants from list			
			if (leavingParticipants != null){
				if (logger.isDebugEnabled()) 
					logger.debug("Removing the participant from the local list");
				for (int i = 0; i < leavingParticipants.length; i++) 
					this.removeParticipant(leavingParticipants[i]);				
			}
		}catch(ProcessMessageException e){
			logger.debug("Exception in unjoin()method.......");
			throw new MediaServerException(e.getMessage(), e);
		}			
	}
	public void updateConference(MsConferenceSpec spec) throws IllegalArgumentException, MediaServerException {
		if(spec.getConferenceId() != null &&
				!spec.getConferenceId().equals(this.conferenceId)){
			throw new IllegalArgumentException("Conference IDs do not match");
		}	
		
		try{
			MsConfSpec confSpec = new MsConfSpec();
			confSpec.setId(this.getConferenceId());
			confSpec.setConnectionId(this.getId());
			confSpec.setOperation(MsConfSpec.OP_CODE_UPDATE_CONF);
			confSpec.setMaxActiveSpeakers(spec.getMaxActiveSpeakers());
			confSpec.setNotifyActiveSpeaker(spec.isNotifyActiveSpeaker());
			confSpec.setNotificationInterval(spec.getNotificationInterval());
            confSpec.setActiveSpeakerThreashold(spec.getActiveSpeakerThreashold());
            confSpec.setAudiomixId(spec.getAudiomixId());
            confSpec.setAudiomixSampleRate(spec.getAudiomixSampleRate());
            
            confSpec.setConferenceType(spec.getConferenceType());
            confSpec.setMsVideoConferenceSpec(spec.getMsVideoConferenceSpec());
            
            confSpec.setMark(spec.getMark());
			ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
			this.addSBBOperation(handler);
			handler.start();
		}catch(ProcessMessageException e){
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	public String getConferenceId() {
		return conferenceId;
	}

	protected void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	void setActiveSpekers(Iterator iterator) {
		this.activeSpeakers.clear();
		for(;iterator.hasNext();){
			this.activeSpeakers.add(iterator.next());
		}
	}
	
	void setId(){
		try{
			id = msAdaptor.getConnectionId(MsAdaptor.CONNECTION_TYPE_CONFERENCE, this.conferenceId);
			if(logger.isDebugEnabled())
				logger.debug("<SBB> setting conference id as "+id);
		}catch(MediaServerException e){
			getServletContext().log(e.getMessage(), e);	
		}
	}
	
	public ConferenceInfo getConferenceInfo(){
		return this.confInfo;
	}
	
	public void setConferenceInfo(ConferenceInfoImpl info){
		this.confInfo = info;
		if(info != null){
			this.registerConference();
		}else{
			this.unregisterConference();
		}
	}
	
	protected void registerConference(){

		if(logger.isDebugEnabled()){
			logger.debug("Register Conference Called on Conference Controller" + this.conferenceId);
		}
		
		ServletContext ctxt = this.getServletContext();
		ConferenceRegistry confMgr = (ConferenceRegistry)
				ctxt.getAttribute(ConferenceRegistry.class.getName());
		
		if(logger.isDebugEnabled()){
			logger.debug("Registering the controller with the Conf Mgr :" + this.conferenceId);
		}
		confMgr.registerConference(this.confInfo);
	}

	protected void unregisterConference(){
		if(logger.isDebugEnabled()){
			logger.debug("UnRegister Conference Called on Conference Controller" + this.conferenceId);
		}
		
		ServletContext ctxt = this.getServletContext();
		ConferenceRegistry confMgr = (ConferenceRegistry)
				ctxt.getAttribute(ConferenceRegistry.class.getName());
		
		if(confMgr.findByConferenceID(this.conferenceId) != null){
			confMgr.unregisterConference(this.conferenceId);
		}
	}
	
	protected void checkMediaServer(MediaServer ms){
		if(!(ms.getHost().equals(super.mediaServer.getHost())) || ms.getPort()!=super.mediaServer.getPort()||!(ms.getAdaptorClassName().equals(super.mediaServer.getAdaptorClassName()))){
			throw new IllegalArgumentException("Media Server cannot be different.");
		}
	}
	
	protected void addParticipant(ConferenceParticipant participant){
		String id = participant.getId();
		if(this.participantIds.indexOf(id) == -1){
			this.participants.add(participant);
			this.participantIds.add(id);
		}
	}

	protected void removeParticipant(ConferenceParticipant participant){
		String id = participant.getId();
		if(this.participantIds.indexOf(id) != -1){
			this.participants.remove(participant);
			this.participantIds.remove(id);
		}
	}
	
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		boolean isconfIdValid=in.readBoolean();
		if(isconfIdValid)
			this.conferenceId = in.readUTF();
		this.participantIds = (ArrayList)in.readObject();
		this.activeSpeakers = (ArrayList)in.readObject();
		this.confInfo = (ConferenceInfoImpl)in.readObject();
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		boolean isconfIdValid=this.conferenceId!=null;
		out.writeBoolean(isconfIdValid);
		if(isconfIdValid)
			out.writeUTF(this.conferenceId);;
		out.writeObject(this.participantIds);
		out.writeObject(this.activeSpeakers);
		out.writeObject(this.confInfo);
	}
	
	public void activate(SipSession session) {
	
		if(logger.isDebugEnabled()){
			logger.debug("activate() IN");
		}
		super.activate(session);
		this.registerConference();
		
		try{
			//Also create teh Active speaker Notification handler.
			if(logger.isDebugEnabled()){
				logger.debug("Registering and starting the ASN Handler.");
			}
			ConferenceASNHandler asnHandler = new ConferenceASNHandler();
			this.addSBBOperation(asnHandler);
			asnHandler.start();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}

	//9411 bug
	public void modifyStream(MsConferenceStream[] streams)
			throws MediaServerException {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside modifyStream()..............");
		}
		if (streams == null){
			logger.debug("MsConferenceStream ---> streams is null so returning....");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Validating the inputs for ModifyStream");
		}
		for (int i = 0; i < streams.length; i++) {
			if (streams[i].getMedia() != MsConferenceStream.AUDIO_MEDIA
					&& streams[i].getMedia() != MsConferenceStream.VIDEO_MEDIA) {
				throw new IllegalArgumentException(
						"Incorrect value for media attribute of modify stream");
			}
			if (streams[i].getId1() == null) {
				throw new IllegalArgumentException("ID1 cannot be NULL");
			}
			if (streams[i].getId1() == null) {
				throw new IllegalArgumentException("ID2 cannot be NULL");
			}
			if (streams[i].getDirection() != MsConferenceStream.TO_ID1
					&& streams[i].getDirection() != MsConferenceStream.FROM_ID1) {
				throw new IllegalArgumentException(
						"Incorrect value for direction attribute of modify stream");
			}
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating the Media Server OP Sepc");
			}
			MsConfSpec confSpec = new MsConfSpec();
			confSpec.setId(this.getConferenceId());
			confSpec.setConnectionId(this.getId());
			confSpec.setOperation(MsConfSpec.OP_CODE_MODIFY_STREAM);
			for (int i = 0; i < streams.length; i++) {
				confSpec.addModifyStream(streams[i]);
			}
			if (logger.isDebugEnabled()) {
				logger
						.debug("Registering and joining the command handler for ModifyStream");
			}
			ConferenceCommandHandler handler = new ConferenceCommandHandler(
					confSpec);
			this.addSBBOperation(handler);
			handler.start();
		} catch (ProcessMessageException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
	}

	
	@Override
	public void muteStream(String[] participantIds, String direction)
	throws IllegalArgumentException,MediaServerException {
		if(logger.isDebugEnabled())
			logger.debug("Inside muteStream()..............");
		if (participantIds != null && participantIds.length!=0 ) {
			String id1=this.getId();
			if (!(direction.equals(FROM_ID1) || direction.equals(TO_ID1))) {
				if(logger.isDebugEnabled())
					logger.debug("Invalid value for direction");
				throw new IllegalArgumentException("Invalid value for direction");
			}
			ArrayList <MsConferenceStream> streamList=new ArrayList<MsConferenceStream>();
			for(String id2:participantIds){
				if (id2 != null) {
					MsConferenceStream stream = new MsConferenceStream();
					stream.setId1(id1);
					stream.setId2(id2);					
					stream.setDirection(direction);
					stream.setMedia(MsConferenceStream.AUDIO_MEDIA);
					stream.setGain_amt(MsConferenceStream.MUTE);
					streamList.add(stream);
				}
			}
				try {
					if(logger.isDebugEnabled())
						logger.debug("Creating the Media Server OP Sepc for mute stream");
					MsConfSpec confSpec = new MsConfSpec();
					confSpec.setId(this.getConferenceId());
					confSpec.setConnectionId(this.getId());
					confSpec.setOperation(MsConfSpec.OP_CODE_MODIFY_STREAM);
					confSpec.setModifyStreamList(streamList);								
					ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
					this.addSBBOperation(handler);
					handler.start();
				} catch (ProcessMessageException e) {
					throw new MediaServerException(e.getMessage(), e);
				}
			
			}else {
				if(logger.isDebugEnabled())
					logger.debug("Invalid value for participantIds");
				throw new IllegalArgumentException("participantIds cannot be NULL or Empty");
			}
		logger.debug("Exitting muteStream()..............");
	}

	@Override
	public void unmuteStream(String[] participantIds, String direction)
	throws IllegalArgumentException,MediaServerException {
		if(logger.isDebugEnabled())
			logger.debug("Inside unmuteStream()..............");
		if (participantIds != null && participantIds.length!=0 ) {
			String id1=this.getId();
			if (!(direction.equals(FROM_ID1) || direction.equals(TO_ID1))) {
				if(logger.isDebugEnabled())
					logger.debug("Invalid value for direction");
				throw new IllegalArgumentException("Invalid value for direction");
			}
			ArrayList <MsConferenceStream> streamList=new ArrayList<MsConferenceStream>();
			for(String id2:participantIds){
				if (id2 != null) {
					MsConferenceStream stream = new MsConferenceStream();
					stream.setId1(id1);
					stream.setId2(id2);					
					stream.setDirection(direction);
					stream.setMedia(MsConferenceStream.AUDIO_MEDIA);
					stream.setGain_amt(MsConferenceStream.UNMUTE);
					streamList.add(stream);
				}
			}
				try {
					if(logger.isDebugEnabled())
						logger.debug("Creating the Media Server OP Sepc for unmute stream");
					MsConfSpec confSpec = new MsConfSpec();
					confSpec.setId(this.getConferenceId());
					confSpec.setConnectionId(this.getId());
					confSpec.setOperation(MsConfSpec.OP_CODE_MODIFY_STREAM);
					confSpec.setModifyStreamList(streamList);								
					ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
					this.addSBBOperation(handler);
					handler.start();
				} catch (ProcessMessageException e) {
					throw new MediaServerException(e.getMessage(), e);
				}
			
			}else {
				if(logger.isDebugEnabled())
					logger.debug("Invalid value for participantIds");
				throw new IllegalArgumentException("participantIds cannot be NULL or Empty");
			}
		if(logger.isDebugEnabled())
			logger.debug("Exitting unmuteStream()..............");
	}
	
	public void destroyConference()throws MediaServerException{
		if(this.getMsAdaptor() instanceof com.baypackets.ase.msadaptor.msml.MsmlAdaptor)
		{
			if (logger.isDebugEnabled()) {
				logger.debug("Sending destroy conference for MsmlAdaptor");
			}	
			// Unregister conference from conference registry
			this.unregisterConference();
			try {
				MsConfSpec confSpec = new MsConfSpec();
				if(logger.isDebugEnabled())
					logger.debug("<SBB> setting connection is in MsConfSpec "+ this.getId());
				confSpec.setId(this.getConferenceId());
				confSpec.setConnectionId(this.getId());
				confSpec.setOperation(MsConfSpec.OP_CODE_DESTROY_CONF);
				ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
				this.addSBBOperation(handler);
				handler.start();
			} catch (ProcessMessageException e) {
				logger.error("Exception while sending destroy conference msml");
				throw new MediaServerException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void monitor(MsMonitorSpec[] specs) throws MediaServerException {

		if (logger.isDebugEnabled()) {
			logger.debug("Inside monitor()..............");
		}
		if (specs == null){
			if(logger.isDebugEnabled())
				logger.debug("MsMonitorSpec ---> specs is null so returning....");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Validating the inputs for Monitor");
		}
		for (int i = 0; i < specs.length; i++) {
			if (specs[i].getMonitorId()== null) {
				throw new IllegalArgumentException("ID1 cannot be NULL");
			}
			if (specs[i].getMonitoringId() == null) {
				throw new IllegalArgumentException("ID2 cannot be NULL");
			}
			
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating the Media Server OP Sepc");
			}
			MsConfSpec confSpec = new MsConfSpec();
			confSpec.setId(this.getConferenceId());
			confSpec.setConnectionId(this.getId());
			confSpec.setOperation(MsConfSpec.OP_CODE_JOIN_MONITOR_STREAM);
			for (int i = 0; i < specs.length; i++) {
				confSpec.addMonitorStream(specs[i]);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Registering and joining the command handler for Monitor");
			}
			ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
			this.addSBBOperation(handler);
			handler.start();
		} catch (ProcessMessageException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
		
	}

	@Override
	public void monitorUnjoin(MsMonitorSpec[] specs)
			throws MediaServerException {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside monitorUnjoin()..............");
		}
		if (specs == null){
			logger.debug("MsMonitorSpec ---> specs is null so returning....");
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Validating the inputs for Monitor");
		}
		for (int i = 0; i < specs.length; i++) {
			if (specs[i].getMonitorId()== null) {
				throw new IllegalArgumentException("ID1 cannot be NULL");
			}
			if (specs[i].getMonitoringId() == null) {
				throw new IllegalArgumentException("ID2 cannot be NULL");
			}
			
		}
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Creating the Media Server OP Sepc");
			}
			MsConfSpec confSpec = new MsConfSpec();
			confSpec.setId(this.getConferenceId());
			confSpec.setConnectionId(this.getId());
			confSpec.setOperation(MsConfSpec.OP_CODE_UNJOIN_MONITOR_STREAM);
			for (int i = 0; i < specs.length; i++) {
				confSpec.addMonitorStream(specs[i]);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Registering and joining the command handler for Unjoin Monitor");
			}
			ConferenceCommandHandler handler = new ConferenceCommandHandler(confSpec);
			this.addSBBOperation(handler);
			handler.start();
		} catch (ProcessMessageException e) {
			throw new MediaServerException(e.getMessage(), e);
		}
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Iterator<ConferenceParticipant> getJoinedParticipants(SBBEvent event) {
		Iterator<ConferenceParticipant> iterator=null;
		if(event!=null && event.getMessage()!=null){
			iterator=(Iterator)(event.getMessage().getAttribute(Constants.ATTRIBUTE_JOINED_PARTICIPANTS));
		}
		return iterator;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Iterator<ConferenceParticipant> getUnjoinedParticipants(SBBEvent event) {
		Iterator<ConferenceParticipant> iterator=null;
		if(event!=null && event.getMessage()!=null){
			iterator=(Iterator)(event.getMessage().getAttribute(Constants.ATTRIBUTE_UNJOINED_PARTICIPANTS));
		}
		return iterator;
	}
	@Override
	@SuppressWarnings("unchecked")
	public Iterator<MsConferenceStream> getModifiedStreams(SBBEvent event) {
		Iterator<MsConferenceStream> iterator=null;
		if(event!=null && event.getMessage()!=null){
			iterator=(Iterator)(event.getMessage().getAttribute(Constants.ATTRIBUTE_MODIFIED_STREAMS));
		}
		return iterator;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Iterator<MsConferenceStream> getUnjoinedStreams(SBBEvent event) {
		Iterator<MsConferenceStream> iterator=null;
		if(event!=null && event.getMessage()!=null){
			iterator=(Iterator)(event.getMessage().getAttribute(Constants.ATTRIBUTE_UNJOINED_STREAMS));
		}
		return iterator;
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
			if (logger.isDebugEnabled()) {
				logger.debug("GroupedMSEventListener : handleEvent() enter");
			}
			
			GroupedMsEvent groupedMsEvent = new GroupedMsEvent();
			groupedMsEvent.setEventId(event.getEventId());
			groupedMsEvent.setReasonCode(event.getReasonCode());
			groupedMsEvent.setMessage(event.getMessage());
			
			if(event.getMessage() instanceof SipServletRequest) 
			{
				if(event.getEventId().equals(SBBEvent.EVENT_CONNECTED)) {	//connected to MS
					groupedMsEvent.setConnectedMediaServer(connectedMS);
					if (logger.isDebugEnabled()) {
						logger.debug("GroupedMSEventListener : MS connected: " + connectedMS);
					}								
				}
				return sbbEventListener.handleEvent(sbb, groupedMsEvent);
			}
			else
			{
				SipServletResponse response = (SipServletResponse)event.getMessage();
				
				if(event.getEventId().equals(SBBEvent.EVENT_CONNECT_FAILED) && response != null && response.getRequest() != null 
						&& response.getRequest().getMethod().equalsIgnoreCase("INVITE") && (response.getStatus() == 486 || response.getStatus() == 408)) {	//MS busy or Timeout
					
					if (logger.isDebugEnabled()) {
						logger.debug("GroupedMSEventListener.handleEvent(): Media server busy / timeout ... retrying : Cause - "+response.getStatus());
					}
									
					if(mediaServerManager == null)
						mediaServerManager = (MediaServerManager) response.getSession().getServletContext().getAttribute(MediaServerSelector.class.getName());		
					
					int localMSCount = mediaServerManager.getActiveMSCount(capabilities, LOCAL_MS);
					
					try{
						//retry (SR) logic
						if(!isSelectRemoteMS && retryCounter < localMSCount) {
							removeA();
							removeB();
								connect();						
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
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						return sbbEventListener.handleEvent(sbb, groupedMsEvent);
					} catch (ConnectException e) {
						// TODO Auto-generated catch block
						return sbbEventListener.handleEvent(sbb, groupedMsEvent);
					}
				}
				else {
					 return sbbEventListener.handleEvent(sbb, groupedMsEvent);						
				}
			}
		}
		
	}
	
	/**
     * Associates the event listener with this SBB.
     * @param listener - Listener to be associated.
     * @throws IllegalStateException if this object is already invalidated.
     */
	public void setEventListener(SBBEventListener listener) throws IllegalStateException {
		this.sbbEventListener = listener;
		super.setEventListener(this.groupedMSEventListener);// set Event Listener for SBBImpl because application can not set it using this.setEventListener()
		if (this.sbbEventListener != null) {
			// Store the name of the listener class in the app session so that it
			// may be reconstructed during session activation on the cluster peer.
			this.appSession.setAttribute(this.getName() + com.baypackets.ase.sbb.util.Constants.SBB_LISTENER_CLASS, 
						this.sbbEventListener.getClass().getName());
		}
	}
}
