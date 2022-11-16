package com.baypackets.ase.ra.diameter.sh.stackif;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameter;
import com.baypackets.ase.ra.diameter.sh.ShMessage;
import com.baypackets.ase.ra.diameter.sh.ShResponse;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.spi.container.SasMessageContext;
import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ShAbstractResponse extends ShMessage implements ShResponse {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ShAbstractResponse.class);
	private static int count = 0;
	private int id;
	private boolean isReplicated = false ;
	// TODO need to add support for ShSession and StackSession.
	DiameterMessage stackObj;
	private ShSession m_session;

	public ShAbstractResponse(int type) {
		super(type);
	}

	public ShAbstractResponse(ShSession session){
		super();
		this.m_session=session;
	}

	public ShAbstractResponse(DiameterMessage stkObj){
		super();
		this.stackObj=stkObj;
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
			String sessionId=stackObj.getSessionIdAVPValue();
			if(sessionId!=null)
				index=sessionId.hashCode();
			if(logger.isDebugEnabled())
				logger.debug("getWorkQueue : hashcode of sessionId:" + index);
		}else{
			logger.error("NULL stack object for ShResponse so using index:"+index);
		}
		return index;
	}

	public void send() throws IOException {
		logger.debug("ShAbstractResponse send() called.");
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			logger.info("Send to SH resource adaptor directly.");
			try {
				ShResourceAdaptorFactory.getResourceAdaptor().sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " ,e);
				throw new IOException(e.getMessage());
			}
		}
	}
	//	//
	//	//	public void setSubscriptionType(int subscriptionType)
	//	//			throws RoResourceException {
	//	//		throw new RoResourceException("Operation is not allowed");
	//	//	}
	//


	///////////////////////////////////////////////////////////////////////////////////
	///////// com.baypackets.ase.ra.sh.diameterbase.message.BaseMessage API starts ////
	///////////////////////////////////////////////////////////////////////////////////	

	//	public boolean equals(java.lang.Object obj){
	//		return false;
	//	}

	public long getApplicationId(){
		return stackObj.getApplicationId();
	}

//	public byte[] getByteArray(){
//		return stackObj.getByteArray();
//	}

	public int getCommandCode(){
		return (int) stackObj.getCommandCode();
	}

	public java.lang.String getDestinationHost(){
		String destHost=null;
		destHost=stackObj.getDestinationHostAVPValue();
		return destHost;
	}

	//	public Peer getDestinationPeer();

	/// TODO unable to resolve
	//	public int getHeaderLength(){
	//		
	//	}

	public long getHopIdentifier(){
		return stackObj.getHopbyHopId();
	}

//	public int getMessageLength(){
//		return stackObj.getMessageLength();
//	}

	public String getName(){
		return stackObj.getCommandName();//getName();
	}

	/// TODO unable to resolve
	//	public int getOffset(){
	//		return stackObj.ge
	//	}

	//public Peer getOriginPeer();

	public String getSessionId(){
		String sessionId=null;
		sessionId=stackObj.getSessionIdAVPValue();
		return sessionId;
	}
//
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


	/**
	 * The End-to-End Identifier is an unsigned 32-bit integer field (in network byte order); 
	 * and is used to detect duplicate messages.
	 * @return
	 */
	public long getEndToEndIdentifier(){
		return stackObj.getEndtoEndId();
	}

	/**
	 * The Hop-by-Hop Identifier is an unsigned 32-bit integer field (in network byte order);
	 * and aids in matching requests and replies.
	 * @return
	 */
	public long getHopByHopIdentifier(){
		return stackObj.getHopbyHopId();
	}

	//public java.util.Set<InbandSecurityId> getInbandSecurityIdSet();

	public java.lang.String getOriginHost() {
		return stackObj.getOriginHostAVPValue();
	}

	public java.lang.String getOriginRealm() {
		return stackObj.getOriginRealmAVPValue();
	}

	public java.util.List<AvpDiameter> getVendorIdAvps(long vendorId){
		// TODO need to write instanceof check for all the AVP classes.
		return null;
	}


	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSet 
	 * object.
	 * @return
	 * @throws RoResourceException
	 */
//	public AvpDiameterVendorSpecificSet getVendorSpecificAvpSet(){
//		if(logger.isDebugEnabled()){
//			logger.debug("Inside getVendorSpecificAvpSet()");
//		}
//		return new AvpDiameterVendorSpecificSet(stackObj.getVendorSpecificAvpSet());
//	}

	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSetNE object.
	 * @return
	 */
	//public DiameterVendorSpecificAvpSetNE getVendorSpecificAvpSetNe();

//	public byte getVersion(){
//		return stackObj.getVersion();
//	}

	public boolean isError(){
		return stackObj.getErrorBit() ;//isError();
	}

	public boolean isProxiable(){
		return stackObj.getProxiableBit() ;//isProxiable();
	}

	public boolean isRequest(){
		return stackObj.getRequestBit() ;//isRequest();
	}

	public boolean isReTransmitted(){
		return stackObj.isTransmitBitSet();//isReTransmitted();
	}

	public void setReTransmitted(boolean value){
		stackObj.setTransmitBit(value);
	}


	public java.lang.String toString(){
		// TODO
		return stackObj.toString();
	}



	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterMessage API ends /////
	///////////////////////////////////////////////////////////////////////////////////	


	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterResponse API starts //
	///////////////////////////////////////////////////////////////////////////////////	

	//	public BaseDiameterRequest getRequest(){
	//		// TODO 
	//		return null;
	//	}

//	public boolean isPerformFailover(){
//		return stackObj..isPerformFailover();
//	}
//
//	public void setPerformFailover(boolean performFailover){
//		stackObj.setPerformFailover(performFailover);
//	}

	///////////////////////////////////////////////////////////////////////////////////
	// com.baypackets.ase.ra.sh.diameterbase.message.BaseDiameterResponse API ends  ////
	///////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns set of Application-Ids taken from the message avps
	 * @return
	 */
	//public java.util.Set<ApplicationId> getApplicationIdSet();
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
			logger.debug("getAvp(long avpCode) " + avpCode);
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
	@Override
	public java.lang.String getDestinationRealm() {
		return stackObj.getDestinationRealmAVPValue();//.getDestinationRealm();
	}

	@Override
	public void setProxiable(boolean value) {
		stackObj.setProxiableBit(value);
	}


}

