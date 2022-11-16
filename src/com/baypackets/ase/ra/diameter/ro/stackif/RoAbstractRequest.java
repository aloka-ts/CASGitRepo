package com.baypackets.ase.ra.diameter.ro.stackif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameter;
import com.baypackets.ase.ra.diameter.base.avp.BaseAvp;
import com.baypackets.ase.ra.diameter.ro.RoMessage;
import com.baypackets.ase.ra.diameter.ro.RoRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.ra.diameter.ro.RoResponse;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.ro.impl.RoSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterMessage;

public abstract class RoAbstractRequest extends RoMessage implements RoRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RoAbstractRequest.class);
	private static int count = 0;
	private int id;
	private boolean isReplicated = false ;
	// TODO need to add support for RoSession and StackSession.
	DiameterMessage stackObj;
	private RoSession m_session;
	
	/**
	 * In-coming or out-going timestamp
	 */
	private long timestamp = -1;
	
	public RoAbstractRequest() {
		super();
	}

	public RoAbstractRequest(int type) {
		super(type);
	}

	public RoAbstractRequest(RoSession session){
		super();
		logger.debug("Inside RoAbstractRequest(RoSession) constructor ");
		this.m_session=session;
	}

	//	public Response createResponse(int type) throws ResourceException {
	//		logger.debug("createResponse(int type) is called.");
	//		//if (this.getResourceContext() != null) {
	//		if(RoResourceAdaptorImpl.getResourceContext() != null) {		
	//			//return (Response)this.getResourceContext().getMessageFactory().createResponse(this, type);
	//			return (Response)RoResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, type);
	//		} else {
	//			logger.debug("Use default MessageFactory.");
	//			return (Response)RoResourceAdaptorFactory.getMessageFactory().createResponse(this, type);
	//		}
	//	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		logger.debug("getTimestamp returning ." +timestamp);
		return this.timestamp;
	}
	
	public Response createResponse() throws RoResourceException {
		logger.debug("createResponse() is called.");
		try {
			//if (this.getResourceContext() != null ) {
			if(RoResourceAdaptorImpl.getResourceContext() != null) {
				//return (RoResponse)this.getResourceContext().getMessageFactory().createResponse(this, this.getType());
				return (RoResponse)RoResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, this.getType());
			} else {
				logger.debug("Use default MessageFactory.");
				return (RoResponse)RoResourceAdaptorFactory.getMessageFactory().createResponse(this, this.getType());
			}
		} catch (Exception e) {
			throw new RoResourceException(e);
		}
	}


	public int getId() {
		if (this.id == -1) {
			this.id = generateId();
		}
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private static int generateId() {
		if (count >= Integer.MAX_VALUE) {
			count = 0;
		}
		return count++;

	}

	public void isAlreadyReplicated(boolean isReplicated) {
		this.isReplicated = isReplicated;
	}

	void setStackObject(DiameterMessage stkObj){
		this.stackObj=stkObj;
	}

	DiameterMessage getStackObject(){
		return this.stackObj;
	}
	

	/**
	 * Overridden from AbstractSasMessage to provide the index
	 * of the worker thread queue to enqueue this message in.  The
	 * value returned is a hash of the sessionId of stack object.
	 */
	public int getWorkQueue() {
		int index=-1;
		if(stackObj!=null){
			String sessionId=stackObj.getSessionIdAVPValue();//
			if(sessionId!=null)
				index=sessionId.hashCode();
			if(logger.isDebugEnabled())
				logger.debug("getWorkQueue : hashcode of sessionId:" + index);
		}else{
			logger.error("NULL stack object for RoRequest so using index:"+index);
		}
		return index;
	}

	//	//
	//	//	public void setSubscriptionType(int subscriptionType)
	//	//			throws RoResourceException {
	//	//		throw new RoResourceException("Operation is not allowed");
	//	//	}
	//
	//
		public Response createResponse(int type) throws ResourceException {
			if(logger.isDebugEnabled())
			logger.debug("createResponse(int type) is called.");
			//if (this.getResourceContext() != null) {
			if(RoResourceAdaptorImpl.getResourceContext() != null) {		
				//return (Response)this.getResourceContext().getMessageFactory().createResponse(this, type);
				return (Response)RoResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, type);
			} else {
				if(logger.isDebugEnabled())
				logger.debug("Use default MessageFactory.");
				return (Response)RoResourceAdaptorFactory.getMessageFactory().createResponse(this, type);
			}
		}


	///////////////////////////////////////////////////////////////////////////////////
	///////// com.baypackets.ase.ra.sh.diameterbase.message.BaseMessage API starts ////
	///////////////////////////////////////////////////////////////////////////////////	

	//	public boolean equals(java.lang.Object obj){
	//		return false;
	//	}

	@Override
	public long getApplicationId(){
		return stackObj.getApplicationId();
	}

