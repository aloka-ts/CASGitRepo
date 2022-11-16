package com.baypackets.ase.ra.diameter.sh.stackif;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.baypackets.ase.ra.diameter.sh.ShMessage;
import com.baypackets.ase.ra.diameter.sh.ShRequest;
import com.baypackets.ase.ra.diameter.sh.ShResourceException;
import com.baypackets.ase.ra.diameter.sh.ShResponse;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.base.avp.AvpDiameter;
import com.baypackets.ase.ra.diameter.base.avp.BaseAvp;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterMessage;

public abstract class ShAbstractRequest extends ShMessage implements ShRequest {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ShAbstractRequest.class);
	private static int count = 0;
	private int id;
	private boolean isReplicated = false ;
	// TODO need to add support for ShSession and StackSession.
	DiameterMessage stackObj;
	private ShSession m_session;

	/**
	 * In-coming or out-going timestamp
	 */
	private long timestamp = -1;

	public ShAbstractRequest() {
		super();
	}

	public ShAbstractRequest(int type) {
		super(type);
	}

	public ShAbstractRequest(ShSession session){
		super();
		logger.debug("Inside ShAbstractRequest(ShSession) constructor ");
		this.m_session=session;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		logger.debug("getTimestamp returning ." +timestamp);
		return this.timestamp;
	}

	public Response createResponse() throws ShResourceException {
		logger.debug("createResponse() is called.");
		try {
			if(ShResourceAdaptorImpl.getResourceContext() != null) {
				return (ShResponse)ShResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, this.getType());
			} else {
				logger.debug("Use default MessageFactory.");
				return (ShResponse) ShResourceAdaptorFactory.getMessageFactory().createResponse(this, this.getType());
			}
		} catch (Exception e) {
			throw new ShResourceException(e);
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
			logger.error("NULL stack object for ShRequest so using index:"+index);
		}
		return index;
	}

	public Response createResponse(int type) throws ResourceException {
		if(logger.isDebugEnabled())
			logger.debug("createResponse(int type) is called.");
		if(ShResourceAdaptorImpl.getResourceContext() != null) {
			return (Response)ShResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, type);
		} else {
			if(logger.isDebugEnabled())
				logger.debug("Use default MessageFactory.");
			return (Response)ShResourceAdaptorFactory.getMessageFactory().createResponse(this, type);
		}
	}


	///////////////////////////////////////////////////////////////////////////////////
	///////// com.baypackets.ase.ra.sh.diameterbase.message.BaseMessage API starts ////
	///////////////////////////////////////////////////////////////////////////////////	
	@Override
	public long getApplicationId(){
		return stackObj.getApplicationId();
	}


	@Override
	public int getCommandCode(){
		return (int) stackObj.getCommandCode();//.getCommandCode();//getCommandCode();
	}

	@Override
	public java.lang.String getDestinationHost(){

		return stackObj.getDestinationHostAVPValue();//.getDestinationHost();

	}

	@Override
	public long getHopIdentifier(){
		return stackObj.getHopbyHopId();
	}

	@Override
	public String getName(){
		return stackObj.getCommandName();
	}

	@Override
	public String getSessionId(){
		String sessionId=stackObj.getSessionIdAVPValue();
		return sessionId;
	}
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
	 * @throws ShResourceException
	//	 */


	/**
	 * Adding a vendor specific avp to the message using the DiameterVendorSpecificAvpSetNE object.
	 * @return
	 */

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


}

