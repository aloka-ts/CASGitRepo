package com.baypackets.ase.ra.diameter.sh.impl;

import com.baypackets.ase.ra.diameter.sh.ShMessage;
import com.baypackets.ase.ra.diameter.sh.ShProfileUpdateRequest;
import com.baypackets.ase.ra.diameter.sh.ShSubscribeNotificationRequest;
import com.baypackets.ase.ra.diameter.sh.ShUserDataRequest;
import com.baypackets.ase.ra.diameter.sh.stackif.*;
import com.baypackets.ase.ra.diameter.sh.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

import fr.marben.diameter.*;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;

import org.apache.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ShMessageFactoryImpl implements ShMessageFactory, Constants {

	private static Logger logger = Logger.getLogger(ShMessageFactoryImpl.class);

	private ResourceContext context;
	private MessageFactory msgFactory;
	private static ShMessageFactoryImpl shMessageFactory;
	private static DiameterShMessageFactory diameterShMsgFactory=null;

	private static DiameterStack stack=null;
	
	static List<String> destinationRelam=null;

	private static DiameterMessageFactory diameterMsgFactory;


	/**
	 *	Default constructor for creating SmppMessageFactory object.
	 *
	 */
	public ShMessageFactoryImpl(){
		logger.debug("creating ShMessageFactory object");
		shMessageFactory=this;
	}

	/**
	 *	This  method returns the instance of SmppMessageFactory.
	 *
	 *	@return SmppMessageFactory object.
	 */
	public static ShMessageFactory getInstance(){
		if(shMessageFactory==null){
			shMessageFactory = new ShMessageFactoryImpl();
		}
		return shMessageFactory;
	}

	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside init(ResourceContext)");
		}
		this.context = context;
		this.msgFactory=context.getMessageFactory();
		if (this.msgFactory == null) {
			logger.error("init(): null message factory.");
		}

		logger.debug("Setting ShMessageFactory in  ShResourceAdaptorFactory");
		ShResourceAdaptorFactory.setMessageFactory(this);
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type) throws ResourceException {
		return null;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,String remoteRealm) throws ResourceException {
		logger.debug("Inside createRequest(session,type remoteRealm,msisdn)" +remoteRealm+" msisdn does nothing");
		return null;
	}

	@Override
	public SasMessage createRequest(SasProtocolSession session, int type,String remoteRealm,String msisdn)
	throws ResourceException {
		logger.debug("Inside createRequest(session,type remoteRealm,msisdn)" +remoteRealm+" msisdn "+msisdn);
		
		if (remoteRealm == null || remoteRealm.isEmpty()) {
			
			logger.debug("check for destination realm defined in diameter_sh config");
			
			if (destinationRelam != null && !destinationRelam.isEmpty()) {

				remoteRealm = destinationRelam.get(0);
				logger.debug("Inside createRequest(session,type) with default realm "
						+ remoteRealm);
			}
		}
		ShAbstractRequest message = null;
		switch (type) {
		case UDR:
			logger.debug("Creating UDR");
			message = new ShUserDataRequestImpl((ShSession)session,type,remoteRealm,msisdn);
			break;
		case PUR:
			logger.debug("Creating PUR request");
			message = new ShProfileUpdateRequestImpl((ShSession)session,type,remoteRealm);
			break;
		case SNR:
			logger.debug("Creating SNR");
			message = new ShSubscribeNotificationRequestImpl((ShSession)session,type,remoteRealm,msisdn);
			break;
		case PNR:
			logger.debug("Creating PNR");
			message = new ShPushNotificationRequestImpl((ShSession)session,type,remoteRealm);
			break;
		default:
			logger.error("Wrong/Unkown request type.");
			throw new ResourceException("Wrong/Unkown request type.");
		}
		message.setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return message;
	}

	public SasMessage createResponse(SasMessage request, int type)
	throws ResourceException {
		return null;
	}

	@Override
	public SasMessage createResponse(SasMessage request) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
	public ShProfileUpdateRequest createProfileUpdateRequest(SasProtocolSession session) throws ResourceException {
		logger.debug("Inside createProfileUpdateRequest():");
		ShProfileUpdateRequest request = (ShProfileUpdateRequest)this.msgFactory.createRequest(session,PUR);
		((ShMessage) request).setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return request;
	}


	@Override
	public ShSubscribeNotificationRequest createSubscribeNotificationsRequest(SasProtocolSession session) throws ResourceException {
		logger.debug("Inside createSubscribeNotificationsRequest():");
		ShSubscribeNotificationRequest request = (ShSubscribeNotificationRequest)this.msgFactory.createRequest(session,SNR);
		((ShMessage) request).setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return request;
	}


	@Override
	public ShUserDataRequest createUserDataRequest(SasProtocolSession session,String realm,String msisdn) throws ResourceException {
		logger.debug("Inside createUserDataRequest(): with relam"+realm +" msisdn " +msisdn);
		ShUserDataRequest request = (ShUserDataRequest) createRequest(session,UDR,realm,msisdn);//
		//(ShUserDataRequest)this.msgFactory.createRequest(session,UDR,realm,msisdn);
		((ShMessage) request).setProtocolSession(session);
		//message.setResourceContext(this.context);
		logger.debug("leaving createRequest():");
		return request;
	}

	@Override
	public DiameterShMessageFactory getDiameterShMessageFactory() {
		return diameterShMsgFactory;
	}


	public static void setDiameterShMsgFactory(DiameterShMessageFactory shMsgFactory) {
		if(logger.isDebugEnabled()){
			logger.debug("setDiameterShMsgFactory as : "+shMsgFactory);
		}
		diameterShMsgFactory = shMsgFactory;
		diameterMsgFactory= ((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.stackInterface).getDiameterStack().getDiameterMessageFactory();

	}


	///////////////////////////////////////////////////////////////////////////////
	/////////////// stack object request creation methods /////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	/**
	 * This method creates a User-Identity grouped AVP with default values.
	 * @return List&lt;DiameterAVP&gt; - List of DiameterAVP containing
	 *         User-Identity grouped AVP.
	 */
	public static List<DiameterAVP> createUserIdentityAVPGroupedValue(String msisdn,String publicIdentity)
			throws DiameterException {
		
//		String lMSISDN = new String(msisdn.getBytes(), StandardCharsets.UTF_8);
//		return diameterShMsgFactory.createUserIdentityAVP(publicIdentity, lMSISDN);
		if(logger.isDebugEnabled()){
			logger.debug("Entering createUserIdentityAVPGroupedValue for MSISDN "+msisdn);
		}
		DiameterGroupedAVP avpIn=diameterMsgFactory.createGroupedAVP("User-Identity", "3GPP");
		
		 DiameterOctetStringAVP msisdnOct=diameterMsgFactory.createOctetStringAVP("MSISDN","3GPP",parseTBCD(msisdn));
		 
		 avpIn.add(msisdnOct);
		return avpIn.getValue();
		
	} // end of createUserIdentityAVPGroupedValue method


	public static DiameterMessage createUDR( String remoteRealm,String msisdn) throws DiameterException,DiameterInvalidArgumentException{
		//Creating User-Data-Request within the current session

		if(logger.isDebugEnabled()){
			logger.debug("Entering createUDR(): with  Remote Realm "+remoteRealm);
		}
		List<DiameterAVP> lUserIdentity = createUserIdentityAVPGroupedValue(msisdn,null);
		String[] lDataReference = new String[]{"LocationInformation"};

		DiameterMessage udr = diameterShMsgFactory.createUserDataRequest( remoteRealm,lUserIdentity,lDataReference);
		// Set End-To-End Id in the request
		udr.setEndtoEndId(stack.getNextEndToEndId());
		udr.setProxiableBit(true);
		return udr;
	}

	public static DiameterMessage createPUR(String remoteRealm) throws DiameterException,DiameterInvalidArgumentException{
		//Creating Profile-Data-Request within the current session

		if(logger.isDebugEnabled()){
			logger.debug("Entering createPNR() : with   Remote Realm "+remoteRealm);
		}
		List<DiameterAVP> lUserIdentity = createUserIdentityAVPGroupedValue(null,null);
		String[] lDataReference = new String[]{"RepositoryData"};
		String userData="";
		DiameterMessage pdr = diameterShMsgFactory.createProfileUpdateRequest( remoteRealm,lUserIdentity,lDataReference,userData);
		// Set End-To-End Id in the request
		pdr.setEndtoEndId(stack.getNextEndToEndId());
		return pdr;
	}
	public static DiameterMessage createSNR( String remoteRealm) throws DiameterException,DiameterInvalidArgumentException{
		//Creating Profile-Data-Request within the current session

		if(logger.isDebugEnabled()){
			logger.debug("Entering createSNR(): with   Remote Realm "+remoteRealm);
		}
		List<DiameterAVP> lUserIdentity = createUserIdentityAVPGroupedValue(null,null);
		String[] lDataReference = new String[]{"RepositoryData"};

		DiameterMessage snr = diameterShMsgFactory.createSubscribeNotificationRequest(remoteRealm,"UTF8StringPublic-Identity","RepositoryData"," SUBSCRIBE",null,null);
		// Set End-To-End Id in the request
		snr.setEndtoEndId(stack.getNextEndToEndId());
		return snr;
	}

	public static void setDiameterShClientStack(DiameterStack clientStack) {

		if(logger.isDebugEnabled()){
			logger.debug("setDiameterShClientStack as : "+clientStack);
		}
		stack=clientStack;
	}

	public static void setDestinationRealm(List<String> list) {
		destinationRelam=list;
		
	}
	
	
	 /*
		 * This method converts a character string to a TBCD string.
		 */
		private static byte[] parseTBCD (java.lang.String tbcd) {
	        int length = (tbcd == null ? 0:tbcd.length());
	        int size = (length + 1)/2;
	        byte[] buffer = new byte[size];

	        for (int i=0, i1=0, i2=1; i<size; ++i, i1+=2, i2+=2) {

	            char c = tbcd.charAt(i1);
	            int n2 = getTBCDNibble(c, i1);
	            int octet = 0;
	            int n1 = 15;
	            if (i2 < length) {
	                c = tbcd.charAt(i2);
	                n1 = getTBCDNibble(c, i2);
	            }
	            octet = (n1 << 4) + n2;
	            buffer[i] = (byte)(octet & 0xFF);
	        }

	        return buffer;
	    }

	    private static int getTBCDNibble(char c, int i1) {

	        int n = Character.digit(c, 10);

	        if (n < 0 || n > 9) {
	            switch (c) {
	                case '*':
	                    n = 10;
	                    break;
	                case '#':
	                    n = 11;
	                    break;
	                case 'a':
	                    n = 12;
	                    break;
	                case 'b':
	                    n = 13;
	                    break;
	                case 'c':
	                    n = 14;
						break;
	                default:
	                    throw new NumberFormatException("Bad character '" + c
	                            + "' at position " + i1);
	            }
	        }
	        return n;
	    }
	  /* Hex chars */
	  private static final byte[] HEX_CHAR = new byte[]
	      { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
}