//	public byte[] getByteArray(){
//		return stackObj..getByteArray();
//	}

	@Override
	public int getCommandCode(){
		return (int) stackObj.getCommandCode();//.getCommandCode();//getCommandCode();
	}

	@Override
	public java.lang.String getDestinationHost(){
		
		return stackObj.getDestinationHostAVPValue();//.getDestinationHost();
		
	}

	//	public Peer getDestinationPeer();

	/// TODO unable to resolve
	//	public int getHeaderLength(){
	//		
	//	}

	@Override
	public long getHopIdentifier(){
		return stackObj.getHopbyHopId();
	}

//	public int getMessageLength(){
//		return stackObj.getMessageLength();
//	}

	@Override
	public String getName(){
		return stackObj.getCommandName();//.getName();
	}

	/// TODO unable to resolve
	//	public int getOffset(){
	//		return stackObj.ge
	//	}

	//public Peer getOriginPeer();
	@Override
	public String getSessionId(){
		String sessionId=stackObj.getSessionIdAVPValue();//.getSessionId();
		return sessionId;
	}

//	public StandardEnum getStandard(){
//		return StandardEnum.getContainerObj(stackObj.getStandard());
//	}

	//Should be implemented by the last class in hierarchy
	//	public void readExternal(java.io.ObjectInput input){
	//		
	//	}

	// unable to resolve
	//	public void resetIdentifier(){
	//		return stackObj.res
	//	}

	// unable to resolve
	//	public void resetIdentifier(long identifier){
	//		
	//	}

	//	public void setDestinationPeer(Peer destinationPeer);

	//	public void setOriginPeer(Peer originPeer);

//	public void toXML(java.lang.StringBuilder builder){
//		stackObj.toXML(builder);
//	}
//
//	public ValidationRecord validate(){
//		return new ValidationRecordImpl(stackObj.validate());
//	}

	///unable to resolve
	//	public void write(java.nio.ByteBuffer otherBuffer){
	//		stackObj.w
	//	}

	// unable to resolve
	//	public void writeExternal(java.io.ObjectOutput output){
	//		
	//	}


	///////////////////////////////////////////////////////////////////////////////////
	///////// com.baypackets.ase.ra.sh.diameterbase.message.BaseMessage API ends //////
	///////////////////////////////////////////////////////////////////////////////////	


	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterMessage API starts ///
	///////////////////////////////////////////////////////////////////////////////////	

	//	public void addAvp(AvpDiameter avp){
	//		
	//	}

	@Override
	public DiameterAVP get(int index){
		if (logger.isDebugEnabled()) {
			logger.debug("get(int index) " +index);
		}
		DiameterAVP stackAvp = stackObj.get(index);
		
		if (logger.isDebugEnabled()) {
			logger.debug("get(int index) return" +stackAvp);
		}
		return stackAvp;
	}
	
	/**
	 * Returns set of Application-Ids taken from the message avps
	 * @return
	 */
	//public java.util.Set<ApplicationId> getApplicationIdSet();
	
	@Override
	public java.util.List<DiameterAVP> getAvp(long avpCode){
		
		if (logger.isDebugEnabled()) {
			logger.debug("getAvp(long avpCode)");
		}
		List containerList= new ArrayList<DiameterAVP>();
		List stackList=stackObj.getAVP(avpCode);
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAVP){
				containerList.add(obj);//createContainerAvp(
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getAvpList(int avpCode) returning "+containerList);
		}
		return containerList;
	}

	@Override
	public DiameterAVP getAvp(long avpCode, long vendorId){
		if (logger.isDebugEnabled()) {
			logger.debug("getAvp(long avpCode, long vendorId) called. Code " +avpCode +" VendorId "+vendorId);
		}
		DiameterAVP avp=null;
		List<DiameterAVP> stackList=stackObj.getAVP(avpCode);
		if (stackList != null) {
		Iterator<DiameterAVP> itr = stackList.iterator();
		while(itr.hasNext()){
			DiameterAVP obj = itr.next();
			if(obj.getVendorId()== vendorId)
				avp=obj;
			break;//createContainerAvp(
		}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("getAvp(long avpCode, long vendorId) avp not found");
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("getAvp(long avpCode, long vendorId) return avp "+avp);
		}
		return avp;
	}

//	@Override
//	public List<? extends BaseAvp> getAvpList(){
//		List containerList= new ArrayList<DiameterAVP>();
//		List<DiameterAVP> stackList=stackObj.getValue();
//		Iterator<DiameterAVP> itr = stackList.iterator();
//		while(itr.hasNext()){
//			DiameterAVP obj = itr.next();
//			
//				containerList.add(obj);//createContainerAvp(
//		}
//		return containerList;
//	}

	@Override
	public java.util.List<DiameterAVP> getAvpList(long avpCode, long vendorId){
		
		if (logger.isDebugEnabled()) {
			logger.debug("getAvpList(int avpCode, long vendorId). Code " +avpCode +" VendorId "+vendorId);
		}
		List containerList= new ArrayList<DiameterAVP>();
		
		List stackList=stackObj.getAVP(avpCode);
		if (stackList != null) {
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAVP && ((DiameterAVP)obj).getVendorId()==vendorId){
				containerList.add(obj);//createContainerAvp((DiameterAVP)obj));
			}
		}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("getAvpList(int avpCode, long vendorId) avp not found");
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("getAvpList(int avpCode, long vendorId). return list "+ containerList);
		}
		return containerList;
	}

	@Override
	public java.util.List<DiameterAVP> getAvpList(long vendorId){
		
		if (logger.isDebugEnabled()) {
			logger.debug(" getAvpList(long vendorId)  VendorId "+vendorId);
		}
		List containerList= new ArrayList<DiameterAVP>();
		List stackList=stackObj.getValue();//AVP(vendorId);
		if (stackList != null) {
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAVP && (((DiameterAVP)obj).getVendorId()==vendorId)){
				containerList.add(obj);//createContainerAvp((DiameterAVP)obj));
			}
		}} else {
			if (logger.isDebugEnabled()) {
				logger.debug("getAvpList(long vendorId)  VendorId avp not found");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(" getAvpList(long vendorId)  VendorId . return list "+ containerList);
		}
		return containerList;
	}

	@Override
	public java.util.List<DiameterAVP> getAvps(){
		
		if (logger.isDebugEnabled()) {
			logger.debug(" getAvps()");
		}
		List containerList= new ArrayList<DiameterAVP>();
		List stackList=stackObj.getValue();
		if (stackList != null) {
		Iterator<?> itr = stackList.iterator();
		while(itr.hasNext()){
			Object obj = itr.next();
			if(obj instanceof DiameterAVP){
				containerList.add(obj);//createContainerAvp((DiameterAVP)obj));
			}
		}} else {
			if (logger.isDebugEnabled()) {
				logger.debug("getAvps not found");
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(" getAvps()  return list "+ containerList);
		}
		return containerList;
	}

	@Override
	public java.util.List<DiameterAVP> getVendorSpecificAvps(){
		
		if (logger.isDebugEnabled()) {
			logger.debug("getVendorSpecificAvps ");
		}
		List containerList= new ArrayList<DiameterAVP>();
		List<DiameterAVP> stackList=stackObj.getValue();
		if (stackList != null) {
		Iterator<DiameterAVP> itr = stackList.iterator();
		while(itr.hasNext()){
			DiameterAVP obj = itr.next();
			if(obj.getVBit()==true ){
				containerList.add(obj);//createContainerAvp((DiameterAVP)obj));
			}
		}} else {
			if (logger.isDebugEnabled()) {
				logger.debug("getVendorSpecificAvps avp not found");
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("getVendorSpecificAvps returning  "+containerList);
		}
		return containerList;
	}
	//public AvpSet getAvpSet();

	/**
	 * The End-to-End Identifier is an unsigned 32-bit integer field (in network byte order); 
	 * and is used to detect duplicate messages.
	 * @return
	 */
	@Override
	public long getEndToEndIdentifier(){
		return stackObj.getEndtoEndId();//get.getEndToEndIdentifier();
	}

	/**
	 * The Hop-by-Hop Identifier is an unsigned 32-bit integer field (in network byte order);
	 * and aids in matching requests and replies.
	 * @return
	 */
	@Override
	public long getHopByHopIdentifier(){
		return stackObj.getHopbyHopId();
	}

	//public java.util.Set<InbandSecurityId> getInbandSecurityIdSet();

	@Override
	public java.lang.String getOriginHost() {
		return stackObj.getOriginHostAVPValue();//getOriginHost();
	}

	@Override
	public java.lang.String getOriginRealm() {
		return stackObj.getOriginRealmAVPValue();
	}

	@Override
	public java.util.List<AvpDiameter> getVendorIdAvps(long vendorId){
		// TODO need to write instanceof check for all the AVP classes.
		return null;
	}

	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSet 
	 * object.
	 * @return
	 * @throws RoResourceException 
//	 */
//	public AvpDiameterVendorSpecificSet getVendorSpecificAvpSet(){
//		if(logger.isDebugEnabled()){
//			logger.debug("Inside getVendorSpecificAvpSet()");
//		}
//		return new AvpDiameterVendorSpecificSet(stackObj.get.getVendorSpecificAvpSet());
//	}

	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSetNE object.
	 * @return
	 */
	//public DiameterVendorSpecificAvpSetNE getVendorSpecificAvpSetNe();

//	@Override
//	public byte getVersion(){
//		return stackObj.getVersion();
//	}

	@Override
	public boolean isError(){
		return stackObj.getErrorBit();//isError();
	}

	@Override
	public boolean isProxiable(){
		return stackObj.getProxiableBit();//.isProxiable();
	}

	@Override
	public boolean isRequest(){
		return stackObj.getRequestBit();
	}

	@Override
	public boolean isReTransmitted(){
		return stackObj.isTransmitBitSet();
	}

	@Override
	public void setReTransmitted(boolean value){
		stackObj.setTransmitBit(value);
	}

	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterMessage API ends /////
	///////////////////////////////////////////////////////////////////////////////////	


	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterRequest API starts ///
	///////////////////////////////////////////////////////////////////////////////////	

	@Override
	public java.lang.String getDestinationRealm() {
		return stackObj.getDestinationRealmAVPValue();//.getDestinationRealm();
	}

	@Override
	public void setProxiable(boolean value) {
		stackObj.setProxiableBit(value);
	}

	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterRequest API ends  ////
	///////////////////////////////////////////////////////////////////////////////////	

}

